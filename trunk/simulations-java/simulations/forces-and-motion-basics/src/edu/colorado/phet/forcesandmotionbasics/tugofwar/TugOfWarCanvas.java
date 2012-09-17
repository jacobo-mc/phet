package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import fj.Effect;
import fj.F;
import fj.data.List;
import fj.data.Option;
import fj.function.Doubles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.KnotNode._force;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.KnotNode._free;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.BLUE;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PColor.RED;
import static edu.colorado.phet.forcesandmotionbasics.tugofwar.TugOfWarCanvas.PSize.*;

/**
 * @author Sam Reid
 */
public class TugOfWarCanvas extends AbstractForcesAndMotionBasicsCanvas implements PullerContext, ForcesNodeContext {

    public static final long ANIMATION_DURATION = 300;
    private final List<KnotNode> blueKnots;
    private final List<KnotNode> redKnots;
    private final ForcesNode forcesNode;
    public final ArrayList<VoidFunction0> forceListeners = new ArrayList<VoidFunction0>();
    private final PImage cartNode;
    private Property<Mode> mode = new Property<Mode>( Mode.WAITING );
    private Cart cart = new Cart();

    public static enum Mode {GOING, WAITING}

    public TugOfWarCanvas( final Context context, final IClock clock ) {

        setBackground( new Color( 209, 210, 212 ) );
        //use view coordinates since nothing compex happening in model coordinates.

        //for a canvas height of 710, the ground is at 452 down from the top
        final int width = 10000;

        //Reverse bottom and top because using view coordinates
        final int grassY = 452;
        addChild( new SkyNode( createIdentity(), new Rectangle2D.Double( -width / 2, -width / 2 + grassY, width, width / 2 ), grassY, SkyNode.DEFAULT_TOP_COLOR, SkyNode.DEFAULT_BOTTOM_COLOR ) );

        final PImage grassNode = new PImage( Images.GRASS );
        grassNode.setOffset( -2, grassY - 2 );
        addChild( grassNode );

        final ControlPanelNode controlPanelNode = new ControlPanelNode(
                new VBox( 2, VBox.LEFT_ALIGNED,

                          //Nudge "show" to the right so it will align with checkboxes
                          new HBox( 5, new PhetPPath( new Rectangle2D.Double( 0, 0, 0, 0 ) ), new PhetPText( "Show", CONTROL_FONT ) ),
                          new PSwing( new JCheckBox( "Values" ) {{setFont( CONTROL_FONT );}} ), new PSwing( new JCheckBox( "Sum of Forces" ) {{
                    setFont( CONTROL_FONT );
                }} ) ), new Color( 227, 233, 128 ), new BasicStroke( 2 ), Color.black );
        controlPanelNode.setOffset( STAGE_SIZE.width - controlPanelNode.getFullWidth() - INSET, INSET );
        addChild( controlPanelNode );

        addChild( new ResetAllButtonNode( new Resettable() {
            public void reset() {
                context.reset();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getMaxY() + INSET );
            setConfirmationEnabled( false );
        }} );

        cartNode = new PImage( Images.CART );
        cartNode.setOffset( STAGE_SIZE.width / 2 - cartNode.getFullBounds().getWidth() / 2, grassY - cartNode.getFullBounds().getHeight() + 4 );


        final PImage rope = new PImage( Images.ROPE );
        rope.setOffset( STAGE_SIZE.width / 2 - rope.getFullBounds().getWidth() / 2, cartNode.getFullBounds().getCenterY() - rope.getFullBounds().getHeight() / 2 );

        blueKnots = ImageMetrics.blueKnots.map( new F<Double, KnotNode>() {
            @Override public KnotNode f( final Double knotLocation ) {
                return new KnotNode( knotLocation, Color.blue, rope.getFullBounds() );
            }
        } );
        redKnots = ImageMetrics.redKnots.map( new F<Double, KnotNode>() {
            @Override public KnotNode f( final Double knotLocation ) {
                return new KnotNode( knotLocation, Color.red, rope.getFullBounds() );
            }
        } );

        addChildren( blueKnots.append( redKnots ) );

        addChild( rope );
        addChild( cartNode );

        final double IMAGE_SCALE = 0.75;
        Vector2D largePosition = Vector2D.v( 88.38995568685374, 488.15361890694203 );
        Vector2D mediumPosition = Vector2D.v( 151.66912850812423, 513.264401772526 );
        Vector2D smallPosition1 = Vector2D.v( 215.9527326440175, 558.463810930576 );
        Vector2D smallPosition2 = Vector2D.v( 263.1610044313148, 559.4682422451999 );
        final PNode largeRedPuller = puller( BLUE, LARGE, IMAGE_SCALE, largePosition, this );
        addChild( largeRedPuller );
        addChild( puller( BLUE, MEDIUM, IMAGE_SCALE, mediumPosition, this ) );
        addChild( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition1, this ) );
        addChild( puller( BLUE, SMALL, IMAGE_SCALE, smallPosition2, this ) );

