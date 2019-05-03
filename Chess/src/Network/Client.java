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
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client implements Runnable {

    private final int port;
    private final String serverURL;
    private Socket client;
    private BufferedReader input;
    private PrintStream output;

    private final JButton exit;
    private final JButton send;
    private final JTextField messageText;
    private final JTextArea messageArea;

    private final Chess parent;

    @SuppressWarnings({"CallToThreadStartDuringObjectConstruction", "Convert2Lambda"})
    public Client(Chess parent, int port, String serverURL, JButton exit, JButton send, JTextField messageText, JTextArea messageArea) {
        this.parent = parent;
        this.port = port;
        this.serverURL = serverURL;
        this.exit = exit;
        this.send = send;
        this.messageText = messageText;
        this.messageArea = messageArea;

        Constants.removeFocusListeners(messageText);
        Constants.removeKeyListeners(messageText);
        Constants.removeActionListeners(exit);
        Constants.removeActionListeners(send);
        
        new Thread(this, "Client Connection Thread").start();
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public final void run() {
        try {
            messageText.setText("Connecting to: " + Quotes.surroundWithDoubleQuotes(serverURL) + " on port: " + port + "...");
            client = new Socket(serverURL, port);
        
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
                            "Are you sure you want to leave your game with the server?", "Leave Game?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, parent.getIcon()) == JOptionPane.YES_OPTION) { //not equal to the exit option, keep playing
                        parent.restore();
                        notifyServerAndCloseStreams();
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
            parent.getChild().restart(Board.CLIENT);
            parent.getChild().setOutput(output);
            JOptionPane.showMessageDialog(parent, "You have connected to the server.\nClose this dialog to start your match with the server.", "Connection Successful", JOptionPane.INFORMATION_MESSAGE, parent.getIcon());
        }
        catch (IOException ex) {
            parent.restore();
            closeStreams();
            JOptionPane.showMessageDialog(parent, "Error: Could not find or connect to: " + Quotes.surroundWithDoubleQuotes(serverURL) + "\n\nPossible Causes:\n-The server is offline or does not exist.\n-The port: " + port + " does not match the server's listening port.\n-The port: " + port + " may be busy with other computer traffic.\n-Your device or network has firewalls blocking this attempted connection.", "Server Not Found", JOptionPane.ERROR_MESSAGE, parent.getIcon());
            return;
        } 
        boolean serverExited = false;
        String messageFromServer;
        while (true) {
            if (client != null && !client.isClosed()) {
                try {
                    messageFromServer = DataProtector.decode(input.readLine());
                }
                catch (IOException ex) {
                    break;
                }
            }
            else {
                break;
            }
            if (EXIT.equals(messageFromServer)) {
                serverExited = true;
                break;
            }
            if (messageFromServer.startsWith(DATA)) {
                parent.getChild().readDataLine(messageFromServer.substring(DATA.length()));
                continue;
            }
            String oldText = messageArea.getText();
            messageArea.setText(oldText.isEmpty() ? "Server: " + messageFromServer : oldText + "\nServer: " + messageFromServer);
        }
        parent.restore();
        closeStreams();
        JOptionPane.showMessageDialog(parent, serverExited ? "The network game was closed because the server has left the game." : "You have left the network game with the server.", "Notice", JOptionPane.WARNING_MESSAGE, parent.getIcon());
    }

    public void notifyServerAndCloseStreams() {
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