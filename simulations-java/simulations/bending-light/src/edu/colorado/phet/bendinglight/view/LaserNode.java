// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.BendingLightApplication;
import edu.colorado.phet.bendinglight.model.Laser;
import edu.colorado.phet.common.phetcommon.math.ModelBounds;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.Or;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.*;

/**
 * Piccolo node for drawing the laser itself, including an on/off button and ability to rotate/translate.
 *
 * @author Sam Reid
 */
public class LaserNode extends PNode {
    boolean debug = false;

    public LaserNode( final ModelViewTransform transform,
                      final Laser laser,
                      final Property<Boolean> showRotationDragHandles,
                      final Property<Boolean> showTranslationDragHandles,
                      final Function1<Double, Double> clampDragAngle,
                      Function2<Shape, Shape, Shape> translationRegion,//Select from the entire region and front region which should be used for translating the laser.  Signature is (full region, front region)=>selection region
                      Function2<Shape, Shape, Shape> rotationRegion, //Select from the entire region and back region which should be used for rotating the laser.  Signature is (full region,back region)=> selected region
                      String imageName,
                      final ModelBounds modelBounds ) {//For making sure the laser doesn't get rotated out of sight
        //Load the image
        final BufferedImage image = flipY( flipX( BendingLightApplication.RESOURCES.getImage( imageName ) ) );

        //Properties to help identify where the mouse is so that arrows can be show indicating how the laser can be dragged
        final BooleanProperty mouseOverRotationPart = new BooleanProperty( false );
        final BooleanProperty mouseOverTranslationPart = new BooleanProperty( false );
        final BooleanProperty draggingRotation = new BooleanProperty( false );
        final BooleanProperty draggingTranslation = new BooleanProperty( false );

        //Continue to show the rotation arrows even if the mouse is outside of the region if the mouse is currently rotating the laser
        final Or showRotationArrows = mouseOverRotationPart.or( draggingRotation );
        showRotationArrows.addObserver( new SimpleObserver() {
            public void update() {
                showRotationDragHandles.set( showRotationArrows.get() );
            }
        } );

        //Continue to show the translation arrows even if the mouse is outside of the region if the mouse is currently translating the laser
        final Or doShowTranslationArrows = mouseOverTranslationPart.or( draggingTranslation );
        final And a = new And( doShowTranslationArrows, new Not( showRotationArrows ) );
        a.addObserver( new SimpleObserver() {
            public void update() {
                showTranslationDragHandles.set( doShowTranslationArrows.get() );
            }
        } );

        //Show the laser image
        addChild( new PImage( image ) );

        //Drag handlers can choose which of these regions to use for drag events
        double fractionBackToRotateHandle = 34.0 / 177.0;//for the rotatable laser, just use the part of the image that looks like a knob be used for rotation
        Rectangle2D.Double frontRectangle = new Rectangle2D.Double( 0, 0, image.getWidth() * ( 1 - fractionBackToRotateHandle ), image.getHeight() );
        Rectangle2D.Double backRectangle = new Rectangle2D.Double( image.getWidth() * ( 1 - fractionBackToRotateHandle ), 0, image.getWidth() * fractionBackToRotateHandle, image.getHeight() );
        Rectangle2D.Double fullRectangle = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );

