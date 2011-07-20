// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;

/**
 * Canvas for the introductory (macro) tab of sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroCanvas extends SugarAndSaltSolutionsCanvas {

    //Separate layer for the conductivity toolbox to make sure the conductivity node shows as submerged in the water, but still goes behind the shaker
    protected final PNode conductivityToolboxLayer = new PNode();

    public final ExpandableConcentrationBarChartNode concentrationBarChart;

    public MacroCanvas( final MacroModel model, GlobalState globalState ) {
        super( model, globalState, createMacroTransform( model ) );

        //This tab uses the conductivity tester
        submergedInWaterNode.addChild( conductivityToolboxLayer );

        //Show the concentration bar chart behind the shaker so the user can drag the shaker in front
        //TODO: why is the scale factor 1 here?
        concentrationBarChart = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart, model.saltConcentration, model.sugarConcentration, model.showConcentrationValues, 1 ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );

        //Toolbox from which the conductivity tester can be dragged
        conductivityToolboxLayer.addChild( new ConductivityTesterToolboxNode( model, this ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getMaxY() + INSET );
        }} );

        //When the shape of the flowing-out water changes, update the model so we can account for conductivity of the water while it is draining
        drainFaucetNode.addListener( new VoidFunction1<Rectangle2D>() {
            public void apply( Rectangle2D outFlowShape ) {
                ImmutableRectangle2D r = new ImmutableRectangle2D( outFlowShape );
                Rectangle2D transformed = drainFaucetNode.localToGlobal( r.toRectangle2D() );
                model.setOutflowShape( transform.viewToModel( transformed ).getBounds2D() );
            }
        } );
    }

    //Create a radio-button-based selector for solutes
    @Override protected SoluteControlPanelNode createSoluteControlPanelNode( SugarAndSaltSolutionModel model, PSwingCanvas canvas, PDimension stageSize ) {
        return new RadioButtonSoluteControlPanelNode( model.dispenserType, canvas );
    }

    //Create the transform from model (SI) to view (stage) coordinates.  Public and static since it is also used to create the MiniBeakerNode in the Water tab
    public static ModelViewTransform createMacroTransform( SugarAndSaltSolutionModel model ) {
        double modelScale = 0.75;//Scale the model down so there will be room for control panels.
        return createRectangleInvertedYMapping( model.visibleRegion.toRectangle2D(),
                                                //Manually tuned so that the model part shows up in the left side of the canvas,
                                                // leaving enough room for controls, labels, and positioning it so it appears near the bottom
                                                new Rectangle2D.Double( 20,
                                                                        //y-position: increasing this number moves down the beaker
                                                                        135,
                                                                        canvasSize.width * modelScale, canvasSize.height * modelScale ) );
    }
}