package edu.colorado.phet.common.phetcommon.statistics;

import java.io.IOException;

/**
 * IStatisticsService is the interface implemented by all statistics services.
 * A statistics service delivers a statistics message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IStatisticsService {

    /**
     * Delivers a statistics message to PhET.
     * @param message
     */
    public void postMessage( StatisticsMessage message ) throws IOException;
}
