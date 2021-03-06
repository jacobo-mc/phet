// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.equalitylab.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode.Element;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.Colors;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.equalitylab.model.EqualityLabModel;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractions.fractionsintro.intro.view.DynamicNumberLineNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionControlNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractions.fractionsintro.intro.view.RepresentationNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.WaterGlassSetNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.MovableSliceLayer;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel.HorizontalBarIcon;
import edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel.NumberLineIcon;
import edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel.PieIcon;
import edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel.VerticalBarIcon;
import edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel.WaterGlassIcon;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.common.view.Colors.HORIZONTAL_SLICE_COLOR;
import static edu.colorado.phet.fractions.common.view.Colors.LIGHT_PINK;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.horizontalBarRadioButton;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.verticalBarRadioButton;
import static edu.colorado.phet.fractions.fractionsintro.equalitylab.model.EqualityLabModel.scaledFactorySet;
import static edu.colorado.phet.fractions.fractionsintro.intro.view.Representation.*;
import static edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.PieSetNode.CreateEmptyCellsNode;
import static edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.PieSetNode.CreateNode;
import static java.awt.Color.black;
import static java.awt.Color.orange;

/**
 * Canvas for "Equality Lab" tab.
 *
 * @author Sam Reid
 */
public class EqualityLabCanvas extends AbstractFractionsCanvas {

