package org.drools.guvnor.decisiontable.client.widget;

import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

/**
 * A factory for cells for vertical Decision Tables. The coordinate's column
 * index is used to lookup the corresponding model column where from the
 * appropriate Cell Renderers can be ascertained.
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableCellRendererFactory extends
	AbstractCellRendererFactory {

    public VerticalDecisionTableCellRendererFactory(
	    DecisionTableWidget decisionTable) {
	super(decisionTable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.AbstractCellRendererFactory
     * #getCell(org.drools.guvnor.decisiontable.client.widget.Coordinate)
     */
    public AbstractCellRenderer getCell(Coordinate c) {
	DTColumnConfig column = decisionTable.columns.get(c.getCol())
		.getModelColumn();

	// TODO Get the correct type of cell
	return new TextCellRenderer();
    }
}