/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents water (H2O) in the model.
 * 
 * @author John Blanco
 */
public class H2O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    // These constants define the initial shape of the water atom.  The angle
    // between the atoms is intended to be correct, and the bond is somewhat
    // longer than real life.  The algebraic calculations are intended to make
    // it so that the bond length and/or the angle could be changed and the
    // correct center of gravity will be maintained.
    private static final double OXYGEN_HYDROGEN_BOND_LENGTH = 150;
    private static final double INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE = 104.5;
    private static final double INITIAL_OXYGEN_VERTICAL_DISPLACEMENT = 2 * new HydrogenAtom().getMass() * 
        OXYGEN_HYDROGEN_BOND_LENGTH * Math.cos( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE ) / (new OxygenAtom().getMass() *
        2 * new HydrogenAtom().getMass());
    private static final double INITIAL_HYDROGEN_VERTICAL_DISPLACEMENT = INITIAL_OXYGEN_VERTICAL_DISPLACEMENT -
        OXYGEN_HYDROGEN_BOND_LENGTH * Math.cos( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE );
    private static final double INITIAL_HYDROGEN_HORIZONTAL_DISPLACEMENT = OXYGEN_HYDROGEN_BOND_LENGTH *
        Math.sin( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE );
        
    // TODO: These values should be calculated for correct COG.
    private static final Dimension2D INITIAL_OXYGEN_OFFSET = new PDimension(0, 10); // In picometers.
    private static final Dimension2D INITIAL_HYDROGEN1_OFFSET = new PDimension(-90, -90); // In picometers.
    private static final Dimension2D INITIAL_HYDROGEN2_OFFSET = new PDimension(90, -90); // In picometers.
    
    private static final double OSCILLATION_FREQUENCY = 6;  // Cycles per second of sim time.
    
    private static final double PHOTON_ABSORPTION_DISTANCE = 200;
    
    private static final double MIN_PHOTON_HOLD_TIME = 500; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 1500; // Milliseconds of sim time.
    private static final double ABSORPTION_HYSTERESIS_TIME = 200; // Milliseconds of sim time.
    
    private static final Random RAND = new Random();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final HydrogenAtom hydrogenAtom1 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom2 = new HydrogenAtom();
    private final AtomicBond oxygenHydrogenBond1 = new AtomicBond( oxygenAtom, hydrogenAtom1, 1 );
    private final AtomicBond oxygenHydrogenBond2 = new AtomicBond( oxygenAtom, hydrogenAtom2, 1 );
    
    private double oscillationRadians = 0;
    
    private boolean photonAbsorbed = false;
    
    private double photonHoldCountdownTime = 0;
    private double absorbtionHysteresisCountdownTime = 0;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public H2O(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( oxygenAtom );
        addAtom( hydrogenAtom1 );
        addAtom( hydrogenAtom2 );
        addAtomicBond( oxygenHydrogenBond1 );
        addAtomicBond( oxygenHydrogenBond2 );

        // Set the initial offsets.
        initializeCogOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public H2O(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeCogOffsets() {
        atomCogOffsets.put(oxygenAtom, new PDimension(0, INITIAL_OXYGEN_VERTICAL_DISPLACEMENT));
        atomCogOffsets.put(hydrogenAtom1, new PDimension(INITIAL_HYDROGEN_HORIZONTAL_DISPLACEMENT,
                -INITIAL_HYDROGEN_VERTICAL_DISPLACEMENT));
        atomCogOffsets.put(hydrogenAtom2, new PDimension(-INITIAL_HYDROGEN_HORIZONTAL_DISPLACEMENT,
                -INITIAL_HYDROGEN_VERTICAL_DISPLACEMENT));
    }
    
    @Override
    public boolean absorbPhoton( Photon photon ) {
        if (!photonAbsorbed &&
             absorbtionHysteresisCountdownTime <= 0 &&
             photon.getWavelength() == GreenhouseConfig.irWavelength &&
             photon.getLocation().distance(oxygenAtom.getPosition()) < PHOTON_ABSORPTION_DISTANCE)
        {
            photonAbsorbed = true;
            photonHoldCountdownTime = MIN_PHOTON_HOLD_TIME + RAND.nextDouble() * (MAX_PHOTON_HOLD_TIME - MIN_PHOTON_HOLD_TIME);
            return true;
        }
        else{
            return false;
        }
    }
}
