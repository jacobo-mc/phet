package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

public interface ParticleMoveListener {
    public void particleMoved( Battery source, Particle p );
}
