package edu.colorado.phet.acidbasesolutions.model;


public class PureWater {
    
    private static final double W = 55.6; // concentration, mol/L
    private static final double Kw = 1E-14; // equilibrium constant

    public PureWater() {}
    
    public double getConcentration() {
        return W;
    }
    
    public double getEquilibriumConstant() {
        return Kw;
    }

}
