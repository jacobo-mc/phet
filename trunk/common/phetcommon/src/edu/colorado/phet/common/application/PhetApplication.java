/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or 
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
* 
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import edu.colorado.phet.common.util.PropertiesLoader;
import edu.colorado.phet.common.view.ITabbedModulePane;
import edu.colorado.phet.common.view.JTabbedModulePane;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.PhetProjectConfig;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Properties;

/**
 * The top-level class for PhET applications.
 * <p/>
 * The prefered method of creating and starting a PhetApplication is shown here:
 * <code>
 * <br>
 * PhetApplication myApp = new PhetApplication( args, "Title", "Description", "Version, frameSetup );<br>
 * myApp.addModule(module1);<br>
 * myApp.addModule(module2);<br>
 * myApp.addModule(module3);<br>
 * myApp.startApplication();<br>
 * </code>
 * <p/>
 *
 * @author ?
 * @version $Revision$
 */
public class PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    /**
     * Mechanism for determining which graphics subsystem we're using
     */
    private static final String DEBUG_MENU_ARG = "-d";
    private static PhetApplication latestInstance = null;
    private static ArrayList phetApplications = new ArrayList();
    private TabbedPaneType tabbedPaneType;
    private Properties simulationProperties;

    private volatile PhetProjectConfig projectConfig;


    public PhetApplication( String[] args, PhetProjectConfig config, FrameSetup frameSetup ) {
        this( args, config.getName(), config.getDescription(), config.getVersion().formatSmart(), frameSetup );

        this.projectConfig = config;
    }

    public PhetApplication( String[] args, PhetProjectConfig config, FrameSetup frameSetup, TabbedPaneType tabbedPaneType ) {
        this( args, config.getName(), config.getDescription(), config.getVersion().formatSmart(), frameSetup, tabbedPaneType );

        this.projectConfig = config;
    }

    public PhetProjectConfig getProjectConfig() {
        return projectConfig;
    }

    /**
     * Get the last created PhetApplication.
     *
     * @return last created PhetApplication.
     */
    public static PhetApplication instance() {
        return latestInstance;
    }

    /**
     * Get all created PhetApplications.
     *
     * @return all created PhetApplications.
     */
    public static PhetApplication[] instances() {
        return (PhetApplication[])phetApplications.toArray( new PhetApplication[0] );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private String title;
    private String description;
    private String version;
    private PhetFrame phetFrame;
    private ModuleManager moduleManager;
    private AWTSplashWindow splashWindow;
    private Frame splashWindowOwner;

    /**
     * Initialize a PhetApplication with a default FrameSetup.
     *
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     *
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version
    ) {
        this( args, title, description, version, new FrameSetup.CenteredWithSize( getScreenSize().width, getScreenSize().height - 150 ) );
    }

    /**
     * Constructor
     *
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     * @param frameSetup  Defines the size and location of the frame
     *
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) {
        this( args, title, description, version, frameSetup, JTABBED_PANE_TYPE );
    }

    /**
     *
     *
     *
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup, TabbedPaneType tabbedPaneType ) {
        // Put up a dialog that lets the user know that the simulation is starting up
        showSplashWindow( title );
        this.tabbedPaneType = tabbedPaneType;
        latestInstance = this;
        phetApplications.add( this );

        this.title = title;
        this.description = description;
        this.version = version;

        this.moduleManager = new ModuleManager( this );
        phetFrame = createPhetFrame();
        frameSetup.initialize( phetFrame );

        // Handle command line arguments
        parseArgs( args );
    }

    /**
     * Creates the PhetFrame for the application
     * <p/>
     * Concrete subclasses implement this
     *
     * @return The PhetFrame
     */
    protected PhetFrame createPhetFrame() {
        return new PhetFrame( this );
    }

    private void showSplashWindow( String title ) {
        if( splashWindow == null ) {
            // PhetFrame doesn't exist when this is called, so create and manage the window's owner.
            splashWindowOwner = new Frame();
            splashWindow = new AWTSplashWindow( splashWindowOwner, title );
            splashWindow.show();
        }
    }

    public AWTSplashWindow getSplashWindow() {
        return splashWindow;
    }

    private void disposeSplashWindow() {
        if( splashWindow != null ) {
            splashWindow.dispose();
            splashWindow = null;
            // Clean up the window's owner that we created in showSplashWindow.
            splashWindowOwner.dispose();
            splashWindowOwner = null;
        }
    }

    private static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Processes command line arguments. May be extended by subclasses.
     *
     * @param args
     */
    protected void parseArgs( String[] args ) {
        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( DEBUG_MENU_ARG ) ) {
//                phetFrame.addDebugMenu();
                //todo generalize debug menu
            }
        }
    }

    /**
     * Starts the PhetApplication.
     * <p/>
     * Sets up the mechanism that sets the reference sizes of all ApparatusPanel2 instances.
     */
    public void startApplication() {
        if( moduleManager.numModules() == 0 ) {
            throw new RuntimeException( "No modules in module manager" );
        }

        // Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
        // after the PhetFrame has been set to its startup size.
        // When the outer WindowAdapter gets called, the PhetFrame is
        // at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                disposeSplashWindow();
                initializeModuleReferenceSizes();
                phetFrame.removeWindowFocusListener( this );
            }
        } );

        moduleManager.setActiveModule( moduleManager.moduleAt( 0 ) );
        phetFrame.setVisible( true );


        updateLogoVisibility();
