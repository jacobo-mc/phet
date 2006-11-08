/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * ThermometerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
/* Copyright 2003-2004, University of Colorado */


public class ThermometerGraphic extends PNode {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static Color s_color = Color.red;
    private static Color s_outlineColor = Color.black;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

//    private BarGauge fillNode;
    private Ellipse2D.Double bulb;
    private NumberFormat formatter = new DecimalFormat( "#0" );
    private Point2D location;
    private double scale;
    private double maxScreenLevel;
    private double thickness;
    private double value;
    private Rectangle2D boundingRect;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 10 );
    private FontMetrics fontMetrics;
    private double rectBorderThickness = 2;
    private RoundRectangle2D.Double readoutRect = new RoundRectangle2D.Double();
    private RoundRectangle2D.Double innerRect = new RoundRectangle2D.Double();
    private BasicStroke rectStroke = new BasicStroke( 3 );
    private float columnStrokeWidth = 1.5f;
    private BasicStroke columnStroke = new BasicStroke( columnStrokeWidth );
    private Color rectColor = Color.yellow;
    private int readoutWidth;
    private float readoutRectStrokeWidth = 0.5f;
    private BasicStroke readoutRectStroke = new BasicStroke( readoutRectStrokeWidth );
    private BasicStroke oneStroke;

    private PPath fillNode;
    private Rectangle2D columnFill;


    private double bulbRadius = 8;
    private double columnWidth = 8;
    private double overallHeight = 80;
    private MRModel model;
    private Rectangle2D column;
    private double maxKe = 100;
    private double fillHeightScale;

    /**
     * @param minLevel
     * @param maxLevel
     */
    public ThermometerGraphic( MRModel model, IClock clock, double minLevel, double maxLevel ) {
        this.model = model;

        column = new Rectangle2D.Double(0,0,columnWidth, overallHeight - bulbRadius );
        fillHeightScale = (overallHeight - bulbRadius) / maxKe;
        PPath columnNode = new PPath( column );
        columnNode.setPaint( new Color( 0,0,0,0 ) );

        columnFill = new Rectangle2D.Double();
        fillNode = new PPath( columnFill );
        fillNode.setPaint( Color.red );
        bulb = new Ellipse2D.Double( 0,0, bulbRadius * 2, bulbRadius * 2 );
        PPath bulbNode = new PPath( bulb );
        bulbNode.setPaint( Color.red );
        this.location = location;
        this.thickness = thickness;
        scale = maxScreenLevel / maxLevel;
        this.maxScreenLevel = maxScreenLevel;
        setPickable( false );

        fillNode.setOffset( -columnWidth / 2, 0 );
        bulbNode.setOffset( -bulbRadius, overallHeight - bulbRadius * 2);
        columnNode.setOffset( -columnWidth / 2, 0 );

        addChild( fillNode );
        addChild( columnNode );
        addChild( bulbNode );

        clock.addClockListener( new Updater() );
    }

    public void update() {
        double ke = model.getAverageKineticEnergy() * fillHeightScale;
        System.out.println( "ke = " + ke );
        columnFill.setFrame( 0, column.getHeight() - ke, column.getWidth(), ke );
        fillNode.setPathTo( columnFill);
    }

    private class Updater extends ClockAdapter {
        public void clockTicked( ClockEvent clockEvent ) {
            update();
        }
    }
}
