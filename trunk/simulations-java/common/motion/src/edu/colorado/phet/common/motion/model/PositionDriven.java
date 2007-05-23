package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.motion.MotionMath;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:37:32 PM
 */

public class PositionDriven implements UpdateStrategy {

    //todo: try 2nd order derivative directly from position data
    public void update( MotionModel model, double dt ) {
        double vel = MotionMath.estimateDerivative( model.getAvailablePositionTimeSeries( 10 ) );
        double acc = MotionMath.estimateDerivative( model.getAvailableVelocityTimeSeries( 10 ) );
        model.setAngularVelocity( vel );
        model.setAngularAcceleration( acc );
    }
}
