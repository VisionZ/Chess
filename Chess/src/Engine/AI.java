package Engine;

import static Engine.EvaluationConstants.CHECKMATE_VALUE;
import static Engine.PieceConverter.PIECE_CONVERTER;
import static Engine.Pieces.checkBlackEnPassantRightsSlow;
import static Engine.Pieces.checkWhiteEnPassantRightsSlow;
import GUI.Chess;
import Util.ChessConstants;
import Util.Constants;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import Util.ImageUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * NOTE: Parallel Processing play may be worse than Normal play.
 *
 * NOTE: ENPASSANT IS ENABLED. I may change my logic to only consider search
 * results only if they all finish a particular depth. If some searches at a
 * depth do not finish, they WILL adversely affect the selection of moves from
 * the AI, thus this CANNOT be countered by the argument that such deeper
 * searches yield more data. This scenario pops up often after using parallel
 * processing.
 *
 * Bug, Non-parallel processing with depth extension (aka default mode) having
 * trouble finding checkmate, most likely located in this class, as parallel
 * processing does it just fine. This bug seems to be resolved as of 4/30/17 by
 * just sorting the possible positions and selecting the one at the top.
 *
 * Could make parallel search better by conducting a single threaded search to
 * depth 3, then sorting and starting parallel searching. However, there may be
 * no need since each position is searched at the same time.
 *
 * Although making sure all results are of the same depth is critical, if a
 * branch detects a checkmate, we should return that branch immediately. This
 * has been done.
 */
public final class AI {

    //NOTE THAT WHEN USING CONCURRENT EXECUTORS, TESTING WILL FAIL!!!
    //THIS IS BECAUSE STATIC SEARCH METHODS ARE BEING ACCESSED CONCURRENTLY
    //EVEN THOUGH THEY ARE NOT THREAD SAFE!!! 
    //SOLUTION: MAKE THE STATIC SEARCH METHODS SYNCHRONIZED
    //number of parallel threads to use, minimum
    private static final int CORE_THREADS = Constants.RUNTIME.availableProcessors() + 1;

    //used for parallel processing
    //https://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing
    private static final BlockingQueue<Runnable> TASK_QUEUE = new LinkedTransferQueue<Runnable>() {

        @Override
        public final boolean offer(Runnable task) {
            //http://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing?rq=1
            return tryTransfer(task);
        }
    };

