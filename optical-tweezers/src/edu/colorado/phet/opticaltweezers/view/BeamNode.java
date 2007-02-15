/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.opticaltweezers.util.ColorUtils;
import edu.umd.cs.piccolo.nodes.PPath;


public class BeamNode extends PPath {

    private static final int ALPHA_CHANNEL = 100; // 0-255
    
    public BeamNode( double width, double height, double wavelength ) {
        super();
        setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
        Color wavelengthColor = VisibleColor.wavelengthToColor( wavelength );
        Color laserColor = ColorUtils.addAlpha( wavelengthColor, ALPHA_CHANNEL );
        setPaint( laserColor );
        setStroke( null );
    }
}
