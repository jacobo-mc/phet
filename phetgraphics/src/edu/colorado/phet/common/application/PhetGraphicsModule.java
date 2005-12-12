/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.help.HelpManager;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

/**
 * This class encapsulates the parts of an application that make up
 * a complete virtual experiment. This includes, but is not limited to, the
 * on-screen controls and view elements that go along with the
 * experiment. Each module has its own model.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetGraphicsModule extends Module {

    ApparatusPanel apparatusPanel;
    HelpManager helpManager;

    /**
     * @param name
     * @param clock
     */
    protected PhetGraphicsModule( String name, AbstractClock clock ) {
        this.name = name;
        this.clock = clock;
        SimStrings.setStrings( "localization/CommonStrings" );
        helpManager = new HelpManager();

        // Handle redrawing while the clock is paused.
        clock.addClockStateListener( new ClockPausedHandler( this ) );
    }

    /**
     * @param name
     * @deprecated
     */
    protected PhetGraphicsModule( String name ) {
        this( name, null );
    }

    protected void init( ApparatusPanel apparatusPanel, JPanel controlPanel, JPanel monitorPanel, BaseModel baseModel ) {
        setApparatusPanel( apparatusPanel );
        setControlPanel( controlPanel );
        setMonitorPanel( monitorPanel );
        setModel( baseModel );
    }

    //-----------------------------------------------------------------
    // Setters and getters
    //-----------------------------------------------------------------

    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
        if( helpManager != null ) {
            helpManager.setComponent( apparatusPanel );
        }
        else {
            helpManager = new HelpManager( apparatusPanel );//TODO fix this.
        }
    }

    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
    }

    public void addGraphic( PhetGraphic graphic, double layer ) {
        getApparatusPanel().addGraphic( graphic, layer );
    }

    protected void add( ModelElement modelElement, PhetGraphic graphic, double layer ) {
        this.addModelElement( modelElement );
        this.addGraphic( graphic, layer );
    }

    protected void remove( ModelElement modelElement, PhetGraphic graphic ) {
        getModel().removeModelElement( modelElement );
        getApparatusPanel().removeGraphic( graphic );
    }

    //-----------------------------------------------------------------
    // Help-related methods
    //-----------------------------------------------------------------

    /**
     * Adds an onscreen help item to the module
     *
     * @param helpItem
     */
    public void addHelpItem( PhetGraphic helpItem ) {
        helpManager.addGraphic( helpItem );
        if( controlPanel != null && controlPanel instanceof ControlPanel ) {
            ( (ControlPanel)controlPanel ).setHelpPanelEnabled( true );
        }
    }

    /**
     * Removes an onscreen help item from the module
     *
     * @param helpItem
     */
    public void removeHelpItem( PhetGraphic helpItem ) {
        helpManager.removeGraphic( helpItem );
        if( controlPanel != null && controlPanel instanceof ControlPanel && helpManager.getNumHelpItems() == 0 ) {
            ( (ControlPanel)controlPanel ).setHelpPanelEnabled( false );
        }
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    //----------------------------------------------------------------
    // Main loop
    //----------------------------------------------------------------

    ////////////////////////////////////////////////////////////////
    // Persistence
    //

//    public void setState( StateDescriptor stateDescriptor ) {
//        stateDescriptor.setState( this );
////        restoreState( (ModuleStateDescriptor)stateDescriptor );
//    }

//    /**
//     * Restores the state of this Module to that specificied in a ModuleStateDescriptor
//     *
//     * @param stateDesriptor
//     */
//    private void restoreState( ModuleStateDescriptor stateDescriptor ) {
//
//        // Remove and clean up the current model
//        AbstractClock clock = PhetApplication.instance().getApplicationModel().getClock();
//        BaseModel oldModel = getModel();
//        oldModel.removeAllModelElements();
//        clock.removeClockTickListener( oldModel );
//
//        // Set up the restored model
//        BaseModel newModel = sd.getModel();
//        clock.addClockTickListener( newModel );
//        setModel( newModel );
//
//        // Set up the restored graphics
//        // Hook all the graphics up to the current apparatus panel
//        MultiMap graphicsMap = sd.getGraphicMap();
//        Iterator it = graphicsMap.iterator();
//        while( it.hasNext() ) {
//            Object obj = it.next();
//            if( obj instanceof PhetGraphic ) {
//                PhetGraphic phetGraphic = (PhetGraphic)obj;
//                phetGraphic.setComponent( getApparatusPanel() );
//            }
//        }
//        getApparatusPanel().getGraphic().setGraphicMap( sd.getGraphicMap() );
//
//        // Force a repaint on the apparatus panel
//        getApparatusPanel().repaint();
//    }

    public void updateGraphics( ClockTickEvent event ) {
        super.updateGraphics( event );
        PhetJComponent.getRepaintManager().updateGraphics();
    }

    public boolean hasHelp() {
        return helpManager.getNumGraphics() > 0;
    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        helpManager.setHelpEnabled( apparatusPanel, h );
    }

    public void refresh() {
        super.refresh();
        // Repaint all dirty PhetJComponents
        PhetJComponent.getRepaintManager().updateGraphics();
        // Paint the apparatus panel
        apparatusPanel.paint();
    }

    protected void handleUserInput() {
        super.handleUserInput();
        getApparatusPanel().handleUserInput();
    }

    public JComponent getSimulationPanel() {
        return getApparatusPanel();
    }

    public Class[] getTransientPropertySources() {
        return new Class[]{PhetImageGraphic.class};
    }
}
