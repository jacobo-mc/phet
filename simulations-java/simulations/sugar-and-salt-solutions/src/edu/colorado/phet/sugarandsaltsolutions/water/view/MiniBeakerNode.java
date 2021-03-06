// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SolutionNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas.WATER_COLOR;

/**
 * Shows a small representation of a beaker with solution that is "zoomed in" on by the ParticleWindowNode
 * Uses features from "macro" tab that were not moved to "common" package.
 *
 * @author Sam Reid
 */
public class MiniBeakerNode extends PNode {
    public MiniBeakerNode() {

        //Create a whole model, but just for the purpose of making a beaker graphic.  Shouldn't be a memory leak since no listeners are wired up and this is done only once.
        final MacroModel model = new MacroModel();
        final ModelViewTransform transform = MacroCanvas.createMacroTransform( model );

        //Add the beaker and water graphics
        addChild( new BeakerNode( transform, model.beaker ) );
        addChild( new SolutionNode( transform, model.solution, new Color( WATER_COLOR.getRed(), WATER_COLOR.getGreen(), WATER_COLOR.getBlue(), 255 ) ) );

        //Make it smaller so it will fit on the screen
        scale( 0.275 );
    }
}