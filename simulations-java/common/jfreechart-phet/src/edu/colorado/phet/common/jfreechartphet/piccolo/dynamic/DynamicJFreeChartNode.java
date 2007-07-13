/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or 
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
* 
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import org.jfree.chart.JFreeChart;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This class extends the functionality of JFreeChartNode by providing different strategies for rendering the data.
 * It is assumed that the chart's plot is XYPlot, and some functionality is lost in rendering, since we
 * have our own rendering strategies here.  Also, the supplied chart's data is not rendered; only the data
 * set explicitly with addValue() to this DynamicJFreeChartNode.
 * <p/>
 * Data is added to the chart through addValue() methods, not through the underlying XYSeriesCollection dataset.
 * <p/>
 * The rendering styles are:
 * 1. JFreeChart renderer: This uses the JFreeChart subsystem to do all the data rendering.
 * This looks beautiful and comes built-in to jfreechart but has performance problems during dynamic data display, since the entire region
 * is repainted whenever a single point changes.
 * <p/>
 * 2. Piccolo renderer: This uses a PPath to render the path as a PNode child of the JFreeChartNode
 * This looks fine and reduces the clip region necessary for painting.  When combined with a buffered jfreechartnode, this can improve computation substantially.
 * This renderer is combined with a PClip to ensure no data is drawn outside the chart's data area (which could otherwise change the fullbounds of the jfreechartnode..
 * <p/>
 * 3. Buffered renderer: This draws directly to the buffer in the JFreeChartNode, and only repaints the changed region of the screen.
 * <p/>
 * 4. Buffered Immediate: This draws directly to the buffer,
 * and immediately repaints the dirty region so that multiple regions don't accumulate in the RepaintManager.
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class DynamicJFreeChartNode extends JFreeChartNode {
    private ArrayList seriesDataList = new ArrayList();//of type SeriesData 
    private ArrayList seriesViewList = new ArrayList();//of type SeriesView
    private PhetPCanvas phetPCanvas;
//    private PhetPPath debugBufferRegion;//internal debugging tool for deciphering screen output regions

    //The default SeriesView is JFreeChart rendering.
    private SeriesViewFactory viewFactory = RENDERER_JFREECHART;
    private boolean updateEnabled = false;//require user to force repaints to reduce redundant calls

    public DynamicJFreeChartNode( PhetPCanvas phetPCanvas, JFreeChart chart ) {
        super( chart );
        this.phetPCanvas = phetPCanvas;
//        debugBufferRegion = new PhetPPath( new BasicStroke( 1.0f ), Color.green );
//        addChild( debugBufferRegion );//this can destroy the bounds of the graph, use with care
    }

    public void forceUpdateAll() {
        this.updateEnabled = true;
        updateAll();
        this.updateEnabled = false;
        for( int i = 0; i < seriesViewList.size(); i++ ) {
            SeriesView seriesView = (SeriesView)seriesViewList.get( i );
            seriesView.forceRepaintAll();
        }
    }

    public void updateAll() {
        if( updateEnabled ) {
            super.updateAll();
        }
    }

    /*
     * This update overriden here doesn't seem to do anything in the Rotation sim except to extend startup time by about 25%
     * todo: investigate this issue
     */
    protected void addPNodeUpdateHandler() {
        addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
//                updateAll();
            }
        } );
    }

    /**
     * Adds the specified (x,y) pair to the 0th series in this plot.
     *
     * @param x the x-value to add
     * @param y the y-value to add
     */
    public void addValue( double x, double y ) {
        addValue( 0, x, y );
    }

    /**
     * Adds the specified (x,y) pair to the specified series.
     *
     * @param series the series to which data should be added.  This series should have already been added to this dynamic jfreechartnode with addSeries().
     * @param x      the x-value to add
     * @param y      the y-value to add
     */
    public void addValue( int series, double x, double y ) {
        getSeries( series ).addValue( x, y );
    }

    public void addSeries( String title, Color color) {
        addSeries( title, color,BufferedSeriesView.DEFAULT_STROKE );
    }

    /**
     * Adds a new series to this chart for plotting, with the given name and color.
     *
     * @param title the title for the series
     * @param color the series' color
     * @param stroke
     */
    public void addSeries( String title, Color color,Stroke stroke ) {
        seriesDataList.add( new SeriesData( title, color ,stroke) );
        updateSeriesViews();
    }

    public void removeSeries( String title ) {
        seriesDataList.remove( getSeriesData( title ) );
        updateSeriesViews();
    }

    /**
     * Looks up the series data object for the specified title; returns the first found, or null if none found.
     *
     * @param title
     * @return the first found, or null if none found.
     */
    private SeriesData getSeriesData( String title ) {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            if( seriesData.getTitle().equals( title ) ) {
                return seriesData;
            }
        }
        return null;
    }

    /**
     * Empties each series associated with this chart.
     */
    public void clear() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            seriesData.clear();
        }
        updateAll();
    }

    //todo: this is public merely for purposes of debugging
    public SeriesData getSeries( int series ) {
        return (SeriesData)seriesDataList.get( series );
    }

    /**
     * Sets the rendering strategy to JFreeChart style.
     */
    public void setJFreeChartSeries() {
        setViewFactory( RENDERER_JFREECHART );
    }

    public SeriesViewFactory getViewFactory() {
        return viewFactory;
    }

    /**
     * Sets the rendering strategy to Piccolo style.
     */
    public void setPiccoloSeries() {
        setViewFactory( RENDERER_PICCOLO );
    }

    /**
     * Sets the rendering strategy to Buffered style.
     */
    public void setBufferedSeries() {
        setViewFactory( RENDERER_BUFFERED );
    }

    /**
     * Sets the rendering strategy to Buffered Immediate style.
     */
    public void setBufferedImmediateSeries() {
        setViewFactory( RENDERER_BUFFERED_IMMEDIATE );
    }

    /**
     * Sets an arbitrary (possibly user-defined) view style.
     *
     * @param factory
     */
    public void setViewFactory( SeriesViewFactory factory ) {
        viewFactory = factory;
        updateSeriesViews();
    }

    private void updateSeriesViews() {
        removeAllSeriesViews();
        updateAll();
        addAllSeriesViews();
        updateChartRenderingInfo();
    }

    private void addAllSeriesViews() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            SeriesView seriesDataView = viewFactory.createSeriesView( this, seriesData );
            seriesDataView.install();
            seriesViewList.add( seriesDataView );
        }
    }

    private void removeAllSeriesViews() {
        while( seriesViewList.size() > 0 ) {
            SeriesView seriesView = (SeriesView)seriesViewList.get( 0 );
            seriesView.uninstall();
            seriesViewList.remove( seriesView );
        }
    }

    public void setBuffered( boolean buffered ) {
        super.setBuffered( buffered );
        updateSeriesViews();
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public void addBufferedImagePropertyChangeListener( PropertyChangeListener listener ) {
        super.addBufferedImagePropertyChangeListener( listener );
    }

    public void removeBufferedImagePropertyChangeListener( PropertyChangeListener listener ) {
        super.removeBufferedImagePropertyChangeListener( listener );
    }

    public BufferedImage getBuffer() {
        return super.getBuffer();
    }

    public static final SeriesViewFactory RENDERER_JFREECHART = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new JFreeChartSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };

    public static final SeriesViewFactory RENDERER_BUFFERED = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BufferedSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    public static final SeriesViewFactory RENDERER_PICCOLO = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new PPathSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    public static final SeriesViewFactory RENDERER_BUFFERED_IMMEDIATE = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BufferedImmediateSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    public static final SeriesViewFactory RENDERER_PICCOLO_INCREMENTAL_IMMEDIATE = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new ImmediateBoundedPPathSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };

    public static final SeriesViewFactory RENDERER_PICCOLO_INCREMENTAL = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BoundedPPathSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };

    public void setSeriesVisible( String title, boolean visible ) {
        getSeriesData( title ).setVisible( visible );
        updateSeriesViews();
    }
}
