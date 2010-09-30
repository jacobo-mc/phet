package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Observable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Abstract base class from which subatomic particles (e.g. electrons) extend.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class SubatomicParticle {

    private static final double MOTION_VELOCITY = 120; // In picometers per second of sim time.

    private final Observable<Point2D.Double> position;
    private final Point2D destination = new Point2D.Double();
    private final double radius;
    private final Observable<Boolean> userControlled;

    public SubatomicParticle( ConstantDtClock clock, double radius, double x, double y ) {
        this.radius = radius;
        position = new Observable<Point2D.Double>( new Point2D.Double( x, y ) );
        this.destination.setLocation( x, y );
        userControlled = new Observable<Boolean>( Boolean.FALSE );
        clock.addClockListener( new ClockAdapter() {

            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }

        } );
    }

    /**
     * @param simulationTimeChange
     */
    private void stepInTime( double dt ) {
        if ( getPosition().distance( destination ) != 0 ) {
            // Move towards the current destination.
            double distanceToTravel = MOTION_VELOCITY * dt;
            if ( distanceToTravel >= getPosition().distance( destination ) ) {
                // Closer than one step, so just go there.
                setPosition( destination );
            }
            else {
                // Move towards the destination.
                double angle = Math.atan2( destination.getY() - getPosition().getY(),
                        destination.getX() - getPosition().getX() );
                translate( distanceToTravel * Math.cos( angle ), distanceToTravel * Math.sin( angle ) );
            }
        }
    }

    public Point2D.Double getPosition() {
        return position.getValue();
    }

    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }

    public void setPosition( double x, double y ) {
        position.setValue( new Point2D.Double( x, y ) );
    }

    public void setDestination( Point2D position ) {
        setDestination( position.getX(), position.getY() );
    }

    public void setDestination( double x, double y ) {
        destination.setLocation( x, y );
    }

    public void setPositionAndDestination( double x, double y ) {
        setPosition( x, y );
        setDestination( x, y );
    }

    public void setPositionAndDestination( Point2D p ) {
        setPosition( p );
        setDestination( p );
    }

    public double getDiameter() {
        return getRadius() * 2;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isUserControlled() {
        return userControlled.getValue();
    }

    public void setUserControlled( boolean userControlled ) {
        if ( userControlled ) {
            this.userControlled.setValue( Boolean.TRUE );
        }
        else {
            this.userControlled.setValue( Boolean.FALSE );
        }
    }

    public void translate( double dx, double dy ) {
        setPosition( position.getValue().getX() + dx, position.getValue().getY() + dy );
    }

    public void reset() {
        position.reset();
    }

    public void addPositionListener( SimpleObserver listener ) {
        position.addObserver( listener );
    }

    public void removePositionListener( SimpleObserver listener ) {
        position.removeObserver( listener );
    }

    public void addUserControlListener( SimpleObserver listener ) {
        userControlled.addObserver( listener );
    }

    public void removeUserControlListener( SimpleObserver listener ) {
        userControlled.removeObserver( listener );
    }
}
