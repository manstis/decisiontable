package org.drools.guvnor.decisiontable.client.widget;

import java.util.HashMap;
import java.util.Map;

import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
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

    private static Map<String, AbstractCellRenderer> rendererCache = new HashMap<String, AbstractCellRenderer>();

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

	AbstractCellRenderer renderer = null;

	// TODO Get the correct type of cell
	if (column instanceof AttributeCol) {
	    AttributeCol col = (AttributeCol) column;
	    if (col.attr.equalsIgnoreCase("enabled")) {
		renderer = getRenderer("boolean");
	    } else {
		renderer = getRenderer("text");
	    }
	} else {
	    renderer = getRenderer("text");
	}
	return renderer;
    }

    private AbstractCellRenderer getRenderer(String key) {
	if (!rendererCache.containsKey(key)) {
	    rendererCache.put(key, makeRenderer(key));
	}
	return rendererCache.get(key);
    }

    private AbstractCellRenderer makeRenderer(String key) {
	AbstractCellRenderer renderer = null;
	if (key.equalsIgnoreCase("text")) {
	    renderer = new TextCellRenderer();
	} else if (key.equalsIgnoreCase("boolean")) {
	    renderer = new BooleanCellRenderer();
	}
	return renderer;

    }
}