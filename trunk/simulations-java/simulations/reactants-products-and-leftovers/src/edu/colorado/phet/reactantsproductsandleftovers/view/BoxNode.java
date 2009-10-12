package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class BoxNode extends PPath {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_PAINT = Color.BLACK;
    private static final Color FILL_PAINT = new Color( 46, 107, 178 );
    
    public BoxNode( PDimension size ) {
        super();
        setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ));
        setPaint( FILL_PAINT );
        setStrokePaint( STROKE_PAINT );
        setStroke( STROKE );
    }
}
