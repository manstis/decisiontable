package org.drools.guvnor.decisiontable.client.widget;

import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource;
import org.drools.guvnor.decisiontable.client.widget.resources.CellTableResource.CellTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;

/**
 * A widget used to display the DecisionTable's "header". A
 * VerticalDecisionTable would display this above the table of data whereas a
 * HorizontalDecisionTable would display this to the left of the data.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableHeaderWidget extends Composite {

    protected Panel panel;
    protected DecisionTableWidget dtable;

    // Resources
    protected static final CellTableResource resource = GWT.create(CellTableResource.class);
    protected static final CellTableStyle style=resource.cellTableStyle();

    // Image resources
    protected static final String DOWN_ARROW = makeImage(resource.downArrow());
    protected static final String SMALL_DOWN_ARROW = makeImage(resource
	    .smallDownArrow());
    protected static final String UP_ARROW = makeImage(resource.upArrow());
    protected static final String SMALL_UP_ARROW = makeImage(resource.smallUpArrow());
    protected static final String MERGE_LINK = makeImage(resource.mergeLink());
    protected static final String MERGE_UNLINK = makeImage(resource.mergeUnlink());

    private static String makeImage(ImageResource resource) {
	AbstractImagePrototype prototype = AbstractImagePrototype
		.create(resource);
	return prototype.getHTML();
    }

    /**
     * Construct a "Header" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public DecisionTableHeaderWidget(DecisionTableWidget dtable) {
	this.dtable = dtable;
    }

    /**
     * Redraw the "Header"
     */
    public abstract void redraw();

    /**
     * Set scroll position to enable some degree of synchronisation between
     * DecisionTable and DecisionTableHeader
     * 
     * @param position
     */
    public abstract void setScrollPosition(int position);

}
