// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources for the line-graphing project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGResources {

    public static final String PROJECT_NAME = "line-graphing";
    private static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    // Localized strings
    public static class Strings {

        public static final String ERASE_LINES = RESOURCES.getLocalizedString( "eraseLines" );
        public static final String SAVE_LINE = RESOURCES.getLocalizedString( "saveLine" );
        public static final String SHOW = RESOURCES.getLocalizedString( "show" );

        public static final String SYMBOL_HORIZONTAL_AXIS = RESOURCES.getLocalizedString( "symbol.horizontalAxis" );
        public static final String SYMBOL_VERTICAL_AXIS = RESOURCES.getLocalizedString( "symbol.verticalAxis" );
        public static final String SYMBOL_SLOPE = RESOURCES.getLocalizedString( "symbol.slope" );
        public static final String SYMBOL_INTERCEPT = RESOURCES.getLocalizedString( "symbol.intercept" );

        public static final String TAB_INTRO = RESOURCES.getLocalizedString( "tab.intro" );
        public static final String TAB_GAME = RESOURCES.getLocalizedString( "tab.game" );

        public static final String PATTERN_0SIGN_1AXIS = RESOURCES.getLocalizedString( "pattern.0sign.1axis" );
    }
}
