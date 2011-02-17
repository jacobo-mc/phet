// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * CH4 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CH4Node extends PComposite {

    public CH4Node() {

        // atom nodes
        AtomNode bigNode = new AtomNode( new C() );
        AtomNode smallTopLeftNode = new AtomNode( new H() );
        AtomNode smallTopRightNode = new AtomNode( new H() );
        AtomNode smallBottomLeftNode = new AtomNode( new H() );
        AtomNode smallBottomRightNode = new AtomNode( new H() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( smallTopRightNode );
        parentNode.addChild( smallBottomLeftNode );
        parentNode.addChild( bigNode );
        parentNode.addChild( smallTopLeftNode );
        parentNode.addChild( smallBottomRightNode );

        // layout
        final double offsetSmall = smallTopLeftNode.getFullBoundsReference().getWidth() / 4;
        double x = 0;
        double y = 0;
        bigNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = bigNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX() - offsetSmall;
        y = bigNode.getFullBoundsReference().getMinY() + offsetSmall;
        smallTopRightNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMinX() + offsetSmall;
        y = bigNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomLeftNode.setOffset( x, y );
        x = bigNode.getFullBoundsReference().getMaxX() - offsetSmall;
        y = bigNode.getFullBoundsReference().getMaxY() - offsetSmall;
        smallBottomRightNode.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
