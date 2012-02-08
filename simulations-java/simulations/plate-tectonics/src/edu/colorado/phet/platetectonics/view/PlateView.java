// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 */
public class PlateView extends GLNode {

    private final PlateTectonicsTab tab;

    // by default, show water
    public PlateView( final PlateModel model, final PlateTectonicsTab tab ) {
        this( model, tab, new Property<Boolean>( true ) );
    }

    public PlateView( final PlateModel model, final PlateTectonicsTab tab, final Property<Boolean> showWater ) {
        this.tab = tab;

        for ( Terrain strip : model.getTerrains() ) {
            addChild( new TerrainStripNode( strip, tab.getModelViewTransform() ) );

            if ( strip.hasWater() ) {
                final WaterStripNode waterNode = new WaterStripNode( strip, model, tab );
                showWater.addObserver( new SimpleObserver() {
                    public void update() {
                        if ( showWater.get() ) {
                            addChild( waterNode );
                        }
                        else {
                            if ( waterNode.getParent() != null ) {
                                removeChild( waterNode );
                            }
                        }
                    }
                } );
            }
        }

        for ( CrossSectionStrip strip : model.getCrossSectionStrips() ) {
            addChild( new CrossSectionStripNode( tab.getModelViewTransform(), tab.colorMode, strip ) );
        }

        model.crossSectionStripAdded.addListener( new VoidFunction1<CrossSectionStrip>() {
            public void apply( CrossSectionStrip strip ) {
                addChild( new CrossSectionStripNode( tab.getModelViewTransform(), tab.colorMode, strip ) );
            }
        } );
        model.terrainStripAdded.addListener( new VoidFunction1<Terrain>() {
            public void apply( Terrain strip ) {
                addChild( new TerrainStripNode( strip, tab.getModelViewTransform() ) );
            }
        } );

        // TODO: handle region removals in a better way
        model.crossSectionStripRemoved.addListener( new VoidFunction1<CrossSectionStrip>() {
            public void apply( CrossSectionStrip strip ) {
                for ( GLNode node : new ArrayList<GLNode>( getChildren() ) ) {
                    if ( node instanceof CrossSectionStripNode && ( (CrossSectionStripNode) node ).getStrip() == strip ) {
                        removeChild( node );
                    }
                }
            }
        } );
        model.terrainStripRemoved.addListener( new VoidFunction1<Terrain>() {
            public void apply( Terrain strip ) {
                for ( GLNode node : new ArrayList<GLNode>( getChildren() ) ) {
                    if ( node instanceof TerrainStripNode && ( (TerrainStripNode) node ).getTerrainStrip() == strip ) {
                        removeChild( node );
                    }
                }
            }
        } );
    }

    @Override protected void renderChildren( GLOptions options ) {
        // render children with a normal pass
        super.renderChildren( options );

        // then render them with the transparency pass
        GLOptions transparencyOptions = options.getCopy();
        transparencyOptions.renderPass = RenderPass.TRANSPARENCY;

        for ( GLNode child : getChildren() ) {
            child.render( transparencyOptions );
        }
    }
}
