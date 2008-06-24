/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.umd.cs.piccolox.pswing.PComboBox;


public class LiquidComboBox extends PComboBox {
    
    private static final Object NO_CHOICE = PHScaleStrings.CHOICE_CHOOSE_LIQUID;
    private static final Font FONT = PHScaleConstants.CONTROL_FONT;
    
    public LiquidComboBox() {
        super();
        setFont( FONT );
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
        setBackground( Color.WHITE );
        
        addItem( NO_CHOICE );
        LiquidDescriptor[] choices = LiquidDescriptor.getChoices();
        for ( int i = 0; i < choices.length; i++ ) {
            addItem( choices[i] );
        }
    }
    
    public void setChoice( LiquidDescriptor liquid ) {
        if ( liquid == null ) {
            setSelectedItem( NO_CHOICE );
        }
        else {
            setSelectedItem( liquid );  
        }
    }
    
    public LiquidDescriptor getChoice() {
        LiquidDescriptor choice = null;
        Object selectedItem = getSelectedItem();
        if ( selectedItem instanceof LiquidDescriptor ) {
            choice = (LiquidDescriptor) selectedItem;
        }
        return choice;
    }
}
