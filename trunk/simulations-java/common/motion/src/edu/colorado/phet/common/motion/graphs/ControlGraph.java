package edu.colorado.phet.common.motion.graphs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.SeriesData;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * The ControlGraph displays a graph of data for (multiple) time series data,
 * including horziontal and vertical zoom in/out and (optionally) a slider control for changing the data.
 *
 * @author Sam Reid
 */
public class ControlGraph extends PNode {
    private JFreeChart jFreeChart;
    private DynamicJFreeChartNode dynamicJFreeChartNode;

    private GraphTimeControlNode graphTimeControlNode;
    private JFreeChartSliderNode jFreeChartSliderNode;
    private ZoomSuiteNode zoomControl;
    private TitleLayer titleLayer;

    private double minDomainValue = 0;
    private double maxDomainValue;
    private double ZOOM_FRACTION = 1.1;
    private Layout layout = new FlowLayout();
    private ArrayList series = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private PSwing additionalControls;
    private VerticalLayoutPanel additionalControlPanel = new VerticalLayoutPanel();
    private IVariable variable;

    private double defaultMinY;
    private double defaultMaxY;
    private double defaultMaxX;

    public ControlGraph( PhetPCanvas pSwingCanvas, final ITemporalVariable temporalVariable,
                         String title, double minY, double maxY, TimeSeriesModel timeSeriesModel ) {
        this( pSwingCanvas, new ControlGraphSeries( temporalVariable ), title, minY, maxY, timeSeriesModel );
    }

    public ControlGraph( PhetPCanvas pSwingCanvas, ControlGraphSeries series,
                         String title, double minY, final double maxY, TimeSeriesModel timeSeriesModel ) {
        this( pSwingCanvas, series, title, minY, maxY, timeSeriesModel, 1000 );
    }

    public ControlGraph( PhetPCanvas pSwingCanvas, ControlGraphSeries series,
                         String title, double minY, final double maxY, TimeSeriesModel timeSeriesModel, double maxDomainTime ) {
        this( createDefaultChart( title ), pSwingCanvas, series, minY, maxY, timeSeriesModel, maxDomainTime );
    }

    public ControlGraph( JFreeChart jFreeChart, PhetPCanvas pSwingCanvas, ControlGraphSeries series,
                         double minY, final double maxY, TimeSeriesModel timeSeriesModel, double maxDomainTime ) {
        this.jFreeChart = jFreeChart;
        PNode thumb = null;
        if ( series != null ) {
            this.variable = series.getTemporalVariable();
            variable.addListener( new IVariable.Listener() {
                public void valueChanged() {
                    updateSliderValue();
                }
            } );
            thumb = new PImage( ColorArrows.createArrow( series.getColor() ) );
        }
        this.maxDomainValue = maxDomainTime;
        titleLayer = createTitleLayer();
        jFreeChart.setTitle( (String) null );
        setVerticalRange( minY, maxY );
        setHorizontalRangeMax( maxDomainTime );
        jFreeChart.setBackgroundPaint( null );

        dynamicJFreeChartNode = new DynamicJFreeChartNode( pSwingCanvas, jFreeChart );
        dynamicJFreeChartNode.setBuffered( true );
        dynamicJFreeChartNode.setBounds( 0, 0, 300, 400 );
        dynamicJFreeChartNode.setBufferedImmediateSeries();

        graphTimeControlNode = createGraphTimeControlNode( timeSeriesModel );
        additionalControls = new PSwing( additionalControlPanel );
//        additionalControls.addChild( new PSwing( additionalControlPanel) );

        jFreeChartSliderNode = createSliderNode( thumb, series != null ? series.getColor() : Color.yellow );
        zoomControl = new ZoomSuiteNode();
        zoomControl.addVerticalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                zoomVertical( ZOOM_FRACTION );
            }

