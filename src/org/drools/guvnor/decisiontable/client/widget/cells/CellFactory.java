package org.drools.guvnor.decisiontable.client.widget.cells;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.cell.client.CheckboxCell;
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
public class CellFactory {

    // Setup the cache. GWT's Cells are wrapped with an adaptor which
    // casts the value of the CellValue to the type required for the GWT Cell
    {
	// Text editor
	DecisionTableCellValueAdaptor<String> TEXT_CELL = new DecisionTableCellValueAdaptor<String>(
		new PopupTextEditCell());

	// Numeric editor
	DecisionTableCellValueAdaptor<Integer> NUMERIC_CELL = new DecisionTableCellValueAdaptor<Integer>(
		new PopupNumericEditCell());

	// Date editor
	DecisionTableCellValueAdaptor<Date> DATE_CELL = new DecisionTableCellValueAdaptor<Date>(
		new DatePickerCell(
			DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));

	// Dialect editor
	List<String> dialectOptions = new ArrayList<String>();
	dialectOptions.add("java");
	dialectOptions.add("mvel");
	SelectionCell sc = new SelectionCell(dialectOptions);
	DecisionTableCellValueAdaptor<String> DIALECT_CELL = new DecisionTableCellValueAdaptor<String>(
		sc);

	// Boolean editor
	DecisionTableCellValueAdaptor<Boolean> BOOLEAN_CELL = new DecisionTableCellValueAdaptor<Boolean>(
		new CheckboxCell());

	cellCache.put(MetadataCol.class.getName(), TEXT_CELL);
	cellCache.put(AttributeCol.class.getName(), TEXT_CELL);
	cellCache.put(AttributeCol.class.getName() + "#salience", NUMERIC_CELL);
	cellCache.put(AttributeCol.class.getName() + "#enabled", BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#no-loop", BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#duration", NUMERIC_CELL);
	cellCache.put(AttributeCol.class.getName() + "#auto-focus",
		BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#lock-on-active",
		BOOLEAN_CELL);
	cellCache.put(AttributeCol.class.getName() + "#date-effective",
		DATE_CELL);
	cellCache
		.put(AttributeCol.class.getName() + "#date-expires", DATE_CELL);
	cellCache.put(AttributeCol.class.getName() + "#dialect", DIALECT_CELL);
	cellCache.put(ConditionCol.class.getName(), TEXT_CELL);
	cellCache.put(ActionCol.class.getName(), TEXT_CELL);
    }

    // Default cell should a specific one not be configured
    private static final DecisionTableCellValueAdaptor<String> DEFAULT_CELL = new DecisionTableCellValueAdaptor<String>(
	    new EditTextCell());

    // The cache
    private static Map<String, DecisionTableCellValueAdaptor<? extends Comparable<?>>> cellCache = new HashMap<String, DecisionTableCellValueAdaptor<? extends Comparable<?>>>();

    /**
     * Lookup a Cell for the given DTColumnConfig. Cells are cached at different
     * levels of precedence; key[0] contains the most specific through to key[2]
     * which contains the most generic. Should no match be found the default is
     * provided
     * 
     * @param column
     *            The Decision Table model column
     * @param manager
     *            The SelectionManager used to update cells' content
     * @return A Cell
     */
    public DecisionTableCellValueAdaptor<? extends Comparable<?>> getCell(DTColumnConfig column,
	    DecisionTableWidget dtable) {

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

	DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = lookupCell(keys);
	cell.setDecisionTableWidget(dtable);
	return cell;

    }

    // Try the keys to find a renderer in the cache
    private DecisionTableCellValueAdaptor<? extends Comparable<?>> lookupCell(String[] keys) {
	DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = DEFAULT_CELL;
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