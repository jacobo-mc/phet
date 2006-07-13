/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.resources;

import edu.colorado.phet.simlauncher.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * ImageResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThumbnailResource extends SimResource {
    private ImageIcon imageIcon;

    public ThumbnailResource( URL url, File localRoot ) {
        super( url, localRoot );
        try {
            BufferedImage bImg = ImageLoader.loadBufferedImage( url );
            imageIcon = new ImageIcon( bImg );
        }
        catch( IOException e ) {
            imageIcon = new ImageIcon( new NoImageImage() );
        }
    }

    public ImageIcon getImageIcon() {
        if( imageIcon == null ) {
            return new ImageIcon( new NoImageImage() );
        }
        else {
            return imageIcon;
        }
    }

    private class NoImageImage extends BufferedImage {

        public NoImageImage() {
            super( 100, 30, BufferedImage.TYPE_INT_RGB );
            Graphics2D g2 = this.createGraphics();
            g2.setColor( Color.white );
            g2.fillRect( 0, 0, getWidth(), getHeight() );
            g2.setColor( Color.black );
            g2.drawString( "Thumbnail not available", 10, 10 );
            g2.dispose();
        }
    }
}
