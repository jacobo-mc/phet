/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.D2CControlPanel;
import edu.colorado.phet.fourier.help.FourierHelpItem;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.view.D2CAmplitudesGraph;
import edu.colorado.phet.fourier.view.D2CHarmonicsGraph;
import edu.colorado.phet.fourier.view.D2CSumGraph;
import edu.colorado.phet.fourier.view.GraphClosed;


/**
 * D2CModule is the "Discrete to Continuous" (D2C) module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CModule extends FourierModule implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;
    private static final double TOOLS_LAYER = 6;
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = new Color( 240, 255, 255 );
    
    // Gaussian wave packet
    private static final double WAVE_PACKET_SPACING = 2 * Math.PI;
    private static final double WAVE_PACKET_WIDTH = 6 * Math.PI;
    private static final double WAVE_PACKET_CENTER = 12 * Math.PI;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesGraph _amplitudesGraph;
    private D2CHarmonicsGraph _harmonicsGraph;
    private D2CSumGraph _sumGraph;
    private GraphClosed _harmonicsGraphClosed;
    private GraphClosed _sumGraphClosed;
    private D2CControlPanel _controlPanel;
    private Dimension _canvasSize;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public D2CModule( AbstractClock clock ) {
        
        super( SimStrings.get( "D2CModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Gaussian wave packet
        _wavePacket = new GaussianWavePacket( WAVE_PACKET_SPACING, WAVE_PACKET_WIDTH, WAVE_PACKET_CENTER );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        _canvasSize = apparatusPanel.getSize();
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.addChangeListener( this );

        // Amplitudes view
        _amplitudesGraph = new D2CAmplitudesGraph( apparatusPanel, _wavePacket );
        _amplitudesGraph.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        // Harmonics view
        _harmonicsGraph = new D2CHarmonicsGraph( apparatusPanel, _wavePacket );
        apparatusPanel.addGraphic( _harmonicsGraph, HARMONICS_LAYER );
        
        // Harmonics view (collapsed)
        _harmonicsGraphClosed = new GraphClosed( apparatusPanel, SimStrings.get( "D2CHarmonicsGraph.title" ) );
        apparatusPanel.addGraphic( _harmonicsGraphClosed, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumGraph = new D2CSumGraph( apparatusPanel, _wavePacket );
        apparatusPanel.addGraphic( _sumGraph, SUM_LAYER );
        
        // Sum view (collapsed)
        _sumGraphClosed = new GraphClosed( apparatusPanel, SimStrings.get( "D2CSumGraph.title" ) );
        apparatusPanel.addGraphic( _sumGraphClosed, SUM_CLOSED_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new D2CControlPanel( this, _wavePacket, _amplitudesGraph, _harmonicsGraph, _sumGraph );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );

        // Open/close buttons on graphs
        {
            // Harmonics close
            _harmonicsGraph.getCloseButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsGraph.setVisible( false );
                    _harmonicsGraphClosed.setVisible( true );
                    resizeGraphs();
                }
             } );
            
            // Harmonics open
            _harmonicsGraphClosed.getOpenButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsGraph.setVisible( true );
                    _harmonicsGraphClosed.setVisible( false );
                    resizeGraphs();
                }
             } );
            
            // Sum close
            _sumGraph.getCloseButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumGraph.setVisible( false );
                    _sumGraphClosed.setVisible( true );
                    resizeGraphs();
                }
             } );
            
            // Sum open
            _sumGraphClosed.getOpenButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumGraph.setVisible( true );
                    _sumGraphClosed.setVisible( false );
                    resizeGraphs();
                }
             } );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        FourierHelpItem harmonicsCloseButtonHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "D2CModule.help.closeHarmonics" ) );
        harmonicsCloseButtonHelp.pointAt( _harmonicsGraph.getCloseButton(), FourierHelpItem.LEFT, 15 );
        addHelpItem( harmonicsCloseButtonHelp );
        
        FourierHelpItem spacingToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "D2CModule.help.spacingTool" ) );
        spacingToolHelp.pointAt( _amplitudesGraph.getSpacingTool(), FourierHelpItem.UP, 15 );
        addHelpItem( spacingToolHelp );
        
        FourierHelpItem kWidthToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "D2CModule.help.widthTool" ) );
        kWidthToolHelp.pointAt( _amplitudesGraph.getWidthTool(), FourierHelpItem.UP, 15 );
        addHelpItem( kWidthToolHelp );
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        reset();
    }
    
    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        _wavePacket.setSpacing( WAVE_PACKET_SPACING );
        _wavePacket.setWidth( WAVE_PACKET_WIDTH );
        
        _amplitudesGraph.reset();
        _harmonicsGraph.reset();
        _sumGraph.reset();
        
        _harmonicsGraph.setVisible( true );
        _harmonicsGraphClosed.setVisible( false );
        _sumGraph.setVisible( true );
        _sumGraphClosed.setVisible( false ); 
        resizeGraphs();
        
        _harmonicsGraph.setLocation( _amplitudesGraph.getX(), _amplitudesGraph.getY() + _amplitudesGraph.getHeight() );
        _harmonicsGraphClosed.setLocation( _amplitudesGraph.getX(), _amplitudesGraph.getY() + _amplitudesGraph.getHeight() );
        _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        
        _controlPanel.reset();
    }
    
    //----------------------------------------------------------------------------
    // EventHandling
    //----------------------------------------------------------------------------
    
    /*
     * Resizes and repositions the graphs based on which ones are visible.
     * 
     * @param event
     */
    private void resizeGraphs() {

        int canvasHeight = _canvasSize.height;
        int availableHeight = canvasHeight - _amplitudesGraph.getHeight();
        
        if (  _harmonicsGraph.isVisible() && _sumGraph.isVisible() ) {
            _harmonicsGraph.setHeight( availableHeight/2 );
            _sumGraph.setHeight( availableHeight/2 );
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        }
        else if ( _harmonicsGraph.isVisible() ) {
            _harmonicsGraph.setHeight( availableHeight - _sumGraphClosed.getHeight() );
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        }
        else if ( _sumGraph.isVisible() ) {
            _sumGraph.setHeight( availableHeight - _harmonicsGraphClosed.getHeight() );
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() );
        }
        else {
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() ); 
        }
        
        System.out.println( "canvasHeight = " + canvasHeight );//XXX
        System.out.println( "amplitudesHeight = " + _amplitudesGraph.getHeight() );//XXX
        System.out.println( "availableHeight = " + availableHeight );//XXX
        System.out.println( "harmonics height = " + _harmonicsGraph.getHeight() );//XXX
        System.out.println( "harmonics location = " + _harmonicsGraph.getLocation() );//XXX
        System.out.println( "sum location = " + _sumGraph.getLocation() );//XXX
        System.out.println( "sum height = " + _sumGraph.getHeight() );//XXX
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Redoes the layout whenever the apparatus panel's canvas size changes.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _canvasSize.setSize( event.getCanvasSize() );
        resizeGraphs();
    }
}
