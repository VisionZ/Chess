package GUI;

import Engine.AI;
import Network.Client;
import Network.Server;
import Sounds.VolumeControl;
import Util.ChessConstants;
import Util.Constants;
import Util.History;
import Util.ImageUtils;
import Util.Quotes;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

public final class Chess extends JFrame {
    
    private static Chess INSTANCE;
    
    private final Icon icon;
    private final Board game;
    
    private Server server;
    private Client client;
    
    private static final String SERVER = "Server";
    private static final String CLIENT = "Client";
    private static final String[] NETWORK_GAME_OPTIONS = {SERVER, CLIENT};
    
    private final JButton exitFromNetworkGame;
    private final JButton sendMessageAcrossNetwork;
    private final JTextField messageAcrossNetwork;
    private final JTextArea messages;
    
    private final JMenuBar menuBar;
    
    private final JMenu fileMenu;
    private final JMenuItem openFile;
    private final JMenuItem saveFile;
    private final JMenuItem deleteFile;
    
    private final JMenu infoMenu;
    private final JMenuItem aboutInfo;
    private final JMenuItem helpInfo;
    private final JMenuItem miscInfo;
    
    private final JMenu settingsMenu;
    private final JMenu restartGame;
    private final JMenuItem localMatchRestart;
    private final JMenuItem playerAgainstComputerRestart;
    private final JMenuItem computerAgainstComputerRestart;
    
    private final JMenu computerSettings;
    private final JMenuItem computerDifficulty;
    private final JMenuItem computerParallelProcessing;
    
    private final JMenuItem useGarbageCollection;
    private final JMenuItem enableGridOutline;
    private final JMenuItem disableGridOutline;
    
