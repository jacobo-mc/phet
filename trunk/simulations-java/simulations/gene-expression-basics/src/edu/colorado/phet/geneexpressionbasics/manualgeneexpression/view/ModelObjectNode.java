// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ModelObject;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for displaying and interacting with any model object in the torque sim.
 * Uses the shape of the object, which will change as the object moves.
 *
 * @author Sam Reid
 */
public class ModelObjectNode extends PNode {
    public ModelObjectNode( final Property<ModelViewTransform> mvtProperty, final ModelObject modelObject, Paint paint ) {
        addChild( new PhetPPath( paint, new BasicStroke( 1 ), Color.BLACK ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    setPathTo( mvtProperty.get().modelToView( modelObject.getShape() ) );
                }
            }.observe( modelObject.getShapeProperty(), mvtProperty );
        }} );
    }
}
