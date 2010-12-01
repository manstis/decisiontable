package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import org.drools.guvnor.decisiontable.client.widget.CellValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A render to give a boolean selection
 * 
 * @author manstis
 * 
 */
public class BooleanCellRenderer extends AbstractCellRenderer {

    private static Template template;

    interface Template extends SafeHtmlTemplates {

	@Template("<div>{0}</div>")
	SafeHtml labelCell(String value);
    }

    public BooleanCellRenderer() {
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
	String state = value.getValue();
	StringBuffer sb = new StringBuffer();
	sb.append("<select>");
	sb.append(makeOption("true", state));
	sb.append(makeOption("false", state));
	sb.append("</select>");
	return SafeHtmlUtils.fromTrustedString(sb.toString());
    }

    private String makeOption(String value, String state) {
	StringBuffer sb = new StringBuffer();
	sb.append("<option value=\"");
	sb.append(value);
	sb.append("\"");
	if(state.equalsIgnoreCase(value)) {
	    sb.append(" selected=\"");
	    sb.append(value);
	    sb.append("\"");
	}
	sb.append("\">");
	sb.append(value);
	sb.append("</option>");
	return sb.toString();
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
	SelectElement select = getSelectElement(parent);
	select.focus();
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
	clearInput(getSelectElement(parent));
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
	clearInput(getSelectElement(parent));
	SelectElement ie = getSelectElement(parent);
	String text = ie.getValue();
	CellValue newValue = new CellValue(text, 0, 0);
	return newValue;
    }

    // Gets the first child of the container; which is the <INPUT> element
    private SelectElement getSelectElement(Element parent) {
	return parent.getFirstChild().<SelectElement> cast();
    }

    // Clear selected from the input element. Both Firefox and IE fire spurious
    // onblur events after the input is removed from the DOM if selection is not
    // cleared.
    //TODO This is borrowed from the TextCellRenderer
    private native void clearInput(Element input) /*-{
        if (input.selectionEnd)
        input.selectionEnd = input.selectionStart;
        else if ($doc.selection)
        $doc.selection.clear();
    }-*/;

}
