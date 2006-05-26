/**
 * Class: ProfiledNucleusModule
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 7, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.view.PotentialProfilePanel;
import edu.colorado.phet.nuclearphysics.view.LegendPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ProfiledNucleusModule extends NuclearPhysicsModule {

    private Nucleus nucleus;
    private PotentialProfilePanel potentialProfilePanel;

    public ProfiledNucleusModule( String name, IClock clock ) {
        super( name, clock );
        getApparatusPanel().setLayout( new GridLayout( 2, 1 ) );
        potentialProfilePanel = new PotentialProfilePanel( getClock() );
        getApparatusPanel().add( potentialProfilePanel, 1 );
    }

    protected List getLegendClasses() {
        LegendPanel.LegendItem[] legendClasses = new LegendPanel.LegendItem[]{
                LegendPanel.NEUTRON,
                LegendPanel.PROTON,
                LegendPanel.ALPHA_PARTICLE,
                LegendPanel.U235
        };
        return Arrays.asList( legendClasses );
    }

    public void setNucleus( Nucleus nucleus ) {
        this.nucleus = nucleus;
    }

    protected Nucleus getNucleus() {
        return nucleus;
    }

    protected void addNucleus( Nucleus nucleus ) {
        super.addNucleus( nucleus );
        potentialProfilePanel.addPotentialProfile( nucleus );
    }

    protected void addNucleus( Nucleus nucleus, Color color ) {
        super.addNucleus( nucleus );
        potentialProfilePanel.addNucleus( nucleus, color );
    }

    protected PotentialProfilePanel getPotentialProfilePanel() {
        return potentialProfilePanel;
    }

    public void clear() {
        getPotentialProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }
}
