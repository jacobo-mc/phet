// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.equalitylab.model.EqualityLabModel;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionControlNode;
import edu.colorado.phet.fractionsintro.intro.view.NumberLineNode;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractionsintro.intro.view.RepresentationNode;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.HorizontalBarIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.NumberLineIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.PieIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationControlPanel;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationIcon;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.WaterGlassIcon;

import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class EqualityLabCanvas extends AbstractFractionsCanvas {

    public EqualityLabCanvas( final EqualityLabModel model ) {

        //Control panel for choosing different representations, can be split into separate controls for each display
        final SettableProperty<Representation> leftRepresentation = model.leftRepresentation;
        final SettableProperty<Representation> rightRepresentation = model.rightRepresentation;
        final RepresentationControlPanel leftControl = new RepresentationControlPanel( leftRepresentation, getRepresentations( leftRepresentation ) ) {{ setOffset( INSET * 3, INSET ); }};
        addChild( leftControl );

        final RepresentationControlPanel rightControl = new RepresentationControlPanel( model.rightRepresentation, getRepresentations( model.rightRepresentation ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullWidth() - INSET * 3, INSET );
        }};
        addChild( rightControl );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        //The fraction control node
        addChild( new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth(), STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
        addRepresentationNodes( model, leftRepresentation, leftControl, model.pieSet );
        addRepresentationNodes( model, rightRepresentation, rightControl, model.rightPieSet );

        resetAllButtonNode.setOffset( STAGE_SIZE.getWidth() - resetAllButtonNode.getFullBounds().getWidth(), STAGE_SIZE.getHeight() - resetAllButtonNode.getFullBounds().getHeight() );
    }

    private void addRepresentationNodes( final EqualityLabModel model,
                                         final SettableProperty<Representation> representation,
                                         final RepresentationControlPanel control,
                                         SettableProperty<PieSet> pieSet ) {
        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( representation, PIE, new PieSetNode( pieSet, rootNode ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode ) ) );

        //For water glasses
        addChild( new RepresentationNode( representation, WATER_GLASSES, new WaterGlassSetNode( model.waterGlassSet, rootNode ) ) );

        //Number line
        addChild( new NumberLineNode( model.numerator, model.denominator, representation.valueEquals( NUMBER_LINE ), model.maximum ) {{
            setOffset( INSET + 10, control.getFullBounds().getMaxY() + 100 + 15 );
        }} );
    }

    private RepresentationIcon[] getRepresentations( SettableProperty<Representation> representation ) {
        return new RepresentationIcon[] {
                new PieIcon( representation ),
                new HorizontalBarIcon( representation ),
                new WaterGlassIcon( representation ),
                new NumberLineIcon( representation ),
        };
    }
}