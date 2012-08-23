// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.buildafunction;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.functions.model.Functions;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class BuildAFunctionScene extends PNode {
    public BuildAFunctionScene() {
        addChild( new BinaryNumberFunctionNode( "+" ) );
        addChild( new BinaryNumberFunctionNode( "-" ) );
        addChild( new CopyNumberFunctionNode( "copy" ) );

        addChild( new UnaryFunctionNode( "\u27152", true, Functions.INTEGER_TIMES_2 ) );
        addChild( new UnaryFunctionNode( "+1", true, Functions.INTEGER_PLUS_1 ) );
        addChild( new UnaryFunctionNode( "-1", true, Functions.INTEGER_MINUS_1 ) );
        addChild( new UnaryFunctionNode( "^2", true, Functions.INTEGER_POWER_2 ) );

        addChild( new ValueNode( new ValueContext() {
            public void mouseDragged( final ValueNode valueNode, final PDimension delta ) {
                valueNode.translate( delta.width, delta.height );
            }

            public void mouseReleased( final ValueNode valueNode ) {
            }
        }, 3, new BasicStroke(), Color.white, Color.black, Color.black ) );
    }
}