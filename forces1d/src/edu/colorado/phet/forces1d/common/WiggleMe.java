package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 28, 2004
 * Time: 7:56:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class WiggleMe extends CompositePhetGraphic {
    private long t0 = System.currentTimeMillis();
//    private double frequency = 0.0025;//in Hertz.
    private double frequency = 2.5;//in Hertz.
    private double amplitude = 30;//In pixels.
    private Vector2D.Double oscillationAxis;//axis along which the wiggle me moves.
    private Target target;
    private PhetShapeGraphic phetShapeGraphic;
    private ShadowHTMLGraphic textGraphic;

    public WiggleMe( Component component, AbstractClock clock, String text, PhetGraphic phetGraphic ) {
        this( component, clock, text, new PhetGraphicTarget( phetGraphic ) );
    }

    public WiggleMe( final Component component, AbstractClock clock, String text, Target t ) {
        this( component, clock, text, t, new Font( "Lucida Sans", Font.BOLD, 20 ), 1, 1 );
    }

    public WiggleMe( final Component component, AbstractClock clock, String text, Target t, Font font, int dx, int dy ) {
        super( component );
        this.target = t;
        this.oscillationAxis = new Vector2D.Double( 0, 1 );
        Color foreGroundColor = new Color( 39, 27, 184 );
        Color shadowColor = new Color( 6, 0, 44 );
        textGraphic = new ShadowHTMLGraphic( component, text, font, foreGroundColor, dx, dy, shadowColor );
        addGraphic( textGraphic );

        textGraphic.setLocation( 0, 0 );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                tick();
            }
        } );
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( 50, 0 ), 20, 20, 10 );
        phetShapeGraphic = new PhetShapeGraphic( component, arrow.getShape(), Color.blue, new BasicStroke( 2 ), Color.black );

        addGraphic( phetShapeGraphic );
//        phetShapeGraphic.setLocation( 0, textGraphic.getHeight() + 5 );
        setRegistrationPoint( getBounds().x, getBounds().y );
        tick();
        setIgnoreMouse( true );
        phetShapeGraphic.setLocation( textGraphic.getWidth() - phetShapeGraphic.getWidth() - 5, textGraphic.getHeight() + 5 );
    }

    public void setOscillationAxis( Vector2D.Double oscillationAxis ) {
        this.oscillationAxis = oscillationAxis;
    }

    public void setFrequency( double frequency ) {
        this.frequency = frequency;
    }

    public void setAmplitude( double amplitude ) {
        this.amplitude = amplitude;
    }

    public void setFont( Font font ) {
        textGraphic.setFont( font );
    }

    public void setArrowDirection( double dx, double dy ) {
        Arrow arrow = new Arrow( new Point2D.Double( 0, 0 ), new Point2D.Double( dx, dy ), 20, 20, 10 );
        phetShapeGraphic.setShape( arrow.getShape() );
    }

    private void tick() {
        if( isVisible() && getComponent().isShowing() ) {
            double time = ( System.currentTimeMillis() - t0 ) / 1000.0;
            Point targetLoc = target.getLocation();

            Point oscillationCenter = new Point( targetLoc.x - getWidth() - 5, targetLoc.y );
            double distAlongAxis = Math.sin( frequency * time ) * amplitude;
            AbstractVector2D norm = oscillationAxis.getInstanceOfMagnitude( distAlongAxis );
            Point2D dest = norm.getDestination( oscillationCenter );

            setLocation( (int)dest.getX(), (int)dest.getY() );
        }
    }

    public void setArrowColor( Color color ) {
        phetShapeGraphic.setColor( color );
    }

    public static interface Target {

        Point getLocation();

        int getHeight();
    }

    public static class PhetGraphicTarget implements Target {
        private PhetGraphic target;

        public PhetGraphicTarget( PhetGraphic t ) {
            this.target = t;
        }

        public Point getLocation() {
            Point targetLoc = target.getLocation();
            return targetLoc;
        }

        public int getHeight() {
            return target.getHeight();
        }
    }
}