        final double offset = largeRedPuller.getFullBounds().getWidth();
        addChild( puller( RED, LARGE, IMAGE_SCALE, reflect( largePosition, offset ), this ) );
        addChild( puller( RED, MEDIUM, IMAGE_SCALE, reflect( mediumPosition, offset ), this ) );
        addChild( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition1, offset ), this ) );
        addChild( puller( RED, SMALL, IMAGE_SCALE, reflect( smallPosition2, offset ), this ) );

        forcesNode = new ForcesNode();
        addChild( forcesNode );

        addChild( new ImageButtonNodeWithText( Images.GO_BUTTON, "Go!", new VoidFunction0() {
            public void apply() {
                mode.set( Mode.GOING );
            }
        } ) {
            {
                setOffset( getButtonLocation( this ) );

                final VoidFunction0 update = new VoidFunction0() {
                    public void apply() {
                        boolean visible = redKnots.append( blueKnots ).filter( new F<KnotNode, Boolean>() {
                            @Override public Boolean f( final KnotNode knotNode ) {
                                return knotNode.getPullerNode() != null;
                            }
                        } ).length() > 0 && mode.get() == Mode.WAITING;
                        setVisible( visible );
                        setChildrenPickable( visible );
                    }
                };
                forceListeners.add( update );
                update.apply();
            }
        } );

        final ImageButtonNodeWithText stopButton = new ImageButtonNodeWithText( Images.STOP_BUTTON, "STOP", new VoidFunction0() {
            public void apply() {
                mode.set( Mode.WAITING );
            }
        } ) {{
            setOffset( getButtonLocation( this ) );
            mode.addObserver( new VoidFunction1<Mode>() {
                public void apply( final Mode mode ) {
                    boolean visible = mode == Mode.GOING;
                    setVisible( visible );
                    setChildrenPickable( visible );
                }
            } );
        }};
        addChild( stopButton );

        addChild( new TextButtonNode( "Restart", CONTROL_FONT, Color.orange ) {{
            setOffset( stopButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, stopButton.getFullBounds().getMaxY() + INSET );
            mode.addObserver( new VoidFunction1<Mode>() {
                public void apply( final Mode mode ) {
                    boolean visible = mode == Mode.GOING;
                    setVisible( visible );
                    setPickable( visible );
                    setChildrenPickable( visible );
                }
            } );
        }} );

        mode.addObserver( new VoidFunction1<Mode>() {
            public void apply( final Mode mode ) {
                updateForceListeners();
            }
        } );

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                if ( mode.get() == Mode.GOING ) {
                    double originalCartPosition = cart.getPosition();
                    final double dt = clockEvent.getSimulationTimeChange();
                    cart.stepInTime( dt, getSumOfForces() );
                    cartNode.translate( cart.getPosition() - originalCartPosition, 0 );
                }
            }
        } );
    }

    private Point2D getButtonLocation( PNode buttonNode ) {
        return new Point2D.Double( STAGE_SIZE.width / 2 - buttonNode.getFullBounds().getWidth() / 2, cartNode.getFullBounds().getMaxY() + INSET );
    }

    private Vector2D reflect( final Vector2D position, final double width ) {
        double distanceFromCenter = STAGE_SIZE.width / 2 - position.x;
        double newX = STAGE_SIZE.width / 2 + distanceFromCenter - width;
        return new Vector2D( newX, position.y );
    }

    public static PullerNode puller( PColor color, PSize size, final double scale, final Vector2D v, PullerContext context ) {
        return new PullerNode( color, size, 0, scale, v, context );
    }

    public void drag( final PullerNode pullerNode ) {
        //find closest knot node
        List<KnotNode> knots = pullerNode.color == BLUE ? blueKnots : redKnots;
        knots.foreach( KnotNode._unhighlight );
        Option<KnotNode> attachNode = getAttachNode( pullerNode );
        attachNode.foreach( new Effect<KnotNode>() {
            @Override public void e( final KnotNode knotNode ) {
                knotNode.setHighlighted( true );
            }
        } );
    }

    public void endDrag( final PullerNode pullerNode ) {
        blueKnots.append( redKnots ).foreach( KnotNode._unhighlight );
        Option<KnotNode> attachNode = getAttachNode( pullerNode );
        if ( attachNode.isSome() ) {
            Point2D hands = pullerNode.getGlobalAttachmentPoint();
            Point2D knot = attachNode.some().getGlobalFullBounds().getCenter2D();
            Vector2D delta = new Vector2D( hands, knot );
            Point2D local = pullerNode.getParent().globalToLocal( delta.toPoint2D() );
            pullerNode.animateToPositionScaleRotation( pullerNode.getOffset().getX() + local.getX(), pullerNode.getOffset().getY() + local.getY(), pullerNode.scale, 0, ANIMATION_DURATION );
            attachNode.some().setPullerNode( pullerNode );
            pullerNode.setKnot( attachNode.some() );
            updateForceListeners();
        }
        else {
            detach( pullerNode );
            pullerNode.animateHome();
        }
    }

    private void detach( final PullerNode pullerNode ) {
        KnotNode node = pullerNode.getKnot();
        if ( node != null ) {
            node.setPullerNode( null );
        }
        pullerNode.setKnot( null );
        updateForceListeners();
    }

    private void updateForceListeners() {
        forcesNode.setForces( mode.get() == Mode.WAITING, getLeftForce(), getRightForce() );

        for ( VoidFunction0 forceListener : forceListeners ) {
            forceListener.apply();
        }
    }

    private double getRightForce() {return redKnots.map( _force ).foldLeft( Doubles.add, 0.0 );}

    private double getLeftForce() {return -blueKnots.map( _force ).foldLeft( Doubles.add, 0.0 );}

    private double getSumOfForces() {return getRightForce() + getLeftForce();}

    public void startDrag( final PullerNode pullerNode ) {
        detach( pullerNode );
    }

    private Option<KnotNode> getAttachNode( final PullerNode pullerNode ) {
        List<KnotNode> knots = pullerNode.color == BLUE ? blueKnots : redKnots;
        List<KnotNode> free = knots.filter( _free ).filter( new F<KnotNode, Boolean>() {
            @Override public Boolean f( final KnotNode knotNode ) {
                return knotPullerDistance( knotNode, pullerNode ) < 80;
            }
        } );
        if ( free.length() > 0 ) {
            KnotNode closest = free.minimum( FJUtils.ord( new F<KnotNode, Double>() {
                @Override public Double f( final KnotNode k ) {
                    return knotPullerDistance( k, pullerNode );
                }
            } ) );
            return Option.some( closest );
        }
        else { return Option.none(); }
    }

    private double knotPullerDistance( final KnotNode k, final PullerNode p ) {return k.getGlobalFullBounds().getCenter2D().distance( p.getGlobalAttachmentPoint() );}

    public static enum PColor {BLUE, RED}

    public static enum PSize {SMALL, MEDIUM, LARGE}
}