    //https://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing
    @SuppressWarnings("Convert2Lambda")
    private static final RejectedExecutionHandler HANDLER = new RejectedExecutionHandler() {
        @Override
        public final void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(task);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
                Thread.currentThread().interrupt();
            }
        }
    };

    //stores positions and their values, in both white and black's persepctive.
    static final Database BLACK_STORE = new Database();
    static final Database WHITE_STORE = new Database();

    //test mode flag
    private static final boolean CHECK_MODE = true;

    //non-private for Pieces and Grid to allocate ArrayList memory
    static int NUMBER_OF_WHITE_PIECES;
    static int NUMBER_OF_BLACK_PIECES;
    static int NUMBER_OF_PIECES;

    //difficulty
    public static final String[] DIFFICULTY = {"Novice", "Student", "Good", "Strong", "Master", "Grand Master"};
    public static final int MIN_DIFFICULTY = 0;
    public static final int MAX_DIFFICULTY = DIFFICULTY.length - 1;

    //maximum time allocated (in seconds) to search per difficulty level
    //first 3 depths have unlimited time since they are shallow and must be finished
    //each value must be unique for proper indexing
    public static final int[] SEARCH_TIMES = {100, 1000, 10000, 100000, 45, 1000000};

    //how far to search, depending on the difficulty
    //These search depths are actually 1 more than shown here, since they
    //do not include the inital moves generated to choose from, which counts
    //as search depth 1
    private static final int[] SEARCH_DEPTHS = {2, 3, 4, 5, 100, 200};

    //test depth used for check mode
    private static final int TEST_DEPTH = 1;

    //images corresponding to the AI difficulty
    private static final BufferedImage[] WHITE_IMAGES = new BufferedImage[DIFFICULTY.length];
    private static final BufferedImage[] BLACK_IMAGES = new BufferedImage[DIFFICULTY.length];

    //load images in static initalizer, invoked by classloader
    static {
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            BufferedImage image = ImageUtils.readImage(DIFFICULTY[index], ImageUtils.PNG);
            WHITE_IMAGES[index] = ImageUtils.tintImage(image, Color.WHITE, 0.5f);
            BLACK_IMAGES[index] = ImageUtils.tintImage(image, Color.BLACK, 0.5f);
        }
    }

    //current AI image rendering in the status dialog
    //updated per call to makeMove
    private static BufferedImage IMAGE;

    //global search timer used by the status dialog
    //and by static search classes for timing
    //updated per call to makeMove
    static SearchTimer TIMER;

    //used when CHECK_MODE = true
    static final SearchTimer TEST_TIMER = new SearchTimer(POSITIVE_INFINITY, "Test Timer");

    //show AI status data to user
    static StatusDialog DIALOG;

    //only 2 instances allowed
    private static final AI WHITE_AI = new AI(true, SEARCH_TIMES[0]);
    private static final AI BLACK_AI = new AI(false, SEARCH_TIMES[0]);

    public static final AI getWhiteAI() {
        return WHITE_AI;
    }

    public static final AI getBlackAI() {
        return BLACK_AI;
    }

    //AI image of the current AI
    private BufferedImage image;

    //search timer of the current AI
    private final SearchTimer timer;

    //color, true indicates white and false indicates black
    private final boolean side;

    //input chess board and pieces
    private final Grid grid = new Grid();
    private final List<Piece> pieces = new ArrayList<>(ChessConstants.NUMBER_OF_PIECES);

    //possible positions to select from
    private final List<PositionHolder> possiblePositions = new ArrayList<>(ChessConstants.INITAL_NUMBER_OF_POSITIONS);

    //table of chosen positions and the number of times each position was chosen
    //used to avoid threefold repetition
    private final Map<String, Integer> selectedPositions = new HashMap<>();

    //parallel processing capability, disabled by default
    private boolean parallel = false;

    //max depth to search, depends on difficulty
    private int maxDepth;

    AI(boolean color, int searchTime) {
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            if (searchTime == SEARCH_TIMES[index]) {
                if (side = color) { //nice trick hehe... we assign side and read its value at the same time
                    timer = new SearchTimer(searchTime, "White AI Timer");
                    image = WHITE_IMAGES[index];
                }
                else {
                    timer = new SearchTimer(searchTime, "Black AI Timer");
                    image = BLACK_IMAGES[index];
                }
                maxDepth = SEARCH_DEPTHS[index];
                return;
            }
        }
        throw new IllegalArgumentException("Invalid Search Time: " + searchTime);
    }

    public SearchTimer getTimer() {
        return timer;
    }

    public static final void resetInfoDialog() {
        if (DIALOG != null) {
            DIALOG.setVisible(false);
        }
        //whenever a game is being restarted while an AI is actively
        //searching, the following code will terminate the AI search instantly
        //since this method is called every time the user restarts
        WHITE_AI.timer.stopTiming();
        BLACK_AI.timer.stopTiming();
    }

    public void setSearchTime(int searchTime) {
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            if (searchTime == SEARCH_TIMES[index]) {
                timer.setCountdownTime(searchTime);
                image = side ? WHITE_IMAGES[index] : BLACK_IMAGES[index];
                maxDepth = SEARCH_DEPTHS[index];
                return;
            }
        }
        throw new IllegalArgumentException("Invalid Search Time: " + searchTime);
    }

    public int getSearchTime() {
        return timer.getCountdownTime();
    }

    public String getDifficulty() {
        int searchTime = timer.getCountdownTime();
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            if (searchTime == SEARCH_TIMES[index]) {
                return DIFFICULTY[index];
            }
        }
        throw new IllegalStateException();
    }

    public void setSelectedPositions(Map<String, Integer> savedSelectedPositions) {
        selectedPositions.clear();
        selectedPositions.putAll(savedSelectedPositions);
    }

    public Map<String, Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public boolean isParallelProcessing() {
        return parallel;
    }

    public void useParallelProcessing(boolean enable) {
        parallel = enable;
    }

    static final class PositionHolder implements Comparable<PositionHolder> {

        final Grid grid;
        final List<Piece> whites;
        final List<Piece> blacks;

        private final Grid clonedGrid;
        private final List<Piece> clonedWhites;
        private final List<Piece> clonedBlacks;

        private final String description;
        private int value = NEGATIVE_INFINITY;

        private PositionHolder(Grid copiedGrid, String moveInfo) {
            List<Piece> pieces = (grid = copiedGrid).getPieces();
            Pieces.sort(pieces);
            clonedGrid = new Grid(grid);
            clonedWhites = Pieces.getDeepCopy(whites = Pieces.getWhite(pieces));
            clonedBlacks = Pieces.getDeepCopy(blacks = Pieces.getBlack(pieces));
            //most valuable pieces appear first
            //AlphaBetaBlack loops backwards from Pawns to Kings
            
            for (Piece piece : clonedWhites) {
                System.out.println(piece.getName() + " " + piece.getIndex());
            }
            System.out.println();
            for (Piece piece : clonedBlacks) {
                System.out.println(piece.getName() + " " + piece.getIndex());
            }
            System.out.println("====================");
            
            description = moveInfo;
        }

        @Override
        public final int compareTo(PositionHolder other) {
            return value - other.value;
        }

        @Override
        public final String toString() {
            return description + " Score: " + value;
        }
    }

    @SuppressWarnings("Convert2Lambda")
    private static final Comparator<PositionHolder> HIGHEST_VALUE_POSITION_FIRST = new Comparator<PositionHolder>() {
        @Override
        public final int compare(PositionHolder first, PositionHolder next) {
            return next.compareTo(first);
        }
    };

    @SuppressWarnings("Convert2Lambda")
    private static final Comparator<PositionHolder> HIGHEST_VALUE_POSITION_LAST = new Comparator<PositionHolder>() {
        @Override
        public final int compare(PositionHolder first, PositionHolder next) {
            return first.compareTo(next);
        }
    };

    private final class ParallelSearch implements Callable<Integer> {

        private final PositionHolder position;
        private final ArrayList<Integer> results = new ArrayList<>(6);

        private ParallelSearch(PositionHolder holder) {
            position = holder;
        }

        @Override
        public final Integer call() {
            Board board = new Board(position.grid, position.whites, position.blacks);
            if (timer.getCountdownTime() == SEARCH_TIMES[SEARCH_TIMES.length - 1])  {
                System.out.println("GRAND MASTER");
                if (side) {
                    results.add(AlphaBetaWhite.min(board, 6)); //guaranteed to finish
                    System.out.println("FINISHED DEPTH 6");
                    for (int searchDepth = 7; searchDepth <= maxDepth; ++searchDepth) {
                        if (timer.timeElapsed() >= 60) {
                            //After all threads have finished to depth 6, if more than
                            //a minute has elapsed, stop all threads.
                            timer.setOverride(true);
                            return 0;
                        }
                        int result = AlphaBetaWhite.min(board, searchDepth);
                        if (result <= -CHECKMATE_VALUE) {
                            int inflate = 20;
                            results.ensureCapacity(searchDepth + inflate);
                            results.add(result);
                            for (--result; inflate >= 0; --inflate, --result) {
                                results.add(result);
                            }
                            return 0;
                        }
                        if (timer.timeOver()) {
                            return 0;
                        }
                        results.add(result);
                    }
                }
                else {
                    results.add(AlphaBetaBlack.min(board, 6)); //guaranteed to finish
                    for (int searchDepth = 7; searchDepth <= maxDepth; ++searchDepth) {
                        if (timer.timeElapsed() >= 60) {
                            return 0;
                        }
                        int result = AlphaBetaBlack.min(board, searchDepth);
                        if (result <= -CHECKMATE_VALUE) {
                            int inflate = 20;
                            results.ensureCapacity(searchDepth + inflate);
                            results.add(result);
                            for (--result; inflate >= 0; --inflate, --result) {
                                results.add(result);
                            }
                            return 0;
                        }
                        if (timer.timeOver()) {
                            return 0;
                        }
                        results.add(result);
                    }
                }
            }
            if (side) {
                results.add(AlphaBetaWhite.min(board, 1)); //guarantee list size of 1
                for (int searchDepth = 2; searchDepth <= maxDepth; ++searchDepth) {
                    int result = AlphaBetaWhite.min(board, searchDepth);
                    if (result <= -CHECKMATE_VALUE) {
                        //If we detect a position where we lose, we need to inflate the size of
                        //of the results list. This will ensure that the minimum depth chosen 
                        //to select moves is not prematurely lower than it should be
                        
                        //also we can stop wasting CPU resources on a position
                        //whose outcome we already know
                        int inflate = 20;
                        results.ensureCapacity(searchDepth + inflate);
                        results.add(result);
                        for (--result; inflate >= 0; --inflate, --result) {
                            results.add(result);
                        }
                        return 0;
                    }
                    if (timer.timeOver()) {
                        return 0;
                    }
                    results.add(result);
                }
            }
            else {
                results.add(AlphaBetaBlack.min(board, 1)); //guarantee list size of 1
                for (int searchDepth = 2; searchDepth <= maxDepth; ++searchDepth) {
                    int result = AlphaBetaBlack.min(board, searchDepth);
                    if (result <= -CHECKMATE_VALUE) {
                        //If we detect a position where we lose, we need to inflate the size of
                        //of the results list. This will ensure that the minimum depth chosen 
                        //to select moves is not prematurely lower than it should be

                        //also we can stop wasting CPU resources on a position
                        //whose outcome we already know
                        int inflate = 20;
                        results.ensureCapacity(searchDepth + inflate);
                        results.add(result);
                        for (--result; inflate >= 0; --inflate, --result) {
                            results.add(result);
                        }
                        return 0;
                    }
                    if (timer.timeOver()) {
                        return 0;
                    }
                    results.add(result);
                }
            }
            return 0;
        }

        private int getMaxDepth() {
            return results.size();
        }

        private int getScore(int index) {
            return results.get(index);
        }

        @Override
        public final String toString() {
            return "Depth: " + results.size() + " Results: " + results.toString();
        }
    }

    @SuppressWarnings({"Convert2Lambda", "UnusedAssignment"})
    public synchronized void makeMove(List<String> list) {
        System.out.println();
        IMAGE = image;

        if (DIALOG == null) {
            //Chess chess = Chess.getInstance();
            //GUI_BOARD = chess.getChild();
            //DIALOG = new StatusDialog(chess);
            DIALOG = new StatusDialog(Chess.getInstance());
            //must init dialog here to avoid circular initalization in chess -> board -> computer -> chess
        }

        DIALOG.setTitle(parallel
                ? (side ? "White AI - (Parallel)" : "Black AI - (Parallel)")
                : (side ? "White AI - (Normal)" : "Black AI - (Normal)"));

        if (CHECK_MODE) {
            if (!side) {
                //Evaluators.MAIN = Evaluators.STANDARD;
            }
            else {
                //Evaluators.MAIN = Evaluators.POWERFUL;
                //The Powerful Evaluator is strong enough to overcome
                //the standard evaluator despite search 1 depth less for example
            }
        }

        NUMBER_OF_WHITE_PIECES = NUMBER_OF_BLACK_PIECES = 0;
        NUMBER_OF_PIECES = list.size();

        //read pieces and place them on the grid
        for (int index = 0; index != NUMBER_OF_PIECES; ++index) {
            Piece piece = PIECE_CONVERTER.convertForward(list.get(index));
            if (piece.isWhite()) {
                ++NUMBER_OF_WHITE_PIECES;
                Pieces.WHITES.add(piece);
            }
            else {
                ++NUMBER_OF_BLACK_PIECES;
                Pieces.BLACKS.add(piece);
            }
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }

        //sort pieces
        Pieces.WHITES.sort(Pieces.BEST_PIECES_FIRST);
        Pieces.BLACKS.sort(Pieces.BEST_PIECES_LAST);
        for (int index = 0; index != NUMBER_OF_WHITE_PIECES; ++index) {
            Piece white = Pieces.WHITES.get(index);
            white.setProtectedTiles(grid);
            pieces.add(white);
        }
        for (int index = 0; index != NUMBER_OF_BLACK_PIECES; ++index) {
            Piece black = Pieces.BLACKS.get(index);
            black.setProtectedTiles(grid);
            pieces.add(black);
        }
        Pieces.WHITES.clear();
        Pieces.BLACKS.clear();

        //For Debugging:
        final List<Piece> clonedPieces = Pieces.getDeepCopy(pieces);
        final Grid clonedGrid = new Grid(grid);

        if (CHECK_MODE) {
            Tester.checkEvaluators(grid);
        }

        if (side) {
            System.out.println("White AI Playing");
            //white move generation
            {
                final List<Piece> whites = Pieces.getWhite(pieces);
                final King whiteKing = Pieces.getWhiteKing(whites);
                {
                    Tile previousTile = grid.getTile(whiteKing.getRow(), whiteKing.getColumn());
                    List<Tile> castleTiles = whiteKing.getCastleTiles(grid);
                    for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                        Tile kingCastleTile = castleTiles.get(index);
                        if (kingCastleTile.getColumn() == ChessConstants.LEFT_KING_CASTLE_COLUMN) {
                            Tile leftRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 0);
                            Tile leftRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.LEFT_ROOK_CASTLE_COLUMN);
                            Piece leftRook = leftRookTile.getOccupant();

                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            kingCastleTile.setOccupant(whiteKing);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);

                            whiteKing.increaseMoveCount();
                            leftRook.increaseMoveCount();
                            {
                                Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                {
                                    possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCastleText(whiteKing, previousTile, leftRook, leftRookTile)));
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            whiteKing.decreaseMoveCount();
                            leftRook.decreaseMoveCount();

                            previousTile.setOccupant(whiteKing);
                            leftRookTile.setOccupant(leftRook);
                            kingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile rightRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 7);
                            Tile rightRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                            Piece rightRook = rightRookTile.getOccupant();

                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            kingCastleTile.setOccupant(whiteKing);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);

                            whiteKing.increaseMoveCount();
                            rightRook.increaseMoveCount();
                            {
                                Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                {
                                    possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCastleText(whiteKing, previousTile, rightRook, rightRookTile)));
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            whiteKing.decreaseMoveCount();
                            rightRook.decreaseMoveCount();

                            previousTile.setOccupant(whiteKing);
                            rightRookTile.setOccupant(rightRook);
                            kingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_WHITE_PIECES; ++pieceIndex) {
                    final Piece white = whites.get(pieceIndex);
                    final int previousRow = white.getRow();
                    final int previousColumn = white.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    List<Tile> attackTiles = white.getAttackTiles(grid);
                    for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                        Tile attackTile = attackTiles.get(index);
                        Piece enemy = attackTile.getOccupant();
                        if (enemy.isKing()) {
                            System.out.println("Enemy King targeted!");
                            continue;
                        }
                        if (white.isPawn() && previousRow == 1) {
                            //Queen replace = Pawn.promote(white);
                            for (Piece replace : Pawn.getPromoted(white)) {
                                previousTile.removeOccupant();
                                attackTile.setOccupant(replace);
                                int pawnIndex = pieces.indexOf(white);
                                pieces.set(pawnIndex, replace);
                                int removeIndex = Pieces.remove(pieces, enemy);
                                grid.setProtections(pieces);
                                if (!whiteKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    {
                                        Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCapturePromotedText(white, previousTile, replace, enemy)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                }
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                pieces.add(removeIndex, enemy);
                                pieces.set(pawnIndex, white);
                                grid.setProtections(pieces);
                            }
                        }
                        else {
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                {
                                    Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                    {
                                        possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCaptureText(white, previousTile, enemy)));
                                    }
                                    if (pawn != null) {
                                        pawn.setJustMadeDoubleJump(true);
                                    }
                                }
                                white.decreaseMoveCount();
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            pieces.add(removeIndex, enemy);
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                    if (white.isPawn()) {
                        {
                            Tile enPassantTile = white.getLeftEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile blackPawnTile = grid.getTile(previousRow, previousColumn - 1);
                                Piece blackPawn = blackPawnTile.getOccupant();
                                previousTile.removeOccupant();
                                blackPawnTile.removeOccupant();
                                enPassantTile.setOccupant(white);
                                int removeIndex = Pieces.remove(pieces, blackPawn);
                                grid.setProtections(pieces);
                                if (!whiteKing.inCheck(grid)) {
                                    white.increaseMoveCount();
                                    {
                                        Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getEnPassantText(white, previousTile, blackPawn)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                    white.decreaseMoveCount();
                                }
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, blackPawn);
                                grid.setProtections(pieces);
                            }
                        }
                        {
                            Tile enPassantTile = white.getRightEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                                Piece blackPawn = blackPawnTile.getOccupant();
                                previousTile.removeOccupant();
                                blackPawnTile.removeOccupant();
                                enPassantTile.setOccupant(white);
                                int removeIndex = Pieces.remove(pieces, blackPawn);
                                grid.setProtections(pieces);
                                if (!whiteKing.inCheck(grid)) {
                                    white.increaseMoveCount();
                                    {
                                        Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getEnPassantText(white, previousTile, blackPawn)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                    white.decreaseMoveCount();
                                }
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, blackPawn);
                                grid.setProtections(pieces);
                            }
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_WHITE_PIECES; ++pieceIndex) {
                    final Piece white = whites.get(pieceIndex);
                    final int previousRow = white.getRow();
                    final int previousColumn = white.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    final Iterator<Tile> moveTiles = white.getMoveTiles(grid);
                    while (moveTiles.hasNext()) {
                        Tile moveTile = moveTiles.next();
                        if (white.isPawn() && previousRow == 1) {
                            //Queen replace = Pawn.promote(white);
                            for (Piece replace : Pawn.getPromoted(white)) {
                                previousTile.removeOccupant();
                                moveTile.setOccupant(replace);
                                int pawnIndex = pieces.indexOf(white);
                                pieces.set(pawnIndex, replace);
                                grid.setProtections(pieces);
                                if (!whiteKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    {
                                        Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getPromotedMoveText(white, previousTile, replace)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                }
                                previousTile.setOccupant(white);
                                moveTile.removeOccupant();
                                pieces.set(pawnIndex, white);
                                grid.setProtections(pieces);
                            }
                        }
                        else {
                            previousTile.removeOccupant();
                            moveTile.setOccupant(white);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                boolean madeDoubleJump = false;
                                if (white.isPawn() && !white.hasMoved()) {
                                    if (white.getRow() == 4) {
                                        white.setJustMadeDoubleJump(madeDoubleJump = true);
                                    }
                                }
                                white.increaseMoveCount();
                                {
                                    Piece pawn = checkWhiteEnPassantRightsSlow(pieces);
                                    {
                                        possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getMoveText(white, previousTile)));
                                    }
                                    if (pawn != null) {
                                        pawn.setJustMadeDoubleJump(true);
                                    }
                                }
                                white.decreaseMoveCount();
                                if (madeDoubleJump) {
                                    white.setJustMadeDoubleJump(false);
                                }
                            }
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }
                Tester.check(grid, clonedGrid, pieces, clonedPieces);
            }

            //show info dialog
            DIALOG.setVisible(true);

            //start timing
            (TIMER = timer).startTiming();

            list.clear();

            final int numberOfPositions = possiblePositions.size();

            if (numberOfPositions != Tester.perft(grid, 1, !side)) {
                throw new InternalError();
            }

            System.out.println("Number of positions from given position: " + numberOfPositions);

            if (numberOfPositions == 0) {
                DIALOG.disableUpdate();
                DIALOG.setTitle(side ? "White AI - (Done)" : "Black AI - (Done)");
                timer.stopTiming();

                //clear previous grids and pieces
                grid.clear();
                pieces.clear();
                //System.out.println("Cannot make a move.");
                return;
            }

            //update the dialog if not game over
            DIALOG.reset();
            DIALOG.setTotalMoves(numberOfPositions);

            PositionHolder bestPosition = possiblePositions.get(0);

            if (parallel) {
                ParallelSearch[] parallelSearches = new ParallelSearch[numberOfPositions];
                Future[] futures = new Future[numberOfPositions];

                //https://stackoverflow.com/questions/10116502/java-threading-optimization-at-100-cpu-usage
                //ExecutorService executor = Executors.newFixedThreadPool(PROCESSORS + 1);
                //https://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing
                //sometimes there are more core threads than positions!
                //so we must make sure core pool size is <= numberOfPositions
                int corePoolSize = CORE_THREADS > numberOfPositions ? numberOfPositions : CORE_THREADS;
                ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, numberOfPositions, 0L, TimeUnit.MILLISECONDS, TASK_QUEUE, HANDLER);

                for (int index = 0; index != numberOfPositions; ++index) {
                    futures[index] = executor.submit(parallelSearches[index] = new ParallelSearch(possiblePositions.get(index)));
                    //start all parallel tasks at the same time
                }
                executor.shutdown();
                try {
                    for (int index = 0; index != numberOfPositions; ++index) {
                        if (((Future<Integer>) futures[index]).get() != 0) { //wait for all parallel tasks to finish.
                            throw new Error("All future results should be zero.");
                        }
                        futures[index] = null;
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
                int shallowestDepth = parallelSearches[0].getMaxDepth();
                System.out.println(parallelSearches[0]);
                for (int index = 1; index != numberOfPositions; ++index) {
                    int depthsCovered = parallelSearches[index].getMaxDepth();
                    if (depthsCovered < shallowestDepth) {
                        shallowestDepth = depthsCovered;
                    }
                    System.out.println(parallelSearches[index]);
                }
                System.out.println("Parallel Processing Shallowest Depth: " + shallowestDepth);
                DIALOG.setFinishedDepth(shallowestDepth--);
                //this is the size of the list, we will need the last index later

                for (int index = 0; index != numberOfPositions; ++index) {
                    possiblePositions.get(index).value = parallelSearches[index].getScore(shallowestDepth);
                    parallelSearches[index] = null;
                }
                possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
                System.out.println(bestPosition);
                parallelSearches = null;
                futures = null;
                executor = null;
                TASK_QUEUE.clear();
            }
            else {
                int previousIterationTime = NEGATIVE_INFINITY;
                //array of previous values
                int[] previousValues = new int[numberOfPositions];
                Iterative_Deepening:
                for (int searchDepth = 1; searchDepth <= maxDepth; ++searchDepth) {
                    //before attempting to search at this particular depth, check time 
                    final int depthStartTime = timer.timeElapsed();
                    //if time remaining is less than the time it took for the previous depth
                    if (previousIterationTime != NEGATIVE_INFINITY && (timer.getCountdownTime() - depthStartTime) <= previousIterationTime) {
                        //it is pretty much impossible to search to a greater depth
                        //in the remaining amount of time so terminate search
                        break;
                    }
                    for (int index = 0; index != numberOfPositions; ++index) {
                        previousValues[index] = possiblePositions.get(index).value;
                    }
                    boolean checkmate = false;
                    for (int positionIndex = 0; positionIndex != numberOfPositions; ++positionIndex) {
                        PositionHolder position = possiblePositions.get(positionIndex);
                        int result = AlphaBetaWhite.min(new Board(position.grid, position.whites, position.blacks), searchDepth);
                        if (timer.timeOver()) {
                            //should time run out before all searches are completed
                            //at the current depth, reset all positional values to
                            //the previous depth iteration
                            for (int index = 0; index != numberOfPositions; ++index) {
                                possiblePositions.get(index).value = previousValues[index];
                            }
                            break Iterative_Deepening;
                        }
                        position.value = result;
                        DIALOG.increaseMovesCompleted();
                    }
                    //at this point the current depth has been fully searched
                    //so now we sort the higher scoring positions to look at first
                    //and find the best position, which is the first after sorting
                    possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                    DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
                    DIALOG.setFinishedDepth(searchDepth);
                    previousIterationTime = timer.timeElapsed() - depthStartTime;
                    DIALOG.setMovesCompleted(0);
                    System.out.println("Depth: " + searchDepth + " Time Taken: " + previousIterationTime);
                    if (checkmate) {
                        break;
                    }
                }
                System.out.println();
                previousValues = null;
            }

            {
                //maybe search for a better position only when losing
                final String encoded = Pieces.encode(bestPosition.whites, bestPosition.blacks);
                if (!selectedPositions.containsKey(encoded)) {
                    selectedPositions.put(encoded, 1);
                }
                else {
                    final int timesSelected = selectedPositions.get(encoded);
                    if (timesSelected >= 2) {
                        if (numberOfPositions >= 2) {
                            possiblePositions.sort(HIGHEST_VALUE_POSITION_LAST);
                            boolean successorFound = false;
                            for (int index = (numberOfPositions - 2); index >= 0; --index) {
                                PositionHolder nextBestPosition = possiblePositions.get(index);
                                String nextBestPositionEncoded = Pieces.encode(nextBestPosition.whites, nextBestPosition.blacks);
                                if (!selectedPositions.containsKey(nextBestPositionEncoded)) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 1);
                                    successorFound = true;
                                    break;
                                }
                                if (selectedPositions.get(nextBestPositionEncoded) == 1) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 2);
                                    successorFound = true;
                                    break;
                                }
                            }
                            if (!successorFound) {
                                selectedPositions.put(encoded, timesSelected + 1);
                            }
                        }
                        else {
                            selectedPositions.put(encoded, timesSelected + 1);
                        }
                    }
                    else {
                        selectedPositions.put(encoded, timesSelected + 1);
                    }
                }
            }

            check();

            list.add(bestPosition.description);
            for (int index = 0, size = bestPosition.whites.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.whites.get(index)));
            }
            for (int index = 0, size = bestPosition.blacks.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.blacks.get(index)));
            }
        }
        else {
            System.out.println("Black AI Playing");
            //black move generation
            {
                final List<Piece> blacks = Pieces.getBlack(pieces);
                final King blackKing = Pieces.getBlackKing(blacks);
                {
                    Tile previousTile = grid.getTile(blackKing.getRow(), blackKing.getColumn());
                    List<Tile> castleTiles = blackKing.getCastleTiles(grid);
                    for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                        Tile kingCastleTile = castleTiles.get(index);
                        if (kingCastleTile.getColumn() == ChessConstants.LEFT_KING_CASTLE_COLUMN) {
                            Tile leftRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 0);
                            Tile leftRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.LEFT_ROOK_CASTLE_COLUMN);
                            Piece leftRook = leftRookTile.getOccupant();

                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            kingCastleTile.setOccupant(blackKing);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);

                            blackKing.increaseMoveCount();
                            leftRook.increaseMoveCount();
                            {
                                Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                {
                                    possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCastleText(blackKing, previousTile, leftRook, leftRookTile)));
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            blackKing.decreaseMoveCount();
                            leftRook.decreaseMoveCount();

                            previousTile.setOccupant(blackKing);
                            leftRookTile.setOccupant(leftRook);
                            kingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile rightRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 7);
                            Tile rightRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                            Piece rightRook = rightRookTile.getOccupant();

                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            kingCastleTile.setOccupant(blackKing);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);

                            blackKing.increaseMoveCount();
                            rightRook.increaseMoveCount();
                            {
                                Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                {
                                    possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCastleText(blackKing, previousTile, rightRook, rightRookTile)));
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            blackKing.decreaseMoveCount();
                            rightRook.decreaseMoveCount();

                            previousTile.setOccupant(blackKing);
                            rightRookTile.setOccupant(rightRook);
                            kingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_BLACK_PIECES; ++pieceIndex) {
                    final Piece black = blacks.get(pieceIndex);
                    final int previousRow = black.getRow();
                    final int previousColumn = black.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    List<Tile> attackTiles = black.getAttackTiles(grid);
                    for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                        Tile attackTile = attackTiles.get(index);
                        Piece enemy = attackTile.getOccupant();
                        if (enemy.isKing()) {
                            System.out.println("Enemy King targeted!");
                            continue;
                        }
                        if (black.isPawn() && previousRow == 6) {
                            //Queen replace = Pawn.promote(black);
                            for (Piece replace : Pawn.getPromoted(black)) {
                                previousTile.removeOccupant();
                                attackTile.setOccupant(replace);
                                int pawnIndex = pieces.indexOf(black);
                                pieces.set(pawnIndex, replace);
                                int removeIndex = Pieces.remove(pieces, enemy);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    {
                                        Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCapturePromotedText(black, previousTile, replace, enemy)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                }
                                previousTile.setOccupant(black);
                                attackTile.setOccupant(enemy);
                                pieces.add(removeIndex, enemy);
                                pieces.set(pawnIndex, black);
                                grid.setProtections(pieces);
                            }
                        }
                        else {
                            previousTile.removeOccupant();
                            attackTile.setOccupant(black);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                black.increaseMoveCount();
                                {
                                    Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                    {
                                        possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getCaptureText(black, previousTile, enemy)));
                                    }
                                    if (pawn != null) {
                                        pawn.setJustMadeDoubleJump(true);
                                    }
                                }
                                black.decreaseMoveCount();
                            }
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            pieces.add(removeIndex, enemy);
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                    if (black.isPawn()) {
                        {
                            Tile enPassantTile = black.getLeftEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile whitePawnTile = grid.getTile(previousRow, previousColumn - 1);
                                Piece whitePawn = whitePawnTile.getOccupant();
                                previousTile.removeOccupant();
                                whitePawnTile.removeOccupant();
                                enPassantTile.setOccupant(black);
                                int removeIndex = Pieces.remove(pieces, whitePawn);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    black.increaseMoveCount();
                                    {
                                        Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getEnPassantText(black, previousTile, whitePawn)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                    black.decreaseMoveCount();
                                }
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                            }
                        }
                        {
                            Tile enPassantTile = black.getRightEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                                Piece whitePawn = whitePawnTile.getOccupant();
                                previousTile.removeOccupant();
                                whitePawnTile.removeOccupant();
                                enPassantTile.setOccupant(black);
                                int removeIndex = Pieces.remove(pieces, whitePawn);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    black.increaseMoveCount();
                                    {
                                        Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getEnPassantText(black, previousTile, whitePawn)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                    black.decreaseMoveCount();
                                }
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                            }
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_BLACK_PIECES; ++pieceIndex) {
                    final Piece black = blacks.get(pieceIndex);
                    final int previousRow = black.getRow();
                    final int previousColumn = black.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    final Iterator<Tile> moveTiles = black.getMoveTiles(grid);
                    while (moveTiles.hasNext()) {
                        Tile moveTile = moveTiles.next();
                        if (black.isPawn() && previousRow == 6) {
                            //Queen replace = Pawn.promote(black);
                            for (Piece replace : Pawn.getPromoted(black)) {
                                previousTile.removeOccupant();
                                moveTile.setOccupant(replace);
                                int pawnIndex = pieces.indexOf(black);
                                pieces.set(pawnIndex, replace);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    {
                                        Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                        {
                                            possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getPromotedMoveText(black, previousTile, replace)));
                                        }
                                        if (pawn != null) {
                                            pawn.setJustMadeDoubleJump(true);
                                        }
                                    }
                                }
                                previousTile.setOccupant(black);
                                moveTile.removeOccupant();
                                pieces.set(pawnIndex, black);
                                grid.setProtections(pieces);
                            }
                        }
                        else {
                            previousTile.removeOccupant();
                            moveTile.setOccupant(black);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                //see if this pawn has made a double jump
                                //this is for enpassant purposes
                                boolean madeDoubleJump = false;
                                if (black.isPawn() && !black.hasMoved()) {
                                    if (black.getRow() == 3) {
                                        black.setJustMadeDoubleJump(madeDoubleJump = true);
                                    }
                                }
                                black.increaseMoveCount();
                                {
                                    Piece pawn = checkBlackEnPassantRightsSlow(pieces);
                                    {
                                        possiblePositions.add(new PositionHolder(new Grid(grid), MoveUtils.getMoveText(black, previousTile)));
                                    }
                                    if (pawn != null) {
                                        pawn.setJustMadeDoubleJump(true);
                                    }
                                }
                                black.decreaseMoveCount();
                                if (madeDoubleJump) {
                                    black.setJustMadeDoubleJump(false);
                                }
                            }
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }
                Tester.check(grid, clonedGrid, pieces, clonedPieces);
            }

            //show info dialog
            DIALOG.setVisible(true);

            //start timing
            (TIMER = timer).startTiming();

            list.clear();

            final int numberOfPositions = possiblePositions.size();

            if (numberOfPositions != Tester.perft(grid, 1, !side)) {
                throw new InternalError();
            }

            System.out.println("Number of positions from given position: " + numberOfPositions);

            if (numberOfPositions == 0) {
                DIALOG.disableUpdate();
                DIALOG.setTitle(side ? "White AI - (Done)" : "Black AI - (Done)");
                timer.stopTiming();

                //clear previous grids and pieces
                grid.clear();
                pieces.clear();
                //System.out.println("Cannot make a move.");
                return;
            }

            //update the dialog if not game over
            DIALOG.reset();
            DIALOG.setTotalMoves(numberOfPositions);

            PositionHolder bestPosition = possiblePositions.get(0);

            if (parallel) {
                ParallelSearch[] parallelSearches = new ParallelSearch[numberOfPositions];
                Future[] futures = new Future[numberOfPositions];

                //https://stackoverflow.com/questions/10116502/java-threading-optimization-at-100-cpu-usage
                //ExecutorService executor = Executors.newFixedThreadPool(PROCESSORS + 1);
                //https://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing
                //sometimes there are more core threads than positions!
                //so we must make sure core pool size is <= numberOfPositions
                int corePoolSize = CORE_THREADS > numberOfPositions ? numberOfPositions : CORE_THREADS;
                ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, numberOfPositions, 0L, TimeUnit.MILLISECONDS, TASK_QUEUE, HANDLER);

                for (int index = 0; index != numberOfPositions; ++index) {
                    futures[index] = executor.submit(parallelSearches[index] = new ParallelSearch(possiblePositions.get(index)));
                    //start all parallel tasks at the same time
                }
                executor.shutdown();
                try {
                    for (int index = 0; index != numberOfPositions; ++index) {
                        if (((Future<Integer>) futures[index]).get() != 0) { //wait for all parallel tasks to finish.
                            throw new Error("All future results should be zero.");
                        }
                        futures[index] = null;
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
                int shallowestDepth = parallelSearches[0].getMaxDepth();
                System.out.println(parallelSearches[0]);
                for (int index = 1; index != numberOfPositions; ++index) {
                    int depthsCovered = parallelSearches[index].getMaxDepth();
                    if (depthsCovered < shallowestDepth) {
                        shallowestDepth = depthsCovered;
                    }
                    System.out.println(parallelSearches[index]);
                }
                System.out.println("Parallel Processing Shallowest Depth: " + shallowestDepth);
                DIALOG.setFinishedDepth(shallowestDepth--);
                //this is the size of the list, we will need the last index later

                for (int index = 0; index != numberOfPositions; ++index) {
                    possiblePositions.get(index).value = parallelSearches[index].getScore(shallowestDepth);
                    parallelSearches[index] = null;
                }
                possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
                System.out.println(bestPosition);
                parallelSearches = null;
                futures = null;
                executor = null;
                TASK_QUEUE.clear();
            }
            else {
                int previousIterationTime = NEGATIVE_INFINITY;
                //array of previous values
                int[] previousValues = new int[numberOfPositions];
                Iterative_Deepening:
                for (int searchDepth = 1; searchDepth <= maxDepth; ++searchDepth) {
                    //before attempting to search at this particular depth, check time 
                    final int depthStartTime = timer.timeElapsed();
                    //if time remaining is less than the time it took for the previous depth
                    if (previousIterationTime != NEGATIVE_INFINITY && (timer.getCountdownTime() - depthStartTime) <= previousIterationTime) {
                        //it is pretty much impossible to search to a greater depth
                        //in the remaining amount of time so terminate search
                        break;
                    }
                    for (int index = 0; index != numberOfPositions; ++index) {
                        previousValues[index] = possiblePositions.get(index).value;
                    }
                    boolean checkmate = false;
                    for (int positionIndex = 0; positionIndex != numberOfPositions; ++positionIndex) {
                        PositionHolder position = possiblePositions.get(positionIndex);
                        int result = AlphaBetaBlack.min(new Board(position.grid, position.whites, position.blacks), searchDepth);
                        if (timer.timeOver()) {
                            //should time run out before all searches are completed
                            //at the current depth, reset all positional values to
                            //the previous depth iteration
                            for (int index = 0; index != numberOfPositions; ++index) {
                                possiblePositions.get(index).value = previousValues[index];
                            }
                            break Iterative_Deepening;
                        }
                        position.value = result;
                        DIALOG.increaseMovesCompleted();
                    }
                    //at this point the current depth has been fully searched
                    //so now we sort the higher scoring positions to look at first
                    //and find the best position, which is the first after sorting
                    possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                    DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
                    DIALOG.setFinishedDepth(searchDepth);
                    previousIterationTime = timer.timeElapsed() - depthStartTime;
                    DIALOG.setMovesCompleted(0);
                    System.out.println("Depth: " + searchDepth + " Time Taken: " + previousIterationTime);
                    if (checkmate) {
                        break;
                    }
                }
                System.out.println();
                previousValues = null;
            }

            {
                //maybe search for a better position only when losing
                final String encoded = Pieces.encode(bestPosition.whites, bestPosition.blacks);
                if (!selectedPositions.containsKey(encoded)) {
                    selectedPositions.put(encoded, 1);
                }
                else {
                    final int timesSelected = selectedPositions.get(encoded);
                    if (timesSelected >= 2) {
                        if (numberOfPositions >= 2) {
                            possiblePositions.sort(HIGHEST_VALUE_POSITION_LAST);
                            boolean successorFound = false;
                            for (int index = (numberOfPositions - 2); index >= 0; --index) {
                                PositionHolder nextBestPosition = possiblePositions.get(index);
                                String nextBestPositionEncoded = Pieces.encode(nextBestPosition.whites, nextBestPosition.blacks);
                                if (!selectedPositions.containsKey(nextBestPositionEncoded)) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 1);
                                    successorFound = true;
                                    break;
                                }
                                if (selectedPositions.get(nextBestPositionEncoded) == 1) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 2);
                                    successorFound = true;
                                    break;
                                }
                            }
                            if (!successorFound) {
                                selectedPositions.put(encoded, timesSelected + 1);
                            }
                        }
                        else {
                            selectedPositions.put(encoded, timesSelected + 1);
                        }
                    }
                    else {
                        selectedPositions.put(encoded, timesSelected + 1);
                    }
                }
            }

            check();

            list.add(bestPosition.description);
            for (int index = 0, size = bestPosition.whites.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.whites.get(index)));
            }
            for (int index = 0, size = bestPosition.blacks.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.blacks.get(index)));
            }
        }

        DIALOG.disableUpdate();
        DIALOG.setTitle(side ? "White AI - (Done)" : "Black AI - (Done)");
        timer.stopTiming();

        //clear previous grids and pieces
        grid.clear();
        pieces.clear();
        possiblePositions.clear();
    }

    private void check() {
        if (CHECK_MODE) {
            (TIMER = TEST_TIMER).startTiming();
            Tester.checkProtections(pieces, side);
            for (int index = 0, numberOfPositions = possiblePositions.size(); index != numberOfPositions; ++index) {
                PositionHolder current = possiblePositions.get(index);

                /**
                 * I believe this is correct, say you are playing as White After
                 * we generate our White Move List, we start tree searching from
                 * Black's moves, checking min function, always.
                 *
                 * If the depth is odd, we will be searching & scoring the worst
                 * of Black's moves. Choosing the maximal (usually negative
                 * value) that is closest to 0 will be correct. White (original)
                 * Black -> Evaluate
                 *
                 * If the depth is even, we will be searching & scoring the best
                 * of White's moves. Choosing the maximal (usually positive
                 * value) that is closest to 0 will be correct. White (original)
                 * Black -> White -> Evaluate
                 *
                 * So yet again, we choose the best of the min.
                 *
                 */
                {
                    Tester.checkGrids(current.grid, current.clonedGrid);
                    Tester.checkPieces(current.whites, current.clonedWhites);
                    Tester.checkPieces(current.blacks, current.clonedBlacks);
                }
                {
                    Board board = new Board(current.grid, current.whites, current.blacks);
                    int alphaBetaWhiteMin = SecureAlphaBetaWhite.min(board, TEST_DEPTH);
                    int alphaBetaWhiteMax = SecureAlphaBetaWhite.max(board, TEST_DEPTH);
                    int minMaxWhiteMin = SecureMinMaxWhite.min(board, TEST_DEPTH);
                    int minMaxWhiteMax = SecureMinMaxWhite.max(board, TEST_DEPTH);

                    int alphaBetaBlackMin = SecureAlphaBetaBlack.min(board, TEST_DEPTH);
                    int alphaBetaBlackMax = SecureAlphaBetaBlack.max(board, TEST_DEPTH);
                    int minMaxBlackMin = SecureMinMaxBlack.min(board, TEST_DEPTH);
                    int minMaxBlackMax = SecureMinMaxBlack.max(board, TEST_DEPTH);

                    {
                        if (alphaBetaWhiteMin != minMaxWhiteMin) {
                            throw new Error();
                        }

                        if (alphaBetaWhiteMax != minMaxWhiteMax) {
                            throw new Error();
                        }

                        if (alphaBetaBlackMin != minMaxBlackMin) {
                            throw new Error();
                        }

                        if (alphaBetaBlackMax != minMaxBlackMax) {
                            throw new Error();
                        }
                    }

                    {
                        if (alphaBetaWhiteMin != -alphaBetaBlackMax) {
                            throw new Error();
                        }

                        if (alphaBetaWhiteMax != -alphaBetaBlackMin) {
                            throw new Error();
                        }

                        if (minMaxWhiteMin != -minMaxBlackMax) {
                            throw new Error();
                        }

                        if (minMaxWhiteMax != -minMaxBlackMin) {
                            throw new Error();
                        }
                    }
                }
                {
                    Tester.checkGrids(current.grid, current.clonedGrid);
                    Tester.checkPieces(current.whites, current.clonedWhites);
                    Tester.checkPieces(current.blacks, current.clonedBlacks);
                }
            }
            TIMER.stopTiming();
        }
    }

    void useTestDialog() {
        DIALOG = new AI.StatusDialog(new JFrame());
    }

    static final class StatusDialog extends JDialog {

        private static final String TITLE = "AI Status";
        private final StatusView view;

        StatusDialog(JFrame parent) {
            super(parent, TITLE, false);
            super.setIconImage(parent.getIconImage());

            Dimension windowSize = new Dimension(parent.getWidth() / 2, Constants.APP_HEIGHT + parent.getHeight() / 2);

            super.setSize(windowSize);
            super.setMinimumSize(windowSize);
            super.setMaximumSize(windowSize);
            super.setPreferredSize(windowSize);

            super.getContentPane().add(view = new StatusView());

            super.setLocationRelativeTo(parent);
            super.setResizable(false);
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if (!visible) {
                view.update = false;
            }
        }

        void setStateTitle(String state) {
            super.setTitle(TITLE + state);
        }

        void disableUpdate() {
            view.update = false;
        }

        void increasePositionsScanned() {
            ++view.nodesScanned;
        }

        void setMaxPositionValue(int num) {
            view.data[3] = ("Best Position Value: " + (view.maxNodeValue = num));
        }

        void setFinishedDepth(int num) {
            view.depth = num;
        }

        void setTotalMoves(int totalMoves) {
            view.totalMoves = totalMoves;
        }

        void setMovesCompleted(int movesCompleted) {
            view.movesCompleted = movesCompleted;
        }

        void increaseMovesCompleted() {
            ++view.movesCompleted;
        }

        void reset() {
            view.update = true;
            view.nodesScanned = view.depth = view.totalMoves = view.movesCompleted = 0;
            view.maxNodeValue = NEGATIVE_INFINITY;
        }

        private static final class StatusView extends JPanel implements Runnable {

            //could use atomicinteger
            private volatile boolean update;
            private volatile int nodesScanned;
            //private int nodesIgnored;
            private volatile int maxNodeValue;
            private volatile int depth;
            private int totalMoves;
            private int movesCompleted;
            private final String[] data = new String[5];

            @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
            private StatusView() {
                super(null);
                data[0] = data[1] = data[2] = data[3] = data[4] = "";
                Thread statusViewThread = new Thread(this, "Status View Thread");
                statusViewThread.setPriority(Thread.MAX_PRIORITY - 1);
                statusViewThread.start();
            }

            private int width;
            private int height;
            private BufferedImage offscreenBuffer;
            private Graphics2D offscreenGraphics;
            private FontRenderContext offscreenFontRenderContext;
            private Font font;

            @Override
            @SuppressWarnings("UnusedAssignment")
            protected final void paintComponent(Graphics window) {
                super.paintComponent(window);
                
                if (offscreenBuffer == null) {
                    offscreenFontRenderContext = (offscreenGraphics = (offscreenBuffer = (BufferedImage) createImage(width = getWidth(), height = getHeight())).createGraphics()).getFontRenderContext();
                    font = getFont();
                }
                
                offscreenGraphics.setColor(Color.BLACK);
                offscreenGraphics.fillRect(0, 0, width, height);
                offscreenGraphics.setColor(Color.WHITE);
                
                if (update) {
                    int secondsElapsed = AI.TIMER.timeElapsed();
                    data[0] = ("Seconds Elapsed: " + secondsElapsed);
                    data[1] = ("Positions Scanned: " + nodesScanned);
                    data[2] = ("Positions Scanned per Second: " + ((secondsElapsed != 0) ? (int) (nodesScanned / secondsElapsed) : nodesScanned));
                    //data.add("Nodes Ignored: " + nodesIgnored);
                    data[3] = ("Max Position Value: " + maxNodeValue);
                    data[4] = ("Search Depths Completed: " + depth);
                    //data[5] = ("Current Search Depth: " + searchDepth);
                    //data.add("Unused Memory: " + RUNTIME.freeMemory() + " bytes");
                    //data.add("Used Memory: " + RUNTIME.totalMemory() + " bytes");
                    //data.add("Maximum Memory: " + RUNTIME.maxMemory() + " bytes");
                }
                
                float textHeight = 0.0f;
                for (int index = 0; index != data.length; ++index) {
                    String line = data[index];
                    offscreenGraphics.drawString(line, 0, textHeight += Constants.getStringHeight(line, font, offscreenFontRenderContext));
                }

                int approximateTextHeight = (int) Math.ceil(textHeight);

                offscreenGraphics.setColor(Color.WHITE);
                offscreenGraphics.fillRect(0, approximateTextHeight, width, 15);
                offscreenGraphics.setColor(Color.GREEN);
                offscreenGraphics.fillRect(0, approximateTextHeight, (int) (width * ((1.0 * movesCompleted) / (1.0 * totalMoves))), 15);

                //offscreenGraphics.drawLine(0, approximateTextHeight, width, approximateTextHeight);
                offscreenGraphics.drawImage(IMAGE, 0, approximateTextHeight + 15, width, height - approximateTextHeight - 15, this);
                window.drawImage(offscreenBuffer, 0, 0, this);
            }

            @Override
            public final void run() {
                try {
                    for (;;) {
                        repaint();
                        //helps to reduce painting overload and stress
                        //especially during parallel processing
                        //reduce the frames per second
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                }
                catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}