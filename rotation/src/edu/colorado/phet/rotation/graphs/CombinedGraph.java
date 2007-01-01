package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:33:46 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

public class CombinedGraph extends PNode {
    private JFreeChart jFreeChart;
    private JFreeChartNode chartNode;
    private PNode controlNode;

    public CombinedGraph( XYPlot[] subplot ) {
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot( new NumberAxis( "Domain" ) );
        plot.setOrientation( PlotOrientation.VERTICAL );
        plot.setGap( 10.0 );

        controlNode = new PNode();

        for( int i = 0; i < subplot.length; i++ ) {
            XYPlot xyPlot = subplot[i];
            plot.add( xyPlot );
        }

//        for( int i = 0; i < combinedGraphComponents.length; i++ ) {
//            // create subplot 1...
//            final XYDataset data1 = createDataset( i );
//            final XYItemRenderer renderer1 = new StandardXYItemRenderer();
//            final NumberAxis rangeAxis1 = new NumberAxis( combinedGraphComponents[i].getRangeAxisTitle() );
//            final XYPlot subplot1 = new XYPlot( data1, null, rangeAxis1, renderer1 );
//            subplot1.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
//
//            plot.add( subplot1 );
//        }

        // return a new chart containing the overlaid plot...
        this.jFreeChart = new JFreeChart( "CombinedDomainXYPlot Demo",
                                          JFreeChart.DEFAULT_TITLE_FONT, plot, true );

        chartNode = new JFreeChartNode( jFreeChart );
        addChild( chartNode );
        addChild( controlNode );
        chartNode.setOffset( 50, 0 );
    }

    public JFreeChart getJFreeChart() {
        return jFreeChart;
    }

    public JFreeChartNode getChartNode() {
        return chartNode;
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        chartNode.setBounds( x, y, width, height );
        return super.setBounds( x, y, width, height );
    }

    private XYDataset createDataset( int index ) {
        return new XYSeriesCollection( new XYSeries( "series_" + index ) );
    }

    public void addControl( PNode control ) {
        controlNode.addChild( control );
    }
}
