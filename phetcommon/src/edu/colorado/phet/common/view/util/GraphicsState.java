/**
 * Class: GraphicsState
 * Package: edu.colorado.phet.common.view.util
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.common.view.util;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A utilitye class for saving and restoring the state of Graphics2D objects
 */
public class GraphicsState {
    private Graphics2D g2;
    private RenderingHints renderingHints;
    private Paint paint;
    private Color color;
    private Stroke stroke;
    private Composite composite;
    private AffineTransform transform;
    private Font font;
    private Shape clip;
    private Color background;

    public GraphicsState( Graphics2D graphics2D ) {
        this.g2 = graphics2D;
        renderingHints = graphics2D.getRenderingHints();
        paint = graphics2D.getPaint();
        color = graphics2D.getColor();
        stroke = graphics2D.getStroke();
        composite = graphics2D.getComposite();
        transform = graphics2D.getTransform();
        font = graphics2D.getFont();
        clip = graphics2D.getClip();
        background = graphics2D.getBackground();
    }

    public void restoreGraphics() {
        if( g2.getRenderingHints() != renderingHints ) {
            g2.setRenderingHints( renderingHints );
        }
        if( g2.getPaint() != paint ) {
            g2.setPaint( paint );
        }
        if( g2.getColor() != color ) {
            g2.setColor( color );
        }
        if( g2.getStroke() != stroke ) {
            g2.setStroke( stroke );
        }
        if( g2.getComposite() != composite ) {
            g2.setComposite( composite );
        }
        if( g2.getTransform() != transform ) {
            g2.setTransform( transform );
        }
        if( g2.getFont() != font ) {
            g2.setFont( font );
        }
        if( g2.getClip() != clip ) {
            g2.setClip( clip );
        }
        if( g2.getBackground() != background ) {
            g2.setBackground( background );
        }
    }
}
