/**
 * Class: AtomGraphic
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AtomGraphic extends PhetImageGraphic implements SimpleObserver {

    static String s_imageName = LaserConfig.ATOM_IMAGE_FILE;
    static String s_groundStateImageName = LaserConfig.GROUND_STATE_IMAGE_FILE;
    static String s_highEnergyStateImageName = LaserConfig.HIGH_ENERGY_STATE_IMAGE_FILE;
    static String s_middleEnergyStateImageName = LaserConfig.MIDDLE_ENERGY_STATE_IMAGE_FILE;
    static Image s_particleImage;
    static Image s_groundStateImage;
    static Image s_highEnergyStateImage;
    static Image s_middleEnergyStateImage;
    static double s_middleEnergyMag = 1.2;
    static double s_highEnergyMag = 1.4;

    static BufferedImage groundImg;
    static BufferedImage highImg;
    static BufferedImage middleImg;

    // Load the images for atoms and scale them to the correct size
    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            s_groundStateImage = ImageLoader.loadBufferedImage( s_groundStateImageName );
            s_highEnergyStateImage = ImageLoader.loadBufferedImage( s_highEnergyStateImageName );
            s_middleEnergyStateImage = ImageLoader.loadBufferedImage( s_middleEnergyStateImageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        groundImg = GraphicsUtil.toBufferedImage( s_groundStateImage );
        AffineTransformOp xformOp;
        AffineTransform xform;
        BufferedImage tempBI;
        tempBI = GraphicsUtil.toBufferedImage( s_highEnergyStateImage );
        xform = AffineTransform.getScaleInstance( 1.2, 1.2 );
        xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        highImg = new BufferedImage( (int)( tempBI.getWidth() * s_highEnergyMag ),
                                     (int)( tempBI.getHeight() * s_highEnergyMag ),
                                     BufferedImage.TYPE_INT_RGB );
        highImg = xformOp.filter( tempBI, null );

        xform = AffineTransform.getScaleInstance( 1.1, 1.1 );
        xformOp = new AffineTransformOp( xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        middleImg = new BufferedImage( (int)( tempBI.getWidth() * s_middleEnergyMag ),
                                       (int)( tempBI.getHeight() * s_middleEnergyMag ),
                                       BufferedImage.TYPE_INT_RGB );
        tempBI = GraphicsUtil.toBufferedImage( s_middleEnergyStateImage );
        middleImg = xformOp.filter( tempBI, null );
    }


    private Atom atom;
    private Color energyRepColor;
    private Ellipse2D energyRep;
    private AtomicState atomicState;

    public AtomGraphic( Component component, Atom atom ) {
        super( component, s_groundStateImageName );
        this.atom = atom;
        atom.addObserver( this );
        update();
    }

    public void update() {
        if( atomicState != atom.getState() ) {
            atomicState = atom.getState();
            double energyRatio = atom.getState().getEnergyLevel() / GroundState.instance().getEnergyLevel();
            double energyRepRad = Math.pow( energyRatio, .5 ) * ( groundImg.getWidth() / 2 );
            energyRep = new Ellipse2D.Double( atom.getPosition().getX() - energyRepRad, atom.getPosition().getY() - energyRepRad,
                                              energyRepRad * 2, energyRepRad * 2 );
            energyRepColor = VisibleColor.wavelengthToColor( atom.getState().getWavelength() );
            setPosition( (int)( atom.getPosition().getX() - getImage().getWidth() / 2 ),
                         (int)( atom.getPosition().getY() - getImage().getHeight() / 2 ) );
            setBoundsDirty();
            repaint();
        }
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( energyRepColor );
        g.fill( energyRep );
        restoreGraphicsState();

        super.paint( g );

        // Debug: draws a dot at the center of the atom
//        g.setColor( Color.RED );
//        g.drawArc( (int)atom.getPosition().getX()-2, (int)atom.getPosition().getY()-2, 4, 4, 0, 360 );
    }
}

