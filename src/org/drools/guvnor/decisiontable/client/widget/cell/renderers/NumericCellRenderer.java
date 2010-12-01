package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.regexp.shared.RegExp;

/**
 * A simple text cell render that permits the entry of numbers
 * 
 * @author manstis
 * 
 */
public class NumericCellRenderer extends TextCellRenderer {

    private RegExp validCharacters = RegExp.compile("[0-9]");

    @Override
    public void processEvent(Element parent, NativeEvent event) {
	String type = event.getType();
	if (type.equals("keypress")) {

	    if(event.getCtrlKey()) {
		return;
	    }
	    
	    int keyCode = event.getKeyCode();
	    if(keyCode==KeyCodes.KEY_DELETE || keyCode==KeyCodes.KEY_BACKSPACE || keyCode==KeyCodes.KEY_LEFT || keyCode==KeyCodes.KEY_RIGHT) {
		return;
	    }

	    int charCode = event.getCharCode();
	    String c = String.valueOf((char) charCode);
	    if (!validCharacters.test(c)) {
		event.preventDefault();
	    }
	}
    }

}
