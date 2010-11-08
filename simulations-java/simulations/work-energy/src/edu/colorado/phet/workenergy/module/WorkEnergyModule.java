package edu.colorado.phet.workenergy.module;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;
import edu.colorado.phet.workenergy.view.WorkEnergyCanvas;

/**
 * @author Sam Reid
 */
public class WorkEnergyModule<ModelType extends WorkEnergyModel> extends Module {
    private ModelType model;
    private Property<Boolean> showPieChartProperty = new Property<Boolean>( true );
    private Property<Boolean> showEnergyBarChartProperty = new Property<Boolean>( false );
    private Property<Boolean> showRulerProperty = new Property<Boolean>( false );

    public WorkEnergyModule( PhetFrame phetFrame, String title, final ModelType model ) {
        super( title, new ConstantDtClock( 30, 1.0 ) );
        this.model = model;
        WorkEnergyCanvas energyCanvas = new WorkEnergyCanvas( this, model );
        getModulePanel().setLogoPanel( null );

        setSimulationPanel( energyCanvas );

        //Handled in canvas
        setControlPanel( null );
        setClockControlPanel( null );

        getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                model.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    public ModelType getWorkEnergyModel() {
        return model;
    }

    public void resetAll() {
    }

    public Property<Boolean> getShowPieChartProperty() {
        return showPieChartProperty;
    }

    public Property<Boolean> getShowEnergyBarChartProperty() {
        return showEnergyBarChartProperty;
    }

    public Property<Boolean> getShowRulerProperty() {
        return showRulerProperty;
    }
}
