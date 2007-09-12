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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Demonstration of usage and behavior of JFreeChartNode
 *
 * @author Sam Reid
 */
public class TestDynamicJFreeChartNode {
    private JFrame frame;
    private Timer timer;
    private double t0 = System.currentTimeMillis();
    private PhetPCanvas phetPCanvas;
    private DynamicJFreeChartNode dynamicJFreeChartNode;
    private PSwing pSwing;

    public TestDynamicJFreeChartNode() {
        frame = new JFrame();
        frame.setSize( 1280, 768 - 100 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );

        JFreeChart chart = ChartFactory.createXYLineChart( "title", "x", "y", new XYSeriesCollection(), PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( phetPCanvas, chart );
        dynamicJFreeChartNode.addSeries( "sine", Color.blue );
        chart.getXYPlot().getRangeAxis().setAutoRange( false );
        chart.getXYPlot().getRangeAxis().setRange( -1, 1 );
        chart.getXYPlot().getDomainAxis().setAutoRange( false );
        chart.getXYPlot().getDomainAxis().setRange( 0, 1 );

        phetPCanvas.addScreenChild( dynamicJFreeChartNode );

        JPanel controlPanel = new JPanel();
        JPanel dynamicJFreeChartNodeControlPanel = new DynamicJFreeChartNodeControlPanel( dynamicJFreeChartNode );
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clear();
            }
        } );
        controlPanel.add( dynamicJFreeChartNodeControlPanel );
        controlPanel.add( clear );
//        panel.add( clear );
        pSwing = new PSwing( controlPanel );
        phetPCanvas.addScreenChild( pSwing );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateGraph();
            }
        } );
        phetPCanvas.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_F1 && e.isAltDown() ) {
                    PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
                }
            }
        } );
        phetPCanvas.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                phetPCanvas.requestFocus();
            }
        } );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    protected void updateGraph() {
//        double t = ( System.currentTimeMillis() - t0 ) / 1000.0;
        double t = ( System.currentTimeMillis() - t0 ) / 500.0;
        double frequency = 1.0 / 10.0;
        double y = Math.sin( t * 2 * Math.PI * frequency );
//        double y = 0;
        Point2D.Double pt = new Point2D.Double( t / 100.0, y );
        dynamicJFreeChartNode.addValue( pt.getX(), pt.getY() );

//        dynamicJFreeChartNode.updateChartRenderingInfo();
//        System.out.println( "dynamicJFreeChartNode.getDataArea( ) = " + dynamicJFreeChartNode.getDataArea() );
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public PSwing getPSwing() {
        return pSwing;
    }

    private void clear() {
        dynamicJFreeChartNode.clear();
        t0 = System.currentTimeMillis();
    }

    protected void relayout() {
        pSwing.setOffset( 0, 0 );
        dynamicJFreeChartNode.setBounds( 0, pSwing.getFullBounds().getHeight(), phetPCanvas.getWidth(), phetPCanvas.getHeight() - pSwing.getFullBounds().getHeight() );
    }

    public void start() {
        frame.setVisible( true );
        timer.start();
        phetPCanvas.requestFocus();
    }

    public double getInitialTime() {
        return t0;
    }

    public static void main( String[] args ) {
        new TestDynamicJFreeChartNode().start();
    }
}
