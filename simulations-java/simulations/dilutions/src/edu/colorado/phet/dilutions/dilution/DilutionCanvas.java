// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsColors;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.ConcentrationDisplayNode;
import edu.colorado.phet.dilutions.common.view.FancyEqualsNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionCanvas extends AbstractDilutionsCanvas {

    // properties common to all 3 beakers
    private static final double BEAKER_SCALE_X = 0.33;
    private static final double BEAKER_SCALE_Y = 0.50;
    private static final PhetFont BEAKER_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PDimension BEAKER_LABEL_SIZE = new PDimension( 100, 50 );

    public DilutionCanvas( final DilutionModel model, Frame parentFrame ) {

        // Solution beaker, with solution inside of it
        final BeakerNode solutionBeakerNode = new BeakerNode( model.solution, model.getMaxBeakerVolume(), BEAKER_SCALE_X, BEAKER_SCALE_Y, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT, Strings.SOLUTION );
        final PDimension cylinderSize = solutionBeakerNode.getCylinderSize();
        final double cylinderEndHeight = solutionBeakerNode.getCylinderEndHeight();
        SolutionNode solutionNode = new SolutionNode( cylinderSize, cylinderEndHeight, model.solution, model.getDiutionVolumeRange() );

        // M1 control (Solution concentration)
        PDimension concentrationBarSize = new PDimension( 20, cylinderSize.getHeight() + 50 );
        DilutionsSliderNode concentrationSliderNode = new DilutionsSliderNode( Strings.CONCENTRATION_M1, Strings.ZERO, Strings.HIGH,
                                                                               concentrationBarSize,
                                                                               new GradientPaint( 0f, 0f, model.solute.solutionColor, 0f, (float) concentrationBarSize.getHeight(), DilutionsColors.WATER_COLOR ),
                                                                               new Color( 0, 0, 0, 0 ), /* invisible track background */
                                                                               model.solutionConcentration, model.getConcentrationRange() );

        // V1 control (Solution volume), sized to match tick marks on the beaker
        final double solutionVolumeSliderHeight = ( model.getSolutionVolumeRange().getLength() / model.getMaxBeakerVolume() ) * cylinderSize.getHeight();
        DilutionsSliderNode solutionVolumeSliderNode = new DilutionsSliderNode( Strings.VOLUME_V1, Strings.EMPTY, Strings.SMALL,
                                                                                new PDimension( 5, solutionVolumeSliderHeight ),
                                                                                model.solution.volume, model.getSolutionVolumeRange() );

        // Water beaker, with water inside of it
        final BeakerNode waterBeakerNode = new BeakerNode( model.water, model.getMaxBeakerVolume(), BEAKER_SCALE_X, BEAKER_SCALE_Y, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT );
        final PDimension waterCylinderSize = waterBeakerNode.getCylinderSize();
        final double waterCylinderEndHeight = waterBeakerNode.getCylinderEndHeight();
        SolutionNode waterNode = new SolutionNode( waterCylinderSize, waterCylinderEndHeight, model.water, model.getDiutionVolumeRange() );

        // "=" that separates left and right sides of dilution equation
        PNode equalsNode = new FancyEqualsNode();

        // dilution beaker, with solution inside of it
        final BeakerNode dilutionBeakerNode = new BeakerNode( model.dilution, model.getMaxBeakerVolume(), BEAKER_SCALE_X, BEAKER_SCALE_Y, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT, Strings.DILUTION );
        SolutionNode dilutionNode = new SolutionNode( cylinderSize, cylinderEndHeight, model.dilution, model.getDiutionVolumeRange() );

        // M2 display (Dilution concentration)
        ConcentrationDisplayNode dilutionConcentrationNode = new ConcentrationDisplayNode( Strings.CONCENTRATION_M2, concentrationBarSize,
                                                                                           model.dilution, model.getConcentrationRange() );

        // V2 control (Dilution volume), sized to match tick marks on the beaker
        final double dilutionVolumeSlider = ( model.getDiutionVolumeRange().getLength() / model.getMaxBeakerVolume() ) * cylinderSize.getHeight();
        DilutionsSliderNode dilutionVolumeSliderNode = new DilutionsSliderNode( Strings.VOLUME_V2, Strings.SMALL, Strings.BIG,
                                                                                new PDimension( 5, dilutionVolumeSlider ),
                                                                                model.dilution.volume, model.getDiutionVolumeRange() );

        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { model }, parentFrame, 18, Color.BLACK, new Color( 235, 235, 235 ) ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( concentrationSliderNode );
            addChild( solutionNode );
            addChild( solutionBeakerNode );
            addChild( solutionVolumeSliderNode );
            addChild( waterNode );
            addChild( waterBeakerNode );
            addChild( dilutionNode );
            addChild( dilutionBeakerNode );
            addChild( dilutionVolumeSliderNode );
            addChild( dilutionConcentrationNode );
            addChild( equalsNode );
            addWorldChild( resetAllButtonNode ); // don't add to root node, so this button isn't involved in centering of rootNode
        }

        // layout, all beakers vertically aligned
        {
            final double waterBeakerYOffset = 0;
            // far left, vertically aligned with bottom of beakers
            concentrationSliderNode.setOffset( 5 - PNodeLayoutUtils.getOriginXOffset( concentrationSliderNode ),
                                               waterBeakerYOffset + waterCylinderSize.getHeight() - concentrationBarSize.getHeight() );
            // to right of M1, vertically aligned with ticks on Solution beaker
            solutionVolumeSliderNode.setOffset( concentrationSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionVolumeSliderNode ) + 5,
                                                waterBeakerYOffset + waterCylinderSize.getHeight() - solutionVolumeSliderHeight );
            // to right of V1
            solutionBeakerNode.setOffset( solutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionBeakerNode ) + 5,
                                          waterBeakerYOffset );
            // in the same coordinate frame as the Solution beaker
            solutionNode.setOffset( solutionBeakerNode.getOffset() );
            // to right of the Solution beaker
            waterBeakerNode.setOffset( solutionBeakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( waterBeakerNode ) + 5,
                                       waterBeakerYOffset );
            // in the same coordinate frame as the Water beaker
            waterNode.setOffset( waterBeakerNode.getOffset() );
            // to right of the Water beaker
            equalsNode.setOffset( waterBeakerNode.getFullBoundsReference().getMaxX() + 5,
                                  waterBeakerNode.getYOffset() + ( waterCylinderSize.getHeight() / 2 ) - ( equalsNode.getFullBoundsReference().getHeight() / 2 ) );
            // to right of Water equals sign, vertically aligned with bottom of beakers
            dilutionConcentrationNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 20,
                                                 waterBeakerNode.getFullBoundsReference().getMaxY() - dilutionConcentrationNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( dilutionConcentrationNode ) );
            // to right of M2, vertically aligned with ticks on Dilution beaker
            dilutionVolumeSliderNode.setOffset( dilutionConcentrationNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionVolumeSliderNode ) + 8,
                                                waterBeakerNode.getYOffset() );
            // to right of V2
            dilutionBeakerNode.setOffset( dilutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionBeakerNode ) + 15,
                                          waterBeakerNode.getYOffset() );
            // in the same coordinate frame as the Dilution beaker
            dilutionNode.setOffset( dilutionBeakerNode.getOffset() );
            // upper-right corner of stage
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - 50, 50 );
        }
        scaleRootNodeToFitStage();
        centerRootNodeOnStage();
    }
}
