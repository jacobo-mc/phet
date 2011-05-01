// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Physical model for the beaker
 *
 * @author Sam Reid
 */
public class Beaker {
    private final double width;
    private final double height;

    public Beaker( double width, double height ) {
        this.width = width;
        this.height = height;
    }

    public Shape getWallShape() {
        return new Rectangle2D.Double( 0, 0, width, height );
    }
}
