package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 1:01:08 AM
 *
 */

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.motion.model.VelocityDriven;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class TestVelocityDriven {
    private JFrame frame;
    private Timer timer;
    private SingleBodyMotionModel rotationModel;

    public TestVelocityDriven() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        rotationModel = new SingleBodyMotionModel( new ConstantDtClock( 30, 1 ) );
        final VelocityDriven updateStrategy = new VelocityDriven();
        rotationModel.setUpdateStrategy( updateStrategy );
        final ModelSlider modelSlider = new ModelSlider( "Velocity", "m/s", -10, 10, rotationModel.getMotionBodyState().getVelocity() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rotationModel.getMotionBodyState().setVelocity( modelSlider.getValue() );
            }
        } );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );
        frame.setContentPane( modelSlider );
    }

    private void step() {
        rotationModel.stepInTime( 1.0 );
        DecimalFormat decimalFormat = new DecimalFormat( "0.000" );
//        System.out.println( decimalFormat.format( rotationModel.getLastState().getPosition() ) + "\t" + decimalFormat.format( rotationModel.getLastState().getVelocity() ) + "\t" + decimalFormat.format( rotationModel.getLastState().getAcceleration() ) );
    }

    public static void main( String[] args ) {
        new TestVelocityDriven().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
