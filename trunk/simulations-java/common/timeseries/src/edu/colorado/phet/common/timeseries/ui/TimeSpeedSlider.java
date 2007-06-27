package edu.colorado.phet.common.timeseries.ui;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.timeseries.model.TimeModelClock;

import javax.swing.*;
import java.util.Hashtable;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 2:27:44 PM
 */
public class TimeSpeedSlider extends LinearValueControl {
    private TimeModelClock energySkateParkClock;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final TimeModelClock defaultClock ) {
        super( min, max, "", textFieldPattern, "" );
        this.energySkateParkClock = defaultClock;
        setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new JLabel( TimeseriesResources.getString( "time.slow" ) ) );
        table.put( new Double( max ), new JLabel( TimeseriesResources.getString( "time.normal" ) ) );
        setTickLabels( table );
        setValue( max );
        defaultClock.addListener( new TimeModelClock.Listener() {
            public void changed() {
                update( defaultClock );
            }
        } );
        update( defaultClock );
    }

    private void update( TimeModelClock defaultClock ) {
        setValue( defaultClock.getTimingStrategy().getSimulationTimeChangeForPausedClock() );
    }
}
