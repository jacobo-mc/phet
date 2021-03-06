// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Base class for energy converters, i.e. model elements that take energy from
 * a source and convert it to something else (such as mechanical to electrical)
 * and then supply it to an energy user.
 *
 * @author John Blanco
 */
public abstract class EnergyConverter extends EnergySystemElement {

    protected final List<EnergyChunk> incomingEnergyChunks = new ArrayList<EnergyChunk>();
    protected final List<EnergyChunk> outgoingEnergyChunks = new ArrayList<EnergyChunk>();

    protected EnergyConverter( Image iconImage ) {
        super( iconImage );
    }

    public abstract Energy stepInTime( double dt, Energy incomingEnergy );

    /**
     * Set up the energy chunks contained by this energy source as though they
     * have been full propagated through the system.
     *
     * @param incomingEnergyRate Incoming energy in joules/sec.
     */
    public abstract void preLoadEnergyChunks( Energy incomingEnergyRate );

    /**
     * Get the energy chunks that this source wants to transfer to the next
     * energy system element.  Reading clears the list.
     *
     * @return list of energy chunks to transfer.
     */
    public List<EnergyChunk> extractOutgoingEnergyChunks() {
        List<EnergyChunk> retVal = new ArrayList<EnergyChunk>( outgoingEnergyChunks );
        energyChunkList.removeAll( outgoingEnergyChunks );
        outgoingEnergyChunks.clear();
        return retVal;
    }

    /**
     * Inject a list of energy chunks into this energy system element.  Once
     * injected, it is the system's responsibility to move, convert, and
     * otherwise manage them.
     *
     * @param energyChunks List of energy chunks to inject.
     */
    public void injectEnergyChunks( List<EnergyChunk> energyChunks ) {
        incomingEnergyChunks.addAll( energyChunks );
    }

    /**
     * Get the amount and type of energy currently being produced.
     *
     * @return amount and type of energy.
     */
    public abstract Energy getEnergyOutputRate();

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        incomingEnergyChunks.clear();
        outgoingEnergyChunks.clear();
    }
}
