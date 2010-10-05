/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the total dielectric charge.
 * Spacing of positive and negative charges remains constant, and they appear in positive/negative pairs.
 * The spacing between the positive/negative pairs changes proportional to Q_excess_dielectric.
 * Outside the capacitor, the spacing between the pairs is at a minimum to reprsent no charge.
 * <p>
 * All model coordinates are relative to the dielectric's local coordinate frame,
 * where the origin is at the 3D geometric center of the dielectric.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricTotalChargeNode extends PhetPNode {
    
    private static final int SPACING_BETWEEN_PAIRS = 30; // view coordinates
    private static final DoubleRange SPACING_BETWEEN_CHARGES = new DoubleRange( 4, 0.45 * SPACING_BETWEEN_PAIRS ); // view coordinates
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PNode parentNode; // parent node for charges

    public DielectricTotalChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        this.parentNode = new PComposite();
        addChild( parentNode );
        
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void capacitanceChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
            @Override
            public void voltageChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );
        
        update();
    }
    
    /**
     * Update the node when it becomes visible.
     */
    @Override
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
        }
    }
    
    private void update() {
        
        // remove existing charges
        parentNode.removeAllChildren();
        
        // spacing between charges
        final double excessCharge = circuit.getExcessDielectricPlateCharge();
        final double spacingBetweenCharges = getSpacingBetweenCharges( excessCharge );
        
        // spacing between pairs
        final double spacingBetweenPairs = mvt.viewToModel( SPACING_BETWEEN_PAIRS );
        
        // rows and columns
        final double dielectricWidth = circuit.getCapacitor().getPlateSideLength();
        final double dielectricHeight = circuit.getCapacitor().getDielectricHeight();
        final double dielectricDepth = dielectricWidth;
        final int rows = (int) ( dielectricHeight / spacingBetweenPairs );
        final int columns = (int) ( dielectricWidth / spacingBetweenPairs );
        
        // margins and offsets
        final double xMargin = ( dielectricWidth - ( columns * spacingBetweenPairs ) ) / 2;
        final double yMargin = ( dielectricHeight - ( rows * spacingBetweenPairs ) ) / 2;
        final double zMargin = xMargin;
        final double offset = spacingBetweenPairs / 2;
        
        // polarity
        final Polarity polarity = ( excessCharge >= 0 ) ? Polarity.POSITIVE : Polarity.NEGATIVE;
        
        // front face
        double xPlateEdge = -( dielectricWidth / 2 ) + ( dielectricWidth - circuit.getCapacitor().getDielectricOffset() );
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                
                ChargePairNode pairNode = new ChargePairNode();
                parentNode.addChild( pairNode );
                
                double x = -( dielectricWidth / 2 ) + offset + xMargin + ( column * spacingBetweenPairs );
                double y = yMargin + offset + ( row * spacingBetweenPairs );
                double z = ( -dielectricDepth / 2 );
                Point2D p = mvt.modelToView( x, y, z );
                pairNode.setOffset( p );
                
                if ( x <= xPlateEdge ) {
                    pairNode.setSpacing( spacingBetweenCharges, polarity );
                }
                else {
                    pairNode.setSpacing( SPACING_BETWEEN_CHARGES.getMin(), polarity );
                }
            }
        }
        
        // side face
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                
                ChargePairNode pairNode = new ChargePairNode();
                parentNode.addChild( pairNode );
                
                double x = ( dielectricWidth / 2 );
                double y = yMargin + offset + ( row * spacingBetweenPairs );
                double z = ( -dielectricDepth / 2 ) + offset + zMargin + ( column * spacingBetweenPairs );
                Point2D p = mvt.modelToView( x, y, z );
                pairNode.setOffset( p );
                
                if ( circuit.getCapacitor().getDielectricOffset() == 0 ) {
                    pairNode.setSpacing( spacingBetweenCharges, polarity );
                }
                else {
                    pairNode.setSpacing( SPACING_BETWEEN_CHARGES.getMin(), polarity );
                }
            }
        }
    }
    
    private double getSpacingBetweenCharges( double charge ) {
        double absCharge = Math.abs( charge );
        double maxCharge = BatteryCapacitorCircuit.getMaxExcessDielectricPlateCharge();
        double percent = absCharge / maxCharge;
        return SPACING_BETWEEN_CHARGES.getMin() + ( percent * SPACING_BETWEEN_CHARGES.getLength() );
    }
    
    private static class ChargePairNode extends PComposite {
        
        private final PNode positiveNode, negativeNode;
        
        public ChargePairNode() {
            positiveNode = new PositiveChargeNode();
            addChild( positiveNode );
            negativeNode = new NegativeChargeNode();
            addChild( negativeNode );
        }
        
        public void setSpacing( double spacing, Polarity polarity ) {
            double yOffset = ( polarity == Polarity.POSITIVE ) ? -( spacing / 2 ) : ( spacing / 2 );
            positiveNode.setOffset( positiveNode.getXOffset(), yOffset );
            negativeNode.setOffset( negativeNode.getXOffset(), -yOffset );
        }
    }
}