    @SuppressWarnings({"Convert2Lambda", "CallToPrintStackTrace"})
    private Chess() {
        super("Chess");
        
        BufferedImage image = ImageUtils.readImage("ChessBoard", ImageUtils.PNG);
        super.setIconImage(image);
        icon = new ImageIcon(image);

        try {
            super.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        catch (Throwable ex) {
            
        }
        
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            try {
                Dimension iconSize = tray.getTrayIconSize();
                tray.add(new TrayIcon(image.getScaledInstance(iconSize.width, iconSize.height, BufferedImage.SCALE_SMOOTH)));
            }
            catch (AWTException ex) {
                ex.printStackTrace();
            }
        }

        {
            //here we set the outer frame dimensions
            final int width = Constants.SCREEN_BOUNDS.width / 2;
            final int height = Constants.SCREEN_BOUNDS.height;
            final Dimension frameArea = new Dimension(width, height);

            super.setSize(frameArea);
            super.setPreferredSize(frameArea);
            super.setMinimumSize(frameArea);
            super.setMaximumSize(frameArea);
            super.setLocation(Integer.MAX_VALUE, Integer.MAX_VALUE);
            super.setResizable(false);
            super.setVisible(true);
        }
        
        super.setJMenuBar(menuBar = new JMenuBar());

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        openFile = new JMenuItem("Open");
        openFile.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser open = new JFileChooser();
                open.addChoosableFileFilter(ChessFileFilter.INSTANCE);
                if (open.showOpenDialog(Chess.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selected = open.getSelectedFile();
                        String name = selected.getName();
                        String filePath = selected.getAbsolutePath();
                        if (name == null || filePath == null) {
                            return;
                        }
                        if (name.isEmpty() || filePath.isEmpty()) {
                            return;
                        }
                        if (!name.endsWith(ChessFileFilter.FILE_EXTENTION) || !filePath.endsWith(ChessFileFilter.FILE_EXTENTION)) {
                            JOptionPane.showMessageDialog(Chess.this, "Error: The file to open: " + Quotes.surroundWithDoubleQuotes(filePath) + "\nis not a \".chess\" file.", "Invalid File Type", JOptionPane.ERROR_MESSAGE, icon);
                            return;
                        }
                        game.readFile(filePath);
                    }
                    catch (NullPointerException ex) {
                        
                    }
                }
            }
        });
        
        saveFile = new JMenuItem("Save");
        saveFile.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser save = new JFileChooser();
                save.addChoosableFileFilter(ChessFileFilter.INSTANCE);
                if (save.showSaveDialog(Chess.this) == JFileChooser.APPROVE_OPTION) {
                    File chosen = save.getSelectedFile();
                    if (chosen == null) {
                        return;
                    }
                    String name = chosen.getName();
                    String filePath = chosen.getAbsolutePath();
                    if (name == null || filePath == null) {
                        return;
                    }
                    if (name.isEmpty() || filePath.isEmpty()) {
                        return;
                    }
                    if (!name.endsWith(ChessFileFilter.FILE_EXTENTION) || !filePath.endsWith(ChessFileFilter.FILE_EXTENTION)) {
                        JOptionPane.showMessageDialog(Chess.this, "Error: The file to save or overwrite: " + Quotes.surroundWithDoubleQuotes(filePath) + "\nis not a \".chess\" file.", "Invalid File Type", JOptionPane.ERROR_MESSAGE, icon);
                        return;
                    }
                    game.saveFile(filePath);
                }
            }
        });
        
        deleteFile = new JMenuItem("Delete");
        deleteFile.setAccelerator(KeyStroke.getKeyStroke('D', Event.CTRL_MASK));
        deleteFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser delete = new JFileChooser();
                delete.addChoosableFileFilter(ChessFileFilter.INSTANCE);
                delete.setMultiSelectionEnabled(true);
                if (delete.showDialog(Chess.this, "Delete") == JFileChooser.APPROVE_OPTION) {
                    if (JOptionPane.showConfirmDialog(Chess.this,
                            "Are you sure you want to delete these files?", "Delete Files?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION) {
                        File[] filesToDelete = delete.getSelectedFiles();
                        List<String> filesNotDeleted = new ArrayList<>(filesToDelete.length);
                        for (int index = 0; index != filesToDelete.length; ++index) {
                            File file = filesToDelete[index];
                            if (file != null) {
                                String filePath = file.getAbsolutePath();
                                if (filePath.toLowerCase().endsWith(ChessFileFilter.FILE_EXTENTION)) {
                                    if (!file.delete()) { //returns true if deleted, false otherwise
                                        filesNotDeleted.add(filePath);
                                    }
                                }
                                else {
                                    filesNotDeleted.add(filePath);
                                }
                            }
                        }
                        if (filesNotDeleted.isEmpty()) {
                            JOptionPane.showMessageDialog(Chess.this, "All files were deleted successfully.", "Delete Successful", JOptionPane.INFORMATION_MESSAGE, icon);
                        }
                        else {
                            String message = "";
                            for (Iterator<String> it = filesNotDeleted.iterator(); it.hasNext(); message += it.next()) {
                                if (it.hasNext()) {
                                    message += "\n";
                                }
                                else {
                                    break; //avoids one useless check of hasNext at the end of iteration
                                }
                            }
                            Util.TextFrame.showTextFrame(Chess.this, image, "Files Not Deleted", message);
                        }
                    }
                }
            }
        });
        
        infoMenu = new JMenu("Info");
        infoMenu.setMnemonic(KeyEvent.VK_I);
        
        aboutInfo = new JMenuItem("About");
        aboutInfo.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
        aboutInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String message = "Copyright 2016 William Zhao. All rights reserved.\n\n";
                Scanner scan = new Scanner(
                          "Sources:\n"
                        + "\n"
                        + "-Chess piece images are from:\n"
                        + " http://www.codemiles.com/finished-projects/java-chess-t618.html\n\n"
                        + "-The main icon image is from:\n"
                        + " https://www.iconfinder.com/icons/7159/board_game_chess_icon#size=128\n"
                        + " with the following license: GNU General Public License.\n"
                        + " For more information about this license please visit: http://www.gnu.org/copyleft/gpl.html\n\n"
                        + "-The source code for this application can be found at:\n"
                        + " https://github.com/VisionZ/Chess"
                );
                while (scan.hasNextLine()) {
                    message += scan.nextLine();
                    if (scan.hasNextLine()) {
                        message += "\n";
                    }
                    else {
                        break;
                    }
                } 
                Constants.close(scan);
                JOptionPane.showMessageDialog(Chess.this, message, "About", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
        
        helpInfo = new JMenuItem("Help");
        helpInfo.setAccelerator(KeyStroke.getKeyStroke('H', Event.CTRL_MASK));
        helpInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(Chess.this, "Helpful Info:\n\n-Some functions of this application will be disabled when playing over a network.\n\n-This application can only open, save, and delete \".chess\" files.\n These files each contain the data of a saved game.\n\n-Hover the cursor above a tile to view its information.\n\n-The current game mode is: " + Quotes.surroundWithDoubleQuotes(game.getGameMode()) + ".", "Help", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
        
        miscInfo = new JMenuItem("Misc");
        miscInfo.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
        miscInfo.addActionListener(new ActionListener() {
            
            private static final String UNKNOWN = "Unknown";
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                String user;
                String deviceName;
                String loopBackAddress;
                String ipAddress;
                try {
                    user = Constants.SYSTEM.getName();
                }
                catch (Throwable ex) {
                    user = UNKNOWN;
                }
                try {
                    deviceName = InetAddress.getLocalHost().getHostName();
                }
                catch (UnknownHostException ex) {
                    deviceName = UNKNOWN;
                }
                try {
                    loopBackAddress = InetAddress.getLoopbackAddress().toString();
                }
                catch (Throwable ex) {
                    loopBackAddress = UNKNOWN;
                }
                try {
                    ipAddress = ""; 
                    InetAddress[] addresses = InetAddress.getAllByName(deviceName);
                    if (addresses.length != 0) {
                        for (int index = 0, lastIndex = (addresses.length - 1);; ++index) {
                            if (index == lastIndex) {
                                ipAddress += "-" + addresses[index];
                                break;
                            }
                            ipAddress += "-" + addresses[index] + "\n";
                        }
                    }
                }
                catch (UnknownHostException ex) {
                    ipAddress = UNKNOWN;
                }
                JOptionPane.showMessageDialog(Chess.this, "Current User: " + user + "\nDevice Hostname: " + deviceName + "\nLoopback Address: " + loopBackAddress + "\n\nVisible IP Addresses of this device:\n" + ipAddress, "Miscellaneous Information", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
        
        settingsMenu = new JMenu("Settings");
        settingsMenu.setMnemonic(KeyEvent.VK_S);
        
        restartGame = new JMenu("Restart");
        restartGame.setMnemonic(KeyEvent.VK_R);
        
        localMatchRestart = new JMenuItem("Local");
        localMatchRestart.setAccelerator(KeyStroke.getKeyStroke('L', Event.CTRL_MASK));
        localMatchRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if ((game.getState() == Board.SERVER) || (game.getState() == Board.CLIENT)) {
                    JOptionPane.showMessageDialog(Chess.this, "Cannot restart while playing a network game.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
                    return;
                }
                if (JOptionPane.showConfirmDialog(Chess.this,
                        "Are you sure you want to play a\nnew local match with another player?", "Restart?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION) {
                    game.restart(Board.LOCAL);
                }
            }
        });

        playerAgainstComputerRestart = new JMenuItem("AI");
        playerAgainstComputerRestart.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
        playerAgainstComputerRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if ((game.getState() == Board.SERVER) || (game.getState() == Board.CLIENT)) {
                    JOptionPane.showMessageDialog(Chess.this, "Cannot restart while playing a network game.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
                    return;
                }
                String color = (String) JOptionPane.showInputDialog(Chess.this, "Do you want to play as White or Black?", "Select Side", JOptionPane.QUESTION_MESSAGE, icon, new String[]{ChessConstants.WHITE, ChessConstants.BLACK}, null);
                if (color == null) {
                    return;
                }
                if (ChessConstants.WHITE.equals(color)) {
                    //human-white
                    String difficulty = (String) JOptionPane.showInputDialog(Chess.this, "Select Black AI difficulty:", "AI Difficulty", JOptionPane.QUESTION_MESSAGE, icon, AI.DIFFICULTY, null);
                    if (difficulty == null) {
                        return;
                    }
                    for (int index = AI.MIN_DIFFICULTY; index <= AI.MAX_DIFFICULTY; ++index) {
                        if (AI.DIFFICULTY[index].equals(difficulty)) {
                            game.getBlackComputer().setSearchTime(AI.SEARCH_TIMES[index]);
                            break;
                        }
                    }
                    game.getBlackComputer().getSelectedPositions().clear();
                    game.restart(Board.WHITE_PLAYER_VS_BLACK_COMPUTER);
                }
                else {
                    //human-black
                    String difficulty = (String) JOptionPane.showInputDialog(Chess.this, "Select White AI difficulty:", "AI Difficulty", JOptionPane.QUESTION_MESSAGE, icon, AI.DIFFICULTY, null);
                    if (difficulty == null) {
                        return;
                    }
                    for (int index = AI.MIN_DIFFICULTY; index <= AI.MAX_DIFFICULTY; ++index) {
                        if (AI.DIFFICULTY[index].equals(difficulty)) {
                            game.getWhiteComputer().setSearchTime(AI.SEARCH_TIMES[index]);
                            break;
                        }
                    }
                    game.getWhiteComputer().getSelectedPositions().clear();
                    game.restart(Board.BLACK_PLAYER_VS_WHITE_COMPUTER);
                }
            }
        });

        computerAgainstComputerRestart = new JMenuItem("AI vs AI");
        computerAgainstComputerRestart.setAccelerator(KeyStroke.getKeyStroke('V', Event.CTRL_MASK));
        computerAgainstComputerRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if ((game.getState() == Board.SERVER) || (game.getState() == Board.CLIENT)) {
                    JOptionPane.showMessageDialog(Chess.this, "Cannot restart while playing a network game.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
                    return;
                }
                String whiteDifficulty = (String) JOptionPane.showInputDialog(Chess.this, "Select White AI difficulty:", "AI Difficulty", JOptionPane.QUESTION_MESSAGE, icon, AI.DIFFICULTY, null);
                if (whiteDifficulty == null) {
                    return;
                }
                String blackDifficulty = (String) JOptionPane.showInputDialog(Chess.this, "Select Black AI difficulty:", "AI Difficulty", JOptionPane.QUESTION_MESSAGE, icon, AI.DIFFICULTY, null);
                if (blackDifficulty == null) {
                    return;
                }
                for (int index = AI.MIN_DIFFICULTY; index <= AI.MAX_DIFFICULTY; ++index) {
                    if (AI.DIFFICULTY[index].equals(whiteDifficulty)) {
                        game.getWhiteComputer().setSearchTime(AI.SEARCH_TIMES[index]);
                        break;
                    }
                }
                for (int index = AI.MIN_DIFFICULTY; index <= AI.MAX_DIFFICULTY; ++index) {
                    if (AI.DIFFICULTY[index].equals(blackDifficulty)) {
                        game.getBlackComputer().setSearchTime(AI.SEARCH_TIMES[index]);
                        break;
                    }
                }
                game.getWhiteComputer().getSelectedPositions().clear();
                game.getBlackComputer().getSelectedPositions().clear();
                game.restart(Board.COMPUTER_VS_COMPUTER);
            }
        });

        computerSettings = new JMenu("AI Settings");
        computerSettings.setMnemonic(KeyEvent.VK_A);

        computerDifficulty = new JMenuItem("Change Difficulty");
        computerDifficulty.setAccelerator(KeyStroke.getKeyStroke('D', Event.CTRL_MASK));
        computerDifficulty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (game.getState()) {
                    case Board.WHITE_PLAYER_VS_BLACK_COMPUTER: {
                        if (game.computerThinking()) {
                            JOptionPane.showMessageDialog(Chess.this, "Cannot access this setting while the AI is thinking.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
                            return;
                        }
                        String difficulty = (String) JOptionPane.showInputDialog(Chess.this, "Select Black AI difficulty:", "AI Difficulty", JOptionPane.QUESTION_MESSAGE, icon, AI.DIFFICULTY, null);
                        if (difficulty == null) {
                            return;
                        }
                        for (int index = AI.MIN_DIFFICULTY; index <= AI.MAX_DIFFICULTY; ++index) {
                            if (AI.DIFFICULTY[index].equals(difficulty)) {
                                game.getBlackComputer().setSearchTime(AI.SEARCH_TIMES[index]);
                                break;
                            }
                        }
                        game.setState(Board.WHITE_PLAYER_VS_BLACK_COMPUTER);
                        return;
                    }
                    case Board.BLACK_PLAYER_VS_WHITE_COMPUTER: {
                        if (game.computerThinking()) {
                            JOptionPane.showMessageDialog(Chess.this, "Cannot access this setting while the AI is thinking.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
                            return;
                        }
                        String difficulty = (String) JOptionPane.showInputDialog(Chess.this, "Select White AI difficulty:", "AI Difficulty", JOptionPane.QUESTION_MESSAGE, icon, AI.DIFFICULTY, null);
                        if (difficulty == null) {
                            return;
                        }
                        for (int index = AI.MIN_DIFFICULTY; index <= AI.MAX_DIFFICULTY; ++index) {
                            if (AI.DIFFICULTY[index].equals(difficulty)) {
                                game.getWhiteComputer().setSearchTime(AI.SEARCH_TIMES[index]);
                                break;
                            }
                        }
                        game.setState(Board.BLACK_PLAYER_VS_WHITE_COMPUTER);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(Chess.this, "Cannot access this setting while not playing against the AI.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });

        computerParallelProcessing = new JMenuItem("Parallel Processing");
        computerParallelProcessing.setAccelerator(KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
        computerParallelProcessing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String whichAI = (String) JOptionPane.showInputDialog(Chess.this, "Which AI do you want to enable/disable parallel processing?", "Parallel Processing", JOptionPane.QUESTION_MESSAGE, icon, new String[]{"White AI", "Black AI"}, null);
                if (whichAI == null) {
                    return;
                }
                switch (whichAI) {
                    case "Black AI": {
                        if (game.getBlackComputer().isParallelProcessing()) {
                            switch (JOptionPane.showConfirmDialog(Chess.this,
                                    "Disable Parallel Processing for the Black AI?", "Disable Parallel Processing",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, icon)) {
                                case JOptionPane.YES_OPTION: {
                                    game.getBlackComputer().useParallelProcessing(false);
                                    return;
                                }
                            }
                        }
                        else {
                            switch (JOptionPane.showConfirmDialog(Chess.this,
                                    "Enable Parallel Processing for the Black AI?\nWarning: This will use all of your device's CPU cores,\nonly do this if you have no other applications open.", "Enable Parallel Processing",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, icon)) {
                                case JOptionPane.YES_OPTION: {
                                    game.getBlackComputer().useParallelProcessing(true);
                                    return;
                                }
                            }
                        }
                        return;
                    }
                    case "White AI": {
                        if (game.getWhiteComputer().isParallelProcessing()) {
                            switch (JOptionPane.showConfirmDialog(Chess.this,
                                    "Disable Parallel Processing for the White AI?", "Disable Parallel Processing",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, icon)) {
                                case JOptionPane.YES_OPTION: {
                                    game.getWhiteComputer().useParallelProcessing(false);
                                    return;
                                }
                            }
                        }
                        else {
                            switch (JOptionPane.showConfirmDialog(Chess.this,
                                    "Enable Parallel Processing for the White AI?\nWarning: This will use all of your device's CPU cores,\nonly do this if you have no other applications open.", "Enable Parallel Processing",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, icon)) {
                                case JOptionPane.YES_OPTION: {
                                    game.getWhiteComputer().useParallelProcessing(true);
                                    return;
                                }
                            }
                        }
                        return;
                    }
                }
                JOptionPane.showMessageDialog(Chess.this, "Cannot access this setting while not playing against the AI.", "Error", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
        
        useGarbageCollection = new JMenuItem("Use Garbage Collection");
        useGarbageCollection.setAccelerator(KeyStroke.getKeyStroke('G', Event.CTRL_MASK));
        useGarbageCollection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Constants.RUNTIME.gc();
                //could make a thread to gc forever until stopped by user
            }
        });

        enableGridOutline = new JMenuItem("Enable Grid Outline");
        enableGridOutline.setAccelerator(KeyStroke.getKeyStroke('E', Event.CTRL_MASK));
        enableGridOutline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color color = JColorChooser.showDialog(Chess.this, "Select Grid Outline", null);
                if (color != null) {
                    game.setOutline(color);
                    game.repaint();
                }
            }
        });
        
        disableGridOutline = new JMenuItem("Disable Grid Outline");
        disableGridOutline.setAccelerator(KeyStroke.getKeyStroke('D', Event.CTRL_MASK));
        disableGridOutline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (game.getOutline() == null) {
                    JOptionPane.showMessageDialog(Chess.this, "The grid currently has no outline.", "Error", JOptionPane.ERROR_MESSAGE, icon);
                    return;
                }
                if (JOptionPane.showConfirmDialog(Chess.this,
                        "Are you sure you want to disable the current grid outline?", "Disable Grid Outline?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION) {
                    game.setOutline(null);
                    game.repaint();
                }
            }
        });
        
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(deleteFile);
        
        infoMenu.add(aboutInfo);
        infoMenu.add(helpInfo);
        infoMenu.add(miscInfo);
        
        settingsMenu.add(restartGame);
        restartGame.add(localMatchRestart);
        restartGame.add(playerAgainstComputerRestart);
        restartGame.add(computerAgainstComputerRestart);
        computerSettings.add(computerDifficulty);
        computerSettings.add(computerParallelProcessing);
        settingsMenu.add(computerSettings);
        settingsMenu.add(useGarbageCollection);
        settingsMenu.add(enableGridOutline);
        settingsMenu.add(disableGridOutline);
        
        menuBar.add(fileMenu);
        menuBar.add(infoMenu);
        menuBar.add(settingsMenu);
        
        Container contentPane = super.getContentPane();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
       
        final int contentPaneWidth = contentPane.getWidth();
        final int contentPaneHeight = contentPane.getHeight();
       
        final int boardLength = getBiggestNumberDivisibleBy8(Math.min(contentPaneWidth / 2, contentPaneHeight / 2));
        final int halfBoardLength = boardLength / 2;  
        final int statusDisplayerHeight = contentPaneHeight / 12;
   
       
        StatusDisplayer gameStatusDisplayer = new StatusDisplayer(0, 0, boardLength, statusDisplayerHeight);
        
        History moveHistory = new History(boardLength, 0, contentPaneWidth - boardLength, boardLength + statusDisplayerHeight);
        JScrollPane historyScrollTable = moveHistory.getScrollPane();
       
        game = new Board(this, icon, moveHistory, gameStatusDisplayer, 0, statusDisplayerHeight, boardLength);
        
        final int volumeControlHeight = (contentPaneHeight - historyScrollTable.getHeight()) / 2;
        
        JSlider volumeControl = new VolumeControl(this, 0, statusDisplayerHeight + boardLength, halfBoardLength, volumeControlHeight);
        
        JButton connectToNetwork = new JButton("Connect");
        connectToNetwork.setBounds(0, volumeControl.getY() + volumeControl.getHeight(), volumeControl.getWidth(), volumeControl.getHeight() / 2);
        connectToNetwork.setToolTipText("Start Network Game");
        connectToNetwork.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        connectToNetwork.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if ((game.getState() == Board.SERVER) || (game.getState() == Board.CLIENT)) {
                    JOptionPane.showMessageDialog(Chess.this, "You are already connected to a network game.", "Error", JOptionPane.ERROR_MESSAGE, icon);
                    return;
                }
                String choice = (String) JOptionPane.showInputDialog(Chess.this, "Host a game as a server or connect to a game as a client:", "Select Option", JOptionPane.QUESTION_MESSAGE, icon, NETWORK_GAME_OPTIONS, null);
                if (choice == null) {
                    return;
                }
                switch (choice) {
                    case SERVER: {
                        String port = (String) JOptionPane.showInputDialog(Chess.this, "Enter a port number to listen for a client connection:", "Enter Port Number", JOptionPane.QUESTION_MESSAGE, icon, null, null);
                        if (port == null || port.isEmpty()) {
                            return;
                        }
                        try {
                            int portNumber = Integer.parseInt(port);
                            if (portNumber <= 0) {
                                JOptionPane.showMessageDialog(Chess.this, "Error: The specified port number: " + Quotes.surroundWithDoubleQuotes(port) + " is less the minimum valid port number: " + Quotes.surroundWithDoubleQuotes("1") + ".", "Invalid Port Number", JOptionPane.ERROR_MESSAGE, icon);
                                return;
                            }
                            else if (portNumber > Character.MAX_VALUE) {
                                JOptionPane.showMessageDialog(Chess.this, "Error: The specified port number: " + Quotes.surroundWithDoubleQuotes(port) + " is over the maximum valid port number: " + Quotes.surroundWithDoubleQuotes("65535") + ".", "Invalid Port Number", JOptionPane.ERROR_MESSAGE, icon);
                                return;
                            }
                            game.restart(Board.SERVER);
                            game.setAccessable(false);
                            fileMenu.remove(0);
                            fileMenu.remove(0);
                            settingsMenu.remove(0);
                            server = new Server(Chess.this, portNumber, exitFromNetworkGame, sendMessageAcrossNetwork, messageAcrossNetwork, messages);
                        }
                        catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(Chess.this, "Error: Invalid port number: " + Quotes.surroundWithDoubleQuotes(port) + ".", "Invalid Port Number", JOptionPane.ERROR_MESSAGE, icon);
                        }
                        break;
                    }
                    case CLIENT: {
                        String host = (String) JOptionPane.showInputDialog(Chess.this, "Enter the server IP-Address or IPv4 Address:", "Enter Server Address", JOptionPane.QUESTION_MESSAGE, icon, null, null);
                        if (host == null || host.isEmpty()) {
                            return;
                        }
                        String port = (String) JOptionPane.showInputDialog(Chess.this, "Enter a port number to connect to the server: " + Quotes.surroundWithDoubleQuotes(host), "Enter Port Number", JOptionPane.QUESTION_MESSAGE, icon, null, null);
                        if (port == null || port.isEmpty()) {
                            return;
                        }
                        try {
                            int portNumber = Integer.parseInt(port);
                            if (portNumber <= 0) {
                                JOptionPane.showMessageDialog(Chess.this, "Error: The specified port number: " + Quotes.surroundWithDoubleQuotes(port) + " is less the minimum valid port number: " + Quotes.surroundWithDoubleQuotes("1") + ".", "Invalid Port Number", JOptionPane.ERROR_MESSAGE, icon);
                                return;
                            }
                            else if (portNumber > Character.MAX_VALUE) {
                                JOptionPane.showMessageDialog(Chess.this, "Error: The specified port number: " + Quotes.surroundWithDoubleQuotes(port) + " is over the maximum valid port number: " + Quotes.surroundWithDoubleQuotes("65535") + ".", "Invalid Port Number", JOptionPane.ERROR_MESSAGE, icon);
                                return;
                            }
                            game.restart(Board.CLIENT);
                            game.setAccessable(false);
                            fileMenu.remove(0);
                            fileMenu.remove(0);
                            settingsMenu.remove(0);
                            client = new Client(Chess.this, portNumber, host, exitFromNetworkGame, sendMessageAcrossNetwork, messageAcrossNetwork, messages);
                        }
                        catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(Chess.this, "Error: Invalid port number: " + Quotes.surroundWithDoubleQuotes(port) + ".", "Invalid Port Number", JOptionPane.ERROR_MESSAGE, icon);
                        }
                        break;
                    }
                }
            }
        });

        messages = new JTextArea("Chat room not available. See Connect to use.");
        messages.setEditable(false);
        messages.setToolTipText("Chat Room");
        messages.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JScrollPane messagesScrollPane = new JScrollPane();
        messagesScrollPane.setBounds(volumeControl.getWidth(), volumeControl.getY(), (game.getWidth() / 2) + historyScrollTable.getWidth(), volumeControl.getHeight() + connectToNetwork.getHeight());
        messagesScrollPane.setViewportView(messages);

        super.setVisible(true);
        
        //we must intialize the following components height in a special manner
        int bottomComponentHeight = contentPaneHeight - (statusDisplayerHeight + boardLength + volumeControlHeight + volumeControlHeight / 2) - menuBar.getHeight();
        System.out.println(bottomComponentHeight);
        exitFromNetworkGame = new JButton("Exit");
        exitFromNetworkGame.setBounds(0, connectToNetwork.getY() + connectToNetwork.getHeight(), connectToNetwork.getWidth() / 2, bottomComponentHeight);
        exitFromNetworkGame.setToolTipText("Exit Network Game");
        exitFromNetworkGame.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        sendMessageAcrossNetwork = new JButton("Send");
        sendMessageAcrossNetwork.setBounds(exitFromNetworkGame.getWidth(), connectToNetwork.getY() + connectToNetwork.getHeight(), connectToNetwork.getWidth() / 2, bottomComponentHeight);
        sendMessageAcrossNetwork.setToolTipText("Send Message");
        sendMessageAcrossNetwork.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        messageAcrossNetwork = new JTextField("NO ACTIVE CONNECTION");
        messageAcrossNetwork.setBounds(volumeControl.getWidth(), connectToNetwork.getY() + connectToNetwork.getHeight(), (game.getWidth() / 2) + historyScrollTable.getWidth(), bottomComponentHeight);
        messageAcrossNetwork.setEditable(false);
        messageAcrossNetwork.setToolTipText("Enter Message");
        messageAcrossNetwork.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        super.setVisible(false);
        super.setVisible(true);

        contentPane.add(historyScrollTable);
        contentPane.add(gameStatusDisplayer);
        contentPane.add(game);
        contentPane.add(volumeControl);
        contentPane.add(connectToNetwork);
        contentPane.add(exitFromNetworkGame);
        contentPane.add(sendMessageAcrossNetwork);
        contentPane.add(messageAcrossNetwork);
        contentPane.add(messagesScrollPane);

 

        /*
        try {
            super.setAlwaysOnTop(true);
        }
        catch (SecurityException ex) {

        }
        */
        
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (JOptionPane.showConfirmDialog(Chess.this,
                        "Are you sure you want to exit the game?", "Exit?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION) { //not equal to the exitFromNetworkGame option, keep playing
                    //notification will do nothing if stream is already closed.
                    if (server != null) {
                        server.notifyClientAndCloseStreams();
                    }
                    if (client != null) {
                        client.notifyServerAndCloseStreams();
                    }
                    Constants.RUNTIME.exit(0);
                }
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });
        
        
        
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocation((Constants.SCREEN_BOUNDS.width / 2) - (Constants.SCREEN_BOUNDS.width / 4), (Constants.SCREEN_BOUNDS.height / 2) - (getHeight() / 2));    
        super.setVisible(true);
        
        System.out.println(menuBar.getBounds());
    }

    public Icon getIcon() {
        return icon;
    }

    public Board getChild() {
        return game;
    }
    
    public static Chess getInstance() {
        return INSTANCE == null ? INSTANCE = new Chess() : INSTANCE;
    }

    public static final void start() {
        Constants.moveToCenterScreen(INSTANCE = new Chess());
    }

    public void restore() {
        fileMenu.add(saveFile, 0);
        fileMenu.add(openFile, 0);
        settingsMenu.add(restartGame, 0);
        fileMenu.repaint();
        settingsMenu.repaint();
    }
    
    private static int getBiggestNumberDivisibleBy8(int n) {
        while (true) {
            if (n % 8 == 0) {
                return n;
            }
            --n;
        }
    }

    private static final class ChessFileFilter extends FileFilter {

        private static final String FILE_EXTENTION = ".chess";
        private static final FileFilter INSTANCE = new ChessFileFilter();

        private ChessFileFilter() {

        }

        @Override
        public final boolean accept(File file) {
            return file == null ? false : file.getAbsolutePath().toLowerCase().endsWith(FILE_EXTENTION);
        }

        @Override
        public final String getDescription() {
            return "Chess Game Files (" + Quotes.surroundWithDoubleQuotes(FILE_EXTENTION) + ")";
        }
    }
}