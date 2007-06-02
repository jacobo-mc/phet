/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.timeseries.model;

import edu.colorado.phet.common.phetcommon.model.clock.*;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2005
 * Time: 11:00:14 AM
 */

public class TimeSeriesModel extends ClockAdapter {
    private RecordableModel recordableModel;
    private IClock clock;
    private TimeStateSeries series = new TimeStateSeries();
    private double maxRecordTime = Double.POSITIVE_INFINITY;

    private Mode.Record record = new Mode.Record( this );
    private Mode.Playback playback = new Mode.Playback( this );
    private Mode.Live live = new Mode.Live( this );

    private Mode mode = live;//the current mode.

    private ArrayList listeners = new ArrayList();

    public TimeSeriesModel( RecordableModel recordableModel, IClock clock ) {
        this.recordableModel = recordableModel;
        this.clock = clock;
        this.mode = live;
    }

    public boolean isPaused() {
        return clock.isPaused();
    }

    public void addPlaybackTimeChangeListener( final PlaybackTimeListener playbackTimeListener ) {
        playback.addListener( playbackTimeListener );
    }

    private double getLiveTime() {
        return live.getTime();
    }

    public double getPlaybackTime() {
        return playback.getPlaybackTime();
    }

    public void setPlaybackTime( double requestedTime ) {
        if( requestedTime > getRecordTime() ) {
            requestedTime = getRecordTime();
        }
        if( requestedTime >= 0 && requestedTime <= getRecordTime() && numPlaybackStates() > 0 ) {
            playback.setTime( requestedTime );
            recordableModel.setState( series.getTimeStateValue( requestedTime ) );
        }
    }

    public Mode getMode() {
        return mode;
    }

    public Mode.Playback getPlaybackMode() {
        return playback;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getRecordTime() {
        return record.getRecordTime();
    }

    public void setPaused( boolean paused ) {
        if( paused != clock.isPaused() ) {
            if( paused ) {
                clock.pause();
            }
            else {
                clock.start();
            }
            notifyPauseChanged();
        }
    }

    private void notifyPauseChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.pauseChanged();
        }
    }

    public void reset() {
        boolean origPauseState = isPaused();
        setPaused( true );
        record.reset();
        series.clear();
//        rewind();
        recordableModel.resetTime();
        setPaused( origPauseState );
        notifyDataSeriesChanged();
    }

    public boolean isLiveMode() {
        return mode == live;
    }

    public boolean isRecordMode() {
        return mode == record;
    }

    public void setRecordMode() {
        setMode( record );
    }

    public void confirmAndApplyReset() {
        if( confirmReset() ) {
            reset();
        }
    }

    private boolean confirmReset() {
        return true;
    }

    protected void setMode( Mode mode ) {
        boolean same = mode == this.mode;
        if( !same ) {
            this.mode = mode;
            notifyModeChanged();
            System.out.println( "Changed mode to: " + mode.getClass().getName() );
        }
    }

    private void notifyModeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.modeChanged();
        }
    }

    public void rewind() {
        setPlaybackTime( getRecordStartTime() );
    }

    private double getRecordStartTime() {
        if( series.numPoints() > 0 ) {
            return series.getStartTime();
        }
        else {
            return 0.0;
        }
    }

    private void notifyDataSeriesChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.dataSeriesChanged();
        }
    }

    public void startPlaybackMode( double playbackSpeed ) {
        //todo: set playback speed on clock, or maybe this functionality should be elsewhere.
        if( clock instanceof Clock ) {
            Clock clock1 = (Clock)clock;
            clock1.setTimingStrategy( new TimingStrategy.Constant( playbackSpeed ) );
        }
        setMode( playback );
        setPaused( false );
        notifyDataSeriesChanged();
    }

    public boolean isPlaybackMode() {
        return mode == playback;
    }

    public boolean isPlaybackMode( double speed ) {
        return isPlaybackMode() && getSpeed()==speed;
    }

    public double getSpeed() {
        return clock instanceof Clock ? ( (Clock)clock ).getTimingStrategy().getSimulationTimeChangeForPausedClock() : clock.getSimulationTimeChange();
    }

    public boolean isRecording() {
        return mode == record && !isPaused();
    }

    public TimeStateSeries getSeries() {
        return series;
    }

    public double getMaxRecordTime() {
        return maxRecordTime;
    }

    public void addSeriesPoint( Object state, double recordTime ) {
        series.addPoint( state, recordTime );
        notifyDataSeriesChanged();
    }

    public void startRecording() {
        setRecordMode();
        setPaused( false );
        notifyDataSeriesChanged();
    }

    public void startLiveMode() {
        setLiveMode();
        setPaused( false );
        notifyDataSeriesChanged();
    }

    public void setLiveMode() {
        setMode( live );
    }

    private void ifRecordTooMuchSwitchToLive() {
        int MAX = 1500;
        //todo magic number: this is the number of recorded points in the 40-second interval that shows up on the time series chart under a particular set of conditions.

        if( isRecordMode() && record.getTimeSeriesModel().getSeries().size() > MAX ) {
            setLiveMode();
            notifyDataSeriesChanged();
        }
    }

    public boolean isThereRecordedData() {
        return getSeries().size() > 0;
    }

    public boolean isFirstPlaybackPoint() {
        return getSeries().size() > 0 && isPlaybackMode() && playback.getPlaybackTime() == 0;
    }

    public void updateModel( double dt ) {
        recordableModel.stepInTime( dt );
    }

    public Object getModelState() {
        return recordableModel.getState();
    }

    public void clear() {
        series.clear();
        notifyDataSeriesChanged();
    }

    public int numPlaybackStates() {
        return series.numPoints();
    }

    public void setMaxRecordTime( double maxRecordTime ) {
        this.maxRecordTime = maxRecordTime;
    }

    /**
     * Returns the current time in the current mode.
     *
     * @return the current time in the current mode.
     */
    public double getTime() {
        if( isPlaybackMode() ) {
            return getPlaybackTime();
        }
        else if( isRecordMode() ) {
            return getRecordTime();
        }
        else if( isLiveMode() ) {
            return getLiveTime();
        }
        else {
            System.out.println( "Time not available for mode: " + getMode() );
            return Double.NaN;
        }
    }

    public void stepClockWhilePaused() {
        clock.stepClockWhilePaused();
    }

    public interface PlaybackTimeListener {
        public void timeChanged();
    }

    public void clockTicked( ClockEvent event ) {
        ifRecordTooMuchSwitchToLive();
        stepMode( event.getSimulationTimeChange() );
    }

    public void clockStarted( ClockEvent clockEvent ) {
        notifyPauseChanged();
    }

    public void clockPaused( ClockEvent clockEvent ) {
        notifyPauseChanged();
    }

    public void stepMode( double simulationTimeChange ) {
        mode.step( simulationTimeChange );
    }

    public void setPlaybackMode() {
        setMode( playback );
    }

    public static interface Listener {
        void dataSeriesChanged();

        void modeChanged();

        void pauseChanged();
    }

    public static class Adapter implements Listener {
        public void dataSeriesChanged() {
        }

        public void modeChanged() {
        }

        public void pauseChanged() {
        }
    }
}
