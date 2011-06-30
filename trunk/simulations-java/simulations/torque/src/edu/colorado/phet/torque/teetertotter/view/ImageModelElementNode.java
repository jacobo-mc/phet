// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.torque.teetertotter.model.weights.ImageWeight;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class defines a Piccolo node that represents a model element in the
 * view, and the particular model element that it represents contains an image
 * that is used in the representation.
 *
 * @author John Blanco
 */
public class ImageModelElementNode extends PNode {
    ModelViewTransform mvt;

    public ImageModelElementNode( final ModelViewTransform mvt, final ImageWeight weight ) {
        this.mvt = mvt;
        final PImage imageNode = new PImage();
        addChild( imageNode );
        // Observe image changes.
        weight.addImageChangeObserver( new VoidFunction1<BufferedImage>() {
            public void apply( BufferedImage image ) {
                imageNode.setScale( 1 );
                imageNode.setImage( image );
                double scalingFactor = Math.abs( mvt.modelToViewDeltaY( weight.getHeight() ) ) / imageNode.getFullBoundsReference().height;
                if ( scalingFactor > 2 || scalingFactor < 0.5 ) {
                    System.out.println( getClass().getName() + " - Warning: Scaling factor is too large or small, drawing size should be adjusted.  Scaling factor = " + scalingFactor );
                }
                imageNode.setScale( scalingFactor );
                updatePosition( weight.getPosition() );
            }
        } );
        // Observe position changes.
        weight.addPositionChangeObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D newPosition ) {
                updatePosition( newPosition );
            }
        } );
        // Make the cursor change on mouse over.
        addInputEventListener( new CursorHandler() );
        // Add the mouse event handler.
        addInputEventListener( new WeightDragHandler( weight, this, mvt ) );
    }

    private void updatePosition( Point2D position ) {
        setOffset( mvt.modelToViewX( position.getX() ) - getFullBoundsReference().width / 2,
                   mvt.modelToViewY( position.getY() ) - getFullBoundsReference().height );
    }
}
