package edu.colorado.phet.buildanatom.modules.game;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * The primary model for the Build an Atom game.  This sequences the game and
 * sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameModel {
    public static final int MAX_LEVELS = 3;
    public static final int MAX_SCORE = 5;

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    private final GameSettingsState gameSettingsState = new GameSettingsState( this );
    private final GameOver gameOverState = new GameOver( this );

    public GameModel() {
        setState( gameSettingsState );
    }

    public GameSettingsState getGameSettingsState() {
        return gameSettingsState;
    }

    void setState( State newState ) {
        if ( currentState != newState ) {
            State oldState = currentState;
            currentState = newState;
            for ( GameModelListener listener : listeners ) {
                listener.stateChanged( oldState, currentState );
            }
        }
    }

    public State getState() {
        return currentState;
    }

    public void startGame( int level, boolean timerOn, boolean soundOn ) {
        ProblemSet problemSet = new ProblemSet( this, level, timerOn, soundOn );
        for ( GameModelListener listener : listeners ) {
            listener.problemSetCreated( problemSet );
        }
        setState( problemSet.getProblem( 0 ) );
    }

    public void addListener( GameModelListener listener ) {
        listeners.add( listener );
    }

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );

        void problemSetCreated( ProblemSet problemSet );
    }

    public abstract static class State {
        protected final GameModel model;

        public State( GameModel model ) {
            this.model = model;
        }
    }

    public static class GameSettingsState extends State {
        public GameSettingsState( GameModel model ) {
            super( model );
        }
    }

    /**
     * Represents one of the Problems in the game, formerly called Challenge.
     */
    public static class Problem extends State {
        private final ProblemSet problemSet;
        private Atom atom;

        public Problem( GameModel model, ProblemSet problemSet, Atom atom ) {
            super( model );
            this.problemSet = problemSet;
            this.atom = atom;
        }

        public Atom getAtom() {
            return atom;
        }

        public void checkGuess() {
            if ( problemSet.isLastProblem( this ) ) {
                model.setState( model.getGameOverState() );
            }
            else {
                model.setState( problemSet.getNextProblem( this ) );
            }
        }
    }

    public static class GameOver extends State {
        public GameOver( GameModel model ) {
            super( model );
        }
    }

    public void newGame() {
        setState( gameSettingsState );
    }

    public State getGameOverState() {
        return gameOverState;
    }

    public static class ProblemSet {
        //keep track by type
        private ArrayList<CompleteTheModelProblem> completeTheModelProblems = new ArrayList<CompleteTheModelProblem>();
        private ArrayList<CompleteTheSymbolProblem> completeTheSymbolProblems = new ArrayList<CompleteTheSymbolProblem>();
        private ArrayList<HowManyParticlesProblem> howManyParticlesProblems = new ArrayList<HowManyParticlesProblem>();
        //keeps track by ordering
        private ArrayList<Problem> allProblems = new ArrayList<Problem>();

        public ProblemSet( GameModel model, int level, boolean timerOn, boolean soundOn ) {
            addProblem( new CompleteTheModelProblem( model, level, timerOn, soundOn, this ) );
            addProblem( new CompleteTheSymbolProblem( model, level, timerOn, soundOn, this ) );
            addProblem( new HowManyParticlesProblem( model, level, timerOn, soundOn, this ) );
        }

        private void addProblem( HowManyParticlesProblem howManyParticlesProblem ) {
            howManyParticlesProblems.add( howManyParticlesProblem );
            allProblems.add( howManyParticlesProblem );
        }

        private void addProblem( CompleteTheSymbolProblem completeTheSymbolProblem ) {
            completeTheSymbolProblems.add( completeTheSymbolProblem );
            allProblems.add( completeTheSymbolProblem );
        }

        private void addProblem( CompleteTheModelProblem completeTheModelProblem ) {
            completeTheModelProblems.add( completeTheModelProblem );
            allProblems.add( completeTheModelProblem );
        }

        public int getNumCompleteTheModelProblems() {
            return completeTheModelProblems.size();
        }

        public int getNumCompleteTheSymbolProblems() {
            return completeTheSymbolProblems.size();
        }

        public int getNumHowManyParticlesProblems() {
            return howManyParticlesProblems.size();
        }

        public CompleteTheModelProblem getCompleteTheModelProblem( int i ) {
            return completeTheModelProblems.get( i );
        }

        public CompleteTheSymbolProblem getCompleteTheSymbolProblem( int i ) {
            return completeTheSymbolProblems.get( i );
        }

        public HowManyParticlesProblem getHowManyParticlesProblem( int i ) {
            return howManyParticlesProblems.get( i );
        }

        public Problem getProblem( int i ) {
            return allProblems.get( i );
        }

        public int getProblemIndex( Problem problem ) {
            return allProblems.indexOf( problem );
        }

        public int getTotalNumProblems() {
            return allProblems.size();
        }

        public boolean isLastProblem( Problem problem ) {
            return getProblemIndex( problem ) == getTotalNumProblems() - 1;
        }

        public Problem getNextProblem( Problem problem ) {
            return getProblem( getProblemIndex( problem ) + 1 );
        }
    }

    public static class AtomValue extends Atom {

        /**
         * Represents one row from the table defined in the design doc (see pools for level 1-3)
         * @param protons
         * @param neutrons
         * @param electrons
         */
        public AtomValue( int protons, int neutrons, int electrons ) {
            super( new Point2D.Double( 0, 0 ) );
            for ( int i = 0; i < protons; i++ ) {
                addProton( new Proton( ConstantDtClock.TEST ) );
            }
            for ( int i = 0; i < neutrons; i++ ) {
                addNeutron( new Neutron( ConstantDtClock.TEST ) );
            }
            for ( int i = 0; i < electrons; i++ ) {
                addElectron( new Electron( ConstantDtClock.TEST ) );
            }
        }
    }

    public static class CompleteTheModelProblem extends Problem {
        public CompleteTheModelProblem( GameModel model, int level, boolean timerOn, boolean soundOn, ProblemSet problemSet ) {
            super( model, problemSet, new AtomValue( 1, 0, 1 ) );
        }
    }

    public static class CompleteTheSymbolProblem extends Problem {
        public CompleteTheSymbolProblem( GameModel model, int level, boolean timerOn, boolean soundOn, ProblemSet problemSet ) {
            super( model, problemSet, new AtomValue( 2, 2, 2 ) );
        }
    }

    public static class HowManyParticlesProblem extends Problem {
        public HowManyParticlesProblem( GameModel model, int level, boolean timerOn, boolean soundOn, ProblemSet problemSet ) {
            super( model, problemSet, new AtomValue( 3, 4, 3 ) );
        }
    }
}
