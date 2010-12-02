package org.drools.guvnor.decisiontable.client.widget;

import org.drools.guvnor.decisiontable.client.widget.cell.renderers.AbstractCellRendererFactory;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DecisionTableCellWrapper extends AbstractCell<CellValue> {

    protected SelectionManager manager;
    protected AbstractCellRendererFactory cellFactory;

    private AbstractCell<CellValue> cell;

    public DecisionTableCellWrapper(SelectionManager manager,
	    AbstractCellRendererFactory cellFactory) {
	super("click", "keydown", "keyup", "keypress", "blur");
	this.manager = manager;
	this.cellFactory = cellFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEditing(Element parent, CellValue value, Object key) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue physical = manager.getPhysicalCell(c);
	assertCell(c);
	return cell.isEditing(parent, physical, c);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBrowserEvent(Element parent, CellValue value, Object key,
	    NativeEvent event, ValueUpdater<CellValue> valueUpdater) {

	String type = event.getType();
	Coordinate c = value.getPhysicalCoordinate();
	CellValue physical = manager.getPhysicalCell(c);
	assertCell(c);

	if (type.equals("click")) {
	    manager.startSelecting(c);
	}

	cell.onBrowserEvent(parent, physical, c, event, valueUpdater);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(CellValue value, Object key, SafeHtmlBuilder sb) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue physical = manager.getPhysicalCell(c);
	assertCell(c);
	cell.render(physical, c, sb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean resetFocus(Element parent, CellValue value, Object key) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue physical = manager.getPhysicalCell(c);
	assertCell(c);
	return cell.resetFocus(parent, physical, c);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Element parent, CellValue value, Object key) {
	Coordinate c = value.getPhysicalCoordinate();
	CellValue physical = manager.getPhysicalCell(c);
	assertCell(c);
	cell.setValue(parent, physical, c);
    }

    private void assertCell(Coordinate c) {
	cell = cellFactory.getCellRenderer(c, manager);
    }

}
