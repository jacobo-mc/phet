// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Shows the icon on the representation control panel for the horizontal bars.
 *
 * @author Sam Reid
 */
public class VerticalBarIcon extends ShapeIcon {
    public VerticalBarIcon( SettableProperty<Representation> chosenRepresentation, Color color ) {
        super(
                new ArrayList<Shape>(),
                new ArrayList<Shape>() {{
                    add( new Rectangle2D.Double( 0, 0, DIM, DIM * 4 ) );
                }}, chosenRepresentation, Representation.VERTICAL_BAR, color
        );
    }
}
