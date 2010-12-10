package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VerticalDecisionTableSidebarWidget extends
	DecisionTableSidebarWidget {

    /**
     * Construct a "Sidebar" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableSidebarWidget(DecisionTableWidget dtable) {
	super(dtable);

	// Construct the Widget
	panel = new ScrollPanel();
	//widget = new CellTable<...>();

	// We don't want scroll bars on the Sidebar
	panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
	//panel.add(widget);
	initWidget(panel);

    }

    
    @Override
    public void redraw() {
	// TODO Auto-generated method stub

    }

    @Override
    public void setScrollPosition(int position) {
	// TODO Auto-generated method stub

    }

}
