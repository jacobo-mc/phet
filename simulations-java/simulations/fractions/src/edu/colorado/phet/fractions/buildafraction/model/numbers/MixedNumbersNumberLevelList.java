// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import fj.Effect;
import fj.F;
import fj.P2;
import fj.Unit;
import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.model.NumberLevelFactory;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternType;

import static edu.colorado.phet.fractions.buildafraction.model.MixedFraction.mixedFraction;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevelList.*;
import static edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget.*;
import static edu.colorado.phet.fractions.common.math.Fraction.fraction;
import static edu.colorado.phet.fractions.common.util.Sampling.*;
import static fj.Unit.unit;
import static fj.data.List.iterableList;
import static fj.data.List.list;

/**
 * List of levels used for the "Mixed Fractions" tab, for the levels in which number cards are used.
 *
 * @author Sam Reid
 */
public class MixedNumbersNumberLevelList implements NumberLevelFactory {
    public static final Random random = new Random();
    private final ArrayList<Function0<NumberLevel>> levels = new ArrayList<Function0<NumberLevel>>();

    //Creates the levels for this game. Please enable closure folding to be able to read it.
    public MixedNumbersNumberLevelList() {
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return level1();
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return level2();
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return level3();
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return level4();
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return withDifferentRepresentations( new F<Unit, NumberLevel>() {
                    @Override public NumberLevel f( final Unit unit ) {
                        return level5();
                    }
                } );
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return withDifferentRepresentations( new F<Unit, NumberLevel>() {
                    @Override public NumberLevel f( final Unit unit ) {
                        return level6();
                    }
                } );
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return withDifferentRepresentations( new F<Unit, NumberLevel>() {
                    @Override public NumberLevel f( final Unit unit ) {
                        return level7();
                    }
                } );
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return withDifferentRepresentations( new F<Unit, NumberLevel>() {
                    @Override public NumberLevel f( final Unit unit ) {
                        return level8();
                    }
                } );
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return withDifferentRepresentations( new F<Unit, NumberLevel>() {
                    @Override public NumberLevel f( final Unit unit ) {
                        return level9();
                    }
                } );
            }
        } );
        add( new Function0<NumberLevel>() {
            public NumberLevel apply() {
                return withDifferentRepresentations( new F<Unit, NumberLevel>() {
                    @Override public NumberLevel f( final Unit unit ) {
                        return level10();
                    }
                } );
            }
        } );
    }

    private void add( final Function0<NumberLevel> level ) { levels.add( level ); }

    public NumberLevel createLevel( final int level ) { return levels.get( level ).apply(); }

    //Keep sampling from the level until we find a level with two different shape types
    //REVIEW describe what happens when we exceed 10 tries, not obvious what f.f(unit()) is.
    private NumberLevel withDifferentRepresentations( final F<Unit, NumberLevel> f ) {
        int count = 0;
        while ( count < 10 ) {
            NumberLevel level = f.f( unit() );
            if ( level.hasDifferentShapeTypes() ) {
                System.out.println( "Has different types, using it" );
                System.out.println( "level.getShapeTypes():" );
                level.getPatternTypes().foreach( new Effect<PatternType>() {
                    @Override public void e( final PatternType patternType ) {
                        System.out.println( " > " + patternType );
                    }
                } );
                return level;
            }
            else {
                System.out.println( "Same types: " );
                level.getPatternTypes().foreach( new Effect<PatternType>() {
                    @Override public void e( final PatternType patternType ) {
                        System.out.println( " > " + patternType );
                    }
                } );
            }
            count++;
            System.out.println( "count = " + count );
        }
        return f.f( unit() );
    }

    /*Level 1:
    -- Circles as targets
    -- {1:1/2, 2:1/2, 3:1/4} as the challenges
    --just enough cards to complete targets
    --When the user presses "refresh" it should randomly reorder and recolor the targets
    */
    private NumberLevel level1() {
        RandomColors3 colors = new RandomColors3();
        return new NumberLevel( shuffle( list( target( 1, 1, 2, colors.next(), pie.sequential() ),
                                               target( 2, 1, 2, colors.next(), pie.sequential() ),
                                               target( 3, 1, 4, colors.next(), pie.sequential() ) ) ) );
    }

    /* Level 2:
    -- Circles or Rectangles as targets, but all targets same shape
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4} */
    private NumberLevel level2() {
        RandomColors3 colors = new RandomColors3();
        final F<MixedFraction, FilledPattern> shape = random.nextBoolean() ? pie.sequential() : horizontalBar.sequential();
        List<Integer> wholes = list( 1, 2, 3 );
        List<Fraction> fractionParts = list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 4 ), fraction( 3, 4 ) );
        List<MixedFraction> mixedFractions = getMixedFractions( wholes, fractionParts );

        List<MixedFraction> selected = choose( 3, mixedFractions );
        return new NumberLevel( selected, colors, shape );
    }

    //Generate all combinations of mixed fractions from the given wholes and fraction parts
    public static List<MixedFraction> getMixedFractions( final List<Integer> wholes, final List<Fraction> fractionParts ) {//Do not let any MixedFraction be selected twice
        ArrayList<MixedFraction> _mixedFractions = new ArrayList<MixedFraction>();
        for ( Integer whole : wholes ) {
            for ( Fraction fractionPart : fractionParts ) {
                _mixedFractions.add( mixedFraction( whole, fractionPart ) );
            }
        }
        return iterableList( _mixedFractions );
    }

    /*Level 3:
    -- All targets “six flowers”
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, 1/6, 5/6}
    -- So, if a “six flower” is showing 3/6, we will want a 1 and 2 card in the deck*/
    private NumberLevel level3() {
        List<MixedFraction> mixedFractions = getMixedFractions( list( 1, 2, 3 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ), fraction( 1, 6 ), fraction( 5, 6 ) ) );

        RandomColors3 colors = new RandomColors3();
        List<MixedFraction> targets = choose( 3, mixedFractions );

        //Scale so they fit in the flowers which requires a denominator of 6
        return new NumberLevel( list( target( targets.index( 0 ), colors.next(), scaleFlowerToSixths() ),
                                      target( targets.index( 1 ), colors.next(), scaleFlowerToSixths() ),
                                      target( targets.index( 2 ), colors.next(), scaleFlowerToSixths() ) ) );

    }

    /*Level 4:
    -- All pyramids
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4, 1/9, 2/9, 4/9, 5/9, 7/9, 8/9}*/
    private NumberLevel level4() {
        List<MixedFraction> mixedFractions = getMixedFractions( list( 1, 2, 3 ), list( fraction( 1, 2 ), fraction( 1, 3 ), fraction( 2, 3 ),
                                                                                       fraction( 1, 4 ), fraction( 3, 4 ),
                                                                                       fraction( 1, 9 ), fraction( 2, 9 ), fraction( 4, 9 ), fraction( 5, 9 ), fraction( 7, 9 ), fraction( 8, 9 )
        ) );
        return new NumberLevel( choose( 3, mixedFractions ), new RandomColors3(), new F<MixedFraction, FilledPattern>() {
            @Override public FilledPattern f( final MixedFraction mixedFraction ) {
                final int d = mixedFraction.getFractionPart().denominator;

                //These next two lines are tricky, let me explain them.
                //We want to use the reduced fractions in the fraction declarations above so when the levels are generated they will have a perfect match for the cards
                //However, some of the values don't have a direct representation in the pyramid shape.  For example: 1/2 appears as a 2/4 pyramid.
                //So we have to make the mapping here, which is used for making the "wholes" as well as the fraction parts.
                //For d==4 and d=9, the regular representations can be used
                return d == 2 ? pyramid4.sequential().f( mixedFraction.scaleNumeratorAndDenominator( 2 ) ) :
                       d == 3 ? pyramid9.sequential().f( mixedFraction.scaleNumeratorAndDenominator( 3 ) ) :
                       d == 4 ? pyramid4.sequential().f( mixedFraction ) :
                       d == 9 ? pyramid9.sequential().f( mixedFraction ) :
                       null;
            }
        } );
    }

    /*Level 5:
    --All representations possible, but each target is only one type of representation
    -- 1, 2, or 3, as whole number
    -- Fractional portion from the set {1/2, 1/3, 2/3, 1/4, 3/4, 1/5, 2/5, 3/5, 4/5, 1/6, 5/6, 1/7, 2/7, 3/7, 4/7, 5/7, 6/7, 1/8, 3/8, 5/8, 7/8, 1/9, 2/9, 4/9, 5/9, 7/9, 8/9}
    --2 of the representations match cards exactly, 1 of the representations requires simplifying to a solution*/
    NumberLevel level5() {
        List<Integer> wholes = list( 1, 2, 3 );
        List<MixedFraction> mixedFractions = getMixedFractions( wholes, fullListOfFractions() );
        final List<MixedFraction> targets = choose( 2, mixedFractions );
        RandomColors3 colors = new RandomColors3();
        final MixedFraction target1 = targets.index( 0 );
        final MixedFraction target2 = targets.index( 1 );
        final MixedFraction target3 = chooseOne( getMixedFractions( wholes, expandable() ) );
        final long seed = random.nextLong();
        return new NumberLevel( list( target( target1, colors.next(), chooseMatchingPattern( target1.denominator ).sequential() ),
                                      target( target2, colors.next(), chooseMatchingPattern( target2.denominator ).sequential() ),

                                      //For the third target, scale it up so that it is shown as a non-reduced picture
                                      target( target3, colors.next(), new F<MixedFraction, FilledPattern>() {
                                          @Override public FilledPattern f( final MixedFraction mixedFraction ) {
                                              int d = mixedFraction.getFractionPart().denominator;
                                              List<Integer> scaleFactors = getScaleFactors( d );

                                              //Use the same random seed each time otherwise composite representations might have different shape types for each of its parts
                                              Integer scaleFactor = chooseOneWithSeed( seed, scaleFactors );
                                              return chooseOneWithSeed( seed, matching( d * scaleFactor ) ).sequential().f( mixedFraction.scaleNumeratorAndDenominator( scaleFactor ) );
                                          }
                                      } ) ) );
    }

    private List<Integer> getScaleFactors( final int d ) {
        final List<Integer> value = d == 2 ? List.list( 2, 3, 4 ) :
                                    d == 3 ? List.list( 2, 3 ) :
                                    d == 4 ? List.list( 2 ) :
                                    null;
        assert value != null;
        return value;
    }

    private PatternMaker chooseMatchingPattern( final int denominator ) { return chooseOne( matching( denominator ) ); }

    private List<PatternMaker> matching( final int denominator ) {
        return NumberLevelList.allTypes.filter( new F<PatternMaker, Boolean>() {
            @Override public Boolean f( final PatternMaker patternMaker ) {
                return patternMaker.acceptedDenominators.exists( equalsInt( denominator ) );
            }
        } );
    }

    private F<Integer, Boolean> equalsInt( final int denominator ) {
        return new F<Integer, Boolean>() {
            @Override public Boolean f( final Integer integer ) {
                return integer == denominator;
            }
        };
    }

    //Parse a list of fractions.
    public static List<Fraction> parse( final String s ) {
        StringTokenizer st = new StringTokenizer( s, ", " );
        ArrayList<Fraction> f = new ArrayList<Fraction>();
        while ( st.hasMoreTokens() ) {
            String fraction = st.nextToken();
            StringTokenizer st2 = new StringTokenizer( fraction, "/" );
            f.add( fraction( Integer.parseInt( st2.nextToken() ), Integer.parseInt( st2.nextToken() ) ) );
        }
        return iterableList( f );
    }

    /**
     * Level 6:
     * --Same as level 5 (now with 4 targets)
     * --Random fill now possible, so for instance {2:1/4} could be represented by 2 full circles with a partially filled circle in between them.
     * As in, we do not need to strictly fill from left to right.
     * -- 2 of the representations require simplifying
     */
    NumberLevel level6() {
        List<Fraction> expandable = expandable();
        List<Integer> wholes = list( 1, 2, 3 );
        List<MixedFraction> mixedFractions = getMixedFractions( wholes, fullListOfFractions() );
        final List<MixedFraction> targets = choose( 2, mixedFractions );
        RandomColors4 colors = new RandomColors4();
        final MixedFraction target1 = targets.index( 0 );
        final MixedFraction target2 = targets.index( 1 );
        final P2<MixedFraction, MixedFraction> target34 = chooseTwo( getMixedFractions( wholes, expandable ) );
        final MixedFraction target3 = target34._1();
        final MixedFraction target4 = target34._2();
        final long seed = random.nextLong();
        return new NumberLevel( list( shuffledTarget( target1, colors.next(), chooseMatchingPattern( target1.denominator ).random() ),
                                      shuffledTarget( target2, colors.next(), chooseMatchingPattern( target2.denominator ).random() ),

                                      //For the third target, scale it up so that it is shown as a non-reduced picture
                                      shuffledTarget( target3, colors.next(), scaledRepresentation( seed, true, true ) ),
                                      shuffledTarget( target4, colors.next(), scaledRepresentation( seed, true, true ) ) ) );
    }

    //Fractions that can be scaled up
    private List<Fraction> expandable() {return parse( "1/2, 1/3, 2/3, 1/4, 3/4" );}

    private List<Fraction> fullListOfFractions() {
        return parse( "1/2, 1/3, 2/3, 1/4, 3/4, 1/5, 2/5, 3/5, 4/5, 1/6, 5/6, 1/7, 2/7, 3/7, 4/7, 5/7, 6/7, 1/8, 3/8, 5/8, 7/8, 1/9, 2/9, 4/9, 5/9, 7/9, 8/9" );
    }

    private F<MixedFraction, FilledPattern> scaleFlowerToSixths() {
        return new F<MixedFraction, FilledPattern>() {
            @Override public FilledPattern f( final MixedFraction mixedFraction ) {
                int d = mixedFraction.getFractionPart().denominator;

                //Use the same random seed each time otherwise composite representations might have different shape types for each of its parts
                Integer scaleFactor = 6 / d;
                return NumberLevelList.flower.sequential().f( mixedFraction.scaleNumeratorAndDenominator( scaleFactor ) );
            }
        };
    }

    private F<MixedFraction, FilledPattern> scaledRepresentation( final long seed, final boolean random, final boolean reallyScaleIt ) {
        return new ScaledRepresentation( seed, random, reallyScaleIt );
    }

    /**
     * Level 7:
     * --Top two representations are equivalent in magnitude, and bottom 2 representations are equivalent in magnitude
     * -- For instance if the top two representations are {1:1/2}, the first representation could be a full circle and
     * a half circle divided in halves, and the second representation could be a full circle and a half circle divide in fourths.
     */
    NumberLevel level7() {
        List<MixedFraction> fractions = getMixedFractions( list( 1, 2, 3 ), expandable() );
        P2<MixedFraction, MixedFraction> selected = chooseTwo( fractions );
        MixedFraction topOne = selected._1();
        MixedFraction bottomOne = selected._2();
        final long seed = random.nextLong();
        RandomColors4 colors = new RandomColors4();

        List<Boolean> a = shuffle( list( true, false ) );
        List<Boolean> b = shuffle( list( true, false ) );
        return new NumberLevel( list( target( topOne, colors.next(), scaledRepresentation( seed, false, a.index( 0 ) ) ),
                                      target( topOne, colors.next(), scaledRepresentation( seed, false, a.index( 1 ) ) ),

                                      //For the third target, scale it up so that it is shown as a non-reduced picture
                                      target( bottomOne, colors.next(), scaledRepresentation( seed, false, b.index( 0 ) ) ),
                                      target( bottomOne, colors.next(), scaledRepresentation( seed, false, b.index( 1 ) ) ) ) );
    }

    /**
     * Level 8:
     * --Same as level 6
     * --All 4 representations require simplifying
     */
    NumberLevel level8() {
        List<Fraction> expandable = expandable();
        List<Integer> wholes = list( 1, 2, 3 );
        RandomColors4 colors = new RandomColors4();
        final List<MixedFraction> targets = choose( 4, getMixedFractions( wholes, expandable ) );
        final long seed = random.nextLong();
        return new NumberLevel( list( shuffledTarget( targets.index( 0 ), colors.next(), scaledRepresentation( seed, true, true ) ),
                                      shuffledTarget( targets.index( 1 ), colors.next(), scaledRepresentation( seed, true, true ) ),

                                      //For the third target, scale it up so that it is shown as a non-reduced picture
                                      shuffledTarget( targets.index( 2 ), colors.next(), scaledRepresentation( seed, true, true ) ),
                                      shuffledTarget( targets.index( 3 ), colors.next(), scaledRepresentation( seed, true, true ) ) ) );
    }

    /*
     * Level 9:
     * -- All representations, random fill, and simplifying possible
     * -- Now representations within the targets can have different divisions, do this for 2 of the targets
     * --So, for instance if {1:3/4} is being represented by circles, the first circle could be divided in ¼’s and the second circle divided in 1/8’s,
     *   with pieces randomly distributed between the two circles.
     */
    NumberLevel level9() { return levelWithSomeScattering( list( true, true, false, false ) ); }

    /*Level 10:
    --Same as level 9, but now all 4 targets can have different internal divisions in representations.*/
    NumberLevel level10() { return levelWithSomeScattering( list( true, true, true, true ) ); }

    //Shared code for levels 9-10, see their descriptions
    NumberLevel levelWithSomeScattering( List<Boolean> scatterList ) {
        final List<Integer> wholes = list( 1, 2, 3 );
        List<Integer> denominators = rangeInclusive( 2, 8 );
        List<Integer> selectedDenominators = choose( 4, denominators );
        List<MixedFraction> mixedFractions = selectedDenominators.map( new F<Integer, MixedFraction>() {
            @Override public MixedFraction f( final Integer denominator ) {
                final MixedFraction mf = new MixedFraction( chooseOne( wholes ), chooseOne( rangeInclusive( 1, denominator - 1 ) ), denominator );
                return mf.withReducedFractionPart();
            }
        } );
        final RandomColors4 colors = new RandomColors4();
        final List<Boolean> scattered = shuffle( scatterList );
        final IntegerProperty index = new IntegerProperty( 0 );
        return new NumberLevel( mixedFractions.map( new F<MixedFraction, NumberTarget>() {
            @Override public NumberTarget f( final MixedFraction mixedFraction ) {
                final NumberTarget result = difficultTarget( mixedFraction, colors.next(), scattered.index( index.get() ) );
                index.increment();
                return result;
            }
        } ) );
    }

    private static final List<PatternMaker> typesForLevels9_10 = list( pie, horizontalBar );

    private NumberTarget difficultTarget( final MixedFraction mixedFraction, final Color next, final Boolean scattered ) {
        return scattered ?
               scatteredTarget( mixedFraction, next, chooseOne( typesForLevels9_10 ).random() ) :
               target( mixedFraction, next, chooseOne( typesForLevels9_10 ).random() );
    }

    private static boolean isSolvable( final NumberLevel level, final NumberTarget target ) {
        for ( int wholeIndex = 0; wholeIndex < level.numbers.length(); wholeIndex++ ) {
            for ( int numeratorIndex = 0; numeratorIndex < level.numbers.length(); numeratorIndex++ ) {
                for ( int denominatorIndex = 0; denominatorIndex < level.numbers.length(); denominatorIndex++ ) {
                    if ( wholeIndex != numeratorIndex && wholeIndex != denominatorIndex && numeratorIndex != denominatorIndex ) {
                        MixedFraction value = new MixedFraction( level.numbers.index( wholeIndex ), level.numbers.index( numeratorIndex ), level.numbers.index( denominatorIndex ) );
                        boolean equals = value.approxEquals( target.mixedFraction.toFraction() );
                        if ( equals ) { return true; }
                    }
                }
            }
        }
        return false;
    }

    //REVIEW doc
    private class ScaledRepresentation extends F<MixedFraction, FilledPattern> {
        private final long seed;
        private final boolean random;
        private final boolean reallyScaleIt;

        public ScaledRepresentation( final long seed, final boolean random, final boolean reallyScaleIt ) {
            this.seed = seed;
            this.random = random;
            this.reallyScaleIt = reallyScaleIt;
        }

        @Override public FilledPattern f( final MixedFraction mixedFraction ) {
            int d = mixedFraction.getFractionPart().denominator;
            List<Integer> scaleFactors = reallyScaleIt ? getScaleFactors( d ) : list( 1 );

            //Use the same random seed each time otherwise composite representations might have different shape types for each of its parts
            Integer scaleFactor = chooseOneWithSeed( seed, scaleFactors );
            final int denominator = d * scaleFactor;
            final PatternMaker patternMaker = chooseOneWithSeed( seed, matching( denominator ) );
            return ( random ? patternMaker.random() : patternMaker.sequential() ).f( mixedFraction.scaleNumeratorAndDenominator( scaleFactor ) );
        }

        @Override public boolean equals( final Object obj ) {
            if ( obj instanceof ScaledRepresentation ) {
                ScaledRepresentation sr = (ScaledRepresentation) obj;
                MixedFraction mf = mixedFraction( 1, fraction( 2, 3 ) );
                FilledPattern thisPattern = this.f( mf );
                FilledPattern thatPattern = sr.f( mf );
                return thisPattern.type.equals( thatPattern.type );
            }
            else {
                return false;
            }
        }
    }

    //REVIEW lots of output form this, but I have no idea how to interpret it, or whether the "test" has succeeded or failed.
    //Test main to make sure that levels are solvable
    public static void main( String[] args ) {
        for ( int i = 0; i < 1000; i++ ) {
            NumberLevel level = new MixedNumbersNumberLevelList().createLevel( 9 );

            //make sure each problem has a solution (forget dependencies)
            //The problem illuminated here was solved with NumberTarget.overflows
            for ( NumberTarget target : level.targets ) {
                boolean solvable = isSolvable( level, target );
                System.out.println( "target = " + target.mixedFraction + ", solvable = " + solvable );
            }
        }
    }
}