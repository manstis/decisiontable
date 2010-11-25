package org.drools.guvnor.decisiontable.client;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.widget.CellValue;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableControlsWidget;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget;
import org.drools.guvnor.decisiontable.client.widget.DynamicData;
import org.drools.guvnor.decisiontable.client.widget.VerticalDecisionTableWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DecisionTable implements EntryPoint {

    public void onModuleLoad() {

	final DecisionTableWidget dtable = new VerticalDecisionTableWidget();

	// TODO This should be hidden within the DecisionTable. CellValue wraps
	// an "actual" value adding numerous additional properties to support
	// merging in the Decision Table. When completed DynamicData and
	// CellValue's accessors can be changed to package-private
	DynamicData data = new DynamicData();
	List<CellValue> row1 = new ArrayList<CellValue>();
	row1.add(new CellValue("R1", 0, 0));
	data.add(row1);
	List<CellValue> row2 = new ArrayList<CellValue>();
	row2.add(new CellValue("R2", 1, 0));
	data.add(row2);

	dtable.setData(data);
	dtable.setHeight("300px");
	dtable.setWidth("800px");

	RootPanel.get("table").add(dtable);
	RootPanel.get("buttons").add(new DecisionTableControlsWidget(dtable));

    }
}
