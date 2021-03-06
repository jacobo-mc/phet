// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Class that represents the sun (as an energy source) in the model.  This
 * includes the clouds that can block the sun's rays.
 *
 * @author John Blanco
 */
public class Sun extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double RADIUS = 0.02; // In meters, apparent size, not (obviously) actual size.
    public static final Vector2D OFFSET_TO_CENTER_OF_SUN = new Vector2D( -0.05, 0.12 );
    public static final double ENERGY_CHUNK_EMISSION_PERIOD = 0.11; // In seconds.
    private static final Random RAND = new Random();
    private static final double MAX_DISTANCE_OF_E_CHUNKS_FROM_SUN = 0.5; // In meters.

    // Constants that control the nature of the emission sectors.  These are
    // used to make emission look random yet still have a fairly steady rate
    // within each sector.  One sector is intended to point at the solar panel.
    public static final int NUM_EMISSION_SECTORS = 10;
    public static final double EMISSION_SECTOR_SPAN = 2 * Math.PI / NUM_EMISSION_SECTORS;
    public static final double EMISSION_SECTOR_OFFSET = EMISSION_SECTOR_SPAN * 0.71; // Used to tweak sector positions to make sure solar panel gets consistent flow of E's.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Clouds that can potentially block the sun's rays.  The positions are
    // set so that they appear between the sun and the solar panel, and must
    // not overlap with one another.
    public final List<Cloud> clouds = new ArrayList<Cloud>() {{
        add( new Cloud( new Vector2D( 0.02, 0.105 ), getObservablePosition() ) );
        add( new Cloud( new Vector2D( 0.017, 0.0875 ), getObservablePosition() ) );
        add( new Cloud( new Vector2D( -0.01, 0.08 ), getObservablePosition() ) );
    }};

    public final Property<Double> cloudiness = new Property<Double>( 0.0 );

    // Solar panel that will, at times, be absorbing the sun's rays.
    public final SolarPanel solarPanel;

    private double energyChunkEmissionCountdownTimer = ENERGY_CHUNK_EMISSION_PERIOD;
    private final BooleanProperty energyChunksVisible;
    private final List<Integer> sectorList = new ArrayList<Integer>() {{
        for ( int i = 0; i < NUM_EMISSION_SECTORS; i++ ) {
            add( i );
        }
    }};

    // List of energy chunks that should be allowed to pass through the clouds
    // without bouncing (i.e. being reflected).
    private final List<EnergyChunk> energyChunksPassingThroughClouds = new ArrayList<EnergyChunk>();

    private int currentSectorIndex = 0;

    private Vector2D sunPosition = OFFSET_TO_CENTER_OF_SUN;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected Sun( SolarPanel solarPanel, BooleanProperty energyChunksVisible ) {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON );
        this.solarPanel = solarPanel;
        this.energyChunksVisible = energyChunksVisible;

        // Add/remove clouds based on the value of the cloudiness property.
        cloudiness.addObserver( new VoidFunction1<Double>() {
            public void apply( Double cloudiness ) {
                for ( int i = 0; i < clouds.size(); i++ ) {
                    // Stagger the existence strength of the clouds.
                    clouds.get( i ).existenceStrength.set( MathUtil.clamp( 0, cloudiness * clouds.size() - i, 1 ) );
                }
            }
        } );

        Collections.shuffle( sectorList );

        // Keep the sun position updated.
        getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D position ) {
                sunPosition = position.plus( OFFSET_TO_CENTER_OF_SUN );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {

        double energyProduced = 0;

        if ( isActive() ) {

            // See if it is time to emit a new energy chunk.
            energyChunkEmissionCountdownTimer -= dt;
            if ( energyChunkEmissionCountdownTimer <= 0 ) {

                // Create a new chunk and start it on its way.
                emitEnergyChunk();
                energyChunkEmissionCountdownTimer += ENERGY_CHUNK_EMISSION_PERIOD;
            }

            // Move the energy chunks.
            updateEnergyChunkPositions( dt );

            // Calculate the amount of energy produced.
            energyProduced = EFACConstants.MAX_ENERGY_PRODUCTION_RATE * ( 1 - cloudiness.get() ) * dt;
        }

        // Produce the energy.
        return new Energy( EnergyType.LIGHT, energyProduced );
    }

    private void updateEnergyChunkPositions( double dt ) {

        // Check for bouncing and absorption of the energy chunks.
        for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {
            if ( solarPanel.isActive() && solarPanel.getAbsorptionShape().contains( energyChunk.position.get().toPoint2D() ) ) {
                // This energy chunk was absorbed by the solar panel, so
                // put it on the list of outgoing chunks.
                outgoingEnergyChunks.add( energyChunk );
            }
            else if ( energyChunk.position.get().distance( sunPosition.plus( OFFSET_TO_CENTER_OF_SUN ) ) > MAX_DISTANCE_OF_E_CHUNKS_FROM_SUN ) {
                // This energy chunk is out of visible range, so remove it.
                energyChunkList.remove( energyChunk );
                energyChunksPassingThroughClouds.remove( energyChunk );
            }
            else {
                for ( Cloud cloud : clouds ) {
                    if ( cloud.getCloudAbsorptionReflectionShape().contains( energyChunk.position.get().toPoint2D() ) &&
                         !energyChunksPassingThroughClouds.contains( energyChunk ) &&
                         Math.abs( energyChunk.getVelocity().getAngle() - energyChunk.position.get().minus( sunPosition ).getAngle() ) < Math.PI / 10 ) {
                        // Decide whether this energy chunk should pass
                        // through the clouds or be reflected.
                        if ( RAND.nextDouble() < cloud.existenceStrength.get() ) {
                            // Reflect the energy chunk.  It looks a little
                            // weird if they go back to the sun, so the
                            // code below tries to avoid that.
                            double angleTowardsSun = energyChunk.getVelocity().getAngle() + Math.PI;
                            double reflectionAngle = new Vector2D( cloud.getCenterPosition(), energyChunk.position.get() ).getAngle();
                            if ( reflectionAngle < angleTowardsSun ) {
                                energyChunk.setVelocity( energyChunk.getVelocity().getRotatedInstance( 0.7 * Math.PI + RAND.nextDouble() * Math.PI / 8 ) );
                            }
                            else {
                                energyChunk.setVelocity( energyChunk.getVelocity().getRotatedInstance( -0.7 * Math.PI - RAND.nextDouble() * Math.PI / 8 ) );
                            }
                        }
                        else {
                            // Let is pass through the cloud.
                            energyChunksPassingThroughClouds.add( energyChunk );
                        }
                    }
                }
            }
        }

        // Move all the energy chunks away from the sun.
        for ( EnergyChunk energyChunk : energyChunkList ) {
            energyChunk.translateBasedOnVelocity( dt );
        }
    }

    private void emitEnergyChunk() {
        double directionAngle = chooseNextEmissionAngle();
        Vector2D initialPosition = sunPosition.plus( new Vector2D( RADIUS / 2, 0 ).getRotatedInstance( directionAngle ) );
        EnergyChunk energyChunk = new EnergyChunk( EnergyType.LIGHT, initialPosition.x, initialPosition.y, energyChunksVisible );
        energyChunk.setVelocity( new Vector2D( EFACConstants.ENERGY_CHUNK_VELOCITY, 0 ).getRotatedInstance( directionAngle ) );
        energyChunkList.add( energyChunk );
    }

    @Override public void preLoadEnergyChunks() {
        clearEnergyChunks();
        double preLoadTime = 6; // In simulated seconds, empirically determined.
        double dt = 1 / EFACConstants.FRAMES_PER_SECOND;
        energyChunkEmissionCountdownTimer = 0;

        // Simulate energy chunks moving through the system.
        while ( preLoadTime > 0 ) {
            energyChunkEmissionCountdownTimer -= dt;
            if ( energyChunkEmissionCountdownTimer <= 0 ) {
                emitEnergyChunk();
                energyChunkEmissionCountdownTimer += ENERGY_CHUNK_EMISSION_PERIOD;
            }
            updateEnergyChunkPositions( dt );
            preLoadTime -= dt;
        }

        // Remove any chunks that actually made it to the solar panel.
        outgoingEnergyChunks.clear();
    }

    @Override public Energy getEnergyOutputRate() {
        return new Energy( EnergyType.LIGHT, EFACConstants.MAX_ENERGY_PRODUCTION_RATE * ( 1 - cloudiness.get() ) );
    }

    // Choose the angle for the emission of an energy chunk from the sun.
    // This uses history and probability to make the distribution somewhat
    // even but still random looking.
    private double chooseNextEmissionAngle() {

        int sector = sectorList.get( currentSectorIndex );
        currentSectorIndex++;
        if ( currentSectorIndex >= NUM_EMISSION_SECTORS ) {
            currentSectorIndex = 0;
        }

        // Angle is a function of the selected sector and a random offset
        // within the sector.
        return sector * EMISSION_SECTOR_SPAN + ( RAND.nextDouble() * EMISSION_SECTOR_SPAN ) + EMISSION_SECTOR_OFFSET;
    }

    @Override public void activate() {
        super.activate();
        // Pre-populate the space around the sun with energy chunks.  The
        // number of iterations is chosen carefully such that there chunks
        // that are close, but not quite reaching, the solar panel.
        for ( int i = 0; i < 100; i++ ) {
            stepInTime( EFACConstants.SIM_TIME_PER_TICK_NORMAL );
        }
    }

    @Override public void deactivate() {
        super.deactivate();
        cloudiness.reset();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
