package edu.colorado.phet.common.motion.model;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.motion.graphs.IUpdateStrategy;

/**
 * This class contains a single MotionBody, and convenience methods for interacting with it.
 */
public class SingleBodyMotionModel extends MotionModel implements IPositionDriven, IUpdateStrategy {

    private MotionBody motionBody;

    public SingleBodyMotionModel( IClock clock ) {
        super( clock );
        motionBody = new MotionBody();
    }

    protected void setTime( double time ) {
        super.setTime( time );
        motionBody.setTime( time );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        motionBody.stepInTime( getTime(), dt );
    }

    public void clear() {
        super.clear();
        motionBody.clear();
    }

    public void setMaxAllowedRecordTime( double maxAllowedRecordTime ) {
        getTimeSeriesModel().setMaxAllowedRecordTime( maxAllowedRecordTime );
    }

    public MotionBodySeries getMotionBodySeries() {
        return motionBody.getMotionBodySeries();
    }

    public ISimulationVariable getXVariable() {
        return motionBody.getXVariable();
    }

    public ISimulationVariable getVVariable() {
        return motionBody.getVVariable();
    }

    public ISimulationVariable getAVariable() {
        return motionBody.getAVariable();
    }

    public MotionBodyState getMotionBodyState() {
        return motionBody.getMotionBodyState();
    }

    public void setPositionDriven() {
        getMotionBodySeries().setPositionDriven();
    }

    public ITimeSeries getXTimeSeries() {
        return getMotionBodySeries().getXTimeSeries();
    }

    public ITimeSeries getVTimeSeries() {
        return getMotionBodySeries().getVTimeSeries();
    }

    public ITimeSeries getATimeSeries() {
        return getMotionBodySeries().getATimeSeries();
    }

    public UpdateStrategy getPositionDriven() {
        return getMotionBodySeries().getPositionDriven();
    }

    public UpdateStrategy getVelocityDriven() {
        return getMotionBodySeries().getVelocityDriven();
    }

    public UpdateStrategy getAccelDriven() {
        return getMotionBodySeries().getAccelDriven();
    }

    public void setAccelerationDriven() {
        getMotionBodySeries().setAccelerationDriven();
    }

    public void setVelocityDriven() {
        getMotionBodySeries().setVelocityDriven();
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        getMotionBodySeries().setUpdateStrategy( updateStrategy );
    }

}
