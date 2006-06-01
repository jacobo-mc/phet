/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import java.awt.*;
import java.text.DecimalFormat;

import org.jfree.data.Range;

import edu.colorado.phet.boundstates.color.BSBlackColorScheme;
import edu.colorado.phet.boundstates.color.BSColorScheme;


/**
 * BSConstants is a collection of constants.
 * Modify these at your peril.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConstants {

    /* Not intended for instantiation. */
    private BSConstants() {}
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    
    //----------------------------------------------------------------------------
    // Localization
    //----------------------------------------------------------------------------
    
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/BSStrings";

    //----------------------------------------------------------------------------
    // Clock
    //----------------------------------------------------------------------------
    
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_STEP = 0.01; // fs, femtoseconds (model time)
    
    /* Controls how time is displayed, should match CLOCK_STEP precision */
    public static final DecimalFormat TIME_FORMAT = new DecimalFormat( "0.00" );
    
    /* Is the time display visible next to the clock controls? */
    public static final boolean TIME_DISPLAY_VISIBLE = true;
    
    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    public static final String FONT_NAME = "Lucida Sans";
    public static final Font AXIS_LABEL_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 20 );
    public static final Font AXIS_TICK_LABEL_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 14 );
    public static final Font HILITE_ENERGY_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 16 );
    public static final Font WAVE_FUNCTION_EQUATION_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 22 );
    public static final Font TIME_DISPLAY_FONT = new Font( BSConstants.FONT_NAME, Font.BOLD, 22 );
    public static final Font TIME_UNITS_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 22 );
    
    //----------------------------------------------------------------------------
    // Special characters
    //----------------------------------------------------------------------------
    
    public static final char LOWERCASE_PSI = '\u03c8';
    public static final char UPPERCASE_PSI = '\u03a8';
    public static final char LOWERCASE_PI = '\u03c0';
    
    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------
    
    public static final Stroke EIGENSTATE_NORMAL_STROKE = new BasicStroke( 1f );
    public static final Stroke EIGENSTATE_HILITE_STROKE = new BasicStroke( 2f );
    public static final Stroke EIGENSTATE_SELECTION_STROKE = new BasicStroke( 2f );
    public static final Stroke POTENTIAL_ENERGY_STROKE = new BasicStroke( 3f );
    public static final Stroke REAL_STROKE = new BasicStroke( 2f );
    public static final Stroke IMAGINARY_STROKE = new BasicStroke( 2f );
    public static final Stroke MAGNITUDE_STROKE = new BasicStroke( 2f );
    public static final Stroke PROBABILITY_DENSITY_STROKE = new BasicStroke( 2f );
    public static final Stroke HILITE_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 220, 220, 220 );
    
    // Color of the stopwatch background
    public static final Color STOPWATCH_BACKGROUND = Color.LIGHT_GRAY;
    
    // The default color scheme
    public static final BSColorScheme COLOR_SCHEME = new BSBlackColorScheme();
    
    // Transparency (alpha) of the background for the hilited eigenstate's value
    public static final int HILITE_VALUE_BACKGROUND_ALPHA = 175;
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    public static final String IMAGES_DIRECTORY = "images/";
    public static final String IMAGE_CLOCK = IMAGES_DIRECTORY + "clock.png";
    public static final String IMAGE_ZOOM_IN = IMAGES_DIRECTORY + "zoomIn.gif";
    public static final String IMAGE_ZOOM_OUT = IMAGES_DIRECTORY + "zoomOut.gif";

    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );
    public static final Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Ranges
    //----------------------------------------------------------------------------
    
    // Energy range is set per potential type in subclasses of BSAbstractModuleSpec.
    
    // Wave Function
//    public static final Range WAVE_FUNCTION_RANGE = new Range( -1.15, +1.15 );
    public static final Range WAVE_FUNCTION_RANGE = new Range( -1.55, +1.55 );
    public static final double WAVE_FUNCTION_TICK_SPACING = 0.5;
    public static final DecimalFormat WAVE_FUNCTION_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Probability Density
//    public static final Range PROBABILITY_DENSITY_RANGE = new Range( 0, 1.05 );
    public static final Range PROBABILITY_DENSITY_RANGE = new Range( 0, 1.55 );
    public static final double PROBABILITY_DENSITY_TICK_SPACING = 0.5;
    public static final DecimalFormat PROBABILITY_DENSITY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Position
    public static final Range POSITION_MODEL_RANGE = new Range( -3.5, 3.5 ); // nm
    public static final Range POSITION_VIEW_RANGE = POSITION_MODEL_RANGE;
    public static final double POSITION_TICK_SPACING = 1; // nm
    public static final DecimalFormat POSITION_TICK_FORMAT = new DecimalFormat( "0" );
    
    // Superposition coefficients
    public static final double COEFFICIENT_MIN = 0.00;
    public static final double COEFFICIENT_MAX = 1.00;
    public static final double COEFFICIENT_STEP = 0.01;
    public static final String COEFFICIENT_PATTERN = "0.00";
    public static final DecimalFormat COEFFICIENT_FORMAT = new DecimalFormat( COEFFICIENT_PATTERN );
    
    //----------------------------------------------------------------------------
    // Charts
    //----------------------------------------------------------------------------
    
    /**
     * Controls when and how JFreeChart is used.
     * true = use JFreeChart to do all chart elements (static and dynamic).
     * false = use JFreeChart to draw static elements, custom code for dynamic elements.
     */
    public static final boolean JFREECHART_DYNAMIC = false;
    
    public static final boolean SHOW_VERTICAL_GRIDLINES = true;
    public static final boolean SHOW_HORIZONTAL_GRIDLINES = false;
    
    //----------------------------------------------------------------------------
    // Lattice size controls
    //----------------------------------------------------------------------------
    
    /**
     * Determines the number of sample points used to draw the potentials. 
     * The actual number of sample points will vary with the width (in pixels) 
     * of the charts.  This guarantees that the rendering quality will be the
     * same resolution for all simulation window sizes.
     */
    public static final double PIXELS_PER_POTENTIAL_SAMPLE_POINT = 1;
    
    /**
     * Determines the number of sample points used to calculate eigenstates
     * and wave functions. Because of how the Schmidt-Lee algorithm behaves,
     * we don't want this to vary with the size of the simulation window.
     * Schmidt-Lee will fail if dx gets too small, so if you change the 
     * position range, you may need to change this value.
     */
    public static final int SCHMIDT_LEE_SAMPLE_POINTS = 1350;
    
    /**
     * Determines the number of samples points used to calculate the 
     * wave function when using the analytic solver for Coulomb wells.
     */
    public static final int COULOMB_ANALYTIC_SAMPLE_POINTS = SCHMIDT_LEE_SAMPLE_POINTS;
    
    //----------------------------------------------------------------------------
    // Miscellaneous
    //----------------------------------------------------------------------------
    
    // Energy must be at least this close to eigenstate to be hilited.
    public static final double HILITE_ENERGY_THRESHOLD = 1; // eV
    
    public static final DecimalFormat MAGNIFICATION_FORMAT = new DecimalFormat( "0.#" );
    
    public static final double ELECTRON_MASS = 5.68; // eV/c^2
    public static final double HBAR = 0.658;
    public static final double KE2 = 1.44; // ke^2, eV nm
}
