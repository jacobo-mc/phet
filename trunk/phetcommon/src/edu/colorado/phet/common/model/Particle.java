/**
 * Class: Particle
 * Package: edu.colorado.phet.mechanics
 * Author: Another Guy
 * Date: Aug 22, 2003
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Point2D;

public class Particle extends SimpleObservable implements ModelElement {
    private Point2D.Double position = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Double();
    private Vector2D acceleration = new Vector2D.Double();
    //Relying on this being null for the first iteration.
    private Vector2D prevAcceleration;

    protected Particle() {
    }

    protected Particle( Point2D.Double position, Vector2D velocity,
                        Vector2D acceleration ) {
        setPosition( position );
        setVelocity( velocity );
        setAcceleration( acceleration );
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition( double x, double y ) {
        position.setLocation( x, y );
        notifyObservers();
    }

    public void setPosition( Point2D.Double position ) {
        setPosition( position.x, position.y );
    }

    public AbstractVector2D getVelocity() {
        return velocity;
    }

    public void setVelocity( Vector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public void setVelocity( double vx, double vy ) {
        velocity.setComponents( vx, vy );
        notifyObservers();
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    public void setAcceleration( Vector2D acceleration ) {
        setAcceleration( acceleration.getX(), acceleration.getY() );
    }

    public void setAcceleration( double ax, double ay ) {
        setAccelerationNoUpdate( ax, ay );
        notifyObservers();
    }

    public void setAccelerationNoUpdate( double ax, double ay ) {
        this.prevAcceleration.setComponents( acceleration.getX(), acceleration.getY() );
        this.acceleration.setComponents( ax, ay );
    }

    /**
     * Determines the new state of the body using the Verlet method
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        // New position
        double xNew = position.getX()
                      + dt * velocity.getX()
                      + dt * dt * acceleration.getX() / 2;
        double yNew = position.getY()
                      + dt * velocity.getY()
                      + dt * dt * acceleration.getY() / 2;
        //        setPosition( xNew, yNew );
        position.setLocation( xNew, yNew );

        // New velocity
        if( prevAcceleration == null ) {
            prevAcceleration = acceleration;
        }
        double vxNew = velocity.getX() + dt * ( acceleration.getX() + prevAcceleration.getX() ) / 2;
        double vyNew = velocity.getY() + dt * ( acceleration.getY() + prevAcceleration.getY() ) / 2;
        velocity.setComponents( vxNew, vyNew );

        // New acceleration
        prevAcceleration.setComponents( acceleration.getX(), acceleration.getY() );

        this.notifyObservers();
    }
}

