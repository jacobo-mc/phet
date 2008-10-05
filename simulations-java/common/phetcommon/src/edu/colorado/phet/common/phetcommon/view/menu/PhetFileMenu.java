/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.view.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.preferences.PreferencesDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * PhetFileMenu
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFileMenu extends JMenu {

    public PhetFileMenu() {
        super( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Title" ) );
        setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.TitleMnemonic" ).charAt( 0 ) );

        JMenuItem preferencesMenuItem = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Preferences" ) );
        preferencesMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PreferencesDialog( null, null, null ).setVisible( true );
            }
        } );
        preferencesMenuItem.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.PreferencesMnemonic" ).charAt( 0 ) );
        add( preferencesMenuItem );
        addSeparator();

        JMenuItem exitMI = new JMenuItem( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.Exit" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        exitMI.setMnemonic( PhetCommonResources.getInstance().getLocalizedString( "Common.FileMenu.ExitMnemonic" ).charAt( 0 ) );
        this.add( exitMI );
    }
}
