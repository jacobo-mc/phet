// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.PlaneF;
import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.PiccoloImage;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPickPath;

import static org.lwjgl.opengl.GL11.*;

/**
 * An alternative to PlanarPiccoloNode that attempts to minimize the thread message passing and
 * frame latency issues experienced by having to pass rendering to be done at a later time in the
 * Swing EDT.
 * <p/>
 * Also, don't add any sort of PSwing under this node. I haven't tried it, but it seems like the
 * perfect way to create an evil threading heisenbug.
 */
public class ThreadedPlanarPiccoloNode extends AbstractGraphicsNode {

    // the node that we are displaying
    private final PNode node;

    // and that node wrapped so that it has zero offset
    private final PNode wrappedNode;

    private Cursor cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    private PiccoloImage piccoloImage;

    private List<Notifier<?>> repaintNotifiers = new ArrayList<Notifier<?>>(  );
    private UpdateListener repaintListener = new UpdateListener() {
        public void update() {
            repaint();
        }
    };

    public ThreadedPlanarPiccoloNode( final PNode node ) {
        this.node = node;
        wrappedNode = new ZeroOffsetNode( node );

        // initialize this correctly
        repaint();
    }

    // handles initialization and everything needed to do a full repaint (resizing actual texture if necessary)
    public void repaint() {
        Dimension currentSize = getCurrentSize();
        if ( piccoloImage == null || !size.equals( currentSize ) ) {
            size = currentSize;
            rebuildComponentImage();
            onResize.updateListeners();
        }
        piccoloImage.repaint();
    }

    private Dimension getCurrentSize() {
        PBounds bounds = node.getFullBounds();
        return new Dimension( (int) Math.ceil( bounds.getWidth() ), (int) Math.ceil( bounds.getHeight() ) );
    }

    public <T> void addRepaintNotifier( Notifier<T> notifier ) {
        repaintListener = new UpdateListener() {
            public void update() {
                repaint();
            }
        };
        notifier.addUpdateListener( repaintListener, false );
        repaintNotifiers.add( notifier );
    }

    public void recycle() {
        for ( Notifier<?> repaintNotifier : repaintNotifiers ) {
            repaintNotifier.removeListener( repaintListener );
        }
        repaintNotifiers.clear();
    }

    public boolean doesLocalRayHit( Ray3F ray ) {
        // find out where the ray hits the z=0 plane
        Vector3F planeHitPoint = PlaneF.XY.intersectWithRay( ray );

        // check for actual intersection, not just bounds intersection
        return intersects( new Vector2F( planeHitPoint.x, getComponentHeight() - planeHitPoint.y ) );
    }

    public PBounds get2DBounds() {
        return new PBounds( 0, 0, node.getFullBounds().getWidth(), node.getFullBounds().getHeight() );
    }

    @Override public void renderSelf( GLOptions options ) {
        if ( piccoloImage == null ) {
            return;
        }

        glColor4f( 1, 1, 1, 1 );

        // TODO: don't tromp over these settings?
        glEnable( GL_TEXTURE_2D );
        glShadeModel( GL_FLAT );

        piccoloImage.useTexture();

        // texture coordinates reversed compared to OrthoComponentNode
        glBegin( GL_QUADS );

        float left = 0;
        float right = piccoloImage.getWidth();
        float top = size.height;
        float bottom = size.height - piccoloImage.getHeight();

        // lower-left
        glTexCoord2f( 0, 1 );
        glVertex3f( left, bottom, 0 );

        // upper-left
        glTexCoord2f( 0, 0 );
        glVertex3f( left, top, 0 );

        // upper-right
        glTexCoord2f( 1, 0 );
        glVertex3f( right, top, 0 );

        // lower-right
        glTexCoord2f( 1, 1 );
        glVertex3f( right, bottom, 0 );
        glEnd();

        glShadeModel( GL_SMOOTH );
        glDisable( GL_TEXTURE_2D );
    }

    // if necessary, creates a new PiccoloImage of a different size to display our component
    @Override protected synchronized void rebuildComponentImage() {
        // how large our PiccoloImage needs to be as a raster to render all of our content
        final int imageWidth = LWJGLUtils.toPowerOf2( size.width );
        final int imageHeight = LWJGLUtils.toPowerOf2( size.height );

        if ( piccoloImage != null ) {
            piccoloImage.dispose();
        }

        // create the new image within the EDT
        final PiccoloImage newPiccoloImage = new PiccoloImage( imageWidth, imageHeight, true, GL_LINEAR, GL_LINEAR, new AffineTransform(), wrappedNode );

        // hook up new PiccoloImage.
        piccoloImage = newPiccoloImage;
    }

    // intersection "hit" test for the underlying node. the point needs to be in piccolo coordinates for proper intersection (check for flipped y)
    public boolean intersects( Vector2F piccoloPosition ) {
        return getNode().fullPick( new PPickPath( new PCamera(), new PBounds( piccoloPosition.x, piccoloPosition.y, 0.1, 0.1 ) ) );
    }

    @Override public int getWidth() {
        return piccoloImage.getWidth();
    }

    @Override public int getHeight() {
        return piccoloImage.getHeight();
    }

    public PNode getNode() {
        return node;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor( Cursor cursor ) {
        this.cursor = cursor;
    }
}
