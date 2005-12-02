/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;


/**
 * StepPotential describes a potential space that contains a single step.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class StepPotential extends AbstractPotentialSpace {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_STEP_POSITION = 5;
    private static final double DEFAULT_STEP_ENERGY = 5;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public StepPotential() {
        super( 2 /* numberOfRegions */ );
        setRegion( 0, getMinPosition(), DEFAULT_STEP_POSITION, 0 );
        setRegion( 1, getRegion( 0 ).getEnd(), getMaxPosition(), DEFAULT_STEP_ENERGY );
    }
    
    /**
     * Copy constructor.
     * 
     * @param step
     */
    public StepPotential( final StepPotential step ) {
        super( step );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the step's position.
     * The step is only moved if it the minumum region size isn't violated.
     * 
     * @param position
     * @return true or false
     */
    public boolean setStepPosition( final double position ) {
        if ( position == getMinPosition() || position == getMaxPosition() ) {
            throw new IllegalArgumentException( "position cannot be at min or max range" );
        }
        
        boolean success = false;
        if ( position - getRegion( 0 ).getStart() >= getMinRegionWidth() &&
                 getRegion( 1 ).getEnd() - position >= getMinRegionWidth() ) {
            setEnd( 0, position );
            setStart( 1, position );
            validateRegions();
            success = true;
        }
        return success;
    }
    
    /**
     * Gets the step's position.
     * 
     * @return position
     */
    public double getStepPosition() {
        return getRegion( 1 ).getStart();
    }
}
