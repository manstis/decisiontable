package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.decisiontable.client.widget.CellValue;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

/**
 * A Factory to create CellValues applicable to given columns.
 * 
 * @author manstis
 * 
 */
public class CellValueFactory {

    // Recognised data-types
    private enum DATA_TYPES {
	STRING, DATE;
    }

    // Setup the cache
    {
	datatypeCache.put(MetadataCol.class.getName(), DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName(), DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#salience",
		DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#enabled",
		DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#no-loop",
		DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#duration",
		DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#auto-focus",
		DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#lock-on-active",
		DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#date-effective",
		DATA_TYPES.DATE);
	datatypeCache.put(AttributeCol.class.getName() + "#date-expires",
		DATA_TYPES.DATE);
	datatypeCache.put(ConditionCol.class.getName(), DATA_TYPES.STRING);
	datatypeCache.put(ActionCol.class.getName(), DATA_TYPES.STRING);
    }

    // The cache
    private static Map<String, DATA_TYPES> datatypeCache = new HashMap<String, DATA_TYPES>();

    // The Singleton
    private static CellValueFactory instance;

    // Singleton constructor
    public static synchronized CellValueFactory getInstance() {
	if (instance == null) {
	    instance = new CellValueFactory();
	}
	return instance;
    }

    private CellValueFactory() {
    }

    /**
     * Make a CellValue applicable for the column
     * 
     * @param column
     *            The model column
     * @param iRow
     *            Row coordinate for initialisation
     * @param iCol
     *            Column coordinate for initialisation
     * @return A CellValue
     */
    @SuppressWarnings("deprecation")
    public CellValue makeCellValue(DTColumnConfig column, int iRow, int iCol) {
	DATA_TYPES dataType = getDataType(column);
	CellValue cv = null;
	switch (dataType) {
	case STRING:
	    cv = new CellValue("", iRow, iCol);
	    break;
	case DATE:
	    Date d = new Date();
	    int year = d.getYear();
	    int month = d.getMonth();
	    int date = d.getDate();
	    Date nd = new Date(year, month, date);
	    cv = new CellValue(nd, iRow, iCol);
	    break;
	default:
	    cv = new CellValue("", iRow, iCol);
	}

	return cv;
    }

    // DataTypes are cached at different levels of precedence; key[0]
    // contains the most specific through to key[2] which contains
    // the most generic. Should no match be found the default is provided
    private DATA_TYPES getDataType(DTColumnConfig column) {

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

	return lookupDataType(keys);

    }

    // Try the keys to find a data-type in the cache
    private DATA_TYPES lookupDataType(String[] keys) {
	DATA_TYPES dataType = DATA_TYPES.STRING;
	for (String key : keys) {
	    if (key != null) {
		if (datatypeCache.containsKey(key)) {
		    dataType = datatypeCache.get(key);
		    break;
		}
	    }
	}
	return dataType;

    }

}
