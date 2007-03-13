/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.MotorsControlPanel;
import edu.colorado.phet.opticaltweezers.control.OTClockControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.help.OTWiggleMe;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.model.OTModel;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.view.OTModelViewManager;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * MotorsModule is the "Molecular Motors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private OTModel _model;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _rootNode;

    // Control
    private OTModelViewManager _modelViewManager;
    private MotorsControlPanel _controlPanel;
    private OTClockControlPanel _clockControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MotorsModule() {
        super( SimStrings.get( "MotorsModule.title" ), MotorsDefaults.CLOCK, MotorsDefaults.CLOCK_PAUSED );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        IClock clock = getClock();
        
        // Model
        _model = new OTModel( clock );

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( OTConstants.CANVAS_RENDERING_SIZE );
            _canvas.setBackground( OTConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );

            _canvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    // update the layout when the canvas is resized
                    updateCanvasLayout();
                }
            } );
        }

        // Root of our scene graph
        _rootNode = new PNode();
        _canvas.addWorldChild( _rootNode );

        
        // Layering order on the canvas (back-to-front)
        {
//            _rootNode.addChild(...);
        }
        
        //----------------------------------------------------------------------------
        // Model-View management
        //----------------------------------------------------------------------------
        
        _modelViewManager = new OTModelViewManager( _model );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new MotorsControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new OTClockControlPanel( (OTClock) getClock() );
        setClockControlPanel( _clockControlPanel );

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        //XXX

        //----------------------------------------------------------------------------
        // Initialize the module state
        //----------------------------------------------------------------------------

        reset();
        updateCanvasLayout();
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Indicates that this module has help.
     * 
     * @return true
     */
    public boolean hasHelp() {
        return true;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    public void updateCanvasLayout() {

        Dimension2D worldSize = _canvas.getWorldSize();
//        System.out.println( "MotorsModule.updateCanvasLayout worldSize=" + worldSize );//XXX
        if ( worldSize.getWidth() == 0 || worldSize.getHeight() == 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        
        // reusable (x,y) coordinates, for setting offsets
        double x, y;
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    public void reset() {
        // TODO Auto-generated method stub
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
