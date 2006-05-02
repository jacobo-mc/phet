/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.controller.*;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

/**
 * MriApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriApplication extends PiccoloPhetApplication {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static String title = "Simplified MRI";
    private static String description = "A simplified model of magnetic resonance imaging";
    private static String version = "0.01";
    private static FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );



    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private  Module[] singleModule = new Module[]{
            new MriModuleA(),
    };

    private Module[] fullAppModules = new Module[]{
            new MriModuleA(),
            new HeadModule(),
            new ScanModule(),
            new ScanModuleB(),
    };

    private Module[] modules = fullAppModules;

    public MriApplication( String[] args ) {
//        super( args, title, description, version, frameSetup, PhetApplication.JTABBED_PANE );
//        super( args, title, description, version, frameSetup );
        super( args, title, description, version, frameSetup, PiccoloPhetApplication.PHET_TABBED_PANE );
        setModules( modules );
    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-d" ) ) {
                PhetUtilities.getPhetFrame().addMenu( new OptionMenu() );
            }
            if( arg.equals( "-singlemodule" ) ) {
                modules = singleModule;
            }
        }
    }

    public static void main( String[] args ) {
        SimStrings.init( args, MriConfig.STRINGS_BUNDLE_NAME );
        PhetApplication app = new MriApplication( args );
        app.startApplication();

    }
}
