package edu.colorado.phet.therampscala.model

import java.awt.Color
import scalacommon.util.Observable

case class SurfaceType(name: String, imageFilename: String, strategy: Double => Double, color: Color) extends SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double) = strategy(objectFriction)
}

class SurfaceModel extends Observable with SurfaceFrictionStrategy {
  val surfaceTypes = SurfaceType("Ice", "robotmovingcompany/ice.gif", x => 0.0, new Color(154, 183, 205)) ::
          SurfaceType("Concrete", "robotmovingcompany/concrete.gif", x => x, new Color(146, 154, 160)) ::
          SurfaceType("Carpet", "robotmovingcompany/carpet.gif", x => x * 1.5, new Color(200, 50, 60)) :: Nil
  private var _surfaceType = surfaceTypes(1)

  def surfaceType = _surfaceType

  def surfaceType_=(x: SurfaceType) = {
    _surfaceType = x
    notifyListeners()
  }

  def getTotalFriction(objectFriction: Double) = _surfaceType.getTotalFriction(objectFriction)

  def resetAll() = surfaceType = surfaceTypes(1)
}