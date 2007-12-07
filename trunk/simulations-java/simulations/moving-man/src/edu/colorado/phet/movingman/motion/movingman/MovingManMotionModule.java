package edu.colorado.phet.movingman.motion.movingman;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.movingman.ArrowPanel;
import edu.colorado.phet.movingman.motion.AbstractMotionModule;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:42:37 PM
 */
public class MovingManMotionModule extends AbstractMotionModule implements ArrowPanel.IArrowPanelModule, OptionsMenu.MovingManOptions {
    private MovingManMotionModel movingManMotionModel;
    private MovingManMotionSimPanel movingManMotionSimPanel;
    public static double MIN_DT = MovingManMotionApplication.FRAME_DELAY_SEC / 2;
    public static double MAX_DT = MovingManMotionApplication.FRAME_DELAY_SEC * 2;

    public MovingManMotionModule( ConstantDtClock clock ) {
        super( "Moving Man", clock );
        movingManMotionModel = new MovingManMotionModel( clock );
        movingManMotionModel.addListener( new MovingManMotionModel.Adapter() {
            public void crashedMin( double v ) {
                playSound();
            }

            public void crashedMax( double v ) {
                playSound();
            }
        } );

        movingManMotionSimPanel = new MovingManMotionSimPanel( movingManMotionModel );
        setSimulationPanel( movingManMotionSimPanel );
        setClockControlPanel( new MovingManSouthControlPanel( this, this, movingManMotionModel.getTimeSeriesModel(), MIN_DT, MAX_DT ) );
        setLogoPanelVisible( false );
    }

    private void playSound() {
        super.playSound( "moving-man/audio/smash0.wav" );
    }

    public void activate() {
        super.activate();
        movingManMotionModel.startRecording();
    }

    public void setShowVelocityVector( boolean selected ) {
        movingManMotionSimPanel.setShowVelocityVector( selected );
    }

    public void setShowAccelerationVector( boolean selected ) {
        movingManMotionSimPanel.setShowAccelerationVector( selected );
    }

    public boolean confirmClear() {
        if ( getTimeSeriesModel().getRecordTime() == 0 ) {
            return true;
        }
        int option = JOptionPane.showConfirmDialog( movingManMotionSimPanel,
                                                    SimStrings.get( "plot.confirm-clear" ),
                                                    SimStrings.get( "plot.confirm-reset" ),
                                                    JOptionPane.YES_NO_CANCEL_OPTION );
        return option == JOptionPane.OK_OPTION;
    }

    public void setRightDirPositive( boolean b ) {
        movingManMotionSimPanel.setRightDirPositive( b );
    }

    public void setBoundaryOpen( boolean b ) {
        movingManMotionModel.setBoundaryOpen( b );
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return movingManMotionModel.getTimeSeriesModel();
    }

    public void setExpressionUpdate( String text ) {
        movingManMotionModel.setExpressionUpdate( text );
    }

}
