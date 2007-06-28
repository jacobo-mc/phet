package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.timeseries.model.TimeModelClock;
import junit.framework.TestCase;

import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * Jun 20, 2007, 12:26:53 AM
 */
public class TestPositionDrivenOffsets extends TestCase {
    //    boolean verbose = true;
    boolean verbose = false;

    private void step( SwingClock clock, SingleBodyMotionModel motionModel, int i ) {
        for( int k = 0; k < i; k++ ) {
            clock.stepClockWhilePaused();
            if( verbose ) {
                showState( motionModel );
            }
        }
    }

    private void showState( SingleBodyMotionModel motionModel ) {
//        System.out.println( "t=" + motionModel.getTime() + ", x=" + motionModel.getPosition() + ", v=" + motionModel.getVelocity() + ", a=" + motionModel.getAcceleration() );
        System.out.println( "x.t=" + motionModel.getTime() + ", x=" + motionModel.getMotionBodyState().getPosition() + ", v=" + motionModel.getMotionBodyState().getVelocity() + ", a=" + motionModel.getMotionBodyState().getAcceleration() );
    }

    public void testDerivativeOffsets() {
//        testVelocityOffset( 1.0, 0.0, 100, 1.0, 100, 10, 1E-6 );
//        testVelocityOffset( 5.0, 0.0, 100, 2.0, 100, 6, 1E-6 );

        for( double dt = 1.0; dt <= 10; dt *= 2.0 ) {
            for( double x0 = -10; x0 < 10; x0 += 6 ) {
                for( double xFinal = -10; xFinal < 10; xFinal += 7 ) {
                    for( int maxWindowSize = 2; maxWindowSize <= 16; maxWindowSize += 2 ) {
                        if( xFinal > x0 ) {

                            if( verbose ) {

                                System.out.println( "############################################" );
                                System.out.println( "######################New Test##############" );
                                System.out.println( "############################################" );
                                System.out.println( "dt = " + dt + ", x0=" + x0 + ", xF=" + xFinal + ", windowSize=" + maxWindowSize );
                            }
                            testVelocityOffset( dt, x0, 100, xFinal, 100, maxWindowSize, 1E-6 );
                        }
                    }
                }
            }
        }
    }


    public void testVelocityOffset( double dt, double x0, int numStepsBefore, double xFinal, int numStepsAfter, int maxWindowSize, double tolerance ) {

        TimeModelClock clock = new TimeModelClock( 30, dt );
        SingleBodyMotionModel motionModel = new SingleBodyMotionModel( clock );
        motionModel.getMotionBodySeries().getPositionDriven().setVelocityWindow( maxWindowSize );
        motionModel.getMotionBodySeries().setPositionDriven();

        if( verbose ) {
            showState( motionModel );
        }
        motionModel.getMotionBodyState().setPosition( x0 );
        step( clock, motionModel, numStepsBefore );
        double t0 = motionModel.getTime();
        motionModel.getMotionBodyState().setPosition( xFinal );
        step( clock, motionModel, 1 );
        double t1 = motionModel.getTime();
        step( clock, motionModel, numStepsAfter );

        double timeXChanged = ( t1 + t0 ) / 2.0;
        double timeForMaxVelocity = motionModel.getMotionBodySeries().getMaxVelocity().getTime();
        double zeroAccelTime = ( motionModel.getMotionBodySeries().getMaxAcceleration().getTime() + motionModel.getMotionBodySeries().getMinAcceleration().getTime() ) / 2.0;
//        System.out.println( "timeXChanged=" + timeXChanged + ", timeForMaxVelocity=" + timeForMaxVelocity + ", zeroAccelTime=" + zeroAccelTime );

        if( verbose ) {
            DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
            for( int i = 0; i < motionModel.getMotionBodySeries().getVelocitySampleCount(); i++ ) {
                System.out.println( "motionModel.getVelocity( i) = " + motionModel.getMotionBodySeries().getVelocity( i ).toString( decimalFormat ) );
            }
            System.out.println( "timeXChanged=" + timeXChanged + ", timeForMaxVelocity=" + timeForMaxVelocity );
        }
//        System.out.println( "timeXChanged=" + timeXChanged + ", timeForMaxVelocity=" + timeForMaxVelocity );
        assertEquals( "Acceleration should have max and min centered on time position changes", timeXChanged, zeroAccelTime, tolerance );
        assertEquals( "time X Changed should be same as when velocity peaks: dt=" + dt + ", x0=" + x0 + ", xF=" + xFinal + ", maxWindowSize=" + maxWindowSize, timeXChanged, timeForMaxVelocity, tolerance );
    }
}
