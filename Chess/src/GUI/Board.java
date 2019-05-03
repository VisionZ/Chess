package GUI;

import Engine.AI;
import static Network.Networking.DATA;
import Util.ChessConstants;
import static Util.ChessConstants.LINEAR_LENGTH;
import Util.Constants;
import Security.DataProtector;
import Util.History;
import Util.Quotes;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javafx.scene.media.AudioClip;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//always remember... all pieces must be on the board
//before enabling protections, othewise we will
//have a board with improper protected tiles
public final class Board extends JPanel implements Runnable {
    
    private static final boolean PERFT = false;

    //game states
    public static final int LOCAL = 0;
    public static final int SERVER = 1;
    public static final int CLIENT = 2;
    public static final int COMPUTER_VS_COMPUTER = 3;
    public static final int WHITE_PLAYER_VS_BLACK_COMPUTER = 4;
    public static final int BLACK_PLAYER_VS_WHITE_COMPUTER = 5;
    public static final int COMPUTER = 6;
    private int gameMode = LOCAL;

    //output stream if game state is server or client
    private PrintStream output;

    //parent frame 
    private final Chess parent;
    private final String parentTitle;
    private final Icon chessIcon;

    //volume and sound
    private double volume = 0.5;
    private static final AudioClip MOVE_SOUND = new AudioClip(Board.class.getResource("/Sounds/Move.wav").toString());
    private static final AudioClip CLICK_SOUND = new AudioClip(Board.class.getResource("/Sounds/Click.wav").toString());
    private static final AudioClip ERROR_SOUND = new AudioClip(Board.class.getResource("/Sounds/Error.wav").toString());

    //if access is true, this board can be modified by the user, otherwise the board is inactive
    private boolean access = true;
    //if paint is true, update screen, otherwise dont
    private boolean paint = true;

    //chess board and gameplay
    private final Grid grid;
    private final ArrayList<Piece> pieces = new ArrayList<>(ChessConstants.NUMBER_OF_PIECES);
    private boolean turn = true;
    private final ArrayList<Tile> actionTiles = new ArrayList<>(ChessConstants.INITAL_NUMBER_OF_POSITIONS);

    //history tracker
    private final History history;
    private final ArrayList<String> whiteMoves = new ArrayList<>(50);
    private final ArrayList<String> blackMoves = new ArrayList<>(50);

    //tile Colors
    public static final Color MOVE = Color.BLUE;
    public static final Color ATTACK = Color.RED;
    public static final Color CASTLE = Color.GREEN;
    public static final Color EN_PASSANT = Color.MAGENTA;
    public static final Color SELECTED = Color.YELLOW;

    //state displayer, shows turn, checks, mates and stalemates
    private final StatusDisplayer display;

