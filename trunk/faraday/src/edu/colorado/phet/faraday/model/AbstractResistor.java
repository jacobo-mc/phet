/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * AbstractResistor is the abstract base class for all resistors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractResistor extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _resistance;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param ohms the resistance, in Ohms
     */
    public AbstractResistor( double ohms ) {
        setResistance( ohms );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the resistance, in ohms.
     * 
     * @param resistance the resistance, in ohms
     * @throws IllegalArgumentException if resistance < 0
     */
    public void setResistance( double resistance ) {
        if ( resistance < 0 ) {
            throw new IllegalArgumentException( "resistance must be >= 0: " + resistance );
        }
        if ( resistance != resistance ) {
            _resistance = resistance;
            notifyObservers();
        }
    }
    
    /**
     * Gets the resistance, in ohms.
     * 
     * @return the resistance
     */
    public double getResistance() {
        return _resistance;
    }
}