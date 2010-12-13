package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource;
import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource.CellTableStyle;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class VerticalHeaderlessCellTable extends Widget {

    private List<DynamicEditColumn> columns = new ArrayList<DynamicEditColumn>();
    private List<List<CellValue<?>>> data = new ArrayList<List<CellValue<?>>>();

    private TableElement table;
    private TableSectionElement tbody;

    protected CellTableResource resource = GWT.create(CellTableResource.class);

    protected CellTableStyle style;

    protected SelectionManager manager;

    public VerticalHeaderlessCellTable(CellTableResource resource) {

	this.resource = resource;
	this.style = resource.cellTableStyle();
	this.style.ensureInjected();

	table = Document.get().createTableElement();
	tbody = Document.get().createTBodyElement();
	table.setClassName(this.style.cellTable());
	table.setCellPadding(0);
	table.setCellSpacing(0);
	setElement(table);

	table.appendChild(tbody);

	sinkEvents(Event.getTypeInt("click") | Event.getTypeInt("mouseover")
		| Event.getTypeInt("mouseout"));
    }

    public void setSelectionManager(SelectionManager manager) {
	this.manager = manager;
    }

    public void removeAllColumns() {
	columns.clear();
    }

    public void removeColumn(int index) {
	columns.remove(index);
    };

    public void addColumn(DynamicEditColumn column) {
	columns.add(column);
    }

    public void addColumn(int index, DynamicEditColumn column) {
	columns.add(index, column);
    }

    public void setRowData(List<List<CellValue<?>>> data) {
	this.data = data;
    }

    public void redraw() {

	String evenRowStyle = style.cellTableEvenRow();
	String oddRowStyle = style.cellTableOddRow();
	String cellStyle = style.cellTableCell();

	TableSectionElement nbody = Document.get().createTBodyElement();

	for (int iRow = 0; iRow < data.size(); iRow++) {

	    List<CellValue<?>> rowData = data.get(iRow);

	    boolean isEven = iRow % 2 == 0;
	    String trClasses = isEven ? evenRowStyle : oddRowStyle;

	    TableRowElement tre = Document.get().createTRElement();
	    tre.addClassName(trClasses);

	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		DynamicEditColumn column = columns.get(iCol);

		String tdClasses = cellStyle;
		String divClasses = cellStyle;

		CellValue<?> cellData = rowData.get(iCol);
		int rowSpan = cellData.getRowSpan();
		if (rowSpan > 0) {

		    TableCellElement tce = Document.get().createTDElement();
		    DivElement div = Document.get().createDivElement();
		    tce.addClassName(tdClasses);
		    div.addClassName(divClasses);

		    tce.getStyle().setHeight(style.rowHeight() * rowSpan,
			    Unit.PX);
		    tce.setRowSpan(rowSpan);

		    SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
		    if (rowData != null) {
			column.render(rowData, null, cellBuilder);
		    }
		    div.setInnerHTML(cellBuilder.toSafeHtml().asString());
		    tce.appendChild(div);
		    tre.appendChild(tce);
		}

	    }

	    nbody.appendChild(tre);
	}
	table.replaceChild(nbody, tbody);
	tbody = nbody;

    }

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
	List<CellValue<?>> htmlRow = data.get(iRow);
	CellValue<?> htmlCell = htmlRow.get(iCol);
	Coordinate c = htmlCell.getPhysicalCoordinate();
	CellValue<?> physicalCell = data.get(c.getRow()).get(c.getCol());

	// Setup the selected range
	if (eventType.equals("click")) {
	    manager.startSelecting(c);
	}

	// Pass event and physical cell to Cell Widget for handling
	Cell<CellValue<?>> cellWidget = columns.get(c.getCol()).getCell();
	if (cellWidget.getConsumedEvents().contains(eventType)) {
	    Element parent = getCellParent(tableCell);
	    cellWidget.onBrowserEvent(parent, physicalCell, null, event, null);
	}
    }

    /**
     * Find the cell that contains the element. Note that the TD element is not
     * the parent. The parent is the div inside the TD cell.
     * 
     * @param elem
     *            the element
     * @return the parent cell
     */
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

    /**
     * Get the parent element that is passed to the {@link Cell} from the table
     * cell element.
     * 
     * @param td
     *            the table cell
     * @return the parent of the {@link Cell}
     */
    private Element getCellParent(TableCellElement td) {
	return td.getFirstChildElement();
    }

}
