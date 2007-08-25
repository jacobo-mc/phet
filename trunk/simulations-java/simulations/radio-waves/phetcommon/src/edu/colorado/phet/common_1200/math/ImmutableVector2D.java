/**
 * Class: ImmutableVector2D
 * Package: edu.colorado.phet.common.math
 * Author: Another Guy
 * Date: May 21, 2004
 */
package edu.colorado.phet.common_1200.math;

import java.awt.geom.Point2D;

public interface ImmutableVector2D extends AbstractVector2D {

    public class Double extends AbstractVector2D.Double implements ImmutableVector2D {
        public Double() {
        }

        public Double( double x, double y ) {
            super( x, y );
        }

        public Double( Vector2D v ) {
            super( v );
        }

        public Double( AbstractVector2D v ) {
            super( v );
        }

        public Double( Point2D p ) {
            super( p );
        }

        public Double( Point2D initialPt, Point2D finalPt ) {
            super( initialPt, finalPt );
        }

    }

}
