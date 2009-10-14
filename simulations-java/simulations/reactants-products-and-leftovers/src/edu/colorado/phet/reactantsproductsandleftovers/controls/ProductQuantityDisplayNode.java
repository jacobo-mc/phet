package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ProductQuantityDisplayNode extends PComposite {
    
    private static final PDimension BAR_SIZE = new PDimension( 15, 75 );
    
    private final IntegerHistogramBarNode barNode;
    private final PText valueNode;
    
    public ProductQuantityDisplayNode( IntegerRange range, PNode imageNode, double imageScale ) {
        super();
        
        barNode = new IntegerHistogramBarNode( range, BAR_SIZE );
        valueNode = new PText( String.valueOf( range.getMax() ) ); // use max for layout
        valueNode.setFont( new PhetFont( 22 ) );
        imageNode.scale( imageScale );
        
        addChild( barNode );
        addChild( valueNode );
        addChild( imageNode );
        
        double x = 0;
        double y = 0;
        barNode.setOffset( x, y );
        x = barNode.getFullBoundsReference().getMinX() - valueNode.getFullBoundsReference().getWidth() - 2;
        y = barNode.getFullBoundsReference().getMaxY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
        x = barNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = barNode.getFullBoundsReference().getMaxY() + 15;
        imageNode.setOffset( x, y );
        
        setValue( range.getDefault() );
    }
    
    public void setValue( int value ) {
        barNode.setValue( value );
        valueNode.setText( String.valueOf( value ) );
    }

}
