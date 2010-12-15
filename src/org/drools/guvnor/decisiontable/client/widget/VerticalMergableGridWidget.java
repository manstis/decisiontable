package org.drools.guvnor.decisiontable.client.widget;

import java.util.List;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
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
    public void redraw() {

	//Prepare sidebar
	sideBarWidget.initialise();
	
	// Could do with a heap load more styles
	String evenRowStyle = style.cellTableEvenRow();
	String oddRowStyle = style.cellTableOddRow();
	String cellStyle = style.cellTableCell();
	String divStyle = style.cellTableCellDiv();

	TableSectionElement nbody = Document.get().createTBodyElement();

	for (int iRow = 0; iRow < data.size(); iRow++) {

	    //Add a selector for each row
	    sideBarWidget.addSelector(iRow);

	    boolean isEven = iRow % 2 == 0;
	    String trClasses = isEven ? evenRowStyle : oddRowStyle;
	    List<CellValue<? extends Comparable<?>>> rowData = data.get(iRow);

	    TableRowElement tre = Document.get().createTRElement();
	    tre.addClassName(trClasses);

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
		    tce.addClassName(cellStyle);
		    div.addClassName(divStyle);

		    // A dynamic attribute!
		    tce.getStyle().setHeight(style.rowHeight() * rowSpan,
			    Unit.PX);
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

	    nbody.appendChild(tre);
	}

	// Update table to DOM
	table.replaceChild(nbody, tbody);
	tbody = nbody;

    }

}
