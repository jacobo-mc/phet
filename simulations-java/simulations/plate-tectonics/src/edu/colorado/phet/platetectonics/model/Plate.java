// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.regions.Region;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.*;

public class Plate {
    private Region crust;
    private Region mantle;

    private Terrain terrain;

    public final ObservableList<Region> regions = new ObservableList<Region>();

    public void addCrust( Region crust ) {
        assert this.crust == null;

        this.crust = crust;
        regions.add( crust );
    }

    public void addMantle( Region mantle ) {
        assert this.mantle == null;

        this.mantle = mantle;
        regions.add( mantle );
    }

    public void addTerrain( final TextureStrategy textureStrategy, final int depthSamples, float minZ, float maxZ ) {
        final Region currentTopRegion = crust == null ? mantle : crust;
        terrain = new Terrain( depthSamples, minZ, maxZ ) {{
            for ( int xIndex = 0; xIndex < currentTopRegion.getTopBoundary().samples.size(); xIndex++ ) {
                final float x = currentTopRegion.getTopBoundary().samples.get( xIndex ).getPosition().x;
                final int finalXIndex = xIndex;
                addToRight( x, new ArrayList<TerrainSamplePoint>() {{
                    for ( int zIndex = 0; zIndex < depthSamples; zIndex++ ) {
                        final float z = zPositions.get( zIndex );
                        // elevation to be fixed later
                        add( new TerrainSamplePoint( currentTopRegion.getTopBoundary().samples.get( finalXIndex ).getPosition().y, textureStrategy.mapTop( new ImmutableVector2F( x, z ) ) ) );
                    }
                }} );
            }
        }};
    }

    public void shiftZ( float zOffset ) {
        for ( SamplePoint sample : getSamples() ) {
            sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( 0, 0, zOffset ) ) );
        }
        getTerrain().shiftZ( zOffset );
    }

    public List<SamplePoint> getSamples() {
        return unique( flatten( map( regions, new Function1<Region, Collection<? extends SamplePoint>>() {
            public Collection<? extends SamplePoint> apply( Region region ) {
                return region.getSamples();
            }
        } ) ) );
    }

    public Region getCrust() {
        return crust;
    }

    public Region getMantle() {
        return mantle;
    }

    public Terrain getTerrain() {
        return terrain;
    }
}
