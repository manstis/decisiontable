package org.drools.guvnor.decisiontable.client.widget;

import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Simple container for controls to manipulate a Decision Table
 * 
 * @author manstis
 * 
 */
public class DecisionTableControlsWidget extends Composite {

    private Panel panel = new HorizontalPanel();

    public DecisionTableControlsWidget(final DecisionTableWidget dtable) {

	// Add Metadata column button
	Button btnAddMetadataColumn = new Button("Add Metadata Column", new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		dtable.clearSelection();
		dtable.addColumn(getNewMetadataColumn());
	    }
	});

	// Add Attribute column button
	Button btnAddAttributeColumn = new Button("Add Attribute Column", new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		dtable.clearSelection();
		dtable.addColumn(getNewAttributeColumn());
	    }
	});

	// Add Condition column button
	Button btnAddConditionColumn = new Button("Add Condition Column", new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		dtable.clearSelection();
		dtable.addColumn(getNewConditionColumn());
	    }
	});

	// Add Action column button
	Button btnAddActionColumn = new Button("Add Action Column", new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		dtable.clearSelection();
		dtable.addColumn(getNewActionColumn());
	    }
	});
	
	// Insert Row control
	final NumberRequestor columnNumberWidget = new NumberRequestor(
		"Insert column before:");
	columnNumberWidget.setCommand(new Command() {

	    @Override
	    public void execute() {
		dtable.insertColumnBefore(getNewMetadataColumn(),
			columnNumberWidget.getValue());
	    }

	});
	// Add row button
	Button btnAddRow = new Button("Add Row", new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		dtable.clearSelection();
		dtable.addRow();
	    }
	});

	// Insert Row control
	final NumberRequestor rowNumberWidget = new NumberRequestor(
		"Insert row before:");
	rowNumberWidget.setCommand(new Command() {

	    @Override
	    public void execute() {
		dtable.insertRowBefore(rowNumberWidget.getValue());
	    }

	});

	// Toggle Merging button
	Button btnToggleMerging = new Button("Toggle merging",
		new ClickHandler() {

		    @Override
		    public void onClick(ClickEvent event) {
			dtable.clearSelection();
			dtable.toggleMerging();
		    }
		});

	VerticalPanel vp1 = new VerticalPanel();
	vp1.add(btnAddMetadataColumn);
	vp1.add(btnAddAttributeColumn);
	vp1.add(btnAddConditionColumn);
	vp1.add(btnAddActionColumn);
	panel.add(vp1);
	
	VerticalPanel vp2 = new VerticalPanel();
	vp2.add(columnNumberWidget);
	vp2.add(rowNumberWidget);
	panel.add(vp2);
	
	panel.add(btnAddRow);
	panel.add(btnToggleMerging);
	initWidget(panel);

    }

    private DTColumnConfig getNewMetadataColumn() {
	MetadataCol column = new MetadataCol();
	column.attr = "metadata";
	return column;
    }

    private DTColumnConfig getNewAttributeColumn() {
	AttributeCol column = new AttributeCol();
	column.attr = "attribute";
	return column;
    }

    private DTColumnConfig getNewConditionColumn() {
	ConditionCol column = new ConditionCol();
	column.setFactType("fact-type");
	column.setFactField("fact-field");
	return column;
    }

    private DTColumnConfig getNewActionColumn() {
	ActionCol column = new ActionCol();
	column.setHeader("action");
	return column;
    }
    
    /**
     * Control allowing entry of numerical value and button to invoke specified
     * Command.
     * 
     * @author manstis
     * 
     */
    private static class NumberRequestor extends Composite {

	private Panel panel = new VerticalPanel();
	private Button btnEnter = new Button();
	private TextBox txtNumber = new TextBox();

	NumberRequestor(final String buttonLabel) {
	    this.btnEnter.setText(buttonLabel);
	    this.txtNumber.setText("0");
	    this.txtNumber.setWidth("64px");
	    this.txtNumber.addKeyPressHandler(new KeyPressHandler() {

		private final RegExp pattern = RegExp.compile("\\d+");

		@Override
		public void onKeyPress(KeyPressEvent event) {
		    // Only numerical digits allowed
		    if (!pattern.test(String.valueOf(event.getCharCode()))) {
			event.preventDefault();
		    }
		}

	    });
	    panel.add(btnEnter);
	    panel.add(txtNumber);
	    initWidget(panel);
	}

	private void setCommand(final Command command) {
	    this.btnEnter.addClickHandler(new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
		    command.execute();
		}

	    });
	}

	private int getValue() {
	    String text = txtNumber.getText();
	    if (text.equals("")) {
		return 0;
	    }
	    return Integer.parseInt(text);
	}

    }

}
