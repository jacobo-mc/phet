/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.BasicStroke;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;


/**
 * EnergyPlot is the plot that displays total and potential energy.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyPlot extends XYPlot implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractPotential _potentialEnergy;
    private TotalEnergy  _totalEnergy;
    
    private XYSeries _totalEnergySeries;
    private XYSeries _potentialEnergySeries;
    
    private int _peRendererIndex;
    private int _tePlaneRendererIndex;
    private int _tePacketRendererIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        
        int index = 0;
        
        // Potential Energy series
        _potentialEnergySeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            _peRendererIndex = index++;
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _potentialEnergySeries );
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( QTConstants.POTENTIAL_ENERGY_COLOR );
            renderer.setStroke( QTConstants.POTENTIAL_ENERGY_STROKE );
            setDataset( _peRendererIndex, dataset );
            setRenderer( _peRendererIndex, renderer );
        }
        
        // Total Energy series
        _totalEnergySeries = new XYSeries( totalEnergyLabel, false /* autoSort */ );
        {
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _totalEnergySeries );
            
            // Renderer for plane wave
            _tePlaneRendererIndex = index++;
            XYItemRenderer planeRenderer = new StandardXYItemRenderer();
            planeRenderer.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
            planeRenderer.setStroke( QTConstants.TOTAL_ENERGY_STROKE );
            setDataset( _tePlaneRendererIndex, dataset );
            setRenderer( _tePlaneRendererIndex, planeRenderer );
            
            // Renderer for wave packet
            _tePacketRendererIndex = index++;
            XYItemRenderer packetRenderer = new PacketTotalEnergyRenderer( QTConstants.TOTAL_ENERGY_DEVIATION );
            packetRenderer.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
            packetRenderer.setStroke( new BasicStroke( 50f ) );
            setDataset( _tePacketRendererIndex, dataset );
            setRenderer( _tePacketRendererIndex, packetRenderer );
        }
        setWaveType( WaveType.PLANE );
        
        // X axis 
        PositionAxis xAxis = new PositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.ENERGY_RANGE );
        if ( QTConstants.USE_INTEGER_ENERGY_TICKS ) {
            TickUnits yUnits = (TickUnits) NumberAxis.createIntegerTickUnits();
            yAxis.setStandardTickUnits( yUnits );
        }
        yAxis.setTickLabelPaint( QTConstants.TICK_LABEL_COLOR );
        yAxis.setTickMarkPaint( QTConstants.TICK_MARK_COLOR );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis ); 
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wave type, which determines how total energy is displayed.
     * 
     * @param true or false
     */
    public void setWaveType( WaveType waveType ) {
        boolean isPlane = ( waveType == WaveType.PLANE );
        getRenderer( _tePlaneRendererIndex ).setSeriesVisible( new Boolean( isPlane ) );
        getRenderer( _tePacketRendererIndex ).setSeriesVisible( new Boolean( !isPlane ) );
    }
    
    /**
     * Sets the total energy model that is displayed.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        updateTotalEnergy();
    }
    
    /**
     * Sets the potential energy model that is displayed.
     * 
     * @param potential
     */
    public void setPotentialEnergy( AbstractPotential potential ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potential;
        _potentialEnergy.addObserver( this );
        updatePotentialEnergy();
    }
    
    /**
     * Sets the font used for labeling the axes.
     * 
     * @param font
     */
    public void setAxesFont( Font font ) {
        getDomainAxis().setLabelFont( font );
        getRangeAxis().setLabelFont( font );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _potentialEnergy ) {
            updatePotentialEnergy();
        }
        else if ( observable == _totalEnergy ) {
            updateTotalEnergy();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------
    
    /*
     * Updates the total energy series to match the model.
     */
    private void updateTotalEnergy() {
        Range range = getDomainAxis().getRange();
        _totalEnergySeries.setNotify( false );
        _totalEnergySeries.clear();
        _totalEnergySeries.add( range.getLowerBound(), _totalEnergy.getEnergy() );
        _totalEnergySeries.add( range.getUpperBound(), _totalEnergy.getEnergy() );
        _totalEnergySeries.setNotify( true );
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updatePotentialEnergy() {
        _potentialEnergySeries.setNotify( false );
        _potentialEnergySeries.clear();
        int numberOfRegions = _potentialEnergy.getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {
            double start = _potentialEnergy.getStart( i );
            double end = _potentialEnergy.getEnd( i );
            double energy = _potentialEnergy.getEnergy( i );
            _potentialEnergySeries.add( start, energy );
            _potentialEnergySeries.add( end, energy );
        }
        _potentialEnergySeries.setNotify( true );
    }
}
