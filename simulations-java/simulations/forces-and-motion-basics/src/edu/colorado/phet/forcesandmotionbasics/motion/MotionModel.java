package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;

import static edu.colorado.phet.forcesandmotionbasics.motion.MotionCanvas.STROBE_SPEED;
import static edu.colorado.phet.forcesandmotionbasics.motion.SpeedValue.*;

/**
 * Model for tab 2 "Motion" and tab 3 "Friction", mutable values for the forces, position, velocity, etc.
 * Acceleration is computed dynamically from the sum of forces.
 * The mass is modeled in MotionCanvas.  The MotionCanvas also creates this model.
 *
 * @author Sam Reid
 */
public class MotionModel {

    public final DoubleProperty appliedForce = new DoubleProperty( 0.0 );
    public final DoubleProperty frictionForce = new DoubleProperty( 0.0 );
    public final DoubleProperty sumOfForces = new DoubleProperty( 0.0 );
    public final DoubleProperty velocity = new DoubleProperty( 0.0 );
    public final DoubleProperty position = new DoubleProperty( 0.0 );
    public final Property<Option<Double>> speed = new Property<Option<Double>>( new Some<Double>( 0.0 ) );
    private final boolean friction;
    public final ObservableProperty<Double> massOfObjectsOnSkateboard;
    public final BooleanProperty fallen = new BooleanProperty( false );

    //Internal value for whether speed is exceeded, always exactly up-to-date
    public final Property<SpeedValue> _speedValue = new Property<SpeedValue>( WITHIN_ALLOWED_RANGE );

    //Public value for whether a speed should be treated as exceeded.  Once a value is exceeded it is marked as exceeded for 1 second to avoid quirky behavior with slider disabling and re-enabling too quickly
    public final Property<SpeedValue> speedValue = new Property<SpeedValue>( _speedValue.get() );

    //Only used in Tab 3 "Friction"
    public final SettableProperty<Double> frictionValue = new Property<Double>( FrictionSliderControl.MAX / 2 );//The coefficient of friction (mu_k = mu_s)
    private Pair<Long, SpeedValue> lastOutOfRange = null;

    public MotionModel( boolean friction, ObservableProperty<Double> massOfObjectsOnSkateboard ) {
        this.friction = friction;
        this.massOfObjectsOnSkateboard = massOfObjectsOnSkateboard;
        new RichSimpleObserver() {
            @Override public void update() {
                updateForces();
            }
        }.observe( frictionValue, massOfObjectsOnSkateboard );
    }

    public void stepInTime( final double dt ) {
        double sumOfForces = updateForces();

        final double mass = massOfObjectsOnSkateboard.get();
        double acceleration = mass != 0 ? sumOfForces / mass : 0.0;

        double newVelocity = velocity.get() + acceleration * dt;

        //friction force should not be able to make the object move backwards
        //Also make sure velocity goes exactly to zero when the pusher is pushing so that the friction force will be correctly computed
        //Without this logic, it was causing flickering arrows because the velocity was flipping sign and the friction force was flipping direction
        if ( MathUtil.getSign( newVelocity ) != MathUtil.getSign( velocity.get() ) ) {
            newVelocity = 0.0;
        }

        //Cap at strobe speed.  This is necessary so that a reverse applied force will take effect immediately, without these lines of code the pusher will stutter.
        if ( newVelocity > STROBE_SPEED ) { newVelocity = STROBE_SPEED; }
        if ( newVelocity < -STROBE_SPEED ) { newVelocity = -STROBE_SPEED; }

        velocity.set( newVelocity );
        position.set( position.get() + velocity.get() * dt );
        speed.set( new Some<Double>( Math.abs( velocity.get() ) ) );
        _speedValue.set( velocity.get() >= STROBE_SPEED ? RIGHT_SPEED_EXCEEDED :
                         velocity.get() <= -STROBE_SPEED ? LEFT_SPEED_EXCEEDED :
                         WITHIN_ALLOWED_RANGE );

        if ( _speedValue.get() != WITHIN_ALLOWED_RANGE ) {
            lastOutOfRange = new Pair<Long, SpeedValue>( System.currentTimeMillis(), _speedValue.get() );
            speedValue.set( _speedValue.get() );
        }
    }

    //The first part of stepInTime is to compute and set the forces.  But this is factored out because the forces must also be updated
    //When the user changes the friction force or mass while the sim is paused.
    public double updateForces() {
        double appliedForce = this.appliedForce.get();
        double frictionForce = getFrictionForce( appliedForce );
        this.frictionForce.set( frictionForce );
        double sumOfForces = frictionForce + appliedForce;
        this.sumOfForces.set( sumOfForces );
        return sumOfForces;
    }

    private double getFrictionForce( final double appliedForce ) {
        if ( !friction ) { return 0.0; }
        double frictionForce = Math.abs( frictionValue.get() ) * MathUtil.getSign( appliedForce ) * massOfObjectsOnSkateboard.get();

        //Friction force only applies above this velocity
        final double velocityThreshold = 1E-12;
        if ( Math.abs( velocity.get() ) <= velocityThreshold && frictionForce > appliedForce ) {
            frictionForce = appliedForce;
        }
        else if ( Math.abs( velocity.get() ) > velocityThreshold ) {
            frictionForce = MathUtil.getSign( velocity.get() ) * frictionValue.get() * massOfObjectsOnSkateboard.get();
        }
        return -frictionForce;
    }

    public void rewind() {
        velocity.set( 0.0 );
        position.set( 0.0 );
        speed.set( new Some<Double>( 0.0 ) );
        appliedForce.set( 0.0 );
        frictionForce.set( 0.0 );
    }

    //Called whether paused or not.  When a second passes, the slider should gray in, if the speed value is no longer exceeded.
    public void clockStepped() {
        if ( lastOutOfRange != null ) {
            if ( System.currentTimeMillis() - lastOutOfRange._1 > 1000 ) {
                lastOutOfRange = null;
                speedValue.set( _speedValue.get() );
            }
        }
    }
}