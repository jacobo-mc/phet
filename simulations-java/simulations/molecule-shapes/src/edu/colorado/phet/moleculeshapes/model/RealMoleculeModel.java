// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.moleculeshapes.model.AttractorModel.ResultMapping;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.rangeInclusive;

/**
 * Represents a physically malleable version of a real molecule, with lone pairs if necessary. Different from the
 * RealMolecule class, which is a static representation that cannot be worked with.
 */
public class RealMoleculeModel extends MoleculeModel {

    private final RealMolecule realMolecule;

    // stores the ideal position vectors for both the bonds and lone pairs
    private List<ImmutableVector3D> idealPositionVectors;

    private Set<Element> elementsUsed = new HashSet<Element>();

    private List<Permutation> cachedPermutations;

    public RealMoleculeModel( final RealMolecule realMolecule ) {
        this.realMolecule = realMolecule;

        setSortingEnabled( false );
        repulsionMultiplier = 0;

        final int numLonePairs = realMolecule.getCentralLonePairCount();
        final int numBonds = realMolecule.getBonds().size();

        idealPositionVectors = new ArrayList<ImmutableVector3D>();

        // add in bonds
        for ( Bond<Atom3D> bond : realMolecule.getBonds() ) {
            Atom3D atom = bond.getOtherAtom( realMolecule.getCentralAtom() );
            ImmutableVector3D normalizedPosition = atom.position.get().normalized();
            idealPositionVectors.add( normalizedPosition );
            addPair( new RealPairGroup( normalizedPosition.times( PairGroup.BONDED_PAIR_DISTANCE ), bond.order, atom.getElement() ) );
            elementsUsed.add( atom.getElement() );
        }

        // all of the ideal vectors (including for lone pairs)
        final List<ImmutableVector3D> idealModelVectors = new VseprConfiguration( numBonds, numLonePairs ).geometry.unitVectors;

        // ideal vectors excluding lone pairs (just for the bonds)
        List<ImmutableVector3D> idealModelBondVectors = new ArrayList<ImmutableVector3D>() {{
            for ( int i = numLonePairs; i < idealModelVectors.size(); i++ ) {
                add( idealModelVectors.get( i ) );
            }
        }};

        ResultMapping mapping = AttractorModel.findClosestMatchingConfiguration( this, idealModelBondVectors, Permutation.permutations( idealModelBondVectors.size() ) );

        // add in lone pairs in their correct "initial" positions
        for ( int i = 0; i < numLonePairs; i++ ) {
            ImmutableVector3D normalizedPosition = mapping.rotateVector( idealModelVectors.get( i ) );
            idealPositionVectors.add( normalizedPosition );
            addPair( new PairGroup( normalizedPosition.times( PairGroup.LONE_PAIR_DISTANCE ), 0, false ) );
        }

        cachedPermutations = computePermutations();
    }

    @Override public List<ImmutableVector3D> getIdealPositionVectors() {
        return idealPositionVectors;
    }

    @Override public List<Permutation> getAllowablePositionPermutations() {
        return cachedPermutations;
    }

    @Override public boolean isReal() {
        return true;
    }

    public RealMolecule getRealMolecule() {
        return realMolecule;
    }

    private List<Permutation> computePermutations() {
        List<Permutation> permutations = new ArrayList<Permutation>();
        permutations.add( Permutation.identity( getGroups().size() ) );

        // permute away the lone pairs
        if ( getLonePairs().size() > 1 ) {
            List<Permutation> result = new ArrayList<Permutation>();
            for ( Permutation permutation : permutations ) {
                // lone pairs are stored at the back, so adjust accordingly
                result.addAll( permutation.withIndicesPermuted( rangeInclusive( getBondedGroups().size(), getGroups().size() - 1 ) ) );
            }
            permutations = result;
        }

        // for each element, add permutations if necessary
        for ( Element element : elementsUsed ) {
            List<Integer> indicesWithElement = new ArrayList<Integer>();
            for ( int i = 0; i < getBondedGroups().size(); i++ ) {
                if ( ( (RealPairGroup) ( getGroups().get( i ) ) ).getElement() == element ) {
                    indicesWithElement.add( i );
                }
            }

            // permutations only necessary if we have more than two of that type
            if ( indicesWithElement.size() > 1 ) {
                List<Permutation> result = new ArrayList<Permutation>();
                for ( Permutation permutation : permutations ) {
                    result.addAll( permutation.withIndicesPermuted( indicesWithElement ) );
                }
                permutations = result;
            }
        }

        return permutations;
    }
}
