// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationNode extends PhetPNode {

    protected static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    protected final int pointSize; // point size of the static font used to render the equation

    /*
    * This controls the vertical offset of the slope's sign.
    * Zero is vertically centered on the equals sign, positive values move it down, negative move it up.
    * This was created because there was a great deal of discussion and disagreement about where the sign should be placed.
    */
    protected final double slopeSignYOffset;

    // fudge factors for horizontal lines, to vertically center them with equals sign (set by visual inspection)
    protected final double slopeSignYFudgeFactor;
    protected final double operatorYFudgeFactor;
    protected final double fractionLineYFudgeFactor;
    protected final double undefinedSlopeYFudgeFactor;

    protected final float fractionLineThickness; // thickness of the fraction divisor line
    protected final PDimension operatorLineSize; // size of the lines used to create + and - operators
    protected final PDimension signLineSize; // size of the lines used to create + and - signs

    // spacing between components of an equation (set by visual inspection)
    protected final double integerSignXSpacing; // spacing between a sign and the integer to the right of it
    protected final double fractionSignXSpacing; // spacing between a sign and the fraction to the right of it
    protected final double slopeXSpacing; // spacing between the slope and what's to the right of it
    protected final double operatorXSpacing; // space around an operator (eg, +)
    protected final double relationalOperatorXSpacing; // space around the relational operator (eg, =)
    protected final double parenXSpacing; // space between a parenthesis and the thing it encloses
    protected final double spinnersYSpacing; // y spacing between spinners and fraction line
    protected final double ySpacing; // all y spacing

    protected EquationNode( int pointSize ) {

        this.pointSize = pointSize;

        // Compute dimensions and layout offsets as percentages of the font's point size.
        slopeSignYOffset = 0;
        slopeSignYFudgeFactor = 0.07 * pointSize;
        operatorYFudgeFactor = 0.07 * pointSize;
        fractionLineYFudgeFactor = 0.07 * pointSize;
        undefinedSlopeYFudgeFactor = 0.07 * pointSize;
        fractionLineThickness = 0.06f * pointSize;
        operatorLineSize = new PDimension( 0.54 * pointSize, 0.07 * pointSize );
        signLineSize = new PDimension( 0.54 * pointSize, 0.11 * pointSize );
        integerSignXSpacing = 0.18 * pointSize;
        fractionSignXSpacing = 0.36 * pointSize;
        slopeXSpacing = 0.15 * pointSize; //TODO have separate spacing for fraction vs integer slope, this is a bit big for integer slopes
        operatorXSpacing = 0.25 * pointSize;
        relationalOperatorXSpacing = 0.35 * pointSize;
        parenXSpacing = 0.07 * pointSize;
        ySpacing = 0.1 * pointSize;
        spinnersYSpacing = 0.2 * pointSize;
    }

    // Creates a plus or minus sign. Sign is distinct from operator, so that they can have different looks.
    protected PNode createSignNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( signLineSize, color );
        }
        else {
            return new MinusNode( signLineSize, color );
        }
    }

    // Creates a plus or minus operator. Operator is distinct from sign, so that they can have different looks.
    protected PNode createOperatorNode( double value, Color color ) {
        if ( value >= 0 ) {
            return new PlusNode( operatorLineSize, color );
        }
        else {
            return new MinusNode( operatorLineSize, color );
        }
    }

    // Creates the horizontal line that separates numerator and denominator in a fraction (slope, in our case.)
    protected PNode createFractionLineNode( double width, Color color ) {
        PPath node = new PPath( new Rectangle2D.Double( 0, 0, width, fractionLineThickness ) );
        node.setStroke( null );
        node.setPaint( color );
        return node;
    }

    // When slope is undefined, we display "undefined" in place of an equation.
    protected static class UndefinedSlopeNode extends StaticEquationNode {
        public UndefinedSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );
            assert ( !line.isSlopeDefined() );
            setPickable( false );
            addChild( new PhetPText( MessageFormat.format( Strings.SLOPE_UNDEFINED, Strings.SYMBOL_X, line.x1 ), font, color ) );
        }
    }
}
