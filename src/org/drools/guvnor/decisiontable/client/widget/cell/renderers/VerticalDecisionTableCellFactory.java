package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import org.drools.guvnor.decisiontable.client.widget.Coordinate;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

/**
 * A factory for cells for vertical Decision Tables.
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableCellFactory extends
	AbstractCellFactory {

    public VerticalDecisionTableCellFactory(
	    DecisionTableWidget decisionTable) {
	super(decisionTable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.decisiontable.client.widget.cell.renderers.
     * AbstractCellRendererFactory
     * #lookupColumn(org.drools.guvnor.decisiontable.client.widget.Coordinate)
     */
    @Override
    protected DTColumnConfig lookupColumn(Coordinate c) {
	DTColumnConfig column = decisionTable.getColumns().get(c.getCol())
		.getModelColumn();
	return column;
    }

}