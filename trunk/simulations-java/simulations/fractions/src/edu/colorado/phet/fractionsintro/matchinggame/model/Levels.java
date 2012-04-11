// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.F2;
import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Grid;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.PlusSigns;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Pyramid;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.HorizontalBarsNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PieNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.VerticalBarsNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.matchingGameFraction;
import static edu.colorado.phet.fractionsintro.common.view.Colors.LIGHT_BLUE;
import static edu.colorado.phet.fractionsintro.common.view.Colors.LIGHT_GREEN;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.MoveToCell;
import static edu.colorado.phet.fractionsintro.matchinggame.model.RepresentationType.*;
import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.randomFill;
import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.sequentialFill;
import static fj.data.List.*;

/**
 * Levels for the matching game, declared as functions below that return the MovableFraction instances (and hence their representations) in a level.
 *
 * @author Sam Reid
 */
public class Levels {

    private static final F2<Pattern, Integer, FilledPattern> SEQUENTIAL = new F2<Pattern, Integer, FilledPattern>() {
        @Override public FilledPattern f( final Pattern pattern, final Integer numFilled ) {
            return sequentialFill( pattern, numFilled );
        }
    };
    private static final F2<Pattern, Integer, FilledPattern> RANDOM = new F2<Pattern, Integer, FilledPattern>() {
        @Override public FilledPattern f( final Pattern pattern, final Integer numFilled ) {
            return randomFill( pattern, numFilled );
        }
    };

    public static F<Fraction, ArrayList<RepresentationType>> representationFunction( final List<RepresentationType> r ) {
        return new F<Fraction, ArrayList<RepresentationType>>() {
            @Override public ArrayList<RepresentationType> f( Fraction fraction ) {
                return createRepresentations( fraction, r );
            }
        };
    }

    private static final boolean debug = false;

    //Singleton, use Levels instance
    private Levels() {
    }

    public static final F<Fraction, Boolean> all = new F<Fraction, Boolean>() {
        @Override public Boolean f( final Fraction fraction ) {
            return true;
        }
    };

    final static RepresentationType numeric = singleRepresentation( "numeric", all,
                                                                    new F<Fraction, PNode>() {
                                                                        @Override public PNode f( Fraction f ) {
                                                                            return new FractionNode( f, 0.3 );
                                                                        }
                                                                    } );
    final RepresentationType horizontalBars = twoComposites( "horizontal bars", all,
                                                             new F<Fraction, PNode>() {
                                                                 @Override public PNode f( Fraction f ) {
                                                                     return new HorizontalBarsNode( f, 0.9, LIGHT_GREEN );
                                                                 }
                                                             },
                                                             new F<Fraction, PNode>() {
                                                                 @Override public PNode f( Fraction f ) {
                                                                     return new HorizontalBarsNode( f, 0.9, LIGHT_BLUE );
                                                                 }
                                                             }
    );
    final RepresentationType verticalBars = twoComposites( "vertical bars", all,
                                                           new F<Fraction, PNode>() {
                                                               @Override public PNode f( Fraction f ) {
                                                                   return new VerticalBarsNode( f, 0.9, LIGHT_GREEN );
                                                               }
                                                           },
                                                           new F<Fraction, PNode>() {
                                                               @Override public PNode f( Fraction f ) {
                                                                   return new VerticalBarsNode( f, 0.9, LIGHT_BLUE );
                                                               }
                                                           }
    );
    final RepresentationType pies = twoComposites( "pies", all,
                                                   new F<Fraction, PNode>() {
                                                       @Override public PNode f( Fraction f ) {
                                                           return myPieNode( f, LIGHT_GREEN );
                                                       }
                                                   },
                                                   new F<Fraction, PNode>() {
                                                       @Override public PNode f( Fraction f ) {
                                                           return myPieNode( f, LIGHT_BLUE );
                                                       }
                                                   }
    );
    final RepresentationType twoPlusses = makePlusses( 2 );
    final RepresentationType threePlusses = makePlusses( 3 );
    final RepresentationType fourPlusses = makePlusses( 4 );
    final RepresentationType fivePlusses = makePlusses( 5 );
    final RepresentationType sixPlusses = makePlusses( 6 );

    private RepresentationType makePlusses( final int numPlusses ) {
        return createPatterns( numPlusses + " plusses", numPlusses, 10, new F<Integer, Pattern>() {
            @Override public Pattern f( final Integer integer ) {
                return new PlusSigns( numPlusses );
            }
        }, SEQUENTIAL );
    }

