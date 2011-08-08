// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank.LeverArmVector;
import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank.MassForceVector;
import edu.colorado.phet.balanceandtorque.teetertotter.model.SupportColumn;
import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * @author John Blanco
 */
public class TeeterTotterTorqueCanvas extends PhetPCanvas {

    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;

    public final BooleanProperty leverArmVectorsVisibleProperty = new BooleanProperty( false );
    public final BooleanProperty forceVectorsFromObjectsVisibleProperty = new BooleanProperty( false );

    public TeeterTotterTorqueCanvas( final TeeterTotterTorqueModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.4 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.75 ) ),
                150 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );

        // Whenever a mass is added to the model, create a graphic for it.
        model.massList.addElementAddedObserver( new VoidFunction1<Mass>() {
            public void apply( Mass mass ) {
                // Create and add the view representation for this mass.
                PNode massNode = null;
                if ( mass instanceof ShapeMass ) {
                    // TODO: Always bricks right now, may have to change in the future.
                    massNode = new BrickStackNode( mvt, (ShapeMass) mass );
                }
                else if ( mass instanceof ImageMass ) {
                    massNode = new ImageModelElementNode( mvt, (ImageMass) mass );
                }
                else {
                    System.out.println( getClass().getName() + " - Error: Unrecognized mass type." );
                    assert false;
                }
                rootNode.addChild( massNode );
                // Add the removal listener for if and when this mass is removed from the model.
                final PNode finalMassNode = massNode;
                model.massList.addElementRemovedObserver( mass, new VoidFunction0() {
                    public void apply() {
                        rootNode.removeChild( finalMassNode );
                    }
                } );
            }
        } );

        // Add graphics for the plank, the fulcrum, the attachment bar, and the columns.
        rootNode.addChild( new FulcrumAbovePlankNode( mvt, model.getFulcrum() ) );
        rootNode.addChild( new AttachmentBarNode( mvt, model.getAttachmentBar() ) );
        rootNode.addChild( new PlankNode( mvt, model.getPlank() ) );
        for ( SupportColumn supportColumn : model.getSupportColumns() ) {
            rootNode.addChild( new SupportColumnNode( mvt, supportColumn, model.supportColumnsActive ) );
        }

        // Listen to the list of various vectors and manage their representations.
        model.getPlank().forceVectorList.addElementAddedObserver( new VoidFunction1<MassForceVector>() {
            public void apply( final MassForceVector addedMassForceVector ) {
                // Add a representation for the new vector.
                final PositionedVectorNode positionedVectorNode = new PositionedVectorNode( addedMassForceVector.forceVectorProperty,
                                                                                            0.002,  // Scaling factor, chosen to make size reasonable.
                                                                                            forceVectorsFromObjectsVisibleProperty,
                                                                                            Color.WHITE,
                                                                                            mvt );
                rootNode.addChild( positionedVectorNode );
                // Listen for removal of this vector and, if and when it is
                // removed, remove the corresponding representation.
                model.getPlank().forceVectorList.addElementRemovedObserver( new VoidFunction1<MassForceVector>() {
                    public void apply( MassForceVector removedMassForceVector ) {
                        if ( removedMassForceVector == addedMassForceVector ) {
                            rootNode.removeChild( positionedVectorNode );
                        }
                    }
                } );
            }
        } );
        model.getPlank().leverArmVectorList.addElementAddedObserver( new VoidFunction1<LeverArmVector>() {
            public void apply( final LeverArmVector addedLeverArmVector ) {
                // Add a representation for the new vector.
                final PositionedVectorNode positionedVectorNode = new PositionedVectorNode( addedLeverArmVector.leverArmVectorProperty,
                                                                                            1.0,
                                                                                            leverArmVectorsVisibleProperty,
                                                                                            new Color( 255, 190, 0 ),
                                                                                            mvt );
                rootNode.addChild( positionedVectorNode );
                // Listen for removal of this vector and, if and when it is
                // removed, remove the corresponding representation.
                model.getPlank().leverArmVectorList.addElementRemovedObserver( new VoidFunction1<LeverArmVector>() {
                    public void apply( LeverArmVector removedLeverArmVector ) {
                        if ( removedLeverArmVector == addedLeverArmVector ) {
                            rootNode.removeChild( positionedVectorNode );
                        }
                    }
                } );
            }
        } );

        // Add the button that will restore the columns if they have been
        // previously removed.
        // TODO: i18n
        final TextButtonNode restoreColumnsButton = new TextButtonNode( "Add Supports", new PhetFont( 14 ) ) {{
            setBackground( Color.YELLOW );
            setOffset( mvt.modelToViewX( 2.3 ) - getFullBounds().width / 2, mvt.modelToViewY( -0.2 ) );
            addInputEventListener( new ButtonEventHandler() {
                @Override public void mouseReleased( PInputEvent event ) {
                    model.supportColumnsActive.set( true );
                }
            } );
        }};
        rootNode.addChild( restoreColumnsButton );

        // Add the Reset All button.
        rootNode.addChild( new ResetAllButtonNode( model, this, 14, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            centerFullBoundsOnPoint( restoreColumnsButton.getFullBoundsReference().getCenterX(),
                                     restoreColumnsButton.getFullBoundsReference().getMaxY() + 30 );
            setConfirmationEnabled( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    // Reset properties that control vector visibility.
                    leverArmVectorsVisibleProperty.reset();
                    forceVectorsFromObjectsVisibleProperty.reset();
                }
            } );
        }} );

        // Only show the Restore Columns button when the columns are not active.
        model.supportColumnsActive.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean supportColumnsActive ) {
                restoreColumnsButton.setVisible( !supportColumnsActive );
            }
        } );

        // Add the control panel that will allow users to control the visibility
        // of the various vectors.
        PNode vectorControlPanel = new ControlPanelNode( new SwingLayoutNode( new GridLayout( 4, 1 ) ) {{
            addChild( new PText( "Show" ) {{
                setFont( new PhetFont( 18 ) );
            }} );
            addChild( new PropertyCheckBoxNode( "Distances", leverArmVectorsVisibleProperty ) );
            addChild( new PropertyCheckBoxNode( "Forces from Object", forceVectorsFromObjectsVisibleProperty ) );
        }} );
        rootNode.addChild( vectorControlPanel );

        // Add the mass box, which is the place where the user will get the
        // objects that can be placed on the balance.
        MassBoxNode massBoxControlPanel = new MassBoxNode( model, mvt, this );
        rootNode.addChild( massBoxControlPanel );

        // Lay out the control panels.
        double controlPanelCenterX = Math.min( STAGE_SIZE.getWidth() - massBoxControlPanel.getFullBoundsReference().width / 2 - 10,
                                               STAGE_SIZE.getWidth() - vectorControlPanel.getFullBoundsReference().width / 2 - 10 );
        massBoxControlPanel.setOffset( controlPanelCenterX - massBoxControlPanel.getFullBoundsReference().width / 2,
                                       mvt.modelToViewY( 0 ) - massBoxControlPanel.getFullBoundsReference().height - 10 );
        vectorControlPanel.setOffset( controlPanelCenterX - vectorControlPanel.getFullBoundsReference().width / 2,
                                      massBoxControlPanel.getFullBoundsReference().getMinY() - vectorControlPanel.getFullBoundsReference().height - 10 );

        rootNode.addChild( new ZeroOffsetNode( new ControlPanelNode( new MassKitSelectionNode( new Property<Integer>( 0 ), model, mvt, this ) ) ) );
    }

    // Convenience class for avoiding code duplication.
    private static class PropertyCheckBoxNode extends PNode {
        private PropertyCheckBoxNode( String text, BooleanProperty property ) {
            PropertyCheckBox checkBox = new PropertyCheckBox( text, property );
            checkBox.setFont( new PhetFont( 14 ) );
            addChild( new PSwing( checkBox ) );
        }
    }
}
