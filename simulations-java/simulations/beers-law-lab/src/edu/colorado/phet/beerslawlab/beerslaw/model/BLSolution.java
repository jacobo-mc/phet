// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltIINitrate;
import edu.colorado.phet.beerslawlab.common.model.Solute.CopperSulfate;
import edu.colorado.phet.beerslawlab.common.model.Solute.KoolAid;
import edu.colorado.phet.beerslawlab.common.model.Solute.NickelIIChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumChromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumDichromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumPermanganate;
import edu.colorado.phet.beerslawlab.concentration.model.Solution;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Solution model for the Beer's Law module.
 * Concentration is directly settable, solvent and solute are immutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLSolution {

    public final Property<Double> concentration;
    public final Solvent solvent;
    public final Solute solute;
    public final CompositeProperty<Color> fluidColor; // derived from solute color and concentration
    //TODO add CompositeProperty for absorbance and %transmission

    public BLSolution( final Solute solute ) {
        this.concentration = new Property<Double>( 0d );
        this.solvent = new Water();
        this.solute = solute;

         // derive the solution color
        this.fluidColor = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return Solution.createColor( solvent, solute, concentration.get() ); //TODO undesirable dependency on Solution here
            }
        }, this.concentration );
    }

    public static class KoolAidSolution extends BLSolution {
        public KoolAidSolution() {
            super( new KoolAid() );
        }
    }

    public static class CobaltIINitrateSolution extends BLSolution {
        public CobaltIINitrateSolution() {
            super( new CobaltIINitrate() );
        }
    }

    public static class CobaltChlorideSolution extends BLSolution {
        public CobaltChlorideSolution() {
            super( new CobaltChloride() );
        }
    }

    public static class PotassiumDichromateSolution extends BLSolution {
        public PotassiumDichromateSolution() {
            super( new PotassiumDichromate() );
        }
    }

    public static class PotassiumChromateSolution extends BLSolution {
        public PotassiumChromateSolution() {
            super( new PotassiumChromate() );
        }
    }

    public static class NickelIIChlorideSolution extends BLSolution {
        public NickelIIChlorideSolution() {
            super( new NickelIIChloride() );
        }
    }

    public static class CopperSulfateSolution extends BLSolution {
        public CopperSulfateSolution() {
            super( new CopperSulfate() );
        }
    }

    public static class PotassiumPermanganateSolution extends BLSolution {
        public PotassiumPermanganateSolution() {
            super( new PotassiumPermanganate() );
        }
    }
}
