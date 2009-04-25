package edu.colorado.phet.therampscala.graphics


import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.SwingUtils
import common.piccolophet.PhetPCanvas
import java.awt.Color
import java.awt.event._

import java.awt.geom.Rectangle2D
import javax.swing.{JFrame, JDialog}
import model.{BeadVector, Bead, RampModel}
import scalacommon.math.Vector2D
import scalacommon.Predef._
import umd.cs.piccolo.PNode
import java.lang.Math._

class RampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel, frame: JFrame) extends DefaultCanvas(22, 20) {
  setBackground(new Color(200, 255, 240))

  addNode(new SkyNode(transform))
  addNode(new EarthNode(transform))

  addNode(new RampSegmentNode(model.rampSegments(0), transform))
  addNode(new RotatableSegmentNode(model.rampSegments(1), transform))

  addNode(new RampHeightIndicator(model.rampSegments(1), transform))
  addNode(new RampAngleIndicator(model.rampSegments(1), transform))

  addNode(new BeadNode(model.leftWall, transform, "barrier2.jpg"))
  addNode(new BeadNode(model.rightWall, transform, "barrier2.jpg"))
  addNode(new BeadNode(model.tree, transform, "tree.gif"))

  val cabinetNode = new DraggableBeadNode(model.beads(0), transform, "cabinet.gif")
  model.addListenerByName(cabinetNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(cabinetNode)

  addNode(new PusherNode(transform, model.beads(0), model.manBead))
  addNode(new AppliedForceSliderNode(model.beads(0), transform))

  addNode(new ObjectSelectionNode(transform, model))

  addNode(new CoordinateFrameNode(model, coordinateSystemModel, transform))

  val fbdWidth = RampDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable,PhetCommonResources.getImage("buttons/maximizeButton.png"))
  fbdNode.setOffset(10, 10)
  addNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    fbdNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val fbdWindow = new JDialog(frame, "Free Body Diagram", false)
  fbdWindow.setSize(600, 600)
  val canvas = new PhetPCanvas
  val windowFBDNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 600, 600, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable,PhetCommonResources.getImage("buttons/minimizeButton.png"))
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateNodeSize()
  })
  updateNodeSize()
  def updateNodeSize() = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val w = Math.min(canvas.getWidth, canvas.getHeight)
      val inset = 40
      windowFBDNode.setSize(w - inset*2, w - inset*2)
      windowFBDNode.setOffset(inset,inset)
    }
  }
  canvas.addScreenChild(windowFBDNode)
  fbdWindow.setContentPane(canvas)
  var initted=false
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    val wasVisible = fbdWindow.isVisible
    fbdWindow.setVisible(freeBodyDiagramModel.visible && freeBodyDiagramModel.windowed)
    if (fbdWindow.isVisible && !wasVisible  && !initted) {
      initted=true
      SwingUtils.centerDialogInParent(fbdWindow)
    }
    updateNodeSize()
  }
  fbdWindow.addWindowListener(new WindowAdapter{
    override def windowClosing(e: WindowEvent) = freeBodyDiagramModel.visible=false
  })

  class VectorSetNode(transform: ModelViewTransform2D, bead: Bead) extends PNode {
    def addVector(a: Vector, offset: VectorValue) = {
      val node = new BodyVectorNode(transform, a, offset)
      addChild(node)
    }
  }

  class BodyVectorNode(transform: ModelViewTransform2D, vector: Vector, offset: VectorValue) extends VectorNode(transform, vector, offset) {
    model.beads(0).addListenerByName {
      setOffset(model.beads(0).position2D)
      update
    }
  }

  val vectorNode = new VectorSetNode(transform, model.beads(0))
  addNode(vectorNode)

  def addVector(a: Vector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double) = {
    fbdNode.addVector(a, offsetFBD)
    windowFBDNode.addVector(a, offsetFBD)

    val tailLocationInPlayArea = new VectorValue() {
      def addListenerByName(listener: => Unit) = {
        model.beads(0).addListenerByName(listener)
        vectorViewModel.addListenerByName(listener)
      }

      def getValue = {
        val defaultCenter = model.beads(0).height / 2.0
        model.beads(0).position2D + new Vector2D(model.beads(0).getAngle + PI / 2) *
                (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else a.getPointOfOriginOffset(defaultCenter)))
      }
    }
    val playAreaAdapter = new Vector(a.color, a.name, a.abbreviation) {
      def getValue = a.getValue * RampDefaults.PLAY_AREA_VECTOR_SCALE
    }
    vectorNode.addVector(playAreaAdapter, tailLocationInPlayArea)
  }

  def addVector(a: BeadVector): Unit = addVector(a, new ConstantVectorValue, 0)
  addVector(model.beads(0).appliedForceVector)
  addVector(model.beads(0).gravityForceVector)
  addVector(model.beads(0).normalForceVector)
  addVector(model.beads(0).frictionForceVector)
  addVector(model.beads(0).wallForceVector)
  addVector(model.beads(0).totalForceVector, new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2)


}
trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}