/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 25, 2006
 * Time: 11:10:22 AM
 * Copyright (c) May 25, 2006 by Sam Reid
 */

public class EnergySkateParkDebugFrameSetup implements FrameSetup {

    public void initialize( JFrame frame ) {
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - EnergySkateParkModule.energyFrameWidth,
                       Toolkit.getDefaultToolkit().getScreenSize().height - 100 - EnergySkateParkModule.chartFrameHeight //for debug
        );
        frame.setLocation( 0, 0 );
    }

}
