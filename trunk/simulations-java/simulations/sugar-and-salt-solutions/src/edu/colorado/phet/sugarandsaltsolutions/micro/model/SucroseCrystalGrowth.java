// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for sucrose crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating sucrose crystals
 *
 * @author Sam Reid
 */
public class SucroseCrystalGrowth extends IncrementalGrowth<Sucrose, SucroseCrystal> {
    public SucroseCrystalGrowth( MicroModel model, ItemList<SucroseCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected SucroseCrystal toCrystal( Sucrose particle ) {
        return new SucroseCrystal( particle.getPosition(), randomAngle() );
    }

    protected Option<?> selectSeed() {
        return model.freeParticles.selectRandom( Sucrose.class );
    }
}