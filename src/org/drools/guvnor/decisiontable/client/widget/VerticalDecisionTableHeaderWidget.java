package org.drools.guvnor.decisiontable.client.widget;

import org.drools.guvnor.decisiontable.client.guvnor.TableImageResources;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The "header" for a VerticalDecisionTable. This is a bit of a cheat really: A
 * VerticalDecisionTable can have one row of it's Conditions columns merged if
 * need be. This is not easily achieved with GWT's vanilla CellTable as the
 * Header is merged across all columns that share the same object and therefore
 * you cannot specify it is rendered differently for each column. The cheat
 * employed herein is to use a single "Header" Cell for all columns. This header
 * contains a list of all columns defined for the DecisionTable and renders an
 * HTML table accordingly. The plumbing for sorting, filtering etc therefore has
 * to be performed at a level lower than the usual GWT abstraction (i.e. more
 * DOM level manipulation).
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableHeaderWidget extends
	DecisionTableHeaderWidget {

    private HeaderWidget widget;

    /**
     * Construct a "Header" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableHeaderWidget(DecisionTableWidget dtable) {
	this.dtable = dtable;

	// Construct the Widget
	panel = new ScrollPanel();
	widget = new HeaderWidget(dtable);

	// We don't want scroll bars on the Header
	panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
	panel.add(widget);
	initWidget(panel);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
     * #setScrollPosition(int)
     */
    @Override
    public void setScrollPosition(int position) {
	((ScrollPanel) this.panel).setHorizontalScrollPosition(position);
    }

    @Override
    public void redraw() {
	widget.redraw();
    }

    private class HeaderWidget extends Widget {

	private TableElement table;
	// private DecisionTableWidget dtable;

	private final TableImageResources TABLE_IMAGE_RESOURCES = GWT
		.create(TableImageResources.class);
	private final AbstractImagePrototype DOWN_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.downArrow());
	private final AbstractImagePrototype SMALL_DOWN_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.smallDownArrow());
	private final AbstractImagePrototype UP_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.upArrow());
	private final AbstractImagePrototype SMALL_UP_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.smallUpArrow());

	private AbstractImagePrototype makeImage(ImageResource resource) {
	    AbstractImagePrototype prototype = AbstractImagePrototype
		    .create(resource);
	    return prototype;
	}

	public HeaderWidget(DecisionTableWidget dtable) {
	    dtable = dtable;
	    table = Document.get().createTableElement();
	    table.setCellPadding(0);
	    table.setCellSpacing(0);
	    table.setClassName("header-table");

	    table.appendChild(Document.get().createTRElement());
	    table.appendChild(Document.get().createTRElement());
	    sinkEvents(Event.getTypeInt("click"));
	    setElement(table);
	}

	@Override
	public void onBrowserEvent(Event event) {
	    String type = event.getType();

	    EventTarget eventTarget = event.getEventTarget();
	    if (!Element.is(eventTarget)
		    || !getElement().isOrHasChild(Element.as(eventTarget))) {
		return;
	    }

	    Element target = event.getEventTarget().cast();

	    // Find the cell where the event occurred.
	    TableCellElement cell = findNearestParentCell(target);
	    if (cell == null) {
		return;
	    }

	    if (type.equals("click")) {
		Window.alert(cell.getInnerText());
	    }
	}

	private TableCellElement findNearestParentCell(Element elem) {
	    while ((elem != null) && (elem != table)) {
		String tagName = elem.getTagName();
		if ("td".equalsIgnoreCase(tagName)
			|| "th".equalsIgnoreCase(tagName)) {
		    return elem.cast();
		}
		elem = elem.getParentElement();
	    }
	    return null;
	}

	private TableRowElement makeTableRowElement() {
	    TableRowElement trow = Document.get().createTRElement();
	    trow.setClassName("header-row");
	    return trow;
	}

	public void redraw() {
	    TableRowElement oldRow;
	    TableRowElement newRow;
	    oldRow = table.getRows().getItem(0);
	    newRow = makeTopRowElement();
	    table.replaceChild(newRow, oldRow);
	    oldRow = table.getRows().getItem(1);
	    newRow = makeBottomRowElement();
	    table.replaceChild(newRow, oldRow);
	}

	protected TableRowElement makeTopRowElement() {
	    TableRowElement trow = makeTableRowElement();
	    TableCellElement cell = null;
	    for (DynamicEditColumn column : dtable.columns) {
		DTColumnConfig modelColumn = column.getModelColumn();
		if (modelColumn instanceof MetadataCol) {
		    cell = makeMetadataTableCellElement((MetadataCol) modelColumn);
		    trow.appendChild(cell);
		} else if (modelColumn instanceof AttributeCol) {
		    cell = makeAttributeTableCellElement((AttributeCol) modelColumn);
		    trow.appendChild(cell);
		} else if (modelColumn instanceof ConditionCol) {
		    cell = makeConditionFactTypeTableCellElement((ConditionCol) modelColumn);
		    cell.setClassName("header-cell-secondary");
		    trow.appendChild(cell);
		} else if (modelColumn instanceof ActionCol) {
		    cell = makeActionTableCellElement((ActionCol) modelColumn);
		    trow.appendChild(cell);
		}
	    }
	    return trow;
	}

	protected TableRowElement makeBottomRowElement() {
	    TableRowElement trow = makeTableRowElement();
	    TableCellElement cell = null;
	    for (DynamicEditColumn column : dtable.columns) {
		DTColumnConfig modelColumn = column.getModelColumn();
		if (modelColumn instanceof ConditionCol) {
		    cell = makeConditionFactFieldTableCellElement((ConditionCol) modelColumn);
		    trow.appendChild(cell);
		}
	    }
	    return trow;
	}

	private TableCellElement makeTableCellElement() {
	    TableCellElement tcell = Document.get().createTHElement();
	    tcell.setClassName("header-cell-primary");

	    DivElement div1 = Document.get().createDivElement();
	    DivElement div2 = Document.get().createDivElement();
	    DivElement div3 = Document.get().createDivElement();
	    div1.setClassName("header-container");
	    div2.setClassName("header-text");
	    div3.setClassName("header-widget");

	    div1.appendChild(div2);
	    div1.appendChild(div3);
	    div3.appendChild(DOWN_ARROW.createElement());

	    tcell.appendChild(div1);
	    return tcell;

	}

	private TableCellElement makeMetadataTableCellElement(MetadataCol column) {
	    TableCellElement tcell = makeTableCellElement();
	    setText(tcell, column.attr);
	    tcell.setRowSpan(2);
	    return tcell;
	}

	private TableCellElement makeAttributeTableCellElement(
		AttributeCol column) {
	    TableCellElement tcell = makeTableCellElement();
	    setText(tcell, column.attr);
	    tcell.setRowSpan(2);
	    return tcell;
	}

	private TableCellElement makeConditionFactTypeTableCellElement(
		ConditionCol column) {
	    TableCellElement tcell = makeTableCellElement();
	    setText(tcell, column.getFactType());
	    return tcell;
	}

	private TableCellElement makeConditionFactFieldTableCellElement(
		ConditionCol column) {
	    TableCellElement tcell = makeTableCellElement();
	    setText(tcell, column.getFactField());
	    return tcell;
	}

	private TableCellElement makeActionTableCellElement(ActionCol column) {
	    TableCellElement tcell = makeTableCellElement();
	    setText(tcell, column.getHeader());
	    tcell.setRowSpan(2);
	    return tcell;
	}

	private void setText(TableCellElement cell, String text) {
	    cell.getFirstChild().getFirstChild().<DivElement> cast()
		    .setInnerText(text);
	}

    }

}
