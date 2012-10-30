// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.MinusNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.PointColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Renderer for slope equations, with interactive points (x1, y1, x2, y2).
 * Form is: m = y2 - y1 / x2 - x1  = unsimplified value = simplified value
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeEquationNode extends EquationNode {

    private final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> x1, y1, x2, y2; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes
    private PNode undefinedSlopeIndicator;

    public SlopeEquationNode( final Property<Line> interactiveLine,
                              Property<DoubleRange> xRange,
                              Property<DoubleRange> yRange,
                              PhetFont interactiveFont,
                              final PhetFont staticFont,
                              final Color staticColor ) {
        super( staticFont.getSize() );

        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );
        this.x2 = new Property<Double>( interactiveLine.get().x2 );
        this.y2 = new Property<Double>( interactiveLine.get().y2 );

        // Nodes that could appear is all possible ways to write the equation
        // m =
        PNode mNode = new PhetPText( Strings.SYMBOL_SLOPE, staticFont, staticColor );
        PNode interactiveEqualsNode = new PhetPText( "=", staticFont, staticColor );
        // y2 - y2
        PNode y2Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y2Spinner, y2, xRange, new PointColors(), interactiveFont, FORMAT ) );
        PNode numeratorOperatorNode = new PhetPText( "-", staticFont, staticColor );
        PNode y1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y1Spinner, y1, xRange, new PointColors(), interactiveFont, FORMAT ) );
        // fraction line
        PPath interactiveFractionLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later
        // x2 - x1
        PNode x2Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x2Spinner, x2, xRange, new PointColors(), interactiveFont, FORMAT ) );
        PNode denominatorOperatorNode = new PhetPText( "-", staticFont, staticColor );
        PNode x1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x1Spinner, x1, xRange, new PointColors(), interactiveFont, FORMAT ) );
        // = unsimplified value
        final PNode unsimplifiedEqualsNode = new PhetPText( "=", staticFont, staticColor );
        final PText unsimplifiedRiseNode = new PhetPText( "?", staticFont, staticColor );
        final PText unsimplifiedRunNode = new PhetPText( "?", staticFont, staticColor );
        final PPath unsimplifiedFractionLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later
        // = simplified value
        final PNode simplifiedEqualsNode = new PhetPText( "=", staticFont, staticColor );
        final PNode simplifiedMinusSign = new MinusNode( signLineSize, staticColor );
        final PText simplifiedRiseNode = new PhetPText( "?", staticFont, staticColor );
        final PText simplifiedRunNode = new PhetPText( "?", staticFont, staticColor );
        final PPath simplifiedFractionLineNode = new PhetPPath( createFractionLineShape( 10 ), staticColor, null, null ); // correct length will be set later
        final PText undefinedSlopeNode = new PhetPText( Strings.UNDEFINED, staticFont, staticColor );

        // rendering order
        {
            // m =
            addChild( mNode );
            addChild( interactiveEqualsNode );
            // y2 - y1
            addChild( y2Node );
            addChild( numeratorOperatorNode );
            addChild( y1Node );
            // fraction line
            addChild( interactiveFractionLineNode );
            // x2 - x1
            addChild( x2Node );
            addChild( denominatorOperatorNode );
            addChild( x1Node );
            // = unsimplified value
            addChild( unsimplifiedEqualsNode );
            addChild( unsimplifiedRiseNode );
            addChild( unsimplifiedFractionLineNode );
            addChild( unsimplifiedRunNode );
            // = simplified value
            addChild( simplifiedEqualsNode );
            // other nodes are added as needed by dynamic layout
        }

        // static layout
        {
            // m =
            mNode.setOffset( 0, 0 );
            interactiveEqualsNode.setOffset( mNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, mNode.getYOffset() );
            // fraction line
            interactiveFractionLineNode.setOffset( interactiveEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                   interactiveEqualsNode.getFullBoundsReference().getCenterY() + fractionLineYFudgeFactor );
            // y2 - y1
            y2Node.setOffset( interactiveFractionLineNode.getXOffset(),
                              interactiveFractionLineNode.getFullBoundsReference().getMinY() - y2Node.getFullBoundsReference().getHeight() - spinnersYSpacing );
            numeratorOperatorNode.setOffset( y2Node.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                             y2Node.getFullBoundsReference().getCenterY() - ( numeratorOperatorNode.getFullBoundsReference().getHeight() / 2 ) );
            y1Node.setOffset( numeratorOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              y2Node.getYOffset() );
            // fix fraction line length
            final double leftLineLength = y1Node.getFullBoundsReference().getMaxX() - y2Node.getFullBoundsReference().getMinX();
            interactiveFractionLineNode.setPathTo( createFractionLineShape( leftLineLength ) );
            // x2 - x1
            x2Node.setOffset( y2Node.getXOffset(),
                              interactiveFractionLineNode.getFullBoundsReference().getMaxY() + spinnersYSpacing );
            denominatorOperatorNode.setOffset( x2Node.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                               x2Node.getFullBoundsReference().getCenterY() - ( denominatorOperatorNode.getFullBoundsReference().getHeight() / 2 ) );
            x1Node.setOffset( denominatorOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              x2Node.getYOffset() );
            // = unsimplified value
            unsimplifiedEqualsNode.setOffset( interactiveFractionLineNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                              interactiveEqualsNode.getYOffset() );
            unsimplifiedFractionLineNode.setOffset( unsimplifiedEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                    interactiveFractionLineNode.getYOffset() );
            // all other layout is done dynamically, in updateLayout
        }

        // dynamic layout
        final VoidFunction1<Line> updateLayout = new VoidFunction1<Line>() {
            public void apply( Line line ) {

                // Unsimplified: adjust the fraction line length, center the rise and run values
                {
                    unsimplifiedFractionLineNode.setPathTo( createFractionLineShape( Math.max( unsimplifiedRiseNode.getFullBoundsReference().getWidth(), unsimplifiedRunNode.getFullBoundsReference().getWidth() ) ) );
                    //TODO horizontally center the numbers by taking into account the width of any minus signs. Eg, in -2/3, "2" should be centered over "3".
                    unsimplifiedRiseNode.setOffset( unsimplifiedFractionLineNode.getFullBoundsReference().getCenterX() - ( unsimplifiedRiseNode.getFullBoundsReference().getWidth() / 2 ),
                                                    unsimplifiedFractionLineNode.getFullBoundsReference().getMinY() - unsimplifiedRiseNode.getFullBoundsReference().getHeight() - ySpacing );
                    unsimplifiedRunNode.setOffset( unsimplifiedFractionLineNode.getFullBoundsReference().getCenterX() - ( unsimplifiedRunNode.getFullBoundsReference().getWidth() / 2 ),
                                                   unsimplifiedFractionLineNode.getFullBoundsReference().getMaxY() + ySpacing );
                }

                // Simplified
                {
                    simplifiedEqualsNode.setOffset( unsimplifiedFractionLineNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                    unsimplifiedEqualsNode.getYOffset() );

                    // remove all related nodes, then we'll add the ones that are relevant
                    removeChild( simplifiedMinusSign );
                    removeChild( simplifiedRiseNode );
                    removeChild( simplifiedRunNode );
                    removeChild( simplifiedFractionLineNode );
                    removeChild( undefinedSlopeNode );

                    if ( line.undefinedSlope() ) {
                        // "undefined"
                        addChild( undefinedSlopeNode );
                        undefinedSlopeNode.setOffset( simplifiedEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                      simplifiedEqualsNode.getY() );
                    }
                    else if ( line.getSlope() == 0 ) {
                        // 0
                        addChild( simplifiedRiseNode );
                        simplifiedRiseNode.setText( "0" );
                        simplifiedRiseNode.setOffset( simplifiedEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                      simplifiedEqualsNode.getY() );
                    }
                    else {
                        final double nextXOffset;
                        if ( line.getSlope() < 0 ) {
                            // minus sign
                            addChild( simplifiedMinusSign );
                            simplifiedMinusSign.setOffset( simplifiedEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                           simplifiedEqualsNode.getFullBoundsReference().getCenterY() - ( simplifiedMinusSign.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor + slopeSignYOffset );
                            nextXOffset = simplifiedMinusSign.getFullBoundsReference().getMaxX() + operatorXSpacing;
                        }
                        else {
                            nextXOffset = simplifiedEqualsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing;
                        }

                        if ( MathUtil.isInteger( line.getSlope() ) ) {
                            // integer
                            addChild( simplifiedRiseNode );
                            simplifiedRiseNode.setText( FORMAT.format( Math.abs( line.getSlope() ) ) );
                            simplifiedRiseNode.setOffset( nextXOffset, simplifiedEqualsNode.getYOffset() );
                        }
                        else {
                            // fraction
                            addChild( simplifiedFractionLineNode );
                            addChild( simplifiedRiseNode );
                            addChild( simplifiedRunNode );

                            // set absolute values
                            simplifiedRiseNode.setText( FORMAT.format( Math.abs( line.getSimplifiedRise() ) ) );
                            simplifiedRunNode.setText( FORMAT.format( Math.abs( line.getSimplifiedRun() ) ) );

                            // adjust fraction line length
                            simplifiedFractionLineNode.setPathTo( createFractionLineShape( Math.max( simplifiedRiseNode.getFullBoundsReference().getWidth(), simplifiedRunNode.getFullBoundsReference().getWidth() ) ) );

                            // layout, values horizontally centered
                            simplifiedFractionLineNode.setOffset( nextXOffset, unsimplifiedFractionLineNode.getYOffset() );
                            simplifiedRiseNode.setOffset( simplifiedFractionLineNode.getFullBoundsReference().getCenterX() - ( simplifiedRiseNode.getFullBoundsReference().getWidth() / 2 ),
                                                          simplifiedFractionLineNode.getFullBoundsReference().getMinY() - simplifiedRiseNode.getFullBoundsReference().getHeight() - ySpacing );
                            simplifiedRunNode.setOffset( simplifiedFractionLineNode.getFullBoundsReference().getCenterX() - ( simplifiedRunNode.getFullBoundsReference().getWidth() / 2 ),
                                                         simplifiedFractionLineNode.getFullBoundsReference().getMaxY() + ySpacing );
                        }
                    }
                }
            }
        };

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                if ( !updatingControls ) {
                    interactiveLine.set( new Line( x1.get(), y1.get(), x2.get(), y2.get(), interactiveLine.get().color ) );
                }
            }
        };
        lineUpdater.observe( x1, y1, x2, y2 );

        // sync the controls and layout with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            public void apply( Line line ) {

                // Synchronize the controls atomically.
                updatingControls = true;
                {
                    x1.set( line.x1 );
                    y1.set( line.y1 );
                    x2.set( line.x2 );
                    y2.set( line.y2 );
                }
                updatingControls = false;

                // Update the rise & run values
                unsimplifiedRiseNode.setText( FORMAT.format( line.rise ) );
                unsimplifiedRunNode.setText( FORMAT.format( line.run ) );

                // do layout before adding undefined-slope indicator
                updateLayout.apply( line );

                // undefined-slope indicator
                removeChild( undefinedSlopeIndicator );
                if ( line.undefinedSlope() ) {
                    final double centerX = getFullBoundsReference().getCenterX();
                    final double centerY = getFullBoundsReference().getCenterY();
                    undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
                    undefinedSlopeIndicator.setOffset( centerX - ( undefinedSlopeIndicator.getFullBoundsReference().getWidth() / 2 ),
                                                       centerY - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) );
                    addChild( undefinedSlopeIndicator );
                }
            }
        } );
    }

    // Creates a node that displays the general form of this equation.
    public static PNode createGeneralFormNode() {

        final Color color = LGColors.INTERACTIVE_LINE;
        final Font font = new PhetFont( Font.BOLD, 18 );

        // m =
        String htmlLeftSide = MessageFormat.format( "{0} =", Strings.SYMBOL_SLOPE );
        PNode leftSideNode = new HTMLNode( htmlLeftSide, color, font );

        // y2 - y1
        String htmlNumerator = MessageFormat.format( "{0} - {1}", Strings.SYMBOL_Y2, Strings.SYMBOL_Y1 );
        PNode numeratorNode = new HTMLNode( htmlNumerator, color, font );

        // x2 - x1
        String htmlDenominator = MessageFormat.format( "{0} - {1}", Strings.SYMBOL_X2, Strings.SYMBOL_X1 );
        PNode denominatorNode = new HTMLNode( htmlDenominator, color, font );

        // fraction line
        final double length = Math.max( numeratorNode.getFullBoundsReference().getWidth(), denominatorNode.getFullBoundsReference().getWidth() );
        PPath fractionLineNode = new PPath( new Rectangle2D.Double( 0, 0, length, 1 ) );
        fractionLineNode.setPaint( color );
        fractionLineNode.setStroke( null );

        // rendering order
        PNode parentNode = new PNode();
        parentNode.addChild( leftSideNode );
        parentNode.addChild( numeratorNode );
        parentNode.addChild( denominatorNode );
        parentNode.addChild( fractionLineNode );

        // layout
        leftSideNode.setOffset( 0, 0 );
        fractionLineNode.setOffset( leftSideNode.getFullBoundsReference().getMaxX() + 5,
                                    leftSideNode.getFullBoundsReference().getCenterY() - ( fractionLineNode.getFullBoundsReference().getHeight() / 2 ) + 3 );
        numeratorNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( numeratorNode.getFullBoundsReference().getWidth() / 2 ),
                                 fractionLineNode.getFullBoundsReference().getMinY() - numeratorNode.getFullBoundsReference().getHeight() - 1 );
        denominatorNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( denominatorNode.getFullBoundsReference().getWidth() / 2 ),
                                   fractionLineNode.getFullBoundsReference().getMaxY() + 1 );

        return new ZeroOffsetNode( parentNode );
    }
}
