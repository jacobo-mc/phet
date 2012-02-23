// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.fractionsintro.intro.view.Representation.WATER_GLASSES;

/**
 * Representation control panel icon for water glass.
 *
 * @author Sam Reid
 */
public class WaterGlassIcon extends PNode implements RepresentationIcon {

    public WaterGlassIcon( final SettableProperty<Representation> selected ) {
        addChild( new WaterGlassNode( 1, 1 ) );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                selected.set( getRepresentation() );
            }
        } );
        scale( 0.4 );
    }

    public PNode getNode() {
        return this;
    }

    public Representation getRepresentation() {
        return WATER_GLASSES;
    }
}