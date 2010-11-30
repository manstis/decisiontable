package org.drools.guvnor.decisiontable.client.widget;

/**
 * A class to lookup the Cell Renderer for a given coordinate.
 * 
 * @author manstis
 * 
 */
public abstract class AbstractCellRendererFactory {

    // Decision table to which the Cell Renderers relate
    protected DecisionTableWidget decisionTable = null;

    public AbstractCellRendererFactory(DecisionTableWidget decisionTable) {
	if (decisionTable == null) {
	    throw new IllegalArgumentException("decisionTable == null");
	}
	this.decisionTable = decisionTable;
    }

    /**
     * Lookup a Cell Renderer for the given coordinate.
     * 
     * @param c
     *            The physical coordinate of the cell
     * @return A Cell Renderer
     */
    public abstract AbstractCellRenderer getCell(Coordinate c);

}