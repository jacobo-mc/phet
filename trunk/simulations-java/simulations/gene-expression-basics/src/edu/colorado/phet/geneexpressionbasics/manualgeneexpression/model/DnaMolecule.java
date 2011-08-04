// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;

/**
 * This class models a molecule of DNA in the model.  It includes the shape of
 * the two strands of the DNA and the base pairs, defines where the various
 * genes reside, and retains other information about the DNA molecule.  This
 * is an important and central object in the model for this simulation.
 *
 * @author John Blanco
 */
public class DnaMolecule {

    private static final double LEFT_EDGE_X_POS = 0;
    private static final double STRAND_WIDTH = 200; // In picometers.
    private static final double LENGTH_PER_TWIST = 340; // In picometers.
    private static final double BASE_PAIRS_PER_TWIST = 10; // In picometers.
    private static final double DISTANCE_BETWEEN_BASE_PAIRS = LENGTH_PER_TWIST / BASE_PAIRS_PER_TWIST;
    private static final double INTER_STRAND_OFFSET = LENGTH_PER_TWIST * 0.3;

    private DnaStrand strand1;
    private DnaStrand strand2;
    private ArrayList<Gene> genes = new ArrayList<Gene>();
    private ArrayList<BasePair> basePairs = new ArrayList<BasePair>();

    /**
     * Constructor.
     */
    public DnaMolecule() {
        // Create the two strands that comprise the DNA "backbone".
        strand1 = generateDnaStrand( LEFT_EDGE_X_POS, LENGTH_PER_TWIST * 100, true );
        strand2 = generateDnaStrand( LEFT_EDGE_X_POS + INTER_STRAND_OFFSET, LENGTH_PER_TWIST * 100, false );

        // Add in the base pairs between the strands.
        double basePairXPos = INTER_STRAND_OFFSET;
        while ( basePairXPos < strand2.get( strand2.size() - 1 ).getShape().getBounds2D().getMaxX() ) {
            double height = Math.abs( ( Math.sin( ( basePairXPos - INTER_STRAND_OFFSET ) / LENGTH_PER_TWIST * 2 * Math.PI ) -
                                        Math.sin( basePairXPos / LENGTH_PER_TWIST * 2 * Math.PI ) ) ) * STRAND_WIDTH / 2;
            double basePairYPos = ( Math.sin( ( basePairXPos - INTER_STRAND_OFFSET ) / LENGTH_PER_TWIST * 2 * Math.PI ) +
                                    Math.sin( basePairXPos / LENGTH_PER_TWIST * 2 * Math.PI ) ) / 2 * STRAND_WIDTH / 2;
            basePairs.add( new BasePair( new Point2D.Double( basePairXPos, basePairYPos ), height ) );
            basePairXPos += DISTANCE_BETWEEN_BASE_PAIRS;
        }

        // Add the genes.
//        genes.add( new Gene( new Rectangle2D.Double( 5000, -200, 2400, 400 ), new Color( 255, 165, 79, 150 ) ) );
//        genes.add( new Gene( new Rectangle2D.Double( 15000, -200, 3200, 400 ), new Color( 240, 246, 143, 150 ) ) );
//        genes.add( new Gene( new Rectangle2D.Double( 25000, -200, 4000, 400 ), new Color( 205, 255, 112, 150 ) ) );
        genes.add( new Gene( this, new DoubleRange( 5000, 6000 ), Color.BLUE, new DoubleRange( 6000, 8000 ), Color.GREEN ) );
    }

    // Generate a single DNA strand, i.e. one side of the double helix.
    private DnaStrand generateDnaStrand( double initialOffset, double length, boolean initialInFront ) {
        double offset = initialOffset;
        boolean inFront = initialInFront;
        boolean curveUp = true;
        DnaStrand dnaStrand = new DnaStrand();
        while ( offset + LENGTH_PER_TWIST < length ) {
            // Create the next segment.
            DoubleGeneralPath segmentPath = new DoubleGeneralPath();
            segmentPath.moveTo( offset, 0 );
            if ( curveUp ) {
                segmentPath.quadTo( offset + LENGTH_PER_TWIST / 4, STRAND_WIDTH / 2 * 2.0,
                                    offset + LENGTH_PER_TWIST / 2, 0 );
            }
            else {
                segmentPath.quadTo( offset + LENGTH_PER_TWIST / 4, -STRAND_WIDTH / 2 * 2.0,
                                    offset + LENGTH_PER_TWIST / 2, 0 );
            }

            // Add the strand segment to the end of the strand.
            dnaStrand.add( new DnaStrandSegment( segmentPath.getGeneralPath(), inFront ) );
            curveUp = !curveUp;
            inFront = !inFront;
            offset += LENGTH_PER_TWIST / 2;
        }
        return dnaStrand;
    }

    public DnaStrand getStrand1() {
        return strand1;
    }

    public DnaStrand getStrand2() {
        return strand2;
    }

    public ArrayList<Gene> getGenes() {
        return genes;
    }

    public Gene getLastGene() {
        return genes.get( genes.size() - 1 );
    }

    public ArrayList<BasePair> getBasePairs() {
        return basePairs;
    }

    /**
     * Get the position in model space of the leftmost edge of the DNA strand.
     * The Y position is in the vertical center of the strand.
     */
    public Point2D getLeftEdgePos() {
        // Note: Y position of zero is a built-in assumption.  This will need
        // to change if the DNA strand needs to be somewhere else in the Y
        // direction.
        return new Point2D.Double( LEFT_EDGE_X_POS, 0 );
    }

    public double getWidth() {
        return STRAND_WIDTH;
    }

    /**
     * This class defines a segment of the DNA strand.  It is needed because
     * the DNA molecule needs to look like it is 3D, but we are only modeling
     * it as 2D, so in order to create the appearance of a twist between the
     * two strands, we need to track which segments are in front and which are
     * in back.
     */
    public class DnaStrandSegment extends ShapeChangingModelElement {
        public final boolean inFront;

        public DnaStrandSegment( Shape shape, boolean inFront ) {
            super( shape );
            this.inFront = inFront;
        }
    }

    public class DnaStrand extends ArrayList<DnaStrandSegment> {
    }
}
