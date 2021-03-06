// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SaltShakerNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.createPolar;
import static edu.colorado.phet.sugarandsaltsolutions.macro.model.SoluteModel.VOLUME_PER_SOLID_MOLE_SALT;

/**
 * Model element for the salt shaker, which includes its position and rotation and adds salt to the model when shaken.
 * Shaking (by acceleration and deceleration) along the axis produce salt.
 *
 * @author Sam Reid
 */
public abstract class SaltShaker<T extends SugarAndSaltSolutionModel> extends Dispenser<T> {

    //Some randomness in number of generated crystals when shaken
    private final Random random = new Random();

    //Keep track of how much the salt shaker was shaken, if so, then generate salt on the next updateModel() step
    private double shakeAmount;

    //Keep track of recorded positions when the shaker is translated so we can compute accelerations, which are responsible for shaking out the salt
    private final ArrayList<Vector2D> positions = new ArrayList<Vector2D>();

    public SaltShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, T model ) {
        super( x, y, Math.PI * 3 / 4, beaker, moreAllowed, name, distanceScale, selectedType, type, model );
        moreAllowed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean allowed ) {
                //If the shaker is emptied, prevent spurious grains from coming out the next time it is refilled by setting the shake amount to 0.0 and clearing the sampled positions
                if ( !allowed ) {
                    shakeAmount = 0;
                    positions.clear();
                }
            }
        } );
    }

    //Translate the dispenser by the specified delta in model coordinates
    public void translate( Dimension2D delta ) {
        super.translate( delta );

        //Only increment the shake amount if the shaker is non-empty, otherwise when it refills it might automatically emit salt even though the user isn't controlling it
        if ( moreAllowed.get() ) {
            //Add the new position to the list, but keep the list short so there is no memory leak
            positions.add( center.get() );
            while ( positions.size() > 50 ) {
                positions.remove( 0 );
            }

            //Make sure we have enough data, then compute accelerations of the shaker in the direction of its axis
            //to determine how much to shake out
            if ( positions.size() >= 20 ) {

                //Average the second derivatives
                Vector2D sum = new Vector2D();
                int numIterations = 10;
                for ( int i = 0; i < numIterations; i++ ) {
                    sum = sum.plus( getSecondDerivative( i ) );
                }
                sum = sum.times( 1.0 / numIterations );

                //But only take the component along the axis
                //Have to rotate by 90 degrees since for positions 0 degrees is to the right, but for the shaker 0 degrees is up
                double dist = Math.abs( sum.dot( createPolar( 1, angle.get() + Math.PI / 2 ) ) );

                //Account for the distance scale so we produce the same amount for micro translations as for macro translations
                dist = dist * distanceScale;

                //only add to the shake amount if it was vigorous enough
                if ( dist > 1E-4 ) {
                    shakeAmount += dist;
                }
            }
        }
    }

    //Called when the model steps in time, and adds any salt crystals to the sim if the dispenser is pouring
    public void updateModel() {
        //Check to see if we should be emitting salt crystals-- if the shaker was shaken enough
        if ( enabled.get() && shakeAmount > 0 && moreAllowed.get() ) {
            int numCrystals = (int) ( random.nextInt( 2 ) + Math.min( shakeAmount * 4000, 4 ) );
            for ( int i = 0; i < numCrystals; i++ ) {

                //Determine where the salt should come out
                //Hand tuned to match up with the image, will need to be re-tuned if the image changes
                double randUniform = ( random.nextDouble() - 0.5 ) * 2;
                final Vector2D outputPoint = center.get().plus( createPolar( dispenserHeight / 2 * 0.8, angle.get() - Math.PI / 2 + randUniform * Math.PI / 32 * 1.2 ) );

                //Add the salt to the model
                addSalt( model, outputPoint, VOLUME_PER_SOLID_MOLE_SALT, getCrystalVelocity( outputPoint ) );
                shakeAmount = 0.0;
                //don't clear the position array here since the user may still be shaking the shaker
            }
        }
    }

    //Adds the salt to the model
    protected abstract void addSalt( T model, final Vector2D outputPoint, double volumePerSolidMole, final Vector2D crystalVelocity );

    //Create a SaltShakerNode for display and interaction with this model element
    @Override public PNode createNode( ModelViewTransform transform, boolean micro, Function1<Point2D, Point2D> constraint ) {
        return new SaltShakerNode<T>( transform, this, micro, constraint );
    }

    @Override public void reset() {
        super.reset();
        //Additionally make it so it won't emit salt right after reset
        shakeAmount = 0.0;
        positions.clear();
    }

    //Estimate the acceleration at the specified point in the time series using centered difference approximation
    private Vector2D getSecondDerivative( int i ) {
        Vector2D x0 = positions.get( positions.size() - 1 - i );
        Vector2D x1 = positions.get( positions.size() - 2 - i );
        Vector2D x2 = positions.get( positions.size() - 3 - i );

        return x0.minus( x1.times( 2 ) ).plus( x2 );
    }
}