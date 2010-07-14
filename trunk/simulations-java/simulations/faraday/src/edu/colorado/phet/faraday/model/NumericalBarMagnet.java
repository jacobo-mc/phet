/* Copyright 2010, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.util.Vector2D;

/**
 * Model of a bar magnet that uses a grid of precomputed B-field values.
 * <p>
 * It was not feasible to implement a numerical model directly in Java, as it relies on double integrals.
 * So the model was implemented in MathCAD, and MathCAD was used to create a grid of B-field vectors.
 * That grid is stored in 2 files (one for Bx, one for By).
 * This simulation reads those files, and computes the B-field at a specified point using 
 * the grid and an interpolation algorithm.
 * <p>
 * The grid assumes that the magnet's center is at the origin, and the grid includes only 
 * the quadrant where x and y are both positive (lower-right quadrant in our coordinate system).
 * <p>
 * Our coordinate system has +x to the left, and +y down, with quadrants numbered like this: 
 * <code>
 * Q3 | Q2
 * -------
 * Q4 | Q1 
 * </code>
 * The grid files contain the B-field components for Q1, with values in column-major order.
 * (This means that the x coordinate changes more slowly than the y coordinate.)
 * x and y coordinates both start at 0.
 * <p>
 * After locating a B-field vector in Q1, here's how to map it to one of the other quadrants: 
 * Q2: reflect about the x axis, so multiply By by -1 
 * Q3: reflect through the origin, so no change
 * Q4: reflect about the x axis and reflect through the origin, so so multiply By by -1
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NumericalBarMagnet extends AbstractMagnet {

    private static final Dimension GRID_SPACING = new Dimension( 4, 4 ); // value used when generating data files in MathCAD
    private static final Dimension GRID_SIZE = new Dimension( 125, 50 ); // value used when generating data files in MathCAD
    private static final double GRID_MAGNET_STRENGTH = 1; // strength of the magnet used in MathCAD, in Gauss
    private static final String BX_RESOURCE_NAME = "bfield/Bx.csv"; // B-field vector x components, one quadrant (+x +y)
    private static final String BY_RESOURCE_NAME = "bfield/By.csv"; // B-field vector y components, one quadrant (+x +y)
    
    final double[][] bxArray, byArray;
    
    public NumericalBarMagnet() {
        super();
        GridReader reader = new GridReader();
        bxArray = reader.getBxArray();
        byArray = reader.getByArray();
    }

    @Override
    public Vector2D getStrength( Point2D p, Vector2D outputVector ) {

        // convert to a point that is relative to magnet's origin
        double xRelative = p.getX() - getLocation().getX();
        double yRelative = p.getY() - getLocation().getY();
        
        // find B-field by interpolating grid points
        double x = getBx( xRelative, yRelative );
        double y = getBy( xRelative, yRelative );
        Vector2D fieldVector = outputVector;
        if ( fieldVector == null ) {
            fieldVector = new Vector2D();
        }
        fieldVector.setXY( x, y );

        // scale based on magnet strength
        fieldVector.scale( getStrength() / GRID_MAGNET_STRENGTH ); 
        
        //TODO handle orientation, magnet may be rotated!
        
        return fieldVector;
    }
    
    //XXX ignore extra args, remove this from AbstractMagnet interface?
    @Override
    public Vector2D getStrength( Point2D p, Vector2D outputVector, double distanceExponent ) {
        return getStrength( p, outputVector );
    }

    //XXX ignore extra args and outside-of-plane semantics (see Unfuddle #424), remove this from AbstractMagnet interface?
    @Override
    public Vector2D getStrengthOutsidePlane( Point2D p, Vector2D outputVector, double distanceExponent ) {
        return getStrength( p, outputVector );
    }
    
    /*
     * Get Bx component for a point relative to the magnet's origin.
     * This component is identical in all 4 quadrants.
     * See class javadoc.
     */
    private double getBx( final double x, final double y ) {
        return interpolate( Math.abs( x ), Math.abs( y ), bxArray );
    }
    
    /*
     * Get By component for a point relative to the magnet's origin.
     * This component is the same in 2 quadrants, but must be reflected about the y axis for 2 quadrants.
     * See class javadoc.
     */
    private double getBy( final double x, final double y ) {
        double by = interpolate( Math.abs( x ), Math.abs( y ), byArray );
        if ( ( x > 0 && y < 0 ) || ( x < 0 && y > 0 ) ) {
            by *= -1; // reflect about the y axis
        }
        return by;
    }
    
    /*
     * Locates the 4 grid points that form a rectangle enclosing the specified point.
     * Performs a linear interpolation of the B-field at those 4 points.
     */
    private double interpolate( final double x, final double y, double[][] componentValues ) {
        if ( !( x >= 0 && y >= 0 ) ) {
            throw new IllegalArgumentException( "x and y must be positive" ); // ...because our grid is for that quadrant
        }
        double value = 0; // B-field outside the grid is zero
        if ( gridContains( x, y ) ) {
            int columnIndex = (int) ( x / GRID_SPACING.width );
            int rowIndex = (int) ( y / GRID_SPACING.height );
            //TODO need to interpolate here, instead of rounding to nearest point
            value = componentValues[columnIndex][rowIndex];
        }
        return value;
    }
    
    /*
     * Does the grid contain this point?
     */
    private boolean gridContains( double x, double y ) {
        boolean xInside = ( x >=0 && x <= ( GRID_SPACING.width * ( GRID_SIZE.width - 1 ) ) ); //XXX 0-496
        boolean yInside = ( y >=0 && y <= ( GRID_SPACING.height * ( GRID_SIZE.height - 1 ) ) ); //XXX 0-196
        return ( xInside && yInside );
    }

    /*
     * Reads the B-field grid files.
     * One file for each 2D vector component (Bx, By).
     * Files are in CSV (Comma Separated Value) format, with values in column-major order.
     * (This means that the x coordinate changes more slowly than the y coordinate.)
     */
    private static class GridReader {
        
        private static final String TOKEN_DELIMITER = ","; // CSV format
        
        private final double[][] bxArray, byArray;  // double[columns][rows]
        
        public GridReader() {
            bxArray = readGridComponent( BX_RESOURCE_NAME, GRID_SIZE );
            byArray = readGridComponent( BY_RESOURCE_NAME, GRID_SIZE );
        }
        
        public double[][] getBxArray() {
            return bxArray;
        }
        
        public double[][] getByArray() {
            return byArray;
        }
        
        /*
         * Reads one B-field component from the specified resource file.
         */
        private double[][] readGridComponent( String resourceName, Dimension size ) {
            double array[][] = new double[size.width][size.height];
            int count = 0;
            try {
                InputStream is = FaradayResources.getResourceLoader().getResourceAsStream( resourceName );
                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                String line = br.readLine();
                int row = 0;
                int column = 0;
                while ( line != null ) {
                    StringTokenizer stringTokenizer = new StringTokenizer( line, TOKEN_DELIMITER );
                    while ( stringTokenizer.hasMoreTokens() ) {
                        String token = stringTokenizer.nextToken();
                        array[row][column] = Double.parseDouble( token );
                        count++;
                        column++;
                        if ( column == size.height ) {
                            column = 0;
                            row++;
                        }
                    }
                    line = br.readLine();
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
                // If we can't read the grid files, this sim can't function, so bail.
                PhetOptionPane.showErrorDialog( PhetApplication.getInstance().getPhetFrame(), FaradayStrings.MESSAGE_CANNOT_FIND_BAR_MAGNET_FILES );
                System.exit( 1 );
            }
            int expectedCount = size.width * size.height;
            if ( count < expectedCount ) {
                throw new IllegalStateException( "fewer values than expected in " + resourceName + " count=" + count + "expectedCount=" + expectedCount );
            }
            return array;
        }
    }
}