    public EqualityLabCanvas( final EqualityLabModel model ) {

        final SettableProperty<Representation> leftRepresentation = model.leftRepresentation;
        final ObservableProperty<Representation> rightRepresentation = model.rightRepresentation;

        //Control panel for choosing different representations, can be split into separate controls for each display
        final double representationControlPanelScale = 1.0;
        final int padding = 7;
        final List<Element<Representation>> icons = getIcons( leftRepresentation, FractionsIntroSimSharing.leftSide );
        final RadioButtonStripControlPanelNode<Representation> node = new RadioButtonStripControlPanelNode<Representation>( leftRepresentation, icons, padding ) {{
            scale( representationControlPanelScale );
        }};
        final RichPNode leftRepresentationControlPanel = new ZeroOffsetNode( node ) {{

            //Added extra offset for experimentation with "dynamic number line"
            setOffset( 60, INSET );
        }};

        // Instead of "same" and "number line" radio buttons, what we envisioned on the right hand side
        // was one button that is the same as the representation on the left, and another that was the number line.
        // So for instance if you were on the green circle, and clicked on the red square,
        // the button on the right would change to the red square.
        final RichPNode rightRepresentationControlPanel = new ZeroOffsetNode( new PNode() {{
            leftRepresentation.addObserver( new VoidFunction1<Representation>() {
                public void apply( final Representation representation ) {
                    removeAllChildren();

                    final List<Element<Boolean>> icons = new ArrayList<Element<Boolean>>();
                    if ( representation == PIE ) {
                        icons.add( new Element<Boolean>( new PieIcon( new Property<Representation>( PIE ), LIGHT_PINK ), true, chain( Components.pieRadioButton, FractionsIntroSimSharing.rightSide ) ) );
                    }
                    else if ( representation == HORIZONTAL_BAR ) {
                        icons.add( new Element<Boolean>( new HorizontalBarIcon( new Property<Representation>( HORIZONTAL_BAR ), LIGHT_PINK ) {{
                            scale( 0.8 );
                        }}, true, chain( horizontalBarRadioButton, FractionsIntroSimSharing.rightSide ) ) );
                    }
                    else if ( representation == VERTICAL_BAR ) {
                        final VerticalBarIcon verticalBarIcon = new VerticalBarIcon( scaledFactorySet.verticalSliceFactory, LIGHT_PINK );
                        PNode node = verticalBarIcon.getNode();
                        node.scale( 0.8 );
                        icons.add( new Element<Boolean>( node, true, chain( verticalBarRadioButton, FractionsIntroSimSharing.rightSide ) ) );
                    }
                    else if ( representation == WATER_GLASSES ) {
                        icons.add( new Element<Boolean>( new WaterGlassIcon( new Property<Representation>( WATER_GLASSES ), LIGHT_PINK ) {{
                            scale( 0.8 );
                        }}, true, chain( Components.waterGlassesRadioButton, FractionsIntroSimSharing.rightSide ) ) );
                    }
                    else if ( representation == NUMBER_LINE ) {
                        icons.add( new Element<Boolean>( new NumberLineIcon( new Property<Representation>( NUMBER_LINE ) ), true, chain( Components.numberLineRadioButton, FractionsIntroSimSharing.rightSide ) ) );
                    }

                    icons.add( new Element<Boolean>( new NumberLineIcon( new Property<Representation>( NUMBER_LINE ) ), false, chain( Components.numberLineRadioButton, FractionsIntroSimSharing.leftSide ) ) );
                    addChild( new RadioButtonStripControlPanelNode<Boolean>( model.sameAsLeft, icons, padding ) {{
                        scale( representationControlPanelScale );
                    }} );
                }
            } );
        }} ) {{
            setOffset( leftRepresentationControlPanel.getMaxX() + 110, leftRepresentationControlPanel.getCenterY() - getFullBounds().getHeight() / 2 );
        }};

        addChildren( leftRepresentationControlPanel, rightRepresentationControlPanel );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, black, orange ) {{
            setConfirmationEnabled( false );
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, STAGE_SIZE.height - getFullBounds().getHeight() - INSET );
        }};
        addChild( resetAllButtonNode );

        //Show the icon text on the left so that it will be far from the main fraction display in the play area
        //Text is on the right for Intro tab, on the left for Equality Lab tab
        boolean iconTextOnTheRight = false;

        addPrimaryRepresentationNodes( model, leftRepresentation, model.pieSet, iconTextOnTheRight );

        //Show the pie set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, PIE, new PNode() {{
            model.scaledPieSet.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.scaledPieSet.get() ) );
                    addChild( new MovableSliceLayer( model.scaledPieSet.get(), CreateNode, model.scaledPieSet, rootNode, null ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the horizontal bar set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, HORIZONTAL_BAR, new PNode() {{
            model.rightHorizontalBars.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.rightHorizontalBars.get() ) );
                    addChild( new MovableSliceLayer( model.rightHorizontalBars.get(), CreateNode, model.rightHorizontalBars, rootNode, null ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the vertical bar set node when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, VERTICAL_BAR, new PNode() {{
            model.rightVerticalBars.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    addChild( CreateEmptyCellsNode.f( model.rightVerticalBars.get() ) );
                    addChild( new MovableSliceLayer( model.rightVerticalBars.get(), CreateNode, model.rightVerticalBars, rootNode, null ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Show the water glasses when selected for the right-side
        addChild( new RepresentationNode( rightRepresentation, WATER_GLASSES, new PNode() {{
            model.rightWaterGlasses.addObserver( new SimpleObserver() {
                public void update() {
                    removeAllChildren();
                    final Shape shape = scaledFactorySet.waterGlassSetFactory.createSlicesForBucket( model.denominator.get(), 1, model.getRandomSeed() ).head().getShape();
                    addChild( WaterGlassSetNode.createEmptyCellsNode( LIGHT_PINK, shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight() ).f( model.rightWaterGlasses.get() ) );
                }
            } );
            setChildrenPickable( false );
        }} ) );

        //Number line
        addChild( new DynamicNumberLineNode( model.scaledNumerator, null, model.scaledDenominator, model.rightRepresentation.valueEquals( NUMBER_LINE ),
                                             model.maximum, 15,
                                             new Color( Colors.LIGHT_PINK.getRed(), Colors.LIGHT_PINK.getGreen(), Colors.LIGHT_PINK.getBlue(), 200 ), true,
                                             model.denominator ) {{
            setOffset( 585, 445 ); //Numbers manually tuned so it would look about right

            //Can't interact with right-side representations
            setPickable( false );
            setChildrenPickable( false );
        }} );

        //The fraction control node.  In front so the user doesn't accidentally press a flying pie slice when they are trying to toggle the spinner
        final ZeroOffsetNode fractionControl = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum, 6 ) {{
            setScale( 0.75 );
        }} ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() - 50, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionControl );

        final PhetPText equalsSign = new PhetPText( "=", new PhetFont( 120 ) ) {{
            setOffset( fractionControl.getMaxX() + 10, fractionControl.getCenterY() - getFullHeight() / 2 );
        }};
        addChild( equalsSign );

        addChild( new ZeroOffsetNode( new ScaledUpFractionNode( model.numerator, model.denominator, model.scale ) {{
            setScale( 0.75 );
        }} ) {{
            setOffset( equalsSign.getMaxX() + 10, equalsSign.getCenterY() - getFullHeight() / 2 );
        }} );
    }

    //Add representations for the left side
    private void addPrimaryRepresentationNodes( final EqualityLabModel model,
                                                final SettableProperty<Representation> representation,
                                                SettableProperty<PieSet> pieSet,

                                                //Text is on the right for intro, on the left for equality lab
                                                boolean iconTextOnTheRight ) {
        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( representation, PIE, new PieSetNode( pieSet, rootNode, iconTextOnTheRight ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode, iconTextOnTheRight ) ) );

        //For vertical bars
        addChild( new RepresentationNode( representation, VERTICAL_BAR, new PieSetNode( model.verticalBarSet, rootNode, iconTextOnTheRight ) ) );

        //For water glasses
        final Rectangle2D b = model.getWaterGlassSetNodeBounds();
        addChild( new RepresentationNode( representation, WATER_GLASSES, new WaterGlassSetNode( model.waterGlassSet, rootNode, Colors.CUP_COLOR, b.getWidth(), b.getHeight(), iconTextOnTheRight ) ) );
    }

    private List<Element<Representation>> getIcons( SettableProperty<Representation> representation, String type ) {
        final PNode verticalBarIcon = new VerticalBarIcon( scaledFactorySet.verticalSliceFactory, Colors.VERTICAL_SLICE_COLOR ).getNode();
        verticalBarIcon.scale( 0.8 );
        return Arrays.asList( new Element<Representation>( new PieIcon( representation, Colors.CIRCLE_COLOR ), PIE, chain( Components.pieRadioButton, type ) ),
                              new Element<Representation>( new HorizontalBarIcon( representation, HORIZONTAL_SLICE_COLOR ) {{
                                  scale( 0.8 );
                              }}, HORIZONTAL_BAR, chain( horizontalBarRadioButton, type ) ),
                              new Element<Representation>( verticalBarIcon, VERTICAL_BAR, chain( verticalBarRadioButton, type ) ),
                              new Element<Representation>( new WaterGlassIcon( representation, Colors.CUP_COLOR ) {{
                                  scale( 0.8 );
                              }}, WATER_GLASSES, chain( Components.waterGlassesRadioButton, type ) )
        );
    }
}