// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class WaterTowerModel extends FluidPressureAndFlowModel implements VelocitySensor.Context {

    private WaterTower waterTower = new WaterTower();
    private ArrayList<WaterDrop> particles = new ArrayList<WaterDrop>();
    private ArrayList<VoidFunction1<WaterDrop>> dropAddedListeners = new ArrayList<VoidFunction1<WaterDrop>>();
    private Random random = new Random();
    private FaucetFlowLevel faucetFlowLevel = new FaucetFlowLevel();
    private double g = 9.8;

    public WaterTowerModel() {
        addPressureSensor( new PressureSensor( this, 0, 0 ) );
        addVelocitySensor( new VelocitySensor( this, 0, 0 ) );
        getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double remainingVolume = waterTower.fluidVolume.getValue();
                if ( waterTower.isHoleOpen() && remainingVolume > 0 ) {
                    double dropVolume = remainingVolume > 1 ? 1 : remainingVolume;
                    double velocity = Math.sqrt( 2 * g * waterTower.getWaterHeight() );//Toricelli's theorem, one of the main learning goals of this tab
                    final WaterDrop drop = new WaterDrop(
                            new ImmutableVector2D( waterTower.getHoleLocation().getX() + random.nextDouble() * 0.1, waterTower.getHoleLocation().getY() + random.nextDouble() * 0.1 ),
                            new ImmutableVector2D( velocity, 0 ),
                            dropVolume );
                    particles.add( drop );
                    for ( int i = 0; i < dropAddedListeners.size(); i++ ) {dropAddedListeners.get( i ).apply( drop );}
                    waterTower.setFluidVolume( waterTower.fluidVolume.getValue() - drop.getVolume() );
                }
                for ( int i = 0; i < particles.size(); i++ ) {
                    particles.get( i ).stepInTime( clockEvent.getSimulationTimeChange() );
                }
            }
        } );
    }

    public void addDropAddedListener( VoidFunction1<WaterDrop> dropAddedListener ) {
        dropAddedListeners.add( dropAddedListener );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        return new ImmutableVector2D();
    }

    public void addVelocityUpdateListener( SimpleObserver observer ) {

    }

    public WaterTower getWaterTower() {
        return waterTower;
    }

    public FaucetFlowLevel getFaucetFlowLevel() {
        return faucetFlowLevel;
    }
}
