// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.DecimalFractionNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class DecimalFraction extends Representation {

    public DecimalFraction( Fraction fraction, double x, double y ) {
        super( fraction, x, y );
    }

    @Override public PNode toNode( ModelViewTransform transform ) {
        return new DecimalFractionNode( transform, this );
    }
}