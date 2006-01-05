/**
 * Copyright (C) 1998-2000 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package edu.umd.cs.piccolox.pswing;

import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * <b>PMouseMotionEvent</b> is an event which indicates that a mouse motion action occurred in a node.
 * <p/>
 * This low-level event is generated by a node object for:
 * <ul>
 * <li> Mouse Motion Events
 * <ul>
 * <li>the mouse is moved
 * <li>the mouse is dragged
 * </ul>
 * </ul>
 * <p/>
 * A PMouseEvent object is passed to every <code>PMouseMotionListener</code>
 * or <code>PMouseMotionAdapter</code> object which registered to receive
 * mouse motion events using the component's <code>addMouseMotionListener</code>
 * method. (<code>PMouseMotionAdapter</code> objects implement the
 * <code>PMouseMotionListener</code> interface.) Each such listener object
 * gets a <code>PMouseEvent</code> containing the mouse motion event.
 * <p/>
 * <p/>
 * <b>Warning:</b> Serialized objects of this class will not be
 * compatible with future Piccolo releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Piccolo. A future release of Piccolo will provide support for long
 * term persistence.
 *
 * @author Benjamin B. Bederson
 * @author Sam R. Reid
 * @author Lance E. Good
 */
public class PSwingMouseMotionEvent extends PSwingMouseEvent {

    /**
     * Constructs a new PMouse event from a Java MouseEvent.
     *
     * @param id The event type (MOUSE_MOVED, MOUSE_DRAGGED)
     * @param e  The original Java mouse event
     *           when in MOUSE_DRAGGED events.
     */
    protected PSwingMouseMotionEvent( int id, MouseEvent e, PInputEvent event ) {
        super( id, e, event );
    }

    /**
     * Calls appropriate method on the listener based on this events ID.
     *
     * @param listener the target for dispatch.
     */
    public void dispatchTo( Object listener ) {
        MouseMotionListener mouseMotionListener = (MouseMotionListener)listener;
        switch( getID() ) {
            case PSwingMouseEvent.MOUSE_DRAGGED:
                mouseMotionListener.mouseDragged( this );
                break;
            case PSwingMouseEvent.MOUSE_MOVED:
                mouseMotionListener.mouseMoved( this );
                break;
            default:
                throw new RuntimeException( "PMouseMotionEvent with bad ID" );
        }
    }

}