// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.F;
import fj.data.Option;
import lombok.Data;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * Apply an effect to the graphics2d with the specified paint
 *
 * @author Sam Reid
 */
public @Data class WithFont extends SNode {
    public final Font font;
    public final SNode child;

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        apply( graphics2D, new Effect<DrawableGraphicsContext>() {
            @Override public void e( final DrawableGraphicsContext context ) {
                child.render( context );
            }
        }.e() );
    }

    private <T extends GraphicsContext, U> U apply( T context, final F<T, U> f ) {
        Font origFont = context.getFont();
        context.setFont( font );
        U result = f.f( context );
        context.setFont( origFont );
        return result;
    }

    @Override public ImmutableRectangle2D getBounds( final GraphicsContext context ) {
        return apply( context, new F<GraphicsContext, ImmutableRectangle2D>() {
            @Override public ImmutableRectangle2D f( final GraphicsContext graphicsContext ) {
                return child.getBounds( graphicsContext );
            }
        } );
    }

    @Override protected Option<PickResult> pick( final Vector2D vector2D, final MockState mockState ) {

        return apply( mockState, new F<MockState, Option<PickResult>>() {
            @Override public Option<PickResult> f( final MockState mockState ) {
                return child.pick( vector2D, mockState );
            }
        } );
    }
}