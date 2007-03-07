/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author: cmalley $
 * Revision : $Revision: 1484 $
 * Date modified : $Date: 2004-12-10 17:16:34 -0700 (Fri, 10 Dec 2004) $
 */
package edu.colorado.phet.mechanics;

import edu.colorado.phet.common_1200.math.Vector2D;
import edu.colorado.phet.common_1200.model.Particle;

import java.awt.geom.Point2D;

/**
 * Body
 *
 * @author Ron LeMaster
 * @version $Revision: 1484 $
 */
public abstract class Body extends Particle {

    private Particle lastColidedBody = null;
    private double theta;
    private double omega;
    private double alpha;
    private double prevAlpha;
    private double mass;

    protected Body() {
    }

    protected Body( Point2D location, Vector2D velocity,
//    protected Body( Point2D.Double location, Vector2D velocity,
                    Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration );
        setMass( mass );
    }

    public abstract Point2D.Double getCM();

    public abstract double getMomentOfInertia();

    public double getKineticEnergy() {
        return ( getMass() * getVelocity().getMagnitudeSq() / 2 ) +
               getMomentOfInertia() * omega * omega / 2;
    }

    /**
     * @param dt
     */
    public void stepInTime( double dt ) {
        // New orientation
        theta = theta + dt * omega + dt * dt * alpha / 2;
        // New angular velocity
        omega = omega + dt * ( alpha + prevAlpha ) / 2;
        // Track angular acceleration
        prevAlpha = alpha;

        super.stepInTime( dt );
    }

    public double getSpeed() {
        return getVelocity().getMagnitude();    
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta( double theta ) {
        this.theta = theta;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega( double omega ) {
        this.omega = omega;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha( double alpha ) {
        this.alpha = alpha;
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double mass ) {
        this.mass = mass;
    }

    /**
     * @deprecated
     * @return
     */
    public Particle getLastColidedBody() {
        return lastColidedBody;
    }

    /**
     * @deprecated
     */
    public void setLastColidedBody( Particle lastColidedBody ) {
        this.lastColidedBody = lastColidedBody;
    }
}
