// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.Medium;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MediumNode extends PNode {
    public MediumNode( final ModelViewTransform transform, final Property<Medium> medium ) {
        addChild( new PhetPPath( medium.getValue().getColor() ) {{
            medium.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( medium.getValue().getShape() ) );
                    final Color color = medium.getValue().getColor();
                    setPaint( new Color( color.getRed(), color.getGreen(), color.getBlue()
//                            , color.getAlpha()//ignoring alpha, but left in the code in case we go back to it.
                    ) );
                }
            } );
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }
}
