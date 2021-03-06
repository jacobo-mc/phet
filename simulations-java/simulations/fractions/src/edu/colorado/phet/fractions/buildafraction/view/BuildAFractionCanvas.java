// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.CompositeDelegate;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Canvas for the "Build a Fraction" tab.  Shows the level selection screen or a particular level.  Provides animation between different scenes.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements LevelSelectionContext, SceneContext {

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke CONTROL_PANEL_STROKE = new BasicStroke( 2 );

    private PNode currentScene;
    private final BuildAFractionModel model;

    //Keeps track of the scene node PNode for each level
    private final HashMap<LevelIdentifier, PNode> levelMap = new HashMap<LevelIdentifier, PNode>();

    private static final int CROSS_FADE_DURATION = 500;
    private final String title;

    public static final Color LIGHT_BLUE = new Color( 236, 251, 251 );

    private ArrayList<VoidFunction1<PNode>> levelStartedListeners = new ArrayList<VoidFunction1<PNode>>();

    public final Property<BuildAFractionScreenType> screenType = new Property<BuildAFractionScreenType>( BuildAFractionScreenType.LEVEL_SELECTION );

    public BuildAFractionCanvas( final BuildAFractionModel model, String title ) {
        this.model = model;
        this.title = title;
        //Set a really light blue because there is a lot of white everywhere
        setBackground( LIGHT_BLUE );

        currentScene = createLevelSelectionNode();
        addChild( currentScene );
    }

    public void addLevelStartedListener( VoidFunction1<PNode> listener ) {
        levelStartedListeners.add( listener );
    }

    public static enum Direction {
        RIGHT,
        LEFT,
        DOWN
    }

    //Fade out the current node and fade in the newly specified node.
    private void crossFadeTo( final PNode newNode ) {
        newNode.setTransparency( 0 );
        addChild( newNode );
        final PNode oldNode = currentScene;

        PActivity activity = oldNode.animateToTransparency( 0, CROSS_FADE_DURATION );
        activity.setDelegate( new PActivityDelegateAdapter() {
            public void activityFinished( final PActivity activity ) {
                oldNode.removeFromParent();
                newNode.animateToTransparency( 1, CROSS_FADE_DURATION );
            }
        } );
        currentScene = newNode;
        notifyLevelStarted( newNode );
    }

    //Scroll the main view to show the specified PNode by scrolling the specified direction.
    //For example, if the user presses the "Level 1" button, it would scroll to the right to show the level
    private void animateTo( final PNode node, Direction direction ) {
        node.setTransparency( 1 );
        Vector2D nodeOffset = direction == Direction.RIGHT ? new Vector2D( STAGE_SIZE.width, 0 ) :
                              direction == Direction.LEFT ? new Vector2D( -STAGE_SIZE.width, 0 ) :
                              direction == Direction.DOWN ? new Vector2D( 0, STAGE_SIZE.height ) :
                              Vector2D.ZERO;
        node.setOffset( nodeOffset.toPoint2D() );
        addChild( node );

        final PNode oldNode = currentScene;

        Vector2D oldNodeOffset = direction == Direction.RIGHT ? new Vector2D( -STAGE_SIZE.width, 0 ) :
                                 direction == Direction.LEFT ? new Vector2D( STAGE_SIZE.width, 0 ) :
                                 direction == Direction.DOWN ? new Vector2D( 0, -STAGE_SIZE.height ) :
                                 Vector2D.ZERO;

        PActivity activity = currentScene.animateToPositionScaleRotation( oldNodeOffset.x, oldNodeOffset.y, 1, 0, 400 );
        activity.setDelegate( new CompositeDelegate( new PActivityDelegateAdapter() {
            public void activityFinished( final PActivity activity ) {

                PInterpolatingActivity fade = oldNode.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
                fade.setDelegate( new PActivityDelegateAdapter() {
                    public void activityFinished( final PActivity activity ) {
                        oldNode.removeFromParent();
                    }
                } );
            }
        }, new DisablePickingWhileAnimating( currentScene, true ) ) );
        node.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
        currentScene = node;
        notifyLevelStarted( node );
    }

    //For sim sharing only, notify that a level has been started (if it is a level and not a level selection screen or other)
    private void notifyLevelStarted( PNode node ) {
        if ( node instanceof ShapeSceneNode || node instanceof NumberSceneNode ) {
            for ( VoidFunction1<PNode> listener : levelStartedListeners ) { listener.apply( node ); }
        }
    }

    public void levelButtonPressed( final LevelInfo info ) {

        //if level was in progress, go back to it.  Otherwise create a new one and cache it.
        PNode levelNode = levelNode( info.levelIdentifier );
        animateTo( levelNode, Direction.RIGHT );
        screenType.set( info.levelIdentifier.levelType.equals( LevelType.SHAPES ) ? BuildAFractionScreenType.SHAPES : BuildAFractionScreenType.NUMBERS );
    }

    private PNode levelNode( final LevelIdentifier level ) {
        if ( !levelMap.containsKey( level ) ) {
            levelMap.put( level, createLevelNode( level.levelIndex, level.levelType ) );
        }
        return levelMap.get( level );
    }

    public void reset() {
        model.resetAll();
        levelMap.clear();
        crossFadeTo( createLevelSelectionNode() );
        screenType.set( BuildAFractionScreenType.LEVEL_SELECTION );
    }

    //Creates the level selection node to be displayed
    private AbstractLevelSelectionNode createLevelSelectionNode() {
        return model.isMixedNumbers() ?
               new MixedNumbersLevelSelectionNode( title, this, model.audioEnabled, model.selectedPage, model.gameProgress ) :
               new LevelSelectionNode( title, this, model.audioEnabled, model.selectedPage, model.gameProgress );
    }

    public Component getComponent() { return this; }

    //Creates the SceneNode for the specified level and type of level (shapes or numbers)
    private PNode createLevelNode( final int levelIndex, final LevelType levelType ) {
        return levelType == LevelType.SHAPES ?
               new ShapeSceneNode( levelIndex, model, this, model.audioEnabled, false, true ) :
               new NumberSceneNode( levelIndex, rootNode, model, this, model.audioEnabled, false );
    }

    //Animates to the right to show the specified shape level
    public void goToShapeLevel( final int newLevelIndex ) {
        animateTo( levelNode( new LevelIdentifier( newLevelIndex, LevelType.SHAPES ) ), Direction.RIGHT );
        screenType.set( BuildAFractionScreenType.SHAPES );
    }

    //Animates to the right to show the specified number level
    public void goToNumberLevel( final int newLevelIndex ) {
        animateTo( levelNode( new LevelIdentifier( newLevelIndex, LevelType.NUMBERS ) ), Direction.RIGHT );
        screenType.set( BuildAFractionScreenType.NUMBERS );
    }

    //Animates to show the level selection screen
    public void goToLevelSelectionScreen( final int fromLevelIndex ) {
        model.selectedPage.set( fromLevelIndex < 5 ? 0 : 1 );
        animateTo( createLevelSelectionNode(), Direction.LEFT );
        screenType.set( BuildAFractionScreenType.LEVEL_SELECTION );
    }

    //The user has pressed the "refresh" button on a shape level and the level will be regenerated.
    public void resampleShapeLevel( final int levelIndex ) {
        model.resampleShapeLevel( levelIndex );
        final PNode newNode = createLevelNode( levelIndex, LevelType.SHAPES );
        levelMap.put( new LevelIdentifier( levelIndex, LevelType.SHAPES ), newNode );
        crossFadeTo( newNode );
        screenType.set( BuildAFractionScreenType.SHAPES );
    }

    //The user has pressed the "refresh" button on a number level and the level will be regenerated.
    public void resampleNumberLevel( final int levelIndex ) {
        model.resampleNumberLevel( levelIndex );
        final PNode newNode = createLevelNode( levelIndex, LevelType.NUMBERS );
        levelMap.put( new LevelIdentifier( levelIndex, LevelType.NUMBERS ), newNode );
        crossFadeTo( newNode );
        screenType.set( BuildAFractionScreenType.NUMBERS );
    }
}