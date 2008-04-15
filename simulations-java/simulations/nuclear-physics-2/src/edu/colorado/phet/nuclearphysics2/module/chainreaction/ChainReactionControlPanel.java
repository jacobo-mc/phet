/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationLegendPanel;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModule;

/**
 * This class represents the control panel that presents the legend and allows
 * the user to control some aspects of the chain reaction tab of this sim.
 *
 * @author John Blanco
 */
public class ChainReactionControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ChainReactionLegendPanel _legendPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public ChainReactionControlPanel( ChainReactionModule chainReactionModule, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysics2Resources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new ChainReactionLegendPanel();
        
        // Add the legend panel.
        addControlFullWidth( _legendPanel );
        
        // Add the Reset All button.
        addVerticalSpace( 10 );
        addResetAllButton( chainReactionModule );

    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
}