//        started = true;
    }

    /**
     * This is supposed hides the logo panel in the control panel if there is already a logo visible in the tabbed module pane,
     * so that both aren't visible by default..
     * <p/>
     * This method appears to give the correct behavior in the test program: TestPiccoloPhetApplication
     * <p/>
     * This method and functionality will be removed if is too awkward in practice (for example, too confusing to override this default).
     */
    protected void updateLogoVisibility() {
        for( int i = 0; i < moduleManager.numModules(); i++ ) {
            if( moduleAt( i ).isLogoPanelVisible() && phetFrame.getTabbedModulePane() != null && phetFrame.getTabbedModulePane().getLogoVisible() ) {
                moduleAt( i ).setLogoPanelVisible( false );
            }
        }
    }

    private void initializeModuleReferenceSizes() {
        for( int i = 0; i < moduleManager.numModules(); i++ ) {
            Module module = moduleManager.moduleAt( i );
            module.setReferenceSize();
        }
    }

    /**
     * Get the PhetFrame for this Application.
     *
     * @return the PhetFrame for this Application.
     */
    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    /**
     * Sets the simulation's properties.
     * These are properties that do NOT require localization.
     * 
     * @param simulationProperties
     */
    public void setSimulationProperties( Properties simulationProperties ) {
        this.simulationProperties = simulationProperties;
    }
    
    /**
     * Gets the simulation's properties.
     * These are properties that do NOT require localization.
     * 
     * @return Properties, null if setSimulationProperties was not called
     */
    public Properties getSimulationProperties() {
        return simulationProperties;
    }
    
    /**
     * Gets the version string in the proper format for displaying in the 
     * title bar of the application's main frame.
     * 
     * @param simulationProperties
     * @return
     */
    public static String getVersionString( Properties simulationProperties ) {
        String major = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_MAJOR, "?" );
        String minor = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_MINOR, "?" );
        String dev = simulationProperties.getProperty( PropertiesLoader.PROPERTY_VERSION_DEV, "?" );
        String version = major + "." + minor;
        if ( !dev.matches( "0*" ) ) {
            // add dev number only if it's not zero
            version += "." + dev;
        }
        return version;
    }
    
    //----------------------------------------------------------------
    // Module-related methods
    //----------------------------------------------------------------

    /**
     * Creates the tabbed pane for the modules in the application.
     *
     * @return a tabbed module pane
     */
    public ITabbedModulePane createTabbedPane() {
        return tabbedPaneType.createTabbedPane();
    }

    /**
     * Sets the modules in the application
     *
     * @param modules
     */
    public void setModules( Module[] modules ) {
        moduleManager.setModules( modules );
    }

    /**
     * Remove a Module from this PhetApplication.
     *
     * @param module the Module to remove.
     */
    public void removeModule( Module module ) {
        moduleManager.removeModule( module );
    }

    /**
     * Add one Module to this PhetApplication.
     *
     * @param module the Module to add.
     */
    public void addModule( Module module ) {
        moduleManager.addModule( module );
    }

    /**
     * Get the specified Module.
     *
     * @param i the Module index
     * @return the Module.
     */
    public Module moduleAt( int i ) {
        return moduleManager.moduleAt( i );
    }

    /**
     * Gets a module based on its index.
     * (This is a more common name for the moduleAt method.)
     *
     * @param i
     * @return the module
     */
    public Module getModule( int i ) {
        return moduleAt( i );
    }

    /**
     * Set the specified Module to be active.
     *
     * @param module the module to activate.
     */
    public void setActiveModule( Module module ) {
        moduleManager.setActiveModule( module );
    }

    /**
     * Set the specified Module to be active.
     *
     * @param i the module index to activate.
     */
    public void setActiveModule( int i ) {
        moduleManager.setActiveModule( i );
    }

    /**
     * Add a ModuleObserver to this PhetApplication to observe changes in the list of Modules, and which Module is active.
     *
     * @param moduleObserver
     */
    public void addModuleObserver( ModuleObserver moduleObserver ) {
        moduleManager.addModuleObserver( moduleObserver );
    }

    /**
     * Get the index of the specified Module.
     *
     * @param m
     * @return the index of the specified Module.
     */
    public int indexOf( Module m ) {
        return moduleManager.indexOf( m );
    }

    /**
     * Get the number of modules.
     *
     * @return the number of modules.
     */
    public int numModules() {
        return moduleManager.numModules();
    }

    /**
     * Returns the active Module, or null if no module has been activated yet.
     *
     * @return the active Module, or null if no module has been activated yet.
     */
    public Module getActiveModule() {
        return moduleManager.getActiveModule();
    }

    /**
     * Get the title for this PhetApplication.
     *
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the description for this PhetApplication.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the version string for this PhetApplication.
     *
     * @return the version string.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Adds modules.
     *
     * @param m the array of modules to add
     */
    public void addModules( Module[] m ) {
        for( int i = 0; i < m.length; i++ ) {
            Module module = m[i];
            addModule( module );
        }
    }

    /**
     * Save the set of modules to the specified location.
     *
     * @param filename
     */
    public void saveState( String filename ) {
        new ModuleSerializationManager().saveState( this, filename );
    }

    /**
     * Restore the module states specified in the file.
     *
     * @param filename
     */
    public void restoreState( String filename ) {
        new ModuleSerializationManager().restoreState( this, filename );
    }

    /**
     * Pauses the PhetApplication (including any Modules that are active).
     */
    public void pause() {
        getActiveModule().deactivate();
    }

    /**
     * Resumes progress of the PhetApplication (including any Modules that are active).
     */
    public void resume() {
        getActiveModule().activate();
    }

    /**
     * Returns all the Modules registered with this PhetApplication.
     *
     * @return all the Modules registered with this PhetApplication.
     */
    public Module[] getModules() {
        return moduleManager.getModules();
    }

    /**
     * Displays an About Dialog for the simulation, including version information, license information and references.
     */
    public void showAboutDialog() {
        createPhetAboutDialog().show();
    }

    /**
     * Creates the PhetAboutDialog which is used in showAboutDialog
     *
     * @return the Application's PhetAboutDialog
     */
    protected PhetAboutDialog createPhetAboutDialog() {
        return new PhetAboutDialog( this );
    }

    public void setTabbedPaneType( TabbedPaneType tabbedPaneType ) {
        this.tabbedPaneType = tabbedPaneType;
    }

    /**
     * Close this PhetApplication cleanly without exiting the JVM;
     * Deactivates all modules and disposes of the associated PhetFrame for this PhetApplication.
     * TODO: verify there are no memory leaks
     */
    public void closeApplication() {
        for( int i = 0; i < numModules(); i++ ) {
            moduleAt( i ).deactivate();
        }
        getPhetFrame().dispose();
    }

    /**
     * Enumeration class used to specify the type of tabbed panes the application is to use in
     * its Module instances
     */
    public abstract static class TabbedPaneType {
        protected TabbedPaneType() {
        }

        public abstract ITabbedModulePane createTabbedPane();
    }

    // Standard Swing JTabbedPanes
    public static final TabbedPaneType JTABBED_PANE_TYPE = new TabbedPaneType() {
        public ITabbedModulePane createTabbedPane() {
            return new JTabbedModulePane();
        }
    };
}
