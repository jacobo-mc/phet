// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.MediumControlPanel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.NodeFactory;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.ToolIconNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.modules.prisms.PrismBreakModel.getPrismPrototypes;

/**
 * Prism toolbox which contains draggable prisms as well as the control panel for their index of refraction.
 *
 * @author Sam Reid
 */
public class PrismToolboxNode extends PNode {
    public PrismToolboxNode( final PrismBreakCanvas canvas, final ModelViewTransform transform, final PrismBreakModel model ) {
        //Create and add Title label for the prism toolbox
        final PText titleLabel = new PText( BendingLightStrings.PRISMS ) {{
            setFont( BendingLightCanvas.labelFont );
        }};
        addChild( titleLabel );
        HBox content = new HBox();
        content.setOffset( 0, 5 );//Move it down so it doesn't overlap the title label
        addChild( content );

        //Iterate over the prism prototypes in the model and create a draggable icon for each one
        for ( final Prism prism : getPrismPrototypes() ) {
            content.addChild( new PrismIcon( prism, model, transform, canvas, new Function0<Rectangle2D>() {
                public Rectangle2D apply() {
                    return getGlobalFullBounds();
                }
            } ) );
        }

        //Allow the user to control the type of material in the prisms
        content.addChild( new MediumControlPanel( canvas, model.prismMedium, BendingLightStrings.OBJECTS, false, model.wavelengthProperty, "0.0000000", 8 ) );
    }

    static class PrismIcon extends ToolIconNode<BendingLightCanvas<PrismBreakModel>> {
        private final PrismBreakModel model;

        public PrismIcon( final Prism prism, final PrismBreakModel model, ModelViewTransform transform, PrismBreakCanvas canvas, final Function0<Rectangle2D> globalToolboxBounds ) {
            super( toThumbnail( prism, model, transform ), new Property<Boolean>( false ) {
                       @Override public void set( Boolean value ) {
                           super.set( false );
                       }
                   }, transform, canvas, new NodeFactory() {
                       public ToolNode createNode( ModelViewTransform transform, Property<Boolean> visible, Point2D location ) {
                           return new PrismToolNode( transform, prism.copy(), model, location );
                       }
                   }, model, globalToolboxBounds, true
            );
            this.model = model;
        }

        @Override protected void addChild( BendingLightCanvas<PrismBreakModel> canvas, ToolNode node ) {
            canvas.addChildBehindLight( node );

            //Add the prism model element
            model.addPrism( ( (PrismToolNode) node ).prism );
        }

        @Override protected void removeChild( BendingLightCanvas<PrismBreakModel> canvas, ToolNode node ) {
            canvas.removeChildBehindLight( node );

            //Remove the associated prism from the model when dropped back in the toolbox, resolves #2833
            model.removePrism( ( (PrismToolNode) node ).prism );
        }

        private static Image toThumbnail( Prism prism, PrismBreakModel model, ModelViewTransform transform ) {
            PrismNode prismNode = new PrismNode( transform, prism, model.prismMedium );
            final int thumbnailHeight = 70;
            return prismNode.toImage( (int) ( prismNode.getFullBounds().getWidth() * thumbnailHeight / prismNode.getFullBounds().getHeight() ), thumbnailHeight, null );
        }
    }

    static class PrismToolNode extends ToolNode {
        private final ModelViewTransform transform;
        private final Prism prism;

        PrismToolNode( final ModelViewTransform transform, final Prism prism, final PrismBreakModel model, Point2D modelPoint ) {
            this.transform = transform;
            this.prism = prism;
            //Create a new prism model, and add it to the model
            final Rectangle2D bounds = prism.getBounds();
            Point2D copyCenter = new Point2D.Double( bounds.getX(), bounds.getY() );
            prism.translate( modelPoint.getX() - copyCenter.getX() - bounds.getWidth() / 2, modelPoint.getY() - copyCenter.getY() - bounds.getHeight() / 2 );
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        @Override public void dragAll( PDimension viewDelta ) {
            prism.translate( transform.viewToModelDelta( viewDelta ) );
        }
    }
}