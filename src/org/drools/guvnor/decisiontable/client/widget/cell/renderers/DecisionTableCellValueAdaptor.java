package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import java.util.Set;

import org.drools.guvnor.decisiontable.client.widget.CellValue;
import org.drools.guvnor.decisiontable.client.widget.SelectionManager;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DecisionTableCellValueAdaptor<T> extends AbstractCell<CellValue> {

    protected SelectionManager manager;
    private AbstractCell cell;

    @SuppressWarnings("unchecked")
    public DecisionTableCellValueAdaptor(AbstractCell cell) {
	super(cell.getConsumedEvents());
	this.cell = cell;
    }
    
    public void setSelectionManager(SelectionManager manager) {
	this.manager=manager;
    }
    
    @Override
    public boolean dependsOnSelection() {
	return cell.dependsOnSelection();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getConsumedEvents() {
	return cell.getConsumedEvents();
    }

    @Override
    public boolean handlesSelection() {
	return cell.handlesSelection();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEditing(Element parent, CellValue value, Object key) {
	return cell.isEditing(parent, (T) value.getValue(), key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBrowserEvent(Element parent, CellValue value, Object key,
	    NativeEvent event, ValueUpdater<CellValue> valueUpdater) {
	cell.onBrowserEvent(parent, (T) value.getValue(), key, event, new ValueUpdater<T>() {

	    @Override
	    public void update(T value) {
		manager.update(value);
	    }

	});
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(CellValue value, Object key, SafeHtmlBuilder sb) {
	cell.render((T) value.getValue(), key, sb);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean resetFocus(Element parent, CellValue value, Object key) {
	return cell.resetFocus(parent, (T) value.getValue(), key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Element parent, CellValue value, Object key) {
	cell.setValue(parent, (T) value.getValue(), key);
    }

}
