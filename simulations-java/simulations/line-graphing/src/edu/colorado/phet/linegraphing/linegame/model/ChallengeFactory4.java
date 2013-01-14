// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=4, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory4 extends ChallengeFactory {

    public ChallengeFactory4() {
        super();
    }

    /**
     * Creates challenges for this game level.
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // positive slopes
        final ArrayList<Fraction> positiveSlopes = new ArrayList<Fraction>() {{
            // positive fractions
            add( new Fraction( 1, 4 ) );
            add( new Fraction( 1, 5 ) );
            add( new Fraction( 1, 6 ) );
            add( new Fraction( 1, 7 ) );
            add( new Fraction( 2, 5 ) );
            add( new Fraction( 3, 5 ) );
            add( new Fraction( 2, 7 ) );
            add( new Fraction( 3, 7 ) );
            add( new Fraction( 4, 7 ) );
            add( new Fraction( 5, 2 ) );
            add( new Fraction( 3, 2 ) );
            add( new Fraction( 7, 2 ) );
            add( new Fraction( 7, 3 ) );
            add( new Fraction( 7, 4 ) );
        }};

        // for slope manipulation challenges, 1 slope must come from each list
        ArrayList<ArrayList<Fraction>> slopeLists = new ArrayList<ArrayList<Fraction>>() {{
            add( new ArrayList<Fraction>() {{
                // positive and negative integers
                add( new Fraction( 1, 1 ) );
                add( new Fraction( 2, 1 ) );
                add( new Fraction( 3, 1 ) );
                add( new Fraction( 4, 1 ) );
                add( new Fraction( 5, 1 ) );
                add( new Fraction( -1, 1 ) );
                add( new Fraction( -2, 1 ) );
                add( new Fraction( -3, 1 ) );
                add( new Fraction( -4, 1 ) );
                add( new Fraction( -5, 1 ) );
            }} );
            add( positiveSlopes );
            add( new ArrayList<Fraction>() {{
                // negative fractions
                add( new Fraction( -1, 2 ) );
                add( new Fraction( -1, 3 ) );
                add( new Fraction( -1, 4 ) );
                add( new Fraction( -1, 5 ) );
                add( new Fraction( -2, 3 ) );
                add( new Fraction( -3, 4 ) );
                add( new Fraction( -2, 5 ) );
                add( new Fraction( -3, 5 ) );
                add( new Fraction( -4, 5 ) );
                add( new Fraction( -3, 2 ) );
                add( new Fraction( -4, 3 ) );
                add( new Fraction( -5, 2 ) );
                add( new Fraction( -5, 3 ) );
                add( new Fraction( -5, 4 ) );
            }} );
        }};
        ArrayList<Integer> slopeBinIndices = rangeToList( new IntegerRange( 0, slopeLists.size() - 1 ) );

        // for y-intercept manipulation challenges, one must be positive, one negative
        final IntegerRange yInterceptRange = new IntegerRange( -10, 10 );
        ArrayList<ArrayList<Integer>> yInterceptLists = new ArrayList<ArrayList<Integer>>() {{
            assert( yInterceptRange.getMin() < 0 && yInterceptRange.getMax() > 0 );
            add( rangeToList( new IntegerRange( yInterceptRange.getMin(), -1 ) ) );
            add( rangeToList( new IntegerRange( 1, yInterceptRange.getMax() ) ) );
        }};
        ArrayList<Integer> yInterceptListIndices = rangeToList( new IntegerRange( 0, yInterceptLists.size() - 1 ) );

        // equation form for 3rd challenge of each type
        ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
            add( EquationForm.SLOPE_INTERCEPT );
            add( EquationForm.POINT_SLOPE );
        }};

        // random choosers
        final RandomChooser<Fraction> fractionChooser = new RandomChooser<Fraction>();
        final RandomChooser<Integer> integerChooser = new RandomChooser<Integer>();
        final RandomChooser<EquationForm> equationFormChooser = new RandomChooser<EquationForm>();

        // MTE, SI, slope & intercept
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // first required y-intercept
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new MTE_Challenge( "1 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // MTE, PS, point & slope
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeBinIndices ); // first required slope
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new MTE_Challenge( "1 of 3 required slopes",
                                               line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        // MTE, SI or PS (random choice)
        {
            if ( equationFormChooser.choose( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // MTE, SI, slope & intercept
                Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
                int yIntercept = integerChooser.chooseFromLists( yInterceptLists ); // unique y-intercept
                Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
                challenges.add( new MTE_Challenge( "random choice of slope-intercept",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                // MTE, PS, point & slope
                Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeBinIndices ); // second required slope, unique
                Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
                Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
                challenges.add( new MTE_Challenge( "2 of 2 required slopes, random choice of point-slope",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // GTL, SI, slope & intercept
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists ); // unique slope
            int yIntercept = integerChooser.chooseFromLists( yInterceptLists, yInterceptListIndices ); // second required y-intercept, unique
            Line line = Line.createSlopeIntercept( slope.numerator, slope.denominator, yIntercept );
            challenges.add( new GTL_Challenge( "2 of 2 required y-intercepts",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // GTL, PS, point & slope
        {
            Fraction slope = fractionChooser.chooseFromLists( slopeLists, slopeBinIndices ); // third required slope, unique
            Point2D point = pickPointForSlope( slope, xRange, yRange ); // random point, not necessarily unique
            Line line = Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator );
            challenges.add( new GTL_Challenge( "3 of 3 required slopes",
                                               line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
        }

        /*
         * GTL, SI or PS (random choice), 2 points.
         * Choose y-intercept or point such that (x2,y2) is off the graph, so that user is forced to invert the slope.
         */
        {
            Fraction slope = fractionChooser.choose( positiveSlopes ); // unique positive slope
            Point2D point = pickPointForInvertedSlope( slope, xRange, yRange ); // random point, not necessarily unique
            if ( equationFormChooser.choose( equationForms ) == EquationForm.SLOPE_INTERCEPT ) {
                // GTL, SI, 2 points
                challenges.add( new GTL_Challenge( "slope-intercept because MTE uses point-slope, force slope inversion",
                                                   Line.createSlopeIntercept( slope.numerator, slope.denominator, point.getY() ),
                                                   EquationForm.SLOPE_INTERCEPT, ManipulationMode.TWO_POINTS, xRange, yRange ) );
            }
            else {
                // GTL, PS, 2 points
                challenges.add( new GTL_Challenge( "point-slope because MTE uses slope-intercept, force slope inversion",
                                                   Line.createPointSlope( point.getX(), point.getY(), slope.numerator, slope.denominator ),
                                                   EquationForm.POINT_SLOPE, ManipulationMode.TWO_POINTS, xRange, yRange ) );
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