    final RepresentationType fourGrid = createPatterns( "four grid", 4, 100, new F<Integer, Pattern>() {
        @Override public Pattern f( final Integer length ) {
            return new Grid( 2 );
        }
    }, SEQUENTIAL );
    final RepresentationType nineGrid = createPatterns( "nine grid", 9, 50, new F<Integer, Pattern>() {
        @Override public Pattern f( final Integer length ) {
            return new Grid( 3 );
        }
    }, SEQUENTIAL );

    //TODO: Could factor out other patterns using this style
    final RepresentationType onePyramid = createPatterns( "one pyramid", 1, 100, new F<Integer, Pattern>() {
        @Override public Pattern f( final Integer length ) {
            return Pyramid.single( length );
        }
    }, SEQUENTIAL );
    final RepresentationType fourPyramid = createPatterns( "four pyramid", 4, 50, new F<Integer, Pattern>() {
        @Override public Pattern f( final Integer length ) {
            return Pyramid.four( length );
        }
    }, SEQUENTIAL );
    final RepresentationType ninePyramid = createPatterns( "nine pyramid", 9, 30, new F<Integer, Pattern>() {
        @Override public Pattern f( final Integer length ) {
            return Pyramid.nine( length );
        }
    }, SEQUENTIAL );

    private RepresentationType createPatterns( String name, final int max, final int length, final F<Integer, Pattern> pattern, final F2<Pattern, Integer, FilledPattern> fill ) {
        return twoComposites( name, new F<Fraction, Boolean>() {
                                  @Override public Boolean f( final Fraction fraction ) {
                                      return fraction.denominator == max;
                                  }
                              },
                              new F<Fraction, PNode>() {
                                  @Override public PNode f( Fraction f ) {
                                      return new PatternNode( fill.f( pattern.f( length ), f.numerator ), LIGHT_GREEN );
                                  }
                              },
                              new F<Fraction, PNode>() {
                                  @Override public PNode f( Fraction f ) {
                                      return new PatternNode( fill.f( pattern.f( length ), f.numerator ), LIGHT_BLUE );
                                  }
                              }
        );
    }

    public static Levels Levels = new Levels();

    @SuppressWarnings("unchecked")
    final List<RepresentationType> levelOneRepresentations = list( numeric, horizontalBars, verticalBars, pies );
    final List<RepresentationType> allRepresentations = levelOneRepresentations.append( list( twoPlusses, threePlusses, fourPlusses, fivePlusses, sixPlusses, fourGrid, nineGrid, onePyramid, fourPyramid, ninePyramid ) );

    //Convenience Wrapper to create PieNodes
    private PNode myPieNode( final Fraction f, final Color color ) {
        return new PieNode( new Property<ContainerSet>( new ContainerSet( f.denominator, new Container[] { new Container( f.denominator, range( 0, f.numerator ) ) } ) ), color );
    }

    public static final Random random = new Random();

    private ResultPair createPair( ArrayList<Fraction> fractions, ArrayList<Cell> cells, F<Fraction, ArrayList<RepresentationType>> representationPool, final List<ResultPair> alreadySelected ) {

        //choose a fraction
        final Fraction fraction = fractions.get( random.nextInt( fractions.size() ) );

        //Sampling is without replacement, so remove the old fraction.
        fractions.remove( fraction );
        ArrayList<RepresentationType> representations = representationPool.f( fraction );

        //Don't allow duplicate representations for fractions
        //Find all representations for the given fraction
        List<Result> previouslySelected = alreadySelected.map( new F<ResultPair, Result>() {
            @Override public Result f( final ResultPair r ) {
                return r.a;
            }
        } ).append( alreadySelected.map( new F<ResultPair, Result>() {
            @Override public Result f( final ResultPair r ) {
                return r.b;
            }
        } ) );
        List<Result> same = previouslySelected.filter( new F<Result, Boolean>() {
            @Override public Boolean f( final Result r ) {
                return r.fraction.fraction().equals( fraction ) && numeric.contains( r.representation );
            }
        } );
        representations.removeAll( same.map( new F<Result, F<Fraction, PNode>>() {
            @Override public F<Fraction, PNode> f( final Result result ) {
                return result.representation;
            }
        } ).toCollection() );

        //create 2 representation for it
        RepresentationType representationSetA = representations.get( random.nextInt( representations.size() ) );
        final Cell cellA = cells.get( random.nextInt( cells.size() ) );
        final F<Fraction, PNode> representationA = representationSetA.chooseOne();
        MovableFraction fractionA = fraction( fraction, cellA, representationA, createUserComponent( fraction, representationSetA ) );

        //Don't use the same representation for the 2nd one, and put it in a new cell
        while ( representations.contains( representationSetA ) ) {
            representations.remove( representationSetA );
        }
        cells.remove( cellA );

        final Cell cellB = cells.get( random.nextInt( cells.size() ) );
        RepresentationType representationSetB = representations.get( random.nextInt( representations.size() ) );
        final F<Fraction, PNode> representationB = representationSetB.chooseOne();
        MovableFraction fractionB = fraction( fraction, cellB, representationB, createUserComponent( fraction, representationSetB ) );

        cells.remove( cellB );

        return new ResultPair( new Result( fractionA, representationSetA, representationA ), new Result( fractionB, representationSetB, representationB ) );
    }

