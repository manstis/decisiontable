package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A generic cell that handles events and state maintenance. Click to edit,
 * escape to cancel, return or blur to commit. The cell's content, depending
 * upon "edit" or "non-edit" state, is rendered by Cell Renderers looked up from
 * a Cell Renderer Factory based upon the coordinate of the cell being rendered.
 * Whilst this appears daft (as GWT handles assigning Cells to Columns for
 * rendering of the column) when a Horizontal Decision table is implemented each
 * column represents a row and therefore will have the need to render each cell
 * in the column differently.
 */
public class PassthroughCell extends
	AbstractEditableCell<CellValue, PassthroughCell.ViewData> {

    /**
     * The state of the cell. Most cells don't require a state however when a
     * cell is being edited it's old value and Cell Renderer (for rendering the
     * "editor") are required.
     * 
     * @author manstis
     * 
     */
    static class ViewData {

	private boolean isEditing;
	private CellValue originalValue;
	private AbstractCellRenderer renderer;

	public ViewData(CellValue originalValue, AbstractCellRenderer renderer) {
	    this.originalValue = originalValue;
	    this.renderer = renderer;
	    this.isEditing = true;
	}

	public CellValue getOriginalValue() {
	    return originalValue;
	}

	public AbstractCellRenderer getRenderer() {
	    return this.renderer;
	}

	@Override
	public boolean equals(Object o) {
	    if (o == null) {
		return false;
	    }
	    ViewData vd = (ViewData) o;
	    return equalsOrBothNull(originalValue, vd.originalValue)
		    && equalsOrBothNull(renderer, vd.renderer)
		    && isEditing == vd.isEditing;
	}

	@Override
	public int hashCode() {
	    return originalValue.hashCode() * 29 + renderer.hashCode() * 29
		    + +Boolean.valueOf(isEditing).hashCode();
	}

	public boolean isEditing() {
	    return isEditing;
	}

	public void setEditing(boolean isEditing) {
	    this.isEditing = isEditing;
	}

	private boolean equalsOrBothNull(Object o1, Object o2) {
	    return (o1 == null) ? o2 == null : o1.equals(o2);
	}
    }

    protected SelectionManager manager;
    protected AbstractCellRendererFactory cellFactory;

    public PassthroughCell(SelectionManager manager, AbstractCellRendererFactory cellFactory) {
	super("click", "keyup", "keydown", "blur");
	if (manager == null) {
	    throw new IllegalArgumentException("manager == null");
	}
	if (cellFactory == null) {
	    throw new IllegalArgumentException("cellFactory == null");
	}
	this.cellFactory = cellFactory;
	this.manager = manager;
    }

    @Override
    public boolean isEditing(Element element, CellValue selectedCell, Object key) {
	Coordinate physicalCoordinate = selectedCell.getPhysicalCoordinate();
	key = physicalCoordinate;
	ViewData viewData = getViewData(key);
	return viewData == null ? false : viewData.isEditing();
    }

    /**
     * Handle the browser event. Note <code>Element</code> contains the physical
     * HTML element on which the event was triggered and <code>CellValue</code>
     * contains the logical object relating to the HTML coordinate. Therefore in
     * a table with merged cells the HTML coordinate may not be the same as the
     * physical coordinate required to retrieve the correct display value.
     * 
     * The physical coordinate is used as the key to saved state. This is
     * derieved from the <code>CellVale</code> and not the provided argument
     * which is set to be an object of the row type (i.e. List&lt;CellValue&gt;)
     * by GWT.
     * 
     * @param parent
     *            The HTML element containing the cell
     * @param selectedCell
     *            The value of the cell at the HTML coordinate
     * @param key
     *            Key of the view state data (GWT sets this to be an object of
     *            the row type)
     * @param valueUpdater
     *            Callback mechanism to communicate updates
     */
    @Override
    public void onBrowserEvent(Element parent, CellValue selectedCell,
	    Object key, NativeEvent event, ValueUpdater<CellValue> valueUpdater) {

	// Lookup physical cell
	Coordinate physicalCoordinate = selectedCell.getPhysicalCoordinate();
	key = physicalCoordinate;
	ViewData viewData = getViewData(key);

	if (viewData != null && viewData.isEditing()) {
	    // If editing the cell pass events to the Editor
	    editEvent(parent, selectedCell, key, event, valueUpdater);

	    // Otherwise check for events to initiate editing
	} else {
	    String type = event.getType();
	    int keyCode = event.getKeyCode();
	    boolean enterPressed = "keyup".equals(type)
		    && keyCode == KeyCodes.KEY_ENTER;
	    if ("click".equals(type) || enterPressed) {
		// Go into edit mode.
		if (viewData == null) {
		    CellValue physicalCell = manager
			    .getPhysicalCell(physicalCoordinate);
		    viewData = new ViewData(physicalCell,
			    cellFactory.getCell(physicalCoordinate));
		    setViewData(key, viewData);
		} else {
		    viewData.setEditing(true);
		}
		manager.startSelecting(physicalCoordinate);
		setValue(parent, selectedCell, key);
		viewData.getRenderer().focus(parent);
	    }
	}
    }

    /**
     * Render the required cell using the Cell Renderer applicable to the
     * physical coordinate of the cell.
     */
    @Override
    public void render(CellValue selectedCell, Object key, SafeHtmlBuilder sb) {

	key = selectedCell.getPhysicalCoordinate();
	Coordinate physicalCoordinate = (Coordinate) key;
	ViewData viewData = getViewData(key);

	if (viewData != null) {
	    if (viewData.isEditing()) {
		sb.append(viewData.getRenderer().getEditorCell(
			viewData.getOriginalValue()));
	    } else {
		sb.append(viewData.getRenderer().getReadOnly(
			viewData.getOriginalValue()));
		clearViewData(key);
		viewData = null;
	    }
	} else if (selectedCell != null) {
	    sb.append(cellFactory.getCell(physicalCoordinate).getReadOnly(
		    selectedCell));
	}
    }

    // Handle events relating to the cell while being edited
    private void editEvent(Element parent, CellValue selectedCell, Object key,
	    NativeEvent event, ValueUpdater<CellValue> valueUpdater) {

	Coordinate physicalCoordinate = selectedCell.getPhysicalCoordinate();
	key = physicalCoordinate;
	ViewData viewData = getViewData(key);

	String type = event.getType();
	boolean keyUp = "keyup".equals(type);
	boolean keyDown = "keydown".equals(type);
	if (keyUp || keyDown) {
	    int keyCode = event.getKeyCode();
	    if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
		commit(parent, key, viewData);

	    } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
		clearViewData(key);
		viewData.setEditing(false);
		viewData.getRenderer().cancel(parent);
		setValue(parent, selectedCell, null);
	    }

	} else if ("blur".equals(type)) {
//	    EventTarget eventTarget = event.getEventTarget();
//	    if (Element.is(eventTarget)) {
//		Element target = Element.as(eventTarget);
//		if ("input".equals(target.getTagName().toLowerCase())) {
		    commit(parent, key, viewData);
//		}
//	    }
	}
    }

    //Commit the value of the Cell's editor to the underlying table
    private void commit(Element parent, Object key, ViewData viewData) {
	clearViewData(key);
	CellValue newValue = viewData.getRenderer().commit(parent);
	setValue(parent, newValue, null);
	manager.update(newValue);

    }

}
