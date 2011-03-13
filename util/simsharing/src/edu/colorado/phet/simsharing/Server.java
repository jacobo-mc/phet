// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.japi.Creator;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;
import edu.colorado.phet.simsharing.teacher.GetSessionList;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.remote;

/**
 * @author Sam Reid
 */
public class Server {
    public static int PORT = 44101;
    public static String HOST_IP_ADDRESS = "128.138.145.107";//phet-server, but can be mutated to specify a different host
    public static String[] names = new String[] { "Alice", "Bob", "Charlie", "Danielle", "Earl", "Frankie", "Gail", "Hank", "Isabelle", "Joe", "Kim", "Lucy", "Mikey", "Nathan", "Ophelia", "Parker", "Quinn", "Rusty", "Shirley", "Tina", "Uther Pendragon", "Vivian", "Walt", "Xander", "Yolanda", "Zed" };
    private ArrayList<SessionID> students = new ArrayList<SessionID>();
    private Morphia morphia;
    private Datastore ds;
    private Mongo mongo;

    public Server() {
        try {
            mongo = new Mongo();
            morphia = new Morphia();
            morphia.map( GravityAndOrbitsApplicationState.class );
            morphia.map( LatestIndex.class );
            morphia.map( Sample.class );
            ds = morphia.createDatastore( mongo, "simsharing-test-1" );//change index on datastore name instead of clearing datastore?
            ds.ensureIndexes(); //creates all defined with @Indexed
            ds.ensureCaps(); //creates all collections for @Entity(cap=@CappedAt(...))
        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }
    }

    /*
    * Use phet-server for deployments, but localhost for local testing.
     */
    public static void parseArgs( String[] args ) {
        final List<String> list = Arrays.asList( args );
        if ( list.contains( "-host" ) ) {
            HOST_IP_ADDRESS = args[list.indexOf( "-host" ) + 1];
        }
        System.out.println( "Using host: " + HOST_IP_ADDRESS );
    }

    public Sample getSample( SessionID id, int index ) {
//        long start = System.currentTimeMillis();
        if ( index == -1 ) {//just get the latest
            index = getLastIndex( id );
        }
        Query<Sample> found = ds.find( Sample.class, "sessionID", id ).filter( "index", index );
        final Sample sample = found.get();
//        long end = System.currentTimeMillis();
//        System.out.println( "found one, elapsed = " + ( end - start ) );
        return sample;
    }

    private void start() {
        remote().start( HOST_IP_ADDRESS, PORT ).register( "server", actorOf( new Creator<Actor>() {
            public Actor create() {
                return new UntypedActor() {
                    public void onReceive( Object o ) {
                        if ( o instanceof GetStudentData ) {
                            GetStudentData request = (GetStudentData) o;
                            Sample data = getSample( request.getStudentID(), request.getIndex() );
                            getContext().replySafe( data );//could be null
                        }
                        else if ( o instanceof StartSession ) {
                            if ( ds.createQuery( SessionCount.class ).get() == null ) {
                                ds.save( new SessionCount( 1 ) );
                            }
                            else {
                                ds.update( ds.createQuery( SessionCount.class ), ds.createUpdateOperations( SessionCount.class ).inc( "count" ) );
                            }
                            int sessionCount = ds.createQuery( SessionCount.class ).get().getCount();

                            final SessionID studentID = new SessionID( sessionCount, names[sessionCount % names.length] );
                            getContext().replySafe( studentID );
                            students.add( studentID );
                            ds.save( new SessionStarted( studentID, System.currentTimeMillis() ) );
                        }
                        else if ( o instanceof EndSession ) {
                            //Save the student info to disk and remove from system memory
                            final SessionID studentID = ( (EndSession) o ).getStudentID();
                            students.remove( studentID );
                            System.out.println( "student exited: " + studentID );
                            ds.save( new SessionEnded( studentID, System.currentTimeMillis() ) );
                        }
                        else if ( o instanceof GetStudentList ) {
                            ArrayList<StudentSummary> list = new ArrayList<StudentSummary>();
                            for ( SessionID student : students ) {
                                final Sample latestDataPoint = getSample( student, getLastIndex( student ) );
                                SerializableBufferedImage image = null;
                                if ( latestDataPoint != null && latestDataPoint.getData() != null ) {
                                    image = ( (GravityAndOrbitsApplicationState) latestDataPoint.getData() ).getThumbnail();
                                }
                                list.add( new StudentSummary( student, image, getSessionTime( student ), getTimeSinceLastEvent( student ) ) );
                            }
                            getContext().replySafe( new StudentList( list ) );
                        }
                        else if ( o instanceof AddStudentDataSample ) {
                            AddStudentDataSample request = (AddStudentDataSample) o;
                            int newIndex = getLastIndex( request.getSessionID() ) + 1;
                            ds.delete( ds.createQuery( LatestIndex.class ).filter( "sessionID", request.getSessionID() ) );
                            ds.save( new LatestIndex( request.getSessionID(), newIndex ) );

                            final Sample sample = new Sample( System.currentTimeMillis(), request.getSessionID(), request.getData(), newIndex, newIndex );
                            ds.save( sample );
                        }
                        else if ( o instanceof GetSessionList ) {
                            final SessionList recordingList = new SessionList();
                            final List<SessionStarted> sessionStarted = ds.find( SessionStarted.class ).asList();
                            Collections.sort( sessionStarted, new Comparator<SessionStarted>() {
                                public int compare( SessionStarted o1, SessionStarted o2 ) {
                                    return Double.compare( o1.getTime(), o2.getTime() );
                                }
                            } );
                            for ( SessionStarted started : sessionStarted ) {
                                recordingList.add( started );
                            }
                            getContext().replySafe( recordingList );
                        }
                    }
                };
            }
        } ) );
    }

    private int getLastIndex( SessionID sessionID ) {
        final LatestIndex index = ds.createQuery( LatestIndex.class ).filter( "sessionID", sessionID ).get();
        return index == null ? -1 : index.getIndex();
    }

    private long getTimeSinceLastEvent( SessionID session ) {
        return -1;
//        final ArrayList<Sample> data = getData( student );
//        if ( data == null ) { return -1; }
//        return System.currentTimeMillis() - data.get( data.size() - 1 ).getTime();
    }

    //how long has student been logged in
    private long getSessionTime( SessionID session ) {
        final SessionStarted sessionStarted = ds.find( SessionStarted.class, "sessionID", session ).get();
        return sessionStarted == null ? -1 : System.currentTimeMillis() - sessionStarted.getTime();
    }

    public static void main( String[] args ) throws IOException {
        Server.parseArgs( args );
        SimSharing.init();
        new Server().start();
    }
}