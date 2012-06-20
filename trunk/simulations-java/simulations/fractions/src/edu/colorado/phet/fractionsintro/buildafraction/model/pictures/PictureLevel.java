package edu.colorado.phet.fractionsintro.buildafraction.model.pictures;

import fj.F;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Level for the build a fraction game.
 *
 * @author Sam Reid
 */
public class PictureLevel {

    //Fractions the user has created in the play area, which may match a target
    public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );

    public final List<Integer> containers;
    public final List<PictureTarget> targets;
    public final List<Integer> pieces;

    //Cannot be a constructor because has same erasure
    public static PictureLevel pictureLevel( final List<Integer> containers, final List<Integer> pieces, final List<Fraction> targets ) {
        return new PictureLevel( containers, pieces, targets.map( new F<Fraction, PictureTarget>() {
            @Override public PictureTarget f( final Fraction fraction ) {
                return new PictureTarget( fraction );
            }
        } ) );
    }

    public PictureLevel( final List<Integer> containers, final List<Integer> pieces, final List<PictureTarget> targets ) {
        this.containers = containers;
        this.targets = targets;
        this.pieces = pieces;
    }

    public PictureTarget getTarget( final int i ) { return targets.index( i ); }

    public void resetAll() { createdFractions.reset(); }
}