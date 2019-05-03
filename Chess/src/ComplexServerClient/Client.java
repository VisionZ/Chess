package ComplexServerClient;

import Util.Constants;
import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Iterator;

final class Client implements Runnable {

    private final Server server;
    
    private String name;
    private String color;

    private int state = ClientStates.WAITING;

    private Socket clientConnection;
    private BufferedReader fromClient;
    private PrintStream toClient;

    private Client connectedWith;

    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    Client(Server host, Socket connection) {
        server = host;
        clientConnection = connection;
        try {
            fromClient = new BufferedReader(new InputStreamReader(connection.getInputStream(), Constants.UTF_8));
            toClient = new PrintStream(connection.getOutputStream(), true, Constants.UTF_8);
            String[] nameAndColor = fromClient.readLine().split(" ");
            name = nameAndColor[0];
            color = nameAndColor[1];
            new Thread(name).start();
        }
        catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    void disconnect() {
        //notify client to close
        if (fromClient != null) {
            try {
                fromClient.close();
            }
            catch (IOException ex) {
                throw new IOError(ex);
            }
            fromClient = null;
        }
        if (toClient != null) {
            toClient.close();
            toClient = null;
        }
        if (clientConnection != null) {
            try {
                clientConnection.close();
            }
            catch (IOException ex) {
                throw new IOError(ex);
            }
            clientConnection = null;
        }
    }
    
    
    void notifyAndDisconnect(String message) {
        //notify client to close
        if (fromClient != null) {
            try {
                fromClient.close();
            }
            catch (IOException ex) {
                throw new IOError(ex);
            }
            fromClient = null;
        }
        if (toClient != null) {
            toClient.println(SpecialMessages.EXIT + message);
            toClient.close();
            toClient = null;
        }
        if (clientConnection != null) {
            try {
                clientConnection.close();
            }
            catch (IOException ex) {
                throw new IOError(ex);
            }
            clientConnection = null;
        }
    }
    
    void link(Client other) {
        if (connectedWith != null || other.connectedWith != null) {
            throw new IllegalStateException();
        }
        if (color.equals(other.color)) {
            throw new IllegalArgumentException();
        }
        connectedWith = other;
        other.connectedWith = this;
    }

    @Override
    public void run() {
        if (state != ClientStates.WAITING) {
            throw new IllegalStateException();
        }
        //send server data table to this client
        //and all other waiting clients.
        server.updateList();
        for (;;) {
            try {
                String dataFromClient = fromClient.readLine();
                if (SpecialMessages.EXIT.equals(dataFromClient)) {
                    //client is leaving.
                    if (state == ClientStates.PLAYING) {
                        disconnect(); //this client already knows it has disconnected
                        connectedWith.notifyAndDisconnect(name + " has left the match."); //the other client needs to why the connection is suddenly severed
                        server.removeClientFromPlayingList(this);
                        //send special message to the other client that this client caused their match to end
                        server.removeClientFromPlayingList(connectedWith);
                    }
                    else {
                        disconnect();
                        server.removeClientFromWaitingList(this);
                        //this lonely client can leave freely.
                    }
                    //update client data table and send to remaining waiting clients, not this one
                    server.updateList(); //this will do it automatically because we told the server to remove these clients.
                    return;
                }
                else if (dataFromClient.startsWith(SpecialMessages.PLAYER)) {
                    String requestedName = dataFromClient.substring(SpecialMessages.PLAYER.length());
                    for (Iterator<Client> it = server.waitingClients.iterator(); it.hasNext();) {
                        Client other = it.next();
                        if (other.name.equals(requestedName)) {
                            link(other);
                            server.moveClientFromWaitingListToPlayingList(other);
                            server.moveClientFromWaitingListToPlayingList(this);
                            server.updateList();
                            return;
                        }
                    }
                    toClient.println(requestedName + " could not be found!");
                    server.updateList();
                }                
                else {
                    //just a regular text message between clients.
                    //or a chess position message, does not matter
                    //send to real client anyway
                    connectedWith.toClient.println(dataFromClient);
                }
            }
            catch (IOException ex) {

            }
        }
    }
}
