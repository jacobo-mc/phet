/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.util.ShapeUtils;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Voltmeter model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Voltmeter {
    
    private final PDimension PROBE_TIP_SIZE = new PDimension( 0.0005, 0.0015 );
    
    private final BatteryCapacitorCircuit circuit;
    private final World world;
    
    // observable properties
    private final Property<Boolean> visibleProperty;
    private final Property<Point3D> positiveProbeLocationProperty, negativeProbeLocationProperty;
    private final Property<Double> valueProperty;

    public Voltmeter( BatteryCapacitorCircuit circuit, final World world, boolean visible, Point3D positiveProbeLocation, Point3D negativeProbeLocation ) {
       
        this.circuit = circuit;
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void efieldChanged() {
                updateValue();
            }
        });
        
        this.world = world;
        this.visibleProperty = new Property<Boolean>( visible );
        this.positiveProbeLocationProperty = new Property<Point3D>( positiveProbeLocation );
        this.negativeProbeLocationProperty = new Property<Point3D>( negativeProbeLocation );
        this.valueProperty = new Property<Double>( 0d ); // will be properly initialized by updateValue
        
        world.addBoundsObserver( new SimpleObserver() {
            public void update() {
                if ( !world.isBoundsEmpty() ) {
                    constrainProbeLocation( Voltmeter.this.positiveProbeLocationProperty );
                    constrainProbeLocation( Voltmeter.this.negativeProbeLocationProperty );
                }
            }
        } );
        
        updateValue();
    }
    
    private void updateValue() {
        if ( probesAreTouching() ) {
            valueProperty.setValue( 0d );
        }
        else {
            valueProperty.setValue( circuit.getVoltageBetween( getPositiveProbeTipShapeWorld(), getNegativeProbeTipShapeWorld() ) );
        }
    }
    
    private boolean probesAreTouching() {
        return ShapeUtils.intersects( getPositiveProbeTipShapeWorld(), getNegativeProbeTipShapeWorld() );
    }
    
    public void reset() {
        visibleProperty.reset();
        positiveProbeLocationProperty.reset();
        negativeProbeLocationProperty.reset();
        // value property updates other properties are reset
    }
    
    public boolean isVisible() {
        return visibleProperty.getValue();
    }
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            this.visibleProperty.setValue( visible );
        }
    }
    
    public void addVisibleObserver( SimpleObserver o ) {
        visibleProperty.addObserver( o );
    }
    
    public Point3D getPositiveProbeLocationReference() {
        return positiveProbeLocationProperty.getValue();
    }
    
    public void setPositiveProbeLocation( Point3D location ) {
        if ( !location.equals( getPositiveProbeLocationReference() )) {
            this.positiveProbeLocationProperty.setValue( new Point3D.Double( location ) );
            updateValue();
        }
    }
    
    public void addPositiveProbeLocationObserver( SimpleObserver o ) {
        positiveProbeLocationProperty.addObserver( o );
    }
    
    public Point3D getNegativeProbeLocationReference() {
        return negativeProbeLocationProperty.getValue();
    }
    
    public void setNegativeProbeLocation( Point3D location ) {
        if ( !location.equals( getNegativeProbeLocationReference() )) {
            this.negativeProbeLocationProperty.setValue( new Point3D.Double( location ) );
            updateValue();
        }
    }
    
    public void addNegativeProbeLocationObserver( SimpleObserver o ) {
        negativeProbeLocationProperty.addObserver( o );
    }

    public double getValue() {
        return valueProperty.getValue();
    }
    
    public void setValue( double value ) {
        if ( value != getValue() ) {
            this.valueProperty.setValue( value );
        }
    }
    
    public void addValueObserver( SimpleObserver o ) {
        valueProperty.addObserver( o );
    }
    
    private void constrainProbeLocation( Property<Point3D> probeLocation ) {
        if ( !world.contains( probeLocation.getValue() ) ) {
            
            // adjust x coordinate
            double newX = probeLocation.getValue().getX();
            if ( probeLocation.getValue().getX() < world.getBoundsReference().getX() ) {
                newX = world.getBoundsReference().getX();
            }
            else if ( probeLocation.getValue().getX() > world.getBoundsReference().getMaxX() ) {
                newX = world.getBoundsReference().getMaxX();
            }
            
            // adjust y coordinate
            double newY = probeLocation.getValue().getY();
            if ( probeLocation.getValue().getY() < world.getBoundsReference().getY() ) {
                newY = world.getBoundsReference().getY();
            }
            else if ( probeLocation.getValue().getY() > world.getBoundsReference().getMaxY() ) {
                newY = world.getBoundsReference().getMaxY();
            }
            
            // z is fixed
            final double z = probeLocation.getValue().getZ();
            
            probeLocation.setValue( new Point3D.Double( newX, newY, z ) );
        }
    }
    
    public Shape getPositiveProbeTipShapeLocal() {
        return getProbeTipShape( new Point3D.Double() );
    }
    
    public Shape getPositiveProbeTipShapeWorld() {
        Shape shape = getProbeTipShape( positiveProbeLocationProperty.getValue() );
        double theta = -CLConstants.MVT_YAW; //XXX get from mvt?
        AffineTransform t = AffineTransform.getRotateInstance( theta, getPositiveProbeLocationReference().getX(), getPositiveProbeLocationReference().getY() );
        return t.createTransformedShape( shape );
    }
    
    public Shape getNegativeProbeTipShapeLocal() {
        return getProbeTipShape( new Point3D.Double() );
    }
    
    public Shape getNegativeProbeTipShapeWorld() {
        Shape shape = getProbeTipShape( negativeProbeLocationProperty.getValue() );
        double theta = -CLConstants.MVT_YAW; //XXX get from mvt?
        AffineTransform t = AffineTransform.getRotateInstance( theta, getNegativeProbeLocationReference().getX(), getNegativeProbeLocationReference().getY() );
        return t.createTransformedShape( shape );
    }
    
    private Shape getProbeTipShape( Point3D origin ) {
        double x = origin.getX() - ( PROBE_TIP_SIZE.getWidth() / 2 );
        double y = origin.getY();
        return new Rectangle2D.Double( x, y, PROBE_TIP_SIZE.getWidth(), PROBE_TIP_SIZE.getHeight() );
    }
}
