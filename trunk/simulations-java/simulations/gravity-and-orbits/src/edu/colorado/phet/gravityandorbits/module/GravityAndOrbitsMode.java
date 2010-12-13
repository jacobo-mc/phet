package edu.colorado.phet.gravityandorbits.module;

import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsResources;
import edu.colorado.phet.gravityandorbits.controlpanel.GORadioButton;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * A GravityAndOrbitsMode behaves like a module, it has its own model, control panel, canvas, and remembers its state when you leave and come back.
 *
 * @author Sam Reid
 */
public abstract class GravityAndOrbitsMode {
    private String name;
    private GravityAndOrbitsModel model;
    private Property<Boolean> moonProperty = new Property<Boolean>( false );
    private GravityAndOrbitsCanvas canvas;
    private double forceScale;
    private final Camera camera;
    private Property<Boolean> active;
    private ArrayList<SimpleObserver> modeActiveListeners = new ArrayList<SimpleObserver>();
    private final Property<Boolean> clockRunningProperty;
    private Function1<Double, String> timeFormatter;

    public GravityAndOrbitsMode( String name, double forceScale, boolean active, Camera camera, double dt, Function1<Double, String> timeFormatter ) {
        this.name = name;
        this.forceScale = forceScale;
        this.camera = camera;
        this.active = new Property<Boolean>( active );
        this.timeFormatter = timeFormatter;

        model = new GravityAndOrbitsModel( new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, dt ), moonProperty );

        getMoonProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( getMoonProperty().getValue() ) {
                    for ( Body body : model.getBodies() ) {
                        body.resetAll();
                    }
                }
            }
        } );

        //Clock control panel
        clockRunningProperty = new Property<Boolean>( false ) {{
            final SimpleObserver updateClock = new SimpleObserver() {
                public void update() {
                    model.getClock().setRunning( isActive() && getValue() );
                }
            };
            //This assumes that this code is the only place that changes whether the clock is running
            //If another place called clock.start() or stop(), then this property wouldn't get a callback
            addObserver( updateClock );
            addModeActiveListener( updateClock );
        }};
    }

    public GravityAndOrbitsClock getClock() {
        return model.getClock();
    }

    public void addBody( Body body ) {
        model.addBody( body );
    }

    public String getName() {
        return name;
    }

    public GravityAndOrbitsModel getModel() {
        return model;
    }

    public void reset() {
        model.getClock().resetSimulationTime();// reset the clock
        moonProperty.reset();
        model.resetAll();
    }

    public Property<Boolean> getMoonProperty() {
        return moonProperty;
    }

    public void setRunning( boolean running ) {
        model.getClock().setRunning( running );
    }

    public JComponent getCanvas() {
        return canvas;
    }

    public void init( GravityAndOrbitsModule module ) {
        canvas = new GravityAndOrbitsCanvas( model, module, this, forceScale );
    }

    public JComponent newComponent( Property<GravityAndOrbitsMode> modeProperty ) {
        return createRadioButtonWithIcons( modeProperty );
    }

    protected JComponent createRadioButtonWithText( final Property<GravityAndOrbitsMode> modeProperty ) {
        return new GORadioButton<GravityAndOrbitsMode>( null, modeProperty, GravityAndOrbitsMode.this );
    }

    protected JComponent createRadioButtonWithIcons( final Property<GravityAndOrbitsMode> modeProperty ) {
        return new JPanel() {{
            setOpaque( false );//TODO: is this a mac problem?
            setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
            setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
            add( new GORadioButton<GravityAndOrbitsMode>( null, modeProperty, GravityAndOrbitsMode.this ) );
            add( new JLabel( new ImageIcon( new PNode() {{
                setOpaque( false );//TODO: is this a mac problem?
                setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
                setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
                int index = 0;
                int iconSize = 30;
                int inset = 4;
                for ( Body body : GravityAndOrbitsMode.this.getModel().getBodies() ) {
                    PNode renderer = body.createRenderer( iconSize );
                    addChild( renderer );
                    renderer.setOffset( iconSize * index + inset * index, 0 );
                    index++;
                }
            }}.toImage() ) ) );
        }};
    }

    private Icon createIcon() {
        return new ImageIcon( GravityAndOrbitsResources.getImage( "earth.png" ) );
    }

    public void setActive( boolean active ) {
        this.active.setValue( active );
    }

    public void addModeActiveListener( SimpleObserver simpleObserver ) {
        active.addObserver( simpleObserver );
    }

    public boolean isActive() {
        return active.getValue();
    }

    public Property<Boolean> getClockRunningProperty() {
        return clockRunningProperty;
    }

    public Property<ModelViewTransform> getModelViewTransformProperty() {
        return camera.getModelViewTransformProperty();
    }

    //Zoom from the original zoom (default for all views) to the correct zoom for this mode
    public void startZoom() {
        camera.zoomTo( getZoomScale(), getZoomOffset() );
    }

    public abstract double getZoomScale();

    public abstract ImmutableVector2D getZoomOffset();

    public Function1<Double, String> getTimeFormatter() {
        return timeFormatter;
    }

    public void resetBodies() {
        model.resetBodies();
    }
}
