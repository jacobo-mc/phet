/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Main model class.  Units are picometers (1E-12).
 */
public class BuildAnAtomModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Rectangle2D MODEL_VIEWPORT =
            new Rectangle2D.Double( -200, -150,
            400,
            400 * BuildAnAtomDefaults.STAGE_SIZE.getHeight() / BuildAnAtomDefaults.STAGE_SIZE.getWidth() );//use the same aspect ratio so circles don't become elliptical

    // Constants that define the number of sub-atomic particles that exist
    // within the sim.
    private static final int DEFAULT_NUM_ELECTRONS = 10;
    private static final int DEFAULT_NUM_PROTONS = 10;
    private static final int DEFAULT_NUM_NEUTRONS = 13;

    // Constants that define the size, position, and appearance of the buckets.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 60, 30 );
    private static final Point2D PROTON_BUCKET_POSITION = new Point2D.Double( -80, -150 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( 0, -150 );
    private static final Point2D ELECTRON_BUCKET_POSITION = new Point2D.Double( 80, -150 );

    protected static final double NUCLEUS_CAPTURE_DISTANCE = Atom.ELECTRON_SHELL_1_RADIUS;

    protected static final double ELECTRON_CAPTURE_DISTANCE = Atom.ELECTRON_SHELL_2_RADIUS + 20;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    private final Atom atom;

    // The subatomic particles.
    private final ArrayList<Electron> electrons = new ArrayList<Electron>();
    private final ArrayList<Proton> protons = new ArrayList<Proton>();
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();

    // The buckets which can hold the subatomic particles.
    private final SubatomicParticleBucket electronBucket = new SubatomicParticleBucket( ELECTRON_BUCKET_POSITION,
            BUCKET_SIZE, Color.blue, BuildAnAtomStrings.ELECTRONS_NAME, Electron.RADIUS, 0.6, -Electron.RADIUS / 2 );
    private final SubatomicParticleBucket protonBucket = new SubatomicParticleBucket( PROTON_BUCKET_POSITION,
            BUCKET_SIZE, Color.red, BuildAnAtomStrings.PROTONS_NAME, Proton.RADIUS );
    private final SubatomicParticleBucket neutronBucket = new SubatomicParticleBucket( NEUTRON_BUCKET_POSITION,
            BUCKET_SIZE, Color.gray, BuildAnAtomStrings.NEUTRONS_NAME, Neutron.RADIUS );

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        this( clock, new AtomValue( DEFAULT_NUM_PROTONS, DEFAULT_NUM_NEUTRONS, DEFAULT_NUM_ELECTRONS ) );
    }

    public BuildAnAtomModel( BuildAnAtomClock clock, AtomValue atomValue ) {
        super();

        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ) );

        for ( int i = 0; i < atomValue.getElectrons(); i++ ) {
            final Electron electron = new Electron( clock );
            electrons.add( electron );
            electron.addListener( new SubatomicParticle.Adapter() {
                @Override
                public void droppedByUser( SubatomicParticle particle ) {
                    // The user just released this electron.  If it is close
                    // enough to the shell, and there is room, send it there.
                    // Otherwise send it to its bucket.
                    if ( atom.getRemainingElectronCapacity() > 0 && electron.getPosition().distance( atom.getPosition() ) < ELECTRON_CAPTURE_DISTANCE ) {
                        atom.addElectron( electron ,true);
                    }
                    else {
                        electronBucket.addParticle( electron, false );
                    }
                }
            } );
        }

        for ( int i = 0; i < atomValue.getProtons(); i++ ) {
            final Proton proton = new Proton( clock );
            protons.add( proton );
            proton.addListener( new SubatomicParticle.Adapter() {
                @Override
                public void droppedByUser( SubatomicParticle particle ) {
                    // The user just released this proton.  If it is close
                    // enough to the nucleus, send it there, otherwise
                    // send it to its bucket.
                    if ( proton.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                        atom.addProton( proton ,true);
                    }
                    else {
                        protonBucket.addParticle( proton, false );
                    }
                }
            } );
        }

        for ( int i = 0; i < atomValue.getNeutrons(); i++ ) {
            final Neutron neutron = new Neutron( clock );
            neutrons.add( neutron );
            neutron.addListener( new SubatomicParticle.Adapter() {
                @Override
                public void droppedByUser( SubatomicParticle particle ) {
                    // The user just released this neutron.  If it is close
                    // enough to the nucleus, send it there, otherwise
                    // send it to its bucket.
                    if ( neutron.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                        atom.addNeutron( neutron,true );
                    }
                    else {
                        neutronBucket.addParticle( neutron, false );
                    }
                }
            } );
        }
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public Electron getElectron( int i ) {
        assert i >= 0 && i < numElectrons();
        return electrons.get( i );
    }

    public int numElectrons() {
        return electrons.size();
    }

    public Proton getProton( int i ) {
        assert i >= 0 && i < numProtons();
        return protons.get( i );
    }

    public int numProtons() {
        return protons.size();
    }

    public Neutron getNeutron( int i ) {
        assert i >= 0 && i < numNeutrons();
        return neutrons.get( i );
    }

    public int numNeutrons() {
        return neutrons.size();
    }

    public void reset() {

        // Reset the constituent model elements.
        atom.reset();
        electronBucket.reset();
        neutronBucket.reset();
        protonBucket.reset();

        // Put all the particles back in the bucket.
        for ( Electron electron : electrons ) {
            // TODO: Get rid of commented out code when done debugging nucleus reconfiguration algorithm.
            //            atom.addElectron( electron);
            electronBucket.addParticle( electron, true );
        }
        for ( Proton proton : protons ) {
            // TODO: Get rid of commented out code when done debugging nucleus reconfiguration algorithm.
            //            atom.addProton( proton );
            protonBucket.addParticle( proton, true );
        }
        for ( Neutron neutron : neutrons ) {
            // TODO: Get rid of commented out code when done debugging nucleus reconfiguration algorithm.
            //            atom.addNeutron( neutron );
            neutronBucket.addParticle( neutron, true );
        }

    }

    public Atom getAtom() {
        return atom;
    }

    public Rectangle2D getModelViewport() {
        return MODEL_VIEWPORT;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public SubatomicParticleBucket getElectronBucket() {
        return electronBucket;
    }

    public SubatomicParticleBucket getProtonBucket() {
        return protonBucket;
    }

    public SubatomicParticleBucket getNeutronBucket() {
        return neutronBucket;
    }

    public ArrayList<Proton> getProtons() {
        return protons;
    }

    public ArrayList<Neutron> getNeutrons() {
        return neutrons;
    }

    public ArrayList<Electron> getElectrons() {
        return electrons;
    }
    public static<T> ArrayList<T> reverse(ArrayList<T>list){
        ArrayList<T> t = new ArrayList<T>( list );
        Collections.reverse( t );
        return t;
    }

    public Proton getFreeProton() {
        for ( Proton proton : reverse(protons )) {
            if (!getAtom().containsProton(proton )){
                return proton;
            }
        }
        return null;
    }
    public Neutron getFreeNeutron() {
        for ( Neutron neutron : reverse(neutrons ) ) {
            if (!getAtom().containsNeutron(neutron )){
                return neutron;
            }
        }
        return null;
    }
    public Electron getFreeElectron() {
        for ( Electron electron : reverse(electrons )) {
            if (!getAtom().containsElectron(electron )){
                return electron;
            }
        }
        return null;
    }

    public void setState( AtomValue answer, boolean moveImmediately ) {
        ArrayList<SubatomicParticle> removed = getAtom().setState( answer, this, moveImmediately );// Add new particles into the atom
        for ( SubatomicParticle particle : removed ) {
            if ( particle instanceof Proton ) {
                protonBucket.addParticle( particle, moveImmediately );
            }
            else if ( particle instanceof Electron ) {
                electronBucket.addParticle( particle, moveImmediately );
            }
            else if ( particle instanceof Neutron ) {
                neutronBucket.addParticle( particle, moveImmediately );
            }
        }
    }
}
