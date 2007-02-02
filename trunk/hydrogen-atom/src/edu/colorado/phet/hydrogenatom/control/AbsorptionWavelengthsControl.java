/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Font;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.view.util.SimStrings;


public class AbsorptionWavelengthsControl extends JCheckBox {

    public AbsorptionWavelengthsControl( Font font ) {
        super();
        setText( SimStrings.get( "label.showAbsorptionWavelengths" ) );
        setFont( font );
        setOpaque( false );
    }
}
