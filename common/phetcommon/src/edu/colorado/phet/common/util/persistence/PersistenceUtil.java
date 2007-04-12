/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * PersistenceUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistenceUtil {

    /**
     * Adds Save and Restore items to the File menu of a PhetApplication
     *
     * @param application
     * @deprecated
     */
    public static void addMenuItems( PhetApplication application ) {

        JMenuItem mi2 = new JMenuItem( "Restore state" );
        mi2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().restoreState( "/temp/ttt.xml" );
            }
        } );
        application.getPhetFrame().addFileMenuItem( mi2 );

        JMenuItem mi = new JMenuItem( "Save state" );
        mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().saveState( "/temp/ttt.xml" );
            }
        } );
        application.getPhetFrame().addFileMenuItem( mi );
        application.getPhetFrame().addFileMenuSeparatorAfter( mi2 );
    }

    /**
     * Perform a deep copy of a Serializable object graph using serialization.
     *
     * @param object the object to be copied.
     * @return a deep copy of the specified object.
     * @throws edu.colorado.phet.common.util.persistence.PersistenceUtil.CopyFailedException
     *
     */
    public static Serializable copy( Serializable object ) throws CopyFailedException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            ObjectOutputStream objectOut = new ObjectOutputStream( byteOut );

            objectOut.writeObject( object );
            objectOut.flush();

            ByteArrayInputStream byteIn = new ByteArrayInputStream( byteOut.toByteArray() );
            return (Serializable)new ObjectInputStream( byteIn ).readObject();
        }
        catch( IOException e ) {
            throw new CopyFailedException( e );
        }
        catch( ClassNotFoundException e ) {
            throw new CopyFailedException( e );
        }
    }

//    private static class MyObjectOutputStream extends ObjectOutputStream {
//
//        public MyObjectOutputStream( OutputStream out ) throws IOException {
//            super( out );
//            enableReplaceObject( true );
//        }
//
//        protected Object replaceObject( Object obj ) throws IOException {
//            if( obj instanceof Point2D.Double && !( obj instanceof SPoint2D.Double ) ) {
//                Point2D.Double pt = (Point2D.Double)obj;
//                return new SPoint2D.Double( pt.x, pt.y );
//            }
//            else {
//                return super.replaceObject( obj );
//            }
//        }
//    }

    public static class CopyFailedException extends Exception {
        public CopyFailedException( Throwable cause ) {
            super( cause );
        }
    }
}
