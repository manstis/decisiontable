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
    protected CellTableResource resource = GWT.create(CellTableResource.class);
    protected CellTableStyle style;

    // The DecisionTable to which this grid belongs. This is used soley
    // to record when a cell has been clicked as the DecisionTable manages
    // writing values back to merged cells...
    protected DecisionTableWidget dtable;

    /**
     * A grid of possibly merged cells.
     * 
     * @param dtable
     *            DecisionTable to which the grid is a child
     * @param resource
     *            ClientBundle for the grid
     */
    public MergableGridWidget(DecisionTableWidget dtable,
	    CellTableResource resource) {

	this.dtable = dtable;
	this.resource = resource;
	this.style = resource.cellTableStyle();
	this.style.ensureInjected();

	// Create some elements to contain the grid
	table = Document.get().createTableElement();
	tbody = Document.get().createTBodyElement();
	table.setClassName(this.style.cellTable());
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
     * Delete all columns
     */
    public void removeAllColumns() {
	columns.clear();
    }

    /**
     * Remove a column at a specific index
     * 
     * @param index
     */
    public void removeColumn(int index) {
	columns.remove(index);
    };

    /**
     * Add a column at the end of the list of columns
     * 
     * @param column
     */
    public void addColumn(DynamicEditColumn column) {
	columns.add(column);
    }

    /**
     * Add a column at a specific index
     * 
     * @param index
     * @param column
     */
    public void addColumn(int index, DynamicEditColumn column) {
	columns.add(index, column);
    }

    /**
     * Get a list of columns (Woot, CellTable lacks this!)
     * 
     * @return
     */
    public List<DynamicEditColumn> getColumns() {
	return this.columns;
    }

    /**
     * Set the data to be rendered. HasData<?> has this method but it also
     * forces you to implement paging that we did not need.
     * 
     * @param data
     */
    public void setRowData(List<List<CellValue<? extends Comparable<?>>>> data) {
	this.data = data;
    }

    /**
     * Render the data as HTML
     */
    public abstract void redraw();

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
