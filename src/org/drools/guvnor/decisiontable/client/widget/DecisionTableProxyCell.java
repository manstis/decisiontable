package org.drools.guvnor.decisiontable.client.widget;

import org.drools.guvnor.decisiontable.client.widget.cells.AbstractCellFactory;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * All Decision Table cells are of this type. This class forwards all
 * AbstractCell method invocations to the appropriate cell corresponding to the
 * physical cell coordinate. For a Vertical Decision table this would be the
 * same as the cell defined for the column however for a Horizontal Decision
 * table the AbstractCell for the physical coordinate differs per row.
 * 
 * @author manstis
 * 
 */
public class DecisionTableProxyCell extends AbstractCell<CellValue<?>> {

    protected SelectionManager manager;
    protected AbstractCellFactory cellFactory;

    // The physical cell for the coordinate
    private AbstractCell<CellValue<?>> physicalCell;

    public DecisionTableProxyCell(SelectionManager manager,
	    AbstractCellFactory cellFactory) {
	super("click", "keydown", "keyup", "keypress", "change", "blur");
	this.manager = manager;
	this.cellFactory = cellFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#isEditing(com.google.gwt.dom.
     * client.Element, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isEditing(Element parent, CellValue<?> value, Object key) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue<?> physical = manager.getPhysicalCell(c);
	assertCell(c);
	return physicalCell.isEditing(parent, physical, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt
     * .dom.client.Element, java.lang.Object, java.lang.Object,
     * com.google.gwt.dom.client.NativeEvent,
     * com.google.gwt.cell.client.ValueUpdater)
     */
    @Override
    public void onBrowserEvent(Element parent, CellValue<?> value, Object key,
	    NativeEvent event, ValueUpdater<CellValue<?>> valueUpdater) {

	String type = event.getType();
	Coordinate c = value.getPhysicalCoordinate();
	CellValue<?> physical = manager.getPhysicalCell(c);
	assertCell(c);

	// Setup the selected range
	if (type.equals("click")) {
	    manager.startSelecting(c);
	}

	physicalCell.onBrowserEvent(parent, physical, c, event, valueUpdater);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.AbstractCell#render(java.lang.Object,
     * java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    public void render(CellValue<?> value, Object key, SafeHtmlBuilder sb) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue<?> physical = manager.getPhysicalCell(c);
	assertCell(c);
	physicalCell.render(physical, c, sb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#resetFocus(com.google.gwt.dom
     * .client.Element, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean resetFocus(Element parent, CellValue<?> value, Object key) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue<?> physical = manager.getPhysicalCell(c);
	assertCell(c);
	return physicalCell.resetFocus(parent, physical, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#setValue(com.google.gwt.dom.client
     * .Element, java.lang.Object, java.lang.Object)
     */
    @Override
    public void setValue(Element parent, CellValue<?> value, Object key) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue<?> physical = manager.getPhysicalCell(c);
	assertCell(c);
	physicalCell.setValue(parent, physical, c);
    }

    // Get the cell for the physical coordinate
    private void assertCell(Coordinate c) {
	physicalCell = cellFactory.getCell(c, manager);
    }

}
