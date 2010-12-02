package org.drools.guvnor.decisiontable.client.widget.cell.renderers;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A Popup Text Editor.
 * 
 * @author manstis
 * 
 */
public class PopupTextEditCell extends AbstractEditableCell<String, String> {

    private int offsetX = 10;
    private int offsetY = 10;
    private Object lastKey;
    private Element lastParent;
    private String lastValue;
    private PopupPanel panel;
    private TextBox textBox;
    private final SafeHtmlRenderer<String> renderer;
    private ValueUpdater<String> valueUpdater;

    public PopupTextEditCell() {
	this(SimpleSafeHtmlRenderer.getInstance());
    }

    public PopupTextEditCell(SafeHtmlRenderer<String> renderer) {
	super("click", "keydown");
	if (renderer == null) {
	    throw new IllegalArgumentException("renderer == null");
	}
	this.renderer = renderer;
	this.textBox = new TextBox();

	// Pressing ESCAPE dismisses the pop-up loosing any changes
	this.panel = new PopupPanel(true, true) {
	    @Override
	    protected void onPreviewNativeEvent(NativePreviewEvent event) {
		if (Event.ONKEYUP == event.getTypeInt()) {
		    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
			panel.hide();
		    }
		}
	    }
	};

	// Closing the pop-up commits the change
	panel.addCloseHandler(new CloseHandler<PopupPanel>() {
	    public void onClose(CloseEvent<PopupPanel> event) {
		lastKey = null;
		lastValue = null;
		if (lastParent != null && !event.isAutoClosed()) {
		    lastParent.focus();
		} else if (event.isAutoClosed()) {
		    commit();
		}
		lastParent = null;
	    }
	});

	// Loss of focus on the Textbox commits the change
	textBox.addBlurHandler(new BlurHandler() {

	    @Override
	    public void onBlur(BlurEvent event) {
		commit();
	    }

	});

	// Pressing ENTER commits the change
	textBox.addKeyDownHandler(new KeyDownHandler() {

	    @Override
	    public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		if (keyCode == KeyCodes.KEY_ENTER) {
		    commit();
		}
	    }

	});
	panel.add(textBox);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractEditableCell#isEditing(com.google.
     * gwt.dom.client.Element, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean isEditing(Element parent, String value, Object key) {
	return lastKey != null && lastKey.equals(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt
     * .dom.client.Element, java.lang.Object, java.lang.Object,
     * com.google.gwt.dom.client.NativeEvent,
     * com.google.gwt.cell.client.ValueUpdater)
     */
    @Override
    public void onBrowserEvent(Element parent, String value, Object key,
	    NativeEvent event, ValueUpdater<String> valueUpdater) {
	super.onBrowserEvent(parent, value, key, event, valueUpdater);
	if (event.getType().equals("click")) {
	    onEnterKeyDown(parent, value, key, event, valueUpdater);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.AbstractCell#render(java.lang.Object,
     * java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    public void render(String value, Object key, SafeHtmlBuilder sb) {
	// Get the view data.
	String viewData = getViewData(key);
	if (viewData != null && viewData.equals(value)) {
	    clearViewData(key);
	    viewData = null;
	}

	String s = null;
	if (viewData != null) {
	    s = viewData;
	} else if (value != null) {
	    s = value;
	}
	if (s != null) {
	    if (s.length() > 8) {
		sb.append(renderer.render(s.substring(0, 8) + "..."));
	    } else {
		sb.append(renderer.render(s));
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#onEnterKeyDown(com.google.gwt
     * .dom.client.Element, java.lang.Object, java.lang.Object,
     * com.google.gwt.dom.client.NativeEvent,
     * com.google.gwt.cell.client.ValueUpdater)
     */
    @Override
    protected void onEnterKeyDown(final Element parent, String value,
	    Object key, NativeEvent event, ValueUpdater<String> valueUpdater) {
	this.lastKey = key;
	this.lastParent = parent;
	this.lastValue = value;
	this.valueUpdater = valueUpdater;

	String viewData = getViewData(key);
	String text = (viewData == null) ? value : viewData;
	textBox.setValue(text);
	panel.setPopupPositionAndShow(new PositionCallback() {
	    public void setPosition(int offsetWidth, int offsetHeight) {
		panel.setPopupPosition(parent.getAbsoluteLeft() + offsetX,
			parent.getAbsoluteTop() + offsetY);

		// Focus the Textbox
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

		    @Override
		    public void execute() {
			String text = textBox.getValue();
			textBox.setFocus(true);
			textBox.setCursorPos(text.length());
			textBox.setSelectionRange(0, text.length());
		    }

		});
	    }
	});
    }

    // Commit the change
    protected void commit() {
	// Hide pop-up
	Element cellParent = lastParent;
	String oldValue = lastValue;
	Object key = lastKey;
	panel.hide();

	// Update values
	String text = textBox.getValue();
	setViewData(key, text);
	setValue(cellParent, oldValue, key);
	if (valueUpdater != null) {
	    valueUpdater.update(text);
	}

    }

}
