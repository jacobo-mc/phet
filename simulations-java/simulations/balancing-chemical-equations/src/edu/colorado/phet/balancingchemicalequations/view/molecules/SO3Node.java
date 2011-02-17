// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.O;
import edu.colorado.phet.balancingchemicalequations.model.Atom.S;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * SO3 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SO3Node extends PComposite {

    public SO3Node() {

        // atom nodes
        AtomNode centerNode = new AtomNode( new S() );
        AtomNode leftNode = new AtomNode( new O() );
        AtomNode rightNode = new AtomNode( new O() );
        AtomNode topNode = new AtomNode( new O() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( topNode );
        parentNode.addChild( leftNode );
        parentNode.addChild( centerNode );
        parentNode.addChild( rightNode );

        // layout
        double x = 0;
        double y = 0;
        centerNode.setOffset( x, y );
        y = centerNode.getFullBoundsReference().getMinX();
        topNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMinX();
        y = centerNode.getYOffset() + ( 0.25 * leftNode.getFullBoundsReference().getHeight() );
        leftNode.setOffset( x, y );
        x = centerNode.getFullBoundsReference().getMaxX();
        y = centerNode.getYOffset() + ( 0.25 * rightNode.getFullBoundsReference().getHeight() );
        rightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
