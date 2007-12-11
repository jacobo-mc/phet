/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersResources;
import edu.umd.cs.piccolo.nodes.PImage;


public class BoreholeDrillNode extends PImage {

    public BoreholeDrillNode() {
        super();
        setImage( GlaciersResources.getImage( "boreholeDrill.png" ) );
    }
}
