// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.Piccolo3DCanvas;
import edu.umd.cs.piccolo.PNode;

public class OrthoPiccoloNode extends OrthoComponentNode {
    private final PNode node;

    public OrthoPiccoloNode( final PNode node, final LWJGLTab tab, CanvasTransform canvasTransform, Property<ImmutableVector2D> position ) {
        // use a wrapper panel that takes up no extra room
        super( new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) ) {{
                   add( new Piccolo3DCanvas( node ) );
               }}, tab, canvasTransform, position );
        this.node = node;
    }

    public Piccolo3DCanvas getCanvas() {
        return (Piccolo3DCanvas) ( getComponent().getComponent( 0 ) );
    }

    public PNode getNode() {
        return node;
    }
}
