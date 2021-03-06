// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionalscenegraph;

import fj.data.Option;
import lombok.Data;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * Apply an effect to the graphics2d with the specified paint
 *
 * @author Sam Reid
 */
public @Data class WithTransform extends SNode {
    public final AffineTransform transform;
    public final SNode child;

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        AffineTransform orig = graphics2D.getTransform();
        graphics2D.transform( transform );
        child.render( graphics2D );
        graphics2D.setTransform( orig );
    }

    //TODO: is this the right direction of transform?
    //Also reduce wrapping here
    @Override public ImmutableRectangle2D getBounds( final GraphicsContext mockState ) {
        return new ImmutableRectangle2D( transform.createTransformedShape( child.getBounds( mockState ).toRectangle2D() ) );
    }

    @Override protected Option<PickResult> pick( final Vector2D vector2D, final MockState mockState ) {
        try {
            return child.pick( new Vector2D( transform.inverseTransform( vector2D.toPoint2D(), null ) ), mockState );
        }
        catch ( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
    }
}