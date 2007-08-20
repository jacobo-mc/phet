package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.FireHandler;
import edu.colorado.phet.common_cck.model.ModelElement;

import java.text.DecimalFormat;
import java.util.*;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 4:01:09 PM
 */
public class ConstantDensityPropagator implements ModelElement {
    private ParticleSet particleSet;
    private Circuit circuit;
    private double speedScale = .01;
    private double MIN_CURRENT = Math.pow( 10, -10 );
    private double MAX_STEP = CCKModel.ELECTRON_DX * .43;
    private int numEqualize = 2;
    private double scale;
    private SmoothData smoothData = new SmoothData( 30 );
    private double timeScalingPercentValue;
    private String percent = "100";
    private DecimalFormat decimalFormat = new DecimalFormat( "##" );

    public ConstantDensityPropagator( ParticleSet particleSet, Circuit circuit ) {
        this.particleSet = particleSet;
        this.circuit = circuit;
    }

    public double getTimeScalingPercentValue() {
        return timeScalingPercentValue;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void stepInTime( double dt ) {
        cap = 0;
        double maxCurrent = getMaxCurrent();
        //        System.out.println( "maxCurrent = " + maxCurrent );
        double maxVelocity = maxCurrent * speedScale;
        double maxStep = maxVelocity * dt;
        if( maxStep >= MAX_STEP ) {
            scale = MAX_STEP / maxStep;
        }
        else {
            scale = 1;
        }
        smoothData.addData( scale * 100 );
        this.timeScalingPercentValue = smoothData.getAverage();

        this.percent = decimalFormat.format( timeScalingPercentValue );
        if( percent.equals( "0" ) ) {
            percent = "1";
        }
        //todo add test for change before notify
        notifyListeners();
        if( timeScalingPercentValue < 1 ) {
        }
        for( int i = 0; i < particleSet.numParticles(); i++ ) {
            Electron e = particleSet.particleAt( i );
            propagate( e, dt );
        }
        //maybe this should be done in random order, otherwise we may get artefacts.

        for( int i = 0; i < numEqualize; i++ ) {
            equalize( dt );
        }
        if( cap != 0 ) {
            System.out.println( "cap = " + cap );
        }
    }

    private ArrayList listeners = new ArrayList();

    public String getPercentString() {
        return percent;
    }

    public static interface Listener {
        void timeScaleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.timeScaleChanged();
        }
    }

