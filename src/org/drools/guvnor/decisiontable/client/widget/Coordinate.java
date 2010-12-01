package org.drools.guvnor.decisiontable.client.widget;

/**
 * A coordinate
 * 
 * @author manstis
 * 
 */
public class Coordinate {
    private int row;
    private int col;
    private String displayString;

    Coordinate(int row, int col) {
	this.row = row;
	this.col = col;
	this.displayString = "(R" + row + ",C" + col + ")";
    }

    Coordinate(Coordinate c) {
	this.row = c.row;
	this.col = c.col;
	this.displayString = "(R" + c.row + ",C" + c.col + ")";
    }

    public int getRow() {
	return this.row;
    }

    public int getCol() {
	return this.col;
    }

    @Override
    public String toString() {
	return displayString;
    }

    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	}
	Coordinate c = (Coordinate) o;
	return c.col == col && c.row == row;
    }

    @Override
    public int hashCode() {
	return row * 29 + col;
    }

}