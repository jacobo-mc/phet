/*
 * Class: GasMolecule
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public abstract class GasMolecule extends IdealGasParticle {

    public static Point2D getCm( List instances ) {
        Point2D cm = new Point2D.Double();
        for( int i = 0; i < instances.size(); i++ ) {
            GasMolecule molecule = (GasMolecule)instances.get( i );
            cm.setLocation( cm.getX() + molecule.getPosition().getX(),
                            cm.getY() + molecule.getPosition().getY() );
        }
        if( instances.size() != 0 ) {
            cm.setLocation( cm.getX() / instances.size(),
                            cm.getY() / instances.size() );
        }
        return cm;
    }

    // List of contraints that must be applied to the IdealGasParticle's state
    // at the end of each stepInTime
    protected ArrayList constraints = new ArrayList();
    // Working copy of constraint list used in case a constraint
    // needs to modify the real constraint list
    private ArrayList workingList = new ArrayList();

    // List of GasMolecule.Observers
    private ArrayList observers = new ArrayList();

    public interface Observer extends SimpleObserver {
        void removedFromSystem();
    }

    public GasMolecule( Point2D position, Vector2D velocity, Vector2D acceleration, float mass ) {
        super( position, velocity, acceleration, mass );
    }

    public GasMolecule( Point2D position, Vector2D velocity, Vector2D acceleration,
                        float mass, float charge ) {
        super( position, velocity, acceleration, mass, charge );
    }

    public void addObserver( GasMolecule.Observer observer ) {
        observers.add( observer );
        this.addObserver( (SimpleObserver)observer );
    }

    public void removeObserver( GasMolecule.Observer observer ) {
        observers.remove( observer );
    }

    public void setVelocity( Vector2D velocity ) {
        super.setVelocity( velocity );
    }

    public void setVelocity( double vX, double vY ) {
        super.setVelocity( vX, vY );
    }

    public void setVelocityX( double vX ) {
        super.setVelocity( vX, getVelocity().getY() );
    }

    public void setVelocityY( double vY ) {
        super.setVelocity( getVelocity().getX(), vY );
    }

    public double getRadius() {
        return s_moleculeRadius;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        // Iterate the receiver's constraints. We iterate a copy of the list, in case
        // any of the constraints need to add or remove constraints from the list
        workingList.clear();
        workingList.addAll( constraints );
        for( Iterator iterator = workingList.iterator(); iterator.hasNext(); ) {
            Constraint constraintSpec = (Constraint)iterator.next();
            constraintSpec.apply();
        }
    }

    //
    // Static fields and methods
    //
    private static float s_moleculeRadius = 5.0f;

    public static void enableParticleParticleInteractions( boolean interactionsEnabled ) {
        if( interactionsEnabled ) {
            s_moleculeRadius = 5.0f;
        }
        else {
            s_moleculeRadius = 0.0f;
        }
    }

    /**
     *
     */
    public static Double computeTemperature( List molecules ) {
        double kineticEnergy = 0;
        boolean init = false;

        Double result = null;
        if( molecules.isEmpty() ) {
            result = new Double( 0.0 );
        }
        else {
            for( int i = 0; i < molecules.size(); i++ ) {
                GasMolecule body = (GasMolecule)molecules.get( i );

                // Get a reference to the box and gravity.
                if( !init ) {
                    init = true;
                    //                    if( body.getPhysicalSystem() == null ) {
                    //                        System.out.println( "body has no physical system assigned: " + body.getClass() );
                    //                    }
                }
                kineticEnergy += body.getKineticEnergy();
            }
            result = new Double( kineticEnergy / molecules.size() );
        }
        return result;
    }

    /**
     * todo: This should be done by the IdealGasModel, because it knows about
     * gravity
     */
    public static Double getTotalEnergy( List molecules, IdealGasModel idealGasModel ) {
        double kineticEnergy = 0;
        double potentialEnergy = 0;
        //        Gravity gravity = null;

        Double result = null;
        if( molecules.isEmpty() ) {
            result = new Double( 0.0 );
        }
        else {
            synchronized( molecules ) {

                // Get a reference to the box and gravity.
                //                gravity = ( (IdealGasSystem)PhysicalSystem.instance() ).getGravity();
                //                isGravityActing = ( gravity != null ) && ( gravity.getY() != 0 );

                for( int i = 0; i < molecules.size(); i++ ) {
                    GasMolecule body = (GasMolecule)molecules.get( i );

                    potentialEnergy += idealGasModel.getBodyEnergy( body );
                    //                    potentialEnergy += PhysicalSystem.instance().getBodyEnergy( body );
                    /*
                                        kineticEnergy += body.getKineticEnergy();
                                        if( isGravityActing ) {
                                            potentialEnergy += body.getMass() * gravity.getY() * ( box.getMaxY() - body.getPosition().getY() );
                                        }
                    */
                }
            }
            result = new Double( kineticEnergy + potentialEnergy );
        }
        return result;
    }

    /**
     * TODO: put this and getTemperature in stepInTime(), along with the pressure determination, so
     * that we only have to iterate the list of bodies once
     */
    protected static Double computeAveSpeed( List molecules ) {
        double totalSpeed = 0;
        int denominator = 0;
        if( molecules.isEmpty() ) {
            totalSpeed = 0.0;
        }
        else {
            synchronized( molecules ) {
                for( int i = 0; i < molecules.size(); i++ ) {
                    GasMolecule body = (GasMolecule)molecules.get( i );
                    totalSpeed += body.getSpeed();
                }
            }
            denominator = ( molecules.isEmpty() ? 1 : molecules.size() );
        }
        return new Double( totalSpeed / denominator );
    }

    //
    // Constraint related methods
    //
    public void addConstraint( Constraint constraintSpec ) {
        constraints.add( constraintSpec );
    }

    public void removeConstraint( Constraint constraintSpec ) {
        constraints.remove( constraintSpec );
    }


    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        super.setPosition( position );
    }

    public void removeYourselfFromSystem() {
        for( int i = 0; i < observers.size(); i++ ) {
            Observer observer = (Observer)observers.get( i );
            observer.removedFromSystem();
        }
    }
}
