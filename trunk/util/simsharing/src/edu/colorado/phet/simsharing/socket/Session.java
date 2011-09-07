// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.SerializableBufferedImage;
import edu.colorado.phet.common.phetcommon.simsharing.SimsharingApplicationState;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StudentSummary;

/**
 * @author Sam Reid
 */
public class Session<T extends SimsharingApplicationState> {
    private long startTime;
    private Option<Long> endTime = new Option.None<Long>();
    private final ArrayList<T> entries = new ArrayList();
    private final SessionID sessionID;

    public Session( SessionID sessionID ) {
        this.sessionID = sessionID;
        startTime = System.currentTimeMillis();
    }

    public void endSession() {
        endTime = new Option.Some<Long>( System.currentTimeMillis() );
    }

    public StudentSummary getStudentSummary() {
        SerializableBufferedImage image = entries.size() == 0 ? new SerializableBufferedImage( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ) ) : entries.get( entries.size() - 1 ).getImage();
        long time = entries.size() == 0 ? -1 : System.currentTimeMillis() - entries.get( entries.size() - 1 ).getTime();
        return new StudentSummary( sessionID, image, System.currentTimeMillis() - startTime, time, entries.size() );
    }

    public void addSamples( AddSamples<T> sampleSet ) {
        for ( T sample : sampleSet.getData() ) {
            entries.add( sample );
        }
    }

    public T getSample( int index ) {

        //Handle flag for request for latest data point
        if ( index == -1 ) {
            return entries.get( entries.size() - 1 );
        }
        else {
            return entries.get( index );
        }
    }

    public boolean isActive() {
        return endTime.isNone();
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public long getStartTime() {
        return startTime;
    }
}