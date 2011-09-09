// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PNode that represents outlined text.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class OutlinePText extends PNode {

    // Font render context used for outline text.  Honestly, I (jblanco) don't
    // know much about font render contexts, but I found a Piccolo node called
    // PStyledText that did this, so I tried it, and it seems to work okay.
    // This should be tested in some other environments (e.g. Mac) too.
    private static FontRenderContext SWING_FRC = new FontRenderContext( null, true, false );

    /**
     * Constructor.
     *
     * @param text
     * @param font
     * @param fillColor
     * @param outlineColor
     */
    public OutlinePText( String text, Font font, Color fillColor, Color outlineColor, double outlineStrokeWidth ) {
        PPath textPPath = new PhetPPath( fillColor, new BasicStroke( (float) outlineStrokeWidth ), outlineColor );
        TextLayout textLayout = new TextLayout( text, font, SWING_FRC );
        textPPath.setPathTo( textLayout.getOutline( new AffineTransform() ) );
        addChild( textPPath );
    }

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D STAGE_SIZE = new PDimension( 800, 600 );
        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );

        // Add the text.
        OutlinePText outlineTextNode1 = new OutlinePText( "36 point plain", new PhetFont( 36 ), Color.PINK, Color.BLACK, 1 );
        outlineTextNode1.setOffset( 50, 50 );
        canvas.addWorldChild( outlineTextNode1 );
        OutlinePText outlineTextNode2 = new OutlinePText( "48 point bold", new PhetFont( 48, true ), Color.YELLOW, Color.BLACK, 1 );
        outlineTextNode2.setOffset( 50, 100 );
        canvas.addWorldChild( outlineTextNode2 );
        OutlinePText outlineTextNode3 = new OutlinePText( "24 point bold", new PhetFont( 24, true ), Color.GREEN, Color.BLACK, 1 );
        outlineTextNode3.setOffset( 50, 150 );
        canvas.addWorldChild( outlineTextNode3 );
        OutlinePText outlineTextNode4 = new OutlinePText( "72 point bold", new PhetFont( 72, true ), Color.MAGENTA, Color.BLACK, 2 );
        outlineTextNode4.setOffset( 50, 250 );
        canvas.addWorldChild( outlineTextNode4 );

        // Boiler plate Piccolo app stuff.
        JFrame frame = new JFrame( "Outline Text Test" );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
