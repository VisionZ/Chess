package Engine;

import static Engine.Evaluators.EXPLICIT;
import static Engine.Evaluators.STANDARD;
import static Engine.Pieces.checkBlackEnPassantRightsSlow;
import static Engine.Pieces.checkWhiteEnPassantRightsSlow;
import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.LINEAR_LENGTH;
import static Util.Constants.RUNTIME;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class Tester {
    
    private static final int MAX_DEPTH = 6;
    private static final boolean TEST_PERFT = false;

    @SuppressWarnings("Convert2Lambda")
    private static final Comparator<Tile> TILE_SORTER = new Comparator<Tile>() {
        @Override
        public int compare(Tile first, Tile next) {
            return first.getIndex() - next.getIndex();
        }
    };

    private Tester() {

    }
    
    static final void checkGrids(Grid grid1, Grid grid2) {
        if (!grid1.deepEquals(grid2)) {
            throw new Error();
        }
    }
    
    static final void checkPieces(List<Piece> list1, List<Piece> list2) {
        if (!list1.equals(list2)) {
            throw new Error();
        }
    }

    static final void check(Grid grid1, Grid grid2, List<Piece> list1, List<Piece> list2) {
        checkGrids(grid1, grid2);
        checkPieces(list1, list2);
    }
    
    public static final void main(String... args) {
        System.out.println(055L); //octal
        String s = "type anything here";
        s = s.replace("white", "black");
        s = s.replace("WHITE", "BLACK");
        s = s.replace("White", "Black");
        System.out.println(s);
        
        System.out.println("Number of Processors: " + RUNTIME.availableProcessors());
        System.out.println();
        
        for (int times = 10; times > 0; --times) {
            int[] first = new int[10000000];
            int[] second = new int[10000000];
            long start = System.nanoTime();
            for (int index = 0; index < first.length; ++index) {
                first[index] = index << 3;
            }
            long timeTaken = System.nanoTime() - start;
            System.out.println("Speed of bitshift: " + timeTaken);
            start = System.nanoTime();
            for (int index = 0; index < second.length; ++index) {
                second[index] = index * LENGTH;
            }
            timeTaken = System.nanoTime() - start;
            System.out.println("Speed of multiply: " + timeTaken);
            System.out.println();
        }
        
        {
            (AI.TIMER = AI.TEST_TIMER).startTiming();
            new AI(true, 60).useTestDialog();
        }
        
        testStartPosition();
        
        //movecounts accounted for
        {
            List<Piece> pieces = new ArrayList<>();
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

            testPosition(pieces, false);
        }
        
        testErrorPosition();
        
        RUNTIME.exit(0);
    }

    private static void testStartPosition() {
        System.out.println("Testing Starting Position");

        List<Piece> pieces = new ArrayList<>();

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

        Pieces.sort(pieces);
        testPosition(pieces, false);
    }

    private static void testErrorPosition() {
        System.out.println("Testing Error Position");
        List<Piece> pieces = new ArrayList<>();

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

        Pieces.sort(pieces);
        testPosition(pieces, false);
    }

    //checks to see if evaluator & evaluator special agree
    //check protected tiles!
    static void checkEvaluators(Grid grid) {
        Grid clone = new Grid(grid);
        List<Piece> pieces = grid.getPieces();
        Pieces.sort(pieces);
        List<Piece> whites = Pieces.getWhite(pieces);
        List<Piece> blacks = Pieces.getBlack(pieces);
        Board board = new Board(grid, whites, blacks);
        int normal = STANDARD.evaluateInBlackPerspective(board);
        int explicit = EXPLICIT.evaluateInBlackPerspective(board);
        if (normal != explicit) {
            throw new Error("Black Evaluator Error! Normal: " + normal + " Explicit: " + explicit);
        }
        normal = STANDARD.evaluateInWhitePerspective(board);
        explicit = EXPLICIT.evaluateInWhitePerspective(board);
        if (normal != explicit) {
            throw new Error("White Evaluator Error! Normal: " + normal + " Explicit: " + explicit);
        }
        checkGrids(grid, clone);
    }

    //false=white, true=black
    private static void testPosition(final List<Piece> pieces, final boolean color) {
        final Grid grid = new Grid();
        final int numberOfPieces = pieces.size();

        //put pieces onto the grid
        for (int index = 0; index != numberOfPieces; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }

        //for each piece, test their protected tiles
        for (int pieceIndex = 0; pieceIndex != numberOfPieces; ++pieceIndex) {
            final Piece piece = pieces.get(pieceIndex);
            final List<Tile> protectedTiles = piece.getProtectedTiles(grid);
            final int numberOfProtectedTiles = piece.getNumberOfProtectedTiles(grid);
            piece.setProtectedTiles(grid);
            final List<Tile> protectedTilesOnGrid = new ArrayList<>(numberOfProtectedTiles);
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                Tile tile = grid.getTile(index);
                if (tile.protectedByAlly(piece)) {
                    protectedTilesOnGrid.add(tile);
                }
            }
            if (protectedTiles.size() != numberOfProtectedTiles) {
                throw new Error();
            }
            protectedTiles.sort(TILE_SORTER);
            protectedTilesOnGrid.sort(TILE_SORTER);
            if (!protectedTiles.equals(protectedTilesOnGrid)) {
                throw new Error();
            }
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                grid.getTile(index).removeProtections();
            }
            /*
            if (piece.getMoveTiles(grid).size() != piece.getNumberOfMoveTiles(grid)) {
                throw new Error();
            }
            */
            System.out.println("All good.");
        }
        grid.setProtections(pieces);

        Grid copiedGrid = new Grid(grid);
        List<Piece> copiedPieces = Pieces.getDeepCopy(pieces);

        if (TEST_PERFT) {
            for (int depth = 1; depth <= MAX_DEPTH; ++depth) {
                long startTime = System.nanoTime();
                System.out.println((!color ? "White" : "Black") + " Perft(" + depth + ") Result: " + perft(grid, depth, color) + " Took: " + TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) + " seconds");
            }
        }
        else {
            for (int depth = 1; depth <= MAX_DEPTH; ++depth) {
                long startTime = System.nanoTime();
                int score = !color ? SecureMinMaxBlack.min(new Board(new Grid(grid)), depth) : SecureMinMaxBlack.max(new Board(new Grid(grid)), depth);
                long nanoSecondsElapsed = System.nanoTime() - startTime;
                double secondElapsed = nanoSecondsElapsed / 1000000000.0;
                System.out.println("SecureMinMaxBlack (" + ((!color) ? "Min-White" : "Max-Black") + ") Depth: " + depth + " Score: " + score + " Perft: " + SecureMinMaxBlack.getPerftCounter() + " Took: " + secondElapsed + " seconds & " + nanoSecondsElapsed + " nanoseconds");
                SecureMinMaxBlack.setPerftCounter(0);
            }
        }

        check(grid, copiedGrid, pieces, copiedPieces);
        System.out.println();
    }

    //checks to make sure all piece protection methods are working properly
    static final void checkProtections(final List<Piece> pieces, final boolean color) {
        final List<Piece> clonedPieces = Pieces.getDeepCopy(pieces);
        final Grid grid = new Grid();
        final int numberOfPieces = pieces.size();

        //put pieces onto the grid
        for (int index = 0; index != numberOfPieces; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }

        //for each piece, test their protected tiles
        for (int pieceIndex = 0; pieceIndex != numberOfPieces; ++pieceIndex) {
            final Piece piece = pieces.get(pieceIndex);
            final List<Tile> protectedTiles = piece.getProtectedTiles(grid);
            final int numberOfProtectedTiles = piece.getNumberOfProtectedTiles(grid);
            piece.setProtectedTiles(grid);
            final List<Tile> protectedTilesOnGrid = new ArrayList<>(numberOfProtectedTiles);
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                Tile tile = grid.getTile(index);
                if (tile.protectedByAlly(piece)) {
                    protectedTilesOnGrid.add(tile);
                }
            }
            if (protectedTiles.size() != numberOfProtectedTiles) {
                throw new Error();
            }
            protectedTiles.sort(TILE_SORTER);
            protectedTilesOnGrid.sort(TILE_SORTER);
            if (!protectedTiles.equals(protectedTilesOnGrid)) {
                throw new Error();
            }
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                grid.getTile(index).removeProtections();
            }
        }
        grid.setProtections(pieces);
        check(grid, new Grid(grid), pieces, clonedPieces);
    }
    
    //after a player makes a turn that does
    //not capture the enemy pawn that moved up 2 tiles
    //that enemy pawn is now immune from enpassant 
    

    //improved perft function  
    /**
     * Counts the number of all possible positions from a given start position.
     *
     * @param grid The given chess position.
     * @param depth How deep the calculation should be.
     * @param color Which player to start the calculation from. If {@code true}
     * then this function will count the number of all possible positions
     * starting from Black's turn of the given position. If {@code false} then
     * this function will count the number of all possible positions starting
     * from White's turn of the given position.
     * @return
     */
    static final long perft(final Grid grid, int depth, final boolean color) {
        if (depth == 0) {
            return 1L;
        }

        --depth;
        long moves = 0L;

        final List<Piece> pieces = grid.getPieces();
        Pieces.sort(pieces);
        //must sort pieces per iteration, since the
        //pieces from the grid are not sorted.

        final Grid clonedGrid = new Grid(grid);
        final List<Piece> clonedPieces = Pieces.getDeepCopy(pieces);

        if (color) {
            //black moves
            final List<Piece> blacks = Pieces.getBlack(pieces);
            final int numberOfBlackPieces = blacks.size();
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
                            moves += perft(grid, depth, !color);
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
                            moves += perft(grid, depth, !color);
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

            for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
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
                                    moves += perft(grid, depth, !color);
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
                                moves += perft(grid, depth, !color);
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
                                    moves += perft(grid, depth, !color);
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
                                    moves += perft(grid, depth, !color);
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

            for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
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
                                    moves += perft(grid, depth, !color);
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
                                moves += perft(grid, depth, !color);
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
            return moves;
        }
        else {
            //white moves
            final List<Piece> whites = Pieces.getWhite(pieces);
            final King whiteKing = Pieces.getWhiteKing(whites);
            final int numberOfWhitePieces = whites.size();
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
                            moves += perft(grid, depth, !color);
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
                            moves += perft(grid, depth, !color);
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

            for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
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
                                    moves += perft(grid, depth, !color);
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
                                moves += perft(grid, depth, !color);
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
                                    moves += perft(grid, depth, !color);
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
                                    moves += perft(grid, depth, !color);
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

            for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
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
                                    moves += perft(grid, depth, !color);
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
                                moves += perft(grid, depth, !color);
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
            return moves;
        }
    }

    //will not work since we dont read piece movecounts
    //which is important for enpassant and castling and other stuff
    @Deprecated
    public static Grid readGrid(String line) {
        Grid grid = new Grid();
        String[] split = line.split(Pattern.quote("/"));
        for (int index = 0; index < split.length; ++index) {
            String original = split[index];
            String formatted = "";
            for (int outer = 0, length = original.length(); outer != length; ++outer) {
                if (Character.isDigit(original.charAt(outer))) {
                    int spaces = Integer.parseInt(original.substring(outer, outer + 1));
                    while (spaces-- > 0) {
                        formatted += " ";
                    }
                }
                else {
                    formatted += original.substring(outer, outer + 1);
                }
            }
            split[index] = formatted;
        }
        int gridIndex = 0;
        for (String each : split) {
            for (char current : each.toCharArray()) {
                if (Character.isLetter(current)) {
                    switch (current) {
                        case 'K': {
                            grid.getTile(gridIndex).setOccupant(new King(0, 0, true));
                            break;
                        }
                        case 'Q': {
                            grid.getTile(gridIndex).setOccupant(new Queen(0, 0, true));
                            break;
                        }
                        case 'R': {
                            grid.getTile(gridIndex).setOccupant(new Rook(0, 0, true));
                            break;
                        }
                        case 'B': {
                            grid.getTile(gridIndex).setOccupant(new Bishop(0, 0, true));
                            break;
                        }
                        case 'N': {
                            grid.getTile(gridIndex).setOccupant(new Knight(0, 0, true));
                            break;
                        }
                        case 'P': {
                            grid.getTile(gridIndex).setOccupant(new Pawn(0, 0, true));
                            break;
                        }

                        case 'k': {
                            grid.getTile(gridIndex).setOccupant(new King(0, 0, !true));
                            break;
                        }
                        case 'q': {
                            grid.getTile(gridIndex).setOccupant(new Queen(0, 0, !true));
                            break;
                        }
                        case 'r': {
                            grid.getTile(gridIndex).setOccupant(new Rook(0, 0, !true));
                            break;
                        }
                        case 'b': {
                            grid.getTile(gridIndex).setOccupant(new Bishop(0, 0, !true));
                            break;
                        }
                        case 'n': {
                            grid.getTile(gridIndex).setOccupant(new Knight(0, 0, !true));
                            break;
                        }
                        case 'p': {
                            grid.getTile(gridIndex).setOccupant(new Pawn(0, 0, !true));
                            break;
                        }
                    }
                }
                ++gridIndex;
            }
        }
        grid.setProtections(grid.getPieces());
        return grid;
    }
}