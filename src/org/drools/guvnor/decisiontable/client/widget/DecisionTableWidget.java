package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;


import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
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
    
    //Decision Table data
    protected DynamicData data;

    //This handles interaction between the user and costraints on how the table should be rendered
    protected SelectionManager manager;
    
    //CellTable does not expose "getColumns" so we keep track ourselves. Could sub-class CellTable....
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
     * Set the Decision Table's data. This removes all existing columns
     * from the Decision Table and re-creates them based upon the provided 
     * data.
     * 
     * @param data
     */
    public void setData(DynamicData data) {
	this.data = data;

	final int dataSize = data.size();

	// Initialise CellTable's columns
	if (dataSize > 0) {
	    List<CellValue> row = data.get(0);
	    for (int iCol = 0; iCol < columns.size(); iCol++) {
		table.removeColumn(iCol);
	    }
	    columns.clear();
	    for (int iCol = 0; iCol < row.size(); iCol++) {
		DynamicEditColumn column = new DynamicEditColumn(
			new EditableCell(this.manager), iCol);
		table.addColumn(column, "new");
		columns.add(iCol, column);
	    }
	}
	table.setPageSize(dataSize);
	table.setRowCount(dataSize);
	table.setRowData(0, data);
	
	//Calls to CellTable.setRowData() causes the rows to revert to their default heights
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

    protected void addColumn() {
	//TODO Columns can have "type" (meta, action, condition)
	manager.addColumn("new");
    }

    protected void insertColumnBefore(int index) {
	//TODO Columns can have "type" (meta, action, condition)
	manager.insertColumnBefore("new", index);
    }

    protected void toggleMerging() {
	manager.toggleMerging();
    }

}
