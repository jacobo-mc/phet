// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.HighlightHandler.FunctionHighlightHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.intro.view.ReducedSlopeInterceptEquationFactory.ReducedSlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a line in slope-intercept form (y = mx + b), with arrows on both ends.
 * The line's equation (in reduced form) is positioned towards the tip, parallel with the line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLineNode extends PComposite {

    private static final PDimension ARROW_HEAD_SIZE = new PDimension( 10, 10 );
    private static final double LINE_THICKNESS = 3;
    private static final double LINE_EXTENT = 25; // how far the line extends past the grid
    private static final PhetFont EQUATION_FONT = new PhetFont( Font.BOLD, 18 );

    private final DoubleArrowNode arrowNode;
    private final ReducedSlopeInterceptEquationNode equationNode;

    // This constructor adds highlighting on mouseOver.
    public SlopeInterceptLineNode( SlopeInterceptLine line, LineGraph graph, ModelViewTransform mvt, final Color color, final Color highlightColor ) {
        this( line, graph, mvt, color );
        addInputEventListener( new FunctionHighlightHandler( new VoidFunction1<Boolean>() {
            public void apply( Boolean highlighted ) {
                updateColor( highlighted ? highlightColor : color );
            }
        } ) );
    }

    public SlopeInterceptLineNode( SlopeInterceptLine line, LineGraph graph, ModelViewTransform mvt, Color color ) {

        final double xExtent = mvt.viewToModelDeltaX( LINE_EXTENT );
        final double yExtent = Math.abs( mvt.viewToModelDeltaY( LINE_EXTENT ) );

        double tailX, tailY, tipX, tipY;

        if ( line.run == 0 ) {
            // x = 0
            tailX = 0;
            tailY = graph.maxY + yExtent;
            tipX = 0;
            tipY = graph.minY - yExtent;
        }
        else if ( line.rise == 0 ) {
            // y = b
            tailX = graph.minX - xExtent;
            tailY = line.intercept;
            tipX = graph.maxY + yExtent;
            tipY = line.intercept;
        }
        else {

            // tail is the left-most end point. Compute x such that the point is inside the grid.
            tailX = graph.minX - xExtent;
            tailY = line.solveY( tailX );
            if ( tailY < graph.minY - yExtent ) {
                tailX = line.solveX( graph.minY - yExtent );
                tailY = line.solveY( tailX );
            }
            else if ( tailY > graph.maxY + yExtent ) {
                tailX = line.solveX( graph.maxY + yExtent );
                tailY = line.solveY( tailX );
            }

            // tip is the right-most end point. Compute x such that the point is inside the grid.
            tipX = graph.maxX + xExtent;
            tipY = line.solveY( tipX );
            if ( tipY < graph.minY - yExtent ) {
                tipX = line.solveX( graph.minY - yExtent );
                tipY = line.solveY( tipX );
            }
            else if ( tipY > graph.maxY + yExtent ) {
                tipX = line.solveX( graph.maxY + yExtent );
                tipY = line.solveY( tipX );
            }
        }

        // double-headed arrow
        Point2D tailLocation = new Point2D.Double( mvt.modelToViewX( tailX ), mvt.modelToViewY( tailY ) );
        Point2D tipLocation = new Point2D.Double( mvt.modelToViewX( tipX ), mvt.modelToViewY( tipY ) );
        arrowNode = new DoubleArrowNode( tailLocation, tipLocation, ARROW_HEAD_SIZE.getHeight(), ARROW_HEAD_SIZE.getWidth(), LINE_THICKNESS );
        arrowNode.setPaint( color );
        arrowNode.setStroke( null ); // DoubleArrowNode is a shape that we fill, no need to stroke
        addChild( arrowNode );

        // equation
        PNode equationParentNode = new PNode();
        addChild( equationParentNode );
        equationParentNode.setOffset( tipLocation );
        equationParentNode.setRotation( line.run == 0 ? Math.PI / 2 : -Math.atan( line.rise / line.run ) );
        equationNode = ReducedSlopeInterceptEquationFactory.createNode( line, color, EQUATION_FONT );
        PNode zeroOffsetNode = new ZeroOffsetNode( equationNode );
        equationParentNode.addChild( zeroOffsetNode );
        zeroOffsetNode.setOffset( -zeroOffsetNode.getFullBoundsReference().getWidth() - 12,
                                -zeroOffsetNode.getFullBoundsReference().getHeight() - 12 );
    }

    public void setEquationVisible( boolean visible ) {
        equationNode.setVisible( visible );
    }

    protected void updateColor( Color color ) {
        arrowNode.setPaint( color );
        equationNode.setEquationColor( color );
    }
}
