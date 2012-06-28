// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.model.numbers;

import fj.data.List;

import java.awt.Color;

import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class RandomColors4 {
    private final List<Color> colors = NumberLevelList.shuffle( list( LIGHT_RED, LIGHT_GREEN, LIGHT_BLUE, Color.orange ) );
    private int index = 0;

    public Color next() {
        final Color color = colors.index( index );
        index++;
        return color;
    }
}