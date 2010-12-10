package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.widget.cells.AbstractCellFactory;
import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollHandler;
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

    protected Panel mainPanel;
    protected Panel bodyPanel;
    protected ScrollPanel scrollPanel;
    protected DecisionTableHeaderWidget headerWidget;
    protected DecisionTableSidebarWidget sidebarWidget;
    protected AbstractCellFactory cellFactory;

    protected CellTableResource resource = GWT.create(CellTableResource.class);
    protected CellTable<List<CellValue<?>>> table = new CellTable<List<CellValue<?>>>(
	    0, resource);
    
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
	mainPanel = getMainPanel();
	bodyPanel = getBodyPanel();
	headerWidget = getHeaderWidget();
	sidebarWidget = getSidebarWidget();

	scrollPanel = new ScrollPanel();
	scrollPanel.add(table);
	scrollPanel.addScrollHandler(getScrollHandler());

	table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
	table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

	bodyPanel.add(headerWidget);
	bodyPanel.add(scrollPanel);
	mainPanel.add(sidebarWidget);
	mainPanel.add(bodyPanel);

	initWidget(mainPanel);
    }

    public List<DynamicEditColumn> getColumns() {
	return this.columns;
    }

    /**
     * Set the Decision Table's data. This removes all existing columns from the
     * Decision Table and re-creates them based upon the provided data.
     * 
     * @param data
     */
    public abstract void setData(GuidedDecisionTable model);

    @Override
    public void setHeight(String height) {
	super.setHeight(height);
	mainPanel.setHeight(height);
	scrollPanel.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
	super.setWidth(width);
	mainPanel.setWidth(width);
	scrollPanel.setWidth(width);
	headerWidget.setWidth(width);
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

}
