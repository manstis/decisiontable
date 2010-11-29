package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.decisiontable.client.guvnor.SortDirection;
import org.drools.guvnor.decisiontable.client.guvnor.TableImageResources;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ScrollPanel;

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

    // The single cell
    private Header<String> header = null;

    // A CellTable to display the "Header"
    private CellTable<String> table = new CellTable<String>();

    /**
     * Construct a "Header" for the provided DecisionTable
     * 
     * @param decisionTable
     */
    public VerticalDecisionTableHeaderWidget(DecisionTableWidget decisionTable) {
	this.decisionTable = decisionTable;

	// We don't need any data in the table; as everything is handled by the
	// Header
	this.header = new Header<String>(new HeaderCell(
		this.decisionTable.columns)) {

	    @Override
	    public String getValue() {
		return "throw-away";
	    }

	};
	// IdentityColumn chosen for no particular reason
	IdentityColumn<String> col = new IdentityColumn<String>(new TextCell());
	table.addColumn(col, header);

	// Construct the Widget
	panel = new ScrollPanel();

	// We don't want scroll bars on the Header
	panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

	panel.add(table);
	table.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
	table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
	table.setRowData(0, new ArrayList<String>());
	table.setRowCount(0);
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
    protected void setScrollPosition(int position) {
	((ScrollPanel) this.panel).setHorizontalScrollPosition(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
     */
    @Override
    public void setWidth(String width) {
	super.setWidth(width);
	this.panel.setWidth(width);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
     * #redraw()
     */
    @Override
    public void redraw() {
	this.table.redrawHeaders();
    }

    /**
     * This is our Super Hero Header Cell.
     * 
     * @author manstis
     * 
     */
    static class HeaderCell extends AbstractCell<String> {

	// OK, should probably expose as a property
	private static final int ROW_HEIGHT = 32;

	// The columns to be rendered in the Header. This is maintain by
	// SelectionManager when columns are inserted
	protected List<DynamicEditColumn> columns = new ArrayList<DynamicEditColumn>();
	protected List<SortDirection> sortDirections = new ArrayList<SortDirection>();

	private static Template template;

	// HTML Templates for the different cells
	interface Template extends SafeHtmlTemplates {
	    @Template("<td rowspan=\"2\" class=\"generalHeaderCell\" style=\"height:{0}px\">{1}</td>")
	    SafeHtml metaColumnHeader(int height, String value);

	    @Template("<td rowspan=\"2\" class=\"generalHeaderCell\" style=\"height:{0}px\">{1}</td>")
	    SafeHtml attributeColumnHeader(int height, String value);

	    @Template("<td class=\"factTypeHeaderCell\" style=\"height:{0}px\">{1}</td>")
	    SafeHtml conditionFactTypeColumnHeader(int height, String value);

	    @Template("<td class=\"generalHeaderCell\" style=\"height:{0}px\">{1}</td>")
	    SafeHtml conditionFactFieldColumnHeader(int height, String value);

	    @Template("<td rowspan=\"2\" class=\"generalHeaderCell\" style=\"height:{0}px\">{1}</td>")
	    SafeHtml actionColumnHeader(int height, String value);
	}

	// Images for sorting (to be implemented). This uses the same resources
	// as those used in Ge0ffrey's new Guvnor tables
	private static final TableImageResources TABLE_IMAGE_RESOURCES = GWT
		.create(TableImageResources.class);
	private static final String DOWN_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.downArrow());
	private static final String SMALL_DOWN_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.smallDownArrow());
	private static final String UP_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.upArrow());
	private static final String SMALL_UP_ARROW = makeImage(TABLE_IMAGE_RESOURCES
		.smallUpArrow());

	private static String makeImage(ImageResource resource) {
	    AbstractImagePrototype prototype = AbstractImagePrototype
		    .create(resource);
	    return prototype.getHTML();
	}

	/**
	 * Construct the Header Cell
	 * @param columns
	 */
	HeaderCell(List<DynamicEditColumn> columns) {
	    this.columns = columns;

	    if (template == null) {
		template = GWT.create(Template.class);
	    }
	}

	@Override
	public void render(String value, Object key, SafeHtmlBuilder sb) {
	    StringBuffer header = new StringBuffer();
	    header.append("<table class=\"headerTable\" cellspacing=\"0\" cellpadding=\"0\">");
	    header.append("<tr>");
	    for (DynamicEditColumn column : columns) {
		header.append(makeColumnHeader(0, column));
	    }
	    header.append("</tr><tr>");
	    for (DynamicEditColumn column : columns) {
		header.append(makeColumnHeader(1, column));
	    }
	    header.append("</tr><table>");
	    SafeHtml html = SafeHtmlUtils.fromTrustedString(header.toString());
	    sb.append(html);
	}

	protected StringBuffer makeColumnHeader(int iRow,
		DynamicEditColumn column) {
	    StringBuffer sb = new StringBuffer();
	    DTColumnConfig modelColumn = column.getModelColumn();
	    if (modelColumn instanceof MetadataCol) {
		sb.append(makeMetaColumnHeader(iRow, (MetadataCol) modelColumn));
	    } else if (modelColumn instanceof AttributeCol) {
		sb.append(makeAttributeColumnHeader(iRow,
			(AttributeCol) modelColumn));
	    } else if (modelColumn instanceof ConditionCol) {
		sb.append(makeConditionColumnHeader(iRow,
			(ConditionCol) modelColumn));
	    } else if (modelColumn instanceof ActionCol) {
		sb.append(makeActionColumnHeader(iRow, (ActionCol) modelColumn));
	    }
	    return sb;
	}

	protected StringBuffer makeMetaColumnHeader(int iRow, MetadataCol column) {
	    StringBuffer sb = new StringBuffer();
	    switch (iRow) {
	    case 0:
		sb.append(template
			.metaColumnHeader(ROW_HEIGHT * 2, column.attr)
			.asString());
		break;
	    case 1:
		// Nothing for the second row
	    }
	    return sb;
	}

	protected StringBuffer makeAttributeColumnHeader(int iRow,
		AttributeCol column) {
	    StringBuffer sb = new StringBuffer();
	    switch (iRow) {
	    case 0:
		sb.append(template.attributeColumnHeader(ROW_HEIGHT * 2,
			column.attr).asString());
		break;
	    case 1:
		// Nothing for the second row
	    }
	    return sb;
	}

	protected StringBuffer makeConditionColumnHeader(int iRow,
		ConditionCol column) {
	    StringBuffer sb = new StringBuffer();
	    switch (iRow) {
	    case 0:
		sb.append(template.conditionFactTypeColumnHeader(ROW_HEIGHT,
			column.getFactType()).asString());
		break;
	    case 1:
		sb.append(template.conditionFactFieldColumnHeader(ROW_HEIGHT,
			column.getFactField()).asString());
	    }
	    return sb;
	}

	protected StringBuffer makeActionColumnHeader(int iRow, ActionCol column) {
	    StringBuffer sb = new StringBuffer();
	    switch (iRow) {
	    case 0:
		sb.append(template.actionColumnHeader(ROW_HEIGHT * 2,
			column.getHeader()).asString());
		break;
	    case 1:
		// Nothing for the second row
	    }
	    return sb;
	}
    }

}
