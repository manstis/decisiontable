package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;

/**
 * A widget used to display the DecisionTable's "sidebar". A
 * VerticalDecisionTable would display this to the left of the table of data
 * whereas a HorizontalDecisionTable would display this above the data.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableSidebarWidget extends Composite {

    protected Panel panel;
    protected DecisionTableWidget dtable;

    /**
     * Construct a "Sidebar" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public DecisionTableSidebarWidget(DecisionTableWidget dtable) {
	this.dtable = dtable;
    }

    /**
     * Redraw the "Sidebar"
     */
    public abstract void redraw();

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableSidebar
     * 
     * @param position
     */
    public abstract void setScrollPosition(int position);

}
