package org.drools.guvnor.decisiontable.client.guvnor;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Geoffrey De Smet
 */
public interface TableImageResources extends ClientBundle {

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

}
