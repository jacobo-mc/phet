// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGColors;
import edu.colorado.phet.linegraphing.LGConstants;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.intro.model.IntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroCanvas extends LGCanvas implements Resettable {

    public final Property<Boolean> linesVisible = new Property<Boolean>( true ); //TODO this property is problematic...

    private final InteractiveLineGraphNode lineGraphNode;

    public IntroCanvas( final IntroModel model ) {

        lineGraphNode = new InteractiveLineGraphNode( model.graph, model.mvt, model.interactiveLine,
                                                      IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE,
                                                      model.yEqualsXLine, model.yEqualsNegativeXLine, linesVisible, model.pointToolLocation );
        PNode graphNode = new ZeroOffsetNode( lineGraphNode );
        PNode equationControls = new EquationControls( model.interactiveLine, lineGraphNode, IntroModel.RISE_RANGE, IntroModel.RUN_RANGE, IntroModel.INTERCEPT_RANGE );
        PNode visibilityControls = new VisibilityControls( linesVisible, lineGraphNode.riseOverRunVisible, lineGraphNode.yEqualsXVisible, lineGraphNode.yEqualsNegativeXVisible,
                                                           lineGraphNode.pointToolVisible );
        PNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, null, LGConstants.CONTROL_FONT_SIZE, Color.BLACK, LGColors.RESET_ALL_BUTTON ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( graphNode );
            addChild( equationControls );
            addChild( visibilityControls );
            addChild( resetAllButtonNode );
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double xMargin = 20;
            final double yMargin = 20;
            graphNode.setOffset( xMargin, yMargin );

            // upper-right of graph
            equationControls.setOffset( graphNode.getFullBoundsReference().getMaxX() + 20, 50 );
            // centered below equation
            visibilityControls.setOffset( equationControls.getFullBoundsReference().getCenterX() - ( visibilityControls.getFullBoundsReference().getWidth() / 2 ),
                                          equationControls.getFullBoundsReference().getMaxY() + 35 );
            // buttons centered below control panel
            resetAllButtonNode.setOffset( visibilityControls.getFullBoundsReference().getCenterX() - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                     getStageSize().getHeight() - yMargin - resetAllButtonNode.getFullBoundsReference().getHeight() );
        }
    }

    public void reset() {
        lineGraphNode.reset();
    }
}
