package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.batteryvoltage.ParticleLocation;
import edu.colorado.phet.common.batteryvoltage.electron.man.Motion;
import edu.colorado.phet.common.batteryvoltage.electron.man.motions.Location;
import edu.colorado.phet.common.batteryvoltage.electron.man.motions.TranslateToLocation;
import edu.colorado.phet.common.batteryvoltage.phys2d.DoublePoint;
import edu.colorado.phet.common.batteryvoltage.phys2d.PropagatingParticle;

public class Get extends DefaultAction implements Location {
    TranslateToLocation ttl;
    PropagatingParticle target;

    public Get( Motion m, TranslateToLocation ttl ) {
        super( m );
        this.ttl = ttl;
    }

    public DoublePoint getPosition() {
        return target.getPosition();
    }

    public PropagatingParticle getTarget() {
        return target;
    }

    public void setTarget( PropagatingParticle t ) {
        if( t == null ) {
            throw new RuntimeException( "Set null target." );
        }
        this.target = t;
        ttl.setLocation( new ParticleLocation( t ) );
    }
}

