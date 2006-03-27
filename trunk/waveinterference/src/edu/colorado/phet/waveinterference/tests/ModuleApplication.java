/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.PhetLookAndFeel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:18:50 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class ModuleApplication {
    public ModuleApplication() {
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.setFont( new Font( "Lucida Sans", Font.BOLD, 13 ) );
        phetLookAndFeel.apply();
//                SwingUtilities.updateComponentTreeUI( phetApplication.getPhetFrame() );
    }

    public void startApplication( String[]args, Module module ) {
        PhetApplication phetApplication = new PhetApplication( args, module.getName(), "", "" );
        phetApplication.addModule( module );
        phetApplication.startApplication();
    }
}
