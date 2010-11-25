package org.drools.guvnor.decisiontable.client.widget;

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

	// Add column button
	Button btnAddColumn = new Button("Add Column", new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		dtable.clearSelection();
		dtable.addColumn();
	    }
	});

	// Insert Row control
	final NumberRequestor columnNumberWidget = new NumberRequestor(
		"Insert column before:");
	columnNumberWidget.setCommand(new Command() {

	    @Override
	    public void execute() {
		dtable.insertColumnBefore(columnNumberWidget.getValue());
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

	panel.add(btnAddColumn);
	panel.add(columnNumberWidget);
	panel.add(btnAddRow);
	panel.add(rowNumberWidget);
	panel.add(btnToggleMerging);
	initWidget(panel);

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
