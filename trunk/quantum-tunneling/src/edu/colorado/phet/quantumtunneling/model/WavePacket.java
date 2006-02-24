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

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.model.RichardsonSolver.RComplex;
import edu.colorado.phet.quantumtunneling.util.Distribution;
import edu.colorado.phet.quantumtunneling.util.Distribution.DistributionAccessor;


/**
 * WavePacket is the model of a wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WavePacket extends AbstractWave implements Observer, ClockListener {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TotalEnergy _te;
    private AbstractPotential _pe;
    private Direction _direction;
    private boolean _enabled;
    private double _width;
    private double _center;
    private RichardsonSolver _solver;
    private boolean _measureEnabled;
    private double _saveCenter;
    private double _saveWidth;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WavePacket() {
        super();
        _te = null;
        _pe = null;
        _direction = Direction.LEFT_TO_RIGHT;
        _enabled = true;
        _width = QTConstants.DEFAULT_PACKET_WIDTH;
        _center = QTConstants.DEFAULT_PACKET_CENTER;
        _solver = new RichardsonSolver( this );
        _measureEnabled = false;
        _saveCenter = _center;
        _saveWidth = _width;
    }
    
    public void cleanup() {
        if ( _te != null ) {
            _te.deleteObserver( this );
            _te = null;
        }
        if ( _pe != null ) {
            _pe.deleteObserver( this );
            _pe = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractWave implementation
    //----------------------------------------------------------------------------
    
    public boolean isInitialized() {
        return ( _te != null && _pe != null );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public RichardsonSolver getSolver() {
        return _solver;
    }
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
        }
    }

    public boolean isEnabled() {
        return _enabled;
    }
    
    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width must be > 0: " + width );
        }
        _width = width;
        update();
    }
    
    public double getWidth() {
        return _width;
    }
    
    public void setCenter( double center ) {
        _center = center;
        update();
    }
    
    public double getCenter() {
        return _center;
    }
    
    public void setTotalEnergy( TotalEnergy te ) {
        if ( _te != null ) {
            _te.deleteObserver( this );
        }
        _te = te;
        _te.addObserver( this );
        update();
    }

    public TotalEnergy getTotalEnergy() {
        return _te;
    }

    public void setPotentialEnergy( AbstractPotential pe ) {
        if ( _pe != null ) {
            _pe.deleteObserver( this );
        }
        _pe = pe;
        _pe.addObserver( this );
        update();
    }

    public AbstractPotential getPotentialEnergy() {
        return _pe;
    }

    public void setDirection( Direction direction ) {
        _direction = direction;
        update();
    }

    public Direction getDirection() {
        return _direction;
    }
    
    public boolean isLeftToRight() {
        return ( _direction == Direction.LEFT_TO_RIGHT );
    }
    
    public boolean isRightToLeft() {
        return ( _direction == Direction.RIGHT_TO_LEFT );
    }

    /**
     * Enables measuring.
     * Note that each time this is called with true, the wave packet should be measured again.
     * 
     * @param enabled true or false
     */
    public void setMeasureEnabled( boolean enabled ) {
        boolean wasMeasureEnabled = _measureEnabled;
        _measureEnabled = enabled;
        if ( enabled ) {
            if ( !wasMeasureEnabled ) {
                _saveCenter = _center;
                _saveWidth = _width;
            }
            setCenter( chooseRandomPosition() );
            setWidth( QTConstants.MEASURING_WIDTH );
        }
        else if ( !enabled && wasMeasureEnabled ) {
            setCenter( _saveCenter );
            setWidth( _saveWidth );
        }
        update();
    }
    
    /*
     * Select a random position, weighted by the probability density.
     * The probability that a particle is at a particular sample position
     * is given by abs( Psi[x,t] )^2 * dx, where dx is the space between
     * sample positions.
     * 
     * @return a position
     */
    private double chooseRandomPosition() {
        double center = 0;
        double[] positions = _solver.getPositions();
        RComplex[] energies = _solver.getEnergies();
        if ( positions.length > 1 ) {
            double dx = positions[1] - positions[0];
            Distribution distribution = new Distribution();
            for ( int i = 0; i < positions.length; i++ ) {
                double abs = energies[i].getAbs();
                double weight = abs * abs * dx;
                distribution.add( new Double( positions[i] ), weight );
            }
            DistributionAccessor accessor = new DistributionAccessor( distribution, new Random() );
            Object o = accessor.nextObject();
            if ( o != null ) {
                center = ((Double)o).doubleValue();
            }
            else {
                /* 
                 * If the distribution returned null, then it's likely because the 
                 * wave function solution is zero.  In this case, we want to 
                 * simply leave the wave function center where it is.
                 */
                center = getCenter(); 
            }
        }
        return center;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
       update();
    }
    
    private void update() {
        _solver.update();
        notifyObservers();
    }

    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------

    public void clockTicked( ClockEvent clockEvent ) {
        if ( _enabled && isInitialized() ) {
            _solver.propagate( QTConstants.RICHARDSON_STEPS_PER_CLOCK_TICK );
            notifyObservers();
        }
    }

    public void clockStarted( ClockEvent clockEvent ) {}

    public void clockPaused( ClockEvent clockEvent ) {}

    public void simulationTimeChanged( ClockEvent clockEvent ) {}

    public void simulationTimeReset( ClockEvent clockEvent ) {
        if ( _enabled && isInitialized() ) {
            update();
        }
    }
}
