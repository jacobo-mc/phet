// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.DynamicValueNode;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.MinusNode;
import edu.colorado.phet.linegraphing.common.view.PlusNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RiseSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SlopeSpinnerNode.RunSpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.PointColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerStateIndicator.SlopeColors;
import edu.colorado.phet.linegraphing.common.view.UndefinedSlopeIndicator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Renderer for point-slope equations, with optional interactivity of point and slope.
 * General point-slope form is: (y - y1) = m(x - x1)
 * <p/>
 * Spinners are used to increment/decrement parts of the equation that are specified as being interactive.
 * Non-interactive parts of the equation are expressed in a form that is typical of how the equation
 * would normally be written. For example, if the slope is -1, then only the sign is written, not "-1".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeEquationNode extends EquationNode {

    private final Property<Double> rise, run, x1, y1; // internal properties that are connected to spinners
    private boolean updatingControls; // flag that allows us to update all controls atomically when the model changes

    // Nodes that appear in all possible forms of the equation "(y - y1) = m(x - x1)"
    private final PNode yLeftParenNode, yNode, yOperatorNode, y1Node, yRightParenNode, equalsNode;
    private final PNode slopeMinusSignNode, riseNode, runNode, xLeftParenNode, xNode, xOperatorNode, x1Node, xRightParenNode;
    private final PNode y1MinusSignNode; // for "y = -y1" case
    private final PPath fractionLineNode;
    private PNode undefinedSlopeIndicator;

    // Constructor for a static line. Note that static lines are automatically simplified.
    public PointSlopeEquationNode( Line line, PhetFont font, Color color ) {
        this( new Property<Line>( line.simplified() ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              new Property<DoubleRange>( new DoubleRange( 0, 1 ) ),
              false, false, false,
              font, font, color );
    }

    public PointSlopeEquationNode( final Property<Line> interactiveLine,
                                   Property<DoubleRange> riseRange,
                                   Property<DoubleRange> runRange,
                                   Property<DoubleRange> x1Range,
                                   Property<DoubleRange> y1Range,
                                   final boolean interactiveX1,
                                   final boolean interactiveY1,
                                   final boolean interactiveSlope,
                                   PhetFont interactiveFont,
                                   final PhetFont staticFont,
                                   final Color staticColor ) {
        super( staticFont.getSize() );

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.x1 = new Property<Double>( interactiveLine.get().x1 );
        this.y1 = new Property<Double>( interactiveLine.get().y1 );

        // Determine the max width of the rise and run spinners.
        double maxSlopeSpinnerWidth = computeMaxSlopeSpinnerWidth( riseRange, runRange, interactiveFont, FORMAT );

        // nodes: (y-y1) = m(x-x1)
        yLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        yNode = new PhetPText( Strings.SYMBOL_Y, staticFont, staticColor );
        yOperatorNode = new PNode(); // parent for + or - node
        if ( interactiveY1 ) {
            y1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.y1Spinner, y1, y1Range, new PointColors(), interactiveFont, FORMAT ) );
        }
        else {
            y1Node = new DynamicValueNode( y1, staticFont, staticColor, true ); // displayed as absolute value
        }
        yRightParenNode = new PhetPText( ")", staticFont, staticColor );
        y1MinusSignNode = new MinusNode( signLineSize, staticColor ); // for y=-y1 case
        equalsNode = new PhetPText( "=", staticFont, staticColor );
        slopeMinusSignNode = new MinusNode( signLineSize, staticColor );
        if ( interactiveSlope ) {
            riseNode = new ZeroOffsetNode( new RiseSpinnerNode( UserComponents.riseSpinner, rise, run, riseRange, new SlopeColors(), interactiveFont, FORMAT ) );
            runNode = new ZeroOffsetNode( new RunSpinnerNode( UserComponents.runSpinner, rise, run, runRange, new SlopeColors(), interactiveFont, FORMAT ) );
        }
        else {
            riseNode = new DynamicValueNode( rise, staticFont, staticColor, true ); // displayed as absolute value
            runNode = new DynamicValueNode( run, staticFont, staticColor, true ); // displayed as absolute value
        }
        fractionLineNode = new PPath( new Rectangle2D.Double( 0, 0, maxSlopeSpinnerWidth, fractionLineThickness ) ) {{
            setStroke( null );
            setPaint( staticColor );
        }};
        xLeftParenNode = new PhetPText( "(", staticFont, staticColor );
        xNode = new PhetPText( Strings.SYMBOL_X, staticFont, staticColor );
        xOperatorNode = new PNode(); // parent for + or - node
        if ( interactiveX1 ) {
            x1Node = new ZeroOffsetNode( new SpinnerNode( UserComponents.x1Spinner, x1, x1Range, new PointColors(), interactiveFont, FORMAT ) );
        }
        else {
            x1Node = new DynamicValueNode( x1, staticFont, staticColor, true ); // displayed as absolute value
        }
        xRightParenNode = new PhetPText( ")", staticFont, staticColor );

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                if ( !updatingControls ) {
                    interactiveLine.set( Line.createPointSlope( x1.get(), y1.get(), rise.get(), run.get(), interactiveLine.get().color ) );
                }
            }
        };
        lineUpdater.observe( rise, run, x1, y1 );

        // sync the controls and layout with the model
        interactiveLine.addObserver( new VoidFunction1<Line>() {

            public void apply( Line line ) {

                // Synchronize the controls atomically.
                updatingControls = true;
                {
                    rise.set( interactiveSlope ? line.rise : line.simplified().rise );
                    run.set( interactiveSlope ? line.run : line.simplified().run );
                    x1.set( line.x1 );
                    y1.set( line.y1 );
                }
                updatingControls = false;

                // Update the layout
                updateLayout( line, interactiveX1, interactiveY1, interactiveSlope, staticFont, staticColor );
            }
        } );
    }

    /*
     * Updates the layout to match the desired form of the equation.
     * This is based on which parts of the equation are interactive, and what the
     * non-interactive parts of the equation should look like when written in simplified form.
     */
    private void updateLayout( Line line, boolean interactiveX1, boolean interactiveY1, boolean interactiveSlope, PhetFont staticFont, Color staticColor ) {

        // Start by adding all nodes, then we'll selectively remove some nodes based on the desired form of the equation.
        removeAllChildren();
        if ( !line.isSlopeDefined() && !interactiveX1 && !interactiveY1 && !interactiveSlope ) {
            // slope is undefined and nothing is interactive
            addChild( new UndefinedSlopeNode( line, staticFont, staticColor ) );
            return;
        }
        else {
            // nodes that may be interactive first, so we can more easily identify layout problems
            addChild( y1Node );
            addChild( x1Node );
            addChild( riseNode );
            addChild( runNode );

            addChild( yLeftParenNode );
            addChild( yNode );
            addChild( yOperatorNode );
            addChild( yRightParenNode );
            addChild( y1MinusSignNode );
            addChild( equalsNode );
            addChild( slopeMinusSignNode );
            addChild( fractionLineNode );
            addChild( xLeftParenNode );
            addChild( xNode );
            addChild( xOperatorNode );
            addChild( xRightParenNode );
        }

        // Change the operators to account for the signs of x1 and y1.
        {
            xOperatorNode.removeAllChildren();
            if ( interactiveX1 || line.x1 >= 0 ) {
                xOperatorNode.addChild( new MinusNode( operatorLineSize, staticColor ) );
            }
            else {
                xOperatorNode.addChild( new PlusNode( operatorLineSize, staticColor ) );
            }

            yOperatorNode.removeAllChildren();
            if ( interactiveY1 || line.y1 >= 0 ) {
                yOperatorNode.addChild( new MinusNode( operatorLineSize, staticColor ) );
            }
            else {
                yOperatorNode.addChild( new PlusNode( operatorLineSize, staticColor ) );
            }
        }

        if ( line.rise == 0 && !interactiveSlope && !interactiveX1 ) {

            // y1 is on the right side of the equation

            removeChild( yLeftParenNode );
            removeChild( yOperatorNode );
            removeChild( yRightParenNode );
            removeChild( slopeMinusSignNode );
            removeChild( riseNode );
            removeChild( runNode );
            removeChild( fractionLineNode );
            removeChild( xLeftParenNode );
            removeChild( xNode );
            removeChild( xOperatorNode );
            removeChild( x1Node );
            removeChild( xRightParenNode );

            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            if ( interactiveY1 || line.y1 >= 0 ) {
                removeChild( y1MinusSignNode );
                // y = y1
                y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // y = -y1
                y1MinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                           equalsNode.getFullBoundsReference().getCenterY() - ( y1MinusSignNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
                y1Node.setOffset( y1MinusSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing,
                                  yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            }
        }
        else {
            // y1 is on the left side of the equation

            removeChild( y1MinusSignNode );

            // left side of equation (y term)
            PNode previousNode;
            double previousXOffset = 0;
            removeChild( y1MinusSignNode );
            // (y - y1)
            yLeftParenNode.setOffset( 0, 0 );
            yNode.setOffset( yLeftParenNode.getFullBoundsReference().getMaxX() + parenXSpacing,
                             yLeftParenNode.getYOffset() );
            yOperatorNode.setOffset( yNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                     equalsNode.getFullBoundsReference().getCenterY() - ( yOperatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
            y1Node.setOffset( yOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                              yNode.getFullBoundsReference().getCenterY() - ( y1Node.getFullBoundsReference().getHeight() / 2 ) );
            yRightParenNode.setOffset( y1Node.getFullBoundsReference().getMaxX() + parenXSpacing,
                                       yNode.getYOffset() );

            // =
            equalsNode.setOffset( yRightParenNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                  yNode.getYOffset() );

            // slope
            if ( interactiveSlope ) {
                // (rise/run), where rise and run are spinners, and the sign is integrated into the spinners
                removeChild( slopeMinusSignNode );
                fractionLineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                            equalsNode.getFullBoundsReference().getCenterY() + 2 );
                riseNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                    fractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - spinnersYSpacing );
                runNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                   fractionLineNode.getFullBoundsReference().getMinY() + spinnersYSpacing );
                previousNode = fractionLineNode;
                previousXOffset = slopeXSpacing;
            }
            else {
                // slope is not interactive, so here we put it in the desired form

                // slope properties, used to determine correct form
                final boolean undefinedSlope = ( line.run == 0 );
                final boolean zeroSlope = ( line.getSlope() == 0 );
                final boolean unitySlope = ( Math.abs( line.getSlope() ) == 1 );
                final boolean integerSlope = ( Math.abs( line.simplified().run ) == 1 );
                final boolean positiveSlope = ( line.getSlope() > 0 );
                final boolean fractionalSlope = ( !zeroSlope && !unitySlope && !integerSlope );

                // adjust fraction line width, use max width of rise or run
                double lineWidth = Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() );
                fractionLineNode.setPathTo( new Rectangle2D.Double( 0, 0, lineWidth, fractionLineThickness ) );

                // decide whether to include the slope minus sign
                if ( positiveSlope ) {
                    // no sign
                    removeChild( slopeMinusSignNode );
                    previousNode = equalsNode;
                    previousXOffset = relationalOperatorXSpacing;
                }
                else {
                    // -
                    slopeMinusSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                                  equalsNode.getFullBoundsReference().getCenterY() - ( slopeMinusSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor + slopeSignYOffset );
                    previousNode = slopeMinusSignNode;
                    previousXOffset = ( fractionalSlope ? fractionSignXSpacing : integerSignXSpacing );
                }

                if ( undefinedSlope || fractionalSlope ) {
                    // rise/run
                    fractionLineNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset,
                                                equalsNode.getFullBoundsReference().getCenterY() + 2 );
                    riseNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                        fractionLineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
                    runNode.setOffset( fractionLineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                                       fractionLineNode.getFullBoundsReference().getMinY() + ySpacing );
                    previousNode = fractionLineNode;
                    previousXOffset = slopeXSpacing;
                }
                else if ( zeroSlope ) {
                    // 0
                    removeChild( slopeMinusSignNode );
                    removeChild( fractionLineNode );
                    removeChild( runNode );
                    riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
                    previousNode = riseNode;
                    previousXOffset = slopeXSpacing;
                }
                else if ( unitySlope ) {
                    // no slope term
                    removeChild( fractionLineNode );
                    removeChild( riseNode );
                    removeChild( runNode );
                }
                else if ( integerSlope ) {
                    // N
                    removeChild( fractionLineNode );
                    removeChild( runNode );
                    riseNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset, yNode.getYOffset() );
                    previousNode = riseNode;
                    previousXOffset = slopeXSpacing;
                }
                else {
                    throw new IllegalStateException( "programming error, didn't handle some slope case" );
                }
            }

            // x term
            if ( interactiveX1 || line.rise != 0 ) {
                // (x - x1)
                xLeftParenNode.setOffset( previousNode.getFullBoundsReference().getMaxX() + previousXOffset,
                                          yNode.getYOffset() );
                xNode.setOffset( xLeftParenNode.getFullBoundsReference().getMaxX() + parenXSpacing,
                                 yNode.getYOffset() );
                xOperatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( xOperatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
                x1Node.setOffset( xOperatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                  xNode.getFullBoundsReference().getCenterY() - ( x1Node.getFullBoundsReference().getHeight() / 2 ) );
                xRightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + parenXSpacing,
                                           yNode.getYOffset() );
            }
            else if ( line.rise == 0 ) {
                // no x term
                removeChild( xLeftParenNode );
                removeChild( xNode );
                removeChild( xOperatorNode );
                removeChild( x1Node );
                removeChild( xRightParenNode );
            }
            else {
                throw new IllegalStateException( "programming error, didn't handle some x-term case" );
            }
        }

        // remove any previous undefined-slope indicator
        if ( undefinedSlopeIndicator != null ) {
            removeChild( undefinedSlopeIndicator );
            undefinedSlopeIndicator = null;
        }

        // undefined-slope indicator, added after layout has been done
        if ( !line.isSlopeDefined() ) {
            undefinedSlopeIndicator = new UndefinedSlopeIndicator( getFullBoundsReference().getWidth(), getFullBoundsReference().getHeight() );
            undefinedSlopeIndicator.setOffset( 0, fractionLineNode.getFullBoundsReference().getCenterY() - ( undefinedSlopeIndicator.getFullBoundsReference().getHeight() / 2 ) + 2 );
            addChild( undefinedSlopeIndicator );
        }
    }
}
