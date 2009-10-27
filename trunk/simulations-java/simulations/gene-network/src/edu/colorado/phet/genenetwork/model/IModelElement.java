package edu.colorado.phet.genenetwork.model;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public interface IModelElement {
    Shape getShape();
    Point2D getPositionRef();
    void setPosition(Point2D newPosition);
    void setPosition(double xPos, double yPos);
    Vector2D getVelocityRef();
    void setVelocity(Vector2D newVelocity);
    void setVelocity(double xVel, double yVel);
}
