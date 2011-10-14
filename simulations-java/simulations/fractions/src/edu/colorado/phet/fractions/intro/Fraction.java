// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

/**
 * Immutable fraction object with denominator and numerator
 *
 * @author Sam Reid
 */
public class Fraction {
    public final int numerator;
    public final int denominator;

    //Reduces a fraction
    public Fraction( Integer num, Integer den ) {
        int value = num;
        if ( num > den ) { value = gcd( num, den ); }
        else if ( num < den ) { value = gcd( den, num ); }

        // set result based on common factor derived from gcd
        this.numerator = num / value;
        this.denominator = den / value;
    }

    public static int gcd( int a, int b ) {
        int factor;
        while ( b != 0 ) {
            factor = b;
            b = a % b;
            a = factor;
        }
        return a;
    }
}