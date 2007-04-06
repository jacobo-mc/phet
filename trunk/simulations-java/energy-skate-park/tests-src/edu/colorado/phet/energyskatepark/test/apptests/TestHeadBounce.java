package edu.colorado.phet.energyskatepark.test.apptests;

import edu.colorado.phet.energyskatepark.view.EC3LookAndFeel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 11:42:52 PM
 * Copyright (c) Oct 9, 2006 by Sam Reid
 */

public class TestHeadBounce {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestHeadBounce( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestHeadBounce( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
//        energySkateParkApplication.getModule().getEnergySkateParkModel().getBody( 0 ).convertToFreefall();
//        energySkateParkApplication.getModule().getEnergySkateParkModel().getBody( 0 ).setCMRotation( 0 );
        energySkateParkApplication.getModule().getEnergySkateParkModel().getBody( 0 ).setPosition( 5, 8 );
    }
}