        //A drag region is an invisible shape that can be dragged with the mouse for translation or rotation
        class DragRegion extends PhetPPath {
            DragRegion( Shape shape, Paint fill, final VoidFunction1<DragEvent> eventHandler, final BooleanProperty isMouseOver,
                        final BooleanProperty isDragging,
                        final VoidFunction0 dropped//function that will be called when the laser gets dropped, e.g., to ensure it is in a good bounds
            ) {
                super( shape, fill );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new CanvasBoundedDragHandler( LaserNode.this ) {
                    //Pass the event through to the eventHandler
                    @Override protected void dragNode( DragEvent event ) {
                        eventHandler.apply( event );
                    }

                    public void mouseEntered( PInputEvent event ) {
                        super.mouseEntered( event );//call the super since we extend BoundedDragHandler
                        isMouseOver.set( true );
                    }

                    public void mouseExited( PInputEvent event ) {
                        super.mouseExited( event );//call the super since we extend BoundedDragHandler
                        isMouseOver.set( false );
                    }

                    public void mouseReleased( PInputEvent event ) {
                        super.mouseReleased( event );//call the super since we extend BoundedDragHandler
                        isDragging.set( false );

                        //Signify that the laser was dropped so it can be bounds tested.
                        dropped.apply();
                    }

                    public void mousePressed( PInputEvent event ) {
                        super.mousePressed( event );//call the super since we extend BoundedDragHandler
                        isDragging.set( true );
                    }
                } );
            }
        }

        //Set up the colors to be invisible (or red and blue for debugging)
        final Color dragRegionColor;
        final Color rotationRegionColor;
        if ( debug ) {
            dragRegionColor = new Color( 255, 0, 0, 128 );
            rotationRegionColor = new Color( 0, 0, 255, 128 );
        }
        else {
            dragRegionColor = new Color( 255, 0, 0, 0 );
            rotationRegionColor = new Color( 0, 0, 255, 0 );
        }

        //Add the drag region for translating the laser
        addChild( new DragRegion( translationRegion.apply( fullRectangle, frontRectangle ), dragRegionColor, new VoidFunction1<DragEvent>() {
            public void apply( DragEvent event ) {
                laser.translate( transform.viewToModelDelta( event.delta ) );
            }
        }, mouseOverTranslationPart, draggingTranslation, new VoidFunction0.Null() ) );

        //Add the drag region for rotating the laser
        addChild( new DragRegion( rotationRegion.apply( fullRectangle, backRectangle ), rotationRegionColor, new VoidFunction1<DragEvent>() {
            public void apply( DragEvent event ) {
                Vector2D modelPoint = new Vector2D( transform.viewToModel( event.event.getPositionRelativeTo( getParent().getParent() ) ) );
                Vector2D vector = modelPoint.minus( laser.pivot.get() );
                final double angle = vector.getAngle();
                double after = clampDragAngle.apply( angle );
                laser.setAngle( after );
            }
        }, mouseOverRotationPart, draggingRotation, new VoidFunction0() {
            public void apply() {
                //If the laser's emission point got dropped outside the visible play area, then move it back to its initial location
                if ( !modelBounds.contains( laser.emissionPoint.get() ) ) {
                    laser.resetLocation();
                }
            }
        }
        ) );

        //Update the transform of the laser when its model data (pivot or emission point) changes
        new RichSimpleObserver() {
            public void update() {
                Point2D emissionPoint = transform.modelToView( laser.emissionPoint.get() ).toPoint2D();
                final double angle = transform.modelToView( Vector2D.createPolar( 1, laser.getAngle() ) ).getAngle();

                final AffineTransform t = new AffineTransform();
                t.translate( emissionPoint.getX(), emissionPoint.getY() );
                t.rotate( angle );
                t.translate( 0, -image.getHeight() / 2 );

                LaserNode.this.setTransform( t );
            }
        }.observe( laser.pivot, laser.emissionPoint );

        //Show the button on the laser that turns it on and off
        final BufferedImage pressed = flipY( flipX( multiScaleToHeight( BendingLightApplication.RESOURCES.getImage( "button_pressed.png" ), 42 ) ) );
        final BufferedImage unpressed = flipY( flipX( multiScaleToHeight( BendingLightApplication.RESOURCES.getImage( "button_unpressed.png" ), 42 ) ) );
        addChild( new PImage( pressed ) {{
            setOffset( -getFullBounds().getWidth() / 2 + image.getWidth() / 2, -getFullBounds().getHeight() / 2 + image.getHeight() / 2 );
            laser.on.addObserver( new SimpleObserver() {
                public void update() {
                    setImage( laser.on.get() ? pressed : unpressed );
                }
            } );

            //User interaction: when the user presses the red laser button turn the laser and off
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    laser.on.set( !laser.on.get() );
                }
            } );
        }} );
    }
}
