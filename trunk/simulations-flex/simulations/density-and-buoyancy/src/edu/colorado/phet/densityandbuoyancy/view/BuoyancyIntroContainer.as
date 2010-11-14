package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.controls.Label;
import mx.controls.RadioButton;
import mx.events.FlexEvent;

public class BuoyancyIntroContainer extends BuoyancyContainer {
    public static var count: Number = 0;//for different button groups
    private var sameMassButton: RadioButton;

    public function BuoyancyIntroContainer() {
        super( false, true );
        count = count + 1;

        var modeControlPanel: DensityVBox = new DensityVBox();
        modeControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        modeControlPanel.y = DensityConstants.CONTROL_INSET;

        var label: Label = new Label();
        label.text = FlexSimStrings.get( 'mode.title', 'Blocks' );
        label.setStyle( "fontWeight", "bold" );
        modeControlPanel.addChild( label );

        var groupName: String = "modes" + count;

        sameMassButton = new RadioButton();
        sameMassButton.groupName = groupName;
        sameMassButton.label = FlexSimStrings.get( 'mode.objectsOfSameMass', 'Same Mass' );
        sameMassButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToSameMass();
        } );
        modeControlPanel.addChild( sameMassButton );

        var sameVolumeButton: RadioButton = new RadioButton();
        sameVolumeButton.groupName = groupName;
        sameVolumeButton.label = FlexSimStrings.get( 'mode.objectsOfSameVolume', 'Same Volume' );
        sameVolumeButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToSameVolume();
        } );
        modeControlPanel.addChild( sameVolumeButton );

        var sameDensityButton: RadioButton = new RadioButton();
        sameDensityButton.groupName = groupName;
        sameDensityButton.label = FlexSimStrings.get( 'mode.objectsOfSameDensity', 'Same Density' );
        sameDensityButton.addEventListener( MouseEvent.CLICK, function(): void {
            buoyancyCanvas.switchToSameDensity();
        } );
        modeControlPanel.addChild( sameDensityButton );

        addChild( modeControlPanel );
        sameMassButton.selected = true;
    }

    override public function resetAll(): void {
        super.resetAll();
        sameMassButton.selected = true;
    }

    override public function getDefaultMode( canvas: AbstractDBCanvas ): Mode {
        return buoyancyCanvas.sameMassMode;
    }

    override public function init(): void {
        super.init();

        const fluidDensityControl: IntroFluidDensityControl = new IntroFluidDensityControl( buoyancyCanvas.model.fluidDensity, buoyancyCanvas.units );
        fluidDensityControl.setStyle( "bottom", DensityConstants.CONTROL_INSET );

        const updateFluidDensityControlLocation: Function = function(): void {
            fluidDensityControl.x = stage.width / 2 - fluidDensityControl.width / 2;
        };
        addEventListener( Event.RESIZE, updateFluidDensityControlLocation );
        fluidDensityControl.addEventListener( FlexEvent.UPDATE_COMPLETE, updateFluidDensityControlLocation ); // listen to when our fluid control gets its size
        updateFluidDensityControlLocation();

        addChild( fluidDensityControl );
    }
}
}