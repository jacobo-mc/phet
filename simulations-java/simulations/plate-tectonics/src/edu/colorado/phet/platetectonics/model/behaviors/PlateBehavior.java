// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.labels.RangeLabel;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.MagmaRegion;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

/**
 * Responsible for the model changes of a plate throughout its lifetime. Main behavior is placed in stepInTime( dt ). Many convenience
 * functions specified in this base class.
 */
public abstract class PlateBehavior {
    public final PlateMotionPlate plate;
    public final PlateMotionPlate otherPlate;

    // location just outside of view for the Plate Motion tab, so that we can create or remove crust from this location and the discrete changes are not visible
    public static final float PLATE_X_LIMIT = 700000;

    // all magma blobs
    protected final List<MagmaRegion> magmaBlobs = new ArrayList<MagmaRegion>();

    // where the magma blobs are heading towards
    protected Vector2F magmaTarget;

    // visual region where magma is being stored
    protected MagmaRegion magmaChamber;

    // how fast the magma moves
    protected float magmaSpeed;

    // reference to our tab's labels
    public final List<RangeLabel> rangeLabels;

    public PlateBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        this.plate = plate;
        this.otherPlate = otherPlate;

        rangeLabels = plate.getModel().rangeLabels;
    }

    // called after both behaviors have initialized
    public void afterConstructionInit() {

    }

    public abstract void stepInTime( float millionsOfYears );

    public PlateMotionPlate getOtherPlate() {
        return otherPlate;
    }

    public PlateMotionPlate getPlate() {
        return plate;
    }

    public Side getSide() {
        return getPlate().getSide();
    }

    public Side getOppositeSide() {
        return getSide().opposite();
    }

    public int getNumCrustXSamples() {
        return getCrust().getTopBoundary().samples.size();
    }

    public int getNumTerrainXSamples() {
        return getTerrain().getNumColumns();
    }

    public Terrain getTerrain() {
        return plate.getTerrain();
    }

    public Region getCrust() {
        return plate.getCrust();
    }

    public Region getLithosphere() {
        return plate.getLithosphere();
    }

    public Region[] getLithosphereRegions() {
        return new Region[]{getCrust(), getLithosphere()};
    }

    public Boundary getTopCrustBoundary() {
        return getCrust().getTopBoundary();
    }

    protected void createEarthEdges() {
        while ( getSide().opposite().isToSideOf(
                getPlate().getCrust().getTopBoundary().getEdgeSample( getSide() ).getPosition().x,
                PlateBehavior.PLATE_X_LIMIT * getSide().getSign() ) ) {
            getPlate().addSection( getSide(), plate.getPlateType() );
        }
    }

    protected void removeEarthEdges() {
        while ( getSide().isToSideOf(
                getPlate().getCrust().getTopBoundary().getEdgeSample( getSide() ).getPosition().x,
                PlateBehavior.PLATE_X_LIMIT * getSide().getSign() ) ) {
            getPlate().removeSection( getSide() );
        }
    }

    protected void moveMantleTopTo( float y ) {
        for ( Sample sample : getPlate().getMantle().getTopBoundary().samples ) {
            sample.shiftWithTexture( new Vector3F( 0, y - sample.getPosition().y, 0 ), plate.getTextureStrategy() );
        }
        redistributeMantle();
    }

    // currently kept because this is critically needed for mantle motion (which I hope is brought back to the sim)
    protected void glueMantleTopToLithosphere( float verticalPadding ) {
        int xIndex = 0;
        final Boundary lithosphereBottomBoundary = getPlate().getLithosphere().getBottomBoundary();
        Sample leftSample = lithosphereBottomBoundary.getEdgeSample( Side.LEFT );
        for ( Sample mantleSample : getPlate().getMantle().getTopBoundary().samples ) {
            // too far to the left
            if ( leftSample.getPosition().x > mantleSample.getPosition().x ) {
                continue;
            }

            int rightIndex = xIndex + 1;

            // too far to the right
            if ( rightIndex > lithosphereBottomBoundary.samples.size() - 1 ) {
                break;
            }
            Sample rightSample = lithosphereBottomBoundary.samples.get( rightIndex );
            while ( rightSample.getPosition().x < mantleSample.getPosition().x && rightIndex + 1 < lithosphereBottomBoundary.samples.size() ) {
                rightIndex++;
                rightSample = lithosphereBottomBoundary.samples.get( rightIndex );
            }

            // couldn't go far enough
            if ( rightSample.getPosition().x < mantleSample.getPosition().x ) {
                break;
            }
            leftSample = lithosphereBottomBoundary.samples.get( rightIndex - 1 );

            // how leftSample and rightSample surround our x
            assert leftSample.getPosition().x <= mantleSample.getPosition().x;
            assert rightSample.getPosition().x >= mantleSample.getPosition().x;

            // interpolate between their y values
            float ratio = ( mantleSample.getPosition().x - leftSample.getPosition().x ) / ( rightSample.getPosition().x - leftSample.getPosition().x );
            assert ratio >= 0;
            assert ratio <= 1;
            mantleSample.setPosition( new Vector3F( mantleSample.getPosition().x,
                                                    verticalPadding + leftSample.getPosition().y * ( 1 - ratio ) + rightSample.getPosition().y * ratio,
                                                    mantleSample.getPosition().z ) );
        }
    }

    protected void redistributeMantle() {
        Region mantle = getPlate().getMantle();
        // evenly distribute the asthenosphere mantle samples from top to bottom
        for ( int xIndex = 0; xIndex < mantle.getTopBoundary().samples.size(); xIndex++ ) {
            float topY = mantle.getTopBoundary().samples.get( xIndex ).getPosition().y;
            float bottomY = mantle.getBottomBoundary().samples.get( xIndex ).getPosition().y;

            // iterate over the interior boundaries (not including the top and bottom)
            for ( int yIndex = 1; yIndex < mantle.getBoundaries().size() - 1; yIndex++ ) {
                float ratioToBottom = ( (float) yIndex ) / ( (float) ( mantle.getBoundaries().size() - 1 ) );
                final Sample sample = mantle.getBoundaries().get( yIndex ).samples.get( xIndex );

                // interpolate Y between top and bottom
                final float newY = topY * ( 1 - ratioToBottom ) + bottomY * ratioToBottom;

                sample.shiftWithTexture( new Vector3F( 0, sample.getPosition().y - newY, 0 ), plate.getTextureStrategy() );
            }
        }
    }

    // override if we want to do something when this is removed
    protected void onMagmaRemoved( MagmaRegion magma ) {

    }

    public MagmaRegion addMagma( Vector2F position, float initialAlpha ) {
        Vector2F dirToTarget = magmaTarget.minus( position ).normalized();
        float angle = (float) Math.atan2( dirToTarget.y, dirToTarget.x );

        MagmaRegion magmaBlob = new MagmaRegion( plate.getTextureStrategy(), 1000, angle, 6, position );
        magmaBlob.alpha.set( initialAlpha );
        plate.regions.add( magmaBlob );
        magmaBlob.moveToFront();
        magmaChamber.moveToFront(); // keep the chamber in front
        magmaBlobs.add( magmaBlob );
        return magmaBlob;
    }

    public void addMagma( Vector2F position ) {
        addMagma( position, 0 );
    }

    protected void animateMagma( float millionsOfYears ) {
        // animate the magma blobs
        for ( MagmaRegion blob : new LinkedList<MagmaRegion>( magmaBlobs ) ) {
            animateMagmaBlob( blob, millionsOfYears, true );
        }
    }

    protected void animateMagmaBlob( MagmaRegion blob, float millionsOfYears, boolean reAdd ) {
        final Vector2F currentPosition = blob.position.get();
        final Vector2F directionToTarget = magmaTarget.minus( currentPosition ).normalized();
        final Vector2F newPosition = currentPosition.plus( directionToTarget.times( magmaSpeed * millionsOfYears ) );
        if ( newPosition.y > magmaTarget.y ) {
            // get rid of blob and create a new one
            assert plate.regions.contains( blob );
            plate.regions.remove( blob );
            assert !plate.regions.contains( blob );
            assert !plate.getModel().getRegions().contains( blob );
            magmaBlobs.remove( blob );
            blob.position.set( newPosition );
            if ( reAdd ) {
                onMagmaRemoved( blob );
            }
        }
        else {
            final float alphaSpeed = 0.25f;
            if ( blob.alpha.get() < 1 ) {
                blob.alpha.set( Math.min( 1, blob.alpha.get() + alphaSpeed * millionsOfYears ) );
            }
            blob.position.set( newPosition );
        }
    }

    /**
     * Allows us to somewhat fake timestep independence down to a certain precision by applying a non-analytical operation more
     * times at less of an amplitude when our timestep is large.
     *
     * @param callback The operation to call, with an sub-amount that is less than the cutoff value (but where the total sub-amounts sum to the total amount)
     * @param amount   Total amount passed to the operations. This is split into separate class
     * @param cutoff   Each operation will be called with a sub-amount less than this cutoff
     */
    public static void recursiveSplitCall( VoidFunction1<Float> callback, float amount, float cutoff ) {
        if ( amount > cutoff ) {
            recursiveSplitCall( callback, amount / 2, cutoff );
            recursiveSplitCall( callback, amount / 2, cutoff );
        }
        else {
            callback.apply( amount );
        }
    }
}
