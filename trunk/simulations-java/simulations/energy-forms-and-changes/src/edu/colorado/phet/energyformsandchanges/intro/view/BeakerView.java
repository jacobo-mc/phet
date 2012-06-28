// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.intro.model.Beaker;
import edu.colorado.phet.energyformsandchanges.intro.model.Block;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkContainerSliceNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Object that represents a beaker in the view.  This representation is split
 * between a front node and a back node, which must be separately added to the
 * canvas.  This is done to allow a layering effect.
 *
 * @author John Blanco
 */
public class BeakerView {

    private static final Stroke OUTLINE_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color OUTLINE_COLOR = Color.LIGHT_GRAY;
    private static final double PERSPECTIVE_PROPORTION = -EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER;
    private static final Font LABEL_FONT = new PhetFont( 32, false );
    private static final boolean SHOW_MODEL_RECT = false;
    private static final Color BEAKER_COLOR = new Color( 250, 250, 250, 100 );
    private static final ImmutableVector2D BLOCK_OFFSET_POST_TO_CENTER = new ImmutableVector2D( 0, Block.SURFACE_WIDTH / 2 );

    private final PhetPCanvas canvas;
    private final ModelViewTransform mvt;

    private final PNode frontNode = new PNode();
    private final PNode backNode = new PNode();

