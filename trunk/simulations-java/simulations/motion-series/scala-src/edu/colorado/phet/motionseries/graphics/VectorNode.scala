package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes._
import java.awt.geom.{Point2D}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.MotionSeriesDefaults

import edu.umd.cs.piccolo.nodes.{PImage}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesConfig
import java.awt.{Color}
import edu.colorado.phet.motionseries.model._
import MotionSeriesConfig._

/**
 * The VectorNode is the PNode that draws the Vector (e.g. a force vector) either in the free body diagram or directly on the object itself.
 *
 * todo: could improve performance by passing isContainerVisible:()=>Boolean and addContainerVisibleListener:(()=>Unit)=>Unit
 * @author Sam Reid
 */
class VectorNode(val transform: ModelViewTransform2D, val vector: Vector, val tailLocation: VectorValue, maxLabelDistance: Double) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 1), VectorHeadWidth(), VectorHeadWidth(), VectorTailWidth(), 0.5, true) {
    setPaint(vector.getPaint)
  }
  MotionSeriesConfig.VectorTailWidth.addListener(() => {arrowNode.setTailWidth(MotionSeriesConfig.VectorTailWidth.value)})
  MotionSeriesConfig.VectorHeadWidth.addListener(() => {
    arrowNode.setHeadWidth(MotionSeriesConfig.VectorHeadWidth.value)
    arrowNode.setHeadHeight(MotionSeriesConfig.VectorHeadWidth.value)
  })

  addChild(arrowNode)
  private val abbreviatonTextNode = {
    val html = new ShadowHTMLNode(vector.html) {
      setShadowColor(vector.color)
      setColor(Color.black)
      setShadowOffset(2, 2)
      setFont(new PhetFont(22, false))
    }
    //for performance, buffer these outlines; htmlnodes are very processor intensive, each outline is 5 htmlnodes and there are many per sim
    new PImage(html.toImage)
  }
  addChild(abbreviatonTextNode)

  //can't use def since eta-expansion makes == and array -= impossible, and we need to be able to remove this callback
  //todo: see if def eta-expansion causes problems elsewhere
  val update = () => {
    setVisible(vector.visible)
    if (vector.visible) { //skip expensive updates if not visible
      val viewTail = transform.modelToViewDouble(tailLocation())
      val viewTip = transform.modelToViewDouble(vector() + tailLocation())
      arrowNode.setTipAndTailLocations(viewTip, viewTail)

      val proposedLabelLocation = vector() * 0.6
      val minLabelDistance = maxLabelDistance / 2.0 //todo: improve heuristics for min label distance, or make it settable in the constructor

      var labelVector = if (proposedLabelLocation.magnitude > maxLabelDistance)
        new Vector2D(vector.angle) * maxLabelDistance
      else if (proposedLabelLocation.magnitude < minLabelDistance && proposedLabelLocation.magnitude > 1E-2)
        new Vector2D(vector.angle) * minLabelDistance
      else
        proposedLabelLocation

      val textLocation = {
        val centeredPt = transform.modelToViewDouble(labelVector + tailLocation())
        val deltaArrow = new Vector2D(vector.angle + PI / 2) * abbreviatonTextNode.getFullBounds.getWidth * 0.75 //move orthogonal to the vector itself
        deltaArrow + centeredPt
      }
      abbreviatonTextNode.setOffset(textLocation.x - abbreviatonTextNode.getFullBounds.getWidth / 2, textLocation.y - abbreviatonTextNode.getFullBounds.getHeight / 2)
      abbreviatonTextNode.setVisible(labelVector.magnitude > 1E-2)
    }
  }
  update()
  vector.addListener(update)
  tailLocation.addListener(update)

  setPickable(false)
  setChildrenPickable(false)

  def deleting() {
    vector.removeListener(update)
    tailLocation.removeListener(update)
  }
}

class BodyVectorNode(transform: ModelViewTransform2D,
                     vector: Vector,
                     offset: VectorValue,
                     bead: Bead)
        extends VectorNode(transform, vector, offset, MotionSeriesDefaults.BODY_LABEL_MAX_OFFSET) {
  def doUpdate() = {
    setOffset(bead.position2D)
    update()
  }

  bead.addListenerByName {
    doUpdate()
  }
  doUpdate()
}