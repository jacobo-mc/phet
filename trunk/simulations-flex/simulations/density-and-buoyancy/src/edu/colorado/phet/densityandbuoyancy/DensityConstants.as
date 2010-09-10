package edu.colorado.phet.densityandbuoyancy {
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

import flash.geom.ColorTransform;

public class DensityConstants {
    public static const MIN_MASS:Number = 1;
    public static const MAX_MASS:Number = 10;
    public static const MIN_VOLUME:Number = litersToMetersCubed(1);
    public static const MAX_VOLUME:Number = litersToMetersCubed(10);
    public static const MIN_DENSITY:Number = kgLtoSI(0.1);
    public static const MAX_DENSITY:Number = kgLtoSI(3);

    public static const POOL_WIDTH_X:Number = 1;
    public static const POOL_DEPTH_Z:Number = 0.3;
    public static const POOL_HEIGHT_Y:Number = 0.4;
    public static const DEFAULT_BLOCK_MASS:Number = 2;
    /**
     * Scale up all box2d computations so that they are in the sweet spot for ranges for box2d.
     * Have to keep within a range so that velocity doesn't exceed about 200, see B2Settings.b2_maxLinearVelocity
     */
    public static const SCALE_BOX2D:Number = 10;
    public static const GRAVITY:Number = 9.8;
    public static const DEFAULT_BLOCK_WATER_OFFSET:Number = 10 / DensityModel.DISPLAY_SCALE;

    //Offset the objects slightly to prevent intersections
    public static const FUDGE_FACTOR:Number = 1000.0 / DensityModel.DISPLAY_SCALE;
    public static const VERTICAL_GROUND_OFFSET_AWAY_3D:Number = -FUDGE_FACTOR;  //This number was hand-tuned so that no rendering artifacts (flickering faces) occur, but may need to change if scale or other parameters change
    public static const FUDGE_FACTOR_DZ:Number = FUDGE_FACTOR;//Objects shouldn't exactly overlap in the z-dimension either

    //Flex properties that we couldn't find as enum values in Flex
    public static const FLEX_UNDERLINE:String = "underline";
    public static const FLEX_NONE:String = "none";
    public static const FLEX_FONT_SIZE:String = "fontSize";
    public static const FLEX_FONT_WEIGHT:String = "fontWeight";
    public static const FLEX_FONT_BOLD:String = "bold";
    public static const FLEX_TEXT_DECORATION:String = "textDecoration";
    public static const CONTROL_PANEL_COLOR:Number = 0xfafad7;//Color recommended by Kathy to be a pale yellow background for control panels

    public static const LARGE_BLOCK_WIDTH:Number = 0.18;
    public static const NUMBER_OF_DECIMAL_PLACES:Number = 2;
    public static const CONTROL_INSET:Number = 5;
    public static const SLIDER_READOUT_TEXT_FIELD_WIDTH:Number = 50;
    public static const YELLOW:ColorTransform = new ColorTransform(1, 1, 0);
    public static const RED:ColorTransform = new ColorTransform(1, 0, 0);
    public static const GREEN:ColorTransform = new ColorTransform(0, 0.9, 0);
    public static const BLUE:ColorTransform = new ColorTransform(0, 0, 1);
    public static const PURPLE:ColorTransform = new ColorTransform(0.75, 0, 0.75);
    //this is a magic number that just barely fits "00.00" with the above specified font;

    //So that away3d faces don't overlap
    private static function kgLtoSI(number:Number):Number {
        return number / 0.001;
    }

    public static function litersToMetersCubed(x:Number):Number {
        return 0.001 * x;
    }

    public static function metersToLitersCubed(v:Number):Number {
        return v / 0.001;
    }
}
}