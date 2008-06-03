/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import java.awt.Frame;

import javax.swing.JPanel;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.defaults.AdvancedDefaults;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;

/**
 * ExperiementsModule is the "Climate Experiments" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AdvancedModule( Frame parentFrame ) {
        super( GlaciersStrings.TITLE_ADVANCED, AdvancedDefaults.CLOCK );
        setLogoPanel( null );
        setSimulationPanel( new JPanel() );
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void resetAll() {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public AdvancedConfig save() {
        AdvancedConfig config = new AdvancedConfig();
        //XXX
        return config;
    }

    public void load( AdvancedConfig config ) {
        //XXX
    }

}
