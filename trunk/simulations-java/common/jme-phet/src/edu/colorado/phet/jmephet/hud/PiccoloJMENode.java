// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.hud;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.CanvasTransform;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Embed a Piccolo node within the JME3 space as a Spatial. Handles proper resizing.
 */
public class PiccoloJMENode extends SwingJMENode {
    private final PNode node;

    public PiccoloJMENode( final PNode node, final JMEInputHandler inputHandler, PhetJMEApplication app ) {
        this( node, inputHandler, app, getDefaultTransform() );
    }

    public PiccoloJMENode( final PNode node, final JMEInputHandler inputHandler, PhetJMEApplication app, CanvasTransform canvasTransform ) {
        this( node, inputHandler, app, canvasTransform, getDefaultPosition() );
    }

    public PiccoloJMENode( final PNode node, final JMEInputHandler inputHandler, PhetJMEApplication app, CanvasTransform canvasTransform, Property<ImmutableVector2D> position ) {
        // use a wrapper panel that takes up no extra room
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
                   add( new PiccoloJMECanvas( node ) );
               }}, inputHandler, app, canvasTransform, position );
        this.node = node;
    }

    public PCanvas getCanvas() {
        return (PSwingCanvas) getComponent();
    }

    public PNode getNode() {
        return node;
    }
}
