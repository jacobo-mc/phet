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
import java.awt.Dimension;
import java.awt.Point;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.ElectromagnetControlPanel;
import edu.colorado.phet.faraday.model.Battery;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.Electromagnet;
import edu.colorado.phet.faraday.model.SourceCoil;
import edu.colorado.phet.faraday.view.*;


/**
 * TransformerModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetModule extends Module implements ICompassGridModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double ELECTROMAGNET_BACK_LAYER = 2;
    private static final double ELECTROMAGNET_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double ELECTROMAGNET_FRONT_LAYER = 5;
    private static final double METER_LAYER = 6;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point MAGNET_LOCATION = new Point( 400, 300 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point SOURCE_COIL_LOCATION = new Point( 500, 400 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Battery
    private static final double BATTERY_VOLTAGE = 50; // XXX % ?
    
    // Source Coil
    private static final int NUMBER_OF_LOOPS = 4;
    private static final double LOOP_RADIUS = 50.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CompassGridGraphic _gridGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public ElectromagnetModule( ApplicationModel appModel ) {

        super( SimStrings.get( "ElectromagnetModule.title" ) );
        assert( appModel != null );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
     
        // Battery
        Battery batteryModel = new Battery();
        batteryModel.setVoltage( BATTERY_VOLTAGE );
        
        // Source Coil
        SourceCoil sourceCoilModel = new SourceCoil( batteryModel );
        sourceCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        sourceCoilModel.setRadius( LOOP_RADIUS );
        sourceCoilModel.setDirection( 0 /* radians */ );
        sourceCoilModel.setLocation( SOURCE_COIL_LOCATION );
        
        // Electromagnet
        Electromagnet electromagnetModel = new Electromagnet( sourceCoilModel, batteryModel );
        electromagnetModel.setMaxStrength( FaradayConfig.ELECTROMAGNET_STRENGTH_MAX );
        electromagnetModel.setMinStrength( 0 );
        electromagnetModel.setStrength( 0 );
        electromagnetModel.setLocation( MAGNET_LOCATION );
        electromagnetModel.setDirection( 0 /* radians */ );
        electromagnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE ); // XXX
        model.addModelElement( electromagnetModel );
         
        // Compass model
        Compass compassModel = new Compass( electromagnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( true );
        model.addModelElement( compassModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        ElectromagnetGraphic electromagnetGraphic = new ElectromagnetGraphic( apparatusPanel, model, electromagnetModel, sourceCoilModel, batteryModel );
        apparatusPanel.addChangeListener( electromagnetGraphic );
        apparatusPanel.addGraphic( electromagnetGraphic.getForeground(), ELECTROMAGNET_FRONT_LAYER );
        apparatusPanel.addGraphic( electromagnetGraphic.getBackground(), ELECTROMAGNET_BACK_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, electromagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        _gridGraphic.setAlphaEnabled( ! APPARATUS_BACKGROUND.equals( Color.BLACK ) );
        apparatusPanel.addChangeListener( _gridGraphic );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // Compass
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, electromagnetModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, METER_LAYER );
        
        // Debugger
//      DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//      debugger.setLocationColor( Color.GREEN );
//      debugger.setLocationStrokeWidth( 1 );
//      debugger.add( XXX );
//      apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        // Collision detection
        electromagnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( electromagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        ElectromagnetControlPanel controlPanel = new ElectromagnetControlPanel( this, 
                batteryModel, compassModel, _gridGraphic, fieldMeterGraphic );
        this.setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
    
    //----------------------------------------------------------------------------
    // ICompassGridModule implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridSpacing(int, int)
     */
    public void setGridSpacing( int xSpacing, int ySpacing ) {
        _gridGraphic.setSpacing( xSpacing, ySpacing );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridXSpacing()
     */
    public int getGridXSpacing() {
        return _gridGraphic.getXSpacing();
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridYSpacing()
     */
    public int getGridYSpacing() {
        return _gridGraphic.getYSpacing();
    }  
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridNeedleSize(Dimension)
     */
    public void setGridNeedleSize( Dimension size ) {
        _gridGraphic.setNeedleSize( size );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridNeedleSize()
     */
    public Dimension getGridNeedleSize() {
        return _gridGraphic.getNeedleSize();
    }
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setAlphaEnabled(boolean)
     */
    public void setAlphaEnabled( boolean enabled ) {
        _gridGraphic.setAlphaEnabled( enabled );
    }
}
