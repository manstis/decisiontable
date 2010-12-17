package org.drools.guvnor.decisiontable.client.widget;

import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Vertical implementation of MergableGridWidget, that renders columns as erm,
 * columns and rows as rows. Supports merging of cells between rows.
 * 
 * @author manstis
 * 
 */
public class VerticalMergableGridWidget extends MergableGridWidget {

    public VerticalMergableGridWidget(DecisionTableWidget dtable) {
	super(dtable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#deleteRow
     * (int)
     */
    @Override
    public void deleteRow(int index) {
	if (index < 0) {
	    throw new IllegalArgumentException(
		    "Index cannot be less than zero.");
	}
	if (index > data.size()) {
	    throw new IllegalArgumentException(
		    "Index cannot be greater than the number of rows.");
	}

	sideBarWidget.deleteSelector(index);
	tbody.deleteRow(index);
	fixRowStyles(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#
     * insertRowBefore(int)
     */
    @Override
    public void insertRowBefore(int index) {
	if (index < 0) {
	    throw new IllegalArgumentException(
		    "Index cannot be less than zero.");
	}
	if (index > data.size()) {
	    throw new IllegalArgumentException(
		    "Index cannot be greater than the number of rows.");
	}

	List<CellValue<? extends Comparable<?>>> rowData = data.get(index);
	sideBarWidget.insertSelectorBefore(index);
	TableRowElement newRow = tbody.insertRow(index);
	populateTableRowElement(newRow, rowData);
	fixRowStyles(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#redraw()
     */
    @Override
    public void redraw() {

	// Prepare sidebar
	sideBarWidget.initialise();

	TableSectionElement nbody = Document.get().createTBodyElement();

	for (int iRow = 0; iRow < data.size(); iRow++) {

	    // Add a selector for each row
	    sideBarWidget.addSelector();

	    TableRowElement tre = Document.get().createTRElement();
	    tre.setClassName(getRowStyle(iRow));

	    List<CellValue<? extends Comparable<?>>> rowData = data.get(iRow);
	    populateTableRowElement(tre, rowData);
	    nbody.appendChild(tre);
	}

	// Update table to DOM
	table.replaceChild(nbody, tbody);
	tbody = nbody;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#
     * redrawColumns(int)
     */
    @Override
    public void redrawColumns(int startRedrawIndex) {
	if (startRedrawIndex < 0) {
	    throw new IllegalArgumentException(
		    "Start Column index cannot be less than zero.");
	}
	if (startRedrawIndex > columns.size() - 1) {
	    throw new IllegalArgumentException(
		    "Start Column index cannot be greater than the number of defined columns.");
	}
	for (int iRow = 0; iRow < data.size(); iRow++) {
	    TableRowElement tre = tbody.getRows().getItem(iRow);
	    List<CellValue<? extends Comparable<?>>> rowData = data.get(iRow);
	    redrawTableRowElement(rowData, tre, startRedrawIndex);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#redrawRows
     * (int, int)
     */
    @Override
    public void redrawRows(int startRedrawIndex, int endRedrawIndex) {
	if (startRedrawIndex < 0) {
	    throw new IllegalArgumentException(
		    "Start Row index cannot be less than zero.");
	}
	if (startRedrawIndex > data.size() - 1) {
	    throw new IllegalArgumentException(
		    "Start Row index cannot be greater than the number of rows in the table.");
	}
	if (endRedrawIndex < 0) {
	    throw new IllegalArgumentException(
		    "End Row index cannot be less than zero.");
	}
	if (endRedrawIndex > data.size() - 1) {
	    throw new IllegalArgumentException(
		    "End Row index cannot be greater than the number of rows in the table.");
	}
	if (endRedrawIndex < startRedrawIndex) {
	    throw new IllegalArgumentException(
		    "End Row index cannot be greater than Start Row index.");
	}

	for (int iRow = startRedrawIndex; iRow <= endRedrawIndex; iRow++) {
	    TableRowElement newRow = Document.get().createTRElement();
	    List<CellValue<? extends Comparable<?>>> rowData = data.get(iRow);
	    populateTableRowElement(newRow, rowData);
	    tbody.replaceChild(newRow, tbody.getChild(iRow));
	}
	fixRowStyles(startRedrawIndex);
    }

    // Redraw a row adding new cells if necessary
    private void redrawTableRowElement(
	    List<CellValue<? extends Comparable<?>>> rowData,
	    TableRowElement tre, int index) {
	for (int iCol = index; iCol < columns.size(); iCol++) {
	    if (iCol < tre.getCells().getLength()) {
		TableCellElement newCell = makeTableCellElement(iCol, rowData);
		TableCellElement oldCell = tre.getCells().getItem(iCol);
		tre.replaceChild(newCell, oldCell);
	    } else {
		TableCellElement newCell = makeTableCellElement(iCol, rowData);
		tre.appendChild(newCell);
	    }
	}

    }

    // Row styles need to be re-applied after inserting and deleting rows
    private void fixRowStyles(int iRow) {
	while (iRow < tbody.getChildCount()) {
	    Element e = Element.as(tbody.getChild(iRow));
	    TableRowElement tre = TableRowElement.as(e);
	    tre.setClassName(getRowStyle(iRow));
	    iRow++;
	}
    }

    // Get style applicable to row
    private String getRowStyle(int iRow) {
	String evenRowStyle = style.cellTableEvenRow();
	String oddRowStyle = style.cellTableOddRow();
	boolean isEven = iRow % 2 == 0;
	String trClasses = isEven ? evenRowStyle : oddRowStyle;
	return trClasses;
    }

    // Build a TableCellElement
    private TableCellElement makeTableCellElement(int iCol,
	    List<CellValue<? extends Comparable<?>>> rowData) {

	String cellStyle = style.cellTableCell();
	String divStyle = style.cellTableCellDiv();
	TableCellElement tce = null;

	// Column to render the column
	DynamicEditColumn column = columns.get(iCol);

	CellValue<? extends Comparable<?>> cellData = rowData.get(iCol);
	int rowSpan = cellData.getRowSpan();
	if (rowSpan > 0) {

	    // Use Elements rather than Templates as it's easier to set
	    // attributes that need to be dynamic
	    tce = Document.get().createTDElement();
	    DivElement div = Document.get().createDivElement();
	    tce.setClassName(cellStyle);
	    div.setClassName(divStyle);

	    // A dynamic attribute!
	    tce.getStyle().setHeight(style.rowHeight() * rowSpan, Unit.PX);
	    tce.setRowSpan(rowSpan);

	    // Render the cell and set inner HTML
	    SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
	    if (rowData != null) {
		column.render(rowData, null, cellBuilder);
	    }
	    div.setInnerHTML(cellBuilder.toSafeHtml().asString());

	    // Construct the table
	    tce.appendChild(div);
	}
	return tce;

    }

    // Populate the content of a TableRowElement
    private TableRowElement populateTableRowElement(TableRowElement tre,
	    List<CellValue<? extends Comparable<?>>> rowData) {

	for (int iCol = 0; iCol < columns.size(); iCol++) {
	    TableCellElement tce = makeTableCellElement(iCol, rowData);
	    if (tce != null) {
		tre.appendChild(tce);
	    }
	}

	return tre;

    }

}
