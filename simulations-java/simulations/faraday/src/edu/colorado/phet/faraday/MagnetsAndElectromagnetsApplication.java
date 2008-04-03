/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday;

import java.io.IOException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.faraday.control.menu.OptionsMenu;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.module.ElectromagnetModule;

/**
 * MagnetsAndElectromagnetsApplication is the main application 
 * for the "Magnets and Electromagnets" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnetsAndElectromagnetsApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public MagnetsAndElectromagnetsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    private void initModules() {
        
        BarMagnetModule barMagnetModule = new BarMagnetModule( true /* wiggleMeEnabled */);
        barMagnetModule.setClockControlPanelVisible( false );
        addModule( barMagnetModule );
        
        ElectromagnetModule electromagnetModule = new ElectromagnetModule();
        electromagnetModule.setClockControlPanelVisible( false );
        addModule( electromagnetModule );
    }

    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for the PhET Color Vision application.
     *
     * @param args command line arguments
     */
    public static void main( final String[] args ) throws IOException {

        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                PhetApplicationConfig config = new PhetApplicationConfig( args, FaradayConstants.FRAME_SETUP, 
                        FaradayResources.getResourceLoader(), FaradayConstants.FLAVOR_MAGNETS_AND_ELECTROMAGNETS );

                // Create the application.
                PhetApplication app = new MagnetsAndElectromagnetsApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}