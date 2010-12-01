package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import org.drools.guvnor.decisiontable.client.widget.CellValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * A simple text cell render
 * 
 * @author manstis
 * 
 */
public class TextCellRenderer extends AbstractCellRenderer {

    private static Template template;

    interface Template extends SafeHtmlTemplates {
	@Template("<input style=\"width:40px\" type=\"text\" value=\"{0}\" tabindex=\"-1\"></input>")
	SafeHtml textInputCell(String value);

	@Template("<div>{0}</div>")
	SafeHtml labelCell(String value);
    }

    public TextCellRenderer() {
	if (template == null) {
	    template = GWT.create(Template.class);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.decisiontable.client.widget.AbstractCellRenderer#
     * getReadOnly(org.drools.guvnor.decisiontable.client.widget.CellValue)
     */
    @Override
    public SafeHtml getReadOnly(CellValue value) {
	return template.labelCell(value.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.guvnor.decisiontable.client.widget.AbstractCellRenderer#
     * getEditorCell(org.drools.guvnor.decisiontable.client.widget.CellValue)
     */
    @Override
    public SafeHtml getEditorCell(CellValue value) {
	return template.textInputCell(value.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.AbstractCellRenderer#focus
     * (com.google.gwt.dom.client.Element)
     */
    @Override
    public void focus(Element parent) {
	InputElement input = getInputElement(parent);
	input.focus();
	input.select();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.AbstractCellRenderer#cancel
     * (com.google.gwt.dom.client.Element)
     */
    @Override
    public void cancel(Element parent) {
	clearInput(getInputElement(parent));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.AbstractCellRenderer#commit
     * (com.google.gwt.dom.client.Element)
     */
    @Override
    public CellValue commit(Element parent) {
	clearInput(getInputElement(parent));
	InputElement ie = getInputElement(parent);
	String text = ie.getValue();
	CellValue newValue = new CellValue(text, 0, 0);
	return newValue;
    }

    // Gets the first child of the container; which is the <INPUT> element
    private InputElement getInputElement(Element parent) {
	return parent.getFirstChild().<InputElement> cast();
    }

    // Clear selected from the input element. Both Firefox and IE fire spurious
    // onblur events after the input is removed from the DOM if selection is not
    // cleared.
    private native void clearInput(Element input) /*-{
        if (input.selectionEnd)
        input.selectionEnd = input.selectionStart;
        else if ($doc.selection)
        $doc.selection.clear();
    }-*/;

}
