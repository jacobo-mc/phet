package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.application.Module
import javax.swing.{JFrame, RepaintManager}
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.model._

//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: JFrame, 
		clock: ScalaClock, 
		name: String, 
		defaultBeadPosition: Double, 
		pausedOnReset: Boolean,
        initialAngle: Double)
        extends Module(name, clock) {
  def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)

  val motionSeriesModel = createMotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle)
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) motionSeriesModel.coordinateFrameModel.angle = 0)
  private var lastTickTime = System.currentTimeMillis

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    motionSeriesModel.update(dt)
    RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions()//todo: this still shows clipping of incorrect regions, maybe we need to repaint the entire area
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    if (elapsed < 25) {
      val toSleep = 25 - elapsed
      //      println("had excess time, sleeping: " + toSleep)
      Thread.sleep(toSleep) //todo: blocks swing event handler thread and paint thread, should run this clock loop in another thread
    }
    lastTickTime = System.currentTimeMillis
  })

  //pause on start/reset, and unpause (and start recording) when the user applies a force
  motionSeriesModel.setPaused(true)

  def resetRampModule(): Unit = {
    motionSeriesModel.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    //pause on startup/reset, and unpause (and start recording) when the user applies a force
    motionSeriesModel.setPaused(true)
    resetAll()
  }

  def resetAll() = {}
}