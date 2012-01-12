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

    private final IMessageType messageType;
    private final T object;
    private final U action;
    private final ParameterSet parameters;

    public SimSharingMessage( IMessageType messageType, T object, U action, final ParameterSet parameters ) {
        this.object = object;
        this.action = action;
        this.parameters = parameters;
        this.messageType = messageType;
    }

    public String toString() {
        return messageType + DELIMITER + object + DELIMITER + action + DELIMITER + parameters.toString( DELIMITER );
    }
}