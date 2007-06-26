package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.movingman.MovingManApplication;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManMotionApplication {
    private JFrame frame;
    private SwingClock swingClock;

    public MovingManMotionApplication() {

        frame = new JFrame( "Test Moving Man Node" );
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 400 );
        PhetPCanvas phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setZoomEventHandler( new PZoomEventHandler() );
        frame.setContentPane( phetPCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        swingClock = new SwingClock( 30, 1.0 );
        final MotionModel motionModel = new MotionModel( swingClock );
        System.out.println( "motionModel.getTimeSeriesModel().getMode() = " + motionModel.getTimeSeriesModel().getMode() +" ispaused="+motionModel.getTimeSeriesModel().isPaused());

        MovingManNode movingManNode = new MovingManNode( motionModel );
        movingManNode.scale( 50 );
        movingManNode.translate( 10.5, 0 );
        phetPCanvas.addScreenChild( movingManNode );

        motionModel.setPositionDriven();
//        motionModel.setVelocityDriven();
//        motionModel.setVelocity( 0.1 );

        int MAX_T = 200;
        System.out.println( "motionModel.getTimeSeriesModel().getMode() = " + motionModel.getTimeSeriesModel().getMode() +" ispaused="+motionModel.getTimeSeriesModel().isPaused());
        GraphSetNode graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( SimStrings.get( "variables.position.abbreviation" ), new MotionControlGraph(
                        phetPCanvas, motionModel.getXVariable(), motionModel.getXTimeSeries(), SimStrings.get( "variables.position.abbreviation" ), SimStrings.get( "variables.position" ), -10, 10, Color.blue, new PImage( GraphSuiteSet.loadBlueArrow() ), motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MAX_T ) ),
                new MinimizableControlGraph( SimStrings.get( "variables.velocity.abbreviation" ), new MotionControlGraph(
                        phetPCanvas, motionModel.getVVariable(), motionModel.getVTimeSeries(), SimStrings.get( "variables.velocity.abbreviation" ), SimStrings.get( "variables.velocity" ), -1, 1, Color.red, new PImage( GraphSuiteSet.loadRedArrow() ), motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getVelocityDriven(), MAX_T ) ),
                new MinimizableControlGraph( SimStrings.get( "variables.acceleration.abbreviation" ), new MotionControlGraph(
                        phetPCanvas, motionModel.getAVariable(), motionModel.getATimeSeries(), SimStrings.get( "variables.acceleration.abbreviation" ), SimStrings.get( "variables.acceleration" ), -0.01, 0.01, Color.green, new PImage( GraphSuiteSet.loadGreenArrow() ), motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getAccelDriven(), MAX_T ) )
        } ) ) );

        graphSetNode.setAlignedLayout();
        graphSetNode.setBounds( 0, 0, 800, 600 );
        graphSetNode.setOffset( 0, 200 );
        phetPCanvas.addScreenChild( graphSetNode );
        phetPCanvas.requestFocus();
        phetPCanvas.addKeyListener( new PDebugKeyHandler() );
        swingClock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                System.out.println( "motionModel.getTimeSeriesModel().getMode() = " + motionModel.getTimeSeriesModel().getMode() +" ispaused="+motionModel.getTimeSeriesModel().isPaused());
            }
        });
    }

    private void start() {
        frame.setVisible( true );
        swingClock.start();
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SimStrings.getInstance().addStrings( MovingManApplication.localizedStringsPath );
                new MovingManMotionApplication().start();
            }
        } );
    }
}
