
package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.EquilibriumModel;
import edu.umd.cs.piccolo.util.PDimension;


public class ConcentrationGraphNode extends AbstractConcentrationGraphNode {

    public ConcentrationGraphNode( PDimension outlineSize, AqueousSolution solution ) {
        this( outlineSize );
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    public ConcentrationGraphNode( PDimension outlineSize ) {
        super( outlineSize );
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
    }
    
    private static class ModelViewController implements SolutionListener {

        private final AqueousSolution solution;
        private final ConcentrationGraphNode countsNode;
        
        public ModelViewController( AqueousSolution solution, ConcentrationGraphNode countsNode ) {
            this.solution = solution;
            this.countsNode = countsNode;
            updateView();
        }
        
        public void soluteChanged() {
            updateView();
        }
        
        public void concentrationChanged() {
            updateView();
        }

        public void strengthChanged() {
            updateView();
        }
        
        private void updateView() {
            
            // hide reactant and product bars for pure water
            countsNode.setReactantVisible( !solution.isPureWater() );
            countsNode.setProductVisible( !solution.isPureWater() );
            
            // labels
            Solute solute = solution.getSolute();
            countsNode.setReactantLabel( solute.getSymbol() );
            countsNode.setProductLabel( solute.getConjugateSymbol() );
            
            // molecule representations
            countsNode.setReactantMolecule( solute.getSymbol(), solute.getIcon(), solute.getColor() );
            countsNode.setProductMolecule( solute.getConjugateSymbol(), solute.getConjugateIcon(), solute.getConjugateColor() );
            
            // "negligible" counts
            countsNode.setReactantNegligibleEnabled( solute.isZeroNegligible() );
            
            // counts
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            countsNode.setReactantConcentration( equilibriumModel.getReactantConcentration() );
            countsNode.setProductConcentration( equilibriumModel.getProductConcentration() );
            countsNode.setH3OConcentration( equilibriumModel.getH3OConcentration() );
            countsNode.setOHConcentration( equilibriumModel.getOHConcentration() );
            countsNode.setH2OConcentration( equilibriumModel.getH2OConcentration() );
        }
    }
}
