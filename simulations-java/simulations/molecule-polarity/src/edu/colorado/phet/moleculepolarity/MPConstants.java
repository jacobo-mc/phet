// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPConstants {

    /* Not intended for instantiation. */
    private MPConstants() {
    }

    public static final String PROJECT_NAME = "molecule-polarity";

    // Model
    public static final DoubleRange ELECTRONEGATIVITY_RANGE = new DoubleRange( 0.7, 4, 2 );
    public static final double ELECTRONEGATIVITY_SNAP_INTERVAL = 0.1;

    // View
    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1024, 600 );
    public static final Color CANVAS_COLOR = new Color( 215, 255, 229 ); // light green
    public static final Color NEGATIVE_COLOR = Color.BLUE;
    public static final Color POSITIVE_COLOR = PhetColorScheme.RED_COLORBLIND;
    public static final Color PLATE_DISABLED_COLOR = Color.LIGHT_GRAY;
}
