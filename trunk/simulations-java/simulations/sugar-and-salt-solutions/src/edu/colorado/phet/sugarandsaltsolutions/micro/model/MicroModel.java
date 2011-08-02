// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Oxygen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.Ethanol;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.EthanolDropper;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.SucroseDispenser;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.molesPerLiterToMolesPerMeterCubed;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.ParticleCountTable.*;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NEUTRAL_COLOR;
import static java.awt.Color.blue;
import static java.awt.Color.red;
import static java.lang.Math.PI;
import static java.lang.Math.random;
import static java.util.Collections.sort;

/**
 * Model for the micro tab, which uses code from soluble salts sim.
 *
 * @author Sam Reid
 */
public class MicroModel extends SugarAndSaltSolutionModel {

    private static final double framesPerSecond = 30;

    //List of all spherical particles
    public final ItemList<SphericalParticle> sphericalParticles = new ItemList<SphericalParticle>();

    //List of all free particles, used to keep track of which particles (includes molecules) to move about randomly
    public final ItemList<Particle> freeParticles = new ItemList<Particle>();

    //List of all drained particles, used to keep track of which particles (includes molecules) should flow out of the output drain
    public final ItemList<Particle> drainedParticles = new ItemList<Particle>();

    //Lists of compounds
    public final ItemList<SodiumChlorideCrystal> sodiumChlorideCrystals = new ItemList<SodiumChlorideCrystal>() {{
        size.trace( "sodium chloride crystals" );
    }};
    public final ItemList<SodiumNitrateCrystal> sodiumNitrateCrystals = new ItemList<SodiumNitrateCrystal>();
    public final ItemList<CalciumChlorideCrystal> calciumChlorideCrystals = new ItemList<CalciumChlorideCrystal>();
    public final ItemList<SucroseCrystal> sucroseCrystals = new ItemList<SucroseCrystal>();

    //The factor by which to scale particle sizes, so they look a bit smaller in the graphics
    public static final double sizeScale = 0.35;

    //User setting for whether color should be based on charge or identity
    public final BooleanProperty showChargeColor = new BooleanProperty( false );

    //Settable property that indicates whether the clock is running or paused
    public final Property<Boolean> clockRunning = new Property<Boolean>( true );

    //The index of the kit selected by the user
    public final Property<Integer> selectedKit = new Property<Integer>( 0 ) {{

        //When the user switches kits, clear the solutes and reset the water level
        addObserver( new SimpleObserver() {
            public void update() {
                clearSolutes();
                resetWater();
            }
        } );
    }};

    //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    private final ObservableProperty<Boolean> anySolutes = freeParticles.size.greaterThan( 0 );

    //Debugging flag for draining particles through the faucet
    private boolean debugDraining = false;

    //Colors for all the dissolved components timexr
    public final ObservableProperty<Color> sodiumColor = new IonColor( this, new Sodium() );
    public final ObservableProperty<Color> chlorideColor = new IonColor( this, new Chloride() );
    public final ObservableProperty<Color> calciumColor = new IonColor( this, new Calcium() );
    public final ObservableProperty<Color> sucroseColor = new CompositeProperty<Color>( new Function0<Color>() {
        public Color apply() {
            return showChargeColor.get() ? NEUTRAL_COLOR : red;
        }
    }, showChargeColor );
    public final ObservableProperty<Color> nitrateColor = new CompositeProperty<Color>( new Function0<Color>() {
        public Color apply() {
            return showChargeColor.get() ? NEUTRAL_COLOR : blue;
        }
    }, showChargeColor );
    public final ObservableProperty<Color> ethanolColor = new CompositeProperty<Color>( new Function0<Color>() {
        public Color apply() {
            return showChargeColor.get() ? NEUTRAL_COLOR : Color.pink;
        }
    }, showChargeColor );

    //Particle concentrations for all of the dissolved components
    public final CompositeDoubleProperty sodiumConcentration = new IonConcentration( this, Sodium.class );
    public final CompositeDoubleProperty chlorideConcentration = new IonConcentration( this, Chloride.class );
    public final CompositeDoubleProperty calciumConcentration = new IonConcentration( this, Calcium.class );
    public final CompositeDoubleProperty sucroseConcentration = new IonConcentration( this, Sucrose.class );
    public final CompositeDoubleProperty ethanolConcentration = new IonConcentration( this, Ethanol.class );
    public final CompositeDoubleProperty nitrateConcentration = new IonConcentration( this, Nitrate.class );

