// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * ModelState represents an immutable representation of the entire physical state and code for performing the numerical integration which produces the next ModelState.
 * It is used by the GravityAndOrbitsModel to update the physics.
 *
 * @author Sam Reid
 */
public class ModelState {
    private ArrayList<BodyState> bodyStates;

    public ModelState( ArrayList<BodyState> bodyStates ) {
        this.bodyStates = bodyStates;
    }

    //Updates the model, producing the next ModelState
    public ModelState getNextState( double dt, int numSteps, Property<Boolean> gravityEnabledProperty ) {
        ModelState state = this;
        for ( int i = 0; i < numSteps; i++ ) {
            state = state.getNextState( dt / numSteps, gravityEnabledProperty );
        }
        return state;
    }

    //Updates the model, producing the next ModelState
    public ModelState getNextState( double dt, Property<Boolean> gravityEnabledProperty ) {
        //See http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
        ArrayList<BodyState> newState = new ArrayList<BodyState>();
        for ( BodyState bodyState : bodyStates ) {
            //Velocity Verlet (see svn history for Euler)
            Vector2D newPosition = bodyState.position.plus( bodyState.velocity.times( dt ) ).plus( bodyState.acceleration.times( dt * dt / 2 ) );
            Vector2D newVelocityHalfStep = bodyState.velocity.plus( bodyState.acceleration.times( dt / 2 ) );
            Vector2D newAcceleration = getForce( bodyState, newPosition, gravityEnabledProperty ).times( -1.0 / bodyState.mass );
            Vector2D newVelocity = newVelocityHalfStep.plus( newAcceleration.times( dt / 2.0 ) );
            newState.add( new BodyState( newPosition, newVelocity, newAcceleration, bodyState.mass, bodyState.exploded ) );
        }
        return new ModelState( newState );
    }

    //TODO: limit distance so forces don't become infinite

    private Vector2D getForce( BodyState source, BodyState target, Vector2D newTargetPosition ) {
        if ( source.position.equals( newTargetPosition ) ) {//If they are on top of each other, force should be infinite, but ignore it since we want to have semi-realistic behavior
            return new Vector2D();
        }
        else if ( source.exploded ) { //ignore in the computation if that body has exploded
            return new Vector2D();
        }
        else {
            return getUnitVector( source, newTargetPosition ).times( GravityAndOrbitsModule.G * source.mass * target.mass / source.distanceSquared( newTargetPosition ) );
        }
    }

    private Vector2D getUnitVector( BodyState source, Vector2D newPosition ) {
        return newPosition.minus( source.position ).normalized();
    }

    //Get the force on body at its proposed new position, unconventional but necessary for velocity verlet.
    private Vector2D getForce( BodyState target, Vector2D newTargetPosition, Property<Boolean> gravityEnabledProperty ) {
        Vector2D sum = new Vector2D(); //zero vector, for no gravity
        if ( gravityEnabledProperty.get() ) {
            for ( BodyState source : bodyStates ) {
                if ( source != target ) {
                    sum = sum.plus( getForce( source, target, newTargetPosition ) );
                }
            }
        }
        return sum;
    }

    //Get the BodyState for the specified index--future work could change this signature to getState(Body body) since it would be safer. See usage in GravityAndOrbitsModel constructor.
    public BodyState getBodyState( int index ) {
        return bodyStates.get( index );
    }
}
