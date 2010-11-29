package org.drools.guvnor.decisiontable.client.widget;

import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;


/**
 * SelectionManagers act as the Mediator between user-interactions and a
 * DecisionTable implementation. They have intrinsic knowledge of how to perform
 * the functions for the type of Decision Table to which they relate
 * 
 * @author manstis
 * 
 */
public interface SelectionManager {

    public abstract void startSelecting(Coordinate start);

    public abstract CellValue getPhysicalCell(Coordinate c);

    public abstract void clearSelection();

    public abstract void setSelectionValue(Object value);

    public abstract void addColumn(DTColumnConfig modelColumn);

    public abstract void insertColumnBefore(DTColumnConfig modelColumn, int index);

    public abstract void addRow();

    public abstract void insertRowBefore(int index);

    public abstract void assertRowHeights();

    public abstract boolean toggleMerging();

}