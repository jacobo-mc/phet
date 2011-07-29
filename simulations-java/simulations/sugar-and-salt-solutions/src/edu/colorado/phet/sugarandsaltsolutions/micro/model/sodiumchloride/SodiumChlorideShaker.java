// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This salt shaker adds salt (NaCl) crystals to the model when shaken
 *
 * @author Sam Reid
 */
public class SodiumChlorideShaker extends MicroShaker {

    public SodiumChlorideShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    //Create a random salt crystal and add it to the model
    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {
        SodiumChlorideCrystal crystal = new SodiumChlorideCrystal( outputPoint, randomAngle() );
        for ( int i = 0; i < 10; i++ ) {
            crystal.grow( model );
        }
        System.out.println( "crystal.numberConstituents() = " + crystal.numberConstituents() );
        model.addSaltCrystal( crystal );
    }
}