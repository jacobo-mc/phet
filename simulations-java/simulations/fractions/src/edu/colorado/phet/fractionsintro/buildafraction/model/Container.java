package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public @Data class Container {
    public final DraggableObject draggableObject;
    public final int numSegments;
    public static final F<Container, ObjectID> ID = new F<Container, ObjectID>() {
        @Override public ObjectID f( final Container container ) {
            return container.getID();
        }
    };

    public Container translate( final Vector2D delta ) { return new Container( draggableObject.translate( delta ), numSegments ); }

    public Container withDragging( final boolean dragging ) { return new Container( draggableObject.withDragging( dragging ), numSegments ); }

    public boolean isDragging() { return draggableObject.dragging; }

    public ObjectID getID() { return draggableObject.id; }

    public Vector2D getPosition() { return draggableObject.position; }
}