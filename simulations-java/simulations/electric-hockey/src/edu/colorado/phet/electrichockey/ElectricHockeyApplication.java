package edu.colorado.phet.electrichockey;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class ElectricHockeyApplication extends PhetApplication {
    private ElectricHockeyModule module;

    public ElectricHockeyApplication( PhetApplicationConfig config ) {
        super( config );
        module = new ElectricHockeyModule( config );
        addModule( module );
    }

    public static class ElectricHockeyApplicationConfig extends PhetApplicationConfig {
        public ElectricHockeyApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, null, "electric-hockey" );
            setFrameSetup( new FrameSetup.CenteredWithSize( 800, 750 ) );
            super.setLookAndFeel( new PhetLookAndFeel() );
        }
    }

    private class ElectricHockeyModule extends Module {
        public ElectricHockeyModule( PhetApplicationConfig config ) {
            super( config.getName(), new ConstantDtClock( 30, 1 ) );
            ElectricHockeySimulationPanel balloonsSimulationPanel = new ElectricHockeySimulationPanel();
            balloonsSimulationPanel.init();
            setSimulationPanel( balloonsSimulationPanel );
            setClockControlPanel( null );
            setLogoPanelVisible( false );
        }
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new ElectricHockeyApplicationConfig( args ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new ElectricHockeyApplication( config );
            }
        } );
    }
}
