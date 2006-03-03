/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.model.WaveModel;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 3, 2006
 * Time: 12:52:00 AM
 * Copyright (c) Mar 3, 2006 by Sam Reid
 */

public class MandelSplitColorMap implements ColorMap {
    private MiniPhotonColorMap leftColorMapMini;
    private MiniPhotonColorMap rightColorMapMini;
    private WaveValueAccessor waveValueAccessor = new WaveValueAccessor.Magnitude();

    public MandelSplitColorMap( MandelModule mandelModule ) {
        leftColorMapMini = new MiniPhotonColorMap(
                mandelModule.getMandelSchrodingerPanel().getLeftGun().getWavelength(),
                waveValueAccessor, mandelModule.getMandelModel().getLeftWaveModel() );
        rightColorMapMini = new MiniPhotonColorMap(
                mandelModule.getMandelSchrodingerPanel().getRightGun().getWavelength(),
                waveValueAccessor, mandelModule.getMandelModel().getRightWaveModel() );
    }

    public Paint getColor( int i, int k ) {
        Color a = (Color)leftColorMapMini.getColor( i, k );
        Color b = (Color)rightColorMapMini.getColor( i, k );

        return new Color( Math.min( a.getRed() + b.getRed(), 255 ), Math.min( a.getGreen() + b.getGreen(), 255 ), Math.min( a.getBlue() + b.getBlue(), 255 ) );
    }

    private class MiniPhotonColorMap implements ColorMap {
        private WaveValueAccessor waveValueAccessor;
        private double intensityScale = 20;
        private ColorData rootColor;
        private WaveModel wave;

        public MiniPhotonColorMap( double wavelengthNM,
                                   WaveValueAccessor waveValueAccessor, WaveModel wave ) {
            this.wave = wave;
            if( waveValueAccessor == null ) {
                throw new RuntimeException( "Null waveValueAccessor" );
            }
            this.waveValueAccessor = waveValueAccessor;
            this.rootColor = new ColorData( wavelengthNM );
        }

        public Paint getColor( int i, int k ) {
            double abs = Math.min( waveValueAccessor.getValue( wave.getWavefunction(), i, k ) * intensityScale, 1.0 );
            return rootColor.toColor( abs );
        }

    }
}
