package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

/**
 * An editable text cell. Click to edit, escape to cancel, return to commit.
 */
public class EditableCell extends
	AbstractEditableCell<CellValue, EditableCell.ViewData> {

    interface Template extends SafeHtmlTemplates {
	@Template("<input style=\"width:40px\" type=\"text\" value=\"{0}\" tabindex=\"-1\"></input>")
	SafeHtml input(String value);

	@Template("<div>{0}</div>")
	SafeHtml text(String value);
    }

    static class ViewData {

	private boolean isEditing;

	private Object originalValue;

	public ViewData(Object originalValue) {
	    this.originalValue = originalValue;
	    this.isEditing = true;
	}

	public Object getOriginalValue() {
	    return originalValue;
	}

	@Override
	public boolean equals(Object o) {
	    if (o == null) {
		return false;
	    }
	    ViewData vd = (ViewData) o;
	    return equalsOrBothNull(originalValue, vd.originalValue)
		    && isEditing == vd.isEditing;
	}

	@Override
	public int hashCode() {
	    return originalValue.hashCode() * 29
		    + Boolean.valueOf(isEditing).hashCode();
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

    private static Template template;

    private final SafeHtmlRenderer<String> renderer;

    private final SelectionManager manager;

    public EditableCell(SelectionManager manager) {
	this(manager, SimpleSafeHtmlRenderer.getInstance());
    }

    public EditableCell(SelectionManager manager,
	    SafeHtmlRenderer<String> renderer) {
	super("click", "keyup", "keydown", "blur");
	if (template == null) {
	    template = GWT.create(Template.class);
	}
	if (renderer == null) {
	    throw new IllegalArgumentException("renderer == null");
	}
	this.renderer = renderer;
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
		    viewData = new ViewData(physicalCell.getValue());
		    setViewData(key, viewData);
		} else {
		    viewData.setEditing(true);
		}
		manager.startSelecting(physicalCoordinate);
		edit(parent, selectedCell, key);
	    }
	}
    }

    protected void edit(Element parent, CellValue selectedCell, Object key) {
	setValue(parent, selectedCell, key);
	InputElement input = getInputElement(parent);
	input.focus();
	input.select();
    }

    @Override
    public void render(CellValue selectedCell, Object key, SafeHtmlBuilder sb) {

	key = selectedCell.getPhysicalCoordinate();
	ViewData viewData = getViewData(key);

	if (viewData != null) {
	    if (viewData.isEditing()) {
		sb.append(template
			.input(viewData.getOriginalValue().toString()));
	    } else {
		// SafeHtml html = renderer.render());
		sb.append(template.text(viewData.getOriginalValue().toString()));
		clearViewData(key);
		viewData = null;
	    }
	} else if (selectedCell != null) {
	    // SafeHtml html = renderer.render();
	    sb.append(template.text(selectedCell.getValue().toString()));
	}
    }

    private void cancel(Element parent, CellValue selectedCell) {
	clearInput(getInputElement(parent));
	setValue(parent, selectedCell, null);
    }

    /**
     * Clear selected from the input element. Both Firefox and IE fire spurious
     * onblur events after the input is removed from the DOM if selection is not
     * cleared.
     * 
     * @param input
     *            the input element
     */
    private native void clearInput(Element input) /*-{
        if (input.selectionEnd)
        input.selectionEnd = input.selectionStart;
        else if ($doc.selection)
        $doc.selection.clear();
    }-*/;

    private void commit(Element parent) {
	clearInput(getInputElement(parent));
	InputElement ie = getInputElement(parent);
	String text = ie.getValue();
	CellValue cell = new CellValue(text, 0, 0);
	setValue(parent, cell, null);
	manager.setSelectionValue(text);
    }

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
		clearViewData(key);
		commit(parent);
	    } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
		viewData.setEditing(false);
		cancel(parent, selectedCell);
		clearViewData(key);
	    }
	} else if ("blur".equals(type)) {
	    EventTarget eventTarget = event.getEventTarget();
	    if (Element.is(eventTarget)) {
		Element target = Element.as(eventTarget);
		if ("input".equals(target.getTagName().toLowerCase())) {
		    clearViewData(key);
		    commit(parent);
		}
	    }
	}
    }

    private InputElement getInputElement(Element parent) {
	return parent.getFirstChild().<InputElement> cast();
    }

}
