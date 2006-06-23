/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.common_cck.view.util.BufferedImageUtils;
import edu.colorado.phet.common_cck.view.util.CachingImageLoader;
import edu.colorado.phet.common_cck.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:38:59 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
 * Testing cvs.
 */
public class CCK2ImageSuite {

    private ImageSuite lifelikeImageSuite;
    private BufferedImage closedImage;
    private String batteryImageLocation = "images/AA-battery-100.gif";
    private String resistorImageLocation = "images/resistor3.gif";
    private BufferedImage resistorImage;
    private CachingImageLoader imageLoader = new CachingImageLoader();
    private BufferedImage capacitorImage;
    private String capImageLoc = "images/cap5.gif";
    private String acImageLoc = "images/ac.gif";
    private BufferedImage acImage;
    private BufferedImage inductorImage;
//    private String inductorImageLoc="images/inductor1.jpg";
    private String inductorImageLoc = "images/inductor2.gif";

    public CCK2ImageSuite() throws IOException {
        loadImages();
    }

    public BufferedImage getParticleImage() throws IOException {
        return imageLoader.loadImage( "images/electron9.gif" );
    }

    private void loadImages() throws IOException {
        resistorImage = ImageLoader.loadBufferedImage( resistorImageLocation );
        BufferedImage batteryImage = ImageLoader.loadBufferedImage( batteryImageLocation );
        lifelikeImageSuite = new ImageSuite( resistorImage, batteryImage, closedImage );
        capacitorImage = ImageLoader.loadBufferedImage( capImageLoc );
        acImage = BufferedImageUtils.flipY( ImageLoader.loadBufferedImage( acImageLoc ) );
        inductorImage = ImageLoader.loadBufferedImage( inductorImageLoc );
//        capacitorImage = BufferedImageUtils.rescaleYMaintainAspectRatio( null,capacitorImage, 50);
    }

    public ImageSuite getLifelikeSuite() {
        return lifelikeImageSuite;
    }

    public BufferedImage getKnifeHandleImage() {
        try {
            return imageLoader.loadImage( "images/handle8.gif" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getKnifeBoardImage() {
        try {
            return imageLoader.loadImage( "images/knifeBoard.gif" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public BufferedImage getCapacitorImage() {
        return capacitorImage;
    }

    public CachingImageLoader getImageLoader() {
        return imageLoader;
    }

    public BufferedImage getACImage() {
        return acImage;
    }

    public BufferedImage getInductorImage() {
        return inductorImage;
    }
}
