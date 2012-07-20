// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import fj.F3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.functions.game.API.*;

/**
 * @author Sam Reid
 */
public class GameInstance {
    private final JFrame frame = new JFrame() {{
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }};
    private final Game game;
    private GameState state = null;
    private final PhetPCanvas canvas;
    private PNode rootNode = new PNode();
    private double time = 0;//in seconds
    private EventRecorder eventRecorder = new EventRecorder();

    public GameInstance( final Game game ) {
        this.game = game;
        canvas = new PhetPCanvas() {{
            setPreferredSize( new Dimension( 1024, 768 ) );
            addScreenChild( rootNode );
        }};
        frame.setContentPane( canvas );
        frame.pack();
    }

    private void start() {
        frame.setVisible( true );
        state = game.initialState;
        final int delay = 20;
        new Timer( delay, new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                rootNode.removeAllChildren();
                rootNode.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, canvas.getWidth(), canvas.getHeight() ), Color.yellow ) {{
                    addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mousePressed( final PInputEvent event ) {
                            eventRecorder.mousePressed( new Vector2D( event.getCanvasPosition() ) );
                        }

                        @Override public void mouseDragged( final PInputEvent event ) {
                            eventRecorder.mousePressed( new Vector2D( event.getCanvasPosition() ) );
                        }
                    } );
                }} );
                rootNode.addChild( state.toPiccoloNode( eventRecorder ) );
                state = game.update.f( state, time, eventRecorder.process() );
                time = time + delay;//TODO: read system clock for this one.
            }
        } ).start();
    }

    private static final F3<GameState, Double, UserInput, GameState> animation1 = new F3<GameState, Double, UserInput, GameState>() {
        @Override public GameState f( final GameState state, final Double time, final UserInput userInput ) {

            //A function like this is interactively generated by the user with the sim
            //                return gameState( append( translate( head( state ), new Vector2D( 1, 1 ) ), "!" ) );
            return gameState( translate( head( state ), new Vector2D( 1, 1 ) ) );
        }
    };

    private static final F3<GameState, Double, UserInput, GameState> animation2 = new F3<GameState, Double, UserInput, GameState>() {
        @Override public GameState f( final GameState state, final Double time, final UserInput userInput ) {
            return gameState( append( translate( head( state ), new Vector2D( 1, 1 ) ), "!" ) );
        }
    };

    private static final F3<GameState, Double, UserInput, GameState> followMouse = new F3<GameState, Double, UserInput, GameState>() {
        @Override public GameState f( final GameState state, final Double time, final UserInput userInput ) {
            return gameState( setPosition( head( state ), userInput.pressLocation.orSome( head( state ).position ) ) );
        }
    };

    //To solve the tuple problem: Different styles for GameState of increasing complexity:
    //1. single item
    //2. List of homogeneous items
    //3. Heterogeneous list of homogeneous lists

    //How about just adding a button that lets user create their own named (and typed) tuple data structure?
    public static void main( String[] args ) {
        Game game = new Game( new GameState( new GameItem( "hello", new Vector2D( 100, 100 ) ) ), animation1 );
        new GameInstance( game ).start();
    }
}