/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.*;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:08:56 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGGun extends HighIntensityGunGraphic implements FireParticle {
    private DGParticle dgParticle;
    private FireButton fireOne;

    public DGGun( SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
    }

    public DGParticle getDgParticle() {
        return dgParticle;
    }

    protected ImagePComboBox initComboBox() {
        dgParticle = new DGParticle( this );
        final HighIntensityBeam[] mybeams = new HighIntensityBeam[]{
                new ParticleBeam( dgParticle )
        };
        setBeams( mybeams );
        final ImagePComboBox imageComboBox = new ImagePComboBox( mybeams );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( mybeams[index] );
            }
        } );
        return imageComboBox;
    }

    public void clearAndFire() {
//        clearWavefunction();
        fireParticle();
//        fireOne.setEnabled( false );
    }

    protected GunControlPanel createGunControlPanel() {
        if( fireOne == null ) {
            fireOne = new FireButton( this );
        }
        GunControlPanel gunControlPanel = new GunControlPanel( getSchrodingerPanel() );
        gunControlPanel.setFillNone();
        gunControlPanel.add( fireOne );
        gunControlPanel.setFillHorizontal();
        gunControlPanel.add( getIntensitySlider() );//todo unprotect data
        gunControlPanel.add( getAlwaysOnCheckBox() );//todo unprotect data
        return gunControlPanel;
    }

    private void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void fireParticle() {
        double intensityScale = dgParticle.getIntensityScale();
        dgParticle.setIntensityScale( 1.0 );
        dgParticle.fireParticle();
        dgParticle.setIntensityScale( intensityScale );
        notifyFireListeners();
    }
}
