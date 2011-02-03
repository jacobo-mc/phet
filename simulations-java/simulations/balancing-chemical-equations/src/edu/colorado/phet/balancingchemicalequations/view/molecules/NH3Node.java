// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * NH3 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NH3Node extends PComposite {

    public NH3Node() {

        // atom nodes
        AtomNode atomBig = new BigAtomNode( BCEColors.N );
        AtomNode atomSmallLeft = new SmallAtomNode( BCEColors.H );
        AtomNode atomSmallRight = new SmallAtomNode( BCEColors.H );
        AtomNode atomSmallBottom = new SmallAtomNode( BCEColors.H );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmallLeft );
        parentNode.addChild( atomSmallRight );
        parentNode.addChild( atomBig );
        parentNode.addChild( atomSmallBottom );

        // layout
        double x = 0;
        double y = 0;
        atomBig.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMinX();
        y = atomBig.getFullBoundsReference().getMaxY() - ( 0.25 * atomBig.getFullBoundsReference().getHeight() );
        atomSmallLeft.setOffset( x, y );
        x = atomBig.getFullBoundsReference().getMaxX();
        y = atomSmallLeft.getYOffset();
        atomSmallRight.setOffset( x, y );
        x = atomBig.getXOffset();
        y = atomBig.getFullBoundsReference().getMaxY();
        atomSmallBottom.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
