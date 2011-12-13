// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLCanvas;
import edu.colorado.phet.testlwjglproject.lwjgl.LWJGLTab;
import edu.colorado.phet.testlwjglproject.lwjgl.StartupUtils;

public class TestLWJGLApplication extends PhetApplication {

    public TestLWJGLApplication( PhetApplicationConfig config ) {
        super( config );
        final LWJGLCanvas canvas = LWJGLCanvas.getCanvasInstance();
        addModule( new TabbedModule( canvas ) {{
            Tab[] tabs = new Tab[] {
                    new TestingTab( canvas, TestProjectResources.getString( "sim1.module1" ) ),
                    new TestingTab( canvas, TestProjectResources.getString( "sim1.module2" ) )
            };
            for ( Tab tab : tabs ) {
                addTab( tab );
            }
        }} );
    }

    public static void main( String[] args ) {
        try {
            StartupUtils.setupLibraries();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, "test-lwjgl-project", "sim1" ), TestLWJGLApplication.class );
    }

    private static class TestingTab extends LWJGLTab {
        private long timeElapsed = 0;
        private long lastTime = 0;

        public TestingTab( LWJGLCanvas canvas, String title ) {
            super( canvas, title );
        }

        @Override public void start() {
            lastTime = System.currentTimeMillis();

            GL11.glMatrixMode( GL11.GL_PROJECTION );
            GL11.glLoadIdentity();
            GL11.glOrtho( 0, 800, 600, 0, 1, -1 );
            GL11.glMatrixMode( GL11.GL_MODELVIEW );

            GL11.glEnable( GL11.GL_BLEND );
            GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );

            // show both sides
            GL11.glPolygonMode( GL11.GL_FRONT, GL11.GL_FILL );
            GL11.glPolygonMode( GL11.GL_BACK, GL11.GL_FILL );
        }

        @Override public void stop() {
            // store state here?
        }

        @Override public void loop() {
            Display.sync( 60 );

            long currentTime = System.currentTimeMillis();
            timeElapsed += ( currentTime - lastTime );
            lastTime = currentTime;

            // Clear the screen and depth buffer
            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );

            // Reset the transform
            GL11.glMatrixMode( GL11.GL_MODELVIEW );
            GL11.glLoadIdentity();

            // translate our stuff a bit (can deal with centering after we get resizing working properly
            GL11.glTranslatef( 400, 200, 0 );

            float angle = (float) ( timeElapsed ) / 200;

            // add a fractal-like thing in the background
            fractalThing( angle, 12, 0, 2 );

            // test direct image drawing functionality in the foreground (lower-left corner)
            {
                int width = 127;
                int height = 127;
                ByteBuffer buffer = BufferUtils.createByteBuffer( width * height * 4 );
                for ( int row = 0; row < height; row++ ) {
                    for ( int col = 0; col < width; col++ ) {
                        buffer.put( new byte[] { (byte) ( row + col ), (byte) ( 255 - row - col ), 0, (byte) ( 128 - row + col ) } );
                    }
                }
                buffer.position( 0 );
                GL11.glDrawPixels( width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
            }

            Display.update();
        }

        private void fractalThing( float angle, int num, int depth, int limit ) {
            GL11.glPushMatrix();
            for ( int i = 0; i < num; i++ ) {
                GL11.glTranslatef( 0, 100, 0 );
                GL11.glRotatef( angle, 0, 0, 1 );

                // set the color of the quad (R,G,B,A)
                float n = ( (float) ( i ) ) / ( num - 1 );
                GL11.glColor4f( 1 - n, 0.0f, n, 0.5f - 0.4f * ( (float) depth ) / ( (float) limit ) );

                // draw quad
                GL11.glBegin( GL11.GL_QUADS );
                GL11.glVertex3f( -50, -50, 0 );
                GL11.glVertex3f( 50, -50, 0 );
                GL11.glVertex3f( 50, 50, 0 );
                GL11.glVertex3f( -50, 50, 0 );
                GL11.glEnd();

                if ( depth < limit ) {
                    GL11.glPushMatrix();
                    GL11.glScalef( 0.5f, 0.5f, 1f );
                    fractalThing( angle, num, depth + 1, limit );
                    GL11.glPopMatrix();
                }
            }
            GL11.glPopMatrix();
        }
    }
}
