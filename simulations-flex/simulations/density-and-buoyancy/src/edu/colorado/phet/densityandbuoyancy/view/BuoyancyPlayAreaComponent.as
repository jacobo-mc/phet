//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyObject;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.test.Box2DDebug;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowMesh;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityAndBuoyancyObject3D;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ScaleObject3D;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancyPlaygroundMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameDensityMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameMassMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.BuoyancySameVolumeMode;
import edu.colorado.phet.densityandbuoyancy.view.modes.Mode;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.NumericProperty;
import edu.colorado.phet.flexcommon.model.Vector2D;

/**
 * Contains the sim play area for Buoyancy (not including control panels, which are shown in the BuoyancyApplication)
 */
public class BuoyancyPlayAreaComponent extends AbstractDensityAndBuoyancyPlayAreaComponent {

    private var _container: BuoyancyCanvas;

    private var defaultMode: Mode;
    public var sameMassMode: BuoyancySameMassMode;
    private var sameVolumeMode: BuoyancySameVolumeMode;
    private var sameDensityMode: BuoyancySameDensityMode;

    public var playgroundModes: BuoyancyPlaygroundMode;
    private var mode: Mode;

    public const gravityArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const buoyancyArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const contactArrowsVisible: BooleanProperty = new BooleanProperty( false );
    public const vectorValuesVisible: BooleanProperty = new BooleanProperty( false );

    public function BuoyancyPlayAreaComponent( container: BuoyancyCanvas, extendedPool: Boolean, showExactLiquidColor: Boolean ) {
        super( extendedPool, showExactLiquidColor );
        this._container = container;

        _model.scalesMovableProperty.initialValue = true; // for now, do this early so that when scales are constructed they are initialized properly

        //Initialize the modes (for some reason cannot be done until application is fully loaded).
        const myThis: BuoyancyPlayAreaComponent = this;
        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            sameMassMode = new BuoyancySameMassMode( myThis );
            sameVolumeMode = new BuoyancySameVolumeMode( myThis );
            sameDensityMode = new BuoyancySameDensityMode( myThis );
            playgroundModes = new BuoyancyPlaygroundMode( myThis );
            defaultMode = _container.getDefaultMode( myThis );
            //If other modes are added, you may need to specify a call to the Mode.reset() in resetAll()
            setMode( defaultMode );

            var box2DDebug: Box2DDebug = new Box2DDebug( model.getWorld() );
            //        addChild(box2DDebug.getSprite());
        } );
    }

    override protected function createModel( showExactLiquidColor: Boolean ): DensityAndBuoyancyModel {
        return new DensityAndBuoyancyModel( DensityAndBuoyancyConstants.litersToMetersCubed( 100.0 ) - Scale.SCALE_VOLUME, //this accounts for one submerged scale, so that the readout still reads 100.0 on init
                                            extendedPool, showExactLiquidColor );
    }

    override public function resetAll(): void {
        super.resetAll();
        sameMassMode.reset();
        sameVolumeMode.reset();
        sameDensityMode.reset();
        playgroundModes.reset();

        defaultMode.reset();
        switchToDefaultMode();
        vectorValuesVisible.reset();

        buoyancyArrowsVisible.reset();
        gravityArrowsVisible.reset();
        contactArrowsVisible.reset();
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

    public function switchToDefaultMode(): void {
        setMode( defaultMode );
    }

    override public function get container(): AbstractDensityAndBuoyancyCanvas {
        return _container;
    }

    override protected function createNode( densityObject: DensityAndBuoyancyObject ): DensityAndBuoyancyObject3D {
        var densityObjectNode: DensityAndBuoyancyObject3D = super.createNode( densityObject );
        addArrowNodes( densityObjectNode );
        return densityObjectNode;
    }

    /**
     * Add arrow nodes to depict the forces acting on the specified DensityObjectNode
     * @param densityObjectNode
     */
    private function addArrowNodes( densityObjectNode: DensityAndBuoyancyObject3D ): void {
        if ( !(densityObjectNode is ScaleObject3D) ) {

            var densityObject: DensityAndBuoyancyObject = densityObjectNode.getDensityObject();
//            var offset: Number = 8;
            var offset: Number = 0;  //TODO: trial test of setting offsets to zero, maybe will revert
            const gravityNode: ArrowMesh = new ArrowMesh( densityObject, densityObject.getGravityForceArrowModel(), DensityAndBuoyancyConstants.GRAVITY_COLOR, gravityArrowsVisible, mainCamera, mainViewport, vectorValuesVisible,
                                                          createOffset( densityObject.getGravityForceArrowModel(), densityObject, 0 ), true );
            const buoyancyNode: ArrowMesh = new ArrowMesh( densityObject, densityObject.getBuoyancyForceArrowModel(), DensityAndBuoyancyConstants.BUOYANCY_COLOR, buoyancyArrowsVisible, mainCamera, mainViewport, vectorValuesVisible,
                                                           createOffset( densityObject.getBuoyancyForceArrowModel(), densityObject, 0 ), true );
            const contactForceNode: ArrowMesh = new ArrowMesh( densityObject, densityObject.getContactForceArrowModel(), DensityAndBuoyancyConstants.CONTACT_COLOR, contactArrowsVisible, mainCamera, mainViewport, vectorValuesVisible,
                                                               createOffset( densityObject.getContactForceArrowModel(), densityObject, offset ), false );

            const arrowList: Array = [gravityNode, buoyancyNode, contactForceNode];
            for each ( var arrowNode: ArrowMesh in arrowList ) {
                densityObjectNode.addArrowNode( arrowNode );
            }
        }
    }

    /**
     * Helper function for addArrowNodes that creates a NumericProperty to offset the arrows so they don't overlap.  The offset changes with the sim state, so is NumericProperty.
     */
    private function createOffset( arrowModel: Vector2D, densityObject: DensityAndBuoyancyObject, dx: Number ): NumericProperty {
        var offsetX: NumericProperty = new NumericProperty( "offsetX", "pixels", dx );

        function tooMuchOverlap( y1: Number, y2: Number ): Boolean {
            return y1 * y2 > 100;
        }

        //Check to see if the arrowModel in question has the same sign as any other arrowModel
        function isTooMuchOverlap(): Boolean {
            for each ( var vector: Vector2D in densityObject.forceVectors ) {
                if ( vector != arrowModel ) {
                    if ( tooMuchOverlap( vector.y, arrowModel.y ) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        function update(): void {
            if ( isTooMuchOverlap() ) {
                offsetX.value = dx;
            }
            else {
                offsetX.value = 0;
            }
        }

        densityObject.getGravityForceArrowModel().addListener( update );
        densityObject.getBuoyancyForceArrowModel().addListener( update );
        densityObject.getContactForceArrowModel().addListener( update );
        return offsetX;
    }

    public function switchToOneObject(): void {
        setMode( playgroundModes );
        playgroundModes.setOneObject();
    }

    public function switchToTwoObjects(): void {
        setMode( playgroundModes );
        playgroundModes.setTwoObjects();
    }
}
}

