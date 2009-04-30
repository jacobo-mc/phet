package edu.colorado.phet.naturalselection.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.model.ColorGene;
import edu.colorado.phet.naturalselection.model.Gene;
import edu.colorado.phet.naturalselection.model.TailGene;
import edu.colorado.phet.naturalselection.model.TeethGene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class MutationPendingNode extends PNode {
    private PText label;
    private PImage geneImage;
    private PImage mutationImage;
    private SwingLayoutNode layoutNode;

    private static final double CONTAINER_PADDING = 5.0;

    public MutationPendingNode( Gene gene ) {
        geneImage = NaturalSelectionResources.getImageNode( getImageString( gene ) );
        mutationImage = NaturalSelectionResources.getImageNode( NaturalSelectionConstants.IMAGE_MUTATION_PANEL_LARGE );

        label = new PText( "mutation coming" );
        label.setFont( new PhetFont( 16, true ) );
        //addChild( label );

        layoutNode = new SwingLayoutNode( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        layoutNode.addChild( mutationImage, c );
        c.gridx = 1;
        c.insets = new Insets( 0, 10, 0, 10 );
        layoutNode.addChild( label, c );
        c.gridx = 2;
        c.insets = new Insets( 0, 0, 0, 0 );
        layoutNode.addChild( geneImage, c );

        layoutNode.translate( CONTAINER_PADDING, CONTAINER_PADDING );

        // TODO: see if there's a nice way without downcasting
        PPath background = PPath.createRectangle( 0, 0, (float) getPlacementWidth(), (float) getPlacementHeight() );

        addChild( background );

        addChild( layoutNode );
    }

    public String getImageString( Gene gene ) {
        if ( gene == ColorGene.getInstance() ) {
            return NaturalSelectionConstants.IMAGE_BUNNY_COLOR_BROWN;
        }
        else if ( gene == TailGene.getInstance() ) {
            return NaturalSelectionConstants.IMAGE_BUNNY_TAIL_BIG;
        }
        else if ( gene == TeethGene.getInstance() ) {
            return NaturalSelectionConstants.IMAGE_BUNNY_TEETH_LONG;
        }
        else {
            throw new RuntimeException( "Unknown gene" );
        }
    }

    public double getPlacementWidth() {
        return layoutNode.getContainer().getPreferredSize().getWidth() + CONTAINER_PADDING * 2;
    }

    public double getPlacementHeight() {
        return layoutNode.getContainer().getPreferredSize().getHeight() + CONTAINER_PADDING * 2;
    }
}
