/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.theramp.model.Surface;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:17:00 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class SurfaceGraphic extends PNode {
    private RampPanel rampPanel;
    private Surface ramp;
    private ModelViewTransform2D screenTransform;
    private double viewAngle;
    private PImage surfaceGraphic;
    private PPath floorGraphic;
    private PPath bookStackGraphic;
    private int surfaceStrokeWidth = 12;
    private PPath filledShapeGraphic;
    private RampTickSetGraphic rampTickSetGraphic;
    private PText heightReadoutGraphic;
    private AngleGraphic angleGraphic;
    private BufferedImage texture;

    public SurfaceGraphic( final RampPanel rampPanel, final Surface ramp ) {
        super();
        this.rampPanel = rampPanel;
        this.ramp = ramp;
        screenTransform = new ModelViewTransform2D( new Rectangle2D.Double( -10, 0, 20, 10 ), new Rectangle( -50, -50, 800, 400 ) );

        Stroke stroke = new BasicStroke( 6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        try {
            surfaceGraphic = new PImage( ImageLoader.loadBufferedImage( "images/wood5.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
//        floorGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.black );
        floorGraphic = new PPath( null, stroke );
        floorGraphic.setStrokePaint( Color.black );

        Paint bookFill = createBookFill();
//        bookStackGraphic = new PhetShapeGraphic( getComponent(), null, bookFill );
        bookStackGraphic = new PPath( null );
        bookStackGraphic.setStrokePaint( null );
        bookStackGraphic.setStroke( null );
        bookStackGraphic.setPaint( bookFill );

//        filledShapeGraphic = new PhetShapeGraphic( getComponent(), null, Color.lightGray );
        filledShapeGraphic = new PPath();
        filledShapeGraphic.setPaint( Color.lightGray );
        addChild( filledShapeGraphic );
        filledShapeGraphic.setVisible( false );
        addChild( floorGraphic );
        addChild( bookStackGraphic );
        addChild( surfaceGraphic );

//        heightReadoutGraphic = new PText( rampPanel, new Font( "Lucida Sans", 0, 14 ), "h=0.0 m", Color.black, 1, 1, Color.gray );
        heightReadoutGraphic = new PText( "h=0.0 m" );
        addChild( heightReadoutGraphic );

        surfaceGraphic.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                SurfaceGraphic.this.mouseDragged( event );
            }
        } );
        bookStackGraphic.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                SurfaceGraphic.this.mouseDragged( event );
            }
        } );
        surfaceGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        bookStackGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        rampTickSetGraphic = new RampTickSetGraphic( this );
        addChild( rampTickSetGraphic );

        angleGraphic = new AngleGraphic( this );
        addChild( angleGraphic );

        updateRamp();
        ramp.addObserver( new SimpleObserver() {
            public void update() {
                updateRamp();
            }
        } );
    }

    public PText getHeightReadoutGraphic() {
        return heightReadoutGraphic;
    }

    private void mouseDragged( PInputEvent pie ) {
        Point2D pt = pie.getPosition();//e.getPoint();
        pt = getRampWorld().convertToWorld( pt );
        Vector2D.Double vec = new Vector2D.Double( getViewOrigin(), pt );
        double angle = -vec.getAngle();
        angle = MathUtil.clamp( 0, angle, Math.PI / 2.0 );
        ramp.setAngle( angle );
        rampPanel.getRampModule().record();
    }

    private RampWorld getRampWorld() {
        return rampPanel.getRampWorld();
    }

    private Point getEndLocation() {
        return getViewLocation( ramp.getLocation( ramp.getLength() ) );
    }

    private Paint createBookFill() {
        try {
            texture = ImageLoader.loadBufferedImage( "images/bookstack3.png" );
//            Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() * 0.8 ) );
            Point rampEnd = getEndLocation();
//            System.out.println( "texture = " + texture );
            final TexturePaint paint = new TexturePaint( texture, new Rectangle2D.Double( rampEnd.x - texture.getWidth() / 2, rampEnd.y, texture.getWidth(), texture.getHeight() ) );
            return paint;
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }

    }

    public PImage getSurfaceGraphic() {
        return surfaceGraphic;
    }

    public AngleGraphic getAngleGraphic() {
        return angleGraphic;
    }

    public static class ImageDebugFrame extends JFrame {
        public JLabel label;

        public ImageDebugFrame( Image im ) {
            label = new JLabel( new ImageIcon( im ) );
            setContentPane( label );
            setImage( im );
            pack();
        }

        public void setImage( Image image ) {
            label.setIcon( new ImageIcon( image ) );
        }
    }

    private Point getViewOrigin() {
        Point2D modelOrigin = ramp.getOrigin();
        final Point viewOrigin = screenTransform.modelToView( modelOrigin );
        return viewOrigin;
    }

    private void updateRamp() {

        Point viewOrigin = getViewOrigin();
        Point2D modelDst = ramp.getEndPoint();
        Point viewDst = screenTransform.modelToView( modelDst );
        viewAngle = Math.atan2( viewDst.y - viewOrigin.y, viewDst.x - viewOrigin.x );

//        Line2D.Double origSurface = new Line2D.Double( viewOrigin, viewDst );
//        double origLength = new Vector2D.Double( origSurface.getP1(), origSurface.getP2() ).getMagnitude();
//        Line2D line = RampUtil.getInstanceForLength( origSurface, origLength * 4 );
//        surfaceGraphic.setShape( line );

//        surfaceGraphic.setAutorepaint( false );
        surfaceGraphic.setOffset( getViewOrigin() );
//        surfaceGraphic.setTransform( new AffineTransform() );
        surfaceGraphic.setRotation( viewAngle );
//        double rampLength = 10;//meters
//        ramp.getLocation( 10);
//        getViewLocation( ramp.getLocation( rampLength ) );

        //todo scale the graphic to fit the length.
        double cur_im_width_model = screenTransform.viewToModelDifferentialX( surfaceGraphic.getImage().getWidth( null ) );

        surfaceGraphic.setScale( ramp.getLength() / cur_im_width_model );
        System.out.println( "surfaceGraphic.getGlobalFullBounds() = " + surfaceGraphic.getGlobalFullBounds() );
//        surfaceGraphic.setAutorepaint( true );
//        surfaceGraphic.repaint();

        Point p2 = new Point( viewDst.x, viewOrigin.y );
        Line2D.Double floor = new Line2D.Double( viewOrigin, p2 );
        floorGraphic.setPathTo( new Rectangle() );

//        GeneralPath jackShape = createJackLine();
        Shape jackShape = createJackArea();
        bookStackGraphic.setPathTo( jackShape );
        bookStackGraphic.setPaint( createBookFill() );
        bookStackGraphic.setVisible( ramp.getAngle() * 360 / 2 / Math.PI < 85 );

        DoubleGeneralPath path = new DoubleGeneralPath( viewOrigin );
        path.lineTo( floor.getP2() );
        path.lineTo( viewDst );
        path.closePath();
        filledShapeGraphic.setPathTo( path.getGeneralPath() );

        heightReadoutGraphic.setOffset( (int)( jackShape.getBounds().getMaxX() + 5 ), jackShape.getBounds().y );
        double height = ramp.getHeight();
        String heightStr = new DecimalFormat( "0.0" ).format( height );
        heightReadoutGraphic.setText( "h=" + heightStr + " m" );

        rampTickSetGraphic.update();
        angleGraphic.update();

    }

    private Shape createJackArea() {
        Point rampStart = getViewLocation( ramp.getLocation( 0 ) );
        Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() ) );

        Rectangle rect = new Rectangle( rampEnd.x - texture.getWidth() / 2, (int)( rampEnd.y + surfaceGraphic.getImage().getHeight( null ) * 0.75 ), texture.getWidth(), rampStart.y - rampEnd.y );
        return rect;
    }

    GeneralPath createJackLine() {
        Point rampStart = getViewLocation( ramp.getLocation( 0 ) );
        Point rampEnd = getViewLocation( ramp.getLocation( ramp.getLength() ) );

        DoubleGeneralPath path = new DoubleGeneralPath( new Point( rampEnd.x, rampStart.y ) );
        path.lineTo( new Point( rampEnd.x, rampEnd.y ) );
        return path.getGeneralPath();
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public ModelViewTransform2D getScreenTransform() {
        return screenTransform;
    }

    public Surface getSurface() {
        return ramp;
    }

    public int getSurfaceStrokeWidth() {
        return surfaceStrokeWidth;
    }

    public Point getViewLocation( Point2D location ) {
        Point viewLoc = getScreenTransform().modelToView( location );
//        return getRampWorld().convertToWorld( viewLoc );
        return viewLoc;
    }

    public Point getViewLocation( double rampDist ) {
        return getViewLocation( ramp.getLocation( rampDist ) );
    }

    /**
     * Create the AffineTransform that will put an object of size: dim centered along the ramp at position dist
     */
    public AffineTransform createTransform( double dist, Dimension dim ) {
        Point viewLoc = getViewLocation( ramp.getLocation( dist ) );
        AffineTransform transform = new AffineTransform();
        transform.translate( viewLoc.x, viewLoc.y );
//        transform.rotate( getViewAngle(), dim.width / 2, dim.height / 2 );
        transform.rotate( getViewAngle() );
//        transform.translate( 0, -dim.height );
        int onRamp = 7;
        transform.translate( -dim.width / 2, -dim.height + onRamp );
//        transform.translate( 0, -dim.height + onRamp );
        return transform;
    }

    public int getImageHeight() {
        return surfaceGraphic.getImage().getHeight( null );
    }
}
