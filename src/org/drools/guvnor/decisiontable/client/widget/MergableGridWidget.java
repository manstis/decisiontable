package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource;
import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource.CellTableStyle;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A minimal CellTable replacement that renders merged cells and handles basic
 * events. No keyboard navigation implemented.
 * 
 * @author manstis
 * 
 */
public abstract class MergableGridWidget extends Widget {

    // Data to render
    protected List<DynamicEditColumn> columns = new ArrayList<DynamicEditColumn>();
    protected List<List<CellValue<? extends Comparable<?>>>> data = new ArrayList<List<CellValue<? extends Comparable<?>>>>();

    // TABLE elements
    protected TableElement table;
    protected TableSectionElement tbody;

    // Resources
    protected static final CellTableResource resource = GWT
	    .create(CellTableResource.class);
    protected static final CellTableStyle style = resource.cellTableStyle();

    // The DecisionTable to which this grid belongs. This is used soley
    // to record when a cell has been clicked as the DecisionTable manages
    // writing values back to merged cells...
    protected DecisionTableWidget dtable;
    protected DecisionTableHeaderWidget headerWidget;
    protected DecisionTableSidebarWidget sideBarWidget;

    /**
     * A grid of possibly merged cells.
     * 
     * @param dtable
     *            DecisionTable to which the grid is a child
     * @param resource
     *            ClientBundle for the grid
     */
    public MergableGridWidget(DecisionTableWidget dtable) {

	this.dtable = dtable;
	this.sideBarWidget = dtable.getSidebarWidget();
	this.headerWidget = dtable.getHeaderWidget();

	style.ensureInjected();

	// Create some elements to contain the grid
	table = Document.get().createTableElement();
	tbody = Document.get().createTBodyElement();
	table.setClassName(style.cellTable());
	table.setCellPadding(0);
	table.setCellSpacing(0);
	setElement(table);

	table.appendChild(tbody);

	// Events in which we're interested (note, if a Cell<?> appears not to
	// work I've probably forgotten some events. Might be a better way of
	// doing this, but I copied CellTable<?, ?>' lead
	sinkEvents(Event.getTypeInt("click") | Event.getTypeInt("mouseover")
		| Event.getTypeInt("mouseout") | Event.getTypeInt("change"));
    }

    /**
     * Add a column at the end of the list of columns
     * 
     * @param column
     */
    public void addColumn(DynamicEditColumn column) {
	insertColumnBefore(columns.size(), column);
    }

    /**
     * Add a column at a specific index
     * 
     * @param index
     * @param column
     */
    public void insertColumnBefore(int index, DynamicEditColumn column) {
	columns.add(index, column);
	
	// Re-index columns
	for (int iCol = 0; iCol < columns.size(); iCol++) {
	    DynamicEditColumn col = columns.get(iCol);
	    col.setColumnIndex(iCol);
	}
	
	headerWidget.insertColumnBefore(index, column);
    };

    /**
     * Delete the row at the given index. Partial redraw.
     * 
     * @param index
     */
    public abstract void deleteRow(int index);

    /**
     * Get a list of columns (Woot, CellTable lacks this!)
     * 
     * @return
     */
    public List<DynamicEditColumn> getColumns() {
	return this.columns;
    }

    /**
     * Insert the given row before the provided index. Partial redraw.
     * 
     * @param index
     *            The index of the row before which the new row should be
     *            inserted
     * @param rowData
     *            The row of data to insert
     */
    public abstract void insertRowBefore(int index,
	    List<CellValue<? extends Comparable<?>>> rowData);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user
     * .client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {

	// Get the event target.
	EventTarget eventTarget = event.getEventTarget();
	if (!Element.is(eventTarget)) {
	    return;
	}
	Element target = event.getEventTarget().cast();

	// Find the cell where the event occurred.
	TableCellElement tableCell = findNearestParentCell(target);
	if (tableCell == null) {
	    return;
	}

	Element trElem = tableCell.getParentElement();
	if (trElem == null) {
	    return;
	}
	TableRowElement tr = TableRowElement.as(trElem);

	// Forward the event to the associated header, footer, or column.
	int iCol = tableCell.getCellIndex();
	int iRow = tr.getSectionRowIndex();
	String eventType = event.getType();

	// Convert HTML coordinates to physical coordinates
	List<CellValue<? extends Comparable<?>>> htmlRow = data.get(iRow);
	CellValue<? extends Comparable<?>> htmlCell = htmlRow.get(iCol);
	Coordinate c = htmlCell.getPhysicalCoordinate();
	CellValue<? extends Comparable<?>> physicalCell = data.get(c.getRow())
		.get(c.getCol());

	// Setup the selected range
	if (eventType.equals("click")) {
	    dtable.startSelecting(c);
	}

	// Pass event and physical cell to Cell Widget for handling
	Cell<CellValue<? extends Comparable<?>>> cellWidget = columns.get(
		c.getCol()).getCell();
	if (cellWidget.getConsumedEvents().contains(eventType)) {
	    Element parent = getCellParent(tableCell);
	    cellWidget.onBrowserEvent(parent, physicalCell, null, event, null);
	}
    }

    /**
     * Redraw the whole table
     */
    public abstract void redraw();

    /**
     * Redraw table columns from index to the end. Partial redraw
     * 
     * @param startRedrawIndex
     *            Start column index (inclusive)
     */
    public abstract void redrawColumns(int startRedrawIndex);

    /**
     * Redraw a section of the table. Partial redraw
     * 
     * @param startRedrawIndex
     *            Start row index (inclusive)
     * @param endRedrawIndex
     *            End row index (inclusive)
     */
    public abstract void redrawRows(int startRedrawIndex, int endRedrawIndex);

    /**
     * Delete all columns
     */
    public void removeAllColumns() {
	columns.clear();
	headerWidget.initialise();
    }

    /**
     * Remove a column at a specific index
     * 
     * @param index
     */
    public void removeColumn(int index) {
	columns.remove(index);
    }

    /**
     * Change the visibility of a column
     * 
     * @param index
     *            Column index
     * @param isVisible
     */
    public void setColumnVisibility(int index, boolean isVisible) {
	if (index < 0) {
	    throw new IllegalArgumentException(
		    "Start Column index cannot be less than zero.");
	}
	if (index > columns.size() - 1) {
	    throw new IllegalArgumentException(
		    "Start Column index cannot be greater than the number of defined columns.");
	}
    }

    /**
     * Set the data to be rendered.
     * 
     * @param data
     */
    public void setRowData(List<List<CellValue<? extends Comparable<?>>>> data) {
	this.data = data;
    }

    // Find the cell that contains the element. Note that the TD element is not
    // the parent. The parent is the div inside the TD cell.
    private TableCellElement findNearestParentCell(Element elem) {
	while ((elem != null) && (elem != table)) {
	    String tagName = elem.getTagName();
	    if ("td".equalsIgnoreCase(tagName)
		    || "th".equalsIgnoreCase(tagName)) {
		return elem.cast();
	    }
	    elem = elem.getParentElement();
	}
	return null;
    }

    // Get the parent element that is passed to the {@link Cell} from the table
    // cell element.
    private Element getCellParent(TableCellElement td) {
	return td.getFirstChildElement();
    }

}
