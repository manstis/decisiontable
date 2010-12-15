package org.drools.guvnor.decisiontable.client.widget.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for the Decision Table.
 * 
 * @author manstis
 * 
 */
public interface CellTableResource extends ClientBundle {

    public interface CellTableStyle extends CssResource {

	int rowHeight();
	
	int rowHeaderHeight();

	String cellTable();
	
	String cellTableEvenRow();

	String cellTableOddRow();

	String cellTableCell();
	
	String cellTableCellDiv();

	String headerTable();

	String headerRow();

	String headerCellPrimary();

	String headerCellSecondary();

	String headerContainer();

	String headerText();

	String headerWidget();
	
	String spacer();
	
	String selectorAddCell();
	
	String selectorDeleteCell();

	String selectorControl();
	
	String selectorAddImage();

	String selectorDeleteImage();
	
	String selectorToggle();
	
    };

    @Source("downArrow.png")
    ImageResource downArrow();

    @Source("smallDownArrow.png")
    ImageResource smallDownArrow();

    @Source("upArrow.png")
    ImageResource upArrow();

    @Source("smallUpArrow.png")
    ImageResource smallUpArrow();

    @Source("columnPicker.png")
    ImageResource columnPicker();

    @Source("mergeLink.png")
    ImageResource mergeLink();

    @Source("mergeUnlink.png")
    ImageResource mergeUnlink();

    @Source("toggleSelected.png")
    ImageResource toggleSelected();

    @Source("toggleDeselected.png")
    ImageResource toggleDeselected();

    @Source({ "CellTable.css" })
    CellTableStyle cellTableStyle();

};