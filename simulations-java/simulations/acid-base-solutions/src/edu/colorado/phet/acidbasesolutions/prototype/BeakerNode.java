/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker that is filled to the top with a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private final Beaker beaker;
    private final WeakAcid solution;
    private final PPath outlineNode, solutionNode;
    private final GeneralPath outlinePath; 
    private final Rectangle2D solutionRectangle;
    
    public BeakerNode( Beaker beaker, WeakAcid solution ) {
        super();
        
        this.beaker = beaker;
        beaker.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        
        solutionRectangle = new Rectangle2D.Double();
        solutionNode = new PPath();
        solutionNode.setPaint( solution.getColor() );
        solutionNode.setStroke( null );
        addChild( solutionNode );
        
        outlinePath = new GeneralPath();
        outlineNode = new PPath();
        outlineNode.setPaint( null );
        outlineNode.setStroke( STROKE );
        outlineNode.setStrokePaint( STROKE_COLOR );
        addChild( outlineNode );
        
        update();
    }
    
    private void update() {
        updateBeaker();
        updateSolution();
    }
    
    private void updateBeaker() {
        double width = beaker.getWidth();
        double height = beaker.getHeight();
        double rimOffset = 20;
        outlinePath.reset();
        outlinePath.moveTo( (float) -width/2 - rimOffset, (float) -height/2 - rimOffset );
        outlinePath.lineTo( (float) -width/2, (float) -height/2 );
        outlinePath.lineTo( (float) -width/2, (float) +height/2 );
        outlinePath.lineTo( (float) +width/2, (float) +height/2 );
        outlinePath.lineTo( (float) +width/2, (float) -height/2 );
        outlinePath.lineTo( (float) +width/2 + rimOffset, (float) -height/2 - rimOffset );
        outlineNode.setPathTo( outlinePath );
    }
    
    private void updateSolution() {
        solutionNode.setPaint( solution.getColor() );
        double width = beaker.getWidth();
        double height = beaker.getHeight();
        solutionRectangle.setRect( -width/2, -height/2, width, height );
        solutionNode.setPathTo( solutionRectangle );
    }
}