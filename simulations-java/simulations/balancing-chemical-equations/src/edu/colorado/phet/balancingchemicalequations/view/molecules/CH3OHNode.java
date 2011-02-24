// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * CH3OH molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CH3OHNode extends PComposite {

    public CH3OHNode() {

        // atom nodes
        AtomNode leftNode = new AtomNode( new C() );
        AtomNode smallTopNode = new AtomNode( new H() );
        AtomNode smallBottomNode = new AtomNode( new H() );
        AtomNode smallLeftNode = new AtomNode( new H() );
        AtomNode rightNode = new AtomNode( new O() );
        AtomNode smallRightNode = new AtomNode( new H() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallBottomNode );
        parentNode.addChild( smallTopNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( smallLeftNode );
        parentNode.addChild( smallRightNode );
        parentNode.addChild( rightNode );

        // layout
        double x = 0;
        double y = 0;
        leftNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMaxX() + ( 0.25 * rightNode.getFullBoundsReference().getWidth() );
        y = leftNode.getYOffset();
        rightNode.setOffset( x, y );
        x = leftNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMinY();
        smallTopNode.setOffset( x, y );
        x = smallTopNode.getXOffset();
        y = leftNode.getFullBoundsReference().getMaxY();
        smallBottomNode.setOffset( x, y );
        x = leftNode.getFullBoundsReference().getMinX();
        y = leftNode.getYOffset();
        smallLeftNode.setOffset( x, y );
        x = rightNode.getFullBoundsReference().getMaxX();
        y = rightNode.getYOffset();
        smallRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
