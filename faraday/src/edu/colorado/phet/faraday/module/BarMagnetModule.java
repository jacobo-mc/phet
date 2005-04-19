/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.util.Vector2D;
import edu.colorado.phet.faraday.view.*;


/**
 * BarMagnetModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double COMPASS_GRID_LAYER = 1;
    private static final double BAR_MAGNET_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double FIELD_METER_LAYER = 4;

    // Locations
    private static final Point BAR_MAGNET_LOCATION = new Point( 450, 300 );
    private static final Point COMPASS_LOCATION = new Point( 150, 300 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 250, 175 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BarMagnet _barMagnetModel;
    private Compass _compassModel;
    private BarMagnetGraphic _barMagnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;
    private BarMagnetPanel _barMagnetPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public BarMagnetModule( AbstractClock clock ) {
        
        super( SimStrings.get( "BarMagnetModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet
        _barMagnetModel = new BarMagnet();
        _barMagnetModel.setMaxStrength( FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
        _barMagnetModel.setMinStrength( FaradayConfig.BAR_MAGNET_STRENGTH_MIN );
        _barMagnetModel.setStrength( 0.75 * FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the size -- size is set by the associated BarMagnetGraphic.
        
        // Compass model
        _compassModel = new Compass( _barMagnetModel );
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setBehavior( Compass.KINEMATIC_BEHAVIOR );
        model.addModelElement( _compassModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        _barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, _barMagnetModel );
        apparatusPanel.addChangeListener( _barMagnetGraphic );
        apparatusPanel.addGraphic( _barMagnetGraphic, BAR_MAGNET_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _barMagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setRescalingEnabled( true );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        _gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
        apparatusPanel.addChangeListener( _gridGraphic );
        apparatusPanel.addGraphic( _gridGraphic, COMPASS_GRID_LAYER );
        super.setCompassGridGraphic( _gridGraphic );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        _fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, _barMagnetModel );
        _fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        _fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _fieldMeterGraphic );
        apparatusPanel.addGraphic( _fieldMeterGraphic, FIELD_METER_LAYER );
        
        // Collision detection
        _barMagnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( _barMagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            
            // Bar Magnet controls
            _barMagnetPanel = new BarMagnetPanel( 
                    _barMagnetModel, _compassModel, 
                    _barMagnetGraphic, _gridGraphic, _fieldMeterGraphic );
            controlPanel.addFullWidth( _barMagnetPanel );
            
            // Reset button
            JButton resetButton = new JButton( SimStrings.get( "Reset.button" ) );
            resetButton.addActionListener( new ActionListener() { 
                public void actionPerformed( ActionEvent e ) {
                    reset();
                }
            } );
            controlPanel.add( resetButton ); 
        }
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _barMagnetModel, _compassModel );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /**
     * Handles the "Reset" button, resets everything thing to the initial state.
     */
    private void reset() {
        
        // Bar Magnet model
        _barMagnetModel.setStrength( 0.75 * FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( 0 /* radians */ );
        
        // Compass model
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( true );
        
        // Bar Magnet view
        _barMagnetGraphic.setTransparencyEnabled( false );
        
        // Compass Grid view
        _gridGraphic.setVisible( true );
        
        // Field Meter view
        _fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        _fieldMeterGraphic.setVisible( false );
        
        // Control panel
        _barMagnetPanel.update();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the bar magnet or compass is moved.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private BarMagnet _barMagnetModel;
        private Point2D _barMagnetLocation;
        private Compass _compassModel;
        private Point2D _compassLocation;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param barMagnetModel
         * @param compassModel
         */
        public ThisWiggleMeGraphic( Component component, BaseModel model, BarMagnet barMagnetModel, Compass compassModel ) {
            super( component, model );

            _barMagnetModel = barMagnetModel;
            _barMagnetLocation = barMagnetModel.getLocation();
            barMagnetModel.addObserver( this );
            
            _compassModel = compassModel;
            _compassLocation = compassModel.getLocation();
            compassModel.addObserver( this );
            
            setText( SimStrings.get( "BarMagnetModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.BOTTOM_LEFT, new Vector2D( -40, 50 ) );
            addArrow( WiggleMeGraphic.BOTTOM_RIGHT, new Vector2D( 40, 50 ) );
            setRange( 20, 10 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the bar magnet or compass is moved, disable and unwire the wiggle me.
         */
        public void update() {
            if ( !_barMagnetLocation.equals( _barMagnetModel.getLocation() ) ||
                 !_compassLocation.equals( _compassModel.getLocation() ) ) {
                // Disable
                setEnabled( false );
                // Unwire
                _barMagnetModel.removeObserver( this );
                _compassModel.removeObserver( this );
            }
        }
    }
}
