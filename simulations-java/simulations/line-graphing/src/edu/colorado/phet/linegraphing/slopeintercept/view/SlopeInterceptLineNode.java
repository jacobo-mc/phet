// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Visual representation of a line, labeled with an equation in slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLineNode extends LineNode {

    public SlopeInterceptLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        super( line, graph, mvt );
    }

    // Creates the line's equation in slope-intercept form.
    protected PNode createEquationNode( Line line, PhetFont font, Color color ) {
        return new SlopeInterceptEquationNode( line, font, color );
    }
}
