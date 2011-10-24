// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.intro.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.common.model.masses.FireExtinguisher;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.common.model.masses.SmallTrashCan;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;

/**
 * Primary model class for the intro tab in the balancing act simulation.
 * This model depicts a plank on a fulcrum with a couple of masses that the
 * user can move around.
 *
 * @author John Blanco
 */
public class IntroModel extends BalanceModel {

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Map of the initial positions for the masses, used during resets.
    Map<Mass, Point2D> mapMassesToInitialPositions = new HashMap<Mass, Point2D>();

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public IntroModel() {
        // Add the initial masses and save their initial positions.
        Mass mass = new FireExtinguisher( false ) {{
            setPosition( 2.7, 0 );
        }};
        addMass( mass );
        mapMassesToInitialPositions.put( mass, mass.getPosition() );
        mass = new FireExtinguisher( false ) {{
            setPosition( 3.2, 0 );

        }};
        addMass( mass );
        mapMassesToInitialPositions.put( mass, mass.getPosition() );
        mass = new SmallTrashCan( false ) {{
            setPosition( 3.7, 0 );
        }};
        addMass( mass );
        mapMassesToInitialPositions.put( mass, mass.getPosition() );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    // Adds a mass to the model.
    @Override public UserMovableModelElement addMass( final Mass mass ) {
        mass.userControlled.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean newValue, Boolean oldValue ) {
                if ( newValue == false && oldValue == true ) {
                    // The user has dropped this mass.
                    if ( !plank.addMassToSurface( mass ) ) {
                        // The attempt to add mass to surface of plank failed,
                        // probably because mass was dropped somewhere other
                        // than over the plank.  Put the mass on the ground.
                        mass.setPosition( mass.getPosition().getX(), 0 );
                    }
                }
            }
        } );
        massList.add( mass );
        return mass;
    }

    public void reset() {
        super.reset();

        // Move each mass back to its original position.
        for ( Mass mass : new ArrayList<Mass>( massList ) ) {
            assert mapMassesToInitialPositions.containsKey( mass );  // Should have initial positions for all masses.
            mass.setPosition( mapMassesToInitialPositions.get( mass ) );
        }
    }
}