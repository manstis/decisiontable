package org.drools.guvnor.decisiontable.client.widget;

import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalDecisionTableHeaderWidget extends
	DecisionTableHeaderWidget {

    /**
     * This is the actual header widget.
     * 
     * @author manstis
     * 
     */
    private class HeaderWidget extends Widget {

	private TableElement table;
	private TableSectionElement tbody;

	private HeaderWidget() {
	    table = Document.get().createTableElement();
	    table.setCellPadding(0);
	    table.setCellSpacing(0);
	    table.setClassName(resource.cellTableStyle().headerTable());
	    initialise();
	    sinkEvents(Event.getTypeInt("click"));
	    setElement(table);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
	 * .user.client.Event)
	 */
	@Override
	public void onBrowserEvent(Event event) {
	    String type = event.getType();

	    EventTarget eventTarget = event.getEventTarget();
	    if (!Element.is(eventTarget)
		    || !getElement().isOrHasChild(Element.as(eventTarget))) {
		return;
	    }

	    Element target = event.getEventTarget().cast();

	    // Find the cell where the event occurred.
	    TableCellElement cell = findNearestParentCell(target);
	    if (cell == null) {
		return;
	    }

	    if (type.equals("click")) {
		if (isInteractiveCell(cell)) {
		    int col = cell.getPropertyInt("col");
		    DynamicEditColumn column = columns.get(col);
		    sortableHeaderClicked(column);
		}
	    }
	}

	// Find the nearest TableCellElement to where the event occurred
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

	// The second row of cells in the header is only populated for Action
	// columns. We therefore need to offset the column index by the number
	// of cells that do not require a second row.
	private int getFactFieldColumnOffset(int index) {
	    int offset = 0;
	    for (int iCol = 0; iCol < index; iCol++) {
		DynamicEditColumn column = columns.get(iCol);
		if (column.getModelColumn() instanceof ConditionCol) {
		    offset++;
		}
	    }
	    return offset;
	}

	// Get the InnerHTML to render the applicable icon
	private String getSortDirectionIcon(SortDirection sd, int sortIndex) {
	    String html = "";
	    switch (sd) {
	    case ASCENDING:
		html = (sortIndex == 0 ? UP_ARROW : SMALL_UP_ARROW);
		break;
	    case DESCENDING:
		html = (sortIndex == 0 ? DOWN_ARROW : SMALL_DOWN_ARROW);
	    }
	    return html;
	}

	// Get a TableCellElement on a given row
	private TableCellElement getTableCellElement(TableRowElement tre,
		int iCol) {
	    Node cellNode = tre.getChild(iCol);
	    Element cellElement = Element.as(cellNode);
	    return TableCellElement.as(cellElement);
	}

	// Get a TableRowElement
	private TableRowElement getTableRowElement(int iRow) {
	    Node rowNode = tbody.getChild(iRow);
	    Element rowElement = Element.as(rowNode);
	    return TableRowElement.as(rowElement);
	}

	// Create a two row empty table
	private void initialise() {
	    if (this.tbody == null) {
		this.tbody = Document.get().createTBodyElement();
		this.table.appendChild(this.tbody);
	    } else {
		TableSectionElement newBody = Document.get()
			.createTBodyElement();
		table.replaceChild(newBody, tbody);
		this.tbody = newBody;
	    }
	    tbody.appendChild(makeTableRowElement());
	    tbody.appendChild(makeTableRowElement());
	    columns.clear();
	}

	// Insert a column at the given index
	private void insertColumnBefore(int index, DynamicEditColumn column) {
	    columns.add(index, column);
	    populateColumn(index, column);
	    reindexColumns();
	}

	// Check whether a cell is interactive
	private boolean isInteractiveCell(TableCellElement tce) {
	    int row = tce.getPropertyInt("row");
	    int col = tce.getPropertyInt("col");

	    DynamicEditColumn column = columns.get(col);
	    switch (row) {
	    case 0:
		if (!(column.getModelColumn() instanceof ConditionCol)) {
		    return true;
		}
		break;
	    case 1:
		if (column.getModelColumn() instanceof ConditionCol) {
		    return true;
		}
	    }
	    return false;
	}

	// Make a new TableRowElement
	private TableRowElement makeTableRowElement() {
	    TableRowElement trow = Document.get().createTRElement();
	    trow.setClassName(resource.cellTableStyle().headerRow());
	    return trow;
	}

	// Populate a TableCellElement for an Action column
	private void populateActionTableCellElement(TableCellElement tce,
		DynamicEditColumn column) {
	    populateTableCellPrimaryElement(tce, column);
	    setText(tce, ((ActionCol) column.getModelColumn()).getHeader());
	    tce.setRowSpan(2);
	}

	// Populate a TableCellElement for an Attribute column
	private void populateAttributeTableCellElement(TableCellElement tce,
		DynamicEditColumn column) {
	    populateTableCellPrimaryElement(tce, column);
	    setText(tce, ((AttributeCol) column.getModelColumn()).attr);
	    tce.setRowSpan(2);
	}

	// Populate a column
	private void populateColumn(int index, DynamicEditColumn column) {

	    // Second row index
	    int offset = getFactFieldColumnOffset(index);

	    DTColumnConfig modelColumn = column.getModelColumn();
	    if (modelColumn instanceof MetadataCol) {
		TableRowElement tre = getTableRowElement(0);
		TableCellElement tce = tre.insertCell(index);
		populateMetadataTableCellElement(tce, column);

	    } else if (modelColumn instanceof AttributeCol) {
		TableRowElement tre = getTableRowElement(0);
		TableCellElement tce = tre.insertCell(index);
		populateAttributeTableCellElement(tce, column);

	    } else if (modelColumn instanceof ConditionCol) {
		TableRowElement tre = getTableRowElement(0);
		TableCellElement tce = tre.insertCell(index);
		populateConditionFactTypeTableCellElement(tce, column);

		tre = getTableRowElement(1);
		tce = tre.insertCell(offset);
		populateConditionFactFieldTableCellElement(tce, column);

	    } else if (modelColumn instanceof ActionCol) {
		TableRowElement tre = getTableRowElement(0);
		TableCellElement tce = tre.insertCell(index);
		populateActionTableCellElement(tce, column);

	    }
	}

	// Populate a TableCellElement for an Condition Fact Field column
	private void populateConditionFactFieldTableCellElement(
		TableCellElement tce, DynamicEditColumn column) {
	    String factField = ((ConditionCol) column.getModelColumn())
		    .getFactField();
	    populateTableCellPrimaryElement(tce, column);
	    setText(tce, factField);
	}

	// Populate a TableCellElement for an Condition Fact Type column
	private void populateConditionFactTypeTableCellElement(
		TableCellElement tce, DynamicEditColumn column) {
	    String factType = ((ConditionCol) column.getModelColumn())
		    .getFactType();
	    populateTableCellSecondaryElement(tce, column);
	    setText(tce, factType);
	}

	// Populate a TableCellElement for a Metadata column
	private void populateMetadataTableCellElement(TableCellElement tce,
		DynamicEditColumn column) {
	    populateTableCellPrimaryElement(tce, column);
	    setText(tce, ((MetadataCol) column.getModelColumn()).attr);
	    tce.setRowSpan(2);
	}

	// Populate a TableCellElement with general scaffolding for row zero
	private void populateTableCellPrimaryElement(TableCellElement tce,
		DynamicEditColumn column) {
	    tce.setClassName(resource.cellTableStyle().headerCellPrimary());

	    DivElement div1 = Document.get().createDivElement();
	    DivElement div2 = Document.get().createDivElement();
	    DivElement div3 = Document.get().createDivElement();
	    div1.setClassName(resource.cellTableStyle().headerContainer());
	    div2.setClassName(resource.cellTableStyle().headerText());
	    div3.setClassName(resource.cellTableStyle().headerWidget());

	    div1.appendChild(div2);
	    div1.appendChild(div3);
	    div3.setInnerHTML(getSortDirectionIcon(column.getSortDirection(),
		    column.getSortIndex()));

	    tce.appendChild(div1);
	}

	// Populate a TableCellElement with general scaffolding for row one
	private void populateTableCellSecondaryElement(TableCellElement tce,
		DynamicEditColumn column) {
	    tce.setClassName(resource.cellTableStyle().headerCellSecondary());

	    DivElement div1 = Document.get().createDivElement();
	    DivElement div2 = Document.get().createDivElement();
	    div1.setClassName(resource.cellTableStyle().headerContainer());
	    div2.setClassName(resource.cellTableStyle().headerText());
	    div1.appendChild(div2);
	    tce.appendChild(div1);
	}

	// Redraw entire header
	private void redraw() {
	    if (this.tbody == null) {
		this.tbody = Document.get().createTBodyElement();
		this.table.appendChild(this.tbody);
	    } else {
		TableSectionElement newBody = Document.get()
			.createTBodyElement();
		table.replaceChild(newBody, tbody);
		this.tbody = newBody;
	    }
	    tbody.appendChild(makeTableRowElement());
	    tbody.appendChild(makeTableRowElement());

	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		DynamicEditColumn column = columns.get(iCol);
		populateColumn(iCol, column);
	    }
	    reindexColumns();
	}

	// When a new column is inserted the metadata containing indexing needs
	// to be updated
	private void reindexColumns() {

	    for (int iCol = 0; iCol < columns.size(); iCol++) {

		DynamicEditColumn column = columns.get(iCol);
		DTColumnConfig modelColumn = column.getModelColumn();

		TableRowElement tre = getTableRowElement(0);
		TableCellElement tce = getTableCellElement(tre, iCol);
		tce.setPropertyInt("row", 0);
		tce.setPropertyInt("col", iCol);
		if (modelColumn instanceof ConditionCol) {
		    tre = getTableRowElement(1);
		    int offset = getFactFieldColumnOffset(iCol);
		    tce = getTableCellElement(tre, offset);
		    tce.setPropertyInt("row", 1);
		    tce.setPropertyInt("col", iCol);
		}
	    }
	}

	// Set the InnerText on a TableCellElement allowing for the general
	// scaffolding
	private void setText(TableCellElement cell, String text) {
	    cell.getFirstChild().getFirstChild().<DivElement> cast()
		    .setInnerText(text);
	}

	// Update a sort icon
	private void updateSortIcon(TableCellElement tce,
		DynamicEditColumn column) {
	    Node divNode = tce.getFirstChild().getChild(1);
	    Element divElement = Element.as(divNode);
	    DivElement div = DivElement.as(divElement);
	    div.setInnerHTML(getSortDirectionIcon(column.getSortDirection(),
		    column.getSortIndex()));
	}

	// Update all sort icons when a column is sorted
	private void updateSortIcons() {
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		DynamicEditColumn column = columns.get(iCol);
		DTColumnConfig modelColumn = column.getModelColumn();
		if (!(modelColumn instanceof ConditionCol)) {
		    TableRowElement tre = getTableRowElement(0);
		    TableCellElement tce = getTableCellElement(tre, iCol);
		    updateSortIcon(tce, column);
		} else {
		    TableRowElement tre = getTableRowElement(1);
		    int offset = getFactFieldColumnOffset(iCol);
		    TableCellElement tce = getTableCellElement(tre, offset);
		    updateSortIcon(tce, column);
		}
	    }
	}

    }

    private HeaderWidget widget;

    /**
     * Construct a "Header" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableHeaderWidget(DecisionTableWidget dtable) {
	super(dtable);

	// Construct the Widget
	panel = new ScrollPanel();
	widget = new HeaderWidget();

	// We don't want scroll bars on the Header
	panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
	panel.add(widget);
	initWidget(panel);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
     * #initialise()
     */
    @Override
    public void initialise() {
	widget.initialise();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
     * #insertColumnBefore(int,
     * org.drools.guvnor.decisiontable.client.widget.DynamicEditColumn)
     */
    @Override
    public void insertColumnBefore(int index, DynamicEditColumn column) {
	widget.insertColumnBefore(index, column);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
     * #redraw()
     */
    @Override
    public void redraw() {
	widget.redraw();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
     * #setScrollPosition(int)
     */
    @Override
    public void setScrollPosition(int position) {
	((ScrollPanel) this.panel).setHorizontalScrollPosition(position);
    }

    // A column has been clicked for sorting
    private void sortableHeaderClicked(DynamicEditColumn column) {
	updateSortOrder(column);
	widget.updateSortIcons();
	dtable.sort();
    }

    // Update sort order. The column clicked becomes the primary sort column and
    // the other, previously sorted, columns degrade in priority
    private void updateSortOrder(DynamicEditColumn column) {
	if (column.getSortIndex() == 0) {
	    if (column.getSortDirection() != SortDirection.ASCENDING) {
		column.setSortDirection(SortDirection.ASCENDING);
	    } else {
		column.setSortDirection(SortDirection.DESCENDING);
	    }
	} else {
	    column.setSortIndex(0);
	    column.setSortDirection(SortDirection.ASCENDING);
	    int sortIndex = 1;
	    for (DynamicEditColumn sortableColumn : columns) {
		if (!sortableColumn.equals(column)) {
		    if (sortableColumn.getSortDirection() != SortDirection.NONE) {
			sortableColumn.setSortIndex(sortIndex);
			sortIndex++;
		    }
		}
	    }
	}
    }

}
