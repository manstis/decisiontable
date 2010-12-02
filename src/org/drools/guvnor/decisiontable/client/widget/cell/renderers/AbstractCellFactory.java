package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * A Factory to provide the Cell specific to a given coordinate.
 * 
 * @author manstis
 * 
 */
public abstract class AbstractCellFactory {

    // Setup the cache. GWT's Cells are wrapped with an adaptor which
    // casts the value of the CellValue to the type required for the GWT Cell
    {
	DecisionTableCellValueAdaptor<String> TEXT_CELL = new DecisionTableCellValueAdaptor<String>(
		new EditTextCell());

	DecisionTableCellValueAdaptor<Date> DATE_CELL = new DecisionTableCellValueAdaptor<Date>(
		new DatePickerCell(
			DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));

	// Boolean list box
	List<String> booleanOptions = new ArrayList<String>();
	booleanOptions.add("true");
	booleanOptions.add("false");
	SelectionCell sc = new SelectionCell(booleanOptions);
	DecisionTableCellValueAdaptor<String> BOOLEAN_CELL = new DecisionTableCellValueAdaptor<String>(
		sc);

	cellCache.put(MetadataCol.class.getName(), TEXT_CELL);
	cellCache.put(AttributeCol.class.getName(), TEXT_CELL);
	cellCache.put(AttributeCol.class.getName() + "#salience", TEXT_CELL);
	cellCache.put(AttributeCol.class.getName() + "#enabled", BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#no-loop", BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#duration", TEXT_CELL);
	cellCache.put(AttributeCol.class.getName() + "#auto-focus",
		BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#lock-on-active",
		BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#date-effective",
		DATE_CELL);
	cellCache
		.put(AttributeCol.class.getName() + "#date-expires", DATE_CELL);
	cellCache.put(ConditionCol.class.getName(), TEXT_CELL);
	cellCache.put(ActionCol.class.getName(), TEXT_CELL);
    }

    // Default cell should a specific one not be configured
    private static final DecisionTableCellValueAdaptor<String> DEFAULT_CELL = new DecisionTableCellValueAdaptor<String>(
	    new EditTextCell());

    // The cache
    private static Map<String, DecisionTableCellValueAdaptor<?>> cellCache = new HashMap<String, DecisionTableCellValueAdaptor<?>>();

    // Decision table to which the Cells relate
    protected DecisionTableWidget decisionTable = null;

    public AbstractCellFactory(DecisionTableWidget decisionTable) {
	if (decisionTable == null) {
	    throw new IllegalArgumentException("decisionTable == null");
	}
	this.decisionTable = decisionTable;
    }

    /**
     * Lookup a Cell for the given coordinate.
     * 
     * @param c
     *            The physical coordinate of the cell
     * @return A Cell
     */
    public DecisionTableCellValueAdaptor<?> getCell(Coordinate c,
	    SelectionManager manager) {
	DecisionTableCellValueAdaptor<?> renderer = getCell(lookupColumn(c));
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

    // Cells are cached at different levels of precedence; key[0]
    // contains the most specific through to key[2] which contains
    // the most generic. Should no match be found the default is provided
    private DecisionTableCellValueAdaptor<?> getCell(DTColumnConfig column) {

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

	return lookupCell(keys);

    }

    // Try the keys to find a renderer in the cache
    private DecisionTableCellValueAdaptor<?> lookupCell(String[] keys) {
	DecisionTableCellValueAdaptor<?> cell = DEFAULT_CELL;
	for (String key : keys) {
	    if (key != null) {
		if (cellCache.containsKey(key)) {
		    cell = cellCache.get(key);
		    break;
		}
	    }
	}
	return cell;

    }

}