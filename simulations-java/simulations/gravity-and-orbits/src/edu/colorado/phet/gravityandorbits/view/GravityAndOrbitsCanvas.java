/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsConstants;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsDefaults;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas template.
 */
public class GravityAndOrbitsCanvas extends PhetPCanvas {
    private GravityAndOrbitsModel model;
    private PNode _rootNode;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    private ModelViewTransform2D modelViewTransform2D;

    public GravityAndOrbitsCanvas( final GravityAndOrbitsModel model, final GravityAndOrbitsModule module ) {
        super( GravityAndOrbitsDefaults.VIEW_SIZE );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        this.model = model;

        setBackground( GravityAndOrbitsConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        // stores the current position of the mouse
        final Property<ImmutableVector2D> mousePositionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent mouseEvent ) {
            }

            public void mouseMoved( MouseEvent mouseEvent ) {
                mousePositionProperty.setValue( new ImmutableVector2D( mouseEvent.getPoint().x, mouseEvent.getPoint().y ) );
            }
        } );

        modelViewTransform2D = new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.30, STAGE_SIZE.height * 0.5 ), 1.5E-9, true );

        addChild( new BodyNode( model.getSun(), modelViewTransform2D, module.getToScaleProperty(), mousePositionProperty, this ) );
        addChild( new BodyNode( model.getPlanet(), modelViewTransform2D, module.getToScaleProperty(), mousePositionProperty, this ) );
        addChild( new TraceNode( model.getPlanet(), modelViewTransform2D, module.getTracesProperty() ) );
        addChild( new TraceNode( model.getSun(), modelViewTransform2D, module.getTracesProperty() ) );
        addChild( new VectorNode( model.getPlanet(), modelViewTransform2D, module.getForcesProperty(), model.getPlanet().getForceProperty(), VectorNode.FORCE_SCALE ) );
        addChild( new VectorNode( model.getSun(), modelViewTransform2D, module.getForcesProperty(), model.getSun().getForceProperty(), VectorNode.FORCE_SCALE ) );
        addChild( new GrabbableVectorNode( model.getPlanet(), modelViewTransform2D, module.getVelocityProperty(), model.getPlanet().getVelocityProperty(), VectorNode.VELOCITY_SCALE ) );
        addChild( new GrabbableVectorNode( model.getSun(), modelViewTransform2D, module.getVelocityProperty(), model.getSun().getVelocityProperty(), VectorNode.VELOCITY_SCALE ) );
        addChild( new MassReadoutNode( model.getSun(), modelViewTransform2D, module.getShowMassesProperty() ) );
        addChild( new MassReadoutNode( model.getPlanet(), modelViewTransform2D, module.getShowMassesProperty() ) );

        // Control Panel
        final GravityAndOrbitsControlPanel controlPanel = new GravityAndOrbitsControlPanel( module, model );
        final PNode controlPanelNode = new PNode() {{ //swing border looks truncated in pswing, so draw our own in piccolo
            final PSwing controlPanelPSwing = new PSwing( controlPanel );
            //This next line works around a layout problem in pswing or swing in which the panel was reporting larger bounds than it displayed
//            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), GravityAndOrbitsControlPanel.BACKGROUND ) );
            addChild( controlPanelPSwing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, controlPanelPSwing.getFullBounds().getWidth(), controlPanelPSwing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.green ) );
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );

        //Reset all button
        addChild( new GradientButtonNode( "Reset all", (int) ( GravityAndOrbitsControlPanel.CONTROL_FONT.getSize() * 1.3 ), GravityAndOrbitsControlPanel.BACKGROUND, GravityAndOrbitsControlPanel.FOREGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.resetAll();
                }
            } );
        }} );

        addChild( new GravityAndOrbitsClockControlNode( model.getClock() ) {{
            setOffset( GravityAndOrbitsCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, GravityAndOrbitsCanvas.STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
    }

    public void addChild( PNode node ) {
        _rootNode.addChild( node );
    }
}
