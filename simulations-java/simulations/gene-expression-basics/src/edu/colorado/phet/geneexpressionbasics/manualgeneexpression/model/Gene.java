// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;

/**
 * This class is the model representation of a gene on a DNA molecule.  Each
 * gene consists of a regulatory region and a transcribed region.  In real life,
 * a gene is just a collection of base pairs on the DNA strand.  This class
 * essentially says where on the strand the gene exists.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class Gene {

    // Offset from the first base pair in the regulatory region of the gene
    // where the high-affinity site for the transcription factor lives.
    private static final int TRANSCRIPTION_FACTOR_LOCATION_OFFSET = 3;

    private final DnaMolecule dnaMolecule;
    private final Color regulatoryRegionColor;
    private final Color transcribedRegionColor;
    private final IntegerRange regulatoryRegion;
    private final IntegerRange transcribedRegion;
    private final AttachmentSite polymeraseAttachmentSite;
    private final AttachmentSite transcriptionFactorAttachmentSite;

    /**
     * Constructor.
     *
     * @param dnaMolecule            The DNA molecule within which this gene
     *                               exists.
     * @param regulatoryRegion       The range, in terms of base pairs on the
     *                               DNA strand, where this region exists.
     * @param regulatoryRegionColor
     * @param transcribedRegion      The range, in terms of base pairs on the
     *                               DNA strand, where this region exists.
     * @param transcribedRegionColor
     */
    public Gene( DnaMolecule dnaMolecule, IntegerRange regulatoryRegion, Color regulatoryRegionColor,
                 IntegerRange transcribedRegion, Color transcribedRegionColor ) {
        this.dnaMolecule = dnaMolecule;
        this.regulatoryRegion = regulatoryRegion;
        this.regulatoryRegionColor = regulatoryRegionColor;
        this.transcribedRegion = transcribedRegion;
        this.transcribedRegionColor = transcribedRegionColor;

        // Create the special attachment sites.
        polymeraseAttachmentSite = new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( regulatoryRegion.getMax() ), DnaMolecule.Y_POS ), 1 );
        transcriptionFactorAttachmentSite = new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( regulatoryRegion.getMin() + TRANSCRIPTION_FACTOR_LOCATION_OFFSET ), DnaMolecule.Y_POS ), 1 );
    }

    public Color getRegulatoryRegionColor() {
        return regulatoryRegionColor;
    }

    public Color getTranscribedRegionColor() {
        return transcribedRegionColor;
    }

    public double getCenterX() {
        double startX = dnaMolecule.getBasePairXOffsetByIndex( regulatoryRegion.getMin() );
        double endX = dnaMolecule.getBasePairXOffsetByIndex( transcribedRegion.getMax() );
        return startX + ( endX - startX ) / 2;
    }

    public IntegerRange getRegulatoryRegion() {
        return regulatoryRegion;
    }

    public IntegerRange getTranscribedRegion() {
        return transcribedRegion;
    }

    /**
     * Get the attachment site for a location that is contained within this
     * gene.  In many cases, the affinity of the attachment site will be the
     * same as the default for any DNA, but in some cases it may be especially
     * strong.
     *
     * @param basePairIndex - Index of the base pair on the DNA strand, NOT the
     *                      index within this gene.  In the real world,
     *                      affinities are associated with sets of base pairs
     *                      rather than an individual one, so this is a bit of a
     *                      simplification.
     * @return
     */
    public AttachmentSite getPolymeraseAttachmentSite( int basePairIndex ) {
        if ( basePairIndex == regulatoryRegion.getMax() ) {
            // This is the last base pair within the regulatory region.  This
            // is where the polymerase would start transcribing if the
            // appropriate transcription factor is in place.
            if ( !polymeraseAttachmentSite.inUse.get() ) {
                return polymeraseAttachmentSite;
            }
            else {
                // This attachment site is in use, so we return one with an
                // affinity of 0 so that more polymerase won't attach to it.
                return new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ), dnaMolecule.Y_POS ), 0 );
            }
        }
        else {
            // Not a special location as far as this biomolecule is concerned,
            // so return the default affinity.
            return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
        }
    }

    /**
     * Get the attachment site for a location that is contained within this
     * gene.  In many cases, the affinity of the attachment site will be the
     * same as the default for any DNA, but in some cases it may be especially
     * strong.
     *
     * @param basePairIndex - Index of the base pair on the DNA strand, NOT the
     *                      index within this gene.  In the real world,
     *                      affinities are associated with sets of base pairs
     *                      rather than an individual one, so this is a bit of a
     *                      simplification.
     * @return
     */
    public AttachmentSite getTranscriptionFactorAttachmentSite( int basePairIndex ) {
        if ( basePairIndex == regulatoryRegion.getMin() + TRANSCRIPTION_FACTOR_LOCATION_OFFSET ) {
            if ( !transcriptionFactorAttachmentSite.inUse.get() ) {
                return transcriptionFactorAttachmentSite;
            }
            else {
                // This attachment site is in use, so we return one with an
                // affinity of 0 so that another transcription factor won't
                // attach to it.
                return new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ), dnaMolecule.Y_POS ), 0 );
            }
        }
        else {
            // Not a special location as far as this biomolecule is concerned,
            // so return an attachment site with the default affinity.
            return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
        }
    }

    /**
     * Get the attachment site for a base pair that is contained within this
     * gene.  In many cases, the affinity of the attachment site will be the
     * same as the default for any DNA, but in some cases it may be especially
     * strong.
     *
     * @param basePairIndex - Index of the base pair on the DNA strand, NOT the
     *                      index within this gene.
     * @return
     */
    public AttachmentSite getTranscriptionFactorAttachmentSiteForBasePair( int basePairIndex ) {
        if ( basePairIndex == regulatoryRegion.getMax() ) {
            // This is the last base pair within the regulatory region.  This
            // is where the polymerase would start transcribing if the
            // appropriate transcription factor is in place.
            return new AttachmentSite( new Point2D.Double( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ), dnaMolecule.Y_POS ), 1 );
        }
        else {
            // Not a special location as far as this biomolecule is concerned,
            // so return the default affinity.
            return dnaMolecule.createDefaultAffinityAttachmentSite( dnaMolecule.getBasePairXOffsetByIndex( basePairIndex ) );
        }
    }

    public boolean containsBasePair( int basePairIndex ) {
        return regulatoryRegion.contains( basePairIndex ) || transcribedRegion.contains( basePairIndex );
    }
}
