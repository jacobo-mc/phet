/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.Vector2D;


/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:57 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeFall extends ForceMode {
    double gravity;

    public FreeFall( double gravity ) {
        this.gravity = gravity;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        setNetForce( new Vector2D.Double( 0, body.getMass() * gravity ) );
        super.stepInTime( model, body, dt );
    }
}
