// Copyright 2002-2012, University of Colorado

/** Sam Reid*/
package edu.colorado.phet.selfdrivenparticlemodel.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class JSAudioPlayer {
    private static final int EXTERNAL_BUFFER_SIZE = 128000;

    /*
     * Blocks until finished.
     */
    public static void play( URL url ) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new BufferedInputStream( url.openStream() ) );

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
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        catch ( Exception e ) {
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
            catch ( IOException e ) {
                e.printStackTrace();
            }
            if ( nBytesRead >= 0 ) {
                int nBytesWritten = line.write( abData, 0, nBytesRead );
            }
        }
        line.drain();
        line.close();
    }

    public static void playNoBlock( final URL preyURL ) {
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    play( preyURL );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
                catch ( UnsupportedAudioFileException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
    }
}
