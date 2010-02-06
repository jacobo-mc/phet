/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.util;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Player for JAR audio resources.
 * Can play sim-specific or phetcommon sounds, can be enabled and disabled.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AudioResourcePlayer {

    private final PhetResources simResourceLoader;
    private boolean enabled;

    public AudioResourcePlayer( PhetResources simResourceLoader, boolean enabled ) {
        this.simResourceLoader = simResourceLoader;
        this.enabled = enabled;
    }

    /**
     * Is sound enabled?
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables sound.
     * @param isEnabled
     */
    public void setEnabled( boolean isEnabled ) {
        this.enabled = isEnabled;
    }

    /**
     * Plays an audio resource using the sim-specific resource loader.
     * @param resourceName
     */
    public void playSimAudio( String resourceName ) {
        if ( isEnabled() ) {
            simResourceLoader.getAudioClip( resourceName ).play();
        }
    }
    
    /**
     * Plays an audio resource using phetcommon's resource loader.
     * @param resourceName
     */
    public void playCommonAudio( String resourceName ) {
        if ( isEnabled() ) {
            PhetCommonResources.getInstance().getAudioClip( resourceName ).play();
        }
    }
}
