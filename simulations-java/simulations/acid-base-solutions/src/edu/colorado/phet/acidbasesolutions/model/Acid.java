package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public abstract class Acid extends Solute {
    
    private final String conjugateSymbol;
    
    protected Acid( String name, String symbol, double strength, String conjugateSymbol ) {
        super( name, symbol, strength );
        this.conjugateSymbol = conjugateSymbol;
    }
    
    public String getConjugateSymbol() {
        return conjugateSymbol;
    }
    
    //----------------------------------------------------------------------------
    // Strong acids
    //----------------------------------------------------------------------------
    
    public abstract static class StrongAcid extends Acid {
        
        private StrongAcid( String name, String symbol, double strength, String conjugateSymbol ) {
            super( name, symbol, strength, conjugateSymbol );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.STRONG_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class HydrochloricAcid extends StrongAcid {
        public HydrochloricAcid() {
            super( ABSStrings.HYDROCHLORIC_ACID, ABSSymbols.HCl, 1E7, ABSSymbols.Cl_MINUS );
        }
    }
    
    public static class PerchloricAcid extends StrongAcid {
        public PerchloricAcid() {
            super( ABSStrings.PERCHLORIC_ACID, ABSSymbols.HClO4, 40, ABSSymbols.ClO4_MINUS );
        }
    }

    public static class CustomStrongAcid extends StrongAcid {

        private static final double DEFAULT_STRENGTH = ABSConstants.STRONG_STRENGTH_RANGE.getMin();

        public CustomStrongAcid() {
            super( ABSStrings.CUSTOM_STRONG_ACID, ABSSymbols.HA, DEFAULT_STRENGTH, ABSSymbols.A_MINUS );
        }

        // public setter for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    //----------------------------------------------------------------------------
    // Weak acids
    //----------------------------------------------------------------------------
    
    public abstract static class WeakAcid extends Acid {
        
        private WeakAcid( String name, String symbol, double strength, String conjugateSymbol ) {
            super( name, symbol, strength, conjugateSymbol );
        }
        
        protected boolean isValidStrength( double strength ) {
            return ABSConstants.WEAK_STRENGTH_RANGE.contains( strength );
        }
    }
    
    public static class ChlorusAcid extends WeakAcid {
        public ChlorusAcid() {
            super( ABSStrings.CHLOROUS_ACID, ABSSymbols.HClO2, 1E-2, ABSSymbols.ClO2_MINUS );
        }
    }
    
    public static class HypochlorusAcid extends WeakAcid {
        public HypochlorusAcid() {
            super( ABSStrings.HYPOCHLOROUS_ACID, ABSSymbols.HClO, 2.9E-8, ABSSymbols.ClO_MINUS );
        }
    }
    
    public static class HydrofluoricAcid extends WeakAcid {
        public HydrofluoricAcid() {
            super( ABSStrings.HYDROFLUORIC_ACID, ABSSymbols.HF, 6.8E-4, ABSSymbols.F_MINUS );
        }
    }
    
    public static class AceticAcid extends WeakAcid {
        public AceticAcid() {
            super( ABSStrings.ACETIC_ACID, ABSSymbols.CH3COOH, 1.8E-5, ABSSymbols.CH3COO_MINUS );
        }
    }
    
    public static class CustomWeakAcid extends WeakAcid {
        
        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();

        public CustomWeakAcid() {
            super( ABSStrings.CUSTOM_WEAK_ACID, ABSSymbols.HA, DEFAULT_STRENGTH, ABSSymbols.A_MINUS );
        }

        // public setter for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }
    
    //----------------------------------------------------------------------------
    // Intermediate acids
    //----------------------------------------------------------------------------
    
    public abstract static class IntermediateAcid extends Acid {
        
        private IntermediateAcid( String name, String symbol, double strength, String conjugateSymbol ) {
            super( name, symbol, strength, conjugateSymbol );
        }
        
        protected boolean isValidStrength( double strength ) {
            // exclusive of intermediate range bounds!
            return ( strength > ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMin() && strength < ABSConstants.INTERMEDIATE_STRENGTH_RANGE.getMax() );
        }
    }

    public static class CustomIntermediateAcid extends IntermediateAcid {

        private static final double DEFAULT_STRENGTH = ABSConstants.WEAK_STRENGTH_RANGE.getMin();

        public CustomIntermediateAcid() {
            super( ABSStrings.CUSTOM_INTERMEDIATE_ACID, ABSSymbols.HA, DEFAULT_STRENGTH, ABSSymbols.A_MINUS );
        }

        // public setter for custom
        public void setStrength( double strength ) {
            super.setStrength( strength );
        }
    }

}
