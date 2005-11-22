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

import edu.colorado.phet.quantumtunneling.QTConstants;



/**
 * AbstactPotential is the abstrat base class for all types of potential spaces.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractPotentialEnergy extends QTObservable implements IPotentialEnergy {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    protected static final double MIN_POSITION = QTConstants.POSITION_RANGE.getLowerBound();
    protected static final double MAX_POSITION = QTConstants.POSITION_RANGE.getUpperBound();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PotentialRegion[] _regions; // array of Point2D
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    protected AbstractPotentialEnergy( int numberOfRegions ) {
        super();
        
        if ( numberOfRegions <= 0 ) {
            throw new IllegalArgumentException( "numberOfRegions must be > 0" );
        }
        _regions = new PotentialRegion[ numberOfRegions ];
        if ( numberOfRegions == 1 ) {
            _regions[0] = new PotentialRegion( MIN_POSITION, MAX_POSITION, 0 );
        }
        else {
            for ( int i = 0; i < numberOfRegions; i++ ) {
                if ( i == 0 ) {
                    _regions[i] = new PotentialRegion( MIN_POSITION, i+1, 0 );
                }
                else if ( i == numberOfRegions - 1 ) {
                    _regions[i] = new PotentialRegion( i, MAX_POSITION, 0 );
                }
                else {
                    _regions[i] = new PotentialRegion( i, i+1, 0 ); 
                }
            }
        }
    }
    
    /**
     * Copy constructor.
     * 
     * @param potential
     */
    protected AbstractPotentialEnergy( AbstractPotentialEnergy potential ) {
        super();
        
        _regions = new PotentialRegion[ potential.getNumberOfRegions() ];
        for ( int i = 0; i < potential.getNumberOfRegions(); i ++ ) {
            double start = potential.getRegion( i ).getStart();
            double end = potential.getRegion( i ).getEnd();
            double energy = potential.getRegion( i ).getEnergy();
            setRegion( i, start, end, energy );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public int getNumberOfRegions() {
        return _regions.length;
    }

    public PotentialRegion[] getRegions() {
        return _regions;
    }
    
    public PotentialRegion getRegion( int index ) {
        validateIndex( index );
        return _regions[index];
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setRegion( int index, double start, double end, double energy ) {
        validateIndex( index );
        _regions[ index ] = new PotentialRegion( start, end, energy );
        notifyObservers();
    }
    
    protected void setStart( int index, double start ) {
        validateIndex( index );
        double end = getRegion( index ).getEnd();
        double energy = getRegion( index ).getEnergy();
        setRegion( index, start, end, energy ); 
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setEnd( int index, double end ) {
        validateIndex( index );
        double start = getRegion( index ).getStart();
        double energy = getRegion( index ).getEnergy();
        setRegion( index, start, end, energy );  
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    public void setEnergy( int index, double energy ) {
        validateIndex( index );
        double start = getRegion( index ).getStart();
        double end = getRegion( index ).getEnd();
        setRegion( index, start, end, energy );
    }
    
    public PotentialRegion getRegionAt( double position ) {
        PotentialRegion region = null;
        for ( int i = 0; i < getNumberOfRegions() && region == null; i++ ) {
            double start = getRegion( i ).getStart();
            double end = getRegion( i ).getEnd();
            if ( position >= start && position <= end ) {
                region = getRegion( i );
            }
        }
        if ( region == null ) {
            throw new NullPointerException( "region should not be null here!" );
        }
        return region;
    }
    
    public double getEnergyAt( double position ) {
        return getRegionAt( position ).getEnergy();
    }
       
    //----------------------------------------------------------------------------
    // Validation
    //----------------------------------------------------------------------------
    
    private void validateIndex( int index ) {
        if ( index < 0 || index > _regions.length - 1 ) {
            throw new IllegalArgumentException( "index out of range: " + index );
        }
    }
}
