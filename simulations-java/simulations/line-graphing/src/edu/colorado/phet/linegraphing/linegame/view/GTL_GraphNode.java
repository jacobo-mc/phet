// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for the graph node in all "Graph the Line" (GTL) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_GraphNode extends GraphNode {

    private final PNode linesParent, manipulatorsParent;
    private final PNode slopeToolNode;

    public GTL_GraphNode( GTL_Challenge challenge ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( new LineNode( challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt ) );
        }

        // parent nodes, for maintaining rendering order
        linesParent = new PNode();
        manipulatorsParent = new PNode();

        // Slope tool
        slopeToolNode = new SlopeToolNode( challenge.guess, challenge.mvt );

        // rendering order
        addChild( linesParent );
        addChild( slopeToolNode );
        addChild( manipulatorsParent );
    }

    public void setSlopeToolVisible( boolean visible ) {
        slopeToolNode.setVisible( visible );
    }

    protected void addLineNode( PNode node ) {
        linesParent.addChild( node );
    }

    protected void addManipulatorNode( PNode node ) {
        manipulatorsParent.addChild( node );
    }

    // Changes the visibility of the "answer" line.
    public abstract void setAnswerVisible( boolean visible );
}
