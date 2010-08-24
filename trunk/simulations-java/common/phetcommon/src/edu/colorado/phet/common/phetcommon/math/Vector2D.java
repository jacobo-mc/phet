package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
* Created by IntelliJ IDEA.
* User: Sam
* Date: Aug 24, 2010
* Time: 6:03:13 AM
* To change this template use File | Settings | File Templates.
*/
public class Vector2D extends ImmutableVector2D implements Vector2DInterface {
    public Vector2D() {
    }

    public Vector2D( AbstractVector2DInterface v ) {
        this( v.getX(), v.getY() );
    }

    public Vector2D( double x, double y ) {
        super( x, y );
    }

    public Vector2D( Point2D p ) {
        super( p );
    }

    public Vector2D( Point2D src, Point2D dst ) {
        super( src, dst );
    }

    public Vector2DInterface add( AbstractVector2DInterface v ) {
        setX( getX() + v.getX() );
        setY( getY() + v.getY() );
        return this;
    }

    public Vector2DInterface normalize() {
        double length = getMagnitude();
        if ( length == 0 ) {
            throw new RuntimeException( "Cannot normalize a zero-magnitude vector." );
        }
        return scale( 1.0 / length );
    }

    public Vector2DInterface scale( double scale ) {
        setX( getX() * scale );
        setY( getY() * scale );
        return this;
    }

    public void setX( double x ) {
        super.setX( x );
    }

    public void setY( double y ) {
        super.setY( y );
    }

    public void setComponents( double x, double y ) {
        setX( x );
        setY( y );
    }

    public Vector2DInterface subtract( AbstractVector2DInterface that ) {
        setX( getX() - that.getX() );
        setY( getY() - that.getY() );
        return this;
    }

    public Vector2DInterface rotate( double theta ) {
        double r = getMagnitude();
        double alpha = getAngle();
        double gamma = alpha + theta;
        double xPrime = r * Math.cos( gamma );
        double yPrime = r * Math.sin( gamma );
        this.setComponents( xPrime, yPrime );
        return this;
    }

    public String toString() {
        return "Vector2D.Double[" + getX() + ", " + getY() + "]";
    }
}
