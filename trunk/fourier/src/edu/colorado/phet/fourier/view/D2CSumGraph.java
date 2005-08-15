/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.charts.SumChart;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * D2CSumGraph
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CSumGraph extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;
    private static final double MATH_LAYER = 5;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 200 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 115 );
    
    // Chart parameters
    private static final double X_MIN = -2;
    private static final double X_MAX = 2;
    private static final double Y_MIN = -1;
    private static final double Y_MAX = 1;
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 540, 135 );
    
    // Harmonics in the chart
    private static final int HARMONIC_DARKEST_GRAY = 0; //dark gray
    private static final int HARMONIC_LIGHTEST_GRAY = 230;  // light gray
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _closeButton;
    private ZoomControl _horizontalZoomControl;
    private SumChart _chartGraphic;
    private SumEquation _mathGraphic;
    private String _xTitleSpace, _xTitleTime;
    private int _domain;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param wavePacket the Gaussian wave packet being displayed
     */
    public D2CSumGraph( Component component, GaussianWavePacket wavePacket ) {
        super( component );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Model
        _wavePacket = wavePacket;
        _wavePacket.addObserver( this );
        
        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        _backgroundGraphic.setLocation( 0, 0 );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        
        // Title
        String title = SimStrings.get( "D2CSumGraph.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( 40, BACKGROUND_SIZE.height/2 );
        addGraphic( _titleGraphic, TITLE_LAYER );
        
        // Chart
        _chartGraphic = new SumChart( component, CHART_RANGE, CHART_SIZE );
        addGraphic( _chartGraphic, CHART_LAYER );
        _chartGraphic.setRegistrationPoint( 0, CHART_SIZE.height / 2 ); // at the chart's origin
        _chartGraphic.setLocation( 60, 50 + ( CHART_SIZE.height / 2 ) );
        _xTitleSpace = SimStrings.get( "D2CSumGraph.xTitleSpace" );
        _xTitleTime = SimStrings.get( "D2CSumGraph.xTitleTime" );
        
        // Close button
        _closeButton = new PhetImageGraphic( component, FourierConstants.CLOSE_BUTTON_IMAGE );
        addGraphic( _closeButton, CONTROLS_LAYER );
        _closeButton.centerRegistrationPoint();
        _closeButton.setLocation( (_closeButton.getWidth()/2) + 10, _closeButton.getHeight()/2 + 5 );
        
        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            // Location is aligned with top-right edge of chart.
            int x = _chartGraphic.getX() + CHART_SIZE.width + 20;
            int y = _chartGraphic.getY() - ( CHART_SIZE.height / 2 );
            _horizontalZoomControl.setLocation( x, y );
        }

        // Math
        {
            _mathGraphic = new SumEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            // Location is above the center of the chart.
            int x = _chartGraphic.getX() + ( CHART_SIZE.width / 2 );
            int y = 28;
            _mathGraphic.setLocation( x, y );
        }
        
        // Interactivity
        {
            _backgroundGraphic.setIgnoreMouse( true );
            _titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );
            _mathGraphic.setIgnoreMouse( true );

            _closeButton.setCursorHand();
        }
        
        reset();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _wavePacket.removeObserver( this );
        _wavePacket = null;
    }
    
    /**
     * Resets to the initial state.
     */
    public void reset() {
        update();
        setDomain( FourierConstants.DOMAIN_SPACE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the horizontal zoom control.
     * 
     * @return the horizontal zoom control
     */
    public ZoomControl getHorizontalZoomControl() {
        return _horizontalZoomControl;
    }
    
    /**
     * Sets the domain.
     * Changes various labels on the chart, tools, formulas, etc.
     * 
     * @param domain DOMAIN_SPACE or DOMAIN_TIME
     * @throws IllegalArgumentException if the domain is invalid or not supported
     */
    public void setDomain( int domain ) {
        _domain = domain;
        updateMath();
        updateAxisTitles();
    }
    
    /**
     * Gets a reference to the "close" button.
     * 
     * @return close button
     */
    public PhetImageGraphic getCloseButton() {
        return _closeButton;
    }
    
    /**
     * Sets the height of this graphic.
     * 
     * @param height
     */
    public void setHeight( int height ) {
        int newHeight = BACKGROUND_SIZE.height;
        if ( height > BACKGROUND_SIZE.height ) {
            newHeight = height;
            _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, height ) );
            _chartGraphic.setChartSize( CHART_SIZE.width, height - 70 );
            _titleGraphic.setLocation( TITLE_LOCATION.x, height / 2 );
            setBoundsDirty();
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     * Called when the wave packet notifies us that it has been changed.
     */
    public void update() {
        
        System.out.println( "D2CHarmonicsChart.update" ); //XXX
        
        _chartGraphic.removeAllDataSetGraphics();

        updateMath(); // ...in case the number of components has changed
    }

    private void updateMath() {
        int numberOfHarmonics = _wavePacket.getNumberOfComponents();
        if ( _domain == FourierConstants.DOMAIN_SPACE ) {
            _mathGraphic.setForm( _domain, FourierConstants.MATH_FORM_WAVE_NUMBER, numberOfHarmonics );
        }
        else if ( _domain == FourierConstants.DOMAIN_TIME ) {
            _mathGraphic.setForm( _domain, FourierConstants.MATH_FORM_ANGULAR_FREQUENCY, numberOfHarmonics );
        }
        _mathGraphic.centerRegistrationPoint();
    }
    
    private void updateAxisTitles() {
        if ( _domain == FourierConstants.DOMAIN_SPACE ) {
            _chartGraphic.setXAxisTitle( _xTitleSpace );
        }
        else if ( _domain == FourierConstants.DOMAIN_TIME ) {
            _chartGraphic.setXAxisTitle( _xTitleTime );
        }
    }
}
