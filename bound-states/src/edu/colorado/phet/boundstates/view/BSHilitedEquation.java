/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.view;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;

/**
 * BSHilitedEquation displays the wave function equation of the hilited eigenstate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHilitedEquation extends BSAbstractWaveFunctionEquation implements Observer {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSHilitedEquation() {
        super();
    }
    
    //----------------------------------------------------------------------------
    // BSAbstractWaveFunctionEquation implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color scheme.
     * The color of the text is set to the color used for the hilited eigenstate.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {
        setHTMLColor( colorScheme.getEigenstateHiliteColor() );
    }
    
    /*
     * Gets the hilited eigenstate's subscript.
     * If an eigenstate is hilited, then its subscript is returned.
     * If no eigenstate is hilited, then EIGENSTATE_SUBSCRIPT_NONE is returned.
     */
    protected int getEigenstateSubscript() {
        BSModel model = getModel();
        final int hiliteIndex = model.getHilitedEigenstateIndex();
        int subscript = hiliteIndex;
        if ( subscript == BSEigenstate.INDEX_UNDEFINED ) {
            subscript = EIGENSTATE_SUBSCRIPT_NONE;
        }
        return subscript;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the display when the hilite eigenstate changes.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == getModel() && arg == BSModel.PROPERTY_HILITED_EIGENSTATE_INDEX ) {
            updateDisplay();
        }
    }
}
