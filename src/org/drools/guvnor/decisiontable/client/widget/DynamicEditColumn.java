package org.drools.guvnor.decisiontable.client.widget;

import java.util.List;

import com.google.gwt.user.cellview.client.Column;

/**
 * A column that retrieves it's cell value from an indexed position in a List
 * holding the row data. Normally the row type is defined as a statically
 * typed Class and columns retrieve their cell values from discrete members.
 * A Decision Table's row contains dynamic (i.e. a List) of elements.
 * 
 * @author manstis
 *
 */
public class DynamicEditColumn extends Column<List<CellValue>, CellValue> {

    private int columnIndex = 0;

    public DynamicEditColumn(EditableCell cell, int columnIndex) {
	super(cell);
	this.columnIndex = columnIndex;
    }

    @Override
    public CellValue getValue(List<CellValue> object) {
	return (CellValue) object.get(columnIndex);
    }

    public int getColumnIndex() {
	return this.columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
	this.columnIndex = columnIndex;
    }

}
