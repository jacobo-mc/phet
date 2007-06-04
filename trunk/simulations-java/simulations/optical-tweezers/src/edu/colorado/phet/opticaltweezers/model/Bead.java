/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * Bead is the model of a glass bead, the dialectric particle in this experiement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bead extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_DIAMETER = "diameter";
    public static final String PROPERTY_DT_SUBDIVISION_THRESHOLD = "dtSubdivisionThreshold";
    public static final String PROPERTY_NUMBER_OF_DT_SUBDIVISION = "numberOfDtSubdivisions";
    public static final String PROPERTY_BROWNIAN_MOTION_SCALE = "brownianMotionScale";
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    // Debugging output for the motion algorithm
    private static final boolean MOTION_DEBUG_OUTPUT = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _diameter; // nm
    private final double _density; // kg/nm^3
    private Fluid _fluid;
    private Laser _laser;
    private Random _stepAngleRandom;
    private boolean _motionEnabled;
    private Vector2D _velocity; // nm/sec
    private DNAStrand _dnaStrand;
    
    private DoubleRange _dtSubdivisionThresholdRange;
    private IntegerRange _numberOfDtSubdivisionsRange;
    private DoubleRange _brownianMotionScaleRange;
    
    private double _dtSubdivisionThreshold;
    private int _numberOfDtSubdivisions;
    private double _brownianMotionScale;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position nm
     * @param orientation radians
     * @param diameter nm
     * @param density g/nm^3
     * @param dtSubdivisionThresholdRange
     * @param numberOfDtSubdivisionsRange
     * @param brownianMotionScaleRange
     * @param fluid
     * @param laser
     */
    public Bead( Point2D position, 
            double orientation, 
            double diameter, 
            double density,
            DoubleRange dtSubdivisionThresholdRange,
            IntegerRange numberOfDtSubdivisionsRange,
            DoubleRange brownianMotionScaleRange,
            Fluid fluid, 
            Laser laser ) {
        
        super( position, orientation, 0 /* speed */ );
        
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0: " + diameter );
        }
        if ( density <= 0 ) {
            throw new IllegalArgumentException( "density must be > 0: " + density );   
        }
        
        _diameter = diameter;
        _density = density;
        _fluid = fluid;
        _laser = laser;
        _stepAngleRandom = new Random();
        _motionEnabled = true;
        _velocity = new Vector2D();
        
        _brownianMotionScaleRange = brownianMotionScaleRange;
        _dtSubdivisionThresholdRange = dtSubdivisionThresholdRange;
        _numberOfDtSubdivisionsRange = numberOfDtSubdivisionsRange;
        
        _brownianMotionScale = brownianMotionScaleRange.getDefault();
        _dtSubdivisionThreshold = dtSubdivisionThresholdRange.getDefault();
        _numberOfDtSubdivisions = numberOfDtSubdivisionsRange.getDefault();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the diameter.
     * 
     * @param diameter diameter (nm)
     */
    public void setDiameter( double diameter ) {
        if ( !( diameter > 0 ) ) {
            throw new IllegalArgumentException( "diameter must be > 0" );
        }
        if ( diameter != _diameter ) {
            _diameter = diameter;
            notifyObservers( PROPERTY_DIAMETER );
        }
    }
    
    /**
     * Gets the diameter.
     * 
     * @return diameter (nm)
     */
    public double getDiameter() {
        return _diameter;
    }
    
    /**
     * Gets the mass.
     * 
     * @return mass (g)
     */
    public double getMass() {
        double radius = ( _diameter / 2 );
        double volume = ( 4. / 3. ) * Math.PI * ( radius * radius * radius );
        return volume * _density;
    }
    
    /**
     * Enables and disables motion of the model, used when something else 
     * is determining the bead's position (eg, when the user is dragging the bead).
     * 
     * @param motionEnabled true or false
     */
    public void setMotionEnabled( boolean motionEnabled ) {
        _motionEnabled = motionEnabled;    
    }
    
    /**
     * Gets the Brownian force acting on the bead.
     * 
     * @return Vector2D
     */
    public Vector2D getBrownianForce() {
        //XXX not implemented
        return new Vector2D.Cartesian( 0, 0 );
    }
    
    /**
     * Gets the drag force acting on the bead at its current velocity.
     * 
     * @return Vector2D
     */
    public Vector2D getDragForce() {
        return _fluid.getDragForce( _velocity );
    }
    
    /**
     * Gets the optical trap force acting on the bead at its current location.
     * 
     * @return Vector2D
     */
    public Vector2D getTrapForce() {
        return _laser.getTrapForce( getX(), getY() );
    }
    
    /**
     * Gets the potential energy of the bead.
     * 
     * @return potential energy (mJ)
     */
    public double getPotentialEnergy() {
        return _laser.getPotentialEnergy( getX(), getY() );
    }
    
    /**
     * Attaches the bead to the head of a DNA strand.
     * 
     * @param dnaStrand
     */
    public void attachTo( DNAStrand dnaStrand ) {
        _dnaStrand = dnaStrand;
    }
    
    /**
     * Gets the DNA force, if the bead is attached to a DNA strand.
     * 
     * @return Vector2D, zero if the bead is not attached to a DNA strand
     */
    public Vector2D getDNAForce() {
        Vector2D dnaForce = null;
        if ( _dnaStrand != null ) {
            dnaForce = _dnaStrand.getForce();
        }
        else {
            dnaForce = new Vector2D.Cartesian( 0, 0 );
        }
        return dnaForce;
    }
    
    /**
     * Sets the scaling factor used to calculate Brownian motion.
     * Bigger values cause bigger motion.
     * 
     * @param brownianMotionScale
     */
    public void setBrownianMotionScale( double brownianMotionScale ) {
        if ( !_brownianMotionScaleRange.contains( brownianMotionScale ) ) {
            throw new IllegalArgumentException( "brownianMotionScale out of range: " + brownianMotionScale );
        }
        if ( brownianMotionScale != _brownianMotionScale ) {
            _brownianMotionScale = brownianMotionScale;
            notifyObservers( PROPERTY_BROWNIAN_MOTION_SCALE );
        }
    }
    
    /**
     * Gets the scaling factor used to calculate Brownian motion.
     * 
     * @return double
     */
    public double getBrownianMotionScale() {
        return _brownianMotionScale;
    }
    
    public DoubleRange getBrownianMotionScaleRange() {
        return _brownianMotionScaleRange;
    }
    
    /**
     * Sets the subdivision threshold for the clock step.
     * Clock steps above this value will be subdivided as specified by
     * setNumberOfDtSubdivisions.
     * 
     * @param dtSubdivisionThreshold
     */
    public void setDtSubdivisionThreshold( double dtSubdivisionThreshold ) {
        if ( !_dtSubdivisionThresholdRange.contains( dtSubdivisionThreshold ) ) {
            throw new IllegalArgumentException( "dtSubdivisionThreshold out of range: " + dtSubdivisionThreshold );
        }
        if ( dtSubdivisionThreshold != _dtSubdivisionThreshold ) {
            _dtSubdivisionThreshold = dtSubdivisionThreshold;
            notifyObservers( PROPERTY_DT_SUBDIVISION_THRESHOLD );
        }
    }
    
    /**
     * Gets the subdivision threshold for the clock step.
     * 
     * @return threshold
     */
    public double getDtSubdivisionThreshold() {
        return _dtSubdivisionThreshold;
    }
    
    public DoubleRange getDtSubdivisionThresholdRange() {
        return _dtSubdivisionThresholdRange;
    }
    
    /**
     * Sets the number of subdivisions for the clock step.
     * This determines how many times the motion algorithm is run
     * each time the clock ticks.
     * 
     * @param numberOfDtSubdivisions
     */
    public void setNumberOfDtSubdivisions( int numberOfDtSubdivisions ) {
        if ( !_numberOfDtSubdivisionsRange.contains( numberOfDtSubdivisions ) ) {
            throw new IllegalArgumentException( "numberOfSubdivisions out of range: " + numberOfDtSubdivisions );
        }
        if ( numberOfDtSubdivisions != _numberOfDtSubdivisions ) {
            _numberOfDtSubdivisions = numberOfDtSubdivisions;
            notifyObservers( PROPERTY_NUMBER_OF_DT_SUBDIVISION );
        }
    }
    
    /**
     * Gets the number of subdivisions for the clock step.
     * 
     * @retun number of subdivisions
     */
    public int getNumberOfDtSubdivisions() {
        return _numberOfDtSubdivisions;
    }
    
    public IntegerRange getNumberOfDtSubdivisionsRange() {
        return _numberOfDtSubdivisionsRange;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( _motionEnabled ) {
            move( dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // Motion model
    //----------------------------------------------------------------------------
    
    /*
     * Bead motion algorithm.
     * 
     * Units:
     *     time - sec
     *     force - pN
     *     distance - nm
     *     velocity - nm/sec
     *     temperature - Kelvin
     *     
     * Constraints:
     *     direction of fluid flow must be horizontal
     */
    private void move( double clockDt ) {
        
        // Top and bottom edges of microscope slide, bead treated as a point
        final double yTopOfSlide = _fluid.getMinY() + ( getDiameter() / 2 ); // nm
        final double yBottomOfSlide = _fluid.getMaxY() - ( getDiameter() / 2 ); // nm
        
        // Mobility
        final double normalizedViscosity = _fluid.getDimensionlessNormalizedViscosity(); // unitless
        final double mobility = _fluid.getMobility(); // (nm/sec)/pN
        final Vector2D fluidVelocity = _fluid.getVelocity(); // nm/sec
        if ( fluidVelocity.getY() != 0 ) {
            throw new IllegalStateException( "bead motion algorithm requires horizontal fluid flow" );
        }
        
        // Old position and velocity
        double xOld = getX(); // nm
        double yOld = getY(); // nm
        double vxOld = _velocity.getX(); // nm/sec
        double vyOld = _velocity.getY(); // nm/sec
        
        // New position and velocity
        double xNew = 0;
        double yNew = 0;
        double vxNew = 0;
        double vyNew = 0;
        
        // Subdivide the clock step into N equals pieces
        double dt = clockDt;
        int loops = 1;
        if ( clockDt > ( 1.001 * _dtSubdivisionThreshold ) ) {
            dt = clockDt / _numberOfDtSubdivisions;
            loops = _numberOfDtSubdivisions;
        }
        
        // Run the motion algorithm for subdivided clock step
        for ( int i = 0; i < loops; i++ ) {

            // Trap force
            Vector2D trapForce = _laser.getTrapForce( xOld, yOld ); // pN

            // DNA force
            Vector2D dnaForce = null;
            if ( _dnaStrand != null ) {
                dnaForce = _dnaStrand.getForce();
            }
            else {
                dnaForce = new Vector2D.Cartesian( 0, 0 );
            }
                
            // Brownian displacement (nm)
            Vector2D brownianDisplacement = computeBrownianDisplacement( dt ); // nm

            // New position
            xNew = xOld + ( vxOld * dt ) + brownianDisplacement.getX(); // nm
            yNew = yOld + ( vyOld * dt ) + brownianDisplacement.getY(); // nm

            /*
             * Collision detection.
             * This is very simplified, because the only thing causing collisions
             * with the edges of the microscope slide is the Brownian force.
             */
            if ( yNew < yTopOfSlide ) {
                // collide with top edge of microscope slide
                yNew = yTopOfSlide;
            }
            else if ( yNew > yBottomOfSlide ) {
                // collide with bottom edge of microscope slide
                yNew = yBottomOfSlide;
            }
            
            // New velocity
            vxNew = ( mobility * trapForce.getX() ) + ( mobility * dnaForce.getX() ) + fluidVelocity.getX(); // nm/sec
            vyNew = ( mobility * trapForce.getY() ) + ( mobility * dnaForce.getY() ) + fluidVelocity.getY(); // nm/sec

            if ( MOTION_DEBUG_OUTPUT ) {
                System.out.println( "old position = " + new Point2D.Double( xOld, yOld ) + " nm" );
                System.out.println( "new position = " + new Point2D.Double( xNew, yNew ) + " nm" );
                System.out.println( "old velocity = [" + vxOld + "," + vyOld + "] nm/sec" );
                System.out.println( "new velocity = [" + vxNew + "," + vyNew + "] nm/sec" );
                System.out.println( "dt = " + dt );
                System.out.println( "normalized viscosity = " + normalizedViscosity );
                System.out.println( "mobility = " + mobility + " (nm/sec)/pN" );
                System.out.println( "fluid velocity = " + fluidVelocity + " nm/sec" );
                System.out.println( "trap force = " + trapForce + " pN" );
                System.out.println( "DNA force = " + dnaForce + " pN" );
                System.out.println( "Brownian displacement = " + brownianDisplacement + " nm" );
                System.out.println();
            }
            
            xOld = xNew;
            yOld = yNew;
            vxOld = vxNew;
            vyOld = vyNew;
        }
        
        // Set new values
        _velocity.setXY( vxNew, vyNew ); // nm/sec
        setPosition( xNew, yNew ); // nm
    }
    

    /*
     * Computes a random Brownian displacement.
     * 
     * @return displacement vector (nm)
     */
    private Vector2D computeBrownianDisplacement( double dt ) {
        
        final double normalizedViscosity = _fluid.getDimensionlessNormalizedViscosity(); // unitless
        final double fluidTemperature = _fluid.getTemperature(); // Kelvin
        
        final double stepLength = _brownianMotionScale * ( 2200 / Math.sqrt( normalizedViscosity ) ) * Math.sqrt( fluidTemperature / 300 ) * Math.sqrt( dt ); // nm
        double stepAngle = _stepAngleRandom.nextDouble() * ( 2 * Math.PI ); // radians
        
        return new Vector2D.Polar( stepLength, stepAngle );
    }
}
