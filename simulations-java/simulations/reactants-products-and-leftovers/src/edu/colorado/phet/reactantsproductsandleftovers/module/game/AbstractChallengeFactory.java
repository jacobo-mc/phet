/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;

/**
 * Base class for creating game challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractChallengeFactory implements IChallengeFactory {
    
    private static final boolean DEBUG_OUTPUT_ENABLED = true;
    
    /*
     * Generates a random non-zero quantity.
     * We need at least one of each reactant to have a valid reaction.
     */
    protected static int getRandomQuantity( int maxQuantity ) {
        return 1 + (int) ( Math.random() * maxQuantity );
    }

    /**
     * Relies on the subclass to create the challenges.
     * Then fixes any problems related to quantity range violations.
     * 
     * @param numberOfChallenges
     * @param level 1-N
     * @param maxQuantity
     * @param imagesVisible
     */
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, int maxQuantity, boolean imagesVisible ) {
        GameChallenge[] challenges = createChallengesAux( numberOfChallenges, level, maxQuantity, imagesVisible );
        fixQuantityRangeViolations( challenges, maxQuantity );
        return challenges;
    }
    
    /**
     * Abstract "hook" in the base class.
     * This handles creation of the challenges, which the base class then verifies.
     *
     * @param numberOfChallenges
     * @param level 1-N
     * @param maxQuantity
     * @param imagesVisible
     */
    protected abstract GameChallenge[] createChallengesAux( int numberOfChallenges, int level, int maxQuantity, boolean imagesVisible );
    
    /*
     * Uses reflection to instantiate a chemical reaction by class.
     */
    protected static ChemicalReaction instantiateReaction( Class<? extends ChemicalReaction> c ) {
        ChemicalReaction reaction = null;
        try {
            reaction = c.newInstance();
        }
        catch ( InstantiationException e ) {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return reaction;
    }
    
    /*
     * Ensures that all quantity values are in the range supported by the model and user interface.
     */
    private void fixQuantityRangeViolations( GameChallenge[] challenges, int maxQuantity ) {
        for ( GameChallenge challenge : challenges ) {
            ChemicalReaction reaction = challenge.getReaction();
            fixQuantityRangeViolation( reaction, maxQuantity );
        }
    }
    
    /*
     * Fixes any quantity range violations in a reaction.
     * We do this by decrementing reactant quantites by 1, alternating reactants as we do so.
     * Each reactant must have a quantity of at least 1, in order to have a valid reaction.
     * 
     * @throw IllegalStateException if reducing all reactant quantities to 1 does not fix a range violation
     */
    protected static void fixQuantityRangeViolation( ChemicalReaction reaction, int maxQuantity ) {
        
        if ( hasQuantityRangeViolation( reaction ) ) {

            if ( DEBUG_OUTPUT_ENABLED ) {
                System.out.print( "DEBUG:" );
                System.out.print( " AbstractGameStrategy.fixQuantityRangeViolation" );
                System.out.print( " reaction: " + reaction.getEquationPlainText() );
                System.out.print( " violation: " + reaction.getQuantitiesString() );
            }

            // First, make sure all reactant quantities are in range.
            for ( Reactant reactant : reaction.getReactants() ) {
                if ( reactant.getQuantity() > maxQuantity ) {
                    reactant.setQuantity( maxQuantity );
                }
            }

            // Then incrementally reduce reactant quantities, alternating reactants.
            int reactantIndex = 0;
            boolean changed = false;
            while ( hasQuantityRangeViolation( reaction ) ) {
                Reactant reactant = reaction.getReactant( reactantIndex );
                int quantity = reactant.getQuantity();
                if ( quantity > 1 ) {
                    reactant.setQuantity( quantity - 1 );
                    changed = true;
                }
                reactantIndex++;
                if ( reactantIndex > reaction.getNumberOfReactants() - 1 ) {
                    reactantIndex = 0;
                    if ( !changed ) {
                        // we haven't been able to reduce any reactant
                        break;
                    }
                }
            }

            // If all reactants have been reduced to 1 and we are still out of range, bail with a serious error.
            if ( hasQuantityRangeViolation( reaction ) ) {
                throw new IllegalStateException( "range violation can't be fixed: " + reaction.getEquationHTML() + " : " + reaction.getQuantitiesString() );
            }

            if ( DEBUG_OUTPUT_ENABLED ) {
                System.out.println( " fixed: " + reaction.getQuantitiesString() );
            }
        }
    }
    
    /*
     * Checks a reaction for quantity range violations.
     */
    protected static boolean hasQuantityRangeViolation( ChemicalReaction reaction ) {
        final int maxQuantity = GameModel.getQuantityRange().getMax();
        boolean violation = false;
        for ( int i = 0; !violation && i < reaction.getNumberOfReactants(); i++ ) {
            if ( reaction.getReactant( i ).getQuantity() > maxQuantity || reaction.getReactant( i ).getLeftovers() > maxQuantity ) {
                violation = true;
            }
        }
        for ( int i = 0; !violation && i < reaction.getNumberOfProducts(); i++ ) {
            if ( reaction.getProduct( i ).getQuantity() > maxQuantity ) {
                violation = true;
            }
        }
        return violation;
    }
    
    /*
     * Looks for equations that will experience a violation of the quantity range.
     * Suppose the quantity range is 0-N.  For some reactions, setting the reactant quantities 
     * to N will result in a product quantity > N.  This will result in range violations 
     * elsewhere in the application, for example in the controls used to set and display
     * quantity values.
     */
    protected static void analyzeRangeViolations( ArrayList<Class<? extends ChemicalReaction>> reactionClasses, int maxQuantity ) {
        for ( Class<? extends ChemicalReaction> reactionClass : reactionClasses ) {
            ChemicalReaction reaction = instantiateReaction( reactionClass );
            // set all reactant quantities to their max values.
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( maxQuantity );
            }
            // look for violations and try to fix them.
            fixQuantityRangeViolation( reaction, maxQuantity );
        }
    }
}
