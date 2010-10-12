/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricCanvas;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for meter settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetersControlPanel extends PhetTitledPanel {
    
    private final JCheckBox capacitanceCheckBox, chargeCheckBox, energyCheckBox, voltmeterCheckBox, eFieldDetectorCheckBox;
    
    public MetersControlPanel( final DielectricCanvas canvas ) {
        super( CLStrings.TITLE_METERS );
        
        // Capacitance meter
        {
            final PNode meter = canvas.getCapacitanceMeterNode();
            capacitanceCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CAPACITANCE );
            capacitanceCheckBox.setSelected( meter.getVisible() );
            capacitanceCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( capacitanceCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        capacitanceCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // Plate Charge meter
        {
            final PNode meter = canvas.getChargeMeterNode();
            chargeCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_CHARGE );
            chargeCheckBox.setSelected( meter.getVisible() );
            chargeCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( chargeCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        chargeCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // Energy meter
        {
            final PNode meter = canvas.getEnergyMeterNode();
            energyCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_ENERGY );
            energyCheckBox.setSelected( meter.getVisible() );
            energyCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( energyCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        energyCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // Voltmeter
        {
            final PNode meter = canvas.getVoltMeterNode();
            voltmeterCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_VOLTMETER );
            voltmeterCheckBox.setSelected( meter.getVisible() );
            voltmeterCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( voltmeterCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        voltmeterCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // E-field Detector
        {
            final PNode meter = canvas.getEFieldDetectorNode();
            eFieldDetectorCheckBox = new JCheckBox( CLStrings.CHECKBOX_METER_EFIELD_DETECTOR );
            eFieldDetectorCheckBox.setSelected( meter.getVisible() );
            eFieldDetectorCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    meter.setVisible( eFieldDetectorCheckBox.isSelected() );
                }
            } );
            meter.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                        eFieldDetectorCheckBox.setSelected( meter.getVisible() );
                    }
                }
            } );
        }
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( capacitanceCheckBox );
        innerPanel.add( chargeCheckBox );
        innerPanel.add( energyCheckBox );
        innerPanel.add( voltmeterCheckBox );
        innerPanel.add( eFieldDetectorCheckBox );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
