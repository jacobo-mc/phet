package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.RotationFrameSetup;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.controls.RotationDevMenu;
import edu.colorado.phet.rotation.controls.RotationTestMenu;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

import javax.swing.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:56:31 AM
 */
public class TorqueApplication extends PiccoloPhetApplication {
    private TorqueModule rotationModule;

    public TorqueApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance(), "torque" ) );
        rotationModule = new TorqueModule( getPhetFrame() );
        addModule( rotationModule );
        getPhetFrame().addMenu( new RotationDevMenu( this, rotationModule ) );
        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PSwingRepaintManager synchronizedPSwingRepaintManager = new PSwingRepaintManager();
                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );
                new RotationLookAndFeel().initLookAndFeel();
                new TorqueApplication( args ).startApplication();
            }
        } );
    }
}
