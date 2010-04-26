/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * Model of a box (rectangular cuboid).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Box {

    private double width, height, depth;
    private final EventListenerList listeners;
    
    protected Box( double width, double height, double depth ) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        listeners = new EventListenerList();
    }
    
    protected void setShape( double width, double height, double depth ) {
        if ( width != this.width || height != this.height || depth != this.depth ) {
            double oldWidth = this.width;
            double oldHeight = this.height;
            double oldDepth = this.depth;
            this.width = width;
            this.height = height;
            this.depth = depth;
            fireShapeChanged( oldWidth, oldHeight, oldDepth, width, height, depth );
        }
    }
   
    protected void setWidth( double width ) {
        setShape( width, height, depth );
    }
    
    protected double getWidth() {
        return width;
    }
    
    protected void setHeight( double height ) {
        setShape( width, height, depth );
    }
   
    protected double getHeight() {
        return height;
    }
    
    // thickness is a synonym for height.
    protected void setThickness( double height ) {
        setHeight( height );
    }
    
    // thickness is a synonym for height.
    protected double getThickness() {
        return getHeight();
    }
    
    protected void setDepth( double depth ) {
        setShape( width, height, depth );
    }
    
    protected double getDepth() {
        return depth;
    }
    
    protected void setWidthAndDepth( double width, double depth ) {
        setShape( width, getHeight(), depth );
    }
    
    protected double getVolume() {
        return width * height * depth;
    }
    
    public interface BoxChangeListener extends EventListener {
        public void shapeChanged( double oldWidth, double oldHeight, double oldDepth, double newWidth, double newHeight, double newDepth );
    }
    
    public class BoxChangeAdapter implements BoxChangeListener {
        public void shapeChanged( double oldWidth, double oldHeight, double oldDepth, double newWidth, double newHeight, double newDepth ) {}
    }
    
    public void addBoxChangeListener( BoxChangeListener listener ) {
        listeners.add( BoxChangeListener.class, listener );
    }
    
    public void removeBoxChangeListener( BoxChangeListener listener ) {
        listeners.remove( BoxChangeListener.class, listener );
    }
    
    private void fireShapeChanged( double oldWidth, double oldHeight, double oldDepth, double newWidth, double newHeight, double newDepth ) {
        for ( BoxChangeListener listener : listeners.getListeners( BoxChangeListener.class ) ) {
            listener.shapeChanged( oldWidth, oldHeight, oldDepth, width, height, newDepth );
        }
    }
}
