package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import canvas.LadybugCanvas
import java.awt.Color
import java.awt.geom.Rectangle2D
import model.LadybugModel
import controlpanel.{PathVisibilityModel, VectorVisibilityModel}

class AphidMazeCanvas(model: LadybugModel, vectorVisibilityModel: VectorVisibilityModel, pathVisibilityModel: PathVisibilityModel)
        extends LadybugCanvas(model: LadybugModel, vectorVisibilityModel: VectorVisibilityModel, pathVisibilityModel: PathVisibilityModel) {
  addScreenChild(new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.black))

  addNode(new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.black))
}