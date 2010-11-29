package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class HeaderCell extends AbstractCell<String> {

    private static final int ROW_HEIGHT = 32;

    protected List<DynamicEditColumn> columns = new ArrayList<DynamicEditColumn>();

    private static Template template;

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

    public HeaderCell(List<DynamicEditColumn> columns) {
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

    protected StringBuffer makeColumnHeader(int iRow, DynamicEditColumn column) {
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
	    sb.append(template.metaColumnHeader(ROW_HEIGHT * 2, column.attr)
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
