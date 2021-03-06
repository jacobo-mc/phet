// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

/**
 * Class that represents a belt that connects two rotating wheels together,
 * like a fan belt in an automobile engine.
 *
 * @author John Blanco
 */
public class Belt {

    // Controls the shape of the belt.
    public final double wheel1Radius; // In meters.
    public final Vector2D wheel1Center;
    public final double wheel2Radius; // In meters.
    public final Vector2D wheel2Center;

    // Controls the visibility of the belt.
    public final BooleanProperty isVisible = new BooleanProperty( false );

    public Belt( double wheel1Radius, Vector2D wheel1Center, double wheel2Radius, Vector2D wheel2Center ) {
        this.wheel1Radius = wheel1Radius;
        this.wheel1Center = wheel1Center;
        this.wheel2Radius = wheel2Radius;
        this.wheel2Center = wheel2Center;
    }
}
