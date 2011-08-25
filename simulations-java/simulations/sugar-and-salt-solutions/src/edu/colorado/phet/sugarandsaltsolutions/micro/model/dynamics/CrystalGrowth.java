// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.UpdateStrategy.FREE_PARTICLE_SPEED;
import static java.util.Collections.sort;

/**
 * This class handles incremental crystallization of particles when the concentration surpasses the saturation point.
 * I originally tried just specifying the "T extends particle" generic type and using Crystal
 *
 * @author Sam Reid
 */
public abstract class CrystalGrowth<T extends Particle, U extends Crystal<T>> {

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNewCrystalFormationTime;

    //The main model for timing and adding/removing particles and crystals
    protected final MicroModel model;

    //The list of all crystals of the appropriate type
    private final ItemList<U> crystals;

    //Randomness for crystal formation times and which crystals to bond to
    private final Random random = new Random();

    //Flag to show debug information to the console about crystal formation
    private boolean debug = false;

    public CrystalGrowth( MicroModel model, ItemList<U> crystals ) {
        this.model = model;
        this.crystals = crystals;
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    public void allowCrystalGrowth( double dt, ObservableProperty<Boolean> saturated ) {
        double timeSinceLast = model.getTime() - lastNewCrystalFormationTime;

        //Require some time to pass since each crystallization event so it isn't too jumpy (but if water below threshold, then proceed quickly or it will look unphysical as each ion jumps to the crystal one after the other
        boolean elapsedTimeLongEnough = timeSinceLast > 0.1 || model.isWaterBelowCrystalThreshold();

        //Form new crystals or add on to existing crystals
        if ( ( saturated.get() || model.isWaterBelowCrystalThreshold() ) && elapsedTimeLongEnough ) {
            lastNewCrystalFormationTime = model.getTime();
            if ( crystals.size() == 0 ) {
                //Create a crystal if there weren't any
                debug( "No crystals, starting a new one, num crystals = " + crystals.size() );
                towardNewCrystal( dt );
            }

            //If the solution is saturated, try adding on to an existing crystal
            else {

                //Randomly choose an existing crystal to possibly bond to
                Crystal<T> crystal = crystals.get( random.nextInt( crystals.size() ) );

                //Enumerate all particles and distances from crystal sites, but only look for sites that are underwater, otherwise particles would try to fly out of the solution (and get stuck at the boundary)
                ArrayList<CrystallizationMatch<T>> matches = getAllCrystallizationMatchesSorted( crystal );
                if ( matches.size() > 0 ) {

                    //Find a matching particle nearby one of the sites and join it together
                    CrystallizationMatch<T> match = matches.get( 0 );

                    //With some probability, form a new crystal anyways (if there aren't too many crystals)
                    if ( random.nextDouble() > 0.8 && crystals.size() <= 2 ) {
                        debug( "Random choice to form new crystal instead of joining another" );
                        towardNewCrystal( dt );
                    }

                    //If close enough, join the lattice.  Having a large factor here makes it possible for particles to jump on to crystals quickly
                    //And fixes many problems in crystallization, including large or unbalanced concentrations
                    else if ( match.distance <= FREE_PARTICLE_SPEED * dt * 100 ) {

                        //Remove the particle from the list of free particles
                        model.freeParticles.remove( match.particle );

                        //Add it as a constituent of the crystal
                        crystal.addConstituent( new Constituent<T>( match.particle, match.site.relativePosition ) );
                    }

                    //Otherwise, move closest particle toward the lattice
                    else if ( match.distance <= model.beaker.getWidth() / 2 ) {
                        match.particle.velocity.set( match.site.absolutePosition.minus( match.particle.getPosition() ).getInstanceOfMagnitude( FREE_PARTICLE_SPEED ) );
                    }

                    else {
                        debug( "Best match was too far away (" + match.distance / model.beaker.getWidth() + " beaker widths, so trying to form new crystal from lone ions" );
                        towardNewCrystal( dt );
                    }
                }

                //No matches, so start a new crystal
                else {
                    debug( "No matches, starting a new crystal" );
                    towardNewCrystal( dt );
                }
            }
        }
    }

    //Show some debugging information to the console
    private void debug( String s ) {
        if ( debug ) {
            System.out.println( s );
        }
    }

    //Look for all open site on its lattice and sort by distance to available participants
    //TODO: prefer particles that match the minority type on the crystal (according to the formula ratio) to keep ion levels balanced
    public ArrayList<CrystallizationMatch<T>> getAllCrystallizationMatchesSorted( Crystal<T> crystal ) {
        ArrayList<CrystallizationMatch<T>> matches = new ArrayList<CrystallizationMatch<T>>();
        final Class<? extends Particle> minorityType = crystal.getMinorityType();

        //TODO: If minority type is null, then try to bring out of solution whichever type had more concentration in the model (according to the formula ratio), this will balance ion ratios across crystals and help to maintain a good balance
        //But maybe this would cause a bigger oscillating problem if multiple crystals are vying for the same type

        //If the crystal was balanced, allow adding either type.  If the crystal was unbalanced, add the minority type to restore balance
        Iterable<? extends Particle> particlesToConsider = minorityType == null ? model.freeParticles : model.freeParticles.filter( minorityType );

        for ( Particle freeParticle : particlesToConsider ) {
            for ( OpenSite<T> openSite : crystal.getOpenSites() ) {
                if (
//                        model.solution.shape.get().contains( openSite.shape.getBounds2D() ) &&
                        openSite.matches( freeParticle ) ) {
                    matches.add( new CrystallizationMatch<T>( (T) freeParticle, openSite ) );
                }
            }
        }

        //Find the best site
        sort( matches, new Comparator<CrystallizationMatch>() {
            public int compare( CrystallizationMatch o1, CrystallizationMatch o2 ) {
                return Double.compare( o1.distance, o2.distance );
            }
        } );
        return matches;
    }

    //Move nearby matching particles closer together, or, if close enough, form a 2-particle crystal with them
    private void towardNewCrystal( double dt ) {

        //Find the pair of particles closest to each other, and move them even closer to each other.  When they are close enough, form the crystal
        ArrayList<IFormulaUnit> seeds = getAllSeeds();
        sort( seeds, new Comparator<IFormulaUnit>() {
            public int compare( IFormulaUnit o1, IFormulaUnit o2 ) {
                return Double.compare( o1.getDistance(), o2.getDistance() );
            }
        } );

        //If there was a match, move the closest particles even closer together
        //If they are close enough, convert them into a crystal
        if ( seeds.size() > 0 ) {
            IFormulaUnit closestSet = seeds.get( 0 );
            closestSet.moveTogether( dt );
            if ( closestSet.getDistance() <= dt * UpdateStrategy.FREE_PARTICLE_SPEED ) {
                convertToCrystal( closestSet );

                //Record the crystal formation time so new crystals don't form too often
                lastNewCrystalFormationTime = model.getTime();
            }
        }
    }

    //Crystal-specific code to generate a list of all matching sets of particles according to the chemical formula,
    //these are particles that could form a new crystal, if they are close enough together
    protected abstract ArrayList<IFormulaUnit> getAllSeeds();

    //Convert the specified particles to a crystal and add the crystal to the model
    private void convertToCrystal( IFormulaUnit<T> seed ) {

        T a = seed.getParticles().get( 0 );

        //Create a crystal based on the 'a' particle, then add the 'b' particle as the second constituent
        U crystal = newCrystal( a.getPosition() );

        for ( T particle : seed.getParticles() ) {

            if ( crystal.numberConstituents() == 0 ) {
                crystal.addConstituent( new Constituent<T>( particle, ZERO ) );
            }
            else {

                OpenSite<T> selectedSite = getBindingSite( crystal, particle );
                //Add the second particle as the second constituent of the crystal
                if ( selectedSite == null ) {
                    debug( "No available sites to bind to, this probably shouldn't have happened." );
                }
                else {
                    crystal.addConstituent( new Constituent<T>( particle, selectedSite.relativePosition ) );
                }
            }
            model.freeParticles.remove( particle );
        }
        crystals.add( crystal );
    }

    private OpenSite<T> getBindingSite( U crystal, T b ) {
        //Choose a site that matches the first particle
        ItemList<OpenSite<T>> sites = crystal.getOpenSites();
        Collections.shuffle( sites );
        OpenSite<T> selectedSite = null;
        for ( OpenSite<T> site : sites ) {
            if ( site.matches( b ) ) {
                selectedSite = site;
                break;
            }
        }
        return selectedSite;
    }

    //Create the right subtype of crystal at the specified location.  It will be populated by the convertToCrystal method
    protected abstract U newCrystal( ImmutableVector2D position );
}