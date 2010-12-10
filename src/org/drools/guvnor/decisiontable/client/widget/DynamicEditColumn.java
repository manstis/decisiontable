package org.drools.guvnor.decisiontable.client.widget;

import java.util.List;

import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

import com.google.gwt.user.cellview.client.Column;

/**
 * A column that retrieves it's cell value from an indexed position in a List
 * holding the row data. Normally the row type is defined as a statically typed
 * Class and columns retrieve their cell values from discrete members. A
 * Decision Table's row contains dynamic (i.e. a List) of elements.
 * 
 * @author manstis
 * 
 */
public class DynamicEditColumn extends Column<List<CellValue<?>>, CellValue<?>> {

    private int columnIndex = 0;
    private DTColumnConfig modelColumn;
    private SortDirection sortDirection = SortDirection.NONE;
    private int sortIndex = -1;

    public DynamicEditColumn(DTColumnConfig modelColumn,
	    DecisionTableProxyCell cell, int columnIndex) {
	super(cell);
	this.modelColumn = modelColumn;
	this.columnIndex = columnIndex;
    }

    @Override
    public CellValue<?> getValue(List<CellValue<?>> object) {
	return (CellValue<?>) object.get(columnIndex);
    }

    public int getColumnIndex() {
	return this.columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
	this.columnIndex = columnIndex;
    }

    public DTColumnConfig getModelColumn() {
	return this.modelColumn;
    }

    public SortDirection getSortDirection() {
	return this.sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
	this.sortDirection = sortDirection;
    }

    public int getSortIndex() {
	return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
	this.sortIndex = sortIndex;
    }

    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	}
	DynamicEditColumn c = (DynamicEditColumn) o;
	return c.columnIndex == this.columnIndex
		&& c.sortDirection == this.sortDirection
		&& c.sortIndex == this.sortIndex;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 31 * hash + columnIndex;
	hash = 31 * hash + sortDirection.hashCode();
	hash = 31 * hash + sortIndex;
	return hash;
    }

}
