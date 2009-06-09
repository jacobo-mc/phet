package edu.colorado.phet.naturalselection.model;

import java.util.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;

public class Frenzy extends ClockAdapter {

    private double startTime;
    private double duration;
    private NaturalSelectionClock clock;
    private NaturalSelectionModel model;
    private ArrayList<Wolf> wolves;

    private boolean running = true;

    private ArrayList<Listener> listeners;

    private List<Bunny> targets;

    private static final Random random = new Random( System.currentTimeMillis() );

    public Frenzy( NaturalSelectionModel model, double duration ) {
        this.model = model;
        this.duration = duration;

        listeners = new ArrayList<Listener>();

        clock = model.getClock();
        startTime = clock.getSimulationTime();
        clock.addClockListener( this );

    }

    public void init() {
        int pop = model.getPopulation();
        int numWolves = 2 + pop / 5;

        initializeTargets();

        wolves = new ArrayList<Wolf>();
        for ( int i = 0; i < numWolves; i++ ) {
            final Wolf wolf = new Wolf( model, this );

            // if no wolves are killed this frenzy, don't let the wolves hunt
            if ( targets.isEmpty() ) {
                wolf.setHunting( false );
            }
            wolves.add( wolf );
            notifyWolfCreate( wolf );
            wolf.addListener( new Wolf.Listener() {
                public void onEvent( Wolf.Event event ) {
                    if ( event.type == Wolf.Event.TYPE_KILLED_BUNNY ) {
                        onBunnyKilled( wolf, wolf.getTarget() );
                    }
                }
            } );
        }
    }

    private void initializeTargets() {
        targets = new LinkedList<Bunny>();

        double baseFraction = ( Math.sqrt( (double) model.getPopulation() ) - 3 ) / 4;

        for ( Bunny bunny : model.getAliveBunnyList() ) {
            double actualFraction = baseFraction;

            if (
                    ( bunny.getColorPhenotype() == ColorGene.WHITE_ALLELE && model.getClimate() == NaturalSelectionModel.CLIMATE_ARCTIC )
                    || ( bunny.getColorPhenotype() == ColorGene.BROWN_ALLELE && model.getClimate() == NaturalSelectionModel.CLIMATE_EQUATOR )
                    ) {
                actualFraction /= 5;
            }

            if ( actualFraction > NaturalSelectionModel.MAX_KILL_FRACTION ) {
                actualFraction = NaturalSelectionModel.MAX_KILL_FRACTION;
            }

            if ( Math.random() < actualFraction ) {
                targets.add( bunny );
            }

        }
    }

    private void onBunnyKilled( Wolf wolf, Bunny bunny ) {
        targets.remove( bunny );
        if ( targets.isEmpty() ) {
            for ( Wolf daWolf : wolves ) {
                daWolf.setHunting( false );
            }
        }
    }

    public ArrayList<Wolf> getWolves() {
        return wolves;
    }

    /**
     * Get the time left
     *
     * @return Time left for the frenzy in milliseconds
     */
    public double getTimeLeft() {
        return ( startTime + duration - clock.getSimulationTime() ) * 1000 / ( (double) NaturalSelectionDefaults.CLOCK_FRAME_RATE );
    }

    public boolean isRunning() {
        return running;
    }

    public Bunny getNewWolfTarget( Wolf wolf ) {
        if ( targets.isEmpty() ) {
            return null;
        }
        int index = random.nextInt( targets.size() );
        return targets.get( index );
    }

    public void simulationTimeChanged( ClockEvent event ) {
        notifyFrenzyTimeLeft();

        if ( startTime + duration <= event.getSimulationTime() ) {
            endFrenzy();
        }

    }

    /**
     * Does all of the necessary cleanup to end the frenzy in the model
     */
    public void endFrenzy() {
        if ( !running ) {
            return;
        }

        clock.removeClockListener( this );
        model.endFrenzy();

        running = false;

        for ( Iterator<Wolf> iterator = wolves.iterator(); iterator.hasNext(); ) {
            Wolf wolf = iterator.next();
            wolf.disable();
        }

        notifyFrenzyStop();
    }

    //----------------------------------------------------------------------------
    // Notifiers
    //----------------------------------------------------------------------------

    private void notifyFrenzyStop() {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onFrenzyStop( this );
        }
    }

    private void notifyFrenzyTimeLeft() {
        Iterator<Listener> iter = listeners.iterator();
        double timeLeft = getTimeLeft();
        while ( iter.hasNext() ) {
            ( iter.next() ).onFrenzyTimeLeft( timeLeft );
        }
    }

    private void notifyWolfCreate( Wolf wolf ) {
        Iterator<Listener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).onWolfCreate( wolf );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        public void onFrenzyStop( Frenzy frenzy );

        public void onFrenzyTimeLeft( double timeLeft );

        public void onWolfCreate( Wolf wolf );
    }

}