    private double getMaxCurrent() {
        double max = 0;
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            double current = circuit.branchAt( i ).getCurrent();
            max = Math.max( max, Math.abs( current ) );
        }
        return max;
    }

    private void equalize( double dt ) {
        ArrayList indices = new ArrayList();
        for( int i = 0; i < particleSet.numParticles(); i++ ) {
            indices.add( new Integer( i ) );
        }
        Collections.shuffle( indices );
        for( int i = 0; i < particleSet.numParticles(); i++ ) {
            Integer index = (Integer)indices.get( i );
            int ind = index.intValue();
            Electron e = particleSet.particleAt( ind );
            equalize( e, dt );
        }
    }

    static double highestSoFar = 0;

    private void equalize( Electron e, double dt ) {
        //if it has a lower and upper neighbor, try to get the distance to each to be half of
        //ELECTRON_DX
        Electron upper = particleSet.getUpperNeighborInBranch( e );
        Electron lower = particleSet.getLowerNeighborInBranch( e );
        if( upper == null || lower == null ) {
            return;
        }
        double sep = upper.getDistAlongWire() - lower.getDistAlongWire();
        double myloc = e.getDistAlongWire();
        double midpoint = lower.getDistAlongWire() + sep / 2;
        //move a bit toward the midpoint.
        //        double correctionSpeed = .01;//gives a factor of 100 off the correct answer ^ish.
        //        double correctionSpeed = .2;

        double dest = midpoint;
        double distMoving = Math.abs( dest - myloc );
        double vec = dest - myloc;
        boolean sameDirAsCurrent = vec > 0 && e.getBranch().getCurrent() > 0;
        double correctionSpeed = .055 / numEqualize;
        if( !sameDirAsCurrent ) {
            correctionSpeed = .01 / numEqualize;
        }
        double maxDX = Math.abs( correctionSpeed * dt );

        if( distMoving > highestSoFar ) {//For debugging.
            //            System.out.println( "highestSoFar = " + highestSoFar );
            highestSoFar = distMoving;
        }

        if( distMoving > maxDX ) {
            //move in the appropriate direction maxDX
            if( dest < myloc ) {
                dest = myloc - maxDX;
            }
            else if( dest > myloc ) {
                dest = myloc + maxDX;
            }
        }
        //        double vec = dest - myloc;
        ////        double newDist = Math.abs( dest - myloc );
        ////        System.out.println( "maxDX = " + maxDX + ", distMoving=" + distMoving + ", newDist=" + newDist );
        //        boolean sameDirAsCurrent = vec > 0 && e.getBranch().getCurrent() > 0;
        //        if( sameDirAsCurrent ) {
        if( dest >= 0 && dest <= e.getBranch().getLength() ) {
            e.setDistAlongWire( dest );
        }

        //        }
    }

    class CircuitLocation {
        Branch branch;
        double x;

        public CircuitLocation( Branch branch, double x ) {
            if( branch.containsScalarLocation( x ) ) {
                this.branch = branch;
                this.x = x;
            }
            else {
                throw new RuntimeException( "No such location in branch length=" + branch.getLength() + ", x=" + x );
            }
        }

        public Branch getBranch() {
            return branch;
        }

        public double getX() {
            return x;
        }

    }

    private void propagate( Electron e, double dt ) {
        //        if (isInFireLoop(e.getBranch())){
        //            return;
        //        }
        double x = e.getDistAlongWire();
        if( Double.isNaN( x ) ) {
            //TODO fix this
            //            throw new RuntimeException( "X was nan.");
            return;
        }
        double current = e.getBranch().getCurrent();

        if( current == 0 || Math.abs( current ) < MIN_CURRENT ) {
            return;
        }

        //        if( Math.abs( current ) > MAX_CURRENT ) {
        //            //            System.out.println( "current = " + current + ", max current exceeded" );
        //            //            return;
        //            current = MathUtil.getSign( current ) * MAX_CURRENT;
        //        }
        double speed = current * speedScale;
        double dx = speed * dt;
        dx *= scale;
        //        System.out.println( "dx = " + dx );

        //        dx = cap( dx, MAX_STEP );
        double newX = x + dx;
        Branch branch = e.getBranch();
        if( branch.containsScalarLocation( newX ) ) {
            e.setDistAlongWire( newX );
        }
        else {
            //need a new branch.
            double overshoot = 0;
            boolean under = false;
            if( newX < 0 ) {
                overshoot = -newX;
                under = true;
            }
            else {
                overshoot = Math.abs( branch.getLength() - newX );
                under = false;
            }
            if( Double.isNaN( overshoot ) ) {  //never happens
                throw new RuntimeException( "Overshoot is NaN" );
            }
            //            System.out.println( "overshoot = " + overshoot+", under="+under );
            if( overshoot < 0 ) { //never happens.
                throw new RuntimeException( "Overshoot is <0" );
            }
            CircuitLocation[] loc = getLocations( e, dt, overshoot, under );
            if( loc.length == 0 ) {
                //                System.out.println( "No outgoing wires for current=" + current );
                //                new KirkhoffSolver().apply( circuit );
                //                RuntimeException re = new RuntimeException( "No outgoing wires, current=" + current );
                ////                re.printStackTrace();
                //                StackTraceElement[] se = re.getStackTrace();
                //                for( int i = 0; i < 5; i++ ) {
                //                    StackTraceElement stackTraceElement = se[i];
                //                    System.err.println( stackTraceElement );
                //                }
                //                JOptionPane.showMessageDialog( null, "No outgoing wires, current=" + current );
                return;
            }
            //choose the branch with the furthest away electron
            CircuitLocation chosen = chooseDestinationBranch( loc );
            e.setLocation( chosen.getBranch(), Math.abs( chosen.getX() ) );
        }
    }

    static int cap = 0;

//    private double cap( double value, double max_step ) {
//        if( value > 0 ) {
//            if( value > max_step ) {
//                //                System.out.println( "CAP. value = " + value );
//                cap++;
//                return max_step;
//            }
//            else {
//                return value;
//            }
//        }
//        else {
//            if( value < -max_step ) {
//                //                System.out.println( "CAP: value = " + value );
//                cap++;
//                return -max_step;
//            }
//            else {
//                return value;
//            }
//        }
//    }
    //
    //    private boolean isInFireLoop( Branch branch ) {
    //        KirkhoffSolver.MatrixTable mt = new KirkhoffSolver.MatrixTable( circuit );
    //        Path[] paths = mt.getLoops();
    //        return false;
    //    }

