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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.faraday.util.IRescaler;


/**
 * Voltmeter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Voltmeter extends SimpleObservable implements ModelElement {
  
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /*
     * Define the zero point of the needle.
     */
    private static final double ZERO_NEEDLE_ANGLE = Math.toRadians( 0.0 );
    
    /*
     * The needle deflection range is this much on either side of the zero point.
     */
    private static final double MAX_NEEDLE_ANGLE = Math.toRadians( 90.0 );
    
    /*
     * If rotational kinematics is enabled, the needle will jiggle this much around the zero reading.
     */
    private static final double NEEDLE_JIGGLE_ANGLE = Math.toRadians( 3.0 );
    
    /*
     * When the angle is this close to zero, the needle stops jiggling.
     */
    private static final double NEEDLE_JIGGLE_THRESHOLD = Math.toRadians( 0.5 );
    
    /*
     * Determines how much the needle jiggles around the zero point.
     * The value L should be such that 0 < L < 1.
     * If set to 0, the needle will not jiggle at all.
     * If set to 1, the needle will ocsillate forever.
     */
    private static final double NEEDLE_LIVELINESS = 0.8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // The voltage source that the voltmeter is connected to.
    private AbstractVoltageSource _voltageSourceModel;
    
    // Whether the voltmeter is enabled (ie, connected to the coil).
    private boolean _enabled;
    
    // Whether rotational kinematics behavior is enabled.
    private boolean _rotationalKinematicsEnabled;
    
    // Needle deflection angle
    private double _needleAngle;
    
    // Rescales the voltage.
    private IRescaler _rescaler;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param voltageSourceModel voltmeter is connected to this voltage source
     */
    public Voltmeter( AbstractVoltageSource voltageSourceModel ) {
        super();
        
        assert( voltageSourceModel != null );
        
        _voltageSourceModel = voltageSourceModel;
        
        _enabled = true;
        _rotationalKinematicsEnabled = false; // expensive, so disabled by default
        _needleAngle = ZERO_NEEDLE_ANGLE;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Set the rescaler, applied to the voltage read.
     * 
     * @param rescaler
     */
    public void setRescaler( IRescaler rescaler ) {
        _rescaler = rescaler;
    }
    
    /**
     * Enables or disables the state of the voltmeter.
     * 
     * @param enabled true to enable, false to disable.
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers();
        }
    }
    
    /**
     * Gets the state of the voltmeter.  See setEnabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Enables/disabled rotational kinematics behavior.
     * This turns on a Verlet algorithm that cause the compass needle to wobble.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setRotationalKinematicsEnabled( boolean enabled ) {
        if ( enabled != _rotationalKinematicsEnabled ) {
            _rotationalKinematicsEnabled = enabled;
            // No need to notify observers, handled by stepInTime.
        }
    }
    
    /**
     * Determines whether rotational kinematics behavior is enabled.
     * 
     * @return true if enabled, false if disabled
     */
    public boolean isRotationalKinematicsEnabled() {
        return _rotationalKinematicsEnabled;
    }
    
    /**
     * Get the voltage being read by the voltmeter.
     * If rotational kinematic is enabled, this may not correspond to
     * the needle's deflection angle.
     * 
     * @return the voltage, in volts
     */
    public double getVoltage() {
        return _voltageSourceModel.getVoltage();
    }
    
    /**
     * Sets the needle's deflection angle.
     * 
     * @param needleAngle the angle, in radians
     */
    protected void setNeedleAngle( double needleAngle ) {
        needleAngle = MathUtil.clamp( -MAX_NEEDLE_ANGLE, needleAngle, +MAX_NEEDLE_ANGLE );
        if ( needleAngle != _needleAngle ) {
            _needleAngle = needleAngle;
            notifyObservers();
        }
    }

    /**
     * Gets the needle's deflectin angle.
     * 
     * @return the angle, in radians
     */
    public double getNeedleAngle() {
        return _needleAngle;
    }
    
    /**
     * Gets the desired needle deflection angle.
     * This is the angle that corresponds exactly to the voltage read by the meter.
     * 
     * @return the angle, in radians
     */
    private double getDesiredNeedleAngle() {
        //  Convert the voltage to a value in the range -1...+1.
        double voltage = _voltageSourceModel.getVoltage() / _voltageSourceModel.getMaxVoltage();
        voltage = MathUtil.clamp( -1, voltage, +1 );
        
        // Rescale the voltage to improve the visual effect.
        if ( _rescaler != null ) {
            double sign = ( voltage < 0 ) ? -1 : +1;
            voltage = sign * _rescaler.rescale( Math.abs( voltage ) );
        }
        
        // Determine the needle deflection angle.
        return voltage * MAX_NEEDLE_ANGLE;
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the needle deflection angle.
     * If rotational kinematics are enabled, jiggle the needle around the zero point.
     * 
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public void stepInTime( double dt ) {
       if ( isEnabled() ) {

           // Determine the desired needle deflection angle.
           double needleAngle = getDesiredNeedleAngle();
           
           if ( ! _rotationalKinematicsEnabled ) {
               // If rotational kinematics is disabled, simply set the needle angle.
               setNeedleAngle( needleAngle );
           }
           else {
               // If rotational kinematics is enabled, make the needle jiggle around the zero point.
               if ( needleAngle != ZERO_NEEDLE_ANGLE ) {
                   setNeedleAngle( needleAngle );
               }
               else {
                   double delta = getNeedleAngle();
                   if ( delta == 0 ) {
                       // Do nothing, the needle is "at rest".
                   }
                   else if ( Math.abs( delta ) < NEEDLE_JIGGLE_THRESHOLD ) {
                       // The needle is close enought to "at rest".
                       setNeedleAngle( ZERO_NEEDLE_ANGLE );
                   }
                   else {
                       // Jiggle the needle around the zero point.
                       double jiggleAngle = -delta * NEEDLE_LIVELINESS;
                       jiggleAngle = MathUtil.clamp( -NEEDLE_JIGGLE_ANGLE, jiggleAngle, +NEEDLE_JIGGLE_ANGLE );
                       setNeedleAngle( jiggleAngle );
                   }
               }
           }
       }
    } // stepInTime
}
