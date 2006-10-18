package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 18, 2006
 * Time: 8:15:44 AM
 * Copyright (c) Oct 18, 2006 by Sam Reid
 */

public class EnergySkateParkFrameSetup implements FrameSetup {
    public void initialize( JFrame frame ) {
        if( Toolkit.getDefaultToolkit().getScreenSize().height <= 768 ) {
            new MaxExtent( new TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 100 ) ).initialize( frame );
        }
        else {
            new TopCenter( Toolkit.getDefaultToolkit().getScreenSize().width - 200, Toolkit.getDefaultToolkit().getScreenSize().height - 200 ).initialize( frame );
        }
//        new CenteredWithInsets(50,50).initialize( frame );
    }
}
