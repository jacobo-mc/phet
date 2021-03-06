// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * Enum type pattern for the laser color, which may be white or a specific wavelength.
 *
 * @author Sam Reid
 */
public abstract class LaserColor {

    //White light appears gray against a white background
    public static final LaserColor WHITE_LIGHT = new LaserColor() {
        @Override
        public double getWavelength() {
            new RuntimeException( "GetWavelength shouldn't be called for white light" ).printStackTrace();
            return BendingLightModel.WAVELENGTH_RED;
        }

        @Override
        public Color getColor() {
            return Color.gray;
        }
    };

    public static class OneColor extends LaserColor {
        private double wavelength;

        public OneColor( double wavelength ) {
            this.wavelength = wavelength;
        }

        @Override
        public Color getColor() {
            return new VisibleColor( wavelength * 1E9//convert to nanometers
            ).toColor();
        }

        @Override
        public double getWavelength() {
            return wavelength;
        }
    }

    //Determine the wavelength (in nm) of the light
    public abstract double getWavelength();

    //Determine the color of the light.
    public abstract Color getColor();
}
