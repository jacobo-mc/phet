// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.view;

import fj.Effect;
import fj.F;
import fj.data.List;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo PNode that adds some functionality for improved interoperability with the Functional Java library.
 *
 * @author Sam Reid
 */
public class FNode extends RichPNode {
    public FNode() {
        this( List.<PNode>nil() );
    }

    public FNode( final List<? extends PNode> children ) {
        super( children.toCollection() );
    }

    //Function for adding children to this node
    public final Effect<PNode> addChild = new Effect<PNode>() {
        @Override public void e( PNode p ) {
            addChild( p );
        }
    };

    public static final F<PNode, Double> _fullWidth = new F<PNode, Double>() {
        @Override public Double f( final PNode pnode ) {
            return pnode.getFullBounds().getWidth();
        }
    };
}