    public BeakerView( final EFACIntroModel model, PhetPCanvas canvas, final ModelViewTransform mvt ) {

        this.mvt = mvt;
        this.canvas = canvas;
        final Beaker beaker = model.getBeaker();

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Get a version of the rectangle that defines the beaker size and
        // location in the view.
        final Rectangle2D beakerViewRect = scaleTransform.createTransformedShape( Beaker.getRawOutlineRect() ).getBounds2D();

        // Create the shapes for the top and bottom of the beaker.  These are
        // ellipses in order to create a 3D-ish look.
        double ellipseHeight = beakerViewRect.getWidth() * PERSPECTIVE_PROPORTION;
        final Ellipse2D.Double topEllipse = new Ellipse2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMinY() - ellipseHeight / 2, beakerViewRect.getWidth(), ellipseHeight );
        final Ellipse2D.Double bottomEllipse = new Ellipse2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMaxY() - ellipseHeight / 2, beakerViewRect.getWidth(), ellipseHeight );

        // Add the bottom ellipse.
        backNode.addChild( new PhetPPath( bottomEllipse, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Create and add the shape for the body of the beaker.
        final Area beakerBody = new Area( beakerViewRect );
        beakerBody.add( new Area( bottomEllipse ) );
        beakerBody.subtract( new Area( topEllipse ) );
        frontNode.addChild( new PhetPPath( beakerBody, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Add the water.  It will adjust its size based on the fluid level.
        final PerspectiveWaterNode water = new PerspectiveWaterNode( beakerViewRect, beaker.fluidLevel );
        frontNode.addChild( water );

        // Add the top ellipse.  It is behind the water for proper Z-order behavior.
        backNode.addChild( new PhetPPath( topEllipse, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Add the label.
        final PText label = new PText( "Water" );
        label.setFont( LABEL_FONT );
        label.centerFullBoundsOnPoint( beakerViewRect.getCenterX(), beakerViewRect.getMaxY() - label.getFullBoundsReference().height * 1.5 );
        frontNode.addChild( label );

        // Create the layers where the contained energy chunks will be placed.
        final PNode energyChunkRootNode = new PNode();
        backNode.addChild( energyChunkRootNode );
        final PClip energyChunkClipNode = new PClip();
        energyChunkRootNode.addChild( energyChunkClipNode );
        energyChunkClipNode.setStroke( null );
        for ( int i = beaker.getSlices().size() - 1; i >= 0; i-- ) {
            int colorBase = (int) ( 255 * (double) i / beaker.getSlices().size() );
            energyChunkClipNode.addChild( new EnergyChunkContainerSliceNode( beaker.getSlices().get( i ), mvt, new Color( colorBase, 255 - colorBase, colorBase ) ) );
        }

        // Watch for coming and going of energy chunks that are approaching
        // this model element and add/remove them as needed.
        beaker.approachingEnergyChunks.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                energyChunkRootNode.addChild( energyChunkNode );
                beaker.approachingEnergyChunks.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            energyChunkRootNode.removeChild( energyChunkNode );
                            beaker.approachingEnergyChunks.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );

        // Update the clipping mask when any of the blocks move.  The clipping
        // mask hides energy chunks that overlap with blocks.
        for ( Block block : model.getBlockList() ) {
            block.position.addObserver( new SimpleObserver() {
                public void update() {
                    updateEnergyChunkClipMask( model, energyChunkClipNode );
                }
            } );
        }

        // Adjust the transparency of the water and label based on energy
        // chunk visibility.
        model.energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyChunksVisible ) {
                label.setTransparency( energyChunksVisible ? 0.5f : 1f );
                water.setTransparency( energyChunksVisible ? PerspectiveWaterNode.NOMINAL_OPAQUENESS / 2 : PerspectiveWaterNode.NOMINAL_OPAQUENESS );
            }
        } );

        // If enabled, show the outline of the rectangle that represents the
        // beaker's position in the model.
        if ( SHOW_MODEL_RECT ) {
            frontNode.addChild( new PhetPPath( beakerViewRect, new BasicStroke( 1 ), Color.RED ) );
        }

        // Update the offset if and when the model position changes.
        beaker.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                frontNode.setOffset( mvt.modelToView( position ).toPoint2D() );
                backNode.setOffset( mvt.modelToView( position ).toPoint2D() );
                // Compensate the energy chunk layer so that the energy chunk
                // nodes can handle their own positioning.
                energyChunkRootNode.setOffset( mvt.modelToView( position ).getRotatedInstance( Math.PI ).toPoint2D() );
                BeakerView.this.updateEnergyChunkClipMask( model, energyChunkClipNode );
            }
        } );

        // Add the cursor handler.
        frontNode.addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.  This handler is a bit tricky, since it needs
        // to handle the case where a block is inside the beaker.
        final ImmutableVector2D offsetPosToCenter = new ImmutableVector2D( frontNode.getFullBoundsReference().getCenterX() - mvt.modelToViewX( beaker.position.get().getX() ),
                                                                           frontNode.getFullBoundsReference().getCenterY() - mvt.modelToViewY( beaker.position.get().getY() ) );

        frontNode.addInputEventListener( new PBasicInputEventHandler() {

            // Handler to use when the beaker itself is being dragged.
            final ThermalElementDragHandler beakerDragHandler = new ThermalElementDragHandler( beaker,
                                                                                               frontNode,
                                                                                               mvt,
                                                                                               new ThermalItemMotionConstraint( model,
                                                                                                                                beaker,
                                                                                                                                frontNode,
                                                                                                                                mvt,
                                                                                                                                offsetPosToCenter ) );

            // Handler to use when block dragged from within beaker.
            PBasicInputEventHandler blockDragHandler = null;

            @Override public void mousePressed( PInputEvent event ) {
                Block blockUnderCursor = null;
                for ( Block block : model.getBlockList() ) {
                    // If there is a block at the location inside the beaker
                    // where the user has pressed the mouse, move that instead
                    // of the beaker.
                    if ( block.getRect().contains( convertCanvasPointToModelPoint( event.getCanvasPosition() ) ) ) {
                        blockUnderCursor = block;
                        break;
                    }
                }
                if ( blockUnderCursor == null ) {
                    // No blocks in the beaker where the user has clicked, so
                    // the user is moving the beaker itself.
                    frontNode.addInputEventListener( beakerDragHandler );
                    beakerDragHandler.mousePressed( event );
                }
                else {
                    // There is a block where the user has clicked, set up the
                    // drag handler to allow the user to move it.
                    blockDragHandler = new ThermalElementDragHandler( blockUnderCursor,
                                                                      frontNode,
                                                                      mvt,
                                                                      new ThermalItemMotionConstraint( model,
                                                                                                       blockUnderCursor,
                                                                                                       frontNode,
                                                                                                       mvt,
                                                                                                       BLOCK_OFFSET_POST_TO_CENTER ) );
                    frontNode.addInputEventListener( blockDragHandler );
                    blockDragHandler.mousePressed( event );
                }
            }

            @Override public void mouseDragged( PInputEvent event ) {
                if ( blockDragHandler != null ) {
                    blockDragHandler.mouseDragged( event );
                }
                else {
                    beakerDragHandler.mouseDragged( event );
                }
            }

            @Override public void mouseReleased( PInputEvent event ) {
                if ( blockDragHandler != null ) {
                    blockDragHandler.mouseReleased( event );
                    frontNode.removeInputEventListener( blockDragHandler );
                    blockDragHandler = null;
                }
                else {
                    beakerDragHandler.mouseReleased( event );
                    frontNode.removeInputEventListener( beakerDragHandler );
                }
            }
        } );
    }

    // Update the clipping mask that hides energy chunks behind blocks that are in the beaker.
    private void updateEnergyChunkClipMask( EFACIntroModel model, PClip clip ) {
        ImmutableVector2D forwardPerspectiveOffset = EFACConstants.MAP_Z_TO_XY_OFFSET.apply( Block.SURFACE_WIDTH / 2 );
        ImmutableVector2D backwardPerspectiveOffset = EFACConstants.MAP_Z_TO_XY_OFFSET.apply( -Block.SURFACE_WIDTH / 2 );

        Area clippingMask = new Area( frontNode.getFullBoundsReference() );
        for ( Block block : model.getBlockList() ) {
            if ( model.getBeaker().getRect().contains( block.getRect() ) ) {
                DoubleGeneralPath path = new DoubleGeneralPath();
                Rectangle2D rect = block.getRect();
                path.moveTo( new ImmutableVector2D( rect.getX(), rect.getY() ).getAddedInstance( forwardPerspectiveOffset ) );
                path.lineTo( new ImmutableVector2D( rect.getMaxX(), rect.getY() ).getAddedInstance( forwardPerspectiveOffset ) );
                path.lineTo( new ImmutableVector2D( rect.getMaxX(), rect.getY() ).getAddedInstance( backwardPerspectiveOffset ) );
                path.lineTo( new ImmutableVector2D( rect.getMaxX(), rect.getMaxY() ).getAddedInstance( backwardPerspectiveOffset ) );
                path.lineTo( new ImmutableVector2D( rect.getMinX(), rect.getMaxY() ).getAddedInstance( backwardPerspectiveOffset ) );
                path.lineTo( new ImmutableVector2D( rect.getMinX(), rect.getMaxY() ).getAddedInstance( forwardPerspectiveOffset ) );
                path.closePath();
                clippingMask.subtract( new Area( mvt.modelToView( path.getGeneralPath() ) ) );
            }
        }
        clip.setPathTo( clippingMask );
    }

    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D convertCanvasPointToModelPoint( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
        canvas.getPhetRootNode().screenToWorld( worldPos );
        return mvt.viewToModel( worldPos );
    }

    private static class PerspectiveWaterNode extends PNode {
        public static final float NOMINAL_OPAQUENESS = 0.75f;
        private static final Color WATER_COLOR = new Color( 175, 238, 238, (int) ( Math.round( NOMINAL_OPAQUENESS * 255 ) ) );
        private static final Color WATER_OUTLINE_COLOR = ColorUtils.darkerColor( WATER_COLOR, 0.2 );
        private static final Stroke WATER_OUTLINE_STROKE = new BasicStroke( 2 );

        private PerspectiveWaterNode( final Rectangle2D beakerOutlineRect, Property<Double> waterLevel ) {

            final PhetPPath waterBodyNode = new PhetPPath( WATER_COLOR, WATER_OUTLINE_STROKE, WATER_OUTLINE_COLOR );
            addChild( waterBodyNode );
            final PhetPPath waterTopNode = new PhetPPath( WATER_COLOR, WATER_OUTLINE_STROKE, WATER_OUTLINE_COLOR );
            addChild( waterTopNode );


            waterLevel.addObserver( new VoidFunction1<Double>() {
                public void apply( Double fluidLevel ) {
                    assert fluidLevel >= 0 && fluidLevel <= 1; // Bounds checking.

                    Rectangle2D waterRect = new Rectangle2D.Double( beakerOutlineRect.getX(),
                                                                    beakerOutlineRect.getY() + beakerOutlineRect.getHeight() * ( 1 - fluidLevel ),
                                                                    beakerOutlineRect.getWidth(),
                                                                    beakerOutlineRect.getHeight() * fluidLevel );

                    double ellipseHeight = PERSPECTIVE_PROPORTION * beakerOutlineRect.getWidth();
                    Shape topEllipse = new Ellipse2D.Double( waterRect.getMinX(),
                                                             waterRect.getMinY() - ellipseHeight / 2,
                                                             waterRect.getWidth(),
                                                             ellipseHeight );

                    Shape bottomEllipse = new Ellipse2D.Double( waterRect.getMinX(),
                                                                waterRect.getMaxY() - ellipseHeight / 2,
                                                                waterRect.getWidth(),
                                                                ellipseHeight );

                    // Update the shape of the body and bottom of the water.
                    Area waterBodyArea = new Area( waterRect );
                    waterBodyArea.add( new Area( bottomEllipse ) );
                    waterBodyArea.subtract( new Area( topEllipse ) );
                    waterBodyNode.setPathTo( waterBodyArea );

                    // Update the shape of the water based on the proportionate
                    // water level.
                    waterTopNode.setPathTo( topEllipse );
                }
            } );
        }
    }

    public PNode getFrontNode() {
        return frontNode;
    }

    public PNode getBackNode() {
        return backNode;
    }
}
