/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.theramp.RampObject;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:12:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampModel implements ModelElement, Surface.CollisionListener {
    private Surface ground;
    private Surface ramp;
    private Block block;
    private ForceVector wallForce;
    private ForceVector appliedForce;
    private ForceVector gravityForce;
    private ForceVector totalForce;
    private ForceVector frictionForce;
    private ForceVector normalForce;
    private double gravity = 9.8;
    private double appliedWork = 0.0;
    private double frictiveWork = 0.0;
    private double gravityWork = 0.0;
    private double zeroPointY = 0.0;
    private double thermalEnergy = 0.0;

    private boolean userAddingEnergy = false;
    private ArrayList listeners = new ArrayList();
    private SimpleObservable peObservers = new SimpleObservable();
    private SimpleObservable keObservers = new SimpleObservable();
    private double lastTick;
    private double lastRampAngle = 0.0;

    ModelElement stepStrategy;
    private double originalBlockKE;
    private double lastPotentialEnergy;
    private double lastTotalEnergy;

//    private double rampAngleSavValue;

    public RampModel() {
        ramp = new Ramp( Math.PI / 32, 15.0 );
        ramp.addCollisionListener( this );
        ground = new Ground( 0, 6, -6, 0, 0 );
        ramp.setDistanceOffset( ground.getLength() );
        ground.addCollisionListener( this );
        block = new Block( ramp );
        wallForce = new ForceVector();
        gravityForce = new ForceVector();
        totalForce = new ForceVector();
        frictionForce = new ForceVector();
        appliedForce = new ForceVector();
        normalForce = new ForceVector();
//        setStepStrategy( new RampModel.OriginalStepCode() );
        setStepStrategy( new RampModel.NewStepCode() );
    }

    public void setStepStrategy( ModelElement stepStrategy ) {
        this.stepStrategy = stepStrategy;
    }

    public Surface getRamp() {
        return ramp;
    }

    public Block getBlock() {
        return block;
    }

    public double currentTimeSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

    public void stepInTime( double dt ) {
        stepStrategy.stepInTime( dt );
    }

//    private void originalStepCode( double dt ) {
////        double dt;
//        if( lastTick != 0.0 ) {
//            dt = currentTimeSeconds() - lastTick;
//            dt = MathUtil.clamp( 1 / 30.0, dt, 1 / 5.0 );
//
//            double origBlockPosition = block.getPositionInSurface();
//            double origBlockEnergy = block.getKineticEnergy();
//            double origPotEnergy = getPotentialEnergy();
//            double origMech = getMechanicalEnergy();
//
//            gravityForce.setX( 0 );
//            gravityForce.setY( gravity * block.getMass() );
//            double fa = block.getFrictionForce( gravity, appliedForce.getParallelComponent() + gravityForce.getParallelComponent() );
//            frictionForce.setParallel( fa );
//
//            double force = appliedForce.getParallelComponent() + gravityForce.getParallelComponent() + frictionForce.getParallelComponent();
//            normalForce.setPerpendicular( gravityForce.getPerpendicularComponent() );
//
//            double wallForce = getSurface().getWallForce( force, getBlock() );
//            force += wallForce;
//            this.wallForce.setParallel( wallForce );
//
//            totalForce.setParallel( force );
//            double acceleration = force / block.getMass();
//            block.setAcceleration( acceleration );
//            block.stepInTime( this, dt );
//
//            double newBlockPosition = block.getPositionInSurface();
//            double blockDX = newBlockPosition - origBlockPosition;
//            double dAppliedWork = ( appliedForce.getParallelComponent() * blockDX );
//            double dFrictiveWork = ( frictionForce.getParallelComponent() * blockDX );
//            double dGravityWork = ( gravityForce.getParallelComponent() * blockDX );
//            appliedWork += dAppliedWork;
//            frictiveWork += dFrictiveWork;
//            gravityWork += dGravityWork;
//            double newKE = block.getKineticEnergy();
//            if( newKE != origBlockEnergy ) {
//                keObservers.notifyObservers();
//            }
//            double newPE = getPotentialEnergy();
//            if( newPE != origPotEnergy ) {
//                peObservers.notifyObservers();
//            }
//            if( userIsAddingEnergy() ) {
//                thermalEnergy += Math.abs( dFrictiveWork );//this is close, but not exact.
//            }
//            else {
//                double finalMech = getMechanicalEnergy();
//                double dE = Math.abs( finalMech ) - Math.abs( origMech );
//                if( dE <= 0 ) {
//                    thermalEnergy += Math.abs( dE );
//                }
//                else {
////                    new RuntimeException( "Gained Energy, errTotal=" + errTotal ).printStackTrace();
//                    String message = "Gained Energy, errTotal=" + dE;
//                    System.out.println( "message = " + message );
////                    new RuntimeException( message ).printStackTrace();
//                }
//            }
//            //So height of totalEnergy bar should always be same as height W_app bar
//            double errTotal = getTotalEnergy() - getAppliedWork();
//            if( errTotal != 0.0 ) {
//                System.out.println( "errTotal=" + errTotal + ", EnergyTotal=" + getTotalEnergy() + ", WorkApplied=" + getAppliedWork() );
//            }
//            //deltaKE = W_net
//            double dK = getBlock().getKineticEnergy() - getTotalWork();
//            if( dK != 0.0 ) {
//                System.out.println( "dK=" + dK + ", Delta KE=" + getBlock().getKineticEnergy() + ", Net Work=" + getTotalWork() );
//            }
//        }
//        lastTick = currentTimeSeconds();
//    }


    private void newStepCode( double dt ) {
//        double dt;
        if( lastTick != 0.0 ) {
            dt = currentTimeSeconds() - lastTick;
            dt = MathUtil.clamp( 1 / 30.0, dt, 1 / 5.0 );

            RampModel orig = getState();
            setupForces();
            updateBlock( dt );

            double blockDX = getBlockPosition() - orig.getBlockPosition();
            if( getDTheta( orig ) != 0.0 ) {
                System.out.println( "RampModel.newStepCode" );
            }
//            double energyAddedByRaisingRamp = getEnergyAddedByRaisingRamp( orig );
//            if( energyAddedByRaisingRamp != 0.0 ) {
//                System.out.println( "**************energyAddedByRaisingRamp = " + energyAddedByRaisingRamp );
//            }

            double addedEnergy = appliedForce.getParallelComponent() * blockDX;
            thermalEnergy += addedEnergy - ( getMechanicalEnergy() - orig.getMechanicalEnergy() );
            appliedWork += ( getTotalEnergy() - lastTotalEnergy );
            frictiveWork += -( getThermalEnergy() - orig.getThermalEnergy() );
            gravityWork += -( getPotentialEnergy() - lastPotentialEnergy );

            //So height of totalEnergy bar should always be same as height W_app bar
            double dE = getTotalEnergy() - getAppliedWork();
            if( Math.abs( dE ) > 1.0E-9 ) {
                System.out.println( "dE=" + dE + ", EnergyTotal=" + getTotalEnergy() + ", WorkApplied=" + getAppliedWork() );
            }
            //deltaKE = W_net
            double dK = getBlock().getKineticEnergy() - getTotalWork();
            if( Math.abs( dK ) > 1.0E-9 ) {
                System.out.println( "dK=" + dK + ", Delta KE=" + getBlock().getKineticEnergy() + ", Net Work=" + getTotalWork() );
            }

            if( block.getKineticEnergy() != orig.getBlock().getKineticEnergy() ) {
                keObservers.notifyObservers();
            }

            if( getPotentialEnergy() != orig.getPotentialEnergy() ) {
                peObservers.notifyObservers();
            }
        }
        lastTick = currentTimeSeconds();
        lastRampAngle = getRamp().getAngle();
        lastPotentialEnergy = getPotentialEnergy();
        lastTotalEnergy = getTotalEnergy();
    }

    private double getDTheta( RampModel orig ) {
        Surface origSurf = orig.getBlock().getSurface();
        Surface currSurf = getBlock().getSurface();
        System.out.println( "origAng=" + origSurf.getAngle() + ", newAng=" + currSurf.getAngle() );

        if( origSurf.getClass().equals( currSurf.getClass() ) ) {
//            System.out.println( "Same Surface" );
            return ramp.getAngle() - lastRampAngle;
//            return currSurf.getAngle() - origSurf.getAngle();
        }
        else {
//            System.out.println( "Different Surface" );
            return 0.0;
        }
    }

    private double getEnergyAddedByRaisingRamp( RampModel orig ) {
        double dTheta = getDTheta( orig );
        if( dTheta != 0.0 ) {
            double errOrig = orig.getTotalEnergy() - orig.getAppliedWork();
            System.out.println( "dTheta = " + dTheta );
            System.out.println( "errOrig = " + errOrig );
            return errOrig;
        }
        else {
            return 0.0;
        }
    }

    private void updateBlock( double dt ) {
        double acceleration = totalForce.getParallelComponent() / block.getMass();
        block.setAcceleration( acceleration );
        originalBlockKE = block.getKineticEnergy();
        block.stepInTime( this, dt ); //could fire a collision event.
    }

    private void setupForces() {

        gravityForce.setX( 0 );
        gravityForce.setY( gravity * block.getMass() );
        double fa = block.getFrictionForce( gravity, appliedForce.getParallelComponent() + gravityForce.getParallelComponent() );
        frictionForce.setParallel( fa );

        double netForce = appliedForce.getParallelComponent() + gravityForce.getParallelComponent() + frictionForce.getParallelComponent();
        normalForce.setPerpendicular( gravityForce.getPerpendicularComponent() );

        double wallForce = getSurface().getWallForce( netForce, getBlock() );
        netForce += wallForce;

        this.wallForce.setParallel( wallForce );
        totalForce.setParallel( netForce );


    }

    private double getBlockPosition() {
        return getBlock().getPosition();
    }

//        private void newStepCode( double dt ) {
////        double dt;
//        if( lastTick != 0.0 ) {
//            dt = currentTimeSeconds() - lastTick;
//            dt = MathUtil.clamp( 1 / 30.0, dt, 1 / 5.0 );
//
//            double origBlockPosition = block.getPosition();
//            double origBlockEnergy = block.getKineticEnergy();
//            double origPotEnergy = getPotentialEnergy();
//            double origMech = getMechanicalEnergy();
//
//            gravityForce.setX( 0 );
//            gravityForce.setY( gravity * block.getMass() );
//            double fa = block.getFrictionForce( gravity, appliedForce.getParallelComponent() + gravityForce.getParallelComponent() );
//            frictionForce.setParallel( fa );
//
//            double force = appliedForce.getParallelComponent() + gravityForce.getParallelComponent() + frictionForce.getParallelComponent();
//            normalForce.setPerpendicular( gravityForce.getPerpendicularComponent() );
//
//            double wallForce = getSurface().getWallForce( force, getBlock() );
//            force += wallForce;
//            this.wallForce.setParallel( wallForce );
//
//            totalForce.setParallel( force );
//            double acceleration = force / block.getMass();
//            block.setAcceleration( acceleration );
//            originalBlockKE = block.getKineticEnergy();
//            block.stepInTime( this, dt ); //could fire a collision event.
//
//            double newKE = block.getKineticEnergy();
//            if( newKE != origBlockEnergy ) {
//                keObservers.notifyObservers();
//            }
//            double newPE = getPotentialEnergy();
//            if( newPE != origPotEnergy ) {
//                peObservers.notifyObservers();
//            }
//
//            double totalWork = newKE;
//            double blockDX = block.getPosition() - origBlockPosition;
//            double dFrictiveWork = ( frictionForce.getParallelComponent() * blockDX );
//            frictiveWork += dFrictiveWork;
//
//            thermalEnergy -= dFrictiveWork;
//            double totalEnergy = getTotalEnergy();
//            appliedWork = totalEnergy;
//            gravityWork = totalWork - appliedWork - frictiveWork;
////            System.out.println( "getPotentialEnergy() = " + getPotentialEnergy() );
////            if( userIsAddingEnergy() ) {
////                thermalEnergy += Math.abs( dFrictiveWork );//this is close, but not exact.
////            }
////            else {
////                double finalMech = getMechanicalEnergy();
////                double dE = Math.abs( finalMech ) - Math.abs( origMech );
////                if( dE <= 0 ) {
////                    thermalEnergy += Math.abs( dE );
////                }
////                else {
//////                    new RuntimeException( "Gained Energy, dE=" + dE ).printStackTrace();
////                    String message = "Gained Energy, dE=" + dE;
////                    System.out.println( "message = " + message );
//////                    new RuntimeException( message ).printStackTrace();
////                }
////            }
//            //So height of totalEnergy bar should always be same as height W_app bar
//            double dE = getTotalEnergy() - getAppliedWork();
//            if( Math.abs( dE ) > 1.0E-9 ) {
//                System.out.println( "dE=" + dE + ", EnergyTotal=" + getTotalEnergy() + ", WorkApplied=" + getAppliedWork() );
//            }
//            //deltaKE = W_net
//            double dK = getBlock().getKineticEnergy() - getTotalWork();
//            if( Math.abs( dK ) > 1.0E-9 ) {
//                System.out.println( "dK=" + dK + ", Delta KE=" + getBlock().getKineticEnergy() + ", Net Work=" + getTotalWork() );
//            }
//
//        }
//        lastTick = currentTimeSeconds();
//    }

    private double getMechanicalEnergy() {
        return block.getKineticEnergy() + getPotentialEnergy();
    }

    private boolean userIsAddingEnergy() {
        return userAddingEnergy;
    }

    public void setUserIsAddingEnergy( boolean userAddingEnergy ) {
        this.userAddingEnergy = userAddingEnergy;
    }

    public double getPotentialEnergy() {
        double height = getBlockHeight();
        return block.getMass() * height * gravity;
    }

    private double getBlockHeight() {
        return block.getLocation2D().getY() - zeroPointY;
    }

    public void setAppliedForce( double appliedForce ) {
        this.appliedForce.setParallel( appliedForce );
        notifyAppliedForceChanged();
    }

    private void notifyAppliedForceChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.appliedForceChanged();
        }
    }

    public void addKEObserver( SimpleObserver simpleObserver ) {
        keObservers.addObserver( simpleObserver );
    }

    public void addPEObserver( SimpleObserver simpleObserver ) {
        peObservers.addObserver( simpleObserver );
    }

    public ForceVector getWallForce() {
        return wallForce;
    }

    public ForceVector getAppliedForce() {
        return appliedForce;
    }

    public ForceVector getGravityForce() {
        return gravityForce;
    }

    public ForceVector getTotalForce() {
        return totalForce;
    }

    public ForceVector getFrictionForce() {
        return frictionForce;
    }

    public ForceVector getNormalForce() {
        return normalForce;
    }

    public void reset() {
        block.setSurface( ramp );
        block.setPositionInSurface( 10.0 );
        block.setAcceleration( 0.0 );
        block.setVelocity( 0.0 );
        ramp.setAngle( Math.PI / 16 );
        appliedWork = 0;
        frictiveWork = 0;
        gravityWork = 0;
        thermalEnergy = 0.0;
        peObservers.notifyObservers();
        keObservers.notifyObservers();
        initWorks();
    }

    public void initWorks() {
        gravityWork = -getPotentialEnergy();
        appliedWork = -gravityWork;
    }

    public double getFrictiveWork() {
        return frictiveWork;
    }

    public double getGravityWork() {
        return gravityWork;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setZeroPointY( double zeroPointY ) {
        this.zeroPointY = zeroPointY;
        //TODO updates.
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.zeroPointChanged();
        }
        peObservers.notifyObservers();
    }

    public double getZeroPointY() {
        return zeroPointY;
    }

    public double getThermalEnergy() {
        return thermalEnergy;//TODO good code
    }

    public double getTotalEnergy() {
        return getPotentialEnergy() + getBlock().getKineticEnergy() + getThermalEnergy();
    }

    public Surface getGround() {
        return ground;
    }


    private Surface getSurface() {
        return block.getSurface();
    }

    public void setMass( double value ) {
        block.setMass( value );
    }

    public void setObject( RampObject rampObject ) {
        getBlock().setMass( rampObject.getMass() );
        getBlock().setStaticFriction( rampObject.getStaticFriction() );
        getBlock().setKineticFriction( rampObject.getKineticFriction() );
    }

    public void setStepStrategyEmergent() {
        setStepStrategy( new OriginalStepCode() );
    }

    public void setStepStrategyConstrained() {
        setStepStrategy( new NewStepCode() );
    }

    public void collided( Surface surface ) {
//        double changeInEnergy = Math.abs( block.getKineticEnergy() - originalBlockKE );
//
//        thermalEnergy += changeInEnergy;
//        frictiveWork -= changeInEnergy;
    }

    public class ForceVector extends Vector2D.Double {

        public void setParallel( double parallel ) {
            setX( Math.cos( -getSurface().getAngle() ) * parallel );
            setY( Math.sin( -getSurface().getAngle() ) * parallel );
//            System.out.println( "parallel = " + parallel + " magnitude=" + getMagnitude() );
        }

        public double getParallelComponent() {
//            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -ramp.getAngle() );
            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -getSurface().getAngle() );
            double result = dir.dot( this );
            return result;
        }

        public double getPerpendicularComponent() {
//            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -ramp.getAngle() );
            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -getSurface().getAngle() );
            dir = dir.getNormalVector();
            double result = dir.dot( this );
            return result;
        }

        public void setPerpendicular( double perpendicularComponent ) {
            setX( Math.sin( getSurface().getAngle() ) * perpendicularComponent );
            setY( Math.cos( getSurface().getAngle() ) * perpendicularComponent );
//            System.out.println( "perp= " + perpendicularComponent + " magnitude=" + getMagnitude() );
        }

        public Vector2D toParallelVector() {
            ForceVector fv = new ForceVector();
            fv.setParallel( getParallelComponent() );
            return fv;
        }

        public Vector2D toPerpendicularVector() {
            ForceVector fv = new ForceVector();
            fv.setPerpendicular( -getPerpendicularComponent() );
            return fv;
        }

        public Vector2D toXVector() {
            return new Vector2D.Double( getX(), 0 );
        }

        public Vector2D toYVector() {
            return new Vector2D.Double( 0, getY() );
        }

        public ForceVector copyState() {
            ForceVector copy = new ForceVector();
            copy.setX( getX() );
            copy.setY( getY() );
            return copy;
        }

        public void setState( ForceVector state ) {
            setX( state.getX() );
            setY( state.getY() );
        }
    }

    public double getAppliedWork() {
        return appliedWork;
    }

    public static interface Listener {
        public void appliedForceChanged();

        void zeroPointChanged();
    }

    //could maybe generalize with reflection.
    public RampModel getState() {
        RampModel copy = new RampModel();
        copy.ramp = ramp.copyState();
        copy.ground = ground.copyState();
        copy.block = block.copyState( this, copy );
        copy.wallForce = wallForce.copyState();
        copy.appliedForce = appliedForce.copyState();
        copy.gravityForce = gravityForce.copyState();
        copy.totalForce = totalForce.copyState();
        copy.frictionForce = frictionForce.copyState();
        copy.normalForce = normalForce.copyState();
        copy.gravity = gravity;
        copy.appliedWork = appliedWork;
        copy.frictiveWork = frictiveWork;
        copy.gravityWork = gravityWork;
        copy.zeroPointY = zeroPointY;
        copy.thermalEnergy = thermalEnergy;
        return copy;
    }

    public void setState( RampModel state ) {
        ramp.setState( state.getRamp() );
        block.setState( state.getBlock() );
        wallForce.setState( state.wallForce );
        appliedForce.setState( state.appliedForce );
        gravityForce.setState( state.gravityForce );
        totalForce.setState( state.totalForce );
        frictionForce.setState( state.frictionForce );
        normalForce.setState( state.normalForce );
        gravity = state.gravity;
        appliedWork = state.appliedWork;
        frictiveWork = state.frictiveWork;
        gravityWork = state.gravityWork;
        zeroPointY = state.zeroPointY;
        thermalEnergy = state.thermalEnergy;

        //todo notify observers.
    }

    public double getTotalWork() {
        return getGravityWork() + getFrictiveWork() + getAppliedWork();
    }

    public OriginalStepCode getOriginalStepCode() {
        return new OriginalStepCode();
    }

    public NewStepCode getNewStepCode() {
        return new NewStepCode();
    }

    public class OriginalStepCode implements ModelElement {
        public void stepInTime( double dt ) {
//            originalStepCode( dt );
        }
    }

    public class NewStepCode implements ModelElement {
        public void stepInTime( double dt ) {
            newStepCode( dt );
        }
    }
}