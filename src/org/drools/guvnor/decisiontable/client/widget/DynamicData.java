package org.drools.guvnor.decisiontable.client.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple container for rows of data.
 * 
 * @author manstis
 * 
 */
public class DynamicData extends ArrayList<List<CellValue>> {

    private static final long serialVersionUID = -3710491920672816057L;

    public CellValue get(Coordinate c) {
	return this.get(c.getRow()).get(c.getCol());
    }

    public void set(Coordinate c, Object value) {
	this.get(c.getRow()).get(c.getCol()).setValue(value);
    }

}
