package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 11, 2006
 * Time: 1:34:20 AM
 * Copyright (c) Oct 11, 2006 by Sam Reid
 */

public class BodyShapeNode extends PhetPNode {
    private EnergySkateParkSimulationPanel ec3Canvas;
    private Body body;
    private PPath child;

    public BodyShapeNode( EnergySkateParkSimulationPanel ec3Canvas, Body body ) {
        this.ec3Canvas = ec3Canvas;
        this.body = body;
        child = new PPath( new Rectangle( -1, -1, 2, 2 ) );
        child.setStroke( null );
        child.setPaint( Color.red );
        addChild( child );
        Timer timer = new Timer( 5, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        timer.start();
    }

    private void update() {
//        Point2D attachPoint2D = body.getLocatedShape();
//        ec3Canvas.getPhetRootNode().worldToScreen( attachPoint2D );
//        setOffset( attachPoint2D );
//        child.set
    }
}
