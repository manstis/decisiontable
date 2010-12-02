package org.drools.guvnor.decisiontable.client;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.widget.DecisionTableControlsWidget;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget;
import org.drools.guvnor.decisiontable.client.widget.VerticalDecisionTableWidget;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DecisionTable implements EntryPoint {

    public void onModuleLoad() {

	final DecisionTableWidget dtable = new VerticalDecisionTableWidget();

	// Guvnor's data model for Decision Tables
	GuidedDecisionTable guidedModel = getModel();

	// Pass data to Widget
	dtable.setData(guidedModel);
	dtable.setHeight("300px");
	dtable.setWidth("800px");

	RootPanel.get("table").add(dtable);
	RootPanel.get("buttons").add(new DecisionTableControlsWidget(dtable));

    }

    // Construct a model as used by Guvnor
    private GuidedDecisionTable getModel() {
	GuidedDecisionTable guidedModel = new GuidedDecisionTable();
	List<MetadataCol> metadataCols = new ArrayList<MetadataCol>();
	List<AttributeCol> attributeCols = new ArrayList<AttributeCol>();
	List<ConditionCol> conditionCols = new ArrayList<ConditionCol>();
	List<ActionCol> actionCols = new ArrayList<ActionCol>();
	MetadataCol metaCol = new MetadataCol();
	metaCol.attr = "metadata";
	metadataCols.add(metaCol);

	AttributeCol attributeCol = new AttributeCol();
	attributeCol.attr = "attribute";
	attributeCols.add(attributeCol);

	ConditionCol conditionCol = new ConditionCol();
	conditionCol.setFactType("FactType");
	conditionCol.setFactField("FactField");
	conditionCols.add(conditionCol);

	ActionCol actionCol = new ActionCol();
	actionCol.setHeader("Action");
	actionCols.add(actionCol);

	String[][] guidedData = { { "1","2","3","4" }, { "","","","" } };
	guidedModel.setData(guidedData);
	guidedModel.setMetadataCols(metadataCols);
	guidedModel.setAttributeCols(attributeCols);
	guidedModel.setConditionCols(conditionCols);
	guidedModel.setActionCols(actionCols);
	return guidedModel;
    }
}
