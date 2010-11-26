package org.drools.guvnor.decisiontable.client.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class HeaderCell extends AbstractCell<String> {

    private int columnCount=0;
    
    public void setColumnCount(int columnCount) {
	this.columnCount=columnCount;
    }
    
    @Override
    public void render(String value, Object key, SafeHtmlBuilder sb) {
	StringBuffer table = new StringBuffer();
	table.append("<table class=\"headerTable\" cellspacing=\"0\" cellpadding=\"0\">");
	table.append("<tr>");
	for(int iCol=0;iCol<columnCount;iCol++) {
	    table.append("<td class=\"headerCell\">"+iCol+"</td>");
	}
	table.append("</tr>");
	table.append("<tr>");
	for(int iCol=0;iCol<columnCount;iCol++) {
	    table.append("<td class=\"headerCell\">"+iCol+"</td>");
	}
	table.append("</tr>");
	table.append("<table>");
	SafeHtml html = SafeHtmlUtils.fromTrustedString(table.toString());
	sb.append(html);
    }
    
    

}
