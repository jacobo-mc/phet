/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.dna;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.OpticalTweezersApplication;
import edu.colorado.phet.opticaltweezers.control.ChartsControlPanel;
import edu.colorado.phet.opticaltweezers.control.ForcesControlPanel;
import edu.colorado.phet.opticaltweezers.control.MiscControlPanel;
import edu.colorado.phet.opticaltweezers.control.SimulationSpeedControlPanel;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.module.OTAbstractControlPanel;

/**
 * DNAControlPanel is the control panel for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAControlPanel extends OTAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNACanvas _canvas;
    
    private SimulationSpeedControlPanel _simulationSpeedControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private ChartsControlPanel _chartsControlPanel;
    private MiscControlPanel _miscControlPanel;
    private DeveloperControlPanel _developerControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public DNAControlPanel( DNAModule module) {
        super( module );

        _canvas = module.getDNACanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Sub-panels
        DNAModel model = module.getDNAModel();
        _simulationSpeedControlPanel = new SimulationSpeedControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                model.getBead(), model.getFluid(),
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), _canvas.getDNAForceNode() );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, 
                module.getFrame(), DNADefaults.POSITION_HISTOGRAM_DIALOG_OFFSET,
                model.getClock(), model.getBead(), model.getLaser(),
                _canvas.getPotentialEnergyChartNode(), _canvas.getLaserNode() );
        _miscControlPanel = new MiscControlPanel( TITLE_FONT, CONTROL_FONT, 
                module.getFrame(), DNADefaults.FLUID_CONTROLS_DIALOG_OFFSET, 
                _canvas.getRulerNode(), model.getFluid() );
        List forceVectorNodes = new ArrayList();
        forceVectorNodes.add( _canvas.getTrapForceNode() );
        forceVectorNodes.add( _canvas.getFluidDragForceNode() );
        forceVectorNodes.add( _canvas.getDNAForceNode() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, module.getFrame(),
                (OTClock)module.getClock(), model.getBead(), model.getLaser(), 
                model.getDNAStrand(), _canvas.getDNAStrandNode(),
                null /* dnaStrand2 */, null /* dnaStrandNode2 */,
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), _canvas.getDNAForceNode(),
                null /* electricFieldNode */, null /* chargeDistributionNode */ );
        
        // Turn off some features
        _forcesControlPanel.setBrownianMotionCheckBoxVisible( false );
        _forcesControlPanel.setConstantTrapForceCheckBoxVisible( false );
        _miscControlPanel.setFluidVacuumPanelVisible( false );
        
        // Layout
        {
            addControlFullWidth( _simulationSpeedControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _chartsControlPanel );
            addSeparator();
            addControlFullWidth( _miscControlPanel );
            addSeparator();
            if ( OpticalTweezersApplication.isDeveloperControlsEnabled() ) {
                addControlFullWidth( _developerControlPanel );
                addSeparator();
            }
            addResetButton();
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        _chartsControlPanel.setPositionHistogramSelected( false );
        _miscControlPanel.setFluidControlsSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
    public DeveloperControlPanel getDeveloperControlPanel() {
        return _developerControlPanel;
    }
    
    public SimulationSpeedControlPanel getSimulationSpeedControlPanel() {
        return _simulationSpeedControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public ChartsControlPanel getChartsControlPanel() {
        return _chartsControlPanel;
    }
    
    public MiscControlPanel getMiscControlPanel() {
        return _miscControlPanel;
    }
}
