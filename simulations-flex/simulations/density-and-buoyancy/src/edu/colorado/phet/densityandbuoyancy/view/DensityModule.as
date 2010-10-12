package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.view.modes.CustomObjectMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.densityandbuoyancy.view.modes.MysteryObjectsMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.SameDensityMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.SameMassMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.SameVolumeMode;

import mx.containers.Canvas;

public class DensityModule extends AbstractDensityModule {

    private var _densityCanvas: DensityCanvas;

    private var customObjectMode: CustomObjectMode;
    private var sameMassMode: SameMassMode;
    private var sameVolumeMode: SameVolumeMode;
    private var sameDensityMode: SameDensityMode;
    private var mysteryObjectsMode: MysteryObjectsMode;
    private var mode: Mode;

    public function DensityModule() {
        super();
    }

    public function doInit( densityCanvas: DensityCanvas ): void {
        this._densityCanvas = densityCanvas;
        customObjectMode = new CustomObjectMode( this );
        sameMassMode = new SameMassMode( this );
        sameVolumeMode = new SameVolumeMode( this );
        sameDensityMode = new SameDensityMode( this );
        mysteryObjectsMode = new MysteryObjectsMode( this );
        //If other modes are added, you may need to specify a call to the Mode.reset() in resetAll()
        setMode( customObjectMode );

        var box2DDebug: Box2DDebug = new Box2DDebug( model.getWorld() );
        //        _densityCanvas.addChild(box2DDebug.getSprite());
    }

    override public function resetAll(): void {
        super.resetAll();
        customObjectMode.reset();
        switchToCustomObject();
    }

    public function setMode( mode: Mode ): void {
        if ( this.mode != mode ) {
            if ( this.mode != null ) {
                this.mode.teardown();
            }
            this.mode = mode;
            this.mode.init();
        }
    }

    public function switchToSameMass(): void {
        setMode( sameMassMode );
    }

    public function switchToSameVolume(): void {
        setMode( sameVolumeMode );
    }

    public function switchToSameDensity(): void {
        setMode( sameDensityMode );
    }

    public function switchToCustomObject(): void {
        setMode( customObjectMode );
    }

    public function switchToMysteryObjects(): void {
        setMode( mysteryObjectsMode );
    }

    public function getDensityCanvas(): Canvas {
        return _densityCanvas;
    }

    override public function get canvas(): AbstractDensityAndBuoyancyCanvas {
        return _densityCanvas;
    }
}
}

