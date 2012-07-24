// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Canvas for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameCanvas extends LGCanvas  {

    private final PNode settingsNode; // parent of the nodes related to choosing game settings
    private final PNode playNode; // parent of nodes related to playing the game
    private final ResultsNode resultsNode; // parent of nodes related to displaying game results
    private final GameAudioPlayer audioPlayer;

    public LineGameCanvas( final LineGameModel model ) {

        // audio
        audioPlayer = new GameAudioPlayer( model.settings.soundEnabled.get() );
        model.settings.soundEnabled.addObserver( new SimpleObserver() {
            public void update() {
                audioPlayer.setEnabled( model.settings.soundEnabled.get() );
            }
        } );

        // parent nodes for various "phases" of the game
        settingsNode = new SettingsNode( model, getStageSize() );
        playNode = new PlayNode( model, getStageSize(), audioPlayer );
        resultsNode = new ResultsNode( model, getStageSize(), this );

        // rendering order
        {
            addChild( resultsNode );
            addChild( playNode );
            addChild( settingsNode );
        }

        // game "phase" changes
        model.phase.addObserver( new VoidFunction1<GamePhase>() {
            public void apply( GamePhase gamePhase ) {

                // visibility of scenegraph branches
                settingsNode.setVisible( gamePhase == GamePhase.SETTINGS );
                playNode.setVisible( gamePhase == GamePhase.PLAY );
                resultsNode.setVisible( gamePhase == GamePhase.RESULTS );

                // make the reward fill the canvas
                resultsNode.setRewardBounds( getWorldBounds() );

                // play audio when game ends
                if ( gamePhase == GamePhase.RESULTS ) {
                    if ( model.score.get() == model.getPerfectScore() ) {
                        audioPlayer.gameOverPerfectScore();
                    }
                    else {
                        audioPlayer.gameOverImperfectScore();
                    }
                }
            }
        } );
    }

    // When the canvas size changes, update the rewards bounds so that the reward fills the canvas.
    @Override protected void updateLayout() {
        PBounds worldBounds = getWorldBounds();
        if ( worldBounds.getWidth() > 0 && worldBounds.getHeight() > 0 ) {
            resultsNode.setRewardBounds( worldBounds );
        }
    }

    public boolean isRewardRunning() {
        return resultsNode.isRewardRunning();
    }

    public void setRewardRunning( boolean running ) {
        resultsNode.setRewardRunning( running );
    }
}
