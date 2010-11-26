package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Header;

/**
 * Vertical implementation of a Decision Table where rules are represented as
 * rows and the definition (meta, conditions and actions are represented as
 * columns.
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableWidget extends DecisionTableWidget {

    private static Header<String> header=null;

    public VerticalDecisionTableWidget() {
	this.manager = new VerticalSelectionManager();
    }

    @Override
    protected Header<String> getHeader() {
	if (header == null) {
	    header = new Header<String>(new HeaderCell()) {

		@Override
		public String getValue() {
		    return "hello";
		}
		
	    };
	    }
	return header;
    }

    /**
     * This is where the "action" takes place! Some of this is probably common
     * to a Horizontal Decision Table too, however until I start to write one
     * it's left in here!
     * 
     * @author manstis
     * 
     */
    private class VerticalSelectionManager implements SelectionManager {

	private boolean isMerged = false;

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
	 * Select a single cell. If the cell is merged the selection is extended
	 * to include all merged cells.
	 * 
	 * @param start
	 *            The physical coordinate of the cell
	 */
	@Override
	public void startSelecting(Coordinate start) {
	    clearSelection();
	    CellValue startCell = data.get(start);
	    extendSelection(startCell.getCoordinate());
	}

	/**
	 * Return a cell based upon a coordinate. Rows and Columns are
	 * zero-based and conceptually extend downwards and rightwards.
	 * 
	 * 
	 * @param c
	 *            The physical coordinate of the cell
	 */
	@Override
	public CellValue getPhysicalCell(Coordinate c) {
	    return data.get(c);
	}

	/**
	 * Clear and selection.
	 */
	@Override
	public void clearSelection() {
	    selections.clear();
	}

	/**
	 * Update all selected cells with the applicable value. When a value of
	 * a cell is changed it can force additional merging of cells.
	 * 
	 * @param value
	 *            The value with which to update selected cells
	 */
	@Override
	public void setSelectionValue(Object value) {
	    for (Coordinate c : this.selections) {
		CellValue cv = data.get(c);
		cv.setValue(value);
	    }

	    // A new value could cause other cells to become merged
	    assertMerging();
	    assertIndexes();
	}

	/**
	 * Add a new column to the right of the table
	 * 
	 * @param title
	 *            A title for the column
	 */
	@Override
	public void addColumn(String title) {
	    insertColumnBefore(title, columns.size());
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
	@Override
	public void insertColumnBefore(String title, int index) {

	    for (int iRow = 0; iRow < data.size(); iRow++) {
		CellValue cell = new CellValue("(R" + iRow + ",C"
			+ columns.size() + ")", iRow, index);
		data.get(iRow).add(index, cell);
	    }
	    assertColumnCoordinates(index);
	    // CellTable doesn't support inserting arbitrary columns so we
	    // need to delete and reinsert all columns. We also need to
	    // re-index the model column from which the cell will retrieve
	    // it's value
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		table.removeColumn(0);
	    }
	    DynamicEditColumn column = new DynamicEditColumn(new EditableCell(
		    this), index);
	    columns.add(index, column);
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		DynamicEditColumn col = columns.get(iCol);
		col.setColumnIndex(iCol);
		table.addColumn(col, getHeader());
	    }

	    // Changing the data causes a redraw so we need to re-apply our
	    // visual trickery
	    ((HeaderCell) header.getCell()).setColumnCount(columns.size());
	    table.setRowCount(data.size());
	    table.setRowData(0, data);
	    assertMerging();
	    assertIndexes();
	    assertRowHeights();
	}

	/**
	 * Add a new row to the bottom of the table
	 */
	@Override
	public void addRow() {
	    insertRowBefore(data.size());
	}

	/**
	 * Insert a new row at the specified index.
	 * 
	 * @param index
	 *            The (zero-based) index of the row
	 */
	@Override
	public void insertRowBefore(int index) {
	    if (index < 0) {
		throw new IllegalArgumentException(
			"Row number cannot be less than zero.");
	    }
	    if (index > data.size()) {
		throw new IllegalArgumentException(
			"Row number cannot be greater than the number of declared rows.");
	    }

	    List<CellValue> row = new ArrayList<CellValue>();
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		CellValue data = new CellValue(
			"(R" + index + ",C" + iCol + ")", index, iCol);
		row.add(data);
	    }
	    data.add(index, row);
	    assertRowCoordinates(index);

	    // Changing the data causes a redraw so we need to re-apply our
	    // visual trickery
	    table.setRowCount(data.size());
	    table.setPageSize(data.size());
	    table.setRowData(0, data);
	    assertMerging();
	    assertIndexes();
	    assertRowHeights();
	}

	/**
	 * This ensures the height of rows remains correct following merge
	 * operations. Thinking about it this probably doesn't make sense for a
	 * Vertical Decision Table but I'll refactor it when I get to write that
	 * one!
	 */
	@Override
	public void assertRowHeights() {
	    final int MAX_ROW = data.size();
	    for (int iRow = 0; iRow < MAX_ROW; iRow++) {
		List<CellValue> row = data.get(iRow);
		for (int iCol = 0; iCol < row.size(); iCol++) {
		    CellValue cell = data.get(iRow).get(iCol);
		    if (cell.getRowSpan() != 0) {
			table.getRowElement(cell.getHtmlCoordinate().getRow())
				.getCells()
				.getItem(cell.getHtmlCoordinate().getCol())
				.getStyle()
				.setHeight(cell.getRowSpan() * 48, Unit.PX);
		    }
		}
	    }
	}

	/**
	 * Toggle the state of Decision Table merging.
	 * 
	 * @return The state of merging after completing this call
	 */
	@Override
	public boolean toggleMerging() {
	    if (!isMerged) {
		applyMerging();
	    } else {
		removeMerging();
	    }
	    return isMerged;
	}

	// Ensure Coordinates are the extents of merged cell
	private void extendSelection(Coordinate coordinate) {
	    CellValue startCell = data.get(coordinate);
	    CellValue endCell = startCell;
	    while (startCell.getRowSpan() == 0) {
		startCell = data.get(startCell.getCoordinate().getRow() - 1)
			.get(startCell.getCoordinate().getCol());
	    }

	    if (startCell.getRowSpan() > 1) {
		endCell = data.get(
			endCell.getCoordinate().getRow() + endCell.getRowSpan()
				- 1).get(endCell.getCoordinate().getCol());
	    }
	    selectRange(startCell, endCell);
	}

	// Select a range of cells between the two coordinates.
	private void selectRange(CellValue startCell, CellValue endCell) {
	    int col = startCell.getCoordinate().getCol();
	    for (int iRow = startCell.getCoordinate().getRow(); iRow <= endCell
		    .getCoordinate().getRow(); iRow++) {
		CellValue cell = data.get(iRow).get(col);
		selections.add(cell.getCoordinate());
	    }
	}

	// Ensure cells in rows have correct physical coordinates
	private void assertRowCoordinates(int index) {
	    for (int iRow = index; iRow < data.size(); iRow++) {
		List<CellValue> row = data.get(iRow);
		for (int iCol = 0; iCol < row.size(); iCol++) {
		    CellValue cell = data.get(iRow).get(iCol);
		    cell.setCoordinate(new Coordinate(iRow, iCol));
		}
	    }
	}

	// Ensure cells in columns have correct physical coordinates
	private void assertColumnCoordinates(int index) {
	    for (int iRow = 0; iRow < data.size(); iRow++) {
		List<CellValue> row = data.get(iRow);
		for (int iCol = index; iCol < row.size(); iCol++) {
		    CellValue cell = data.get(iRow).get(iCol);
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
	private void assertIndexes() {

	    for (int iRow = 0; iRow < data.size(); iRow++) {
		List<CellValue> row = data.get(iRow);
		int colCount = 0;
		for (int iCol = 0; iCol < row.size(); iCol++) {
		    CellValue indexCell = row.get(iCol);

		    int newRow = 0;
		    int newCol = 0;
		    if (indexCell.getRowSpan() != 0) {
			newRow = iRow;
			newCol = colCount++;

			CellValue cell = data.get(newRow).get(newCol);
			cell.setPhysicalCoordinate(new Coordinate(iRow, iCol));

		    } else {
			List<CellValue> priorRow = data.get(iRow - 1);
			CellValue priorCell = priorRow.get(iCol);
			Coordinate priorHtmlCoordinate = priorCell
				.getHtmlCoordinate();
			newRow = priorHtmlCoordinate.getRow();
			newCol = priorHtmlCoordinate.getCol();
		    }
		    indexCell.setHtmlCoordinate(new Coordinate(newRow, newCol));

		}
	    }
	}

	// Ensure the table's visuals are correct for the merge state
	private void assertMerging() {
	    if (isMerged) {
		removeMerging();
		applyMerging();
	    }
	}

	// Apply merging
	private void applyMerging() {
	    final int MAX_ROW = data.size() - 1;

	    for (int iCol = columns.size() - 1; iCol >= 0; iCol--) {
		for (int iRow = 0; iRow < MAX_ROW; iRow++) {
		    int rowSpan = 1;
		    CellValue cell1 = data.get(iRow).get(iCol);
		    CellValue cell2 = data.get(iRow + rowSpan).get(iCol);

		    // Don't merge empty cells
		    if (!cell1.isEmpty()) {
			while (cell1.getValue().equals(cell2.getValue())
				&& iRow + rowSpan < MAX_ROW) {
			    if (cell2.getRowSpan() != 0) {
				table.getRowElement(iRow + rowSpan).deleteCell(
					iCol);
				cell2.setRowSpan(0);
			    }

			    rowSpan++;
			    cell2 = data.get(iRow + rowSpan).get(iCol);
			}
			if (cell1.getValue().equals(cell2.getValue())) {
			    if (cell2.getRowSpan() != 0) {
				table.getRowElement(iRow + rowSpan).deleteCell(
					iCol);
				cell2.setRowSpan(0);
			    }
			    rowSpan++;
			}
		    }
		    cell1.setRowSpan(rowSpan);
		    if (rowSpan > 1) {
			table.getRowElement(cell1.getHtmlCoordinate().getRow())
				.getCells()
				.getItem(cell1.getHtmlCoordinate().getCol())
				.setRowSpan(rowSpan);
		    }
		    iRow = iRow + rowSpan - 1;
		}
	    }
	    assertIndexes();
	    assertRowHeights();
	    isMerged = true;
	}

	// Remove merging
	private void removeMerging() {
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		for (int iRow = 0; iRow < data.size(); iRow++) {
		    CellValue cell = data.get(iRow).get(iCol);
		    cell.setRowSpan(1);
		}
	    }
	    table.redraw();
	    assertIndexes();
	    assertRowHeights();
	    isMerged = false;
	}

	// ************** DEBUG

	public void dumpIndexes() {
	    System.out.println("coordinates");
	    System.out.println("-----------");
	    for (int iRow = 0; iRow < data.size(); iRow++) {
		List<CellValue> row = data.get(iRow);
		for (int iCol = 0; iCol < row.size(); iCol++) {
		    CellValue cell = row.get(iCol);

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
		List<CellValue> row = data.get(iRow);
		for (int iCol = 0; iCol < row.size(); iCol++) {
		    CellValue cell = row.get(iCol);

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
		List<CellValue> row = data.get(iRow);
		for (int iCol = 0; iCol < row.size(); iCol++) {
		    CellValue cell = row.get(iCol);

		    Coordinate c = cell.getHtmlCoordinate();
		    int rowSpan = cell.getRowSpan();

		    System.out.print(c.toString());
		    System.out.print("-S" + rowSpan + " ");
		}
		System.out.print("\n");
	    }

	}

	public void dumpSelections(String title) {
	    System.out.println(title);
	    System.out.println();
	    for (Coordinate c : selections) {
		CellValue cell = data.get(c.getRow()).get(c.getCol());
		System.out.println(cell.getCoordinate().toString());
	    }
	    System.out.println();
	}

    }

}