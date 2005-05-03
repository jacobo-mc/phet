/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.components.menu;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleObserver;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.VersionUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * HelpMenu
 *
 * @author ?
 * @version $Revision$
 */
public class HelpMenu extends JMenu {
    private ImageIcon icon;

    public HelpMenu( final PhetApplication application ) throws IOException {
        super( SimStrings.get( "Common.HelpMenu.Title" ) );
        this.setMnemonic( SimStrings.get( "Common.HelpMenu.TitleMnemonic" ).charAt( 0 ) );
        
        final ApplicationModel appDescriptor = application.getApplicationModel();
        Module active = application.getModuleManager().getActiveModule();
        
        //----------------------------------------------------------------------
        // "Help" menu item
        JMenuItem onscreenHelp = new JMenuItem( SimStrings.get( "Common.HelpMenu.Help" ) );
        onscreenHelp.setMnemonic( SimStrings.get( "Common.HelpMenu.HelpMnemonic" ).charAt( 0 ) );
        onscreenHelp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.getModuleManager().getActiveModule().setHelpEnabled( true );
            }
        } );
        onscreenHelp.setEnabled( active != null && active.hasHelp() );
        add( onscreenHelp );
        
        //----------------------------------------------------------------------
        // "MegaHelp" menu item
        final JMenuItem megaHelpItem = new JMenuItem( SimStrings.get( "Common.HelpMenu.MegaHelp" ) );
        megaHelpItem.setMnemonic( SimStrings.get( "Common.HelpMenu.MegaHelpMnemonic" ).charAt( 0 ) );
        megaHelpItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( application.getModuleManager().getActiveModule().hasMegaHelp() ) {
                    application.getModuleManager().getActiveModule().showMegaHelp();
                }
                else {
                    JOptionPane.showMessageDialog( application.getPhetFrame(), "No MegaHelp available for this module." );
                }
            }
        } );
        application.getModuleManager().addModuleObserver( new ModuleObserver() {
            public void moduleAdded( Module m ) {
            }

            public void activeModuleChanged( Module m ) {
                megaHelpItem.setEnabled( m.hasMegaHelp() );
            }

            public void moduleRemoved( Module m ) {
            }
        } );
        megaHelpItem.setEnabled( active != null && active.hasMegaHelp() );
        add( megaHelpItem );

        //----------------------------------------------------------------------
        // Separator
        addSeparator();
        
        //----------------------------------------------------------------------
        // "About" menu item
        final JMenuItem about = new JMenuItem( SimStrings.get( "Common.HelpMenu.About" ) );
        about.setMnemonic( SimStrings.get( "Common.HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        final String name = appDescriptor.getWindowTitle();
        String desc = appDescriptor.getDescription();
        String version = appDescriptor.getVersion();
        String message = name + "\n" + desc + "\n" + SimStrings.get( "Common.HelpMenu.VersionLabel" ) + ": " + version + "\n";
        try {
            VersionUtils.VersionInfo[] inf = appDescriptor.readVersionInfo();
//            System.out.println( "HelpMenu::VersionInfo.length = " + inf.length );
            for( int i = 0; i < inf.length; i++ ) {
                VersionUtils.VersionInfo versionInfo = inf[i];
                message += versionInfo.toString();
                if( i < inf.length ) {
                    message += "\n";
                }
            }
            message += "\n" + SimStrings.get( "Common.HelpMenu.JavaVersion" ) + ": " + System.getProperty( "java.version" ) + "\n" + SimStrings.get( "Common.HelpMenu.By" ) + " " + System.getProperty( "java.vendor" );
            final String msg = message;
            about.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JOptionPane.showMessageDialog( about, msg, SimStrings.get( "Common.HelpMenu.AboutTitle" ) + " " + name, JOptionPane.INFORMATION_MESSAGE, icon );
                }
            } );

        }
        catch( Exception e ) {
            e.printStackTrace();
            message += "Could not load version info, error=" + e.toString();
            StackTraceElement[] st = e.getStackTrace();
            int numElementsToShow = 5;
            for( int i = 0; i < numElementsToShow; i++ ) {
                StackTraceElement stackTraceElement = st[i];
                message += stackTraceElement.toString() + "\n";
            }
        }
        add( about );
    }
}
