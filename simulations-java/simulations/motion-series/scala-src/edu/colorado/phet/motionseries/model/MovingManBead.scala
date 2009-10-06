package edu.colorado.phet.motionseries.model

import common.motion.model.TimeData
import common.motion.MotionMath
import scalacommon.math.Vector2D
import scalacommon.util.Observable

//TODO: change from:
//MovingManBead extends ForceBead extends Bead
//to
//MovingManBead extends Bead
//ForceBead extends Bead

/**
 *
if (bead.mode == bead.velocityMode)        {
//compute acceleration as derivative of velocity
val acceleration = (bead.velocity - origState.velocity) / dt
bead.parallelAppliedForce = acceleration * bead.mass
} else if (bead.mode == bead.positionMode)        {

}
 */
class MovingManBead(_state: BeadState,
                    _height: Double,
                    _width: Double,
                    positionMapper: Double => Vector2D,
                    rampSegmentAccessor: Double => RampSegment,
                    model: Observable,
                    surfaceFriction: () => Boolean,
                    wallsBounce: () => Boolean,
                    __surfaceFrictionStrategy: SurfaceFrictionStrategy,
                    _wallsExist: => Boolean,
                    wallRange: () => Range,
                    thermalEnergyStrategy: Double => Double)
        extends ForceBead(_state, _height, _width, positionMapper, rampSegmentAccessor, model, surfaceFriction, wallsBounce, __surfaceFrictionStrategy, _wallsExist, wallRange, thermalEnergyStrategy) {

  //todo privatize
  object velocityMode
  object positionMode
  object accelerationMode

  private var _mode: AnyRef = accelerationMode

  def mode = _mode

  def setAccelerationMode() = {
    _mode = accelerationMode
  }

  def setVelocityMode() = {
    _mode = velocityMode
  }

  def setPositionMode() = {
    _mode = positionMode
    motionStrategy = new PositionMotionStrategy(this)
  }

  override def acceleration = {
    if (mode == positionMode) {
      if (stateHistory.length <= 3)
        0.0
      else {
        val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
        yield new TimeData(stateHistory(stateHistory.length - 1 - i).position, stateHistory(stateHistory.length - 1 - i).time)
        MotionMath.getSecondDerivative(timeData.toArray).getValue
      }
    }
    else if (mode == velocityMode) {
      //todo: maybe better to estimate 2nd derivative of position instead of 1st derivative of velocity?
      val timeData = for (i <- 0 until java.lang.Math.min(10, stateHistory.length))
      yield new TimeData(stateHistory(stateHistory.length - 1 - i).velocity, stateHistory(stateHistory.length - 1 - i).time)
      MotionMath.estimateDerivative(timeData.toArray)
    }
    else //if (mode == accelerationMode)
      {
        super.acceleration
      }
  }
}


class PositionMotionStrategy(bead: ForceBead) extends MotionStrategy(bead) {
  def getMemento = new MotionStrategyMemento {
    def getMotionStrategy(bead: ForceBead) = new PositionMotionStrategy(bead)
  }

  def stepInTime(dt: Double) = {
    //      println("position = " + bead.position + ", desired = " + bead.desiredPosition)
    //      bead.setPosition((bead.desiredPosition + bead.position) / 2) //attempt at filtering
    val mixingFactor = 0.5
    //maybe a better assumption is constant velocity or constant acceleration ?
    val dst = bead.desiredPosition * mixingFactor + bead.position * (1 - mixingFactor)
    bead.setPosition(dst) //attempt at filtering

    //todo: move closer to bead computation of acceleration derivatives
    val timeData = for (i <- 0 until java.lang.Math.min(15, bead.stateHistory.length))
    yield new TimeData(bead.stateHistory(bead.stateHistory.length - 1 - i).position, bead.stateHistory(bead.stateHistory.length - 1 - i).time)
    val vel = MotionMath.estimateDerivative(timeData.toArray)
    bead.setVelocity(vel)
    bead.setTime(bead.time + dt)
  }

  def position2D = bead.positionMapper(bead.position)

  def getAngle = 0.0
}