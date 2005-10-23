/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.plots.*;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 3:12:06 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class ChartCanvas extends PhetPCanvas {
    private EC3Module ec3Module;
    private ArrayList units = new ArrayList();
    private TimePlotSuitePNode plot;
    private TimeSeriesPNode keSeries;
    private TimeSeriesPNode peSeries;

    public ChartCanvas( final EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        plot = new TimePlotSuitePNode( this,
                                       new Range2D( 0, -500000, 40, 500000 ), "Name",
                                       "units", ec3Module.getTimeSeriesModel(), 250, false );
        addScreenChild( plot );

        keSeries = new TimeSeriesPNode( plot, new ValueAccessor( "KE", "KE", "Joules", "J", Color.red, "Kinetic Energy" ) {
            public double getValue( Object model ) {
                if( ec3Module.getEnergyConservationModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergyConservationModel().bodyAt( 0 );
                    return body.getKineticEnergy();
                }
                else {
                    return 0;
                }
            }
        }, Color.red, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( keSeries );
        units.add( new DataUnit( keSeries ) );

        peSeries = new TimeSeriesPNode( plot, new ValueAccessor( "PE", "PE", "Joules", "J", Color.blue, "Potential Energy" ) {
            public double getValue( Object model ) {
                if( ec3Module.getEnergyConservationModel().numBodies() > 0 ) {
                    Body body = ec3Module.getEnergyConservationModel().bodyAt( 0 );
                    return ec3Module.getEnergyConservationModel().getPotentialEnergy( body );
                }
                else {
                    return 0;
                }
            }
        }, Color.blue, "", ec3Module.getTimeSeriesModel() );
        plot.addTimeSeries( peSeries );
        units.add( new DataUnit( peSeries ) );

        ec3Module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                if( ec3Module.getTimeSeriesModel().isRecording() ) {
                    for( int i = 0; i < units.size(); i++ ) {
                        DataUnit dataUnit = (DataUnit)units.get( i );
                        dataUnit.updatePlot( ec3Module.getEnergyConservationModel(), ec3Module.getTimeSeriesModel().getRecordTime() );
                    }
                }
            }
        } );
    }

    public void reset() {
        plot.reset();
        for( int i = 0; i < units.size(); i++ ) {
            DataUnit dataUnit = (DataUnit)units.get( i );
            dataUnit.reset();
        }
    }
}
