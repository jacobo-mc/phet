package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class PressureSensorNode extends PNode {
    public static final double hotSpotRadius = 3;

    /**
     * @param transform
     * @param sensor
     * @param pool           the area to constrain the node within or null if no constraints//TODO: redesign so this is not a problem
     * @param units
     */
    public PressureSensorNode( final ModelViewTransform transform, final PressureSensor sensor, final Pool pool, final Property<Units.Unit> units ) {
        addChild( new PhetPPath( new Ellipse2D.Double( -hotSpotRadius, -hotSpotRadius, hotSpotRadius * 2, hotSpotRadius * 2 ), Color.red ) );
        final PText textNode = new PText( getText( sensor, units ) ) {{
            setFont( new PhetFont( 18, true ) );
        }};
        addChild( textNode );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new RelativeDragHandler( this, transform, sensor.getLocationProperty(), new Function1<Point2D, Point2D>() {
            //TODO: Factor pool to subclass or general constraint method
            public Point2D apply( Point2D point2D ) {
                if ( pool != null ) {
                    final Point2D.Double pt = new Point2D.Double( point2D.getX(), Math.max( point2D.getY(), pool.getMinY() ) );
                    if ( pt.getY() < 0 ) {
                        pt.setLocation( MathUtil.clamp( pool.getMinX(), pt.getX(), pool.getMaxX() ), pt.getY() );
                    }
                    return pt;//not allowed to go to negative Potential Energy
                }
                else { return point2D; }
            }
        } ) );
        sensor.addPositionObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( sensor.getLocation().toPoint2D() ) );
            }
        } );
        final SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                textNode.setText( getText( sensor, units ) );
                textNode.setOffset( -textNode.getFullBounds().getWidth() / 2, -textNode.getFullBounds().getHeight() );
            }
        };
        sensor.addPressureObserver( updateText );
        units.addObserver( updateText );
    }

    private static String getText( PressureSensor pressureSensor, Property<Units.Unit> units ) {
        if ( Double.isNaN( pressureSensor.getPressure() ) ) {
            return "? " + units.getValue().getAbbreviation();
        }
        else {
            return "" + units.getValue().getDecimalFormat().format( units.getValue().siToUnit( pressureSensor.getPressure() ) ) + " " + units.getValue().getAbbreviation();
        }
    }
}