            public void zoomedIn() {
                zoomVertical( 1.0 / ZOOM_FRACTION );
            }
        } );
        zoomControl.addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                zoomHorizontal( ZOOM_FRACTION );
            }

            public void zoomedIn() {
                zoomHorizontal( 1.0 / ZOOM_FRACTION );
            }
        } );

        addChild( graphTimeControlNode );
        addChild( additionalControls );
        addChild( jFreeChartSliderNode );
        addChild( dynamicJFreeChartNode );
        addChild( zoomControl );
        addChild( titleLayer );

        jFreeChartSliderNode.addListener( new JFreeChartSliderNode.Adapter() {
            public void sliderThumbGrabbed() {
                notifyControlGrabbed();
            }

            public void sliderDragged( double value ) {
                handleValueChanged();
            }
        } );

        dynamicJFreeChartNode.updateChartRenderingInfo();
        relayout();
        updateHorizontalZoomEnabled();

        //for debugging, attach listeners that allow change of rendering style.
        addInputEventListener( new PBasicInputEventHandler() {
            public void keyPressed( PInputEvent event ) {
                if ( event.getKeyCode() == KeyEvent.VK_1 ) {
                    dynamicJFreeChartNode.setJFreeChartSeries();
                }
                else if ( event.getKeyCode() == KeyEvent.VK_2 ) {
                    dynamicJFreeChartNode.setPiccoloSeries();
                }
                else if ( event.getKeyCode() == KeyEvent.VK_3 ) {
                    dynamicJFreeChartNode.setBufferedSeries();
                }
                else if ( event.getKeyCode() == KeyEvent.VK_4 ) {
                    dynamicJFreeChartNode.setBufferedImmediateSeries();
                }
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                event.getInputManager().setKeyboardFocus( event.getPath() );
            }
        } );
        this.defaultMinY = minY;
        this.defaultMaxY = maxY;
        this.defaultMaxX = maxDomainTime;

        if ( series != null ) {
            addSeries( series );
        }
        updateSliderValue();
    }

    public static JFreeChart createDefaultChart( String title ) {
        return createXYLineChart( title, null, null, new XYSeriesCollection( new XYSeries( "dummy series", false ) ), PlotOrientation.VERTICAL );
    }

    public static JFreeChart createXYLineChart( String title, String xAxisLabel, String yAxisLabel, XYDataset dataset,
                                                PlotOrientation orientation ) {
        NumberAxis xAxis = new NumberAxis( xAxisLabel );
        xAxis.setAutoRangeIncludesZero( false );
        NumberAxis yAxis = new NumberAxis( yAxisLabel );
        XYItemRenderer renderer = new XYLineAndShapeRenderer( true, false );
        XYPlot plot = new XYPlot( dataset, xAxis, yAxis, renderer );
        plot.setOrientation( orientation );

        return new JFreeChartDecorator( title, JFreeChart.DEFAULT_TITLE_FONT, plot, false );
    }

    protected JFreeChartSliderNode createSliderNode( PNode thumb, Color highlightColor ) {
        return new JFreeChartSliderNode( dynamicJFreeChartNode, thumb == null ? new PPath() : thumb, highlightColor );//todo: better support for non-controllable graphs
    }

    public void setHorizontalRangeMax( double maxDomainValue ) {
        setHorizontalRange( 0, maxDomainValue );
    }

    public void setHorizontalRange( double minDomainValue, double maxDomainValue ) {
        if ( minDomainValue != this.minDomainValue || this.maxDomainValue != maxDomainValue ) {
            jFreeChart.getXYPlot().getDomainAxis().setRange( minDomainValue, maxDomainValue );
            this.minDomainValue = minDomainValue;
            this.maxDomainValue = maxDomainValue;
            notifyZoomChanged();
        }
    }

    protected GraphTimeControlNode createGraphTimeControlNode( TimeSeriesModel timeSeriesModel ) {
        return new GraphTimeControlNode( timeSeriesModel );
    }

    protected void updateSliderValue() {
        if ( jFreeChartSliderNode != null && getSimulationVariable() != null ) {
            jFreeChartSliderNode.setValue( getSimulationVariable().getValue() );
        }
    }

    protected JFreeChartSliderNode getJFreeChartSliderNode() {
        return jFreeChartSliderNode;
    }

    public void setVerticalRange( double minY, double maxY ) {
        jFreeChart.getXYPlot().getRangeAxis().setRange( minY, maxY );
    }

    protected TitleLayer createTitleLayer() {
        return new TitleLayer();
    }

    public IVariable getSimulationVariable() {
        return variable;
    }

    protected void handleValueChanged() {
        getSimulationVariable().setValue( getModelValue() );
        notifyValueChanged( getModelValue() );
    }

    public double getModelValue() {
        return getSliderValue();
    }

    protected void notifyValueChanged( double value ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.valueChanged( value );
        }
    }

    protected double getSliderValue() {
        return jFreeChartSliderNode.getValue();
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    private void notifyControlGrabbed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.controlFocusGrabbed();
        }
    }

    //todo: should we use addListener instead?
    public void addHorizontalZoomListener( ZoomControlNode.ZoomListener zoomListener ) {
        zoomControl.addHorizontalZoomListener( zoomListener );
    }

    public void addControl( JComponent component ) {
        additionalControlPanel.add( component );
        additionalControls.computeBounds();
    }

    protected void handleControlFocusGrabbed() {
    }

    public void resetRange() {
        setVerticalRange( defaultMinY, defaultMaxY );
        setHorizontalRangeMax( defaultMaxX );
    }

    public double getDefaultMinY() {
        return defaultMinY;
    }

    public double getDefaultMaxY() {
        return defaultMaxY;
    }

    public double getDefaultMaxX() {
        return defaultMaxX;
    }

    private void zoomHorizontal( double v ) {
        double currentValue = jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
        double newValue = currentValue * v;
        if ( newValue > maxDomainValue ) {
            newValue = maxDomainValue;
        }
        setDomainUpperBound( newValue );
        forceUpdateAll();
    }

    private void notifyZoomChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).zoomChanged();
        }
    }

    private void zoomVertical( double zoomValue ) {
        Range verticalRange = getVerticalRange( zoomValue );
        setVerticalRange( verticalRange.getLowerBound(), verticalRange.getUpperBound() );
        updateHorizontalZoomEnabled();//todo: this should probably update the vertical zoom
        notifyZoomChanged();
        forceUpdateAll();
    }

    protected Range getVerticalRange( double zoomValue ) {
        double currentRange = jFreeChart.getXYPlot().getRangeAxis().getUpperBound() - jFreeChart.getXYPlot().getRangeAxis().getLowerBound();
        double newRange = currentRange * zoomValue - currentRange;
        return new Range( jFreeChart.getXYPlot().getRangeAxis().getLowerBound() - newRange / 2, jFreeChart.getXYPlot().getRangeAxis().getUpperBound() + newRange / 2 );
    }

    private void updateHorizontalZoomEnabled() {
        zoomControl.setHorizontalZoomOutEnabled( jFreeChart.getXYPlot().getDomainAxis().getUpperBound() != maxDomainValue );
    }

    public ControlGraphSeries getControlGraphSeries( int i ) {
        return (ControlGraphSeries) series.get( i );
    }

    public int getSeriesCount() {
        return this.series.size();
    }

    public void addSliderListener( JFreeChartSliderNode.Listener listener ) {
        jFreeChartSliderNode.addListener( listener );
    }

    public static class TitleLayer extends PhetPNode {
        public TitleLayer() {
        }

        public void addReadoutNode( ReadoutTitleNode titleNode ) {
            titleNode.setOffset( getFullBounds().getWidth(), 0 );
            addChild( titleNode );
        }

        public ReadoutTitleNode getReadoutNode( ControlGraphSeries series ) {
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                if ( getChild( i ) instanceof ReadoutTitleNode ) {
                    ReadoutTitleNode readoutTitleNode = (ReadoutTitleNode) getChild( i );
                    if ( readoutTitleNode.getSeries() == series ) {
                        return readoutTitleNode;
                    }
                }
            }
            return null;
        }
    }

    public void addSeries( final ControlGraphSeries series ) {
        this.series.add( series );
        final SeriesData data = dynamicJFreeChartNode.addSeries( series.getTitle(), series.getColor(), series.getStroke() );

        final ReadoutTitleNode titleNode = createReadoutTitleNode( series );
        titleLayer.addReadoutNode( titleNode );

        GraphControlSeriesNode seriesNode = null;
        if ( series.isEditable() ) {
            seriesNode = graphTimeControlNode.addVariable( series );
            seriesNode.addListener( new GraphControlTextBox.Listener() {
                public void valueChanged( double newValue ) {
                    notifyValueChanged( newValue );
                }
            } );
            seriesNode.getTextBox().getTextField().addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    handleControlFocusGrabbed();
                }
            } );
        }
        series.getTemporalVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void dataAdded( TimeData timeData ) {
                handleDataAdded( getSeriesIndex( series ), timeData );
            }

            public void dataCleared() {
                clear();
            }
        } );

        final GraphControlSeriesNode seriesNodeTemp = seriesNode;
        series.addListener( new ControlGraphSeries.Adapter() {
            public void visibilityChanged() {
                dynamicJFreeChartNode.setSeriesVisible( data, series.isVisible() );
                titleNode.setVisible( series.isVisible() );
                if ( seriesNodeTemp != null ) {
                    seriesNodeTemp.setVisible( series.isVisible() );
                }
                getDynamicJFreeChartNode().forceUpdateAll();
            }
        } );
    }

    protected ReadoutTitleNode createReadoutTitleNode( ControlGraphSeries series ) {
        return new ReadoutTitleNode( series );
    }

    protected void handleDataAdded( int seriesIndex, TimeData timeData ) {
        addValue( seriesIndex, timeData.getTime(), timeData.getValue() );
    }

    protected int getSeriesIndex( ControlGraphSeries series ) {
        for ( int i = 0; i < getSeriesCount(); i++ ) {
            if ( getControlGraphSeries( i ) == series ) {
                return i;
            }
        }
        return -1;
    }

    public double getMinDataX() {
        return jFreeChart.getXYPlot().getDomainAxis().getLowerBound();
    }

    public double getMaxDataX() {
        return jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
    }

    public void setDomainUpperBound( double maxDataX ) {
        if ( jFreeChart.getXYPlot().getDomainAxis().getUpperBound() != maxDataX ) {
            jFreeChart.getXYPlot().getDomainAxis().setUpperBound( maxDataX );
            updateHorizontalZoomEnabled();
            notifyZoomChanged();
        }
    }

    public void setFlowLayout() {
        setLayout( new FlowLayout() );
    }

    public void setAlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
        setLayout( new AlignedLayout( minimizableControlGraphs ) );
    }

    public JFreeChartNode getJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        relayout();
    }

    public interface Layout {
        void layout();
    }

    public class FlowLayout implements Layout {
        public void layout() {
            double dx = 5;
            graphTimeControlNode.setOffset( 0, 0 );
            additionalControls.setOffset( 0, graphTimeControlNode.getFullBounds().getMaxY() );
            jFreeChartSliderNode.setOffset( graphTimeControlNode.getFullBounds().getMaxX() + dx, 0 );

            //putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            dynamicJFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - jFreeChartSliderNode.getFullBounds().getMaxX(), getBounds().getHeight() );
            dynamicJFreeChartNode.setOffset( jFreeChartSliderNode.getFullBounds().getMaxX(), 0 );
            dynamicJFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( dynamicJFreeChartNode.getFullBounds().getMaxX(), dynamicJFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = dynamicJFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + dynamicJFreeChartNode.getOffset().getX(), d.getY() + dynamicJFreeChartNode.getOffset().getY() );

            setOffset( getBounds().getX(), getBounds().getY() );
        }
    }

    static interface LayoutFunction {
        double getValue( MinimizableControlGraph minimizableControlGraph );
    }

    public class AlignedLayout implements Layout {
        private MinimizableControlGraph[] minimizableControlGraphs;

        public AlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
            this.minimizableControlGraphs = minimizableControlGraphs;
        }

        public double[] getValues( LayoutFunction layoutFunction ) {
            ArrayList values = new ArrayList();
            for ( int i = 0; i < minimizableControlGraphs.length; i++ ) {
                if ( !minimizableControlGraphs[i].isMinimized() ) {
                    values.add( new Double( layoutFunction.getValue( minimizableControlGraphs[i] ) ) );
                }
            }
            double[] val = new double[values.size()];
            for ( int i = 0; i < val.length; i++ ) {
                val[i] = ( (Double) values.get( i ) ).doubleValue();
            }
            return val;
        }

        public void layout() {
//            System.out.println( "ControlGraph$AlignedLayout.layout: " + ControlGraph.this );
            double dx = 5;
            graphTimeControlNode.setOffset( 0, 0 );
            additionalControls.setOffset( 0, graphTimeControlNode.getFullBounds().getMaxY() );
            LayoutFunction controlNodeMaxX = new LayoutFunction() {
                public double getValue( MinimizableControlGraph minimizableControlGraph ) {
                    double maxControlNodeWidth = minimizableControlGraph.getControlGraph().graphTimeControlNode.getFullBounds().getWidth();
                    double maxAdditionalControlWidth = minimizableControlGraph.getControlGraph().additionalControls.getFullBounds().getWidth();
                    return Math.max( maxAdditionalControlWidth, maxControlNodeWidth );
//                    return maxControlNodeWidth;
                }
            };
            if ( getNumberMaximized() == 0 ) {
                return;
            }
            jFreeChartSliderNode.setOffset( max( getValues( controlNodeMaxX ) ) + dx, 0 );

            //compact the jfreechart node in the x direction by distance from optimal.

            LayoutFunction chartInset = new LayoutFunction() {
                public double getValue( MinimizableControlGraph minimizableControlGraph ) {
                    return getInsetX( minimizableControlGraph.getControlGraph().getJFreeChartNode() );
                }
            };
            double maxInset = max( getValues( chartInset ) );
//            System.out.println( "maxInset = " + maxInset );
            //todo: this layout code looks like it depends on layout getting called twice for each graph
            double diff = maxInset - getInsetX( getJFreeChartNode() );

            //putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            dynamicJFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - jFreeChartSliderNode.getFullBounds().getMaxX() - diff, getBounds().getHeight() );
            dynamicJFreeChartNode.setOffset( jFreeChartSliderNode.getFullBounds().getMaxX() + diff, 0 );
            dynamicJFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( dynamicJFreeChartNode.getFullBounds().getMaxX(), dynamicJFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = dynamicJFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + dynamicJFreeChartNode.getOffset().getX(), d.getY() + dynamicJFreeChartNode.getOffset().getY() );
        }

        private int getNumberMaximized() {
            int count = 0;
            for ( int i = 0; i < minimizableControlGraphs.length; i++ ) {
                MinimizableControlGraph minimizableControlGraph = minimizableControlGraphs[i];
                if ( !minimizableControlGraph.isMinimized() ) {
                    count++;
                }
            }
            return count;
        }

        private double max( double[] values ) {
            double max = values[0];
            for ( int i = 1; i < values.length; i++ ) {
                if ( values[i] > max ) {
                    max = values[i];
                }
            }
            return max;
        }
    }

    private static double getInsetX( JFreeChartNode jFreeChartNode ) {
        Rectangle2D bounds = jFreeChartNode.getBounds();
        Rectangle2D dataBounds = jFreeChartNode.getDataArea();
        return dataBounds.getX() - bounds.getX();
    }

    public void setLayout( Layout layout ) {
        this.layout = layout;
        relayout();
    }

    public void relayout() {
        layout.layout();
    }

    private Rectangle2D.Double getDataArea() {
        double xMin = dynamicJFreeChartNode.getChart().getXYPlot().getDomainAxis().getLowerBound();
        double xMax = dynamicJFreeChartNode.getChart().getXYPlot().getDomainAxis().getUpperBound();
        double yMin = dynamicJFreeChartNode.getChart().getXYPlot().getRangeAxis().getLowerBound();
        double yMax = dynamicJFreeChartNode.getChart().getXYPlot().getRangeAxis().getUpperBound();
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal( xMin, yMin, xMax, yMax );
        return r;
    }

    public void clear() {
        dynamicJFreeChartNode.clear();
    }

    public void addValue( double time, double value ) {
        addValue( 0, time, value );
    }

    public void addValue( int series, double time, double value ) {
        //scrolling test
//        if ( time > maxDomainValue / 2 ) {
//            //scroll to put maxDomain time in the middle
//            jFreeChart.getXYPlot().getDomainAxis().setRange( time - maxDomainValue / 2, time + maxDomainValue / 2 );
//            forceUpdateAll();
//        }

        //Throw away data that is outside of the max allowed domain
        if ( domainContains( time ) ) {
            dynamicJFreeChartNode.addValue( series, time, value );
        }
//        System.out.println( "series = " + series + " time=" + time + ", value=" + value );
    }

    private boolean domainContains( double time ) {
        return time >= 0 && time <= maxDomainValue;
    }

    public void setEditable( boolean editable ) {
        jFreeChartSliderNode.setVisible( editable );
        jFreeChartSliderNode.setPickable( editable );
        jFreeChartSliderNode.setChildrenPickable( editable );

        graphTimeControlNode.setEditable( editable );
    }

    public static interface Listener {

        //This method is called when the user makes an input event that indicates
        //that this component should be "in control" of the simulation
        void controlFocusGrabbed();

        void zoomChanged();

        void valueChanged( double value );
    }

    public static class Adapter implements Listener {
        public void controlFocusGrabbed() {
        }

        public void zoomChanged() {
        }

        public void valueChanged( double value ) {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void forceUpdateAll() {
        dynamicJFreeChartNode.forceUpdateAll();
    }

    public void setSliderSelected( boolean selected ) {
        jFreeChartSliderNode.setSelected( selected );
    }

    public void rebuildSeries() {
        getDynamicJFreeChartNode().clear();
        for ( int i = 0; i < series.size(); i++ ) {
            ControlGraphSeries controlGraphSeries = (ControlGraphSeries) series.get( i );
            for ( int k = 0; k < controlGraphSeries.getTemporalVariable().getSampleCount(); k++ ) {
                handleDataAdded( i, controlGraphSeries.getTemporalVariable().getData( k ) );
            }
        }
    }
}