    private IUserComponent createUserComponent( final Fraction fraction, final RepresentationType representationType ) {
        return chain( chain( matchingGameFraction, fraction.numerator + "/" + fraction.denominator ), representationType.name );
    }

    private static MovableFraction fraction( Fraction fraction, Cell cell, final F<Fraction, PNode> node, IUserComponent userComponent ) {
        return fraction( fraction.numerator, fraction.denominator, cell, node, userComponent );
    }

    //Create a MovableFraction for the given fraction at the specified cell
    private static MovableFraction fraction( int numerator, int denominator, Cell cell, final F<Fraction, PNode> node, IUserComponent userComponent ) {

        //Cache nodes as images to improve performance
        //TODO: could this put the same node at 2 places in the scene graph?  If so, what problems would that cause?
        return new MovableFraction( new Vector2D( cell.rectangle.getCenter() ), numerator, denominator, false, cell, 1.0,
                                    new Cache<Fraction, PNode>( new F<Fraction, PNode>() {
                                        @Override public PNode f( final Fraction fraction ) {
                                            return new PComposite() {{ addChild( node.f( fraction ) ); }};
                                        }
                                    } ),
                                    MoveToCell( cell ), false, userComponent );
    }

    private List<MovableFraction> createLevel( F<Fraction, ArrayList<RepresentationType>> representations, List<Cell> _cells, Fraction[] a ) {
        assert _cells.length() % 2 == 0;
        ArrayList<Fraction> fractions = new ArrayList<Fraction>( Arrays.asList( a ) );

        ArrayList<MovableFraction> list = new ArrayList<MovableFraction>();

        //Use mutable collection so it can be removed from for drawing without replacement
        ArrayList<Cell> cells = new ArrayList<Cell>( _cells.toCollection() );

        //Keep track of all so that we don't replicate representations
        ArrayList<ResultPair> all = new ArrayList<ResultPair>();
        while ( list.size() < _cells.length() ) {
            ResultPair pair = createPair( fractions, cells, representations, iterableList( all ) );
            all.add( pair );
            list.add( pair.a.fraction );
            list.add( pair.b.fraction );
        }

        //make sure no representation type used twice
        if ( debug ) {
            makeSureNoRepresentationTypeUsedTwiceForTheSameFraction( iterableList( all ) );
        }

        return iterableList( list );
    }

    private F<Fraction, ArrayList<RepresentationType>> getRepresentationPool( final int level ) {
        return level == 1 ? representationFunction( levelOneRepresentations ) : representationFunction( allRepresentations );
    }

    private void makeSureNoRepresentationTypeUsedTwiceForTheSameFraction( final List<ResultPair> all ) {
        List<Fraction> fractions = all.bind( new F<ResultPair, List<Fraction>>() {
            @Override public List<Fraction> f( final ResultPair resultPair ) {
                return single( resultPair.a.fraction.fraction() ).snoc( resultPair.b.fraction.fraction() );
            }
        } );
        for ( final Fraction fraction : fractions ) {
            List<Result> allResultsForFraction = all.bind( new F<ResultPair, List<Result>>() {
                @Override public List<Result> f( final ResultPair resultPair ) {
                    return single( resultPair.a ).snoc( resultPair.b );
                }
            } ).filter( new F<Result, Boolean>() {
                @Override public Boolean f( final Result result ) {
                    return result.fraction.fraction().equals( fraction );
                }
            } );
            List<RepresentationType> types = allResultsForFraction.map( new F<Result, RepresentationType>() {
                @Override public RepresentationType f( final Result result ) {
                    return result.representationType;
                }
            } );
            List<RepresentationType> unique = types.nub();
            System.out.println( "types.length() = " + types.length() + ", unique.length = " + unique.length() );
            if ( types.length() != unique.length() ) {
                System.out.println( "unique = " + unique.map( _name ) + ", list = " + types.map( _name ) );
            }
            assert types.length() == unique.length();
        }
    }

