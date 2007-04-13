/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.bernoulli.bernoulli.graphics.arrows;

import edu.colorado.phet.common.bernoulli.bernoulli.math.PhetVector;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 9:55:59 AM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class DisappearingArrow {
    Arrowhead head;
    ArrowTail tail;

    public DisappearingArrow(Color color, int tailWidth) {
        this(color, color, tailWidth, tailWidth * 3, tailWidth * 3);
    }

    public DisappearingArrow(Color tailColor, Color headColor, double tailWidth, int headWidth, int headHeight) {
        this.head = new Arrowhead(headColor, headWidth, headHeight);
        tail = new ArrowTail(tailWidth, tailColor);
    }

    public void drawLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        PhetVector vector = new PhetVector(x2 - x1, y2 - y1);
        double mag = vector.getMagnitude();
        if (mag <= head.getHeight())
            return;
        vector.setMagnitude(vector.getMagnitude() - head.getHeight());
        Point headPoint = new Point((int) vector.getX() + x1, (int) vector.getY() + y1);
        tail.paint(g2, x1, y1, headPoint.x, headPoint.y);
        head.paint(g2, headPoint.x, headPoint.y, (int) vector.getX(), (int) vector.getY());
    }

    public boolean headContains(Point point) {
        return head.contains(point);
    }

    public boolean tailContains(Point point) {
        return !headContains(point) && tail.contains(point);
    }

    public boolean contains(Point point) {
        return headContains(point) || tail.contains(point);
    }
}
