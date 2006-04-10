/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.DipoleOrientationAgent;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.view.MonitorPanel;

import java.util.List;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import javax.swing.border.TitledBorder;
//import java.awt.*;
import java.awt.*;

/**
 * MriControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriControlPanel extends ControlPanel {
    private ModelSlider fadingMagnetsSlider;
    private MonitorPanel monitorPanel;
    private MriModel model;
    private JComponent fadingMagnetsControl;

    /**
     * Constructor
     *
     * @param module
     */
    public MriControlPanel( MriModuleA module ) {
        model = (MriModel)module.getModel();

        fadingMagnetsControl = new FadingMagnetControl();
        monitorPanel = new MonitorPanel( model );
        monitorPanel.setPreferredSize( new Dimension( 200, 200 ) );

        layoutPanel();
    }

    private void layoutPanel() {
        addControl( fadingMagnetsControl );
        addControl( monitorPanel );
        addControl( new PrecessionControl() );
        addControl( new SpinDeterminationControl() );
    }

    //----------------------------------------------------------------
    // Control classes
    //----------------------------------------------------------------

    private class FadingMagnetControl extends ModelSlider {
        public FadingMagnetControl() {
            super( "Fading Coil Current", "", 0, MriConfig.MAX_FADING_COIL_CURRENT, 0 );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    Electromagnet upperMagnet = model.getUpperMagnet();
                    upperMagnet.setCurrent( getValue() );
                    Electromagnet lowerMagnet = model.getLowerMagnet();
                    lowerMagnet.setCurrent( getValue() );
                }
            } );
        }
    }

    private class PrecessionControl extends ModelSlider {
        public PrecessionControl() {
            super( "Max Precession", "rad", 0, Math.PI, MriConfig.InitialConditions.DIPOLE_PRECESSION );

            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    List dipoles = model.getDipoles();
                    for( int i = 0; i < dipoles.size(); i++ ) {
                        Dipole dipole = (Dipole)dipoles.get( i );
                        dipole.setPrecession( getValue() );
                    }
                }
            } );
        }
    }

    private class SpinDeterminationControl extends JPanel {
        public SpinDeterminationControl() {
            super( new GridBagLayout() );
            JRadioButton deterministicRb = new JRadioButton( "deterministic" );
            deterministicRb.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setSpinDeterminationPolicy( new DipoleOrientationAgent.DeterministicPolicy() );
                }
            } );
            JRadioButton stocasticRB = new JRadioButton( "stocastic" );
            stocasticRB.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setSpinDeterminationPolicy( new DipoleOrientationAgent.StocasticPolicy() );
                }
            } );

            deterministicRb.setSelected( MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY instanceof DipoleOrientationAgent.DeterministicPolicy );
            stocasticRB.setSelected( MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY instanceof DipoleOrientationAgent.StocasticPolicy );

            ButtonGroup btnGrp = new ButtonGroup();
            btnGrp.add( deterministicRb );
            btnGrp.add( stocasticRB );

            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.NORTHWEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 30, 0, 0 ), 0, 0 );
            add( stocasticRB, gbc );
            add( deterministicRb, gbc );

            setBorder( new TitledBorder( "Spin determination policy" ) );
            setPreferredSize( new Dimension( 180, (int)getPreferredSize().getHeight() ) );
        }
    }
}
