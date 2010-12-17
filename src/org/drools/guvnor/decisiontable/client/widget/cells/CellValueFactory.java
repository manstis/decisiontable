package org.drools.guvnor.decisiontable.client.widget.cells;

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
	STRING() {
	    @Override
	    public CellValue<String> getNewCellValue(int iRow, int iCol) {
		CellValue<String> cv = new CellValue<String>("", iRow, iCol);
		return cv;
	    }

	},
	NUMERIC() {
	    @Override
	    public CellValue<Integer> getNewCellValue(int iRow, int iCol) {
		CellValue<Integer> cv = new CellValue<Integer>(null, iRow, iCol);
		return cv;
	    }

	},
	DATE() {
	    @Override
	    @SuppressWarnings("deprecation")
	    public CellValue<Date> getNewCellValue(int iRow, int iCol) {
		Date d = new Date();
		int year = d.getYear();
		int month = d.getMonth();
		int date = d.getDate();
		Date nd = new Date(year, month, date);
		CellValue<Date> cv = new CellValue<Date>(nd, iRow, iCol);
		return cv;
	    }

	},
	BOOLEAN() {
	    @Override
	    public CellValue<Boolean> getNewCellValue(int iRow, int iCol) {
		CellValue<Boolean> cv = new CellValue<Boolean>(Boolean.TRUE,
			iRow, iCol);
		return cv;
	    }

	},
	DIALECT() {
	    @Override
	    public CellValue<String> getNewCellValue(int iRow, int iCol) {
		CellValue<String> cv = new CellValue<String>("java", iRow, iCol);
		return cv;
	    }

	};
	public abstract CellValue<?> getNewCellValue(int iRow, int iCol);
    }

    // Setup the cache
    {
	datatypeCache.put(MetadataCol.class.getName(), DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName(), DATA_TYPES.STRING);
	datatypeCache.put(AttributeCol.class.getName() + "#salience",
		DATA_TYPES.NUMERIC);
	datatypeCache.put(AttributeCol.class.getName() + "#enabled",
		DATA_TYPES.BOOLEAN);
	datatypeCache.put(AttributeCol.class.getName() + "#no-loop",
		DATA_TYPES.BOOLEAN);
	datatypeCache.put(AttributeCol.class.getName() + "#duration",
		DATA_TYPES.NUMERIC);
	datatypeCache.put(AttributeCol.class.getName() + "#auto-focus",
		DATA_TYPES.BOOLEAN);
	datatypeCache.put(AttributeCol.class.getName() + "#lock-on-active",
		DATA_TYPES.BOOLEAN);
	datatypeCache.put(AttributeCol.class.getName() + "#date-effective",
		DATA_TYPES.DATE);
	datatypeCache.put(AttributeCol.class.getName() + "#date-expires",
		DATA_TYPES.DATE);
	datatypeCache.put(AttributeCol.class.getName() + "#dialect",
		DATA_TYPES.DIALECT);
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
    public CellValue<?> makeCellValue(DTColumnConfig column, int iRow, int iCol) {
	DATA_TYPES dataType = getDataType(column);
	return dataType.getNewCellValue(iRow, iCol);
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
