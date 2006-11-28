package edu.colorado.phet.molecularreactions;

import edu.colorado.phet.molecularreactions.model.EnergyProfile;

import javax.swing.*;
import java.awt.*;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

/**
 * edu.colorado.phet.molecularreactions.MRConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRConfig {

    // Version
    public static final String VERSION = "0.00.11";

    // Prefix of the strings bundles
    public static final String LOCALIZATION_BUNDLE = "localization/MRStrings";

    // Debug flag
    public static boolean DEBUG = true;

    // Model constants
    public static int CLOCK_FPS = 25;
    public static double MAX_REACTION_THRESHOLD = 5E2;      // Max num of any one type of molecule
    public static final int MAX_MOLECULE_CNT = 200;
    public static double DEFAULT_REACTION_THRESHOLD = MAX_REACTION_THRESHOLD * .7;
    public static final double MAX_SPEED = 3;
    public static final double LAUNCHER_MIN_THETA = -Math.PI / 4;
    public static final double LAUNCHER_MAX_THETA = Math.PI / 4;
    public static final double LAUNCHER_MAX_EXTENSION = 70;
    public static final double RUNNING_DT = 1;
    public static final double STEPPING_DT = 0.3;
    public static final EnergyProfile DEFAULT_ENERGY_PROFILE = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                                  MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                                  MRConfig.DEFAULT_REACTION_THRESHOLD * .6,
                                                                                  100 );

    // View Constants
    public static final Dimension SPATIAL_VIEW_SIZE = new Dimension( 850, 575 );
    public static final Dimension ENERGY_VIEW_SIZE = new Dimension( 300, 490 );
    public static final int BAR_CHART_MAX_Y = 20;
    public static final int PIE_CHART_DIAM_FACTOR = 3;
    public static final double ENERGY_VIEW_PROFILE_VERTICAL_SCALE = 1;
    public static final int STRIP_CHART_BUFFER_SIZE = 3 * 60 * CLOCK_FPS;
    public static final double STRIP_CHART_VISIBLE_TIME_RANGE = 300;
    public static final int STRIP_CHART_MIN_RANGE_Y = 10;

    // Colors
    public static final Color SPATIAL_VIEW_BACKGROUND = new Color( 255, 255, 225 );
    public static final Color MOLECULE_PANE_BACKGROUND = new Color( 237, 255, 235 );

    // Images
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String STOVE_IMAGE_FILE = IMAGE_DIRECTORY + "stove.png";
    public static final String FLAMES_IMAGE_FILE = IMAGE_DIRECTORY + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_DIRECTORY + "ice.gif";
    public static final String PUMP_BODY_IMAGE_FILE = IMAGE_DIRECTORY + "pump-body.gif";
    public static final String PUMP_HANDLE_IMAGE_FILE = IMAGE_DIRECTORY + "pump-handle.gif";

    // Fonts
    public static final Font CHART_TITLE_FONT = UIManager.getFont( "InternalFrame.titleFont" );
}
