// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IMessageType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.DELIMITER;

/**
 * Sim-sharing message.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class SimSharingMessage<T, U> {

    enum MessageType implements IMessageType {user, system, model}

    public final IMessageType messageType;
    public final T component;
    public final U action;
    public final ParameterSet parameters;
    public final long time = System.currentTimeMillis();

    public SimSharingMessage( IMessageType messageType, T component, U action, final ParameterSet parameters ) {
        this.component = component;
        this.action = action;
        this.parameters = parameters;
        this.messageType = messageType;
    }

    public String toString() {
        return time + DELIMITER + messageType + DELIMITER + component + DELIMITER + action + DELIMITER + parameters.toString( DELIMITER );
    }
}