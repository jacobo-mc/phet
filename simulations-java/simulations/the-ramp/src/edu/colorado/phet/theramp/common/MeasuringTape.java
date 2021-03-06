// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.theramp.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BoundNode;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid
 * Date: May 20, 2005
 * Time: 10:42:18 PM
 */

public class MeasuringTape extends PNode {
    private ModelViewTransform2D modelViewTransform2D;
    private Point2D.Double modelSrc;
    private Point2D.Double modelDst;
    private BodyGraphic bodyGraphic;
    private TapeGraphic tapeGraphic;
    private EndGraphic endGraphic;
    private ReadoutGraphic readoutGraphic;

    public MeasuringTape( Component component, ModelViewTransform2D modelViewTransform2D, Point2D.Double modelSrc ) {
        super();
        this.modelViewTransform2D = modelViewTransform2D;
        this.modelSrc = modelSrc;
        this.modelDst = new Point2D.Double( modelSrc.x + modelViewTransform2D.viewToModelDifferentialX( 100 ), modelSrc.y );

        bodyGraphic = new BodyGraphic();
        tapeGraphic = new TapeGraphic();
        endGraphic = new EndGraphic();
        readoutGraphic = new ReadoutGraphic( TheRampStrings.getString( "units.abbr.meters" ) );

        addChild( tapeGraphic );
        addChild( bodyGraphic );
        addChild( endGraphic );
        addChild( readoutGraphic );

        update();
    }

    class BodyGraphic extends PNode {
        private PImage imageGraphic;

        public BodyGraphic() {
            try {
                imageGraphic = new PImage( ImageLoader.loadBufferedImage( "the-ramp/images/tape.gif" ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            addChild( imageGraphic );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    Dimension2D dx = getDelta( event );
                    translateAll( dx.getWidth(), dx.getHeight() );
                }
            } );

            int crossHairLength = 10;
            CrossHairGraphic crossHairGraphic = new CrossHairGraphic( crossHairLength );
            addChild( crossHairGraphic );
            crossHairGraphic.setOffset( imageGraphic.getWidth() - crossHairLength / 2, imageGraphic.getHeight() - crossHairLength / 2 );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        }

        public PImage getImageGraphic() {
            return imageGraphic;
        }
    }

    private Dimension2D getDelta( PInputEvent event ) {
        Dimension2D dx = event.getDeltaRelativeTo( this );
        return dx;
    }

    private void translateAll( double dx, double dy ) {
        Point2D modelTx = modelViewTransform2D.viewToModelDifferential( new Point2D.Double( dx, dy ) );
        modelSrc.x += modelTx.getX();
        modelSrc.y += modelTx.getY();
        modelDst.x += modelTx.getX();
        modelDst.y += modelTx.getY();
        update();
    }

    private void update() {
        Point viewSrc = modelViewTransform2D.modelToView( modelSrc );
        Point viewDst = modelViewTransform2D.modelToView( modelDst );
        MutableVector2D viewVector = new MutableVector2D( viewSrc, viewDst );

//        System.out.println( "bodyGraphic.getTransform() = " + bodyGraphic.getTransform() );
        Line2D.Double line = new Line2D.Double( viewSrc, viewDst );
        tapeGraphic.setLine( line );

        bodyGraphic.setTransform( new AffineTransform() );
        Point2D bodyLoc = new Point2D.Double( viewSrc.getX() - bodyGraphic.getImageGraphic().getWidth(),
                                              viewSrc.getY() - bodyGraphic.getImageGraphic().getHeight() );
        bodyGraphic.translate( bodyLoc.getX(), bodyLoc.getY() );
        bodyGraphic.rotateAboutPoint( viewVector.getAngle(), bodyGraphic.getImageGraphic().getWidth(), bodyGraphic.getImageGraphic().getHeight() );
        endGraphic.setOffset( viewDst.getX() - endGraphic.getShapeGraphic().getWidth() / 2, viewDst.getY() - endGraphic.getShapeGraphic().getHeight() / 2 );

        double modelDistance = new MutableVector2D( modelSrc, modelDst ).magnitude();
        readoutGraphic.setDistance( modelDistance );
        readoutGraphic.setOffset( viewSrc.x, (int) ( viewSrc.y + readoutGraphic.getHeight() * 1.2 + 7 ) );
    }

    class TapeGraphic extends PNode {
        public PPath phetShapeGraphic;

        public TapeGraphic() {
            phetShapeGraphic = new PPath( null );
            phetShapeGraphic.setStroke( new BasicStroke( 2 ) );
            phetShapeGraphic.setPaint( Color.black );
            addChild( phetShapeGraphic );

        }

        public void setLine( Line2D.Double line ) {
            phetShapeGraphic.setPathTo( line );
        }
    }

    class EndGraphic extends PNode {
        private PPath phetShapeGraphic;

        public EndGraphic() {
            Ellipse2D.Double shape = new Ellipse2D.Double( 0, 0, 15, 15 );

            phetShapeGraphic = new PPath( shape );
            phetShapeGraphic.setPaint( Color.black );
            addChild( phetShapeGraphic );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    Dimension2D dx = getDelta( event );
                    MeasuringTape.this.translateEndPoint( dx.getWidth(), dx.getHeight() );
                }
            } );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

            int crossHairSize = 10;
            CrossHairGraphic crossHairGraphic = new CrossHairGraphic( crossHairSize );
            crossHairGraphic.setPaint( Color.yellow );
            addChild( crossHairGraphic );

            crossHairGraphic.setOffset( phetShapeGraphic.getWidth() / 2 - crossHairSize / 2, phetShapeGraphic.getHeight() / 2 - crossHairSize / 2 );
        }

        public PPath getShapeGraphic() {
            return phetShapeGraphic;
        }
    }

    private void translateEndPoint( double dx, double dy ) {
        Point2D modelDX = modelViewTransform2D.viewToModelDifferential( new Point2D.Double( dx, dy ) );
        modelDst.x += modelDX.getX();
        modelDst.y += modelDX.getY();
        System.out.println( "modelDst = " + modelDst );
        update();
    }

    class ReadoutGraphic extends PNode {
        DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        private String units;
        public PText phetShadowTextGraphic;

        public ReadoutGraphic( String units ) {
            this.units = units;
            phetShadowTextGraphic = new PText( "" );
            phetShadowTextGraphic.setFont( new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 ) );
            phetShadowTextGraphic.setTextPaint( Color.black );

            BoundNode boundGraphic = new BoundNode( phetShadowTextGraphic, 2, 2 );
            boundGraphic.setStroke( new BasicStroke() );
            boundGraphic.setStrokePaint( Color.black );
            boundGraphic.setPaint( Color.green );
            addChild( boundGraphic );
            addChild( phetShadowTextGraphic );
        }

        public void setDistance( double modelDistance ) {
            String text = decimalFormat.format( modelDistance ) + " " + units;
            phetShadowTextGraphic.setText( text );
        }
    }

    private Component getComponent() {
        return null;
    }

    static class CrossHairGraphic extends PNode {
        public CrossHairGraphic( int length ) {

            PPath verticalStroke = new PPath( new Line2D.Double( length / 2, 0, length / 2, length ) );
            verticalStroke.setStroke( new BasicStroke( 2 ) );
            verticalStroke.setStrokePaint( Color.red );

            PPath horizStroke = new PPath( new Line2D.Double( 0, length / 2, length, length / 2 ) );
            horizStroke.setStroke( new BasicStroke( 2 ) );
            horizStroke.setStrokePaint( Color.red );

            addChild( verticalStroke );
            addChild( horizStroke );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
