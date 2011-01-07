// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forces1d.common;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

/**
 * User: Sam Reid
 * Date: Apr 2, 2004
 * Time: 1:13:42 AM
 */
public class JSAudioPlayer {
    //    private static final int EXTERNAL_BUFFER_SIZE = 128000;
    private static final int EXTERNAL_BUFFER_SIZE = 4000;

    public static void main( String[] args ) throws IOException, UnsupportedAudioFileException {
        URL url = JSAudioPlayer.class.getClassLoader().getResource( "forces-1d/audio/songwriterscentral_one shot c add9 upstroke-ii.wav" );
        System.out.println( "url = " + url );
        System.out.println( "getle = " + getLength( url ) );
        play( url );
    }

    public static double getLength( URL url ) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat( url );
        AudioFormat audioFormat = aff.getFormat();
        double sec = ( aff.getFrameLength() / (double) audioFormat.getFrameRate() );
        System.out.println( "sec = " + sec );
        return sec;
    }

    public static void loop( final URL url ) {
        Runnable r = new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        play( url );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                    catch( UnsupportedAudioFileException e ) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread( r );
        t.setPriority( Thread.MIN_PRIORITY );
        t.start();
    }

    /**
     * Blocks until finished.
     */
    public static void play( URL url ) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( url.openStream() );

        AudioFileFormat aff = AudioSystem.getAudioFileFormat( url );
        AudioFormat audioFormat = aff.getFormat();
        SourceDataLine line = null;
        DataLine.Info info = new DataLine.Info( SourceDataLine.class,
                                                audioFormat );
        try {
            line = (SourceDataLine) AudioSystem.getLine( info );

            /*
              The line is there, but it is not yet ready to
              receive audio data. We have to open the line.
            */
            line.open( audioFormat );
        }
        catch( LineUnavailableException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        catch( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        /*
          Still not enough. The line now can receive data,
          but will not pass them on to the audio output device
          (which means to your sound card). This has to be
          activated.
        */
        line.start();

        /*
          Ok, finally the line is prepared. Now comes the real
          job: we have to write data to the line. We do this
          in a loop. First, we read data from the
          AudioInputStream to a buffer. Then, we write from
          this buffer to the Line. This is done until the finish
          of the file is reached, which is detected by a
          return value of -1 from the read method of the
          AudioInputStream.
        */
        int nBytesRead = 0;

        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
        while ( nBytesRead != -1 ) {
            try {
                nBytesRead = audioInputStream.read( abData, 0, abData.length );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            if ( nBytesRead >= 0 ) {
                int nBytesWritten = line.write( abData, 0, nBytesRead );
            }
        }
        line.drain();
        line.close();
    }

    public static void playNoBlock( final String preyURL ) {
        URL url = JSAudioPlayer.class.getResource( preyURL );
        if ( url == null ) {
            throw new RuntimeException( "No url for name=" + preyURL );
        }
        playNoBlock( url );
    }

    public static void playNoBlock( final URL preyURL ) {
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    play( preyURL );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( UnsupportedAudioFileException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
    }
}
