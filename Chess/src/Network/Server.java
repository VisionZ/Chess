package Network;

import GUI.Board;
import GUI.Chess;
import static Network.Networking.DATA;
import static Network.Networking.EXIT;
import Security.DataProtector;
import Util.Constants;
import Util.Quotes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server implements Runnable {

    private final Chess parent;

    private final int port;
    private ServerSocket server;
    private Socket client;
    private BufferedReader input;
    private PrintStream output;

    private final JButton exit;
    private final JButton send;
    private final JTextField messageText;
    private final JTextArea messageArea;

    @SuppressWarnings({"Convert2Lambda", "CallToThreadStartDuringObjectConstruction"})
    public Server(Chess parent, int port, JButton exit, JButton send, JTextField messageText, JTextArea messageArea) {
        this.parent = parent;
        this.port = port;
        this.exit = exit;
        this.send = send;
        this.messageText = messageText;
        this.messageArea = messageArea;

        Constants.removeFocusListeners(messageText);
        Constants.removeKeyListeners(messageText);
        Constants.removeActionListeners(exit);
        Constants.removeActionListeners(send);

        new Thread(this, "Server Connection Thread").start();
    }

    /**
     * Accepts and attempts to maintain a connection between this Server and
     * connecting client. This method keeps track of incoming input from the
     * client and output from this Server.
     */
    @Override
    @SuppressWarnings("Convert2Lambda")
    public final void run() {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(1000 * 20); //20 seconds
            //server.setSoTimeout(0);
            messageText.setText("Waiting 20 seconds for a client to connect to port: " + port + "...");
            client = server.accept();
            messageText.setText("Enter Message...");
            messageText.setEditable(true);
            messageArea.setText("");
            messageText.addFocusListener(new FocusListener() {
                
                private boolean beenFocused = false;
                
                @Override
                public void focusGained(FocusEvent fe) {
                    if (!beenFocused) {
                        messageText.setText("");
                    }
                    else {
                        beenFocused = true;
                    }
                }

                @Override
                public void focusLost(FocusEvent fe) {

                }
            });
            messageText.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent ke) {

                }

                @Override
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        String message = messageText.getText().trim();
                        output.println(DataProtector.encode(message));
                        messageText.setText("");
                        String oldText = messageArea.getText();
                        messageArea.setText(oldText.isEmpty() ? "You: " + message : oldText + "\nYou: " + message);
                    }
                }

                @Override
                public void keyReleased(KeyEvent ke) {

                }
            });
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (JOptionPane.showConfirmDialog(parent,
                            "Are you sure you want to leave your game with the client?", "Leave Game?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, parent.getIcon()) == JOptionPane.YES_OPTION) { //not equal to the exit option, keep playing
                        parent.restore();
                        notifyClientAndCloseStreams();
                    }
                }
            });
            send.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    String message = messageText.getText().trim();
                    output.println(DataProtector.encode(message));
                    messageText.setText("");
                    String oldText = messageArea.getText();
                    messageArea.setText(oldText.isEmpty() ? "You: " + message : oldText + "\nYou: " + message);
                }
            });
            input = new BufferedReader(new InputStreamReader(client.getInputStream(), Constants.UTF_8));
            output = new PrintStream(client.getOutputStream(), true, Constants.UTF_8);
            parent.getChild().restart(Board.SERVER);
            parent.getChild().setOutput(output);
            JOptionPane.showMessageDialog(parent, "The client: " + Quotes.surroundWithDoubleQuotes(client.toString()) + " has connected to this server.\nClose this dialog to start your match with the client.", "Connection Successful", JOptionPane.INFORMATION_MESSAGE, parent.getIcon());
        }
        catch (IOException ex) {
            parent.restore();
            closeStreams();
            JOptionPane.showMessageDialog(parent, client == null ? "Error: Could not find a client to connect with on port: " + port + "." : "Error: The client: " + client.toString() + " could not connect to this server.", "Client Not Found", JOptionPane.ERROR_MESSAGE, parent.getIcon());
            return;
        }
        boolean clientExited = false;
        String messageFromClient;
        while (true) {
            if (client != null && !client.isClosed()) {
                try {
                    messageFromClient = DataProtector.decode(input.readLine());
                }
                catch (IOException ex) {
                    break;
                }
            }
            else {
                break;
            }
            if (EXIT.equals(messageFromClient)) {
                clientExited = true;
                break;
            }
            if (messageFromClient.startsWith(DATA)) {
                parent.getChild().readDataLine(messageFromClient.substring(DATA.length()));
                continue;
            }
            String oldText = messageArea.getText();
            messageArea.setText(oldText.isEmpty() ? "Client: " + messageFromClient : oldText + "\nClient: " + messageFromClient);
        }
        parent.restore();
        closeStreams();
        JOptionPane.showMessageDialog(parent, clientExited ? "The network game was closed because the client has left the game." : "You have left the network game with the client.", "Notice", JOptionPane.INFORMATION_MESSAGE, parent.getIcon());
    }

    /**
     * Notifies the client that this Server is shutting down.
     */
    public void notifyClientAndCloseStreams() {
        if (output != null) {
            output.println(DataProtector.encode(EXIT));
            Constants.close(output); 
            output = null;
        }
        if (input != null) {
            Constants.close(input);
            input = null;
        }
        if (client != null) {
            Constants.close(client);
            client = null;
        }
        if (server != null) {
            Constants.close(server);
            server = null;
        }

        Constants.removeFocusListeners(messageText);
        Constants.removeKeyListeners(messageText);
        Constants.removeActionListeners(exit);
        Constants.removeActionListeners(send);

        messageText.setText("NO ACTIVE CONNECTION");
        messageText.setEditable(false);

        messageArea.setText("Chat Room not available. See Connect to use.");
        messageArea.setEditable(false);

        parent.getChild().restart(Board.LOCAL);
    }

    /**
     * Shuts down this Server. Once this operation is complete, this Server
     * cannot be effectively used.
     */
    public void closeStreams() {
        if (output != null) {
            Constants.close(output); 
            output = null;
        }
        if (input != null) {
            Constants.close(input);
            input = null;
        }
        if (client != null) {
            Constants.close(client);
            client = null;
        }
        if (server != null) {
            Constants.close(server);
            server = null;
        }

        Constants.removeFocusListeners(messageText);
        Constants.removeKeyListeners(messageText);
        Constants.removeActionListeners(exit);
        Constants.removeActionListeners(send);

        messageText.setText("NO ACTIVE CONNECTION");
        messageText.setEditable(false);

        messageArea.setText("Chat Room not available. See Connect to use.");
        messageArea.setEditable(false);
        parent.getChild().restart(Board.LOCAL);
    }
}