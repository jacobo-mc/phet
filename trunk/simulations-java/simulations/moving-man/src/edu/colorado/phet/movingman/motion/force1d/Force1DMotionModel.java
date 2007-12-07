package edu.colorado.phet.movingman.motion.force1d;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 5, 2007 at 3:32:30 AM
 */
public class Force1DMotionModel extends MovingManMotionModel implements IForceModel {

    //handled in parent hierarchy: time, x,v,a

    private DefaultTemporalVariable appliedForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable frictionForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable wallForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable netForce = new DefaultTemporalVariable();

    private DefaultTemporalVariable gravity = new DefaultTemporalVariable();
    private DefaultTemporalVariable staticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable kineticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( 30 );

    private ControlGraphSeries appliedForceSeries = new ControlGraphSeries( "Fa", Color.blue, "Fa", "N", new BasicStroke( 2 ), null, appliedForce, true );
    private ControlGraphSeries frictionForceSeries = new ControlGraphSeries( "Ff", Color.red, "Ff", "N", new BasicStroke( 2 ), null, frictionForce, false );
    private ControlGraphSeries wallForceSeries = new ControlGraphSeries( "Fw", Color.magenta, "Fw", "N", new BasicStroke( 2 ), null, wallForce, false );
    private ControlGraphSeries netForceSeries = new ControlGraphSeries( "Fnet", Color.green, "Fnet", "N", new BasicStroke( 2 ), null, netForce, false );

    private ControlGraphSeries gravitySeries = new ControlGraphSeries( "Fg", Color.green, "a", "N", new BasicStroke( 2 ), null, gravity, true );
    private ControlGraphSeries staticFrictionSeries = new ControlGraphSeries( "us", Color.green, "", "m/s^2", new BasicStroke( 2 ), null, staticFriction, false );
    private ControlGraphSeries kineticFrictionSeries = new ControlGraphSeries( "uk", Color.green, "", "m/s^2", new BasicStroke( 2 ), null, kineticFriction, false );
    private ControlGraphSeries massSeries = new ControlGraphSeries( "m", Color.green, "a", "m/s^2", new BasicStroke( 2 ), null, mass, true );

    private UpdateStrategy appliedForceStrategy = new UpdateStrategy() {
        public void update( IMotionBody motionBody, double dt, double time ) {
            Force1DMotionModel model = (Force1DMotionModel) motionBody;//Force1DMotionModel.this
            model.getAccelDriven().update( motionBody, dt, time );
        }
    };

    public Force1DMotionModel( ConstantDtClock clock ) {
        super( clock );
        addTemporalVariables( getForce1DVars() );
        appliedForce.addListener( new IVariable.Listener() {
            public void valueChanged() {
                System.out.println( "appliedForceValue = " + appliedForce.getValue());
//                appliedForce.setValue( appliedForceValue );
                netForce.setValue( appliedForce.getValue() + frictionForce.getValue() + wallForce.getValue() );
                getAVariable().setValue( netForce.getValue() / mass.getValue() );
            }
        });
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        defaultUpdate( getForce1DVars() );
    }

    private ITemporalVariable[] getForce1DVars() {
        return new ITemporalVariable[]{appliedForce, frictionForce, wallForce, netForce, gravity, staticFriction, kineticFriction, mass};
    }

    public void setAppliedForce( double appliedForceValue ) {
        appliedForce.setValue( appliedForceValue );
    }

    public ControlGraphSeries getAppliedForceSeries() {
        return appliedForceSeries;
    }

    public DefaultTemporalVariable getAppliedForce() {
        return appliedForce;
    }

    public DefaultTemporalVariable getFrictionForce() {
        return frictionForce;
    }

    public DefaultTemporalVariable getWallForce() {
        return wallForce;
    }

    public DefaultTemporalVariable getNetForce() {
        return netForce;
    }

    public DefaultTemporalVariable getGravity() {
        return gravity;
    }

    public DefaultTemporalVariable getStaticFriction() {
        return staticFriction;
    }

    public DefaultTemporalVariable getKineticFriction() {
        return kineticFriction;
    }

    public DefaultTemporalVariable getMass() {
        return mass;
    }

    public ControlGraphSeries getFrictionForceSeries() {
        return frictionForceSeries;
    }

    public ControlGraphSeries getWallForceSeries() {
        return wallForceSeries;
    }

    public ControlGraphSeries getNetForceSeries() {
        return netForceSeries;
    }

    public ControlGraphSeries getGravitySeries() {
        return gravitySeries;
    }

    public ControlGraphSeries getStaticFrictionSeries() {
        return staticFrictionSeries;
    }

    public ControlGraphSeries getKineticFrictionSeries() {
        return kineticFrictionSeries;
    }

    public ControlGraphSeries getMassSeries() {
        return massSeries;
    }

    public UpdateStrategy getAppliedForceStrategy() {
        return appliedForceStrategy;
    }
}
