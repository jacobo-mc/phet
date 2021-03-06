// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.pieset;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.sprite;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.sliceComponent;

/**
 * Node used in PieSetNode for showing user-draggable pieces.  This matches with the immutable model and so has
 * some quirky implementation (see comments below) because there is an impedance mismatch with Piccolo.
 *
 * @author Sam Reid
 */
public class MovableSliceNode extends PNode {

    public MovableSliceNode( final PNode child, final PNode rootNode, final SettableProperty<PieSet> model, final Slice slice ) {

        addChild( child );

        addInputEventListener( new CursorHandler() );

        addInputEventListener( new SimSharingDragHandler( sliceComponent, sprite, false ) {

            //Flag one slice as dragging
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );

                final boolean inContainer = model.get().isInContainer( slice );
                Vector2D newPosition = getNewPieceLocation( inContainer, child, slice );
                PieSet state = model.get();

                //Do not allow the user to grab a piece that is animating to a target, it causes the representations to get out of sync
                if ( slice.animationTarget == null ) {

                    //If dragging from the bucket, do not delete the old piece (since bucket should always look like it has an infinite supply)
                    if ( state.isInBucket( slice ) ) {
                        model.set( state.withSlices( state.slices.snoc( slice.withDragging( true ).withID( Slice.nextID() ) ) ) );
                    }
                    else {
                        model.set( state.withSlices( state.slices.delete( slice, Equal.<Slice>anyEqual() ).snoc( slice.withDragging( true ).withPosition( newPosition ) ) ) );
                    }
                }
            }

            //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );

                PieSet state = model.get();
                final Dimension2D delta = event.getDeltaRelativeTo( rootNode );
                PieSet newState = state.withSlices( state.slices.map( new F<Slice, Slice>() {
                    public Slice f( Slice s ) {
                        return s.dragging ? s.translate( delta.getWidth(), delta.getHeight() ) : s;
                    }
                } ) );

                model.set( newState );
            }

            //Set all drag flags to false
            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );

                final PieSet state = model.get();

                //Any dropped pieces should snap to their destination.
                final List<Slice> newSlices = state.slices.map( new F<Slice, Slice>() {
                    public Slice f( Slice s ) {
                        Slice target = state.getDropTarget( s );
                        return s.dragging && target != null ? s.moveTo( target ) :
                               s.dragging ? s.withDragging( false ).animationTarget( model.get().sliceFactory.createBucketSlice( model.get().denominator, System.currentTimeMillis() ) ) :
                               s;
                    }
                } );
                final PieSet newState = state.withSlices( newSlices );

                model.set( newState );
            }
        } );
    }

    //For water glasses, move the piece to a standard location instead of based on the location of the piece
    //Hack alert: this uses PhetPPath.class as the check to see if it is water glasses, see WaterGlassNodeFactory
    private Vector2D getNewPieceLocation( final boolean inContainer, final PNode child, final Slice slice ) {
        return inContainer && child.getClass().equals( PhetPPath.class ) ? Vector2D.v( slice.position.x - 20, 300 ) : slice.position;
    }
}