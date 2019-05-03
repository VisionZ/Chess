package ComplexServerClient;

import java.io.IOError;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

final class Server extends JFrame implements Runnable {
    
    private volatile boolean running = true;
    private ServerSocket server;
    
    List<Client> playingClients = new ArrayList<>();
    List<Client> waitingClients = new ArrayList<>();

    //of the list of clients playing, you may eavesdrop on their conversations
    //and may end the match if necessary.
    
    /*
     * 
     * Right clicking on a particular pair of clients will invoke a special custom dialog that shows their 
       converstation and 
     */
    
    public Server(int port) {
        super("Server");
        try {
            server = new ServerSocket(port);
        }
        catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    void shutdown() {
        running = false;
        if (server != null) {
            try {
                server.close();
            }
            catch (IOException ex) {
                throw new IOError(ex);
            }
            server = null;
        }
        for (Client client : waitingClients) {
            client.notifyAndDisconnect("The Server has shutdown.");
        }
        for (Client client : playingClients) {
            client.notifyAndDisconnect("The Server has shutdown.");
        }
        waitingClients.clear();
        playingClients.clear();
        waitingClients = playingClients = null;
    }

    void removeClientFromWaitingList(Client client) {
        waitingClients.remove(client);
    }

    void removeClientFromPlayingList(Client client) {
        playingClients.remove(client);
    }
    
    void moveClientFromWaitingListToPlayingList(Client client) {
        waitingClients.remove(client);
        playingClients.add(client);
    }
    
    void updateList() {
        //this is the list of waiting clients only!!
        //send data to waiting clients only!!!
    }
    
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String[] args) {
        new Server(1000);
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                Socket client = server.accept();
                waitingClients.add(new Client(this, client));
                //update list of waiting clients.
            }
            catch (IOException ex) {
                throw new IOError(ex);
            }
        }
        //shutdown code.
        //send shutdown messages to everyone, regardless of type.
        shutdown();
    }
}