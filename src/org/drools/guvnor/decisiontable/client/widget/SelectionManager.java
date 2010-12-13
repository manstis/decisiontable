package org.drools.guvnor.decisiontable.client.widget;

import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;

import com.google.gwt.cell.client.ValueUpdater;

/**
 * SelectionManagers act as the Mediator between user-interactions and a
 * DecisionTable implementation. They have intrinsic knowledge of how to perform
 * the functions for the type of Decision Table to which they relate
 * 
 * @author manstis
 * 
 */
public interface SelectionManager extends ValueUpdater<Object> {

    public abstract void startSelecting(Coordinate start);

    public abstract Comparable<?> getPhysicalCell(Coordinate c);

    public abstract void clearSelection();

    public abstract void addColumn(DTColumnConfig modelColumn);

    public abstract void insertColumnBefore(DTColumnConfig modelColumn,
	    int index);

    public abstract void addRow();

    public abstract void insertRowBefore(int index);

    public abstract void assertModelIndexes();

    public abstract void assertModelMerging();

    public abstract void removeModelMerging();

    public abstract boolean toggleMerging();

    public abstract void sort();

}