/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

/**
 * An aqueous solution whose solute is a strong acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StrongAcidSolution extends AqueousSolution {

    // Marker class for solutes that are strong acids.
    public static abstract class StrongAcid extends Acid {
        public StrongAcid( Molecule molecule, Molecule conjugateMolecule, double strength ) {
            super( molecule, conjugateMolecule, strength );
        }
    }
    
    public StrongAcidSolution( StrongAcid solute, double concentration ) {
        super( solute, concentration );
    }

    // [HA] = 0
    public double getReactantConcentration() {
        return 0;
    }
    
    // [A-] = c
    public double getProductConcentration() {
        return getInitialConcentration();
    }
    
    // [H3O+] = c
    public double getH3OConcentration() {
        return getInitialConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getOHConcentration() {
        return getWaterEquilibriumConstant() / getH3OConcentration();
    }
    
    // [H2O] = W - c
    public double getH2OConcentration() {
        return getWaterConcentration() - getInitialConcentration();
    }
}
