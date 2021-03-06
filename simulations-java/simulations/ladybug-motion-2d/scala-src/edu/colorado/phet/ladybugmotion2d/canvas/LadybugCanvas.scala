package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.geom.{Rectangle2D, Point2D}
import java.awt.{Rectangle, Dimension, Color}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.ladybugmotion2d.LadybugDefaults
import edu.colorado.phet.ladybugmotion2d.model.LadybugModel
import edu.colorado.phet.scalacommon.CenteredBoxStrategy
import edu.colorado.phet.ladybugmotion2d.controlpanel.{Line, Dots, PathVisibilityModel, VectorVisibilityModel}

class LadybugCanvas(model: LadybugModel,
                    vectorVisibilityModel: VectorVisibilityModel,
                    pathVisibilityModel: PathVisibilityModel,
                    modelWidth: Double,
                    modelHeight: Double)
        extends PhetPCanvas(new Dimension(1024, 768)) {
  setWorldTransformStrategy(new CenteredBoxStrategy(768, 768, this))
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight),
                                                                 new Rectangle(0, 0, 768, 768), LadybugDefaults.POSITIVE_Y_IS_UP)
  val constructed = true
  updateWorldScale

  val worldNode = new PNode
  addWorldChild(worldNode)

  def addNode(node: PNode) {
    worldNode.addChild(node)
  }

  def addNode(index: Int, node: PNode) {
    worldNode.addChild(index, node)
  }

  setBackground(new Color(200, 255, 240))

  val ladybugNode = new LadybugNode(model, model.ladybug, transform, vectorVisibilityModel)
  addNode(ladybugNode)
  val dotTrace = new LadybugDotTraceNode(model, transform, pathVisibilityModel.pathType.valueEquals(Dots), 0.7)
  addNode(dotTrace)
  val fadeTrace = new LadybugFadeTraceNode(model, transform, pathVisibilityModel.pathType.valueEquals(Line), 0.7)
  addNode(fadeTrace)
  addNode(new ReturnLadybugButton(model, this))

  //todo: perhaps this should be a screen child

  def clearTrace() {
    dotTrace.clearTrace()
    fadeTrace.clearTrace()
  }

  def setLadybugDraggable(draggable: Boolean) {
    ladybugNode.setDraggable(draggable)
  }

  override def updateWorldScale() {
    super.updateWorldScale()
    if ( constructed ) {
      //make sure we aren't in the call from superclass
      //to go from pixels to model, must go backwards through canvas transform and modelviewtransform
      val topLeft = new Point2D.Double(0, 0)
      val bottomRight = new Point2D.Double(getWidth, getHeight)

      def tx(pt: Point2D) = {
        val intermediate = getWorldTransformStrategy.getTransform.inverseTransform(pt, null)
        val model = transform.viewToModel(intermediate.getX, intermediate.getY)
        model
      }
      val out = new Rectangle2D.Double()
      out.setFrameFromDiagonal(tx(topLeft).getX, tx(topLeft).getY, tx(bottomRight).getX, tx(bottomRight).getY)
      model.setBounds(out)
    }
  }
}