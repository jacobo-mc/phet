/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * AbstractControlSubPanel is the base class for sub-panels of the main control panel.
 * A sub-panel has a title and a border.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractControlSubPanel extends JPanel {

    public AbstractControlSubPanel( String title, Color titleColor, Font titleFont ) {
        super();
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 ); // top, left, bottom, right
        TitledBorder titledBorder = new TitledBorder( title );
        titledBorder.setTitleFont( titleFont );
        titledBorder.setTitleColor( titleColor );
        titledBorder.setBorder( BorderFactory.createLineBorder( titleColor, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder /* outside */, titledBorder /* inside */);
        setBorder( compoundBorder );
    }
}
