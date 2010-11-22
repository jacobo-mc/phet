/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.ColoredSeparator.BlackSeparator;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;

/**
 * Controls related to the capacitor's dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricPropertiesControlPanel extends PhetTitledPanel {
    
    private final Capacitor capacitor;
    private final SimpleObserver dielectricConstantObserver;
    private final DielectricMaterialControl materialControl;
    private final DielectricConstantControl constantControl;
    private final DielectricChargesControl chargesControl;
    
    private DielectricMaterial material;

    public DielectricPropertiesControlPanel( DielectricModel model, CapacitorNode capacitorNode ) {
        super( CLStrings.DIELECTRIC );
        
        this.capacitor = model.getCapacitor();
        
        this.material = capacitor.getDielectricMaterial();
        dielectricConstantObserver = new SimpleObserver() {
            public void update() {
                handleDielectricConstantChanged();
            }
        };
        
        materialControl = new DielectricMaterialControl( model.getDielectricMaterials(), model.getCapacitor().getDielectricMaterial() );
        materialControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                capacitor.setDielectricMaterial( materialControl.getMaterial() );
            }
        });
        
        constantControl = new DielectricConstantControl( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        constantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( capacitor.getDielectricMaterial() instanceof CustomDielectricMaterial ) {
                    ( (CustomDielectricMaterial) capacitor.getDielectricMaterial() ).setDielectricConstant( constantControl.getValue() );
                }
            }
        } );
        
        chargesControl = new DielectricChargesControl( capacitorNode.getDielectricNode() );
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setFill( Fill.HORIZONTAL );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( materialControl );
        innerPanel.add( new BlackSeparator() );
        innerPanel.add( constantControl );
        innerPanel.add( new BlackSeparator() );
        innerPanel.add( chargesControl );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // observe capacitor
        capacitor.addDielectricMaterialObserver( new SimpleObserver() {
            public void update() {
                handleDielectricMaterialChanged();
            }
        });
        
        // observe dielectric
        material.addDielectricConstantObserver( dielectricConstantObserver );
    }
    
    private void handleDielectricMaterialChanged() {
        
        DielectricMaterial material = capacitor.getDielectricMaterial();
        
        // update controls
        materialControl.setMaterial( material );
        constantControl.setEnabled( material instanceof CustomDielectricMaterial );
        constantControl.setValue( material.getDielectricConstant() );
        
        // rewire dielectric constant observer
        this.material.removeDielectricConstantObserver( dielectricConstantObserver );
        this.material = material;
        this.material.addDielectricConstantObserver( dielectricConstantObserver );
    }
    
    private void handleDielectricConstantChanged() {
        constantControl.setValue( capacitor.getDielectricMaterial().getDielectricConstant() );
    }
}
