// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

//REVIEW
//   Is this really a memento pattern? I don't see any rollback/undo being done. Body is the originator, and this aggregates the Body state. But who is the caretaker in this case?
//   This is also more than state, this class contains the logic for computing the next state.

/**
 * ModelState represents an immutable memento of the entire physical state, for performing the numerical integration.
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
            ImmutableVector2D newPosition = bodyState.position.getAddedInstance( bodyState.velocity.getScaledInstance( dt ) ).getAddedInstance( bodyState.acceleration.getScaledInstance( dt * dt / 2 ) );
            ImmutableVector2D newVelocityHalfStep = bodyState.velocity.getAddedInstance( bodyState.acceleration.getScaledInstance( dt / 2 ) );
            ImmutableVector2D newAcceleration = getForce( bodyState, newPosition, gravityEnabledProperty ).getScaledInstance( -1.0 / bodyState.mass );
            ImmutableVector2D newVelocity = newVelocityHalfStep.getAddedInstance( newAcceleration.getScaledInstance( dt / 2.0 ) );
            newState.add( new BodyState( newPosition, newVelocity, newAcceleration, bodyState.mass, bodyState.exploded ) );
        }
        return new ModelState( newState );
    }

    //TODO: limit distance so forces don't become infinite

    private ImmutableVector2D getForce( BodyState source, BodyState target, ImmutableVector2D newTargetPosition ) {
        if ( source.position.equals( newTargetPosition ) ) {//If they are on top of each other, force should be infinite, but ignore it since we want to have semi-realistic behavior
            return new ImmutableVector2D();
        }
        else if ( source.exploded ) { //ignore in the computation if that body has exploded
            return new ImmutableVector2D();
        }
        else {
            return getUnitVector( source, newTargetPosition ).getScaledInstance( GravityAndOrbitsModule.G * source.mass * target.mass / source.distanceSquared( newTargetPosition ) );
        }
    }

    private ImmutableVector2D getUnitVector( BodyState source, ImmutableVector2D newPosition ) {
        return newPosition.getSubtractedInstance( source.position ).getNormalizedInstance();
    }

    //REVIEW Why is this public? It's sole use is in ModelState.getNextState.
    /*
     * Get the force on body at its proposed new position, unconventional but necessary for velocity verlet.
     */
    public ImmutableVector2D getForce( BodyState target, ImmutableVector2D newTargetPosition, Property<Boolean> gravityEnabledProperty ) {
        ImmutableVector2D sum = new ImmutableVector2D(); //zero vector, for no gravity
        if ( gravityEnabledProperty.getValue() ) {
            for ( BodyState source : bodyStates ) {
                if ( source != target ) {
                    sum = sum.getAddedInstance( getForce( source, target, newTargetPosition ) );
                }
            }
        }
        return sum;
    }

    //REVIEW getState(Body body) would be safer. See related REVIEW comment in GravityAndOrbitsModel constructor.
    public BodyState getBodyState( int index ) {
        return bodyStates.get( index );
    }
}
