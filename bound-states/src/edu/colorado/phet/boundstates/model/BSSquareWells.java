/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;
import edu.colorado.phet.boundstates.test.schmidt_lee.Wavefunction;


/**
 * BSSquareWell is the model of a potential composed of one or more Square wells.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>number of wells
 * <li>spacing
 * <li>offset
 * <li>width
 * <li>depth
 * </ul>
 * Offset, width, depth and spacing are identical for each well.
 * Spacing is irrelevant if the number of wells is 1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWells extends BSAbstractPotential {
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width;
    private double _depth;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareWells( int numberOfWells ) {
        this( numberOfWells, 
                BSConstants.DEFAULT_SQUARE_SPACING, 
                BSConstants.DEFAULT_SQUARE_WIDTH, 
                BSConstants.DEFAULT_SQUARE_DEPTH, 
                BSConstants.DEFAULT_SQUARE_OFFSET, 
                BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSSquareWells( int numberOfWells, double spacing, double width, double depth, double offset, double center ) {
        super( numberOfWells, spacing, offset, center );
        setWidth( width );
        setDepth( depth );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        _width = width;
        notifyObservers();
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        if ( depth < 0 ) {
            throw new IllegalArgumentException( "invalid depth: " + depth );
        }
        _depth = depth;
        notifyObservers();
    }
    
    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return BSWellType.SQUARE;
    }
    
    public int getStartingIndex() {
        return 1;
    }
    
    public double getEnergyAt( double position ) {
        
        final int n = getNumberOfWells();
        final double offset = getOffset();
        final double c = getCenter();
        final double s = getSpacing();
        final double w = getWidth();
        final double d = getDepth();
        
        double energy = offset;
        
        for ( int i = 1; i <= n; i++ ) {
            final double xi = s * ( i - ( ( n + 1 ) / 2.0 ) );
            if ( ( ( position - c ) >= xi - ( w / 2 ) ) && ( ( position - c ) <= xi + ( w / 2 ) ) ) {
                energy = offset - d;
                break;
            }
        }

        return energy;
    }
    
    public BSEigenstate[] getEigenstates() {
        ArrayList eigenstates = new ArrayList();

        for ( int nodes = 0; nodes < 10; nodes++ ) {
            try {
                PotentialFunction function = new PotentialFunctionAdapter( this );
                Wavefunction wavefunction = new Wavefunction( 0.5, -4, +4, 1000, nodes, function );
                double E = wavefunction.getE();
                eigenstates.add( new BSEigenstate( E ) );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
