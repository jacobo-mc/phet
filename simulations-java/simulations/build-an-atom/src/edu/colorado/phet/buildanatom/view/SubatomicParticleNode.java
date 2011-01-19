// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a subatomic particle in the view.
 *
 * @author John Blanco
 */
public class SubatomicParticleNode extends PNode {

    private final ModelViewTransform2D mvt;
    private final SphericalNode sphericalNode;
    private final SubatomicParticle subatomicParticle;

    /**
     * Constructor.
     */
    public SubatomicParticleNode( final ModelViewTransform2D mvt, final SubatomicParticle subatomicParticle, final Color baseColor ) {
        this.mvt = mvt;
        this.subatomicParticle = subatomicParticle;
        double radius = subatomicParticle.getRadius();
        // Create a gradient that gives the particles a 3D look.  The numbers
        // used were empirically determined.
        Paint particlePaint = new RoundGradientPaint( -radius / 1.5, -radius / 1.5,
                ColorUtils.brighterColor( baseColor, 0.8 ), new Point2D.Double( radius, radius ),
                baseColor );
        sphericalNode = new SphericalNode( mvt.modelToViewDifferentialX( subatomicParticle.getDiameter() ),
                particlePaint, false );
        addChild( sphericalNode );

        updatePosition();

        addInputEventListener( new CursorHandler() );

        subatomicParticle.addPositionListener( new SimpleObserver() {
            public void update() {
                updatePosition();
            }
        } );

        addInputEventListener( new PDragEventHandler() {

            @Override
            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                subatomicParticle.setUserControlled( true );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                Point2D modelDelta = mvt.viewToModelDifferential( delta.width, delta.height );
                subatomicParticle.setPositionAndDestination( subatomicParticle.getPosition().getX() + modelDelta.getX(),
                        subatomicParticle.getPosition().getY() + modelDelta.getY() );
            }

            @Override
            protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                subatomicParticle.setUserControlled( false );
            }
        } );
    }

    private void updatePosition() {
        Point2D location = mvt.modelToViewDouble( subatomicParticle.getPosition() );
        sphericalNode.setOffset( location );
    }
}
