// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ConductivityTester;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroSugarDispenser;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;

/**
 * Introductory (macro) model that keeps track of moles of solute dissolved in the liquid.
 *
 * @author Sam Reid
 */
public class MacroModel extends SugarAndSaltSolutionModel {
    //Model for the conductivity tester which is in the macro tab but not other tabs
    public final ConductivityTester conductivityTester;

    //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    private final ObservableProperty<Boolean> anySolutes = salt.moles.greaterThan( 0 ).or( sugar.moles.greaterThan( 0 ) );

    public MacroModel() {
        super( new ConstantDtClock( 30 ), new BeakerDimension( 0.2 ), 0.0005,

               //These values were sampled from the model with debug mode.
               0.011746031746031754, 0.026349206349206344,

               //In macro model scales are already tuned so no additional scaling is needed
               1 );

        //Properties to indicate if the user is allowed to add more of the solute.  If not allowed the dispenser is shown as empty.
        ObservableProperty<Boolean> moreSaltAllowed = salt.grams.plus( airborneSaltGrams ).lessThan( 100 );
        ObservableProperty<Boolean> moreSugarAllowed = sugar.grams.plus( airborneSugarGrams ).lessThan( 100 );

        //Add models for the various dispensers: sugar, salt, etc.
        dispensers.add( new MacroSaltShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSaltAllowed, Strings.SALT, distanceScale, dispenserType, SALT, this ) );
        dispensers.add( new MacroSugarDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSugarAllowed, Strings.SUGAR, distanceScale, dispenserType, SUGAR, this ) );

        //Model for the conductivity tester
        conductivityTester = new ConductivityTester( beaker.getWidth(), beaker.getHeight() );

        //When the conductivity tester probe locations change, also update the conductivity tester brightness since they may come into contact (or leave contact) with the fluid
        conductivityTester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {
            public void brightnessChanged() {
            }

            public void positiveProbeLocationChanged() {
                updateConductivityTesterBrightness();
            }

            public void negativeProbeLocationChanged() {
                updateConductivityTesterBrightness();
            }

            public void locationChanged() {
                //Have to callback here too since the battery or bulb could get sumberged and short the circuit
                updateConductivityTesterBrightness();
            }
        } );

        //Update the conductivity tester when the water level changes, since it might move up to touch a probe (or move out from underneath a submerged probe)
        new RichSimpleObserver() {
            @Override public void update() {
                updateConductivityTesterBrightness();
            }
        }.observe( saltConcentration, solution.shape, outputWater );
    }

    //Determine if a conductivity tester probe is touching water in the beaker, or water flowing out of the beaker (which would have the same concentration as the water in the beaker)
    private boolean isProbeTouchingWaterThatMightHaveSalt( ImmutableRectangle2D region ) {
        Rectangle2D waterBounds = solution.shape.get().getBounds2D();

        final Rectangle2D regionBounds = region.toRectangle2D();
        return waterBounds.intersects( region.toRectangle2D() ) || outputWater.get().getBounds2D().intersects( regionBounds );
    }

    //Update the conductivity tester brightness when the probes come into contact with (or stop contacting) the fluid
    protected void updateConductivityTesterBrightness() {

        //Check for a collision with the probe, using the full region of each probe (so if any part intersects, there is still an electrical connection).
        Rectangle2D waterBounds = solution.shape.get().getBounds2D();

        //See if both probes are touching water that might have salt in it
        boolean bothProbesTouching = isProbeTouchingWaterThatMightHaveSalt( conductivityTester.getPositiveProbeRegion() ) && isProbeTouchingWaterThatMightHaveSalt( conductivityTester.getNegativeProbeRegion() );

        //Check to see if the circuit is shorted out (if light bulb or battery is submerged).
        //Null checks are necessary since those regions are computed from view components and may not have been computed yet (but will be non-null if the user dragged out the conductivity tester from the toolbox)
        boolean batterySubmerged = conductivityTester.getBatteryRegion() != null && waterBounds.intersects( conductivityTester.getBatteryRegion().getBounds2D() );
        boolean bulbSubmerged = conductivityTester.getBulbRegion() != null && waterBounds.intersects( conductivityTester.getBulbRegion().getBounds2D() );

        //The circuit should short out if the battery or bulb is submerged
        boolean shortCircuited = batterySubmerged || bulbSubmerged;

        //Set the brightness to be a linear function of the salt concentration (but keeping it bounded between 0 and 1 which are the limits of the conductivity tester brightness
        //Use a scale factor that matches up with the limits on saturation (manually sampled at runtime)
        conductivityTester.brightness.set( bothProbesTouching && !shortCircuited ? MathUtil.clamp( 0, saltConcentration.get() * 1.62E-4, 1 ) : 0.0 );
        conductivityTester.shortCircuited.set( shortCircuited );
    }

    @Override public ObservableProperty<Boolean> getAnySolutes() {
        return anySolutes;
    }

    @Override public void reset() {
        super.reset();
        conductivityTester.reset();
    }
}