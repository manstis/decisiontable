package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Cell that only permits entry of positive and negative integers
 * 
 * @author manstis
 * 
 */
public class EditNumberCell extends EditTextCell {

    // A valid number
    private static final RegExp VALID = RegExp.compile("(^-{0,1}\\d*$)");

    @Override
    public void onBrowserEvent(Element parent, String value, Object key,
	    NativeEvent event, ValueUpdater<String> valueUpdater) {

	super.onBrowserEvent(parent, value, key, event, valueUpdater);

	// Allow "navigation" keys
	String eventType = event.getType();
	if (eventType.equals("keypress")) {
	    int keyCode = event.getKeyCode();
	    if (event.getCtrlKey() || keyCode == KeyCodes.KEY_BACKSPACE
		    || keyCode == KeyCodes.KEY_DELETE
		    || keyCode == KeyCodes.KEY_LEFT
		    || keyCode == KeyCodes.KEY_RIGHT
		    || keyCode == KeyCodes.KEY_TAB) {
		return;
	    }

	    int charCode = event.getCharCode();
	    String number = getInputElement(parent).getValue()
		    + String.valueOf((char) charCode);
	    if (!VALID.test(String.valueOf(number))) {
		event.preventDefault();
	    }
	} else if (eventType.equals("blur")) {

	    // Clear orphan "-" symbols
	    String number = getInputElement(parent).getValue();
	    if (number.equals("-")) {
		getInputElement(parent).setValue("");
	    }
	}
    }

    // Get the input element in edit mode.
    private InputElement getInputElement(Element parent) {
	return parent.getFirstChild().<InputElement> cast();
    }

}
