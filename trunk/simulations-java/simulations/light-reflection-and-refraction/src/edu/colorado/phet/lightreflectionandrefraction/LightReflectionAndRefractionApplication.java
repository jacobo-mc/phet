// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.IntroModule;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumColorDialog;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionApplication extends PiccoloPhetApplication {
    private static final String NAME = "light-reflection-and-refraction";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public LightReflectionAndRefractionApplication( PhetApplicationConfig config ) {
        super( config );
        final IntroModule introModule = new IntroModule();
        addModule( introModule );
//        addModule( new PrismBreakModule() );
//        addModule( new IntroModule() );
        getPhetFrame().addMenu( new JMenu( "Developer" ) {{
            add( new JMenuItem( "Medium colors" ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        new MediumColorDialog( getPhetFrame(), introModule.getLRRModel() ).setVisible( true );
                    }
                } );
            }} );
        }} );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, LightReflectionAndRefractionApplication.class );
    }
}
