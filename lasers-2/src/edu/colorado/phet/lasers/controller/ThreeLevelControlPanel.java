/**
 * Class: LaserControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ThreeLevelControlPanel extends LaserControlPanel {
//public class ThreeLevelControlPanel extends PhetControlPanel {

    /**
     *
     */
    public ThreeLevelControlPanel() {
        init();
    }

    /**
     *
     */
    private void init() {

        this.setLayout( new GridLayout( 7, 1 ) );
//        this.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        this.setPreferredSize( new Dimension( 160, 300 ) );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );

        LaserSystem laserSystem = (LaserSystem)PhetApplication.instance().getPhysicalSystem();
        this.add( new PumpingBeamControl( laserSystem.getPumpingBeam() ) );
        this.add( new StimulatingBeamControl( laserSystem.getStimulatingBeam() ) );
        this.add( new HighEnergyHalfLifeControl() );
        this.add( new MiddleEnergyHalfLifeControl() );
        ResonatingCavity cavity = laserSystem.getResonatingCavity();
        this.add( new RightMirrorReflectivityControlPanel( cavity ) );
        this.add( new SimulationRateControlPanel( 1, 40, 10 ));

        String s = GraphicsUtil.formatMessage( "Show high to\nmid emissions");
        final JCheckBox showHighToMidEmissionCB = new JCheckBox( s );
        this.add( showHighToMidEmissionCB );
        showHighToMidEmissionCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ((LaserApplication)PhetApplication.instance()).displayHighToMidEmission( showHighToMidEmissionCB.isSelected() );
            }
        } );
    }

    /**
     *
     */
    public void clear() {
    }
}
