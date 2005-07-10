/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.ColorMap;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:43:06 PM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SplitColorMap implements ColorMap {
    private SplitModel splitModel;
    private double intensityScale = 20;

    public SplitColorMap( SplitModel splitModel ) {
        this.splitModel = splitModel;
    }

    public Color getColor( int i, int k ) {
        Rectangle[] areas = splitModel.getDoubleSlitPotential().getSlitAreas();
        double abs = 0;
        if( !contains( areas, i, k ) ) {
            Wavefunction wavefunction = splitModel.getWavefunction();
            abs = getValue( wavefunction, i, k );
        }
        abs += getValue( splitModel.getLeftWavefunction(), i, k );
        abs += getValue( splitModel.getRightWavefunction(), i, k );
        if( abs > 1 ) {
            abs = 1;
        }
        Color color = new Color( (float)abs, (float)abs, (float)abs );
        return color;
    }

    private boolean contains( Rectangle[] areas, int i, int k ) {
        for( int j = 0; j < areas.length; j++ ) {
            Rectangle area = areas[j];
            if( area.contains( i, k ) ) {
                return true;
            }
        }
        return false;
    }

    private double getValue( Wavefunction wavefunction, int i, int k ) {
        if( wavefunction.containsLocation( i, k ) ) {
            double abs = wavefunction.valueAt( i, k ).abs() * intensityScale;
            return abs;
        }
        else {
            return 0;
        }
    }

    protected double getBrightness( double x ) {
        double b = x * intensityScale;
        if( b > 1 ) {
            b = 1;
        }
        return b;
    }

}