    //Determine saturation points
    final double sodiumChlorideSaturationPoint = molesPerLiterToMolesPerMeterCubed( 6.14 );
    final double calciumChlorideSaturationPoint = molesPerLiterToMolesPerMeterCubed( 6.71 );
    final double sodiumNitrateSaturationPoint = molesPerLiterToMolesPerMeterCubed( 10.8 );
    final double sucroseSaturationPoint = molesPerLiterToMolesPerMeterCubed( 5.84 );

    //Create observable properties that indicate whether each solution type is saturated
    final ObservableProperty<Boolean> sodiumChlorideSaturated = sodiumConcentration.greaterThan( sodiumChlorideSaturationPoint ).and( chlorideConcentration.greaterThan( sodiumChlorideSaturationPoint ) );
    final ObservableProperty<Boolean> calciumChlorideSaturated = calciumConcentration.greaterThan( calciumChlorideSaturationPoint ).and( chlorideConcentration.greaterThan( calciumChlorideSaturationPoint * 2 ) );
    final ObservableProperty<Boolean> sucroseSaturated = sucroseConcentration.greaterThan( sucroseSaturationPoint );
    final ObservableProperty<Boolean> sodiumNitrateSaturated = sodiumConcentration.greaterThan( sodiumNitrateSaturationPoint ).and( nitrateConcentration.greaterThan( sodiumNitrateSaturationPoint ) );

    //Listeners that are notified when the simulation time step has completed
    public final ArrayList<VoidFunction0> stepFinishedListeners = new ArrayList<VoidFunction0>();

    //DrainData helps to maintain a constant concentration as particles flow out the drain by tracking flow rate and timing
    //There is one for each type since they may flow at different rates and have different schedules
    public final DrainData sodiumDrainData = new DrainData( Sodium.class );
    public final DrainData chlorideDrainData = new DrainData( Chloride.class );
    public final DrainData sucroseDrainData = new DrainData( Sucrose.class );
    public final DrainData nitrateDrainData = new DrainData( Nitrate.class );
    public final DrainData calciumDrainData = new DrainData( Calcium.class );
    public final DrainData ethanolDrainData = new DrainData( Ethanol.class );

