/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Box2DGraphic extends DefaultInteractiveGraphic {

    public static double s_thickness = 4;
    private static Stroke s_defaultStroke = new BasicStroke( (float)s_thickness );
    private static Color s_defaultColor = Color.black;
    private Box2D box;
    private boolean graphicSelected;
    private int wallSpeedLimit = 6;
    private boolean leftWallHighlighted;

    public Box2DGraphic( Component component, final Box2D box ) {
        super( null );

        this.box = box;
        InternalBoxGraphic internalBoxGraphic = new InternalBoxGraphic( component );
        setBoundedGraphic( internalBoxGraphic );

        this.addCursorBehavior( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ));
        this.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                // Speed limit on wall
                dx = Math.max( -wallSpeedLimit, Math.min(  dx, wallSpeedLimit ));

                double x = Math.min( Math.max( box.getMinX() + dx, 50 ), box.getMaxX() - box.getMinimumWidth() );
                box.setBounds( x, box.getMinY(), box.getMaxX(), box.getMaxY() );
            }
        } );
    }

    public void mousePressed( MouseEvent e ) {
        graphicSelected = true;
        super.mousePressed( e );
    }

    public void mouseReleased( MouseEvent e ) {
        this.graphicSelected = false;
        super.mouseReleased( e );
    }

    public boolean isGraphicSelected() {
        return graphicSelected;
    }

    public void mouseEntered( MouseEvent e ) {
        super.mouseEntered( e );
        leftWallHighlighted = true;
    }

    public void mouseExited( MouseEvent e ) {
        super.mouseExited( e );
        leftWallHighlighted = false;
    }

    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );
//        try {
//            Thread.sleep( 10 );
//        }
//        catch( InterruptedException e1 ) {
//            e1.printStackTrace();
//        }
    }

    private class InternalBoxGraphic extends PhetShapeGraphic implements SimpleObserver {
        private Rectangle2D.Double rect = new Rectangle2D.Double();
        private Rectangle2D.Double mouseableArea = new Rectangle2D.Double();
        private Rectangle openingRect = new Rectangle();
        private BufferedImage wallHandle;
        private Point wallHandleLocation;

        public InternalBoxGraphic( Component component ) {
            super( component, null, s_defaultStroke, s_defaultColor );
            box.addObserver( this );
            this.setShape( mouseableArea );
            try {
                wallHandle = ImageLoader.loadBufferedImage( IdealGasConfig.IMAGE_DIRECTORY + "wall-handle.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            update();
        }

        public void update() {
            rect.setRect( box.getMinX() - s_thickness / 2,
                          box.getMinY() - s_thickness / 2,
                          box.getMaxX() - box.getMinX() + s_thickness,
                          box.getMaxY() - box.getMinY() + s_thickness );
            mouseableArea.setRect( box.getMinX() - s_thickness,
                                   box.getMinY() - s_thickness,
                                   s_thickness,
                                   box.getMaxY() - box.getMinY() + s_thickness );
            wallHandleLocation = new Point( (int)(box.getMinX() - wallHandle.getWidth()),
                         (int)( box.getMinY() + box.getHeight() + wallHandle.getHeight() ) / 2 );
            mouseableArea.add( new Rectangle( wallHandleLocation.x, wallHandleLocation.y,
                                              wallHandle.getWidth(), wallHandle.getHeight( )));
            mouseableArea.setRect( new Rectangle( wallHandleLocation.x, wallHandleLocation.y,
                                              wallHandle.getWidth(), wallHandle.getHeight( )));
            Point2D[] opening = box.getOpening();
            openingRect.setFrameFromDiagonal( opening[0].getX(), opening[0].getY(),
                                              opening[1].getX(), opening[1].getY() - ( s_thickness - 1 ) );
            super.setBoundsDirty();
            super.repaint();
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            g.drawImage( wallHandle, (int)(box.getMinX() - wallHandle.getWidth()),
                         (int)( box.getMinY() + box.getHeight() + wallHandle.getHeight() ) / 2,
                         wallHandle.getWidth(), wallHandle.getHeight(),null);
            g.setStroke( s_defaultStroke );
            g.setColor( s_defaultColor );
            g.draw( rect );
            g.setColor( Color.white );
            g.fill( openingRect );

            if( leftWallHighlighted ) {
                Rectangle2D r = new Rectangle2D.Double( box.getMinX() - s_thickness,
                          box.getMinY() - s_thickness,
                          s_thickness,
                          box.getMaxY() - box.getMinY() + s_thickness * 2 );
                g.setStroke( new BasicStroke( 1 ));
                g.setColor( Color.red );
                g.draw( r );
            }
            restoreGraphicsState();
        }
    }
}
