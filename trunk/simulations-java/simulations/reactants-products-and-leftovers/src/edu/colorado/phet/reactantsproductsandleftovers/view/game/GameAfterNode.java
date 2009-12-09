package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.view.BoxNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


public class GameAfterNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    
    private static final String TITLE = RPALStrings.LABEL_AFTER_REACTION;
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final double TITLE_Y_SPACING = 10;
    
    private final BoxNode boxNode;
    
    public GameAfterNode() {
        super();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( TITLE );
        titleNode.setFont( TITLE_FONT );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
    }
    
    /**
     * Box width, used by layout code.
     * @return
     */
    public double getBoxWidth() {
        return boxNode.getFullBoundsReference().getWidth();
    }

    /**
     * Box height, used by layout code.
     * @return
     */
    public double getBoxHeight() {
        return boxNode.getFullBoundsReference().getHeight();
    }
}
