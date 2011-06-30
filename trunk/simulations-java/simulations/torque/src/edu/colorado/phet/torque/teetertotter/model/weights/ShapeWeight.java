// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * This class defines a weight in the model whose appearance is defined by
 * its shape as opposed to, say, and image.
 *
 * @author John Blanco
 */
public abstract class ShapeWeight extends Weight {
    public final Property<Shape> shapeProperty;

    public ShapeWeight( double mass, Shape shape ) {
        super( mass );
        shapeProperty = new Property<Shape>( shape );
    }
}
