/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.controls.SlitControlPanel;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.SmoothIntensityDisplay;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.gun.HighIntensityGun;
import edu.colorado.phet.qm.view.gun.Photon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityPanel extends SchrodingerPanel {
    private IntensityModule intensityModule;
    private SmoothIntensityDisplay smoothIntensityDisplay;
    private boolean smoothScreen = false;
    private PhetGraphic slitControlGraphic;
    private SlitControlPanel slitControlPanel;
    private SplitColorMap splitColorMap;
    private HighIntensityGun highIntensityGun;

    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        highIntensityGun = createGun();
        setGunGraphic( highIntensityGun );
        getIntensityDisplay().setHighIntensityMode();

//        setSplitGraphics();
        setNormalGraphics();
        smoothIntensityDisplay = new SmoothIntensityDisplay( getIntensityDisplay() );
        setSmoothScreen( false );

        PhetGraphic ds = getDoubleSlitPanelGraphic();
        getDoubleSlitPanel().addDoubleSlitCheckBoxListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setControlsEnabled( getDoubleSlitPanel().isDoubleSlitEnabled() );
            }
        } );

        slitControlPanel = new SlitControlPanel( intensityModule );
        slitControlGraphic = PhetJComponent.newInstance( this, slitControlPanel );
        addGraphic( slitControlGraphic );
        slitControlGraphic.setLocation( ds.getX(), ds.getY() + ds.getHeight() + 5 );

//        getIntensityDisplay().getDetectorSheet().setFadeEnabled( true );
//        getIntensityDisplay().getDetectorSheet().addFadeCheckBox();
//        getIntensityDisplay().getDetectorSheet().setHighIntensityMode();

        splitColorMap = new SplitColorMap( intensityModule.getSplitModel(), this );//this.intensityModule.getSplitModel() );
        setControlsEnabled( getDoubleSlitPanel().isDoubleSlitEnabled() );
//        putBelow( getDoubleSlitPanelGraphic(), slitControlGraphic, 3 );
        setDisplayPhotonColor( super.getDisplayPhotonColor() );
        getIntensityDisplay().getDetectorSheet().getDetectorSheetPanel().setBrightness();
    }

    public PhetGraphic getSlitControlGraphic() {
        return slitControlGraphic;
    }

    private void setControlsEnabled( boolean doubleSlitEnabled ) {
        slitControlPanel.setEnabled( doubleSlitEnabled );
    }

    protected HighIntensityGun createGun() {
        HighIntensityGun gun = new HighIntensityGun( this );
        return gun;
    }

    public SlitControlPanel getSlitControlPanel() {
        return slitControlPanel;
    }

    public void setSplitMode( boolean splitMode ) {
        if( splitMode ) {
            setSplitGraphics();
        }
        else {
            setNormalGraphics();
        }
    }

    public void setSmoothScreen( boolean smoothScreen ) {
        if( smoothScreen != this.smoothScreen ) {
            this.smoothScreen = smoothScreen;
            getIntensityDisplay().clearScreen();
            if( smoothScreen ) {
                getIntensityDisplay().getDetectorSheet().setSaveButtonVisible( true );
            }
        }
    }

    protected void updateScreen() {
        if( smoothScreen ) {
            smoothIntensityDisplay.updateValues();
        }
        else {
            getIntensityDisplay().tryDetecting();
        }
    }

    public void setSplitGraphics() {
        setColorMap( splitColorMap );
    }

    private void setColorMap( ColorMap colorMap ) {
        getWavefunctionGraphic().setWavefunctionColorMap( colorMap );
    }

    public void setNormalGraphics() {
        setColorMap( getWavefunctionGraphic().getMagnitudeColorMap() );
    }

    public boolean isSmoothScreen() {
        return smoothScreen;
    }

    public SmoothIntensityDisplay getSmoothIntensityDisplay() {
        return smoothIntensityDisplay;
    }

    public SplitColorMap getSplitColorMap() {
        return splitColorMap;
    }

    public void setDisplayPhotonColor( Photon photon ) {
        super.setDisplayPhotonColor( photon );
        if( splitColorMap != null ) {
            splitColorMap.setRootColor( photon == null ? null : new PhotonColorMap.ColorData( photon.getWavelengthNM() ) );
        }
        if( smoothIntensityDisplay != null ) {
            smoothIntensityDisplay.setPhotonColor( photon );
        }
    }

    public PhotonColorMap.ColorData getRootColor() {
        return highIntensityGun.getRootColor();
    }

    public void setFadeEnabled( boolean selected ) {
        super.setFadeEnabled( selected );
        smoothIntensityDisplay.setFadeEnabled( selected );
    }
}
