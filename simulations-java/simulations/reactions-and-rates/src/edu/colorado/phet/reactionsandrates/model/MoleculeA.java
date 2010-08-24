/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

import java.awt.geom.Point2D;

/**
 * MoleculeA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeA extends SimpleMolecule {
    private static double RADIUS = 12;

    public static double getRADIUS() {
        return RADIUS;
    }

    public MoleculeA() {
        super( RADIUS );
    }

    public MoleculeA( Point2D location, Vector2DInterface velocity, Vector2DInterface acceleration, double mass, double charge ) {
        super( RADIUS, location, velocity, acceleration, mass, charge );
    }


    public Object clone() {
        return super.clone();
    }
}
