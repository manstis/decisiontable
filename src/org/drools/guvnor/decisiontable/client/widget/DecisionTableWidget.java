package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Abstract Decision Table encapsulating basic operation.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableWidget extends Composite {

    private Panel panel;
    protected CellTable<List<CellValue>> table = new CellTable<List<CellValue>>();

    // Decision Table data
    protected DynamicData data;
    protected GuidedDecisionTable model;

    // This handles interaction between the user and constraints on how the
    // table should be rendered
    protected SelectionManager manager;

    // CellTable does not expose "getColumns" so we keep track ourselves. Could
    // sub-class CellTable....
    protected List<DynamicEditColumn> columns = new ArrayList<DynamicEditColumn>();

    /**
     * Construct at empty Decision Table
     */
    public DecisionTableWidget() {
	panel = new ScrollPanel();
	panel.add(table);
	table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
	table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
	initWidget(panel);
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
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		table.removeColumn(iCol);
	    }
	    columns.clear();
	    Header<String> header = getHeader();

	    // Initialise CellTable's Metadata columns
	    int iCol = 0;
	    for (DTColumnConfig col : model.getMetadataCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			new EditableCell(this.manager), iCol);
		table.addColumn(column, header);
		columns.add(iCol, column);
		iCol++;
	    }

	    // Initialise CellTable's Attribute columns
	    for (DTColumnConfig col : model.getAttributeCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			new EditableCell(this.manager), iCol);
		table.addColumn(column, header);
		columns.add(iCol, column);
		iCol++;
	    }

	    // Initialise CellTable's Condition columns
	    for (DTColumnConfig col : model.getConditionCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			new EditableCell(this.manager), iCol);
		table.addColumn(column, header);
		columns.add(iCol, column);
		iCol++;
	    }

	    // Initialise CellTable's Action columns
	    for (DTColumnConfig col : model.getActionCols()) {
		DynamicEditColumn column = new DynamicEditColumn(col,
			new EditableCell(this.manager), iCol);
		table.addColumn(column, header);
		columns.add(iCol, column);
		iCol++;
	    }

	    // Setup data
	    this.data = new DynamicData();
	    for (int iRow = 0; iRow < dataSize; iRow++) {
		String[] row = model.getData()[iRow];
		ArrayList<CellValue> cellRow = new ArrayList<CellValue>();
		for (iCol = 0; iCol < row.length; iCol++) {
		    cellRow.add(new CellValue(row[iCol], iRow, iCol));
		}
		this.data.add(cellRow);
	    }
	}
	table.setPageSize(dataSize);
	table.setRowCount(dataSize);
	table.setRowData(0, data);

	// Calls to CellTable.setRowData() causes the rows to revert to their
	// default heights
	manager.assertRowHeights();
    }

    @Override
    public void setHeight(String height) {
	super.setHeight(height);
	panel.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
	super.setWidth(width);
	panel.setWidth(width);
    }

    protected void clearSelection() {
	manager.clearSelection();
    }

    protected void addRow() {
	manager.addRow();
    }

    protected void insertRowBefore(int index) {
	manager.insertRowBefore(index);
    }

    protected void addColumn(DTColumnConfig modelColumn) {
	manager.addColumn(modelColumn);
    }

    protected void insertColumnBefore(DTColumnConfig modelColumn, int index) {
	manager.insertColumnBefore(modelColumn, index);
    }

    protected void toggleMerging() {
	manager.toggleMerging();
    }

    protected abstract Header<String> getHeader();

}
