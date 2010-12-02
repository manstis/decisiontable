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

public class CellFactory {

    private enum DATA_TYPES {
	STRING, DATE;
    }

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

    private static CellFactory instance;

    private static Map<String, DATA_TYPES> datatypeCache = new HashMap<String, DATA_TYPES>();

    public static synchronized CellFactory getInstance() {
	if (instance == null) {
	    instance = new CellFactory();
	}
	return instance;
    }

    private CellFactory() {
    }

    @SuppressWarnings("deprecation")
    public CellValue makeCell(DTColumnConfig column, int iRow, int iCol) {
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
