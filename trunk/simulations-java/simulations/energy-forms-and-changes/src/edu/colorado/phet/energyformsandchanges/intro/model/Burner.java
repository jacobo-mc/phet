// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * Model element that represents a burner in the simulation.  The burner can
 * heat and also cool other model elements.
 *
 * @author John Blanco
 */
public class Burner extends ModelElement implements ThermalEnergyContainer {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.075; // In meters.
    private static final double HEIGHT = WIDTH;
    private static final double MAX_ENERGY_GENERATION_RATE = 3000; // joules/sec TODO: Needs tweaking.

    // Constants that define the energy transfer behavior.  This is modeled as
    // though there was a block just above the burner, and it heats up, and
    // then transfers energy to anything on top of it and/or to the surrounding
    // air.
    private static final double ENERGY_TRANSFER_AREA_WIDTH = WIDTH / 2;
    private static final double ENERGY_TRANSFER_AREA_HEIGHT = ENERGY_TRANSFER_AREA_WIDTH;
    private static final double DENSITY = 11300; // In kg/m^3  TODO: Not sure what to use for this.
    private static final double MASS = ENERGY_TRANSFER_AREA_WIDTH * ENERGY_TRANSFER_AREA_WIDTH * ENERGY_TRANSFER_AREA_HEIGHT * DENSITY;
    private static final double SPECIFIC_HEAT = 129; // In J/kg-K
    private static final double INITIAL_ENERGY = MASS * SPECIFIC_HEAT * EFACConstants.ROOM_TEMPERATURE;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final ImmutableVector2D position;

    // Property that is used to control the amount of heating or cooling that
    // is being done.
    public final Property<Double> heatCoolLevel = new Property<Double>( 0.0 );
    private Property<HorizontalSurface> topSurface;

    private double energy = INITIAL_ENERGY;

    private final BooleanProperty energyChunksVisible;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param clock
     * @param position The position in model space where this burner exists.
     *                 By convention for this simulation, the position is
     */
    public Burner( ConstantDtClock clock, ImmutableVector2D position, BooleanProperty energyChunksVisible ) {
        this.position = new ImmutableVector2D( position );
        this.energyChunksVisible = energyChunksVisible;
        topSurface = new Property<HorizontalSurface>( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ), getOutlineRect().getMaxY(), this ) );
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a rectangle that defines the outline of the burner.  In the model,
     * the burner is essentially a 2D rectangle.
     *
     * @return
     */
    public Rectangle2D getOutlineRect() {
        return new Rectangle2D.Double( position.getX() - WIDTH / 2,
                                       position.getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    public void updateInternallyProducedEnergy( double dt ) {
        energy += heatCoolLevel.get() * MAX_ENERGY_GENERATION_RATE * dt;
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public void exchangeEnergyWith( ThermalEnergyContainer energyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( energyContainer.getThermalContactArea() );

        if ( thermalContactLength > 0 ) {

            // The burner is in contact with this item.  Exchange energy.
            if ( Math.abs( energyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between the this and the other energy container.
                // TODO: The following is a first attempt and likely to need much adjustment.
                double thermalEnergyGained = ( energyContainer.getTemperature() - getTemperature() ) * thermalContactLength * 2000 * dt;
                changeEnergy( thermalEnergyGained );
                energyContainer.changeEnergy( -thermalEnergyGained );
            }

            // See if the other element needs energy chunks.
            if ( energyContainer.needsEnergyChunk() ) {
                // Create an energy chunk and give it to the model element.
                // TODO: Probably need a random algorithm that defines where energy chunks originate.
                energyContainer.addEnergyChunk( new EnergyChunk( position, energyChunksVisible ) );
            }
        }
    }

    public boolean needsEnergyChunk() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasExcessEnergyChunks() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public EnergyChunk removeEnergyChunk() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ThermalContactArea getThermalContactArea() {
        // The thermal contact area for the burner a rectangular space that is
        // intended to be just above the top of the burner.  This has to be
        // coordinated a bit with the view.
        Rectangle2D burnerRect = getOutlineRect();
        Rectangle2D thermalContactAreaRect = new Rectangle2D.Double( burnerRect.getCenterX() - ENERGY_TRANSFER_AREA_WIDTH / 2,
                                                                     burnerRect.getCenterY(),
                                                                     ENERGY_TRANSFER_AREA_WIDTH,
                                                                     ENERGY_TRANSFER_AREA_HEIGHT );
        return new ThermalContactArea( thermalContactAreaRect, true );
    }

    public double getTemperature() {
        return energy / ( MASS * SPECIFIC_HEAT );
    }

    @Override public void reset() {
        super.reset();
        energy = INITIAL_ENERGY;
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void stepInTime( double dt ) {
        updateInternallyProducedEnergy( dt );
    }
}
