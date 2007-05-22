/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Todo: see EnergyPositionPlotCanvas for an error in the offset.
 */

public class GridNode extends PhetPNode {
    private static final String METERS = "0 meters";
    private PNode lines = new PNode();
    private PNode textLayer = new PNode();
    private Paint gridPaint = null;

    public GridNode( double minX, double minY, double maxX, double maxY, double dx, double dy ) {
        addChild( lines );
        addChild( textLayer );
        for( double x = minX; x <= maxX; x += dx ) {
            lines.addChild( createXLineNode( minY, maxY, x ) );
            if( x % 2 == 0 ) {
                String aText = "" + (int)x;
                if( aText.equals( "0" ) ) {
                    aText = METERS;
                }
                PText text = new PText( aText );
                text.setOffset( x + dx, minY + dy );

                text.setScale( 0.03f );
                text.getTransformReference( true ).scale( 1, -1 );

                textLayer.addChild( text );
            }
        }
        for( double y = minY; y <= maxY; y += dy ) {
            lines.addChild( createYLineNode( minX, maxX, y ) );
            if( y % 2 == 0 ) {
                String aText = "" + (int)y;
                if( aText.equals( "0" ) ) {
                    aText = METERS;
                }
                PText text = new PText( aText );
                text.setOffset( 0 + dx, y + dy );
                text.setScale( 0.03f );
                text.getTransformReference( true ).scale( 1, -1 );
                textLayer.addChild( text );
            }
        }
        setPickable( false );
        setChildrenPickable( false );
    }

    public void setGridPaint( Paint paint ) {
        if( this.gridPaint == null || !this.gridPaint.equals( paint ) ) {
            this.gridPaint = paint;
            setLinePaint( paint );
            setFontPaint( paint );
        }
    }

    private void setFontPaint( Paint paint ) {
        for( int i = 0; i < textLayer.getChildrenCount(); i++ ) {
            PText t = (PText)textLayer.getChild( i );
            t.setTextPaint( paint );
        }
    }

    public void setLinePaint( Paint linePaint ) {
        for( int i = 0; i < lines.getChildrenCount(); i++ ) {
            PPath line = (PPath)lines.getChild( i );
            line.setStrokePaint( linePaint );
        }
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( false );
        setChildrenPickable( false );
    }

    private PNode createYLineNode( double minX, double maxX, double y ) {
        PPath child = new PPath( new Line2D.Double( minX, y, maxX, y ) );
        child.setStroke( new BasicStroke( 0.01f * ( MathUtil.isApproxEqual( y, 1, 0.001 ) ? 3 : 1 ) ) );
        return child;
    }

    private PNode createXLineNode( double minY, double maxY, double x ) {
        PPath child = new PPath( new Line2D.Double( x, minY, x, maxY ) );
        child.setStroke( new BasicStroke( 0.01f * ( MathUtil.isApproxEqual( x, 1, 0.001 ) ? 3 : 1 ) ) );
        return child;
    }
}
