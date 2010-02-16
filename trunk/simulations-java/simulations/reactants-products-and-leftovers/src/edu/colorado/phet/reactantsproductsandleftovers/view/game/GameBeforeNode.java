/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.util.ArrayList;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameGuess;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameListener;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * "Before Reaction" box and controls for the Game, adds the ability to switch 
 * between viewing the actual reaction and the user's guess.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameBeforeNode extends AbstractBeforeNode {
    
    private final GameModel model;
    private final GameListener gameListener;
    private final ImageLayoutNode guessImagesNode; // parent node for "guess" images, handles layout of the images
    private final ArrayList<ArrayList<SubstanceImageNode>> reactantImageNodeLists; // one list of "guess" images per reactant
    private final MoleculesHiddenNode moleculesHiddenNode; // a message indicating that the molecule images are hidden
    
    public GameBeforeNode( GameModel model, PDimension boxSize ) {
        super( RPALStrings.LABEL_BEFORE_REACTION, boxSize, model.getChallenge().getReaction(), GameModel.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutNode( boxSize ) );
        
        // listen for changes to the user's guess
        this.model = model;
        gameListener = new GameAdapter() {
            @Override 
            public void guessChanged() {
                updateGuessImages();
            }
        };
        model.addGameListener( gameListener );
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        
        // one list of image nodes for each reactant
        reactantImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        for ( int i = 0; i < reaction.getNumberOfReactants(); i++ ) {
            reactantImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
        }
        
        // images
        guessImagesNode = new GridLayoutNode( boxSize );
        updateGuessImages();
        addChild( guessImagesNode );
        
        // "images hidden" message node
        moleculesHiddenNode = new MoleculesHiddenNode();
        addChild( moleculesHiddenNode );
        double x = ( boxSize.getWidth() - moleculesHiddenNode.getFullBoundsReference().getWidth() ) / 2;
        double y = ( boxSize.getHeight() - moleculesHiddenNode.getFullBoundsReference().getHeight() ) / 2;
        moleculesHiddenNode.setOffset( x, y );
        
        // default state
        GameChallenge challenge = model.getChallenge();
        if ( challenge.getChallengeType() == ChallengeType.BEFORE ) {
            showGuess( true /* editable */, challenge.isImagesVisible() );
        }
        else {
            showAnswer( challenge.isImagesVisible() );
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        model.removeGameListener( gameListener );
    }
    
    /**
     * Shows the images and quantities corresponding to the actual reaction.
     * @param showImages
     */
    public void showAnswer( boolean showImages ) {
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        ArrayList<QuantityValueNode> valueNodes = getReactantValueNodes();
        
        // reactants
        for ( int i = 0; i < valueNodes.size(); i++ ) {
            QuantityValueNode valueNode = valueNodes.get( i );
            // attach to reactant of reaction
            valueNode.setSubstance( reaction.getReactant( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        // show images for reaction
        showGuessImages( false );
        showAnswerImages( showImages );
        showImagesHiddenMessage( !showImages );
    }
    
    /**
     * Shows the images and quantities corresponding to the user's guess.
     * The quantities are optionally editable.
     * @param editable
     * @param showImages
     */
    public void showGuess( boolean editable, boolean showImages ) {
        
        GameGuess guess = model.getChallenge().getGuess();
        ArrayList<QuantityValueNode> valueNodes = getReactantValueNodes();
        
        // reactants
        for ( int i = 0; i < valueNodes.size(); i++ ) {
            QuantityValueNode valueNode = valueNodes.get( i );
            // attach to reactant of user's answer
            valueNode.setSubstance( guess.getReactant( i ) );
            // set editability
            valueNode.setEditable( editable );
        }
        
        // show images for user's answer
        showAnswerImages( false );
        showGuessImages( showImages );
        showImagesHiddenMessage( false );
    }
    
    public void showAnswerImages( boolean b ) {
        setImagesVisible( b );
    }
    
    public void showGuessImages( boolean b ) {
        guessImagesNode.setVisible( b );
    }
    
    public void showImagesHiddenMessage( boolean b ) {
        moleculesHiddenNode.setVisible( b );
    }
    
    /*
     * Updates images for reactants to match the user's guess.
     * The last image added is the first to be removed. 
     */
    private void updateGuessImages() {
        
        GameGuess guess = model.getChallenge().getGuess();
        ArrayList<QuantityValueNode> valueNodes = getReactantValueNodes();

        /*
         * Do all removal first, so that we free up space in the box.
         */

        // remove reactants
        Reactant[] reactants = guess.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = reactantImageNodeLists.get( i );
            while ( reactant.getQuantity() < imageNodes.size() ) {
                SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                imageNode.cleanup();
                guessImagesNode.removeNode( imageNode );
                imageNodes.remove( imageNode );
            }
        }

        /*
         * Do all additions after removals, so that we have free space in the box.
         */

        // add reactants
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = reactantImageNodeLists.get( i );
            while ( reactant.getQuantity() > imageNodes.size() ) {
                PNode lastNodeAdded = null;
                if ( imageNodes.size() > 0 ) {
                    lastNodeAdded = imageNodes.get( imageNodes.size() - 1 );
                }
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                guessImagesNode.addNode( imageNode, lastNodeAdded, valueNodes.get( i ) );
                lastNodeAdded = imageNode;
            }
        }
    }
    
}
