/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d;

import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 11, 2005
 * Time: 8:15:29 PM
 * Copyright (c) Jan 11, 2005 by Sam Reid
 */

public class SimpleControlPanel extends IForceControl {
    private FreeBodyDiagramSuite fbdSuite;
    private JCheckBox frictionCheckBox;
    private BarrierCheckBox barriers;
    private Force1DModule simpleForceModule;

    public SimpleControlPanel( final Force1DModule simpleForceModule ) {
        super( simpleForceModule );
        this.simpleForceModule = simpleForceModule;

        JButton moreControls = new JButton( "More Controls!" );
        moreControls.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                simpleForceModule.setAdvancedControlPanel();
            }
        } );
        add( moreControls );
        frictionCheckBox = new JCheckBox( "Friction", true );
        frictionCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                simpleForceModule.setFrictionEnabled( frictionCheckBox.isSelected() );
            }
        } );

        fbdSuite = new FreeBodyDiagramSuite( simpleForceModule );
        fbdSuite.addTo( this );
        if( Toolkit.getDefaultToolkit().getScreenSize().width < 1200 ) {
            super.removeTitle();
        }


        add( frictionCheckBox );
        barriers = new BarrierCheckBox( simpleForceModule );
        add( barriers );
        super.setHelpPanelEnabled( true );
        simpleForceModule.getForceModel().getPlotDeviceModel().addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                setChangesEnabled( true );
            }

            public void playbackStarted() {
                setChangesEnabled( false );
            }

            public void playbackPaused() {
                setChangesEnabled( true );
            }

            public void playbackFinished() {
                setChangesEnabled( true );
            }
        } );
        ObjectSelectionPanel osp = new ObjectSelectionPanel( simpleForceModule.getImageElements(), this );
        add( osp );
    }

    private void setChangesEnabled( boolean enabled ) {
        barriers.setEnabled( enabled );
        frictionCheckBox.setEnabled( enabled );
    }

    public void updateGraphics() {
        fbdSuite.updateGraphics();
    }

    public void reset() {
        fbdSuite.reset();
    }

    public void setup( Force1dObject imageElement ) {
        simpleForceModule.setObject( imageElement );
    }

    public void handleUserInput() {
        fbdSuite.handleUserInput();
    }
}