    //computer
    private volatile boolean computerThinking = false;
    private final AI whiteComputer = AI.getWhiteAI();
    private final AI blackComputer = AI.getBlackAI();

    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public Board(Chess parentFrame, Icon icon, History logger, StatusDisplayer displayer, int x, int y, int length) {
        super(null);
        super.setToolTipText("Chess Board");
        super.setBounds(x, y, length, length);
        super.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        parentFrame.setTitle((parentTitle = (parent = parentFrame).getTitle()) + " - Player vs Player (Local)");
        chessIcon = icon;
        history = logger;
        display = displayer;

        grid = new Grid(0, 0, length /= 8, length);

        pieces.add(new King(7, 4, true));
        pieces.add(new King(0, 4, false));

        pieces.add(new Rook(7, 0, true));
        pieces.add(new Knight(7, 1, true));
        pieces.add(new Bishop(7, 2, true));
        pieces.add(new Queen(7, 3, true));
        pieces.add(new Bishop(7, 5, true));
        pieces.add(new Knight(7, 6, true));
        pieces.add(new Rook(7, 7, true));

        pieces.add(new Pawn(6, 0, true));
        pieces.add(new Pawn(6, 1, true));
        pieces.add(new Pawn(6, 2, true));
        pieces.add(new Pawn(6, 3, true));
        pieces.add(new Pawn(6, 4, true));
        pieces.add(new Pawn(6, 5, true));
        pieces.add(new Pawn(6, 6, true));
        pieces.add(new Pawn(6, 7, true));

        pieces.add(new Rook(0, 0, false));
        pieces.add(new Knight(0, 1, false));
        pieces.add(new Bishop(0, 2, false));
        pieces.add(new Queen(0, 3, false));
        pieces.add(new Bishop(0, 5, false));
        pieces.add(new Knight(0, 6, false));
        pieces.add(new Rook(0, 7, false));

        pieces.add(new Pawn(1, 0, false));
        pieces.add(new Pawn(1, 1, false));
        pieces.add(new Pawn(1, 2, false));
        pieces.add(new Pawn(1, 3, false));
        pieces.add(new Pawn(1, 4, false));
        pieces.add(new Pawn(1, 5, false));
        pieces.add(new Pawn(1, 6, false));
        pieces.add(new Pawn(1, 7, false));

        for (int index = 0; index != ChessConstants.NUMBER_OF_PIECES; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }

        for (int index = 0; index != ChessConstants.NUMBER_OF_PIECES; ++index) {
            pieces.get(index).setProtectedTiles(grid);
        }

        if (PERFT) {
            for (int depth = 0; depth <= 5; ++depth) {
                System.out.println("GUI Piece Perft(" + depth + "): " + Perft.perft(grid, depth, false));
            }
        }

        MouseTracker tracker = new MouseTracker();
        super.addMouseListener(tracker);
        super.addMouseMotionListener(tracker);
        super.setFocusable(true);
        Thread thread = new Thread(this, "Graphics Update Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public int getState() {
        return gameMode;
    }

    public void setState(int state) {
        gameMode = state;
        parent.setTitle(parentTitle + " - " + getGameMode());
    }

    public String getGameMode() {
        switch (gameMode) {
            case LOCAL: {
                return "Player vs Player (Local)";
            }
            case SERVER: {
                return "Player (Host-White) vs Player (Client-Black)";
            }
            case CLIENT: {
                return "Player (Client-Black) vs Player (Host-White)";
            }
            case COMPUTER_VS_COMPUTER: {
                return whiteComputer.getDifficulty() + " AI (White) vs " + blackComputer.getDifficulty() + " AI (Black)";
            }
            case WHITE_PLAYER_VS_BLACK_COMPUTER: {
                return "Player (White) vs " + blackComputer.getDifficulty() + " AI (Black)";
            }
            case BLACK_PLAYER_VS_WHITE_COMPUTER: {
                return "Player (Black) vs " + whiteComputer.getDifficulty() + " AI (White)";
            }
        }
        return null;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setAccessable(boolean access) {
        this.access = access;
    }

    @Override
    public final Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public final Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public final Dimension getMinimumSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public final Dimension getMaximumSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public AI getWhiteComputer() {
        return whiteComputer;
    }

    public AI getBlackComputer() {
        return blackComputer;
    }

    public void restart(int gameMode) {
        AI.resetInfoDialog();
        output = null;
        turn = true;
        paint = access = computerThinking = false;
        actionTiles.clear();
        whiteMoves.clear();
        blackMoves.clear();
        setState(gameMode);
        pieces.clear();
        grid.clear();

        pieces.add(new King(7, 4, true));
        pieces.add(new King(0, 4, false));

        pieces.add(new Rook(7, 0, true));
        pieces.add(new Knight(7, 1, true));
        pieces.add(new Bishop(7, 2, true));
        pieces.add(new Queen(7, 3, true));
        pieces.add(new Bishop(7, 5, true));
        pieces.add(new Knight(7, 6, true));
        pieces.add(new Rook(7, 7, true));

        pieces.add(new Pawn(6, 0, true));
        pieces.add(new Pawn(6, 1, true));
        pieces.add(new Pawn(6, 2, true));
        pieces.add(new Pawn(6, 3, true));
        pieces.add(new Pawn(6, 4, true));
        pieces.add(new Pawn(6, 5, true));
        pieces.add(new Pawn(6, 6, true));
        pieces.add(new Pawn(6, 7, true));

        pieces.add(new Rook(0, 0, false));
        pieces.add(new Knight(0, 1, false));
        pieces.add(new Bishop(0, 2, false));
        pieces.add(new Queen(0, 3, false));
        pieces.add(new Bishop(0, 5, false));
        pieces.add(new Knight(0, 6, false));
        pieces.add(new Rook(0, 7, false));

        pieces.add(new Pawn(1, 0, false));
        pieces.add(new Pawn(1, 1, false));
        pieces.add(new Pawn(1, 2, false));
        pieces.add(new Pawn(1, 3, false));
        pieces.add(new Pawn(1, 4, false));
        pieces.add(new Pawn(1, 5, false));
        pieces.add(new Pawn(1, 6, false));
        pieces.add(new Pawn(1, 7, false));

        /*
        //endgame enpassant victory, probubly buggy, check movecounts
        {
            pieces.clear();

            pieces.add(new Rook(0, 0, 0, 0, 0, 2, true));
            pieces.add(new Rook(0, 0, 0, 0, 0, 6, true));
            pieces.add(new King(0, 0, 0, 0, 1, 7, false));

            pieces.add(new Pawn(0, 0, 0, 0, 2, 7, false));
            pieces.add(new Pawn(0, 0, 0, 0, 2, 0, false));
            pieces.add(new Pawn(0, 0, 0, 0, 3, 6, false));
            pieces.add(new Pawn(0, 0, 0, 0, 3, 7, true));
            pieces.add(new King(0, 0, 0, 0, 6, 7, true));
        }
        
        //former error position
        {
            pieces.clear();

            pieces.add(new Rook(0, 0, false));
            pieces.add(new Bishop(0, 2, false));
            pieces.add(new Queen(0, 3, false));
            pieces.add(new King(0, 4, false));
            pieces.add(new Bishop(0, 5, false));
            pieces.add(new Knight(0, 6, false));
            pieces.add(new Rook(0, 7, false));
            pieces.add(new Pawn(1, 0, false));
            pieces.add(new Pawn(1, 1, false));
            pieces.add(new Pawn(1, 2, false));
            pieces.add(new Pawn(1, 3, false));
            pieces.add(new Pawn(1, 5, false));
            pieces.add(new Pawn(1, 6, false));
            pieces.add(new Pawn(1, 7, false));
            pieces.add(new Knight(2, 2, false));
            pieces.add(new Pawn(3, 4, false));
            pieces.add(new Pawn(4, 4, true));
            pieces.add(new Pawn(5, 3, true));
            pieces.add(new Pawn(6, 0, true));
            pieces.add(new Pawn(6, 1, true));
            pieces.add(new Pawn(6, 2, true));
            pieces.add(new King(6, 4, true));
            pieces.add(new Pawn(6, 5, true));
            pieces.add(new Pawn(6, 6, true));
            pieces.add(new Pawn(6, 7, true));
            pieces.add(new Rook(7, 0, true));
            pieces.add(new Knight(7, 1, true));
            pieces.add(new Bishop(7, 2, true));
            pieces.add(new Queen(7, 3, true));
            pieces.add(new Knight(7, 6, true));
            pieces.add(new Rook(7, 7, true));
        }
        
        //movecounts accounted for
        {
            pieces.clear();
            
            pieces.add(new Rook(0, 0, false));
            pieces.add(new Knight(0, 1, false));
            pieces.add(new Bishop(0, 2, false));
            pieces.add(new Queen(0, 3, false));
            pieces.add(new King(0, 5, false));
            pieces.get(4).increaseMoveCount();
            pieces.add(new Rook(0, 7, false));

            pieces.add(new Pawn(1, 0, false));
            pieces.add(new Pawn(1, 1, false));
            Pawn aboutToBePromoted = new Pawn(1, 3, true);
            aboutToBePromoted.increaseMoveCount();
            pieces.add(aboutToBePromoted);
            pieces.add(new Bishop(1, 4, false));
            pieces.add(new Pawn(1, 5, false));
            pieces.add(new Pawn(1, 6, false));
            pieces.add(new Pawn(1, 7, false));

            Pawn blackPawnMovedOneTileDown = new Pawn(2, 2, false);
            blackPawnMovedOneTileDown.increaseMoveCount();
            pieces.add(blackPawnMovedOneTileDown);

            pieces.add(new Bishop(4, 2, true));

            pieces.add(new Pawn(6, 0, true));
            pieces.add(new Pawn(6, 1, true));
            pieces.add(new Pawn(6, 2, true));
            pieces.add(new Knight(6, 4, true));
            pieces.add(new Knight(6, 5, false));
            pieces.add(new Pawn(6, 6, true));
            pieces.add(new Pawn(6, 7, true));

            pieces.add(new Rook(7, 0, true));
            pieces.add(new Knight(7, 1, true));
            pieces.add(new Bishop(7, 2, true));
            pieces.add(new Queen(7, 3, true));
            pieces.add(new King(7, 4, true));
            pieces.add(new Rook(7, 7, true));
        }
         */
        for (int index = 0, numberOfPieces = pieces.size(); index != numberOfPieces; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }

        updateProtections();
        repaint();

        if (PERFT) {
            for (int depth = 0; depth <= 4; ++depth) {
                System.out.println("GUI Piece Perft(" + depth + "): " + Perft.perft(grid, depth, false));
            }
        }

        paint = access = true;
        history.setData(whiteMoves, blackMoves);
    }

    private void updateProtections() {
        grid.setProtections(pieces);
    }

    private boolean whiteChecked() {
        King whiteKing = Pieces.getWhiteKing(pieces);
        return whiteKing == null ? false : whiteKing.inCheck(grid);
    }

    private boolean blackChecked() {
        King blackKing = Pieces.getBlackKing(pieces);
        return blackKing == null ? false : blackKing.inCheck(grid);
    }

    private void clearTint() {
        for (int index = 0; index < LINEAR_LENGTH; ++index) {
            grid.getTile(index).setTint(null);
        }
    }

    public void setOutline(Color color) {
        super.setBorder(BorderFactory.createLineBorder((color == null) ? Color.BLACK : color));
        for (int index = 0; index < LINEAR_LENGTH; ++index) {
            grid.getTile(index).setOutline(color);
        }
    }

    public Color getOutline() {
        return grid.getTile(0).getOutline();
    }

    private BufferedImage offscreenBuffer;
    private Graphics2D offscreenGraphics;

    @Override
    protected void paintComponent(final Graphics window) {
        super.paintComponent(window);

        if (offscreenBuffer == null) {
            offscreenGraphics = (offscreenBuffer = (BufferedImage) (createImage(getWidth(), getHeight()))).createGraphics();
        }

        grid.render(offscreenGraphics);
        display.setTurnDisplay(turn);
        updateProtections(); //should be used when rendeing states in status display

        //Grid clonedGrid = new Grid(grid);
        //List<Piece> clonedPieces = Pieces.getDeepCopy(pieces);
        
        if (turn) {
            switch (Pieces.getWhiteState(grid, pieces, Pieces.getWhiteKing(pieces))) {
                case ChessConstants.CHECKED: {
                    display.setStateDisplay("White Checked");
                    break;
                }
                case ChessConstants.CHECKMATED: {
                    display.setStateDisplay("White Checkmated");
                    access = false;
                    break;
                }
                case ChessConstants.STALEMATED: {
                    display.setStateDisplay("White Stalemated");
                    access = false;
                    break;
                }
            }
        }
        else {
            switch (Pieces.getBlackState(grid, pieces, Pieces.getBlackKing(pieces))) {
                case ChessConstants.CHECKED: {
                    display.setStateDisplay("Black Checked");
                    break;
                }
                case ChessConstants.CHECKMATED: {
                    display.setStateDisplay("Black Checkmated");
                    access = false;
                    break;
                }
                case ChessConstants.STALEMATED: {
                    display.setStateDisplay("Black Stalemated");
                    access = false;
                    break;
                }
            }
        }

        /*
        if (!grid.equals(clonedGrid)) {
            System.err.println("Eh");
        }
        if (!pieces.equals(clonedPieces)) {
            System.err.println("NO");
        }
         */
        display.repaint();
        window.drawImage(offscreenBuffer, 0, 0, this);
    }

    public boolean computerThinking() {
        return computerThinking;
    }

    @Override
    public final void run() {
        try {
            for (;;) {
                if (paint) {
                    repaint();
                }
                switch (gameMode) {
                    case COMPUTER_VS_COMPUTER: {
                        if (access) {
                            computerThinking = true;
                            int numberOfPieces = pieces.size();
                            List<String> encoded = new ArrayList<>(numberOfPieces);
                            for (int index = 0; index < numberOfPieces; ++index) {
                                encoded.add(pieces.get(index).toEngineString());
                            }
                            if (turn) {
                                whiteComputer.makeMove(encoded);
                            }
                            else {
                                blackComputer.makeMove(encoded);
                            }
                            if (!computerThinking) {
                                AI.resetInfoDialog();
                                encoded.clear();
                                repaint();
                                continue;
                            }
                            if ((numberOfPieces = encoded.size()) == 0) {
                                computerThinking = false;
                                repaint();
                                continue;
                            }
                            pieces.clear();
                            grid.clear();
                            for (int index = 1; index != numberOfPieces; ++index) {
                                Piece readFromEngine = Piece.readEngineString(encoded.get(index));
                                grid.getTile(readFromEngine.getRow(), readFromEngine.getColumn()).setOccupant(readFromEngine);
                                pieces.add(readFromEngine);
                            }
                            MOVE_SOUND.play(volume);
                            updateProtections();
                            if (turn) {
                                whiteMoves.add(encoded.get(0));
                                turn = false;
                            }
                            else {
                                blackMoves.add(encoded.get(0));
                                turn = true;
                            }
                            history.setData(whiteMoves, blackMoves);
                            repaint();
                            encoded.clear();
                            computerThinking = false;
                        }
                        break;
                    }
                    case WHITE_PLAYER_VS_BLACK_COMPUTER: {
                        if (!turn && access) {
                            computerThinking = true;
                            int numberOfPieces = pieces.size();
                            List<String> encoded = new ArrayList<>(numberOfPieces);
                            for (int index = 0; index != numberOfPieces; ++index) {
                                encoded.add(pieces.get(index).toEngineString());
                            }
                            blackComputer.makeMove(encoded);
                            if (!computerThinking) {
                                AI.resetInfoDialog();
                                encoded.clear();
                                repaint();
                                continue;
                            }
                            if ((numberOfPieces = encoded.size()) == 0) {
                                computerThinking = false;
                                repaint();
                                continue;
                            }
                            pieces.clear();
                            grid.clear();
                            for (int index = 1; index != numberOfPieces; ++index) {
                                Piece readFromEngine = Piece.readEngineString(encoded.get(index));
                                grid.getTile(readFromEngine.getRow(), readFromEngine.getColumn()).setOccupant(readFromEngine);
                                pieces.add(readFromEngine);
                            }
                            MOVE_SOUND.play(volume);
                            updateProtections();
                            blackMoves.add(encoded.get(0));
                            turn = true;
                            history.setData(whiteMoves, blackMoves);
                            repaint();
                            encoded.clear();
                            computerThinking = false;
                        }
                        break;
                    }
                    case BLACK_PLAYER_VS_WHITE_COMPUTER: {
                        if (turn && access) {
                            computerThinking = true;
                            int numberOfPieces = pieces.size();
                            List<String> encoded = new ArrayList<>(numberOfPieces);
                            for (int index = 0; index != numberOfPieces; ++index) {
                                encoded.add(pieces.get(index).toEngineString());
                            }
                            whiteComputer.makeMove(encoded);
                            if (!computerThinking) {
                                AI.resetInfoDialog();
                                encoded.clear();
                                repaint();
                                continue;
                            }
                            if ((numberOfPieces = encoded.size()) == 0) {
                                computerThinking = false;
                                repaint();
                                continue;
                            }
                            pieces.clear();
                            grid.clear();
                            for (int index = 1; index != numberOfPieces; ++index) {
                                Piece readFromEngine = Piece.readEngineString(encoded.get(index));
                                grid.getTile(readFromEngine.getRow(), readFromEngine.getColumn()).setOccupant(readFromEngine);
                                pieces.add(readFromEngine);
                            }
                            MOVE_SOUND.play(volume);
                            updateProtections();
                            whiteMoves.add(encoded.get(0));
                            turn = false;
                            history.setData(whiteMoves, blackMoves);
                            repaint();
                            encoded.clear();
                            computerThinking = false;
                        }
                        break;
                    }
                }
                TimeUnit.MILLISECONDS.sleep(50);
            }
        }
        catch (InterruptedException ex) {
            String message = "The following error occurred: " + ex.getClass().getSimpleName() + "\n";
            StackTraceElement[] errors = ex.getStackTrace();
            for (int index = 0;; ++index) {
                StackTraceElement error = errors[index];
                if (index == (errors.length - 1)) {
                    message += "In " + error.getFileName() + " at line: " + error.getLineNumber() + " in method: " + Quotes.surroundWithDoubleQuotes(error.getMethodName());
                    break;
                }
                else {
                    message += "In " + error.getFileName() + " at line: " + error.getLineNumber() + " in method: " + Quotes.surroundWithDoubleQuotes(error.getMethodName()) + "\n";
                }
            }
            JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE, chessIcon);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Method that should be called immediately after White successfully
     * finishes his/her turn. If a Black Pawn made a double jump just 
     * before White's last turn, and has not been captured En Passant,
     * it is now permanently immune from 
     * being targeted by En Passant. 
     * 
     * @see checkBlackEnPassantRights()
     */
    private void checkWhiteEnPassantRights() {
        int count = 0;
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn() && piece.isBlack()) {
                if (piece.justMadeDoubleJump()) {
                    piece.setJustMadeDoubleJump(false);
                    ++count;
                }
            }
        }
        //this redundant check ensures that only 1 pawn is marked per move
        if (count > 1) {
            throw new Error();
        }
    }

    /**
     * Method that should be called immediately after Black successfully
     * finishes his/her turn. If a White Pawn made a double jump just 
     * before Black's last turn, and has not been captured
     * En Passant, it is now permanently immune from 
     * being targeted by En Passant. 
     * 
     * @see checkWhiteEnPassantRights()
     */
    private void checkBlackEnPassantRights() {
        int count = 0;
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn() && piece.isWhite()) {
                if (piece.justMadeDoubleJump()) {
                    piece.setJustMadeDoubleJump(false);
                    ++count;
                }
            }
        }
        //this redundant check ensures that only 1 pawn is marked per move
        if (count > 1) {
            throw new Error();
        }
    }

    private final class MouseTracker extends MouseAdapter {
        
        private MouseTracker() {
            
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            Point mouse = me.getPoint();
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                Tile tile = grid.getTile(index);
                if (tile.contains(mouse)) {
                    Piece occupant = tile.getOccupant();
                    setToolTipText((occupant != null) ? occupant.getName() + " at " + tile.getNotationLocation() + getMoveCountText(occupant.getMoveCount()) : tile.getNotationLocation());
                    return;
                }
            }
            setToolTipText("Chess Board");
        }

        private String getMoveCountText(int moveCount) {
            return (moveCount == 1) ? " has moved 1 time." : " has moved " + moveCount + " times.";
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (gameMode == COMPUTER_VS_COMPUTER || !access || SwingUtilities.isRightMouseButton(me)) {
                return;
            }

            /* //checks that AI & GUI generate same number of moves from a position
            {
                int numPieces = pieces.size();
                List<String> encoded = new ArrayList<>(numPieces);
                for (int index = 0; index < numPieces; ++index) {
                    encoded.add(pieces.get(index).toEngineString());
                }
                if (turn) {
                    whiteComputer.makeMove(encoded);
                }
                else {
                    blackComputer.makeMove(encoded);
                }
                System.out.println("Board: " + Perft.perft(grid, 1, !turn));
            }
             */

            Point mouse = me.getPoint();
            Piece chosen = Pieces.getSelected(pieces);
            int index;

            if (chosen != null) {
                for (index = (actionTiles.size() - 1); index >= 0; --index) {
                    Tile tile = actionTiles.get(index);
                    if (ATTACK.equals(tile.getTint()) && tile.getOccupant().isKing()) {
                        actionTiles.remove(index);
                        break;
                    }
                }
                final boolean whiteCheckedBefore = whiteChecked();
                final boolean blackCheckedBefore = blackChecked();
                final String previousLocation = chosen.getNotationLocation();
                boolean validMove = false;
                final int currentRow = chosen.getRow();
                final int currentColumn = chosen.getColumn();
                OuterLoop:
                for (index = 0; index != actionTiles.size(); ++index) {
                    Tile tile = actionTiles.get(index);
                    if (tile.contains(mouse)) {
                        Color tileColor = tile.getTint();
                        Tile previousTile = grid.getTile(currentRow, currentColumn);
                        if (CASTLE.equals(tileColor)) {
                            Tile leftRookTile = grid.getTile(currentRow, 0);
                            Tile rightRookTile = grid.getTile(currentRow, 7);
                            Tile newLeftRookTile = grid.getTile(currentRow, 3);
                            Tile newRightRookTile = grid.getTile(currentRow, 5);
                            Tile newLeftKingTile = grid.getTile(currentRow, 2);
                            Tile newRightKingTile = grid.getTile(currentRow, 6);
                            if (tile.sameLocation(newLeftKingTile)) {
                                Rook leftRook = (Rook) leftRookTile.getOccupant();
                                leftRookTile.removeOccupant();
                                previousTile.removeOccupant();
                                newLeftRookTile.setOccupant(leftRook);
                                leftRook.increaseMoveCount();
                                newLeftKingTile.setOccupant(chosen);
                                chosen.increaseMoveCount();
                                if (turn) {
                                    whiteMoves.add("White King castles left from " + previousLocation + " to " + chosen.getNotationLocation() + " and the White Rook at [1,A] has moved to [1,D]");
                                }
                                else {
                                    blackMoves.add("Black King castles left from " + previousLocation + " to " + chosen.getNotationLocation() + " and the Black Rook at [8,A] has moved to [8,D]");
                                }
                                validMove = true;
                                break;
                            }
                            else if (tile.sameLocation(newRightKingTile)) {
                                Rook rightRook = (Rook) rightRookTile.getOccupant();
                                rightRookTile.removeOccupant();
                                previousTile.removeOccupant();
                                newRightRookTile.setOccupant(rightRook);
                                rightRook.increaseMoveCount();
                                newRightKingTile.setOccupant(chosen);
                                chosen.increaseMoveCount();
                                if (turn) {
                                    whiteMoves.add("White King castles right from " + previousLocation + " to " + chosen.getNotationLocation() + " and the White Rook at [1,H] has moved to [1,F]");
                                }
                                else {
                                    blackMoves.add("Black King castles right from " + previousLocation + " to " + chosen.getNotationLocation() + " and the Black Rook at [8,H] has moved to [8,F]");
                                }
                                validMove = true;
                                break;
                            }
                        }
                        else if (EN_PASSANT.equals(tileColor)) {
                            Pawn pawn = (Pawn) chosen;
                            List<Tile> enPassant = pawn.getEnPassantTiles(grid);
                            Iterator<Tile> it = enPassant.iterator();
                            if (pawn.isWhite()) {
                                while (it.hasNext()) {
                                    Tile enPassantTile = it.next();
                                    if (enPassantTile.getColumn() == (pawn.getColumn() - 1) && tile.equals(enPassantTile)) {
                                        Tile blackPawnTile = grid.getTile(pawn.getRow(), pawn.getColumn() - 1);
                                        previousTile.removeOccupant();
                                        Pawn blackPawn = (Pawn) blackPawnTile.getOccupant();
                                        int removeIndex = pieces.indexOf(blackPawn);
                                        pieces.remove(removeIndex);
                                        blackPawnTile.removeOccupant();
                                        enPassantTile.setOccupant(pawn);
                                        updateProtections();
                                        if (whiteChecked()) {
                                            ERROR_SOUND.play(volume);
                                            if (whiteCheckedBefore) {
                                                blackPawnTile.setOccupant(blackPawn);
                                                previousTile.setOccupant(chosen);
                                                enPassantTile.removeOccupant();
                                                pieces.add(removeIndex, blackPawn);
                                                King whiteKing = Pieces.getWhiteKing(pieces);
                                                JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform en passant capture\non the Black Pawn at " + blackPawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would still be checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                                break OuterLoop;
                                            }
                                            blackPawnTile.setOccupant(blackPawn);
                                            previousTile.setOccupant(chosen);
                                            enPassantTile.removeOccupant();
                                            pieces.add(removeIndex, blackPawn);
                                            King whiteKing = Pieces.getWhiteKing(pieces);
                                            JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform en passant capture\non the Black Pawn at " + blackPawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                            break OuterLoop;
                                        }
                                        whiteMoves.add("White Pawn at " + previousLocation + " performed enpassant capture on the Black Pawn at " + blackPawn.getNotationLocation() + " and has moved from " + previousLocation + " to " + chosen.getNotationLocation());
                                        pawn.increaseMoveCount();
                                        validMove = true;
                                        break OuterLoop;
                                    }
                                    else if (enPassantTile.getColumn() == (pawn.getColumn() + 1) && tile.equals(enPassantTile)) {
                                        Tile blackPawnTile = grid.getTile(pawn.getRow(), pawn.getColumn() + 1);
                                        previousTile.removeOccupant();
                                        Pawn blackPawn = (Pawn) blackPawnTile.getOccupant();
                                        int removeIndex = pieces.indexOf(blackPawn);
                                        pieces.remove(removeIndex);
                                        blackPawnTile.removeOccupant();
                                        enPassantTile.setOccupant(pawn);
                                        updateProtections();
                                        if (whiteChecked()) {
                                            ERROR_SOUND.play(volume);
                                            if (whiteCheckedBefore) {
                                                blackPawnTile.setOccupant(blackPawn);
                                                previousTile.setOccupant(chosen);
                                                enPassantTile.removeOccupant();
                                                pieces.add(removeIndex, blackPawn);
                                                King whiteKing = Pieces.getWhiteKing(pieces);
                                                JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform en passant capture\non the Black Pawn at " + blackPawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would still be checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                                break OuterLoop;
                                            }
                                            blackPawnTile.setOccupant(blackPawn);
                                            previousTile.setOccupant(chosen);
                                            enPassantTile.removeOccupant();
                                            pieces.add(removeIndex, blackPawn);
                                            King whiteKing = Pieces.getWhiteKing(pieces);
                                            JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform en passant capture\non the Black Pawn at " + blackPawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                            break OuterLoop;
                                        }
                                        whiteMoves.add("White Pawn at " + previousLocation + " performed enpassant capture on the Black Pawn at " + blackPawn.getNotationLocation() + " and has moved from " + previousLocation + " to " + chosen.getNotationLocation());
                                        pawn.increaseMoveCount();
                                        validMove = true;
                                        break OuterLoop;
                                    }
                                }
                            }
                            else {
                                while (it.hasNext()) {
                                    Tile enPassantTile = it.next();
                                    if (enPassantTile.getColumn() == (pawn.getColumn() - 1) && tile.equals(enPassantTile)) {
                                        Tile whitePawnTile = grid.getTile(pawn.getRow(), pawn.getColumn() - 1);
                                        previousTile.removeOccupant();
                                        Pawn whitePawn = (Pawn) whitePawnTile.getOccupant();
                                        int removeIndex = pieces.indexOf(whitePawn);
                                        pieces.remove(removeIndex);
                                        whitePawnTile.removeOccupant();
                                        enPassantTile.setOccupant(pawn);
                                        updateProtections();
                                        if (blackChecked()) {
                                            ERROR_SOUND.play(volume);
                                            if (blackCheckedBefore) {
                                                whitePawnTile.setOccupant(whitePawn);
                                                previousTile.setOccupant(pawn);
                                                enPassantTile.removeOccupant();
                                                pieces.add(removeIndex, whitePawn);
                                                King blackKing = Pieces.getBlackKing(pieces);
                                                JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform enpassant capture\non the White Pawn at " + whitePawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would still be in check.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                                break OuterLoop;
                                            }
                                            whitePawnTile.setOccupant(whitePawn);
                                            previousTile.setOccupant(pawn);
                                            enPassantTile.removeOccupant();
                                            pieces.add(removeIndex, whitePawn);
                                            King blackKing = Pieces.getBlackKing(pieces);
                                            JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform enpassant capture\non the White Pawn at " + whitePawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                            break OuterLoop;
                                        }
                                        blackMoves.add("Black Pawn at " + previousLocation + " performed enpassant capture on the White Pawn at " + whitePawn.getNotationLocation() + " and has moved from " + previousLocation + " to " + chosen.getNotationLocation());
                                        pawn.increaseMoveCount();
                                        validMove = true;
                                        break OuterLoop;
                                    }
                                    else if (enPassantTile.getColumn() == (pawn.getColumn() + 1) && tile.equals(enPassantTile)) {
                                        Tile whitePawnTile = grid.getTile(pawn.getRow(), pawn.getColumn() + 1);
                                        previousTile.removeOccupant();
                                        Pawn whitePawn = (Pawn) whitePawnTile.getOccupant();
                                        int removeIndex = pieces.indexOf(whitePawn);
                                        pieces.remove(removeIndex);
                                        whitePawnTile.removeOccupant();
                                        enPassantTile.setOccupant(pawn);
                                        updateProtections();
                                        if (blackChecked()) {
                                            ERROR_SOUND.play(volume);
                                            if (blackCheckedBefore) {
                                                whitePawnTile.setOccupant(whitePawn);
                                                previousTile.setOccupant(pawn);
                                                enPassantTile.removeOccupant();
                                                pieces.add(removeIndex, whitePawn);
                                                King blackKing = Pieces.getBlackKing(pieces);
                                                JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform enpassant capture\non the White Pawn at " + whitePawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would still be in check.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                                break OuterLoop;
                                            }
                                            whitePawnTile.setOccupant(whitePawn);
                                            previousTile.setOccupant(pawn);
                                            enPassantTile.removeOccupant();
                                            pieces.add(removeIndex, whitePawn);
                                            King blackKing = Pieces.getBlackKing(pieces);
                                            JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot perform enpassant capture\non the White Pawn at " + whitePawn.getNotationLocation() + " and move to " + enPassantTile.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                            break OuterLoop;
                                        }
                                        blackMoves.add("Black Pawn at " + previousLocation + " performed enpassant capture on the White Pawn at " + whitePawn.getNotationLocation() + " and has moved from " + previousLocation + " to " + chosen.getNotationLocation());
                                        pawn.increaseMoveCount();
                                        validMove = true;
                                        break OuterLoop;
                                    }
                                }
                            }
                        }
                        else if (MOVE.equals(tileColor)) {
                            previousTile.removeOccupant();
                            tile.setOccupant(chosen);
                            updateProtections();
                            if (turn) {
                                if (whiteChecked()) {
                                    ERROR_SOUND.play(volume);
                                    if (whiteCheckedBefore) {
                                        previousTile.setOccupant(chosen);
                                        tile.removeOccupant();
                                        King whiteKing = Pieces.getWhiteKing(pieces);
                                        JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot move to " + tile.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would still be checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                        break;
                                    }
                                    previousTile.setOccupant(chosen);
                                    tile.removeOccupant();
                                    King whiteKing = Pieces.getWhiteKing(pieces);
                                    JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot move to " + tile.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                    break;
                                }
                            }
                            else if (blackChecked()) {
                                ERROR_SOUND.play(volume);
                                if (blackCheckedBefore) {
                                    previousTile.setOccupant(chosen);
                                    tile.removeOccupant();
                                    King blackKing = Pieces.getBlackKing(pieces);
                                    JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot move to " + tile.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would still be checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                    break;
                                }
                                previousTile.setOccupant(chosen);
                                tile.removeOccupant();
                                King blackKing = Pieces.getBlackKing(pieces);
                                JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot move to " + tile.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                break;
                            }
                            boolean promoted = false;
                            Piece converted;
                            if (chosen.isPawn()) {

                                //see if this pawn has made a double jump
                                //this is for enpassant purposes
                                if (!chosen.hasMoved()) {
                                    if (chosen.isWhite()) {
                                        //now check if the pawn has made a double jump
                                        if (chosen.getRow() == 4) {
                                            chosen.setJustMadeDoubleJump(true);
                                            System.out.println(chosen + " has just made a double jump!");
                                        }
                                    }
                                    else {
                                        if (chosen.getRow() == 3) {
                                            chosen.setJustMadeDoubleJump(true);
                                            System.out.println(chosen + " has just made a double jump!");
                                        }
                                    }
                                }

                                if (chosen.isWhite() && chosen.getRow() == 0) {
                                    clearTint();
                                    String[] choices = {"White Knight", "White Bishop", "White Rook", "White Queen"};
                                    String convertTo = (String) JOptionPane.showInputDialog(parent, "Note: Pawn promotion is mandatory and must be done to finish this move.\nSelect a piece type for this pawn to promote to:", "Promote Pawn", JOptionPane.QUESTION_MESSAGE, chessIcon, choices, null);
                                    if (convertTo != null) {
                                        converted = ((Pawn) chosen).promote(convertTo.substring(6));
                                        pieces.set(pieces.indexOf(chosen), converted);
                                        tile.setOccupant(converted);
                                        chosen = converted;
                                        promoted = true;
                                        whiteMoves.add("White Pawn at " + previousLocation + " has moved to " + converted.getNotationLocation() + " and has been promoted to a " + converted.getName());
                                    }
                                    else {
                                        previousTile.setOccupant(chosen);
                                        tile.removeOccupant();
                                        break;
                                    }
                                }
                                else if (chosen.isBlack() && chosen.getRow() == 7) {
                                    clearTint();
                                    String[] choices = {"Black Knight", "Black Bishop", "Black Rook", "Black Queen"};
                                    String convertTo = (String) JOptionPane.showInputDialog(parent, "Note: Pawn promotion is mandatory and must be done to finish this move.\nSelect a piece type for this pawn to promote to:", "Promote Pawn", JOptionPane.QUESTION_MESSAGE, chessIcon, choices, null);
                                    if (convertTo != null) {
                                        converted = ((Pawn) chosen).promote(convertTo.substring(6));
                                        pieces.set(pieces.indexOf(chosen), converted);
                                        tile.setOccupant(converted);
                                        chosen = converted;
                                        promoted = true;
                                        blackMoves.add("Black Pawn at " + previousLocation + " has moved to " + converted.getNotationLocation() + " and has been promoted to a " + converted.getName());
                                    }
                                    else {
                                        previousTile.setOccupant(chosen);
                                        tile.removeOccupant();
                                        break;
                                    }
                                }
                            }
                            if (!promoted) {
                                if (turn) {
                                    whiteMoves.add(chosen.getName() + " at " + previousLocation + " has moved to " + chosen.getNotationLocation());
                                }
                                else {
                                    blackMoves.add(chosen.getName() + " at " + previousLocation + " has moved to " + chosen.getNotationLocation());
                                }
                            }
                            chosen.increaseMoveCount();
                            validMove = true;
                            break;
                        }
                        else if (ATTACK.equals(tileColor)) {
                            Piece killed = tile.getOccupant();
                            tile.removeOccupant();
                            int removeIndex = pieces.indexOf(killed);
                            pieces.remove(removeIndex);
                            previousTile.removeOccupant();
                            tile.setOccupant(chosen);
                            updateProtections();
                            if (turn) {
                                if (whiteChecked()) {
                                    ERROR_SOUND.play(volume);
                                    if (whiteCheckedBefore) {
                                        previousTile.setOccupant(chosen);
                                        tile.setOccupant(killed);
                                        pieces.add(removeIndex, killed);
                                        King whiteKing = Pieces.getWhiteKing(pieces);
                                        JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot capture " + killed.getName() + " at " + killed.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would still be checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                        break;
                                    }
                                    previousTile.setOccupant(chosen);
                                    tile.setOccupant(killed);
                                    pieces.add(removeIndex, killed);
                                    King whiteKing = Pieces.getWhiteKing(pieces);
                                    JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot capture " + killed.getName() + " at " + killed.getNotationLocation() + "\nbecause White's King at " + whiteKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                    break;
                                }
                            }
                            else if (blackChecked()) {
                                ERROR_SOUND.play(volume);
                                if (blackCheckedBefore) {
                                    previousTile.setOccupant(chosen);
                                    tile.setOccupant(killed);
                                    pieces.add(removeIndex, killed);
                                    King blackKing = Pieces.getBlackKing(pieces);
                                    JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot capture " + killed.getName() + " at " + killed.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would still be checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                    break;
                                }
                                previousTile.setOccupant(chosen);
                                tile.setOccupant(killed);
                                pieces.add(removeIndex, killed);
                                King blackKing = Pieces.getBlackKing(pieces);
                                JOptionPane.showMessageDialog(parent, chosen.getName() + " at " + chosen.getNotationLocation() + " cannot capture " + killed.getName() + " at " + killed.getNotationLocation() + "\nbecause Black's King at " + blackKing.getNotationLocation() + " would become checked.", "Invalid Move", JOptionPane.ERROR_MESSAGE, chessIcon);
                                break;
                            }
                            boolean promoted = false;
                            Piece converted;
                            if (chosen.isPawn()) {
                                if (chosen.isWhite() && chosen.getRow() == 0) {
                                    clearTint();
                                    String[] choices = {"White Knight", "White Bishop", "White Rook", "White Queen"};
                                    String convertTo = (String) JOptionPane.showInputDialog(parent, "Note: Pawn promotion is mandatory and must be done to finish this move.\nSelect a piece type for this pawn to promote to:", "Promote Pawn", JOptionPane.QUESTION_MESSAGE, chessIcon, choices, null);
                                    if (convertTo != null) {
                                        converted = ((Pawn) chosen).promote(convertTo.substring(6));
                                        pieces.set(pieces.indexOf(chosen), converted);
                                        tile.setOccupant(converted);
                                        chosen = converted;
                                        promoted = true;
                                        whiteMoves.add("White Pawn at " + previousLocation + " has captured " + killed.getName() + " at " + converted.getNotationLocation() + " and has been promoted to a " + converted.getName());
                                    }
                                    else {
                                        previousTile.setOccupant(chosen);
                                        tile.setOccupant(killed);
                                        pieces.add(removeIndex, killed);
                                        break;
                                    }
                                }
                                else if (chosen.isBlack() && chosen.getRow() == 7) {
                                    clearTint();
                                    String[] choices = {"Black Knight", "Black Bishop", "Black Rook", "Black Queen"};
                                    String convertTo = (String) JOptionPane.showInputDialog(parent, "Note: Pawn promotion is mandatory and must be done to finish this move.\nSelect a piece type for this pawn to promote to:", "Promote Pawn", JOptionPane.QUESTION_MESSAGE, chessIcon, choices, null);
                                    if (convertTo != null) {
                                        converted = ((Pawn) chosen).promote(convertTo.substring(6));
                                        pieces.set(pieces.indexOf(chosen), converted);
                                        tile.setOccupant(converted);
                                        chosen = converted;
                                        promoted = true;
                                        blackMoves.add("Black Pawn at " + previousLocation + " has captured " + killed.getName() + " at " + converted.getNotationLocation() + " and has been promoted to a " + converted.getName());
                                    }
                                    else {
                                        previousTile.setOccupant(chosen);
                                        tile.setOccupant(killed);
                                        pieces.add(removeIndex, killed);
                                        break;
                                    }
                                }
                            }
                            if (!promoted) {
                                if (turn) {
                                    whiteMoves.add(chosen.getName() + " at " + previousLocation + " has captured " + killed.getName() + " at " + chosen.getNotationLocation());
                                }
                                else {
                                    blackMoves.add(chosen.getName() + " at " + previousLocation + " has captured " + killed.getName() + " at " + chosen.getNotationLocation());
                                }
                            }
                            chosen.increaseMoveCount();
                            validMove = true;
                            break;
                        }
                    }
                }
                chosen.setSelected(false);
                actionTiles.clear();
                for (index = 0; index < LINEAR_LENGTH; ++index) {
                    Tile tile = grid.getTile(index);
                    tile.setTint(null);
                    tile.removeProtections();
                }
                for (index = (pieces.size() - 1); index >= 0; --index) {
                    pieces.get(index).setProtectedTiles(grid);
                }
                if (validMove) {
                    if (turn) {
                        checkWhiteEnPassantRights();
                    }
                    else {
                        checkBlackEnPassantRights();
                    }
                    MOVE_SOUND.play(volume);
                    turn = !turn;
                    history.setData(whiteMoves, blackMoves);
                    if (output != null) {
                        output.println(DataProtector.encode(DATA + getDataLine()));
                    }
                }
                repaint();
                return;
            }
            if (turn) {
                if (gameMode == CLIENT) {
                    return;
                }
                int numberOfPieces = pieces.size();
                for (index = 0; index != numberOfPieces; ++index) {
                    pieces.get(index).setSelected(false);
                }
                for (index = 0; index != numberOfPieces; ++index) {
                    Piece piece = pieces.get(index);
                    Tile tile = grid.getTile(piece.getRow(), piece.getColumn());
                    if (tile.contains(mouse) && piece.isWhite()) {
                        piece.setSelected(true);
                        tile.setTint(SELECTED);
                        CLICK_SOUND.play(volume);
                        actionTiles.clear();
                        actionTiles.addAll(piece.getLegalMoves(grid));
                        return;
                    }
                }
            }
            else {
                if (gameMode == SERVER) {
                    return;
                }
                int numberOfPieces = pieces.size();
                for (index = 0; index != numberOfPieces; ++index) {
                    pieces.get(index).setSelected(false);
                }
                for (index = 0; index != numberOfPieces; ++index) {
                    Piece piece = pieces.get(index);
                    Tile tile = grid.getTile(piece.getRow(), piece.getColumn());
                    if (tile.contains(mouse) && piece.isBlack()) {
                        piece.setSelected(true);
                        tile.setTint(SELECTED);
                        CLICK_SOUND.play(volume);
                        actionTiles.clear();
                        actionTiles.addAll(piece.getLegalMoves(grid));
                        return;
                    }
                }
            }
        }
    }

    //note there is a small bug... when in AI vs AI mode, when a AI is about to checkmate 
    //the other, the last move string may not be saved when the user
    //saves a file while the winning AI is still thinking
    //becuase the AI returns its move string or the new board 
    //which would happen after the user has saved the file.
    public void saveFile(String fileName) {
        if (gameMode == SERVER || gameMode == CLIENT || output != null) {
            JOptionPane.showMessageDialog(parent, "Error: Cannot save any files while connected to a network game.", "Restricted", JOptionPane.ERROR_MESSAGE, chessIcon);
            return;
        }
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (JOptionPane.showConfirmDialog(parent, "The file: " + Quotes.surroundWithDoubleQuotes(fileName) + " already exists, do you wish to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, chessIcon) == JOptionPane.YES_OPTION) {
                try {
                    writeFile(fileName);
                    JOptionPane.showMessageDialog(parent, "The file: " + Quotes.surroundWithDoubleQuotes(fileName) + "\nwas overwritten successfully.", "Overwrite Successful", JOptionPane.INFORMATION_MESSAGE, chessIcon);
                }
                catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(parent, "Error: Could not open or find: " + Quotes.surroundWithDoubleQuotes(fileName), "File Not Found", JOptionPane.ERROR_MESSAGE, chessIcon);
                }
                catch (UnsupportedEncodingException ex) {
                    JOptionPane.showMessageDialog(parent, "Error: Could not encode UTF-8 to overwrite: " + Quotes.surroundWithDoubleQuotes(fileName), "Encoding Error", JOptionPane.ERROR_MESSAGE, chessIcon);
                }
            }
        }
        else if (JOptionPane.showConfirmDialog(parent, "Are you sure you want to save the new file: " + Quotes.surroundWithDoubleQuotes(fileName) + "?", "Save File?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, chessIcon) == JOptionPane.YES_OPTION) {
            try {
                writeFile(fileName);
                JOptionPane.showMessageDialog(parent, "The file: " + Quotes.surroundWithDoubleQuotes(fileName) + "\nwas saved successfully.", "Save Successful", JOptionPane.INFORMATION_MESSAGE, chessIcon);
            }
            catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(parent, "Error: Could not open or find: " + Quotes.surroundWithDoubleQuotes(fileName), "File Not Found", JOptionPane.ERROR_MESSAGE, chessIcon);
            }
            catch (UnsupportedEncodingException ex) {
                JOptionPane.showMessageDialog(parent, "Error: Could not encode UTF-8 to save: " + Quotes.surroundWithDoubleQuotes(fileName), "Encoding Error", JOptionPane.ERROR_MESSAGE, chessIcon);
            }
        }
    }

    private void writeFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(fileName, Constants.UTF_8);
        writer.println("Original Location: " + Quotes.surroundWithDoubleQuotes(fileName));
        writer.println(DataProtector.encode(Long.toString(System.currentTimeMillis())));
        writer.println(DataProtector.encode(new Date().toString()));
        for (Iterator<String> it = getDataList().iterator(); it.hasNext();) {
            String data = it.next();
            if (it.hasNext()) {
                writer.println(DataProtector.encode(data));
            }
            else {
                writer.print(DataProtector.encode(data));
                break;
            }
        }
        Constants.close(writer);
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public void readFile(String fileName) {
        if (gameMode == SERVER || gameMode == CLIENT || output != null) {
            JOptionPane.showMessageDialog(parent, "Error: Cannot open any files while connected to a network game.", "Restricted", JOptionPane.ERROR_MESSAGE, chessIcon);
            return;
        }
        long currentTimeInMillis = System.currentTimeMillis();
        List<String> currentData = getDataList();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), Constants.UTF_8));
            if (!("Original Location: " + Quotes.surroundWithDoubleQuotes(fileName)).equals(reader.readLine())) {
                JOptionPane.showMessageDialog(parent, "The file: " + Quotes.surroundWithDoubleQuotes(fileName) + " has a invalid heading.\nThis error may occur by modifying or moving the saved file.", "Heading Error", JOptionPane.ERROR_MESSAGE, chessIcon);
            }
            boolean showTimeError = false;
            if (Long.parseLong(DataProtector.decode(reader.readLine())) >= currentTimeInMillis) {
                showTimeError = true;
                JOptionPane.showMessageDialog(parent, "The file: " + Quotes.surroundWithDoubleQuotes(fileName) + " has an invalid date.\nThis error may occur by modifying the saved file or\nby a change in your system's date and time settings.", "Date Error", JOptionPane.ERROR_MESSAGE, chessIcon);
            }
            if (!new Date(currentTimeInMillis).after(new Date(DataProtector.decode(reader.readLine())))) {
                if (!showTimeError) {
                    JOptionPane.showMessageDialog(parent, "The file: " + Quotes.surroundWithDoubleQuotes(fileName) + " has an invalid date.\nThis error may occur by modifying the saved file or\nby a change in your system's date and time settings.", "Date Error", JOptionPane.ERROR_MESSAGE, chessIcon);
                }
            }
            //capacity should be very close
            List<String> savedDataList = new ArrayList<>(currentData.size());
            for (;;) {
                String read = reader.readLine();
                if (read == null) {
                    break;
                }
                savedDataList.add(DataProtector.decode(read));
            }
            Constants.close(reader);
            if (JOptionPane.showConfirmDialog(parent, "Are you sure you want to open the saved game in: " + Quotes.surroundWithDoubleQuotes(fileName) + "?", "Open Saved Game?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, chessIcon) == JOptionPane.YES_OPTION) {
                readDataList(savedDataList);
                JOptionPane.showMessageDialog(parent, "The saved game in: " + Quotes.surroundWithDoubleQuotes(fileName) + " was opened successfully.", "Open Successful", JOptionPane.INFORMATION_MESSAGE, chessIcon);
            }
        }
        catch (IOException ex) {
            Constants.close(reader);
            if (ex instanceof FileNotFoundException) {
                JOptionPane.showMessageDialog(parent, "Error: Could not open or find: " + Quotes.surroundWithDoubleQuotes(fileName) + ".", "File Not Found", JOptionPane.ERROR_MESSAGE, chessIcon);
                //the reader would have never been opened so of course it will never be closed properly
            }
            else {
                JOptionPane.showMessageDialog(parent, "Error: Could not use UTF-8 to read: " + Quotes.surroundWithDoubleQuotes(fileName) + ".", "Decoding Error", JOptionPane.ERROR_MESSAGE, chessIcon);
            }
        }
        catch (RuntimeException ex) {
            Constants.close(reader);
            ex.printStackTrace(System.err);
            readDataList(currentData);
            JOptionPane.showMessageDialog(parent, "Error: The file: " + Quotes.surroundWithDoubleQuotes(fileName) + " has an illegal format.", "Format Error", JOptionPane.ERROR_MESSAGE, chessIcon);
        }
    }

    /**
     * Sets the output stream of this {@link GUI.Board} instance.
     *
     * @param stream The new output stream.
     */
    public void setOutput(PrintStream stream) {
        output = null;
        output = stream;
    }

    private List<String> getDataList() {
        final int numberOfPieces = pieces.size();
        final int numberOfWhiteMoves = whiteMoves.size();
        final int numberOfBlackMoves = blackMoves.size();
        List<String> list = new ArrayList<>(7 + numberOfPieces + numberOfWhiteMoves + numberOfBlackMoves);
        list.add(Integer.toString(gameMode));
        switch (gameMode) {
            case COMPUTER_VS_COMPUTER: {
                list.add(Integer.toString(whiteComputer.getSearchTime()));
                list.add(Integer.toString(blackComputer.getSearchTime()));
                list.add(Boolean.toString(access));
                list.add(Boolean.toString(turn));
                list.add(Integer.toString(numberOfPieces));
                for (int index = 0; index != numberOfPieces; ++index) {
                    list.add(pieces.get(index).toOutputString());
                }
                list.add(Integer.toString(numberOfWhiteMoves));
                for (int index = 0; index != numberOfWhiteMoves; ++index) {
                    list.add(whiteMoves.get(index));
                }
                list.add(Integer.toString(numberOfBlackMoves));
                for (int index = 0; index != numberOfBlackMoves; ++index) {
                    list.add(blackMoves.get(index));
                }
                Map<String, Integer> whiteComputerSelectedPositions = whiteComputer.getSelectedPositions();
                list.add(Integer.toString(whiteComputerSelectedPositions.size()));
                Iterator<String> it = whiteComputerSelectedPositions.keySet().iterator();
                while (it.hasNext()) {
                    String position = it.next();
                    list.add(position + DataProtector.SEPERATOR + whiteComputerSelectedPositions.get(position));
                }
                Map<String, Integer> blackComputerSelectedPositions = blackComputer.getSelectedPositions();
                list.add(Integer.toString(blackComputerSelectedPositions.size()));
                for (it = blackComputerSelectedPositions.keySet().iterator(); it.hasNext();) {
                    String position = it.next();
                    list.add(position + DataProtector.SEPERATOR + blackComputerSelectedPositions.get(position));
                }
                return list;
            }
            case WHITE_PLAYER_VS_BLACK_COMPUTER: {
                list.add(Integer.toString(blackComputer.getSearchTime()));
                list.add(computerThinking ? "true" : Boolean.toString(access));
                list.add(Boolean.toString(turn));
                list.add(Integer.toString(numberOfPieces));
                for (int index = 0; index != numberOfPieces; ++index) {
                    list.add(pieces.get(index).toOutputString());
                }
                list.add(Integer.toString(numberOfWhiteMoves));
                for (int index = 0; index != numberOfWhiteMoves; ++index) {
                    list.add(whiteMoves.get(index));
                }
                list.add(Integer.toString(numberOfBlackMoves));
                for (int index = 0; index != numberOfBlackMoves; ++index) {
                    list.add(blackMoves.get(index));
                }
                Map<String, Integer> blackComputerSelectedPositions = blackComputer.getSelectedPositions();
                list.add(Integer.toString(blackComputerSelectedPositions.size()));
                Iterator<String> it = blackComputerSelectedPositions.keySet().iterator();
                while (it.hasNext()) {
                    String position = it.next();
                    list.add(position + DataProtector.SEPERATOR + blackComputerSelectedPositions.get(position));
                }
                return list;
            }
            case BLACK_PLAYER_VS_WHITE_COMPUTER: {
                list.add(Integer.toString(whiteComputer.getSearchTime()));
                list.add(computerThinking ? "true" : Boolean.toString(access));
                list.add(Boolean.toString(turn));
                list.add(Integer.toString(numberOfPieces));
                for (int index = 0; index != numberOfPieces; ++index) {
                    list.add(pieces.get(index).toOutputString());
                }
                list.add(Integer.toString(numberOfWhiteMoves));
                for (int index = 0; index != numberOfWhiteMoves; ++index) {
                    list.add(whiteMoves.get(index));
                }
                list.add(Integer.toString(numberOfBlackMoves));
                for (int index = 0; index != numberOfBlackMoves; ++index) {
                    list.add(blackMoves.get(index));
                }
                Map<String, Integer> whiteComputerSelectedPositions = whiteComputer.getSelectedPositions();
                list.add(Integer.toString(whiteComputerSelectedPositions.size()));
                Iterator<String> it = whiteComputerSelectedPositions.keySet().iterator();
                while (it.hasNext()) {
                    String position = it.next();
                    list.add(position + DataProtector.SEPERATOR + whiteComputerSelectedPositions.get(position));
                }
                return list;
            }
        }
        list.add(Boolean.toString(access));
        list.add(Boolean.toString(turn));
        list.add(Integer.toString(numberOfPieces));
        for (int index = 0; index != numberOfPieces; ++index) {
            list.add(pieces.get(index).toOutputString());
        }
        list.add(Integer.toString(numberOfWhiteMoves));
        for (int index = 0; index != numberOfWhiteMoves; ++index) {
            list.add(whiteMoves.get(index));
        }
        list.add(Integer.toString(numberOfBlackMoves));
        for (int index = 0; index != numberOfBlackMoves; ++index) {
            list.add(blackMoves.get(index));
        }
        return list;
    }

    private void readDataList(List<String> list) {
        AI.resetInfoDialog();
        paint = access = computerThinking = false;
        pieces.clear();
        grid.clear();
        actionTiles.clear();
        whiteMoves.clear();
        blackMoves.clear();
        final int savedGameMode = Integer.parseInt(list.get(0));
        switch (savedGameMode) {
            case COMPUTER_VS_COMPUTER: {
                whiteComputer.setSearchTime(Integer.parseInt(list.get(1)));
                blackComputer.setSearchTime(Integer.parseInt(list.get(2)));
                setState(savedGameMode);
                turn = Boolean.parseBoolean(list.get(4));
                int cursor = 6;
                int numberOfSavedPieces = Integer.parseInt(list.get(5));
                while (numberOfSavedPieces-- > 0) {
                    Piece read = Piece.readPiece(list.get(cursor++));
                    pieces.add(read);
                    grid.getTile(read.getRow(), read.getColumn()).setOccupant(read);
                }
                for (int index = (pieces.size() - 1); index >= 0; --index) {
                    pieces.get(index).setProtectedTiles(grid);
                }
                int numberOfSavedWhiteMoves = Integer.parseInt(list.get(cursor++));
                whiteMoves.ensureCapacity(numberOfSavedWhiteMoves);
                while (numberOfSavedWhiteMoves-- > 0) {
                    whiteMoves.add(list.get(cursor++));
                }
                int numberOfSavedBlackMoves = Integer.parseInt(list.get(cursor++));
                blackMoves.ensureCapacity(numberOfSavedBlackMoves);
                while (numberOfSavedBlackMoves-- > 0) {
                    blackMoves.add(list.get(cursor++));
                }
                history.setData(whiteMoves, blackMoves);
                Map<String, Integer> whiteComputerSelectedPositions = whiteComputer.getSelectedPositions();
                whiteComputerSelectedPositions.clear();
                int numberOfSavedWhiteComputerSelectedPositions = Integer.parseInt(list.get(cursor++));
                while (numberOfSavedWhiteComputerSelectedPositions-- > 0) {
                    String[] entry = list.get(cursor++).split(DataProtector.SEPERATOR);
                    whiteComputerSelectedPositions.put(entry[0], Integer.parseInt(entry[1]));
                }
                Map<String, Integer> blackComputerSelectedPositions = blackComputer.getSelectedPositions();
                blackComputerSelectedPositions.clear();
                int numberOfSavedBlackComputerSelectedPositions = Integer.parseInt(list.get(cursor++));
                while (numberOfSavedBlackComputerSelectedPositions-- > 0) {
                    String[] entry = list.get(cursor++).split(DataProtector.SEPERATOR);
                    blackComputerSelectedPositions.put(entry[0], Integer.parseInt(entry[1]));
                }
                repaint();
                paint = true;
                access = Boolean.parseBoolean(list.get(3)); //enable access last
                return;
            }
            case WHITE_PLAYER_VS_BLACK_COMPUTER: {
                blackComputer.setSearchTime(Integer.parseInt(list.get(1)));
                setState(savedGameMode);
                turn = Boolean.parseBoolean(list.get(3));
                int cursor = 5;
                int numberOfSavedPieces = Integer.parseInt(list.get(4));
                while (numberOfSavedPieces-- > 0) {
                    Piece read = Piece.readPiece(list.get(cursor++));
                    pieces.add(read);
                    grid.getTile(read.getRow(), read.getColumn()).setOccupant(read);
                }
                for (int index = (pieces.size() - 1); index >= 0; --index) {
                    pieces.get(index).setProtectedTiles(grid);
                }
                int numberOfSavedWhiteMoves = Integer.parseInt(list.get(cursor++));
                whiteMoves.ensureCapacity(numberOfSavedWhiteMoves);
                while (numberOfSavedWhiteMoves-- > 0) {
                    whiteMoves.add(list.get(cursor++));
                }
                int numberOfSavedBlackMoves = Integer.parseInt(list.get(cursor++));
                blackMoves.ensureCapacity(numberOfSavedBlackMoves);
                while (numberOfSavedBlackMoves-- > 0) {
                    blackMoves.add(list.get(cursor++));
                }
                history.setData(whiteMoves, blackMoves);
                Map<String, Integer> blackComputerSelectedPositions = blackComputer.getSelectedPositions();
                blackComputerSelectedPositions.clear();
                int numberOfSavedBlackComputerSelectedPositions = Integer.parseInt(list.get(cursor++));
                while (numberOfSavedBlackComputerSelectedPositions-- > 0) {
                    String[] entry = list.get(cursor++).split(DataProtector.SEPERATOR);
                    blackComputerSelectedPositions.put(entry[0], Integer.parseInt(entry[1]));
                }
                repaint();
                paint = true;
                access = Boolean.parseBoolean(list.get(2));
                return;
            }
            case BLACK_PLAYER_VS_WHITE_COMPUTER: {
                whiteComputer.setSearchTime(Integer.parseInt(list.get(1)));
                setState(savedGameMode);
                turn = Boolean.parseBoolean(list.get(3));
                int cursor = 5;
                int numberOfSavedPieces = Integer.parseInt(list.get(4));
                while (numberOfSavedPieces-- > 0) {
                    Piece read = Piece.readPiece(list.get(cursor++));
                    pieces.add(read);
                    grid.getTile(read.getRow(), read.getColumn()).setOccupant(read);
                }
                for (int index = (pieces.size() - 1); index >= 0; --index) {
                    pieces.get(index).setProtectedTiles(grid);
                }
                int numberOfSavedWhiteMoves = Integer.parseInt(list.get(cursor++));
                whiteMoves.ensureCapacity(numberOfSavedWhiteMoves);
                while (numberOfSavedWhiteMoves-- > 0) {
                    whiteMoves.add(list.get(cursor++));
                }
                int numberOfSavedBlackMoves = Integer.parseInt(list.get(cursor++));
                blackMoves.ensureCapacity(numberOfSavedBlackMoves);
                while (numberOfSavedBlackMoves-- > 0) {
                    blackMoves.add(list.get(cursor++));
                }
                history.setData(whiteMoves, blackMoves);
                Map<String, Integer> whiteComputerSelectedPositions = whiteComputer.getSelectedPositions();
                whiteComputerSelectedPositions.clear();
                int numberOfSavedWhiteComputerSelectedPositions = Integer.parseInt(list.get(cursor++));
                while (numberOfSavedWhiteComputerSelectedPositions-- > 0) {
                    String[] entry = list.get(cursor++).split(DataProtector.SEPERATOR);
                    whiteComputerSelectedPositions.put(entry[0], Integer.parseInt(entry[1]));
                }
                repaint();
                paint = true;
                access = Boolean.parseBoolean(list.get(2));
                return;
            }
        }
        setState(savedGameMode);
        turn = Boolean.parseBoolean(list.get(1));
        pieces.clear();
        grid.clear();
        int cursor = 4;
        int numberOfSavedPieces = Integer.parseInt(list.get(3));
        while (numberOfSavedPieces-- > 0) {
            Piece read = Piece.readPiece(list.get(cursor++));
            pieces.add(read);
            grid.getTile(read.getRow(), read.getColumn()).setOccupant(read);
        }
        for (int index = (pieces.size() - 1); index >= 0; --index) {
            pieces.get(index).setProtectedTiles(grid);
        }
        int numberOfSavedWhiteMoves = Integer.parseInt(list.get(cursor++));
        whiteMoves.ensureCapacity(numberOfSavedWhiteMoves);
        while (numberOfSavedWhiteMoves-- > 0) {
            whiteMoves.add(list.get(cursor++));
        }
        int numberOfSavedBlackMoves = Integer.parseInt(list.get(cursor++));
        blackMoves.ensureCapacity(numberOfSavedBlackMoves);
        while (numberOfSavedBlackMoves-- > 0) {
            blackMoves.add(list.get(cursor++));
        }
        history.setData(whiteMoves, blackMoves);
        repaint();
        paint = true;
        access = Boolean.parseBoolean(list.get(2));
    }

    //a fast buffer used in client-server to send data quickly
    private StringBuilder buffer;

    //data to send in client-server, not thread-safe since it uses an external variable
    private String getDataLine() {
        if (buffer == null) { //initialize on first use
            buffer = new StringBuilder(500);
        }
        else {
            buffer.setLength(0);
        }
        for (Iterator<String> it = getDataList().iterator(); it.hasNext();) {
            buffer.append(it.next()); //this statement cannot be placed in the "update statement" part of the loop
            //since that part is executed last
            if (it.hasNext()) {
                buffer.append(DataProtector.SEPERATOR);
            }
            else {
                break;
            }
        }
        return buffer.toString();
    }

    //data to recieve in client-server
    public void readDataLine(String data) {
        AI.resetInfoDialog();
        paint = access = computerThinking = false;
        actionTiles.clear();
        whiteMoves.clear();
        blackMoves.clear();
        if (output != null) { //incoming data from other player.
            MOVE_SOUND.play(volume);
        }
        String[] split = data.split(DataProtector.SEPERATOR);
        turn = Boolean.parseBoolean(split[2]);
        pieces.clear();
        grid.clear();
        int cursor = 4;
        int numberOfSavedPieces = Integer.parseInt(split[3]);
        while (numberOfSavedPieces-- > 0) {
            Piece read = Piece.readPiece(split[cursor++]);
            pieces.add(read);
            grid.getTile(read.getRow(), read.getColumn()).setOccupant(read);
        }
        for (int index = (pieces.size() - 1); index >= 0; --index) {
            pieces.get(index).setProtectedTiles(grid);
        }
        int numberOfSavedWhiteMoves = Integer.parseInt(split[cursor++]);
        whiteMoves.ensureCapacity(numberOfSavedWhiteMoves);
        while (numberOfSavedWhiteMoves-- > 0) {
            whiteMoves.add(split[cursor++]);
        }
        int numberOfSavedBlackMoves = Integer.parseInt(split[cursor++]);
        blackMoves.ensureCapacity(numberOfSavedBlackMoves);
        while (numberOfSavedBlackMoves-- > 0) {
            blackMoves.add(split[cursor++]);
        }
        history.setData(whiteMoves, blackMoves);
        repaint();
        paint = true;
        access = Boolean.parseBoolean(split[1]);
    }
}