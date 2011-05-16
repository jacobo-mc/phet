// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * A single solid crystal (sugar or salt) that comes from a shaker and gets dissolved in the water.
 *
 * @author Sam Reid
 */
public class MacroCrystal {
    public final double mass = 1E-6;//kg
    public Property<ImmutableVector2D> position;
    public Property<ImmutableVector2D> velocity = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    public Property<ImmutableVector2D> acceleration = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();

    private double moles;//The number of moles of the crystal.  We couldn't just count the number of atoms since it would overflow Long

    public MacroCrystal( ImmutableVector2D position ) {
        this( position, 1E-2 );
    }

    public MacroCrystal( ImmutableVector2D position, double moles ) {
        this.position = new Property<ImmutableVector2D>( position );
        this.moles = moles;
    }

    //propagate the crystal according to the specified applied forces, using euler integration
    public void stepInTime( ImmutableVector2D appliedForce, double dt ) {
        acceleration.set( appliedForce.times( 1.0 / mass ) );
        velocity.set( velocity.get().plus( acceleration.get().times( dt ) ) );
        position.set( position.get().plus( velocity.get().times( dt ) ) );
    }

    //Add a listener which will be notified when this crystal is removed from the model
    public void addRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.add( removalListener );
    }

    //Remove a removal listener
    public void removeRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.remove( removalListener );
    }

    //Notify all removal listeners that this crystal is being removed from the model
    public void remove() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
    }

    public double getMoles() {
        return moles;
    }
}
