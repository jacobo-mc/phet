/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.Random;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import edu.colorado.phet.opticaltweezers.OTConstants;


public class PositionHistogramPlot extends XYPlot {

    private static final String SERIES_KEY = "positionFrequencySeries";
    private static final Color BACKGROUND_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    private static final Color GRIDLINES_COLOR = Color.BLACK;
    private static final Font AXIS_LABEL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final int NUMBER_OF_HISTOGRAM_BINS = 100;
    
    private NumberAxis _xAxis, _yAxis;
    private double[] _positions;
    
    public PositionHistogramPlot() {
        super();
        
        // axis labels
        String positonLabel = "";
        String frequencyLabel = "";
        
        _xAxis = new NumberAxis();
        _xAxis.setLabel( positonLabel );
        _xAxis.setLabelFont( AXIS_LABEL_FONT );
        _xAxis.setTickLabelsVisible( false );
        _xAxis.setTickMarksVisible( false );
        _xAxis.setAxisLineStroke( AXIS_STROKE );
        _xAxis.setAxisLinePaint( AXIS_COLOR );
        
        _yAxis = new NumberAxis();
        _yAxis.setLabel( frequencyLabel );
        _yAxis.setLabelFont( AXIS_LABEL_FONT );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        _yAxis.setAxisLineStroke( AXIS_STROKE );
        _yAxis.setAxisLinePaint( AXIS_COLOR );
        
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        setDomainGridlinePaint( GRIDLINES_COLOR );
        setRangeGridlinePaint( GRIDLINES_COLOR );
        setDomainAxis( _xAxis );
        setRangeAxis( _yAxis );
        
        // frequency will be normalized, so set range to 1
        _yAxis.setRange( new Range( 0, 1 ) );
    }

    public void setPositionRange( double minPosition, double maxPosition ) {
        _xAxis.setRange( minPosition, maxPosition );
        setPositions( _positions );
    }
    
    public void addPosition( double position ) {
        double[] newPositions = null;
        if ( _positions == null ) {
            newPositions = new double[1];
            newPositions[0] = position;
        }
        else {
            newPositions = new double[ _positions.length + 1 ];
            System.arraycopy( _positions, 0, newPositions, 0, _positions.length );
            newPositions[ newPositions.length - 1 ] = position;
        }
        setPositions( newPositions );
    }

    public void clear() {
        setPositions( null );
    }
    
    private void setPositions( double[] positions ) {
        
        _positions = positions;
        
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType( HistogramType.RELATIVE_FREQUENCY );
        if ( positions != null ) {
            dataset.addSeries( SERIES_KEY, positions, NUMBER_OF_HISTOGRAM_BINS );
        }
        setDataset( dataset );
        
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setPaint( BAR_FILL_COLOR );
        renderer.setOutlinePaint( BAR_OUTLINE_COLOR );
        renderer.setDrawBarOutline( true );
        setRenderer( renderer );
    }
}
