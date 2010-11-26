package org.drools.guvnor.decisiontable.client;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.widget.CellValue;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableControlsWidget;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget;
import org.drools.guvnor.decisiontable.client.widget.DynamicData;
import org.drools.guvnor.decisiontable.client.widget.VerticalDecisionTableWidget;
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
	MetadataCol metaCol = new MetadataCol();
	metaCol.attr = "salience";
	metadataCols.add(metaCol);

	String[][] guidedData = { { "1" }, { "1" } };
	guidedModel.setData(guidedData);
	guidedModel.setMetadataCols(metadataCols);
	return guidedModel;
    }
}
