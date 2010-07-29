package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.common.phetcommon.application.Module
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.common.phetcommon.util.SimpleObserver
import javax.swing.RepaintManager
import edu.colorado.phet.common.motion.charts.{TemporalChart, ChartCursor}
import edu.colorado.phet.motionseries.Predef._

//TODO: improve inheritance/composition scheme for different applications/modules/canvases/models
class MotionSeriesModule(frame: PhetFrame,
                         val clock: ScalaClock,
                         name: String,
                         defaultObjectPosition: Double,
                         pausedOnReset: Boolean,
                         initialAngle: Double,
                         fbdPopupOnly: Boolean)
        extends Module(name, clock) {
  TemporalChart.SEC_TEXT = "units.sec".translate; //see doc in SEC_TEXT
  def createMotionSeriesModel(defaultObjectPosition: Double, pausedOnReset: Boolean, initialAngle: Double) =
    new MotionSeriesModel(defaultObjectPosition, pausedOnReset, initialAngle)

  val motionSeriesModel = createMotionSeriesModel(defaultObjectPosition, pausedOnReset, initialAngle)

  private def updateCursorVisibility(model: MotionSeriesModel): Unit = {
    model.chartCursor.setVisible(motionSeriesModel.isPlayback && motionSeriesModel.getNumRecordedPoints > 0)
  }
  motionSeriesModel.addObserver(new SimpleObserver {
    def update: Unit = {
      updateCursorVisibility(motionSeriesModel)
    }
  })

  updateCursorVisibility(motionSeriesModel)

  motionSeriesModel.chartCursor.addListener(new ChartCursor.Adapter {
    override def positionChanged: Unit = {
      motionSeriesModel.setTime(motionSeriesModel.chartCursor.getTime)
    }
  })

  val fbdModel = new FreeBodyDiagramModel(fbdPopupOnly)
  val coordinateSystemModel = new AdjustableCoordinateModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListener(() => if (coordinateSystemModel.fixed) motionSeriesModel.coordinateFrameModel.proposedAngle = 0)

  private var lastTickTime = System.currentTimeMillis
  private var clockTickIndex = 0

  //This clock is always running; pausing just pauses the physics
  clock.addClockListener(dt => {
    val paintAndInputTime = System.currentTimeMillis - lastTickTime

    val startTime = System.currentTimeMillis
    motionSeriesModel.stepInTime(dt)
    //        RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions() //todo: this still shows clipping of incorrect regions, maybe we need to repaint the entire area
    //        getSimulationPanel.paintImmediately(0, 0, getSimulationPanel.getWidth, getSimulationPanel.getHeight)
    //    println("motionSeriesModel.bead.wallForce.magnitude="+motionSeriesModel.bead.wallForce.magnitude)

    //There is a bug that the instantantaneous (one frame) wall force is sometimes not shown or is clipped incorrectly
    //This workaround reduces the probability of having that problem significantly
    //The root of the problem might be that the wall force vector isn't updating at the right times
    //Note that this workaround will increase computational demand, and it will also occur whenever the user is pushing the block against the wall
    if (motionSeriesModel.motionSeriesObject.wallForce.magnitude > 1E-2) {
      getSimulationPanel.paintImmediately(0, 0, getSimulationPanel.getWidth, getSimulationPanel.getHeight)
      RepaintManager.currentManager(getSimulationPanel).paintDirtyRegions()
    }
    val modelTime = System.currentTimeMillis - startTime

    val elapsed = paintAndInputTime + modelTime
    //this policy causes problems on the mac, see #1832
    lastTickTime = System.currentTimeMillis
    clockTickIndex = clockTickIndex + 1
  })

  //pause on start/reset, and unpause (and start recording) when the user applies a force
  def resetPauseValue() = motionSeriesModel.setPaused(true)
  resetPauseValue()

  def resetRampModule(): Unit = {
    motionSeriesModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
    resetPauseValue()
    resetAll()
  }

  def resetAll() = {}

  override def deactivate() = {
    fbdModel.windowed = false //to ensure that fbd dialog doesn't show for this module while user is on a different module
    if (fbdModel.popupDialogOnly) fbdModel.visible = false
    super.deactivate()
  }
}