    public MicroModel() {
        //SolubleSalts clock runs much faster than wall time
        super( new ConstantDtClock( framesPerSecond ),

               //The volume of the micro beaker should be 2E-23L
               //In the macro tab, the dimension is BeakerDimension( width = 0.2, height = 0.1, depth = 0.1 ), each unit in meters
               //So if it is to have the same shape is as the previous tab then we use
               // width*height*depth = 2E-23
               // and
               // width = 2*height = 2*depth
               //Solving for width, we have:
               // 2E-23 = width * width/2 * width/2
               // =>
               // 8E-23 = width^3.  Therefore
               // width = cube root(8E-23)
               new BeakerDimension( Math.pow( 8E-23
                                              //convert L to meters cubed
                                              * 0.001, 1 / 3.0 ) ),

               //Flow rate must be slowed since the beaker is so small.  TODO: compute this factor analytically so that it will match the first tab perfectly?  Factor out numbers?
               0.0005 * 2E-23 / 2,

               //Values sampled at runtime using a debugger using this line in SugarAndSaltSolutionModel.update: System.out.println( "solution.shape.get().getBounds2D().getMaxY() = " + solution.shape.get().getBounds2D().getMaxY() );
               2.5440282964793075E-10, 5.75234062238494E-10,

               //Ratio of length scales in meters
               1.0 / Math.pow( 8E-23 * 0.001, 1 / 3.0 ) / 0.2 );

        //Property that identifies the number of sucrose molecules in crystal form, for making sure the user doesn't exceed the allowed maximum
        //Should be rewritten with Property<Integer> but there is currently no good compositional support for it (using plus(), greaterThan(), etc)
        final Property<Double> numSucroseMoleculesInCrystal = new Property<Double>( 0.0 ) {{
            VoidFunction1<SucroseCrystal> updateSucroseCount = new VoidFunction1<SucroseCrystal>() {
                public void apply( SucroseCrystal sucroseCrystal ) {
                    int count = 0;
                    for ( SucroseCrystal crystal : sucroseCrystals ) {
                        count = count + crystal.numberConstituents();
                    }
                    set( count + 0.0 );
                }
            };
            sucroseCrystals.addItemAddedListener( updateSucroseCount );
            sucroseCrystals.addItemRemovedListener( updateSucroseCount );
        }};

        //Determine whether the user is allowed to add more of each type, based on the particle table
        //These computations make the simplifying assumption that only certain combinations of molecules will appear together
        //This allows us to say, for example, that more NaNO3 may be added if Oxygen is not over the limit, adding another molecule to its kit that contains oxygen would cause this to give incorrect limiting behavior
        //TODO: For sucrose, account for non-dissolved crystals.  Otherwise the user can go over the limit since falling crystals aren't counted
        //For ethanol, okay to count free particles since they are free (non-crystal) when emitted from the dropper
        ObservableProperty<Boolean> moreSodiumChlorideAllowed = sphericalParticles.propertyCount( Sodium.class ).lessThan( MAX_SODIUM_CHLORIDE ).or( sphericalParticles.propertyCount( Chloride.class ).lessThan( MAX_SODIUM_CHLORIDE ) );
        ObservableProperty<Boolean> moreCalciumChlorideAllowed = sphericalParticles.propertyCount( Calcium.class ).lessThan( MAX_CALCIUM_CHLORIDE ).or( sphericalParticles.propertyCount( Chloride.class ).lessThan( MAX_CALCIUM_CHLORIDE ) );
        ObservableProperty<Boolean> moreSodiumNitrateAllowed = sphericalParticles.propertyCount( Sodium.class ).lessThan( MAX_SODIUM_NITRATE ).or( sphericalParticles.propertyCount( Oxygen.class ).lessThan( MAX_SODIUM_NITRATE * 3 ) );
        ObservableProperty<Boolean> moreSucroseAllowed = ( freeParticles.propertyCount( Sucrose.class ).plus( numSucroseMoleculesInCrystal ) ).lessThan( MAX_SUCROSE );
        ObservableProperty<Boolean> moreEthanolAllowed = freeParticles.propertyCount( Ethanol.class ).lessThan( MAX_ETHANOL );

        //Add models for the various dispensers: sugar, salt, etc.
        dispensers.add( new SodiumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSodiumChlorideAllowed, SODIUM_CHLORIDE_NEW_LINE, distanceScale, dispenserType, SALT, this ) );
        dispensers.add( new SucroseDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSucroseAllowed, SUCROSE, distanceScale, dispenserType, SUGAR, this ) );
        dispensers.add( new SodiumNitrateShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSodiumNitrateAllowed, SODIUM_NITRATE_NEW_LINE, distanceScale, dispenserType, DispenserType.SODIUM_NITRATE, this ) );
        dispensers.add( new CalciumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreCalciumChlorideAllowed, CALCIUM_CHLORIDE_NEW_LINE, distanceScale, dispenserType, DispenserType.CALCIUM_CHLORIDE, this ) );
        dispensers.add( new EthanolDropper( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, 0, beaker, moreEthanolAllowed, Strings.ETHANOL, distanceScale, dispenserType, DispenserType.ETHANOL, this ) );

        //When the pause button is pressed, pause the clock
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean running ) {
                clock.setRunning( running );
            }
        } );

        //When the clock pauses or starts, update the property
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockPaused( ClockEvent clockEvent ) {
                clockRunning.set( false );
            }

            @Override public void clockStarted( ClockEvent clockEvent ) {
                clockRunning.set( true );
            }
        } );

        //When the output flow rate changes, recompute the desired flow rate for particles to try to attain a constant concentration over time for each solute type
        outputFlowRate.addObserver( new VoidFunction1<Double>() {
            public void apply( Double outputFlowRate ) {
                rescheduleDrainParticles( sodiumDrainData );
                rescheduleDrainParticles( chlorideDrainData );
                rescheduleDrainParticles( nitrateDrainData );
                rescheduleDrainParticles( ethanolDrainData );
                rescheduleDrainParticles( calciumDrainData );
                rescheduleDrainParticles( sucroseDrainData );
            }
        } );
    }

    //Look up the DrainData corresponding to the specified particle type, so that it may be referenced in the UpdateStrategy when the scheduling is performed
    public DrainData getDrainData( Particle particle ) {
        if ( particle instanceof Sodium ) { return sodiumDrainData; }
        if ( particle instanceof Chloride ) { return chlorideDrainData; }
        if ( particle instanceof Nitrate ) { return nitrateDrainData; }
        if ( particle instanceof Ethanol ) { return ethanolDrainData; }
        if ( particle instanceof Calcium ) { return calciumDrainData; }
        if ( particle instanceof Sucrose ) { return sucroseDrainData; }
        throw new RuntimeException( "unknown type: " + particle.getClass() );
    }

    public static class DrainData {

        //Record the time when particles were scheduled to leave the drain so it can be accounted for during propagation toward the drain.
        double drainFlowStartTime;

        double particlesDrainPerSecond;
        public final Class<? extends Particle> type;

        public DrainData( Class<? extends Particle> type ) {
            this.type = type;
        }
    }

    //store the concentrations of all solutes and set up a drain schedule,
    //so that particles will flow out at rates so as to keep the concentration level as constant as possible
    public void rescheduleDrainParticles( DrainData drainData ) {
        double drainedVolumePerSecond = outputFlowRate.get() * faucetFlowRate;

        if ( debugDraining ) {
            double timeToDrainFully = solution.volume.get() / drainedVolumePerSecond;
            System.out.println( "clock.getDt() = " + clock.getDt() + ", time to drain fully: " + timeToDrainFully );
        }

        if ( drainedVolumePerSecond > 0 ) {

            //Record the drain start time so that it can be accounted for in the propagation schedule, to keep track of how far particles have already come
            drainData.drainFlowStartTime = getTime();

            //Determine the time between particle exits, given that all should exit when the fluid is totally drained
            drainData.particlesDrainPerSecond = getIonsToDrainPerSecond( drainedVolumePerSecond, drainData.type );

            if ( debugDraining ) {
                System.out.println( "ionsPerSec = " + drainData.particlesDrainPerSecond );
            }
        }
    }

    //Compute the number of ions that should be drained per second (on average) to maintain a constant concentration
    private double getIonsToDrainPerSecond( double drainedVolumePerSecond, Class<? extends Particle> type ) {

        //TODO: this is counting all particles, we should just be counting the submerged free particles, not free ethanol falling to the water since it shouldn't move to the drain until it hits water
        int numSodiumIons = freeParticles.count( type );
        double amountOfFluidToDrain = solution.volume.get();

        //When draining, try to attain this number of target ions per volume as closely as possible
        double targetIonsPerVolume = numSodiumIons / amountOfFluidToDrain;

        //flow rate is volume / time, so ionsPerTime = ionsPerVolume * flowRate
        return targetIonsPerVolume * drainedVolumePerSecond;
    }

    //When the simulation clock ticks, move the particles
    @Override protected void updateModel( double dt ) {
        super.updateModel( dt );

        //If water is draining, call this first to set the update strategies to be FlowToDrain instead of FreeParticle
        //Do this before updating the free particles since this could change their strategy
        if ( outputFlowRate.get() > 0 ) {
            updateParticlesFlowingToDrain( sodiumDrainData );
            updateParticlesFlowingToDrain( chlorideDrainData );
            updateParticlesFlowingToDrain( sucroseDrainData );
            updateParticlesFlowingToDrain( nitrateDrainData );
            updateParticlesFlowingToDrain( calciumDrainData );
            updateParticlesFlowingToDrain( ethanolDrainData );
        }

        //Iterate over all particles and let them update in time
        for ( Particle freeParticle : joinLists( freeParticles, sodiumChlorideCrystals, sodiumNitrateCrystals, calciumChlorideCrystals, sucroseCrystals, drainedParticles ) ) {
            freeParticle.stepInTime( dt );
        }

        //Allow the crystals to grow--not part of the strategies because it has to look at all particles within a group to decide which to crystallize
        new SodiumChlorideCrystalGrowth( this, sodiumChlorideCrystals ).allowCrystalGrowth( dt, sodiumChlorideSaturated );
        new SucroseCrystalGrowth( this, sucroseCrystals ).allowCrystalGrowth( dt, sucroseSaturated );
        new CalciumChlorideCrystalGrowth( this, calciumChlorideCrystals ).allowCrystalGrowth( dt, calciumChlorideSaturated );
        new SodiumNitrateCrystalGrowth( this, sodiumNitrateCrystals ).allowCrystalGrowth( dt, sodiumNitrateSaturated );

        //Notify listeners that the update step completed
        for ( VoidFunction0 listener : stepFinishedListeners ) {
            listener.apply();
        }
    }

    private ArrayList<Particle> joinLists( ItemList<?>... freeParticles ) {
        ArrayList<Particle> p = new ArrayList<Particle>();
        for ( ItemList<?> freeParticle : freeParticles ) {
            ArrayList<?> list = freeParticle.toList();
            for ( Object o : list ) {
                p.add( (Particle) o );
            }
        }
        return p;
    }

    //Move the particles toward the drain and try to keep a constant concentration
    //all particles should exit when fluid is gone, move nearby particles
    //For simplicity and regularity (to minimize deviation from the target concentration level), plan to have particles exit at regular intervals
    private void updateParticlesFlowingToDrain( DrainData data ) {

        ArrayList<Particle> particles = freeParticles.filter( data.type );

        //Pre-compute the drain faucet input point since it is used throughout this method, and many times in the sort method
        final ImmutableVector2D inputPoint = getDrainFaucetMetrics().getInputPoint();

        //Sort particles by distance and set their speeds so that they will leave at the proper rate
        sort( particles, new Comparator<Particle>() {
            public int compare( Particle o1, Particle o2 ) {
                return Double.compare( o1.getPosition().getDistance( inputPoint ), o2.getPosition().getDistance( inputPoint ) );
            }
        } );

        //Set a position for each of the particles so they will leave at regular intervals and keep the concentration as constant as possible
        //Make closer particles leave first since that is more natural
        double secondsPerIon = 1.0 / data.particlesDrainPerSecond;
        double elapsedDrainTime = getTime() - data.drainFlowStartTime;
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = particles.get( i );
            int index = i + 1;

            //Compute the target time, distance, speed and velocity, and apply to the particle so they will reach the drain at evenly spaced temporal intervals
            double targetTime = secondsPerIon * index - elapsedDrainTime;
            double distanceToTarget = particle.getPosition().getDistance( inputPoint );
            double speed = distanceToTarget / targetTime;
            ImmutableVector2D velocity = new ImmutableVector2D( particle.getPosition(), inputPoint ).getInstanceOfMagnitude( speed );
            particle.setUpdateStrategy( new FlowToDrainStrategy( this, velocity ) );

            if ( debugDraining ) {
                System.out.println( "i = " + i + ", seconds per ion = " + secondsPerIon + ", target time = " + targetTime + ", velocity = " + speed + " nominal velocity = " + UpdateStrategy.FREE_PARTICLE_SPEED );
            }
        }
    }

    //Add a single salt crystal to the model
    public void addSodiumChlorideCrystal( SodiumChlorideCrystal sodiumChlorideCrystal ) {
        //Add the components of the lattice to the model so the graphics will be created
        for ( Constituent constituent : sodiumChlorideCrystal ) {
            //TODO: separate list for NaCl crystals so no cast required here?
            sphericalParticles.add( (SphericalParticle) constituent.particle );
        }
        sodiumChlorideCrystals.add( sodiumChlorideCrystal );
        sodiumChlorideCrystal.setUpdateStrategy( new CrystalStrategy( this, sodiumChlorideCrystals, sodiumChlorideSaturated ) );
    }

    //Add a single sodium nitrate crystal to the model
    public void addSodiumNitrateCrystal( SodiumNitrateCrystal crystal ) {
        crystal.setUpdateStrategy( new CrystalStrategy( this, sodiumNitrateCrystals, sodiumNitrateSaturated ) );
        addComponents( crystal );
        sodiumNitrateCrystals.add( crystal );
    }

    //Add all SphericalParticles contained in the compound so the graphics will be created
    private void addComponents( Compound<? extends Particle> compound ) {
        for ( SphericalParticle sphericalParticle : compound.getAllSphericalParticles() ) {
            sphericalParticles.add( sphericalParticle );
        }
    }

    //Remove all SphericalParticles contained in the compound so the graphics will be deleted
    void removeComponents( Compound<?> compound ) {
        for ( SphericalParticle sphericalParticle : compound.getAllSphericalParticles() ) {
            sphericalParticles.remove( sphericalParticle );
        }
    }

    public void addCalciumChlorideCrystal( CalciumChlorideCrystal calciumChlorideCrystal ) {
        calciumChlorideCrystal.setUpdateStrategy( new CrystalStrategy( this, calciumChlorideCrystals, calciumChlorideSaturated ) );
        addComponents( calciumChlorideCrystal );
        calciumChlorideCrystals.add( calciumChlorideCrystal );
    }

    //Add a sucrose crystal to the model, and add graphics for all its constituent particles
    public void addSucroseCrystal( SucroseCrystal sucroseCrystal ) {
        sucroseCrystal.setUpdateStrategy( new CrystalStrategy( this, sucroseCrystals, sucroseSaturated ) );
        addComponents( sucroseCrystal );
        sucroseCrystals.add( sucroseCrystal );
    }

    //Add ethanol above the solution at the dropper output location
    public void addEthanol( final ImmutableVector2D location ) {
        Ethanol ethanol = new Ethanol( location, randomAngle() ) {{
            //Give the ethanol molecules some initial downward velocity since they are squirted out of the dropper
            velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 * 3 ).

                    //Add randomness so they look more fluid-like
                            plus( parseAngleAndMagnitude( 0.25E-9 / 4, random() * PI ) ) );
        }};
        freeParticles.add( ethanol );
        ethanol.setUpdateStrategy( new FreeParticleStrategy( this ) );
        addComponents( ethanol );
    }

    //Keep the particle within the beaker solution bounds
    void preventFromLeavingBeaker( Particle particle ) {

        //If the particle ever entered the water fully, don't let it leave through the top
        if ( particle.hasSubmerged() ) {
            preventFromMovingPastWaterTop( particle );
        }
        preventFromFallingThroughBeakerBase( particle );
        preventFromFallingThroughBeakerRight( particle );
        preventFromFallingThroughBeakerLeft( particle );
    }

    //prevent particles from falling through the top of the water
    private void preventFromMovingPastWaterTop( Particle particle ) {
        double waterTopY = solution.shape.get().getBounds2D().getMaxY();
        double particleTopY = particle.getShape().getBounds2D().getMaxY();

        if ( particleTopY > waterTopY ) {
            //TODO: Factor out 1E-12
            particle.translate( 0, waterTopY - particleTopY - 1E-12 );
        }
    }

    boolean isCrystalTotallyAboveTheWater( Crystal crystal ) {
        return crystal.getShape().getBounds2D().getY() > solution.shape.get().getBounds2D().getMaxY();
    }

    void boundToBeakerBottom( Particle particle ) {
        if ( particle.getShape().getBounds2D().getMinY() < 0 ) {
            particle.translate( 0, -particle.getShape().getBounds2D().getMinY() );
        }
    }

    //Get the external force acting on the particle, gravity if the particle is in free fall or zero otherwise (e.g., in solution)
    ImmutableVector2D getExternalForce( final boolean anyPartUnderwater ) {
        return new ImmutableVector2D( 0, anyPartUnderwater ? 0 : -9.8 );
    }

    //Determine whether the object is underwater--when it touches the water it should slow down
    boolean isAnyPartUnderwater( Particle particle ) {
        return particle.getShape().intersects( solution.shape.get().getBounds2D() );
    }

    void collideWithWater( Particle particle ) {
        particle.velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 ) );
    }

    public void reset() {
        super.reset();

        //Clear out solutes, particles, concentration values
        clearSolutes();

        //Reset model for user settings
        showConcentrationValues.reset();
        dispenserType.reset();
        showChargeColor.reset();
        selectedKit.reset();
        clockRunning.reset();
    }

    private void clearSolutes() {
        //Clear particle lists
        sphericalParticles.clear();
        freeParticles.clear();
        sodiumChlorideCrystals.clear();
        sodiumNitrateCrystals.clear();
        calciumChlorideCrystals.clear();
        sucroseCrystals.clear();
    }

    //Determine if there is any table salt to remove
    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return sodiumConcentration.greaterThan( 0.0 ).and( chlorideConcentration.greaterThan( 0.0 ) );
    }

    //Determine if there is any sugar that can be removed
    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return sucroseConcentration.greaterThan( 0.0 );
    }

    //Removes all the sodium nitrate from the model.  This assumes that the nitrate group is unique to the sodium nitrate, i.e. does not appear in any other
    //molecules in the kit.  Based on this assumption, the same number of sodium as nitrates is removed since some sodiums could have come from other sources like NaCl
    public void removeAllSodiumNitrate() {

        //Remove any crystals
        while ( sodiumChlorideCrystals.size() > 0 ) {
            removeSodiumNitrate( sodiumNitrateCrystals.get( 0 ) );
        }

        //Remove the nitrate groups
        ArrayList<Particle> nitrates = freeParticles.filter( Nitrate.class );
        for ( Particle p : nitrates ) {
            Nitrate nitrate = (Nitrate) p;
            freeParticles.remove( nitrate );
            removeComponents( nitrate );
        }

        //Remove just as many sodium particles (if there are that many)
        ArrayList<Particle> sodium = freeParticles.filter( Sodium.class );
        for ( int i = 0; i < nitrates.size() && i < sodium.size(); i++ ) {
            freeParticles.remove( sodium.get( i ) );
            sphericalParticles.remove( (SphericalParticle) sodium.get( i ) );
        }
    }

    public void removeAllCalciumChloride() {
        //Remove any crystals
        while ( calciumChlorideCrystals.size() > 0 ) {
            removeCalciumChlorideCrystal( calciumChlorideCrystals.get( 0 ) );
        }

        //Remove the Calcium ions first, since they are unique to CaCl2 given the kits (other molecule in this kit is NaCl)
        ArrayList<Particle> calcium = freeParticles.filter( Calcium.class );
        for ( Particle p : calcium ) {
            freeParticles.remove( p );
            sphericalParticles.remove( (SphericalParticle) p );
        }

        //Remove twice as many chloride particles (if there are that many)
        ArrayList<Particle> chloride = freeParticles.filter( Chloride.class );
        for ( int i = 0; i < calcium.size() * 2 && i < chloride.size(); i++ ) {
            freeParticles.remove( chloride.get( i ) );
            sphericalParticles.remove( (SphericalParticle) chloride.get( i ) );
        }
    }

    //Remove all corresponding sodium chloride from the simulation
    //TODO: how to make sure no strays left after this?
    public void removeAllSodiumChloride() {

        //Remove any crystals
        while ( sodiumChlorideCrystals.size() > 0 ) {
            removeSodiumChlorideCrystal( sodiumChlorideCrystals.get( 0 ) );
        }

        //Remove the Calcium ions first, since they are unique to CaCl2 given the kits (other molecule in this kit is NaCl)
        ArrayList<Particle> sodium = freeParticles.filter( Sodium.class );
        ArrayList<Particle> chloride = freeParticles.filter( Chloride.class );

        int min = Math.min( sodium.size(), chloride.size() );

        for ( int i = 0; i < min; i++ ) {
            freeParticles.remove( sodium.get( i ) );
            sphericalParticles.remove( (SphericalParticle) sodium.get( i ) );

            freeParticles.remove( chloride.get( i ) );
            sphericalParticles.remove( (SphericalParticle) chloride.get( i ) );
        }
    }

    public void removeAllEthanol() {
        ArrayList<Particle> ethanol = freeParticles.filter( Ethanol.class );
        for ( Particle ethanolMolecule : ethanol ) {
            freeParticles.remove( ethanolMolecule );
            removeComponents( (Compound<?>) ethanolMolecule );
        }
    }

    //Remove a sodium nitrate crystal and all its sub-particles
    private void removeSodiumNitrate( SodiumNitrateCrystal crystal ) {
        sodiumNitrateCrystals.remove( crystal );
        removeComponents( crystal );
    }

    //Remove a calcium chloride crystal and all its sub-particles
    private void removeCalciumChlorideCrystal( CalciumChlorideCrystal crystal ) {
        calciumChlorideCrystals.remove( crystal );
        removeComponents( crystal );
    }

    //Remove a calcium chloride crystal and all its sub-particles
    private void removeSodiumChlorideCrystal( SodiumChlorideCrystal crystal ) {
        sodiumChlorideCrystals.remove( crystal );
        removeComponents( crystal );
    }

    //Remove all sucrose molecules from the model
    public void removeAllSucrose() {
        ArrayList<Particle> sucrose = freeParticles.filter( Sucrose.class );
        for ( Particle sucroseMolecule : sucrose ) {
            freeParticles.remove( sucroseMolecule );
            removeComponents( (Compound<?>) sucroseMolecule );
        }
    }

    @Override public ObservableProperty<Boolean> getAnySolutes() {
        return anySolutes;
    }

    //Iterate over particles that take random walks so they don't move above the top of the water
    private void updateParticlesDueToWaterLevelDropped( double changeInWaterHeight ) {
        waterLevelDropped( freeParticles, changeInWaterHeight );
        waterLevelDropped( sucroseCrystals, changeInWaterHeight );
        waterLevelDropped( sodiumChlorideCrystals, changeInWaterHeight );
        waterLevelDropped( calciumChlorideCrystals, changeInWaterHeight );
        waterLevelDropped( sodiumNitrateCrystals, changeInWaterHeight );
    }

    //When water level decreases, move the particles down with the water level.
    //Beaker base is at y=0.  Move particles proportionately to how close they are to the top.
    private void waterLevelDropped( ItemList<? extends Particle> particles, double volumeDropped ) {

        double changeInWaterHeight = beaker.getHeightForVolume( volumeDropped ) - beaker.getHeightForVolume( 0 );
        for ( Particle particle : particles ) {
            if ( waterVolume.get() > 0 ) {
                double yLocationInBeaker = particle.getPosition().getY();
                double waterTopY = beaker.getHeightForVolume( waterVolume.get() );

                //Only move particles down if they are fully underwater
                if ( yLocationInBeaker < waterTopY ) {
                    double fractionToTop = yLocationInBeaker / waterTopY;
                    particle.translate( 0, -changeInWaterHeight * fractionToTop );

                    //Prevent particles from leaving the top of the liquid
                    preventFromLeavingBeaker( particle );
                }
            }

            //This step must be done after prevention of particles leaving the top because falling through the bottom is worse (never returns), pushing through the top, particles
            //would just fall back to the water level
            preventFromFallingThroughBeakerBase( particle );
        }
    }

    //prevent particles from falling through the bottom of the beaker
    private void preventFromFallingThroughBeakerBase( Particle particle ) {
        double bottomY = particle.getShape().getBounds2D().getMinY();
        if ( bottomY < 0 ) {
            particle.translate( 0, -bottomY + 1E-12 );
        }
    }

    //prevent particles from falling through the bottom of the beaker
    private void preventFromFallingThroughBeakerLeft( Particle particle ) {
        double left = particle.getShape().getBounds2D().getMinX();
        if ( left < beaker.getLeftWall().getX1() ) {
            particle.translate( beaker.getLeftWall().getX1() - left, 0 );
        }
    }

    //prevent particles from falling through the bottom of the beaker
    private void preventFromFallingThroughBeakerRight( Particle particle ) {
        double right = particle.getShape().getBounds2D().getMaxX();
        if ( right > beaker.getRightWall().getX1() ) {
            particle.translate( beaker.getRightWall().getX1() - right, 0 );
        }
    }

    //When water evaporates, move the particles so they move down with the water level
    @Override protected void waterEvaporated( double evaporatedWater ) {
        super.waterEvaporated( evaporatedWater );
        updateParticlesDueToWaterLevelDropped( evaporatedWater );
    }

    //Get one list of bonding sites for each crystal for debugging purposes
    public ArrayList<ArrayList<CrystallizationMatch<SphericalParticle>>> getAllBondingSites() {
        ArrayList<ArrayList<CrystallizationMatch<SphericalParticle>>> s = new ArrayList<ArrayList<CrystallizationMatch<SphericalParticle>>>();
        for ( SodiumChlorideCrystal crystal : sodiumChlorideCrystals ) {
            s.add( new SodiumChlorideCrystalGrowth( this, sodiumChlorideCrystals ).getAllCrystallizationMatches( crystal ) );
        }
        for ( CalciumChlorideCrystal crystal : calciumChlorideCrystals ) {
            s.add( new CalciumChlorideCrystalGrowth( this, calciumChlorideCrystals ).getAllCrystallizationMatches( crystal ) );
        }
        return s;
    }
}