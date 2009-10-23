/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Galactose extends SimpleSugar {

	public Galactose(Point2D initialPosition) {
		super(initialPosition, Color.ORANGE);
	}

    public Galactose(double x,double y) {
        this(new Point2D.Double(x,y));
    }

	public Galactose(){
		this(new Point2D.Double());
	}
}
