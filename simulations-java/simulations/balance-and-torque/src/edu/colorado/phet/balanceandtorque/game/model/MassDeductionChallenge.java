// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.common.model.ColumnState;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;

/**
 * A challenge, used in the balance game, in which the user attempts to
 * deduce the mass of a "mystery mass" using another mass of a known value.
 *
 * @author John Blanco
 */
public class MassDeductionChallenge extends BalanceGameChallenge {

    private static final ChallengeViewConfig VIEW_CONFIG = new ChallengeViewConfig( BalanceAndTorqueResources.Strings.WHAT_IS_THE_MASS, true );

    /**
     * Constructor.
     *
     * @param fixedMasses
     * @param movableMasses
     * @param solutionToDisplay
     */
    public MassDeductionChallenge( final MassDistancePair fixedMasses, List<Mass> movableMasses, List<MassDistancePair> solutionToDisplay ) {
        super( ColumnState.NONE );
        List<MassDistancePair> fixedMassList = new ArrayList<MassDistancePair>() {{
            add( fixedMasses );
        }};
        this.fixedMassDistancePairs.addAll( fixedMassList );
        this.movableMasses.addAll( movableMasses );
        this.balancedConfiguration.addAll( solutionToDisplay );
    }

    @Override public ChallengeViewConfig getChallengeViewConfig() {
        return VIEW_CONFIG;
    }
}
