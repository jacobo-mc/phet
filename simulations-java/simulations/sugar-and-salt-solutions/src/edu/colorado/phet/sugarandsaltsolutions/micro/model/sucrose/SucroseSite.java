package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.*;

/**
 * A way to grow a sugar crystal.
 *
 * @author Sam Reid
 */
public class SucroseSite extends OpenSite<SucroseLattice> {
    public SucroseSite( Component component, BondType type ) {
        super( component, type );
    }

    @Override public SucroseLattice grow( SucroseLattice lattice ) {
        Component newComponent = new SucroseComponent();
        return new SucroseLattice( new ImmutableList<Component>( lattice.components, newComponent ), new ImmutableList<Bond>( lattice.bonds, new Bond( component, newComponent, type ) ) );
    }
}
