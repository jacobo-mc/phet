package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.electron.man.Man;
import edu.colorado.phet.phys2d.Particle;
import edu.colorado.phet.phys2d.Propagator;

public class CarryPropagator implements Propagator {
    Man m;

    public CarryPropagator( Man m ) {
        this.m = m;
    }

    public void propagate( double dt, Particle p ) {
        p.setPosition( m.getNeck().getPosition() );
    }
}
