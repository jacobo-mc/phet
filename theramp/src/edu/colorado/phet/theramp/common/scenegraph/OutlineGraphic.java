/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:06:14 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class OutlineGraphic extends AbstractGraphic {
    private Shape shape;

    public OutlineGraphic( Shape shape ) {
        this( shape, new BasicStroke( 1.0f ) );
    }

    public OutlineGraphic( Shape shape, Stroke stroke ) {
        this.shape = shape;
        setStroke( stroke );
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        graphics2D.draw( shape );
        super.restore( graphics2D );
    }

    public boolean containsLocal( double x, double y ) {
        return super.getStroke() != null && shape != null && getStroke().createStrokedShape( shape ).contains( x, y );//todo this will be slow.
    }

    public Rectangle2D getLocalBounds() {
        return getStroke().createStrokedShape( shape ).getBounds2D();
    }
}
