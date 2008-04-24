/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fitness.module.fitness;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.fitness.FitnessConstants;
import edu.colorado.phet.fitness.control.CaloriePanel;
import edu.colorado.phet.fitness.control.HumanControlPanel;
import edu.colorado.phet.fitness.view.HumanAreaNode;
import edu.colorado.phet.fitness.view.ScaleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * FitnessCanvas is the canvas for FitnessModule.
 */
public class FitnessCanvas extends BufferedPhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private FitnessModel _model;

    // View
    private PNode _rootNode;

    public static final double CANVAS_WIDTH = 4;
    public static final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private RulerNode rulerNode;
    private PSwing humanControlPanelPSwing;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FitnessCanvas( final FitnessModel model ) {
//        super( new Rectangle2D.Double( -10,-10,20,20) );
        super( new PDimension( 10, 10 ) );

        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.
        setWorldTransformStrategy( new FitnessRenderingSizeStrategy( this, CANVAS_WIDTH, CANVAS_HEIGHT ) );
        _model = model;

        setBackground( FitnessConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

//        _rootNode.addChild( new HumanNode( model.getHuman() ) );
        _rootNode.addChild( new ScaleNode( model.getHuman() ) );
        _rootNode.addChild( new HumanAreaNode( model.getHuman() ) );

        setZoomEventHandler( new PZoomEventHandler() );
//        setPanEventHandler( new PPanEventHandler() );

        rulerNode = createRulerNode();
        addWorldChild( rulerNode );

        HumanControlPanel humanControlPanel = new HumanControlPanel( model.getHuman() );
        humanControlPanelPSwing = new PSwing( humanControlPanel );
        addScreenChild( humanControlPanelPSwing );

        PNode caloriePanel = new CaloriePanel( model, this );
        caloriePanel.setOffset( humanControlPanelPSwing.getFullBounds().getWidth(), 0 );
        addScreenChild( caloriePanel );

        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
//        addKeyListener( new KeyAdapter() {
//            public void keyPressed( KeyEvent e ) {
//                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
//                    model.getHuman().setLeanMuscleMass( model.getHuman().getLeanMuscleMass() + 10 );
//                }
//                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
//                    model.getHuman().setLeanMuscleMass( model.getHuman().getLeanMuscleMass() - 10 );
//                }
//            }
//        } );
    }

    private RulerNode createRulerNode() {
        final RulerNode rulerNode = new RulerNode( 1, 0.1, 0.1, new String[]{"0.0", "0.25", "0.5", "0.75", "1.0"}, new PhetDefaultFont(), "m", new PhetDefaultFont(), 4, 0.03, 0.01 );
        rulerNode.rotate( Math.PI * 3 / 2 );
        rulerNode.addInputEventListener( new PDragEventHandler() );

        rulerNode.addInputEventListener( new CursorHandler() );
        rulerNode.setBackgroundStroke( new BasicStroke( 0.005f ) );
        rulerNode.setFontScale( 0.005 );
        rulerNode.setUnitsSpacing( 0.001 );
        rulerNode.setTickStroke( new BasicStroke( 0.005f ) );
        rulerNode.setOffset( -0.6653250773993821, 0.1145510835913312 );
        return rulerNode;
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( FitnessConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        humanControlPanelPSwing.setOffset( 0, getHeight() - humanControlPanelPSwing.getFullBounds().getHeight() );

        //XXX lay out nodes
    }

    //reset any view settings
    public void resetAll() {
    }
}