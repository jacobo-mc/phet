/* Copyright 2004, University of Colorado */

/*
 * CVS Info - 
 * Filename : $Source$
 * Branch : $Name$ 
 * Modified by : $Author$ 
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision;

import edu.colorado.phet.colorvision.phetcommon.application.ApplicationModel;
import edu.colorado.phet.colorvision.phetcommon.application.Module;
import edu.colorado.phet.colorvision.phetcommon.model.clock.SwingTimerClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * ColorVisionApplicationModel is the application model for the PhET Color
 * Vision simulations.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorVisionApplicationModel extends ApplicationModel {

    /**
     * Sole constructor.
     * 
     * @param title the application title
     * @param description the application description
     * @param version the application version
     * @param frameSetup info used to setup the application's frame
     */
    public ColorVisionApplicationModel( String title, String description, String version, FrameSetup frameSetup ) {

        super( title, description, version, frameSetup );

        // Clock
        boolean fixedDelay = true;
        this.setClock( new SwingTimerClock( ColorVisionConstants.TIME_STEP, ColorVisionConstants.WAIT_TIME, fixedDelay ) );

        // Simulation Modules
        Module rgbBulbsModule = new RgbBulbsModule( this );
        Module singleBulbModule = new SingleBulbModule( this );
        this.setModules( new Module[] { rgbBulbsModule, singleBulbModule } );

        // Start with the "RGB Bulbs" module.
        this.setInitialModule( rgbBulbsModule );
    }
}