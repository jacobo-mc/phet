// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import edu.colorado.phet.linegraphing.common.model.PointSlopeModel;
import edu.colorado.phet.linegraphing.common.view.GraphControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsCanvas;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;

/**
 * Canvas for the "Point-Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeCanvas extends LineFormsCanvas {

    public PointSlopeCanvas( PointSlopeModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties,
               new PointSlopeGraphNode( model, viewProperties ),
               new PointSlopeEquationControls( model, viewProperties ),
               new GraphControls( viewProperties.linesVisible, viewProperties.slopeVisible, model.standardLines ) );
    }
}
