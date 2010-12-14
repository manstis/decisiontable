package org.drools.guvnor.decisiontable.client.widget;

import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource;
import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource.CellTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

/**
 * A widget used to display the DecisionTable's "sidebar". A
 * VerticalDecisionTable would display this to the left of the table of data
 * whereas a HorizontalDecisionTable would display this above the data. *
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableSidebarWidget extends Composite {

    protected DecisionTableWidget dtable;

    // Resources
    protected static CellTableResource resource = GWT
	    .create(CellTableResource.class);
    protected static CellTableStyle style = resource.cellTableStyle();

    /**
     * Construct a "Sidebar" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public DecisionTableSidebarWidget(DecisionTableWidget dtable) {
	this.dtable = dtable;
    }

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableSidebar
     * 
     * @param position
     */
    public abstract void setScrollPosition(int position);

    /**
     * Initialise the sidebar, this normally involves clearing any content and
     * setting up any formatting requirements before calls to addSelector are
     * made. I.E. ensure the sidebar is empty before items are added to it.
     */
    public abstract void initialise();

    /**
     * Add a selector widget to the Sidebar. A selector widget can implement any
     * row-level operation, such as selecting, inserting new (positional) etc.
     * It is intended that this is called as each row to MergableGridWidget is
     * added. Each selector widget will be placed beside the previous.
     */
    public abstract void addSelector();

}
