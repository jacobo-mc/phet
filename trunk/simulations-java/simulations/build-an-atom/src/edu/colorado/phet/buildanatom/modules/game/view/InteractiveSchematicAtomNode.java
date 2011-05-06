// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.view.BucketView;
import edu.colorado.phet.buildanatom.view.OrbitalViewProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo Node that adds interactivity to a node that represents and atom.
 * The type of interactivity that it adds is the ability to grab and move
 * the subatomic particles in and out of the atom.  This extension also add
 * buckets for holding subatomic particles that are outside of the atom.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class InteractiveSchematicAtomNode extends SchematicAtomNode {

    private final BuildAnAtomModel model;

    /**
     * Constructor.
     */
    public InteractiveSchematicAtomNode( final BuildAnAtomModel model, ModelViewTransform mvt, final OrbitalViewProperty orbitalView ) {
        super( model.getAtom(), mvt, orbitalView );

        this.model = model;

        // Add a marker that depicts the center of the atom's nucleus.
        electronShellLayer.addChild( new CenterMarkerNode( model, mvt ) );

        // Add the buckets that hold the sub-atomic particles.
        BucketView electronBucketNode = new BucketView( model.getElectronBucket(), mvt );
        electronShellLayer.addChild( electronBucketNode.getHoleNode() );
        frontLayer.addChild( electronBucketNode.getFrontNode() );
        for ( SphericalParticle electron : model.getElectronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addElectronNode( (Electron) electron );
        }
        BucketView protonBucketNode = new BucketView( model.getProtonBucket(), mvt );
        electronShellLayer.addChild( protonBucketNode.getHoleNode() );
        frontLayer.addChild( protonBucketNode.getFrontNode() );
        for ( SphericalParticle proton : model.getProtonBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addProtonNode( (Proton) proton );
        }
        BucketView neutronBucketNode = new BucketView( model.getNeutronBucket(), mvt );
        electronShellLayer.addChild( neutronBucketNode.getHoleNode() );
        frontLayer.addChild( neutronBucketNode.getFrontNode() );
        for ( SphericalParticle neutron : model.getNeutronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addNeutronNode( (Neutron) neutron );
        }
    }

    public ImmutableAtom getAtomValue() {
        return model.getAtom().toImmutableAtom();
    }

    public void setAtomValue( ImmutableAtom answer ) {
        model.setState( answer, false );
    }

    private static class CenterMarkerNode extends PNode {
        public CenterMarkerNode( final BuildAnAtomModel model, ModelViewTransform mvt ) {
            setPickable( false );
            setChildrenPickable( false );

            model.getAtom().addAtomListener( new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    // Only visible when there are no nucleons in the atom.
                    setVisible (model.getAtom().getMassNumber() == 0);
                }
            });

            // Create the marker.
            DoubleGeneralPath nucleusXMarkerModelCoords = new DoubleGeneralPath();
            double xMarkerSize = Proton.RADIUS; // Arbitrary, adjust as desired.
            nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                    model.getAtom().getPosition().getY() - xMarkerSize / 2 );
            nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                    model.getAtom().getPosition().getY() + xMarkerSize / 2 );
            nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                    model.getAtom().getPosition().getY() + xMarkerSize / 2 );
            nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                    model.getAtom().getPosition().getY() - xMarkerSize / 2 );
            Shape nucleusXMarkerShape = mvt.modelToView( nucleusXMarkerModelCoords.getGeneralPath() );
            PNode nucleusXMarkerNode = new PhetPPath( nucleusXMarkerShape, new BasicStroke( 4f ), new Color( 255, 0, 0, 75 ) );
            addChild( nucleusXMarkerNode );
        }
    }
}
