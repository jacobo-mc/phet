// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * Factored out code that launched GravityAndOrbitsApplication and returns a reference to the created application, used by Teacher and Student.
 *
 * @author Sam Reid
 */
public class GAOHelper {
    public static Function0<GravityAndOrbitsApplication> createLauncher() {
        return new Function0<GravityAndOrbitsApplication>() {
            public GravityAndOrbitsApplication apply() {
                //TODO: this skips splash screen, statistics, etc.
                final GravityAndOrbitsApplication[] myapp = new GravityAndOrbitsApplication[1];
                Runnable runnable = new Runnable() {
                    public void run() {
                        GravityAndOrbitsApplication app = new GravityAndOrbitsApplication( new PhetApplicationConfig( new String[0], GravityAndOrbitsApplication.PROJECT_NAME ) );
                        app.startApplication();
                        myapp[0] = app;
                    }
                };
                if ( SwingUtilities.isEventDispatchThread() ) {
                    runnable.run();
                }
                else {
                    try {
                        SwingUtilities.invokeAndWait( runnable );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    catch ( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
                return myapp[0];
            }
        };
    }
}