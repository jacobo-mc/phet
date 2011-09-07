// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socketutil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.colorado.phet.simsharing.Server;

/**
 * Fairly general-purpose class for communicating with a server over sockets.
 *
 * @author Sam Reid
 */
public class Client implements IActor {
    private Socket socket;
    public ObjectOutputStream writeToServer;
    public ObjectInputStream readFromServer;

    public Client() throws ClassNotFoundException, IOException {
        this( Server.HOST_IP_ADDRESS, Server.PORT );
    }

    public Client( String host, int port ) throws IOException, ClassNotFoundException {
        socket = new Socket( host, port );

        //Create streams for communicating with the server
        writeToServer = new ObjectOutputStream( socket.getOutputStream() );
        readFromServer = new ObjectInputStream( socket.getInputStream() );

        //Read the initial message from the server to verify communication is working properly
        Object fromServer = readFromServer.readObject();
        System.out.println( "MessageServer: " + fromServer );
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized Object ask( Object question ) throws IOException, ClassNotFoundException {
        writeToServer.writeObject( question );
        writeToServer.flush();
        Object result = readFromServer.readObject();
        return result;
    }

    //Must be synchronized because multiple threads may use this client to communicate with the server
    //If not synchronized, the messages could get mixed up and you could get exceptions like in:
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6554519
    public synchronized void tell( Object statement ) throws IOException {
        writeToServer.writeObject( statement );
        writeToServer.flush();
    }
}
