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

    @Override
    public void insertRowBefore(int index,
	    List<CellValue<? extends Comparable<?>>> rowData) {
	sideBarWidget.insertSelectorBefore(index);
	TableRowElement newRow = tbody.insertRow(index);
	populateTableRowElement(newRow, rowData);
	fixRowStyles(index);
    }

    @Override
    public void deleteRow(int index) {
	sideBarWidget.deleteSelector(index);
	tbody.deleteRow(index);
	fixRowStyles(index);
    }

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

    @Override
    public void redrawRows(int minRedrawRow, int maxRedrawRow) {
	for (int iRow = minRedrawRow; iRow <= maxRedrawRow; iRow++) {
	    TableRowElement newRow = Document.get().createTRElement(); 
	    List<CellValue<? extends Comparable<?>>> rowData = data.get(iRow);
	    populateTableRowElement(newRow, rowData);
	    tbody.replaceChild(newRow, tbody.getChild(iRow));
	}
	fixRowStyles(minRedrawRow);
    }

    private TableRowElement populateTableRowElement(TableRowElement tre,
	    List<CellValue<? extends Comparable<?>>> rowData) {

	String cellStyle = style.cellTableCell();
	String divStyle = style.cellTableCellDiv();

	for (int iCol = 0; iCol < columns.size(); iCol++) {

	    // Column to render the column
	    DynamicEditColumn column = columns.get(iCol);

	    CellValue<? extends Comparable<?>> cellData = rowData.get(iCol);
	    int rowSpan = cellData.getRowSpan();
	    if (rowSpan > 0) {

		// Use Elements rather than Templates as it's easier to set
		// attributes that need to be dynamic
		TableCellElement tce = Document.get().createTDElement();
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
		tre.appendChild(tce);
	    }

	}
	return tre;

    }

    private String getRowStyle(int iRow) {
	// Could do with a heap load more styles
	String evenRowStyle = style.cellTableEvenRow();
	String oddRowStyle = style.cellTableOddRow();

	boolean isEven = iRow % 2 == 0;
	String trClasses = isEven ? evenRowStyle : oddRowStyle;
	return trClasses;

    }

    private void fixRowStyles(int iRow) {
	while (iRow < tbody.getChildCount()) {
	    Element e = Element.as(tbody.getChild(iRow));
	    TableRowElement tre = TableRowElement.as(e);
	    tre.setClassName(getRowStyle(iRow));
	    iRow++;
	}
    }

}
