// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight;

import edu.colorado.phet.bendinglight.modules.intro.IntroModule;
import edu.colorado.phet.bendinglight.modules.prisms.PrismsModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * @author Sam Reid
 */
public class BendingLightApplication extends PiccoloPhetApplication {
    private static final String NAME = "bending-light";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public BendingLightApplication( PhetApplicationConfig config ) {
        super( config );
        final IntroModule introModule = new IntroModule();
        addModule( introModule );
        addModule( new PrismsModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, BendingLightApplication.class );
    }
}
