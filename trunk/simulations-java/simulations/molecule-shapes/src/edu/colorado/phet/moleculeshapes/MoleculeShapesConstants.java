// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import com.jme3.math.ColorRGBA;

public class MoleculeShapesConstants {

    /* Not intended for instantiation. */
    private MoleculeShapesConstants() {
    }

    public static final String PROJECT_NAME = "molecule-shapes";

    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final ColorRGBA COLOR_ATOM_CENTER = new ColorRGBA( 1f, 0f, 0f, 1f );
    public static final ColorRGBA COLOR_ATOM = new ColorRGBA( 1f, 1f, 1f, 1f );

    public static final double CONTROL_PANEL_INNER_WIDTH = 150; // width of the inner parts of the control panel
    public static final Color CONTROL_PANEL_BORDER_COLOR = new Color( 230, 230, 230 ); // TODO: rename HIGHLIGHT_COLOR, but then separate
    public static final float CONTROL_PANEL_BORDER_WIDTH = 2;

    public static final float MODEL_ATOM_RADIUS = 2f;
    public static final float MODEL_BOND_RADIUS = MODEL_ATOM_RADIUS / 4;

    public static final float MOLECULE_ATOM_RADIUS = 0.2f;
    public static final float MOLECULE_BOND_RADIUS = MOLECULE_ATOM_RADIUS / 4;
    public static final float MOLECULE_SCALE = 14.0f;

    public static final int ANGLE_PRECISION = 25;

    public static final Font CHECKBOX_FONT_SIZE = new PhetFont( 14 );
}
