package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.drools.guvnor.decisiontable.client.widget.cells.CellFactory;
import org.drools.guvnor.decisiontable.client.widget.cells.CellValueFactory;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Abstract Decision Table encapsulating basic operation.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableWidget extends Composite implements
	ValueUpdater<Object> {

    // Widgets for UI
    protected Panel mainPanel;
    protected Panel bodyPanel;
    protected ScrollPanel scrollPanel;
    protected MergableGridWidget gridWidget;
    protected DecisionTableHeaderWidget headerWidget;
    protected DecisionTableSidebarWidget sidebarWidget;
    protected CellFactory cellFactory = new CellFactory();

    protected boolean isMerged = false;

    // Decision Table data
    protected DynamicData data;
    protected GuidedDecisionTable model;

    /**
     * Construct at empty Decision Table
     */
    public DecisionTableWidget() {
	mainPanel = getMainPanel();
	bodyPanel = getBodyPanel();
	gridWidget = getGridWidget();
	headerWidget = getHeaderWidget();
	sidebarWidget = getSidebarWidget();

	scrollPanel = new ScrollPanel();
	scrollPanel.add(gridWidget);
	scrollPanel.addScrollHandler(getScrollHandler());

	bodyPanel.add(headerWidget);
	bodyPanel.add(scrollPanel);
	mainPanel.add(sidebarWidget);
	mainPanel.add(bodyPanel);

	initWidget(mainPanel);
    }

    public List<DynamicEditColumn> getColumns() {
	return gridWidget.getColumns();
    }

    @Override
    public void setHeight(String height) {
	super.setHeight(height);
	mainPanel.setHeight(height);
	scrollPanel.setHeight(height);
	sidebarWidget.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
	super.setWidth(width);
	mainPanel.setWidth(width);
	scrollPanel.setWidth(width);
	headerWidget.setWidth(width);
    }

    // Selections store the actual grid data selected (irrespective of
    // merged cells). So a merged cell spanning 2 rows is stored as 2
    // selections. Selections are ordered by row number so we can
    // iterate top to bottom.
    private TreeSet<Coordinate> selections = new TreeSet<Coordinate>(
	    new Comparator<Coordinate>() {

		@Override
		public int compare(Coordinate o1, Coordinate o2) {
		    return o1.getRow() - o2.getRow();
		}

	    });

    /**
     * Select a single cell. If the cell is merged the selection is extended to
     * include all merged cells.
     * 
     * @param start
     *            The physical coordinate of the cell
     */
    public void startSelecting(Coordinate start) {
	clearSelection();
	CellValue<?> startCell = data.get(start);
	extendSelection(startCell.getCoordinate());
    }

    /**
     * Clear and selection.
     */
    public void clearSelection() {
	selections.clear();
    }

    @Override
    public void update(Object value) {
	for (Coordinate c : this.selections) {
	    data.set(c, value);
	}
	// TODO Partial redraw
	assertModelMerging();
	gridWidget.redraw();
    }

    /**
     * Set the Decision Table's data. This removes all existing columns from the
     * Decision Table and re-creates them based upon the provided data.
     * 
     * @param data
     */
    public void setData(GuidedDecisionTable model) {

	this.model = model;

	final int dataSize = model.getData().length;

	if (dataSize > 0) {

	    // Clear existing columns
	    gridWidget.removeAllColumns();

	    // Initialise CellTable's Metadata columns
	    int iCol = 0;
	    for (DTColumnConfig col : model.getMetadataCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			this.cellFactory.getCell(col, this), iCol);
		gridWidget.addColumn(column);
		iCol++;
	    }

	    // Initialise CellTable's Attribute columns
	    for (DTColumnConfig col : model.getAttributeCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			this.cellFactory.getCell(col, this), iCol);
		gridWidget.addColumn(column);
		iCol++;
	    }

	    // Initialise CellTable's Condition columns
	    for (DTColumnConfig col : model.getConditionCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			this.cellFactory.getCell(col, this), iCol);
		gridWidget.addColumn(column);
		iCol++;
	    }

	    // Initialise CellTable's Action columns
	    for (DTColumnConfig col : model.getActionCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			this.cellFactory.getCell(col, this), iCol);
		gridWidget.addColumn(column);
		iCol++;
	    }

	    // Setup data
	    this.data = new DynamicData();
	    for (int iRow = 0; iRow < dataSize; iRow++) {
		String[] row = model.getData()[iRow];
		ArrayList<CellValue<? extends Comparable<?>>> cellRow = new ArrayList<CellValue<? extends Comparable<?>>>();
		for (iCol = 0; iCol < row.length; iCol++) {
		    DTColumnConfig column = gridWidget.getColumns().get(iCol)
			    .getModelColumn();
		    CellValue<? extends Comparable<?>> cv = CellValueFactory
			    .getInstance().makeCellValue(column, iRow, iCol);
		    cv.setValue(row[iCol]);
		    cellRow.add(cv);
		}
		this.data.add(cellRow);
	    }
	}
	gridWidget.setRowData(data);
	gridWidget.redraw();

	// Draw the header
	headerWidget.redraw();

    }

    /**
     * Add a new column to the right of the table
     * 
     * @param title
     *            A title for the column
     */
    public void addColumn(DTColumnConfig modelColumn) {
	int index = 0;
	if (modelColumn instanceof MetadataCol) {
	    index = findMetadataColumnIndex();
	} else if (modelColumn instanceof AttributeCol) {
	    index = findAttributeColumnIndex();
	} else if (modelColumn instanceof ConditionCol) {
	    index = findConditionColumnIndex();
	} else if (modelColumn instanceof ActionCol) {
	    index = findActionColumnIndex();
	}
	insertColumnBefore(modelColumn, index);
    }

    // Find the right-most index for a Metadata column
    private int findMetadataColumnIndex() {
	int index = 0;
	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    DTColumnConfig column = gridWidget.getColumns().get(iCol)
		    .getModelColumn();
	    if (column instanceof MetadataCol) {
		index = iCol;
	    }
	}
	return index + 1;
    }

    // Find the right-most index for a Attribute column
    private int findAttributeColumnIndex() {
	int index = 0;
	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    DTColumnConfig column = gridWidget.getColumns().get(iCol)
		    .getModelColumn();
	    if (column instanceof MetadataCol) {
		index = iCol;
	    } else if (column instanceof AttributeCol) {
		index = iCol;
	    }
	}
	return index + 1;
    }

    // Find the right-most index for a Condition column
    private int findConditionColumnIndex() {
	int index = 0;
	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    DTColumnConfig column = gridWidget.getColumns().get(iCol)
		    .getModelColumn();
	    if (column instanceof MetadataCol) {
		index = iCol;
	    } else if (column instanceof AttributeCol) {
		index = iCol;
	    } else if (column instanceof ConditionCol) {
		index = iCol;
	    }
	}
	return index + 1;
    }

    // Find the right-most index for an Action column
    private int findActionColumnIndex() {
	int index = gridWidget.getColumns().size();
	return index;
    }

    /**
     * Insert a new column at the specified index.
     * 
     * @param title
     *            A title for the column
     * 
     * @param index
     *            The (zero-based) index of the column
     */
    public void insertColumnBefore(DTColumnConfig modelColumn, int index) {

	for (int iRow = 0; iRow < data.size(); iRow++) {
	    CellValue<?> cell = CellValueFactory.getInstance().makeCellValue(
		    modelColumn, iRow, index);
	    data.get(iRow).add(index, cell);
	}
	assertColumnCoordinates(index);

	DynamicEditColumn column = new DynamicEditColumn(modelColumn,
		cellFactory.getCell(modelColumn, this), index);

	// columns.add(index, column);
	gridWidget.addColumn(index, column);

	// Re-index columns
	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    DynamicEditColumn col = gridWidget.getColumns().get(iCol);
	    col.setColumnIndex(iCol);
	}

	// TODO Partial redraw
	gridWidget.setRowData(data);
	gridWidget.redraw();

	assertDimensions();
    }

    /**
     * Add a new row to the bottom of the table
     */
    public void addRow() {
	insertRowBefore(data.size());
    }

    /**
     * Delete a row at the specified index.
     * 
     * @param index
     */
    public void deleteRow(int index) {
	data.remove(index);
	assertRowCoordinates(index);

	// TODO Partial redraw
	if (!isMerged) {
	    assertModelMerging();
	    gridWidget.deleteRow(index);
	} else {
	    assertModelMerging();
	    gridWidget.redraw();
	}
	assertDimensions();
    }

    /**
     * Insert a new row at the specified index.
     * 
     * @param index
     *            The (zero-based) index of the row
     */
    public void insertRowBefore(int index) {
	if (index < 0) {
	    throw new IllegalArgumentException(
		    "Row number cannot be less than zero.");
	}
	if (index > data.size()) {
	    throw new IllegalArgumentException(
		    "Row number cannot be greater than the number of declared rows.");
	}

	// Find rows that need to be (re)drawn
	int minRedrawRow = index;
	int maxRedrawRow = index;
	if (index < data.size()) {
	    minRedrawRow = findMinRedrawRow(index);
	    maxRedrawRow = findMaxRedrawRow(index)+1;
	}

	List<CellValue<? extends Comparable<?>>> row = new ArrayList<CellValue<? extends Comparable<?>>>();
	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    DTColumnConfig column = gridWidget.getColumns().get(iCol)
		    .getModelColumn();
	    CellValue<? extends Comparable<?>> data = CellValueFactory
		    .getInstance().makeCellValue(column, index, iCol);
	    row.add(data);
	}
	data.add(index, row);
	assertRowCoordinates(index);

	// TODO Partial redraw needs to consider merged cells
	if (!isMerged) {
	    gridWidget.insertRowBefore(index, row);
	} else {
	    assertModelMerging(minRedrawRow, maxRedrawRow);
	    gridWidget.insertRowBefore(index, row);
	    gridWidget.redrawRows(minRedrawRow, maxRedrawRow);
	}
	assertDimensions();
    }

    /**
     * Toggle the state of Decision Table merging.
     * 
     * @return The state of merging after completing this call
     */
    public boolean toggleMerging() {
	if (!isMerged) {
	    isMerged = true;
	    assertModelMerging();
	    gridWidget.redraw();
	} else {
	    isMerged = false;
	    removeModelMerging();
	    gridWidget.redraw();
	}
	return isMerged;
    }

    // Ensure Coordinates are the extents of merged cell
    private void extendSelection(Coordinate coordinate) {
	CellValue<?> startCell = data.get(coordinate);
	CellValue<?> endCell = startCell;
	while (startCell.getRowSpan() == 0) {
	    startCell = data.get(startCell.getCoordinate().getRow() - 1).get(
		    startCell.getCoordinate().getCol());
	}

	if (startCell.getRowSpan() > 1) {
	    endCell = data
		    .get(endCell.getCoordinate().getRow()
			    + endCell.getRowSpan() - 1).get(
			    endCell.getCoordinate().getCol());
	}
	selectRange(startCell, endCell);
    }

    // Select a range of cells between the two coordinates.
    private void selectRange(CellValue<?> startCell, CellValue<?> endCell) {
	int col = startCell.getCoordinate().getCol();
	for (int iRow = startCell.getCoordinate().getRow(); iRow <= endCell
		.getCoordinate().getRow(); iRow++) {
	    CellValue<?> cell = data.get(iRow).get(col);
	    selections.add(cell.getCoordinate());
	}
    }

    // The DecisionTableHeaderWidget and DecisionTableSidebarWidget need to be
    // resized when MergableGridWidget has scrollbars
    private void assertDimensions() {
	headerWidget.setWidth(scrollPanel.getElement().getClientWidth() + "px");
	headerWidget.redraw();
	sidebarWidget.setHeight(scrollPanel.getElement().getClientHeight()
		+ "px");
    }

    // Ensure cells in rows have correct physical coordinates
    private void assertRowCoordinates(int index) {
	for (int iRow = index; iRow < data.size(); iRow++) {
	    List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
	    for (int iCol = 0; iCol < row.size(); iCol++) {
		CellValue<? extends Comparable<?>> cell = data.get(iRow).get(
			iCol);
		cell.setCoordinate(new Coordinate(iRow, iCol));
	    }
	}
    }

    // Ensure cells in columns have correct physical coordinates
    private void assertColumnCoordinates(int index) {
	for (int iRow = 0; iRow < data.size(); iRow++) {
	    List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
	    for (int iCol = index; iCol < row.size(); iCol++) {
		CellValue<? extends Comparable<?>> cell = data.get(iRow).get(
			iCol);
		cell.setCoordinate(new Coordinate(iRow, iCol));
	    }
	}
    }

    // Here lays a can of worms! Each cell in the Decision Table
    // has three coordinates: (1) The physical coordinate, (2) The
    // coordinate relating to the HTML table element and (3) The
    // coordinate mapping a HTML table element back to the physical
    // coordinate. For example a cell could have the (1) physical
    // coordinate (0,0) which equates to (2) HTML element (0,1) in
    // which case the cell at physical coordinate (0,1) would
    // have a (3) mapping back to (0,0).
    private void assertModelIndexes() {

	for (int iRow = 0; iRow < data.size(); iRow++) {
	    List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
	    int colCount = 0;
	    for (int iCol = 0; iCol < row.size(); iCol++) {

		CellValue<? extends Comparable<?>> indexCell = row.get(iCol);

		int newRow = 0;
		int newCol = 0;
		if (indexCell.getRowSpan() != 0) {
		    newRow = iRow;
		    newCol = colCount++;

		    CellValue<? extends Comparable<?>> cell = data.get(newRow)
			    .get(newCol);
		    cell.setPhysicalCoordinate(new Coordinate(iRow, iCol));

		} else {
		    List<CellValue<? extends Comparable<?>>> priorRow = data
			    .get(iRow - 1);
		    CellValue<? extends Comparable<?>> priorCell = priorRow
			    .get(iCol);
		    Coordinate priorHtmlCoordinate = priorCell
			    .getHtmlCoordinate();
		    newRow = priorHtmlCoordinate.getRow();
		    newCol = priorHtmlCoordinate.getCol();
		}
		indexCell.setHtmlCoordinate(new Coordinate(newRow, newCol));

	    }
	}
    }

    // Ensure merging is reflected in the model
    @Deprecated
    private void assertModelMerging() {
	assertModelMerging(0, data.size() - 1);
    }

    // Ensure merging is reflected in the model
    private void assertModelMerging(int minRowIndex, int maxRowIndex) {

	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    for (int iRow = minRowIndex; iRow < maxRowIndex; iRow++) {

		int rowSpan = 1;
		CellValue<?> cell1 = data.get(iRow).get(iCol);
		CellValue<?> cell2 = data.get(iRow + rowSpan).get(iCol);

		// Don't merge empty cells
		if (isMerged && !cell1.isEmpty()) {
		    while (cell1.getValue().equals(cell2.getValue())
			    && iRow + rowSpan < maxRowIndex) {
			cell2.setRowSpan(0);
			rowSpan++;
			cell2 = data.get(iRow + rowSpan).get(iCol);
		    }
		    if (cell1.getValue().equals(cell2.getValue())) {
			cell2.setRowSpan(0);
			rowSpan++;
		    }
		}
		cell1.setRowSpan(rowSpan);
		iRow = iRow + rowSpan - 1;
	    }
	}

	// Set indexes after merging has been corrected
	// TODO Could this be incorporated into here?
	assertModelIndexes();

    }

    // Remove merging from model
    private void removeModelMerging() {

	for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
	    for (int iRow = 0; iRow < data.size(); iRow++) {
		CellValue<?> cell = data.get(iRow).get(iCol);
		Coordinate c = new Coordinate(iRow, iCol);
		cell.setCoordinate(c);
		cell.setHtmlCoordinate(c);
		cell.setPhysicalCoordinate(c);
		cell.setRowSpan(1);
	    }
	}

	// Set indexes after merging has been corrected
	// TODO Could this be incorporated into here?
	assertModelIndexes();
    }

    private int findMinRedrawRow(int baseRowIndex) {
	int minRedrawRow = baseRowIndex;
	List<CellValue<? extends Comparable<?>>> baseRow = data
		.get(baseRowIndex);
	for (int iCol = 0; iCol < baseRow.size(); iCol++) {
	    int iRow = baseRowIndex;
	    CellValue<? extends Comparable<?>> cell = baseRow.get(iCol);
	    while (cell.getRowSpan() == 0 && iRow >= 0) {
		iRow--;
		List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
		cell = row.get(iCol);
	    }
	    minRedrawRow = (iRow < minRedrawRow ? iRow : minRedrawRow);
	}
	return minRedrawRow;
    }

    private int findMaxRedrawRow(int baseRowIndex) {
	int maxRedrawRow = baseRowIndex;
	List<CellValue<? extends Comparable<?>>> baseRow = data
		.get(baseRowIndex);
	for (int iCol = 0; iCol < baseRow.size(); iCol++) {
	    int iRow = baseRowIndex;
	    CellValue<? extends Comparable<?>> cell = baseRow.get(iCol);
	    while (cell.getRowSpan() == 0 && iRow < data.size() - 1) {
		iRow++;
		List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
		cell = row.get(iCol);
	    }
	    maxRedrawRow = (iRow > maxRedrawRow ? iRow : maxRedrawRow);
	}
	return maxRedrawRow;
    }

    public void sort() {
	final DynamicEditColumn[] sortOrderList = new DynamicEditColumn[gridWidget
		.getColumns().size()];
	int index = 0;
	for (DynamicEditColumn column : gridWidget.getColumns()) {
	    int sortIndex = column.getSortIndex();
	    if (sortIndex != -1) {
		sortOrderList[sortIndex] = column;
		index++;
	    }
	}
	final int sortedColumnCount = index;

	Collections.sort(data,
		new Comparator<List<CellValue<? extends Comparable<?>>>>() {

		    @SuppressWarnings({ "rawtypes", "unchecked" })
		    public int compare(
			    List<CellValue<? extends Comparable<?>>> leftRow,
			    List<CellValue<? extends Comparable<?>>> rightRow) {
			int comparison = 0;
			for (int index = 0; index < sortedColumnCount; index++) {
			    DynamicEditColumn sortableHeader = sortOrderList[index];
			    Comparable leftColumnValue = leftRow
				    .get(sortableHeader.getColumnIndex());
			    Comparable rightColumnValue = rightRow
				    .get(sortableHeader.getColumnIndex());
			    comparison = (leftColumnValue == rightColumnValue) ? 0
				    : (leftColumnValue == null) ? -1
					    : (rightColumnValue == null) ? 1
						    : leftColumnValue
							    .compareTo(rightColumnValue);
			    if (comparison != 0) {
				switch (sortableHeader.getSortDirection()) {
				case ASCENDING:
				    break;
				case DESCENDING:
				    comparison = -comparison;
				    break;
				default:
				    throw new IllegalStateException(
					    "Sorting can only be enabled for ASCENDING or"
						    + " DESCENDING, not sortDirection ("
						    + sortableHeader
							    .getSortDirection()
						    + ") .");
				}
				return comparison;
			    }
			}
			return comparison;
		    }
		});

	removeModelMerging();
	assertModelMerging();
	gridWidget.setRowData(data);
	gridWidget.redraw();

    }

    /**
     * Gets the Widget's outer most panel to which other content will be added.
     * This allows subclasses to have some control over the general layout of
     * the Decision Table.
     * 
     * @return
     */
    protected abstract Panel getMainPanel();

    /**
     * Gets the Widgets inner panel to which the DecisionTable and Header will
     * be added. This allows subclasses to have some control over the internal
     * layout of the Decision Table.
     * 
     * @return
     */
    protected abstract Panel getBodyPanel();

    /**
     * Gets the Widget responsible for rendering the DecisionTables "grid".
     * 
     * @return
     */
    protected abstract MergableGridWidget getGridWidget();

    /**
     * Gets the Widget responsible for rendering the DecisionTables "header".
     * 
     * @return
     */
    protected abstract DecisionTableHeaderWidget getHeaderWidget();

    /**
     * Gets the Widget responsible for rendering the DecisionTables "side-bar".
     * 
     * @return
     */
    protected abstract DecisionTableSidebarWidget getSidebarWidget();

    /**
     * The DecisionTable is nested inside a ScrollPanel. This allows
     * ScrollEvents to be hooked up to other defendant controls (e.g. the
     * Header).
     * 
     * @return
     */
    protected abstract ScrollHandler getScrollHandler();

    // ************** DEBUG

    @SuppressWarnings("unused")
    private void dumpIndexes() {
	System.out.println("coordinates");
	System.out.println("-----------");
	for (int iRow = 0; iRow < data.size(); iRow++) {
	    List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
	    for (int iCol = 0; iCol < row.size(); iCol++) {
		CellValue<? extends Comparable<?>> cell = row.get(iCol);

		Coordinate c = cell.getCoordinate();
		int rowSpan = cell.getRowSpan();

		System.out.print(c.toString());
		System.out.print("-S" + rowSpan + " ");
	    }
	    System.out.print("\n");
	}

	System.out.println();
	System.out.println("htmlToDataMap");
	System.out.println("-------------");
	for (int iRow = 0; iRow < data.size(); iRow++) {
	    List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
	    for (int iCol = 0; iCol < row.size(); iCol++) {
		CellValue<? extends Comparable<?>> cell = row.get(iCol);

		Coordinate c = cell.getPhysicalCoordinate();
		int rowSpan = cell.getRowSpan();

		System.out.print(c.toString());
		System.out.print("-S" + rowSpan + " ");
	    }
	    System.out.print("\n");
	}

	System.out.println();
	System.out.println("dataToHtmlMap");
	System.out.println("-------------");
	for (int iRow = 0; iRow < data.size(); iRow++) {
	    List<CellValue<? extends Comparable<?>>> row = data.get(iRow);
	    for (int iCol = 0; iCol < row.size(); iCol++) {
		CellValue<? extends Comparable<?>> cell = row.get(iCol);

		Coordinate c = cell.getHtmlCoordinate();
		int rowSpan = cell.getRowSpan();

		System.out.print(c.toString());
		System.out.print("-S" + rowSpan + " ");
	    }
	    System.out.print("\n");
	}

    }

    @SuppressWarnings("unused")
    private void dumpSelections(String title) {
	System.out.println(title);
	System.out.println();
	for (Coordinate c : selections) {
	    CellValue<?> cell = data.get(c.getRow()).get(c.getCol());
	    System.out.println(cell.getCoordinate().toString());
	}
	System.out.println();
    }

}
