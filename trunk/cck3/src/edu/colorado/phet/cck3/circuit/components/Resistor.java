/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class Resistor extends CircuitComponent {
    public Resistor( Point2D start, AbstractVector2D dir, double length, double height, KirkhoffListener kl ) {
        super( kl, start, dir, length, height );
        setResistance( 10 );
    }
}
