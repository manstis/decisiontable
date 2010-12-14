package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalDecisionTableSidebarWidget extends
	DecisionTableSidebarWidget {

    private ScrollPanel scrollPanel;
    private VerticalPanel container;
    private VerticalSelectorWidget selectors;

    /**
     * Construct a "Sidebar" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableSidebarWidget(DecisionTableWidget dtable) {
	super(dtable);

	style.ensureInjected();

	// Construct the Widget
	scrollPanel = new ScrollPanel();
	container = new VerticalPanel();
	selectors = new VerticalSelectorWidget();

	container.add(new VerticalSideBarSpacerWidget(
		style.rowHeaderHeight() * 2));
	container.add(scrollPanel);
	scrollPanel.add(selectors);

	// We don't want scroll bars on the Sidebar
	scrollPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

	initWidget(container);

    }

    @Override
    public void setHeight(String height) {
	super.setHeight(height);
	this.scrollPanel.setHeight(height);
    }

    @Override
    public void setScrollPosition(int position) {
	this.scrollPanel.setScrollPosition(position);
    }

    @Override
    public void initialise() {
	selectors.clear();
    }

    @Override
    public void addSelector() {
	selectors.add("hello");
    }

    /**
     * Simple spacer to ensure scrollable part of sidebar aligns with grid
     * 
     * @author manstis
     * 
     */
    private static class VerticalSideBarSpacerWidget extends Widget {

	private VerticalSideBarSpacerWidget(int height) {
	    // Use a TABLE to ensure alignment with header and grid
	    TableElement table = Document.get().createTableElement();
	    TableSectionElement tbody = Document.get().createTBodyElement();
	    TableRowElement tre = Document.get().createTRElement();
	    TableCellElement tce = Document.get().createTDElement();

	    table.setClassName(style.spacer());
	    table.setCellPadding(0);
	    table.setCellSpacing(0);

	    table.appendChild(tbody);
	    tbody.appendChild(tre);
	    tre.appendChild(tce);

	    setElement(table);
	}

    }

    private class VerticalSelectorWidget extends Widget {

	private TableElement table;
	private TableSectionElement tbody;
	private int rows;

	private VerticalSelectorWidget() {
	    this.table = Document.get().createTableElement();
	    this.table.setCellPadding(0);
	    this.table.setCellSpacing(0);
	    this.tbody = Document.get().createTBodyElement();
	    this.table.appendChild(tbody);
	    setElement(table);
	    rows = 0;

	    sinkEvents(Event.getTypeInt("click"));
	}

	private void clear() {
	    this.table.removeChild(table.getFirstChild());
	    this.tbody = Document.get().createTBodyElement();
	    this.table.appendChild(tbody);
	    rows = 0;
	}

	private void add(String text) {

	    boolean isEven = rows % 2 == 0;
	    String trClasses = isEven ? style.cellTableEvenRow() : style
		    .cellTableOddRow();
	    String tdAddClasses = style.selectorAddCell();
	    String tdDeleteClasses = style.selectorDeleteCell();

	    TableRowElement tre = Document.get().createTRElement();
	    tre.setClassName(trClasses);
	    tre.getStyle().setHeight(style.rowHeight(), Unit.PX);

	    TableCellElement tce1 = Document.get().createTDElement();
	    tce1.setClassName(tdAddClasses);
	    DivElement divAdd = Document.get().createDivElement();
	    divAdd.setClassName(style.selectorAddImage());
	    divAdd.setInnerHTML("&nbsp");
	    tce1.appendChild(divAdd);

	    TableCellElement tce2 = Document.get().createTDElement();
	    tce2.setClassName(tdDeleteClasses);
	    DivElement divDelete = Document.get().createDivElement();
	    divDelete.setClassName(style.selectorDeleteImage());
	    divDelete.setInnerHTML("&nbsp");
	    tce2.appendChild(divDelete);

	    tre.appendChild(tce1);
	    tre.appendChild(tce2);
	    tbody.appendChild(tre);
	    rows++;
	}

	@SuppressWarnings("unused")
	@Override
	public void onBrowserEvent(Event event) {
	    // Get the event target
	    String eventType = event.getType();
	    EventTarget eventTarget = event.getEventTarget();
	    if (!Element.is(eventTarget)) {
		return;
	    }
	    Element target = event.getEventTarget().cast();

	    // Find the cell where the event occurred
	    TableCellElement cell = findNearestParentTableCellElement(target);
	    if (cell == null) {
		return;
	    }

	    boolean isClick = eventType.equals("click");
	    int column = cell.getCellIndex();
	    if (isClick) {
		// TODO Perform action!
	    }

	}

	// Find the TD that contains the element
	private TableCellElement findNearestParentTableCellElement(Element elem) {
	    while ((elem != null) && (elem != table)) {
		String tagName = elem.getTagName();
		if ("td".equalsIgnoreCase(tagName)) {
		    return elem.cast();
		}
		elem = elem.getParentElement();
	    }
	    return null;
	}

    }

}
