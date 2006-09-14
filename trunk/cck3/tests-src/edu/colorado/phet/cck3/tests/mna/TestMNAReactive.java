/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.tests.mna;

import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.analysis.ModifiedNodalAnalysis_Orig;
import edu.colorado.phet.cck3.model.components.Battery;
import edu.colorado.phet.cck3.model.components.Capacitor;
import edu.colorado.phet.cck3.model.components.Inductor;
import edu.colorado.phet.cck3.model.components.Resistor;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 3:08:18 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class TestMNAReactive extends NodeAnalysisTest {
    public static void main( String[] args ) {
        new TestMNAReactive().start();
    }

    private void start() {
        Resistor r2 = newResistor( 1 );
        r2.setDebugLabel( "R2" );

        Capacitor c2 = newCapacitor( 2 );
        c2.setDebugLabel( "C2" );

        Capacitor c1 = newCapacitor( 3 );
        c1.setDebugLabel( "C1" );

        Inductor L1 = newInductor( 4 );
        L1.setDebugLabel( "L1" );

        Resistor r1 = newResistor( 5 );
        r1.setDebugLabel( "R1" );

        Battery bat = newBattery( 13 );
        bat.setDebugLabel( "V1" );

        Circuit circuit = new Circuit( kl );
        circuit.addBranch( r2 );
        circuit.addBranch( c2 );
        circuit.addBranch( c1 );
        circuit.addBranch( L1 );
        circuit.addBranch( bat );
        circuit.addBranch( r1 );

        Junction j0 = combine( circuit, bat.getStartJunction(), c1.getStartJunction() );
        j0 = combine( circuit, j0, L1.getStartJunction() );
        j0 = combine( circuit, j0, r1.getStartJunction() );
        Junction j1 = combine( circuit, c2.getEndJunction(), c1.getEndJunction() );
        j1 = combine( circuit, j1, L1.getEndJunction() );
        j1 = combine( circuit, j1, r1.getEndJunction() );
        Junction j2 = combine( circuit, c2.getStartJunction(), r2.getEndJunction() );
        Junction j3 = combine( circuit, bat.getEndJunction(), r2.getStartJunction() );
        while( circuit.numJunctions() > 0 ) {
            circuit.remove( circuit.junctionAt( 0 ) );
        }
        circuit.addJunction( j0 );
        circuit.addJunction( j1 );
        circuit.addJunction( j2 );
        circuit.addJunction( j3 );

        circuit.setTime( 6 );
        new ModifiedNodalAnalysis_Orig().applyMNA( circuit );
//        new ModifiedNodalAnalysis().apply( circuit );
        System.out.println( r1.getCurrent() );
    }
}