    public static final @Data class Result {
        public final MovableFraction fraction;
        public final RepresentationType representationType;
        public final F<Fraction, PNode> representation;
    }

    public static final @Data class ResultPair {
        public final Result a;
        public final Result b;
    }

    //Create the default representations that will be used in all levels
    private static ArrayList<RepresentationType> createRepresentations( final Fraction fraction, List<RepresentationType> allRepresentations ) {

        //Find the representations that could be used to show the given fraction
        List<RepresentationType> applicableRepresentations = allRepresentations.filter( new F<RepresentationType, Boolean>() {
            @Override public Boolean f( final RepresentationType r ) {
                return r.appliesTo.f( fraction );
            }
        } );

        //Count the non-numeric representations
        int nonNumeric = applicableRepresentations.filter( new F<RepresentationType, Boolean>() {
            @Override public Boolean f( final RepresentationType representationSet ) {
                return representationSet != numeric;
            }
        } ).length();

        //Count the numeric representations
        int n = applicableRepresentations.filter( new F<RepresentationType, Boolean>() {
            @Override public Boolean f( final RepresentationType representationSet ) {
                return representationSet == numeric;
            }
        } ).length();

        //Add one "numerical" representation for each graphical one, so that on average there will be about 50% numerical
        int numToAdd = nonNumeric - n;
        if ( numToAdd > 0 ) {
            applicableRepresentations = applicableRepresentations.cons( numeric );
        }
        return new ArrayList<RepresentationType>( applicableRepresentations.toCollection() );
    }

    /**
     * Level 1
     * No mixed numbers
     * Only “exact” matches will be present.  So for instance if there is a 3/6  and a pie with 6 divisions and 3 shaded slices, there will not be a ½  present .  In other words, the numerical representation on this level will exactly match the virtual manipulative.
     * Only numbers/representations  ≦ 1 possible on this level
     * “Easy” shapes on this level (not some of the more abstract representations)
     */
    final Fraction[] level1Fractions = {
            new Fraction( 1, 3 ),
            new Fraction( 2, 3 ),
            new Fraction( 1, 4 ),
            new Fraction( 3, 4 ),
            new Fraction( 1, 2 ),
            new Fraction( 1, 1 ) };
    public F<List<Cell>, List<MovableFraction>> Level1 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) { return createLevel( getRepresentationPool( 1 ), cells, level1Fractions ); }
    };

    /**
     * Level 2
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    final Fraction[] level2Fractions = {
            new Fraction( 1, 2 ),
            new Fraction( 2, 4 ),
            new Fraction( 3, 4 ),
            new Fraction( 1, 3 ),
            new Fraction( 2, 3 ),
            new Fraction( 3, 6 ),
            new Fraction( 2, 6 ) };
    public F<List<Cell>, List<MovableFraction>> Level2 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {
            return createLevel( getRepresentationPool( 2 ), cells, level2Fractions );
        }
    };

    /**
     * Level 3:
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    final Fraction[] level3Fractions = {
            new Fraction( 3, 2 ),
            new Fraction( 4, 3 ),
            new Fraction( 6, 3 ),
            new Fraction( 4, 2 ),
            new Fraction( 7, 6 ),
            new Fraction( 4, 5 ),
            new Fraction( 7, 4 ),
            new Fraction( 5, 4 ),
            new Fraction( 6, 4 ),
            new Fraction( 3, 4 ) };
    public F<List<Cell>, List<MovableFraction>> Level3 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {
            return createLevel( getRepresentationPool( 3 ), cells, level3Fractions );
        }
    };

    /**
     * Level 4:
     * All representations possible as well as complicated mixed/improper numbers
     */
    final Fraction[] level4Fractions = {
            new Fraction( 13, 7 ),
            new Fraction( 13, 7 ),
            new Fraction( 14, 8 ),
            new Fraction( 9, 5 ),
            new Fraction( 6, 3 ),
            new Fraction( 9, 8 ),
            new Fraction( 8, 9 ),
            new Fraction( 6, 9 ),
            new Fraction( 4, 9 ),
            new Fraction( 3, 9 ),
            new Fraction( 2, 9 ),
            new Fraction( 9, 7 ) };
    public F<List<Cell>, List<MovableFraction>> Level4 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {
            return createLevel( getRepresentationPool( 4 ), cells, level4Fractions );
        }
    };

    public F<List<Cell>, List<MovableFraction>> get( int level ) {
        return level == 1 ? Level1 :
               level == 2 ? Level2 :
               level == 3 ? Level3 :
               level == 4 ? Level4 :
               Level4;
    }
}