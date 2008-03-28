/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.control.ExampleSubPanel;


public class AlphaRadiationControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AlphaRadiationLegendPanel _legendPanel;
    private AlphaRadiationTimerPanel  _timerPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public AlphaRadiationControlPanel( AlphaRadiationModule alphaRadiationModule, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysics2Resources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _legendPanel = new AlphaRadiationLegendPanel();
        _timerPanel = new AlphaRadiationTimerPanel();
        
        // Add the sub-panels
        {
            addControlFullWidth( _legendPanel );
            addControlFullWidth( _timerPanel );
        }
        
        // Add the Reset All button
        addSeparator();
        addResetAllButton( alphaRadiationModule );

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
