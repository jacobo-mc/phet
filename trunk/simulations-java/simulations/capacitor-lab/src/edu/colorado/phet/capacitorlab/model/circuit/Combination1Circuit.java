// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model.circuit;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CircuitConfig;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryToCapacitors.WireBatteryToCapacitorsBottom;
import edu.colorado.phet.capacitorlab.model.wire.WireBatteryToCapacitors.WireBatteryToCapacitorsTop;
import edu.colorado.phet.capacitorlab.model.wire.WireCapacitorToCapacitors;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model of a circuit with a battery (B), 2 capacitors in series (C1, C2), and one additional in parallel (C3).
 * <p/>
 * <code>
 * |-----|------|
 * |     |      |
 * |     C1     |
 * |     |      |
 * B     |      C3
 * |     |      |
 * |     C2     |
 * |     |      |
 * |-----|------|
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 *         //REVIEW: Remove duplicated code between Combination1Circuit and Combination2Circuit
 */
public class Combination1Circuit extends AbstractCircuit {

    private final Capacitor c1, c2, c3; // references for correlation with javadoc diagram, to improve code readability

    public Combination1Circuit( final CircuitConfig config ) {
        super( CLStrings.COMBINATION_1, config, 3 /* numberOfCapacitors */,
               new CreateCapacitors() {
                   // Creates capacitors, as shown in javadoc diagram.
                   public ArrayList<Capacitor> apply( CircuitConfig config, Integer numberOfCapacitors ) {
                       // Series
                       double x = config.batteryLocation.getX() + config.capacitorXSpacing;
                       double y = config.batteryLocation.getY() - ( 0.5 * config.capacitorYSpacing );
                       final double z = config.batteryLocation.getZ();
                       final Capacitor c1 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );
                       y += config.capacitorYSpacing;
                       final Capacitor c2 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

                       // Parallel
                       x += config.capacitorXSpacing;
                       final Capacitor c3 = new Capacitor( new Point3D.Double( x, y, z ), config.plateWidth, config.plateSeparation, config.dielectricMaterial, config.dielectricOffset, config.mvt );

                       return new ArrayList<Capacitor>() {{
                           add( c1 );
                           add( c2 );
                           add( c3 );
                       }};
                   }
               },
               new CreateWires() {
                   // Creates wires, as shown in javadoc diagram
                   public ArrayList<Wire> apply( final CircuitConfig config, final Battery battery, final ArrayList<Capacitor> capacitors ) {
                       final Capacitor c1 = capacitors.get( 0 );
                       final Capacitor c2 = capacitors.get( 1 );
                       final Capacitor c3 = capacitors.get( 2 );
                       return new ArrayList<Wire>() {{
                           add( new WireBatteryToCapacitorsTop( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, battery, c1, c3 ) );
                           add( new WireCapacitorToCapacitors( config.mvt, CLConstants.WIRE_THICKNESS, c1, c2 ) );
                           add( new WireBatteryToCapacitorsBottom( config.mvt, CLConstants.WIRE_THICKNESS, config.wireExtent, battery, c2, c3 ) );
                       }};
                   }
               } );

        c1 = getCapacitors().get( 0 );
        c2 = getCapacitors().get( 1 );
        c3 = getCapacitors().get( 2 );
        updatePlateVoltages();
    }

    // @see AbstractCircuit.updatePlateVoltages
    @Override protected void updatePlateVoltages() {
        // series
        final double seriesCapacitance = c1.getTotalCapacitance() + c2.getTotalCapacitance();
        c1.setPlatesVoltage( getTotalVoltage() * c1.getTotalCapacitance() / seriesCapacitance );
        c2.setPlatesVoltage( getTotalVoltage() * c2.getTotalCapacitance() / seriesCapacitance );
        // parallel
        c3.setPlatesVoltage( getTotalVoltage() );
    }

    // Gets the wire between the 2 capacitors.
    private Wire getMiddleWire() {
        return getWires().get( 1 );
    }

    // C_total = ( 1 / ( 1/C1 + 1/C2 ) ) + C3
    public double getTotalCapacitance() {
        double C1 = c1.getTotalCapacitance();
        double C2 = c2.getTotalCapacitance();
        double C3 = c3.getTotalCapacitance();
        return ( 1 / ( 1 / C1 + 1 / C2 ) ) + C3;
    }

    // @see ICircuit.getVoltageAt
    public double getVoltageAt( Shape shape ) {
        double voltage = Double.NaN;
        if ( connectedToBatteryTop( shape ) ) {
            voltage = getTotalVoltage();
        }
        else if ( connectedToBatteryBottom( shape ) ) {
            voltage = 0;
        }
        else if ( connectedToC2TopPlate( shape ) ) {
            voltage = c2.getPlatesVoltage();
        }
        return voltage;
    }

    // True if shape is touching part of the circuit that is connected to the battery's top terminal.
    private boolean connectedToBatteryTop( Shape shape ) {
        return getBattery().intersectsTopTerminal( shape ) || getTopWire().intersects( shape ) || c1.intersectsTopPlate( shape ) || c3.intersectsTopPlate( shape );
    }

    // True if shape is touching part of the circuit that is connected to the battery's bottom terminal.
    private boolean connectedToBatteryBottom( Shape shape ) {
        return getBattery().intersectsBottomTerminal( shape ) || getBottomWire().intersects( shape ) || c2.intersectsBottomPlate( shape ) || c3.intersectsBottomPlate( shape );
    }

    // True if shape is touching part of the circuit that is connected to C2's top plate.
    private boolean connectedToC2TopPlate( Shape shape ) {
        return c1.intersectsBottomPlate( shape ) || c2.intersectsTopPlate( shape ) || getMiddleWire().intersects( shape );
    }
}
