package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * A cell that can be rendered by the <code>PassthroughCell</code>. Event
 * handling is conducted by the <code>PassthroughCell</code> however it
 * delegates rendering of different types of cells to implementations of this
 * interface. Instances of different Cell Renderers are looked up using the
 * appropriate <code>AbstractCellRendererFactory</code>
 * 
 * @author manstis
 * 
 */
public interface AbstractCellRenderer {

    /**
     * Return HTML representing the "read-only" state of the cell.
     * PassthroughCell uses this as the innerHTML of the corresponding HTML cell
     * element.
     * 
     * @param value
     *            The value of the cell
     * @return SafeHtml encapsulating the HTML
     */
    public abstract SafeHtml getReadOnly(CellValue value);

    /**
     * Return HTML representing the "editing" state of the cell. PassthroughCell
     * uses this as the innerHTML of the corresponding HTML cell element.
     * 
     * @param value
     *            The value of the cell
     * @return SafeHtml encapsulating the HTML
     */
    public abstract SafeHtml getEditorCell(CellValue value);

    /**
     * Set focus on the editor.
     * 
     * @param parent
     *            The HTML element containing this control. Implementations
     *            would normally walk child elements to find an appropriate
     *            control on which to set the focus.
     */
    public abstract void focus(Element parent);

    /**
     * Cancel editing.
     * 
     * @param parent
     *            The HTML element containing this control.
     */
    public abstract void cancel(Element parent);

    /**
     * Commit the edit.
     * 
     * @param parent
     *            The HTML element containing this control. Implementations
     *            would normally walk child elements and extract an appropriate
     *            value that is returned and used to update the underlying
     *            table.
     * 
     * @return The new value
     */
    public abstract CellValue commit(Element parent);

}
