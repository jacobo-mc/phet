/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.application.PhetApplication;
import smooth.SmoothLookAndFeelFactory;

import javax.swing.*;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 10:52:38 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaveInterferenceApplication extends PhetApplication {
    private static String VERSION = "0.00.10";

    public WaveInterferenceApplication( String[] args ) {
        super( args, "Wave Interference", "Wave Interference simulation", VERSION );

        addModule( new WaterModule() );
        addModule( new SoundModule() );
        addModule( new LightModule() );
        getPhetFrame().addMenu( new WaveInterferenceMenu() );
        if( getModules().length > 1 ) {
            for( int i = 0; i < getModules().length; i++ ) {
                getModule( i ).setLogoPanelVisible( false );
            }
        }
    }

    public static void main( String[] args ) {
        WaveIntereferenceLookAndFeel.initLookAndFeel();
//        System.out.println( "Arrays.ss = " + Arrays.s );
        if( Arrays.asList( args ).contains( "-smooth" ) ) {
            doSmooth();
        }
        new WaveInterferenceApplication( args ).startApplication();
    }

    private static void doSmooth() {
        try {
            final String systemLookAndFeelClassName = SmoothLookAndFeelFactory.getSystemLookAndFeelClassName();
            System.out.println( "systemLookAndFeelClassName = " + systemLookAndFeelClassName );
            UIManager.setLookAndFeel( systemLookAndFeelClassName );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
    }
}
