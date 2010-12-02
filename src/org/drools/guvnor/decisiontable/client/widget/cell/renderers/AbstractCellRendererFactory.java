package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.decisiontable.client.widget.Coordinate;
import org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget;
import org.drools.guvnor.decisiontable.client.widget.SelectionManager;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * A class to lookup the Cell Renderer for a given coordinate.
 * 
 * @author manstis
 * 
 */
public abstract class AbstractCellRendererFactory {

    {
	rendererCache.put(MetadataCol.class.getName(),
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName(),
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName() + "#salience",
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName() + "#enabled",
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName() + "#no-loop",
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName() + "#duration",
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName() + "#auto-focus",
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(AttributeCol.class.getName() + "#lock-on-active",
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache
		.put(AttributeCol.class.getName() + "#date-effective",
			new DecisionTableCellValueAdaptor<Date>(
				new DatePickerCell(DateTimeFormat
					.getFormat(PredefinedFormat.DATE_SHORT))));
	rendererCache
		.put(AttributeCol.class.getName() + "#date-expires",
			new DecisionTableCellValueAdaptor<Date>(
				new DatePickerCell(DateTimeFormat
					.getFormat(PredefinedFormat.DATE_SHORT))));
	rendererCache.put(ConditionCol.class.getName(),
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
	rendererCache.put(ActionCol.class.getName(),
		new DecisionTableCellValueAdaptor<String>(new EditTextCell()));
    }

    // Fall back renderer should we not have one setup for a specific
    // column\type etc
    private static final DecisionTableCellValueAdaptor<String> DEFAULT_RENDERER = new DecisionTableCellValueAdaptor<String>(
	    new EditTextCell());

    private static Map<String, DecisionTableCellValueAdaptor<?>> rendererCache = new HashMap<String, DecisionTableCellValueAdaptor<?>>();

    // Decision table to which the Cell Renderers relate
    protected DecisionTableWidget decisionTable = null;

    public AbstractCellRendererFactory(DecisionTableWidget decisionTable) {
	if (decisionTable == null) {
	    throw new IllegalArgumentException("decisionTable == null");
	}
	this.decisionTable = decisionTable;
    }

    /**
     * Lookup a CellRenderer for the given coordinate.
     * 
     * @param c
     *            The physical coordinate of the cell
     * @return A CellRenderer
     */
    public DecisionTableCellValueAdaptor<?> getCellRenderer(Coordinate c,
	    SelectionManager manager) {
	DecisionTableCellValueAdaptor<?> renderer = getCellRenderer(lookupColumn(c));
	renderer.setSelectionManager(manager);
	return renderer;
    }

    /**
     * Lookup a DTColumnConfig column for a given table coordinate. The column
     * type determines the CellRenderer. A Vertical Decision Table would use the
     * coordinates column property whereas a Horizontal Decision Table would use
     * the coordinates row property.
     * 
     * @param c
     *            The table coordinate
     * @return The DTColumnConfig
     */
    protected abstract DTColumnConfig lookupColumn(Coordinate c);

    // CellRenderers are cached at different levels of precedence; key[0]
    // contains the most specific renderer through to key[2] which contains
    // the most generic. Should no match be found a default TextCellRenderer
    // is provided
    private DecisionTableCellValueAdaptor<?> getCellRenderer(
	    DTColumnConfig column) {

	String[] keys = new String[3];

	if (column instanceof MetadataCol) {
	    keys[2] = null;
	    keys[1] = null;
	    keys[0] = MetadataCol.class.getName();
	} else if (column instanceof AttributeCol) {
	    keys[2] = null;
	    keys[1] = AttributeCol.class.getName();
	    keys[0] = keys[1] + "#" + ((AttributeCol) column).attr;
	} else if (column instanceof ConditionCol) {
	    keys[2] = AttributeCol.class.getName();
	    keys[1] = keys[2] + "#" + ((ConditionCol) column).getFactType();
	    keys[0] = keys[1] + "#" + ((ConditionCol) column).getFactField();
	} else if (column instanceof ActionCol) {
	    keys[2] = null;
	    keys[1] = null;
	    keys[0] = ActionCol.class.getName();
	}

	return lookupRenderer(keys);

    }

    // Try the keys to find a renderer in the cache
    private DecisionTableCellValueAdaptor<?> lookupRenderer(String[] keys) {
	DecisionTableCellValueAdaptor<?> renderer = DEFAULT_RENDERER;
	for (String key : keys) {
	    if (key != null) {
		if (rendererCache.containsKey(key)) {
		    renderer = rendererCache.get(key);
		    break;
		}
	    }
	}
	return renderer;

    }

}