//    private CircuitLocation chooseDestinationBranchOrig( CircuitLocation[] loc ) {
//        if( loc.length == 1 ) {
//            return loc[0];
//        }
//        CircuitLocation bestYet = loc[0];
//        double bestValue = particleSet.distanceToClosestElectron( bestYet.getBranch(), bestYet.getX() );
//
//        //        bestYet.distanceToClosestElectron( particleSet );
//        //        System.out.println( "bestValue = " + bestValue );
//        for( int i = 1; i < loc.length; i++ ) {
//            CircuitLocation circuitLocation = loc[i];
//            double distToElectron = particleSet.distanceToClosestElectron( circuitLocation.getBranch(), circuitLocation.getX() );
//            //            circuitLocation.distanceToClosestElectron( particleSet );
//            //            System.out.println( "distToElectron = " + distToElectron );
//            if( distToElectron > bestValue ) {
//                bestYet = circuitLocation;
//                bestValue = distToElectron;
//                //                System.out.println( "NEWbestValue = " + bestValue );
//            }
//        }
//        return bestYet;
//    }

    class ValueMap {
        Hashtable hashtable = new Hashtable();

        public ValueMap() {

        }

        public void put( Object object, double value ) {
            hashtable.put( object, new Double( value ) );
        }

//        public Object argMax() {
//            List list = new ArrayList( hashtable.keySet() );
//            Collections.sort( list, new Comparator() {
//                public int compare( Object o1, Object o2 ) {
//                    double k1 = get( o1 );
//                    double k2 = get( o2 );
//                    return Double.compare( k1, k2 );
//                }
//            } );
//            Object last = list.get( list.size() - 1 );
//            return last;
//        }

        public double get( Object object ) {
            Double val = (Double)hashtable.get( object );
            return val.doubleValue();
        }

        public Object argMin() {
            List list = new ArrayList( hashtable.keySet() );
            Collections.sort( list, new Comparator() {
                public int compare( Object o1, Object o2 ) {
                    double k1 = get( o1 );
                    double k2 = get( o2 );
                    return Double.compare( k1, k2 );
                }
            } );
            return list.get( 0 );
        }
    }

    private CircuitLocation chooseDestinationBranch( CircuitLocation[] loc ) {
        ValueMap vm = new ValueMap();
        for( int i = 0; i < loc.length; i++ ) {
            CircuitLocation circuitLocation = loc[i];
            vm.put( circuitLocation, getDensity( circuitLocation ) );
        }
        return (CircuitLocation)vm.argMin();
    }

    private double getDensity( CircuitLocation circuitLocation ) {
        Branch branch = circuitLocation.getBranch();
        return particleSet.getDensity( branch );
    }

    private CircuitLocation[] getLocations( Electron e, double dt, double overshoot, boolean under ) {
        Branch branch = e.getBranch();
        Junction jroot = null;
        if( under ) {
            jroot = branch.getStartJunction();
        }
        else {
            jroot = branch.getEndJunction();
        }
        Branch[] adj = circuit.getAdjacentBranches( jroot );
        ArrayList all = new ArrayList();
        //keep only those with outgoing current.
        for( int i = 0; i < adj.length; i++ ) {
            Branch neighbor = adj[i];
            double current = neighbor.getCurrent();
            if( current > FireHandler.FIRE_CURRENT ) {
                current = FireHandler.FIRE_CURRENT;
            }
            else if( current < -FireHandler.FIRE_CURRENT ) {
                current = -FireHandler.FIRE_CURRENT;
            }
            if( current > 0 && neighbor.getStartJunction() == jroot ) {//start near the beginning.
                double distAlongNew = overshoot;
                if( distAlongNew > neighbor.getLength() ) {
                    distAlongNew = neighbor.getLength();
                }
                else if( distAlongNew < 0 ) {
                    distAlongNew = 0;
                }
                CircuitLocation cl = new CircuitLocation( neighbor, distAlongNew );
                all.add( cl );
            }
            else if( current < 0 && neighbor.getEndJunction() == jroot ) {
                double distAlongNew = neighbor.getLength() - overshoot;
                if( distAlongNew > neighbor.getLength() ) {
                    distAlongNew = neighbor.getLength();
                }
                else if( distAlongNew < 0 ) {
                    distAlongNew = 0;
                }
                CircuitLocation cl = new CircuitLocation( neighbor, distAlongNew );
                all.add( cl );
            }
        }
        return (CircuitLocation[])all.toArray( new CircuitLocation[0] );
    }

    static class SmoothData {
        private ArrayList data;
        private int windowSize;

        public SmoothData( int windowSize ) {
            data = new ArrayList( windowSize );
            this.windowSize = windowSize;
        }

        public int numDataPoints() {
            return data.size();
        }

        public int getWindowSize() {
            return windowSize;
        }

        public void addData( double d ) {
            data.add( new Double( d ) );
            while( numDataPoints() > getWindowSize() ) {
                data.remove( 0 );
            }
        }

        public double getAverage() {
            double sum = 0;
            for( int i = 0; i < data.size(); i++ ) {
                java.lang.Double aDouble = (java.lang.Double)data.get( i );
                sum += aDouble.doubleValue();
            }
            return sum / data.size();
        }

        public double getMedian() {
            ArrayList list = new ArrayList( data );
            Collections.sort( list );
            int elm = list.size() / 2;
            return dataAt( elm );
        }

        private double dataAt( int elm ) {
            Double d = (Double)data.get( elm );
            return d.doubleValue();
        }
    }
}
