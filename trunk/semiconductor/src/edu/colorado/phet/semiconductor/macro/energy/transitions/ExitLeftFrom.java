/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.transitions;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.StateTransition;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitLeftState;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 8:27:41 AM
 * Copyright (c) Mar 17, 2004 by Sam Reid
 */
public class ExitLeftFrom extends StateTransition {
    EnergyCell from;

    public ExitLeftFrom( EnergyCell from ) {
        this.from = from;
    }

    public BandParticleState getState( BandParticle particle, EnergySection energySection ) {
        EnergyCell cell = particle.getEnergyCell();
        if( cell == null ) {
            return null;
        }
        EnergyCell left = energySection.getNeighbor( cell, 0, -1 );
        if( cell != null && left == null && cell.getIndex() == 0 && particle.isLocatedAtCell() &&
            particle.isExcited() && cell == from ) {
            double targetX = energySection.getLeftBand().getX();
            double targetY = particle.getY();
            PhetVector dest = new PhetVector( targetX, targetY );
            ExitLeftState sc = new ExitLeftState( dest, energySection.getSpeed(), energySection );
            particle.setState( sc );
        }
        return null;
    }
}
