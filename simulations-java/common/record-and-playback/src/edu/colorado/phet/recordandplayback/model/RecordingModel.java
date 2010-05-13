package edu.colorado.phet.recordandplayback.model;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.scalacommon.record.RecordModelDefaults;
import edu.colorado.phet.scalacommon.util.Observable;

import java.util.ArrayList;

abstract class RecordModel<T> implements Observable {
    private final ArrayList<DataPoint<T>> recordHistory = new ArrayList<DataPoint<T>>();

    private boolean record = true;
    private boolean paused = true;
    private double time = 0.0;
    private double playbackIndexFloat = 0.0; //floor this to get playbackIndex
    private double playbackSpeed = 1.0;

    public abstract void stepRecord();

    public abstract void setPlaybackState(T state);

    public abstract int getMaxRecordPoints();

    public abstract void handleRecordStartedDuringPlayback();

    public void setStateToPlaybackIndex() {
        int playbackIndex = getPlaybackIndex();
        if (playbackIndex >= 0 && playbackIndex < recordHistory.size()) {
            setPlaybackState(recordHistory.get(getPlaybackIndex()).getState());
            time = recordHistory.get(getPlaybackIndex()).getTime();
        }
    }

    public void setPlayback(double speed) {
        setPlaybackSpeed(speed);
        setRecord(false);
    }

    public void rewind() {
        setPlaybackIndexFloat(0.0);
    }

    public void setTime(double t) {
        time = t;
    }

    public void resetAll() {
        record = true;
        paused = true;
        playbackIndexFloat = 0.0;
        playbackSpeed = 1.0;
        recordHistory.clear();
        time = 0;

        notifyListeners(); //todo: duplicate notification
    }

    public void setPlaybackSpeed(double speed) {
        if (speed != playbackSpeed) {
            playbackSpeed = speed;
            notifyListeners();
        }
    }

    public double getPlaybackIndexFloat() {
        return playbackIndexFloat;
    }

    public void setRecord(boolean rec) {
        if (record != rec) {
            record = rec;
            if (record) {
                clearHistoryRemainder();
                handleRecordStartedDuringPlayback();
            }

            notifyListeners();
        }
    }

    ArrayList<Listener> historyRemainderClearListeners = new ArrayList<Listener>();

    public static interface Listener {
        void historyRemainderCleared();
    }

    public void clearHistoryRemainder() {
        ArrayList<DataPoint<T>> keep = new ArrayList<DataPoint<T>>();
        for (int i = 0; i < recordHistory.size(); i++) {
            DataPoint<T> dataPoint = recordHistory.get(i);
            if (dataPoint.getTime() < time) {
                keep.add(dataPoint);
            }
        }

        recordHistory.clear();
        recordHistory.addAll(keep);
        //todo: notify listeners?
        for (Listener historyRemainderClearListener : historyRemainderClearListeners) {
            historyRemainderClearListener.historyRemainderCleared();
        }
    }

    public void stepPlayback() {
        if (getPlaybackIndex() < recordHistory.size()) {
            setStateToPlaybackIndex();
            time = recordHistory.get(getPlaybackIndex()).getTime();
            playbackIndexFloat = playbackIndexFloat + playbackSpeed;
            notifyListeners();
        } else {
            if (RecordModelDefaults.recordAtEndOfPlayback()) {
                setRecord(true);
            }

            if (RecordModelDefaults.pauseAtEndOfPlayback()) {
                setPaused(true);
            }
        }
    }

    ArrayList<Listener2> historyClearListeners = new ArrayList<Listener2>();

    interface Listener2 {
        void historyCleared();
    }

    public void clearHistory() {
        recordHistory.clear();
        notifyListeners();
        for (Listener2 historyClearListener : historyClearListeners) {
            historyClearListener.historyCleared();
        }
    }

    public boolean isPlayback() {
        return !record;
    }

    public boolean isRecord() {
        return record;
    }

    public void setPlaybackIndexFloat(double index) {
        playbackIndexFloat = index;
        setStateToPlaybackIndex();
        notifyListeners();
    }

    public void setPaused(boolean p) {
        if (paused != p) {
            paused = p;
            notifyListeners();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public int getPlaybackIndex() {
        return (int) Math.floor(playbackIndexFloat);
    }

    public boolean isRecordingFull() {
        return recordHistory.size() >= getMaxRecordPoints();
    }

    public ArrayList<DataPoint<T>> getRecordingHistory() {
        return recordHistory;
    }

    public double getRecordedTimeRange() {
        if (recordHistory.size() == 0) {
            return 0;
        } else {
            return recordHistory.get(recordHistory.size() - 1).getTime() - recordHistory.get(0).getTime();
        }
    }

    public double getTime() {
        return time;
    }

    public double getMaxRecordedTime() {
        if (recordHistory.size() == 0) return 0.0;
        else return recordHistory.get(recordHistory.size() - 1).getTime();
    }

    public double getMinRecordedTime() {
        if (recordHistory.size() == 0) return 0.0;
        else return recordHistory.get(0).getTime();
    }

    public void setPlaybackTime(double time) {
        Function.LinearFunction f = new Function.LinearFunction(getMinRecordedTime(), getMaxRecordedTime(), 0, recordHistory.size() - 1);
        setPlaybackIndexFloat(f.evaluate(time));
    }

    public double getFloatTime() {
        Function.LinearFunction f = new Function.LinearFunction(0, recordHistory.size() - 1, getMinRecordedTime(), getMaxRecordedTime());
        return f.evaluate(playbackIndexFloat);
    }
}