/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;

public class GravityAndOrbitsModel {

    private final ArrayList<Body> bodies = new ArrayList<Body>();

    private final GravityAndOrbitsClock clock;
    private ArrayList<SimpleObserver> modelStepListeners = new ArrayList<SimpleObserver>();
    public boolean teacherMode;
    private final VoidFunction1<Double> stepModel;

    public GravityAndOrbitsModel( GravityAndOrbitsClock clock ) {
        super();
        this.clock = clock;

        stepModel = new VoidFunction1<Double>() {
            public void apply( Double dt ) {
                ModelState newState = new ModelState( new ArrayList<BodyState>() {{
                    for ( Body body : bodies ) {
                        add( body.toBodyState() );
                    }
                }} ).getNextState( dt, 1000 );
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).updateBodyStateFromModel( newState.getBodyState( i ) );
                }
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).allBodiesUpdated();
                }
//                System.out.println( "momentum: " + getTotalMomentum() );
            }
        };
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
//                if (teacherMode) return;
                final double dt = clockEvent.getSimulationTimeChange();
                stepModel.apply( dt );
                for ( SimpleObserver modelStepListener : modelStepListeners ) {
                    modelStepListener.update();
                }
            }
        } );
    }

    private ImmutableVector2D getTotalMomentum() {
        ImmutableVector2D total = new ImmutableVector2D();
        for ( Body body : bodies ) {
            total = total.getAddedInstance( body.getVelocity().getScaledInstance( body.getMass() ) );
        }
        return total;
    }

    public GravityAndOrbitsClock getClock() {
        return clock;
    }

    public void resetAll() {
        resetBodies();
        getClock().resetSimulationTime();
    }

    public void addModelSteppedListener( SimpleObserver simpleObserver ) {
        modelStepListeners.add( simpleObserver );
    }

    public void addBody( Body body ) {
        bodies.add( body );
    }

    public ArrayList<Body> getBodies() {
        return new ArrayList<Body>( bodies );
    }

    public void resetBodies() {
        for ( Body body : bodies ) {
            body.resetAll();
        }
    }
}
