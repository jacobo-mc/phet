/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * Class that represents an atom in the model.  This is used in the
 * microscopic view of photon abosorption.  This is an abstract class, and
 * it is expected that it be extended by specific atoms.
 * 
 * @author John Blanco
 */
public abstract class Atom extends SimpleObservable {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private Point2D position;
    private final Color representationColor;
    private final double radius;
    private final double mass;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    public Atom( Color representationColor, double radius, double mass, Point2D position ) {
        super();
        this.representationColor = representationColor;
        this.radius = radius;
        this.mass = mass;
        this.position = position;
    }
    
    public Atom( Color representationColor, double radius, double mass ) {
        this (representationColor, radius, mass, new Point2D.Double(0, 0));
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public Point2D getPosition() {
        return position;
    }
    
    public void setPosition( Point2D position ) {
        if (this.position != position){
            this.position = position;
            notifyObservers();
        }
    }
    
    public Color getRepresentationColor() {
        return representationColor;
    }
    
    public double getRadius() {
        return radius;
    }
    
    public double getMass() {
        return mass;
    }
    
    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
    
}
