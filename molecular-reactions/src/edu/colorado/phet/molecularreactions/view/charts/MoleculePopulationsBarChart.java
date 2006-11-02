/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.util.BarChart;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.view.MoleculePaints;
import org.jfree.chart.plot.PlotOrientation;

import javax.swing.*;
import java.awt.*;

/**
 * MoleculePopulationsStripChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculePopulationsBarChart extends BarChart {
    static String title = SimStrings.get( "StripChart.title" );
    static String[] seriesNames = new String[]{"A", "BC", "AB", "C"};
    static String xAxisLabel = "";
    static String yAxisLabel = SimStrings.get( "StripChart.num" );
    static PlotOrientation orienation = PlotOrientation.VERTICAL;
    private MoleculeCounter counterA;
    private MoleculeCounter counterAB;
    private MoleculeCounter counterBC;
    private MoleculeCounter counterC;
    private double updateInterval;
    private double timeSinceLastUpdate;

    /**
     * @param model
     * @param clock
     * @param minY
     * @param maxY
     * @param updateInterval
     */
    public MoleculePopulationsBarChart( MRModel model, IClock clock, int minY, int maxY,
                                        double updateInterval ) {
        super( title, seriesNames, xAxisLabel, yAxisLabel, orienation, minY, maxY, false );

        this.updateInterval = updateInterval;

        // Create counters for each of the molecule types
        counterA = new MoleculeCounter( MoleculeA.class, model );
        counterAB = new MoleculeCounter( MoleculeAB.class, model );
        counterBC = new MoleculeCounter( MoleculeBC.class, model );
        counterC = new MoleculeCounter( MoleculeC.class, model );

        // Set graphic attributes
        setSeriesPaint( 0, MoleculePaints.getPaint( MoleculeA.class ) );
        setSeriesPaint( 1, MoleculePaints.getPaint( MoleculeBC.class ) );
        setSeriesPaint( 2, MoleculePaints.getPaint( MoleculeAB.class ) );
        setSeriesPaint( 3, MoleculePaints.getPaint( MoleculeC.class ) );

        getChart().setBackgroundPaint( MRConfig.MOLECULE_PANE_BACKGROUND );
        getChart().setBorderStroke( new BasicStroke( 1 ) );
        getChart().setBorderPaint( Color.black );
        getChart().getTitle().setFont( MRConfig.CHART_TITLE_FONT );

        // Hook up to the clock
        clock.addClockListener( new Updater() );
        updateChart();
    }

    private void updateChart(){
        addData( counterA.getCnt(), seriesNames[0], "" );
        addData( counterBC.getCnt(), seriesNames[1], "" );
        addData( counterAB.getCnt(), seriesNames[2], "" );
        addData( counterC.getCnt(), seriesNames[3], "" );
    }

    /**
     * Updates the strip chart at specified intervals
     */
    private class Updater extends ClockAdapter {

        public void clockTicked( ClockEvent clockEvent ) {
            timeSinceLastUpdate += clockEvent.getSimulationTimeChange();
            if( timeSinceLastUpdate >= updateInterval ) {
                timeSinceLastUpdate = 0;
                updateChart();
            }
        }
    }
}
