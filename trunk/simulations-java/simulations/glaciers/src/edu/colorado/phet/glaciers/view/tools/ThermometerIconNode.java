/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.AbstractTool;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolIconNode.InteractiveToolIconNode;

/**
 * ThermometerIconNode
 */
public class ThermometerIconNode extends InteractiveToolIconNode {
    
    public ThermometerIconNode( IToolProducer toolProducer, ModelViewTransform mvt ) {
        super( GlaciersImages.THERMOMETER, GlaciersStrings.TOOLBOX_THERMOMETER, toolProducer, mvt );
    }
    
    public AbstractTool createTool( Point2D position ) {
        return getToolProducer().addThermometer( position );
    }
}