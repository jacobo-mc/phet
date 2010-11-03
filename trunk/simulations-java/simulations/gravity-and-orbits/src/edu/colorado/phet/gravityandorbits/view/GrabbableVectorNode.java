package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class GrabbableVectorNode extends VectorNode {
    private PhetPPath grabArea;

    public GrabbableVectorNode( final Body body, final ModelViewTransform2D modelViewTransform2D, final Property<Boolean> visible, final Property<ImmutableVector2D> property, final double scale ) {
        super( body, modelViewTransform2D, visible, property, scale );
        final Point2D tip = getTip();
        grabArea = new PhetPPath( new Ellipse2D.Double( 0, 0, 40, 40 ), new Color( 0, 0, 0, 0 ), new BasicStroke( 1 ), Color.yellow ) {{
            setOffset( tip.getX() - getFullBounds().getWidth() / 2, tip.getY() - getFullBounds().getHeight() / 2 );
        }};
        final SimpleObserver updateGrabArea = new SimpleObserver() {
            public void update() {
                final Point2D tip = getTip();
                grabArea.setOffset( tip.getX() - grabArea.getFullBounds().getWidth() / 2, tip.getY() - grabArea.getFullBounds().getHeight() / 2 );
            }
        };
        property.addObserver( updateGrabArea );
        body.getPositionProperty().addObserver( updateGrabArea );
        addChild( grabArea );
        grabArea.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                Point2D position = event.getPositionRelativeTo( getParent() );
                Point2D model = modelViewTransform2D.viewToModel( position );
                ImmutableVector2D velocityVector = new ImmutableVector2D( model ).getSubtractedInstance( body.getPosition() );
                body.setVelocity( velocityVector.getScaledInstance( 1.0 / scale ) );
            }
        } );
        grabArea.addInputEventListener( new CursorHandler() );//todo: use same pattern as in body node so that mouse turns into cursor when arrow moves under stationary mouse?
    }
}
