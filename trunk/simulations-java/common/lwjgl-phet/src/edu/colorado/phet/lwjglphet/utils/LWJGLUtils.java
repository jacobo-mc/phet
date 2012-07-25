// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.swing.*;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 * Useful utility functions related to LWJGL
 */
public class LWJGLUtils {

    private static int nextDisplayList = 1;

    // common spot to handle display list names (identifiers)
    public static synchronized int getDisplayListName() {
        return nextDisplayList++;
    }

    // to support all needed video card architectures, we need to be able to make texture dimensions a power of 2 in many cases.
    public static int toPowerOf2( int n ) {
        int result = 1;
        while ( result < n ) {
            result *= 2;
        }
        return result;
    }

    public static Dimension toPowerOf2( Dimension dim ) {
        return new Dimension( toPowerOf2( dim.width ), toPowerOf2( dim.height ) );
    }

    public static boolean isPowerOf2( int n ) {
        return n == toPowerOf2( n );
    }

    /*---------------------------------------------------------------------------*
    * convenience methods
    *----------------------------------------------------------------------------*/

    public static void color4f( Color color ) {
        glColor4f( (float) color.getRed() / 255f,
                   (float) color.getGreen() / 255f,
                   (float) color.getBlue() / 255f,
                   (float) color.getAlpha() / 255f
        );
    }

    public static void vertex3f( Vector3F v ) {
        glVertex3f( v.x, v.y, v.z );
    }

    public static void vertex2fxy( Vector2F v ) {
        glVertex3f( v.x, v.y, 0 );
    }

    /*---------------------------------------------------------------------------*
    * buffer creation
    *----------------------------------------------------------------------------*/

    public static FloatBuffer floatBuffer( float[] floats ) {
        FloatBuffer result = BufferUtils.createFloatBuffer( floats.length );
        result.put( floats );
        result.rewind();
        return result;
    }

    public static ShortBuffer shortBuffer( short[] shorts ) {
        ShortBuffer result = BufferUtils.createShortBuffer( shorts.length );
        result.put( shorts );
        result.rewind();
        return result;
    }

    /*---------------------------------------------------------------------------*
    * threading
    *----------------------------------------------------------------------------*/

    public static void invoke( Runnable runnable ) {
        LWJGLCanvas.addTask( runnable );
    }

    public static boolean isLWJGLRendererThread() {
        return Thread.currentThread().getName().equals( LWJGLCanvas.LWJGL_THREAD_NAME );
    }

    public static SimpleObserver swingObserver( final Runnable runnable ) {
        return new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( runnable );
            }
        };
    }

    public static SimpleObserver jmeObserver( final Runnable runnable ) {
        return new SimpleObserver() {
            public void update() {
                invoke( runnable );
            }
        };
    }

    public static UpdateListener swingUpdateListener( final Runnable runnable ) {
        return new UpdateListener() {
            public void update() {
                SwingUtilities.invokeLater( runnable );
            }
        };
    }

    /*---------------------------------------------------------------------------*
    * capability handling (if in a GLNode, use the behavior there instead)
    *----------------------------------------------------------------------------*/

    public static void withEnabled( int glCapability, Runnable runnable ) {
        glEnable( glCapability );
        runnable.run();
        glDisable( glCapability );
    }

    public static void withClientEnabled( int glClientCapability, Runnable runnable ) {
        glEnableClientState( glClientCapability );
        runnable.run();
        glDisableClientState( glClientCapability );
    }
}
