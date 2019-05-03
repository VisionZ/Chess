package Engine;

import static Engine.EvaluationConstants.CHECKMATE_VALUE;
import static Util.ChessConstants.BLACK_PIECE_ROW;
import static Util.ChessConstants.LEFT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.LEFT_ROOK_CASTLE_COLUMN;
import static Util.ChessConstants.RIGHT_ROOK_CASTLE_COLUMN;
import static Util.ChessConstants.WHITE_PIECE_ROW;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import java.util.List;
import static Engine.Evaluators.MAIN;
import java.util.Iterator;

//Changes: "pawnIndex is always equal to pieceIndex"
public final class SecureMinMaxBlack {

    private static final boolean CHECK_MODE = AlphaBetaBlack.CHECK_MODE;
    private static long PERFT_COUNTER;

    static {
        System.out.println("---------------------------------------------------");
        System.out.println("Loading: " + SecureMinMaxBlack.class.getName());
        System.out.println("Check Mode: " + (CHECK_MODE ? "Active" : "Inactive"));
        System.out.println("---------------------------------------------------");
    }

    private SecureMinMaxBlack() {

    }

    public static void setPerftCounter(long l) {
        PERFT_COUNTER = l;
    }

    public static long getPerftCounter() {
        return PERFT_COUNTER;
    }

    /**
     * Minimizing component of the Min-Max search function seeking to reduce
     * Black's score by as much as possible. This method implements White's
     * moves.
     *
     * @param board The chess board.
     * @param depth Number of ply to search ahead.
     * @return The least possible score to reduce Black's score as much as
     * possible. This score may be extremely high, indicating that Black is
     * wining or that White is about to be checkmated by Black.
     */
    //pick lowest of the max (worst enemy-Black can do)
    static final int min(final Board board, int depth) {
        if (CHECK_MODE) {
            if (depth == 0) {
                ++PERFT_COUNTER;
                return MAIN.evaluateInBlackPerspective(board);
            }
        }
        else {
            if (depth == 0 || AI.TIMER.timeOver()) {
                ++PERFT_COUNTER;
                return MAIN.evaluateInBlackPerspective(board);
            }
        }

        final Grid grid = board.grid;
        final List<Piece> whites = board.whites;
        final List<Piece> blacks = board.blacks;

        --depth;
        int min = POSITIVE_INFINITY;

        final King whiteKing = board.whiteKing;

        final Grid clonedGrid;
        final List<Piece> clonedWhites;
        final List<Piece> clonedBlacks;

        if (CHECK_MODE) {
            clonedGrid = new Grid(grid);
            clonedWhites = Pieces.getDeepCopy(whites);
            clonedBlacks = Pieces.getDeepCopy(blacks);
        }
        else {
            clonedGrid = null;
            clonedWhites = null;
            clonedBlacks = null;
        }

        {
            final Tile previousTile = grid.getTile(whiteKing.getRow(), whiteKing.getColumn());
            final List<Tile> castleTiles = whiteKing.getCastleTiles(grid);
            for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                Tile kingCastleTile = castleTiles.get(index);
                if (kingCastleTile.getColumn() == LEFT_KING_CASTLE_COLUMN) {
                    Tile leftRookTile = grid.getTile(WHITE_PIECE_ROW, 0);
                    Tile leftRookCastleTile = grid.getTile(WHITE_PIECE_ROW, LEFT_ROOK_CASTLE_COLUMN);
                    Piece leftRook = leftRookTile.getOccupant();

                    previousTile.removeOccupant();
                    leftRookTile.removeOccupant();
                    kingCastleTile.setOccupant(whiteKing);
                    leftRookCastleTile.setOccupant(leftRook);
                    grid.setProtections(whites, blacks);

                    whiteKing.increaseMoveCount();
                    leftRook.increaseMoveCount();
                    {
                        Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                        {
                            int result = max(board, depth);
                            if (result < min) {
                                min = result;
                            }
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
                    grid.setProtections(whites, blacks);
                }
                else {
                    Tile rightRookTile = grid.getTile(WHITE_PIECE_ROW, 7);
                    Tile rightRookCastleTile = grid.getTile(WHITE_PIECE_ROW, RIGHT_ROOK_CASTLE_COLUMN);
                    Piece rightRook = rightRookTile.getOccupant();

                    previousTile.removeOccupant();
                    rightRookTile.removeOccupant();
                    kingCastleTile.setOccupant(whiteKing);
                    rightRookCastleTile.setOccupant(rightRook);
                    grid.setProtections(whites, blacks);

                    whiteKing.increaseMoveCount();
                    rightRook.increaseMoveCount();
                    {
                        Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                        {
                            int result = max(board, depth);
                            if (result < min) {
                                min = result;
                            }
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
                    grid.setProtections(whites, blacks);
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
            }
        }

        final int numberOfWhitePieces = whites.size();

        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> attackTiles = white.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                if (white.isPawn() && previousRow == 1) {
                    for (Piece replace : Pawn.getPromoted(white)) {
                        previousTile.removeOccupant();
                        attackTile.setOccupant(replace);
                        int pawnIndex = pieceIndex; //whites.indexOf(white);
                        whites.set(pawnIndex, replace);
                        int removeIndex = Pieces.remove(blacks, enemy);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                                {
                                    int result = max(board, depth);
                                    if (result < min) {
                                        min = result;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        whites.set(pawnIndex, white);
                        blacks.add(removeIndex, enemy);
                        grid.setProtections(whites, blacks);
                    }
                }
                else {
                    previousTile.removeOccupant();
                    attackTile.setOccupant(white);
                    int removeIndex = Pieces.remove(blacks, enemy);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        white.increaseMoveCount();
                        {
                            Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                            {
                                int result = max(board, depth);
                                if (result < min) {
                                    min = result;
                                }
                            }
                            if (pawn != null) {
                                pawn.setJustMadeDoubleJump(true);
                            }
                        }
                        white.decreaseMoveCount();
                    }
                    previousTile.setOccupant(white);
                    attackTile.setOccupant(enemy);
                    blacks.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
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
                        int removeIndex = Pieces.remove(blacks, blackPawn);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                                {
                                    int result = max(board, depth);
                                    if (result < min) {
                                        min = result;
                                    }
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
                        blacks.add(removeIndex, blackPawn);
                        grid.setProtections(whites, blacks);
                    }
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
                {
                    Tile enPassantTile = white.getRightEnPassantTile(grid);
                    if (enPassantTile != null) {
                        Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                        Piece blackPawn = blackPawnTile.getOccupant();
                        previousTile.removeOccupant();
                        blackPawnTile.removeOccupant();
                        enPassantTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, blackPawn);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                                {
                                    int result = max(board, depth);
                                    if (result < min) {
                                        min = result;
                                    }
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
                        blacks.add(removeIndex, blackPawn);
                        grid.setProtections(whites, blacks);
                    }
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
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
                    for (Piece replace : Pawn.getPromoted(white)) {
                        previousTile.removeOccupant();
                        moveTile.setOccupant(replace);
                        int pawnIndex = pieceIndex; //whites.indexOf(white);
                        whites.set(pawnIndex, replace);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                                {
                                    int result = max(board, depth);
                                    if (result < min) {
                                        min = result;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                        }
                        previousTile.setOccupant(white);
                        moveTile.removeOccupant();
                        whites.set(pawnIndex, white);
                        grid.setProtections(whites, blacks);
                    }
                }
                else {
                    previousTile.removeOccupant();
                    moveTile.setOccupant(white);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        boolean madeDoubleJump = false;
                        if (white.isPawn() && !white.hasMoved()) {
                            if (white.getRow() == 4) {
                                white.setJustMadeDoubleJump(madeDoubleJump = true);
                            }
                        }
                        white.increaseMoveCount();
                        {
                            Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                            {
                                int result = max(board, depth);
                                if (result < min) {
                                    min = result;
                                }
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
                    grid.setProtections(whites, blacks);
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
            }
        }

        if (CHECK_MODE) {
            Tester.checkGrids(grid, clonedGrid);
            Tester.checkPieces(whites, clonedWhites);
            Tester.checkPieces(blacks, clonedBlacks);
        }

        return (min == POSITIVE_INFINITY) ? checkWhiteEndGame(grid, whiteKing, depth + 1) : min;
    }

    /**
     * Maximizing component of the Min-Max search function seeking to increase
     * Black's score by as much as possible. This method implements Black's
     * moves.
     *
     * @param grid The chess board.
     * @param whites The white pieces on the chess board.
     * @param blacks The black pieces on the chess board.
     * @param depth Number of ply to search ahead.
     * @return The greatest possible score to increase Black's score by as much
     * as possible. This score may be extremely low, indicating that Black is
     * losing or is about to be checkmated.
     */
    //pick highest of the min (aka worst enemy-white can do)
    static final int max(final Board board, int depth) {
        if (CHECK_MODE) {
            if (depth == 0) {
                ++PERFT_COUNTER;
                return MAIN.evaluateInBlackPerspective(board);
            }
        }
        else {
            if (depth == 0 || AI.TIMER.timeOver()) {
                ++PERFT_COUNTER;
                return MAIN.evaluateInBlackPerspective(board);
            }
        }

        final Grid grid = board.grid;
        final List<Piece> whites = board.whites;
        final List<Piece> blacks = board.blacks;

        --depth;
        int max = NEGATIVE_INFINITY;

        final King blackKing = board.blackKing;

        final Grid clonedGrid;
        final List<Piece> clonedWhites;
        final List<Piece> clonedBlacks;

        if (CHECK_MODE) {
            clonedGrid = new Grid(grid);
            clonedWhites = Pieces.getDeepCopy(whites);
            clonedBlacks = Pieces.getDeepCopy(blacks);
        }
        else {
            clonedGrid = null;
            clonedWhites = null;
            clonedBlacks = null;
        }

        {
            final Tile previousTile = grid.getTile(blackKing.getRow(), blackKing.getColumn());
            final List<Tile> castleTiles = blackKing.getCastleTiles(grid);
            for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                Tile kingCastleTile = castleTiles.get(index);
                if (kingCastleTile.getColumn() == LEFT_KING_CASTLE_COLUMN) {
                    Tile leftRookTile = grid.getTile(BLACK_PIECE_ROW, 0);
                    Tile leftRookCastleTile = grid.getTile(BLACK_PIECE_ROW, LEFT_ROOK_CASTLE_COLUMN);
                    Piece leftRook = leftRookTile.getOccupant();

                    previousTile.removeOccupant();
                    leftRookTile.removeOccupant();
                    kingCastleTile.setOccupant(blackKing);
                    leftRookCastleTile.setOccupant(leftRook);
                    grid.setProtections(whites, blacks);

                    blackKing.increaseMoveCount();
                    leftRook.increaseMoveCount();
                    {
                        Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                        {
                            int result = min(board, depth);
                            if (result > max) {
                                max = result;
                            }
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
                    grid.setProtections(whites, blacks);
                }
                else {
                    Tile rightRookTile = grid.getTile(BLACK_PIECE_ROW, 7);
                    Tile rightRookCastleTile = grid.getTile(BLACK_PIECE_ROW, RIGHT_ROOK_CASTLE_COLUMN);
                    Piece rightRook = rightRookTile.getOccupant();

                    previousTile.removeOccupant();
                    rightRookTile.removeOccupant();
                    kingCastleTile.setOccupant(blackKing);
                    rightRookCastleTile.setOccupant(rightRook);
                    grid.setProtections(whites, blacks);

                    blackKing.increaseMoveCount();
                    rightRook.increaseMoveCount();
                    {
                        Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                        {
                            int result = min(board, depth);
                            if (result > max) {
                                max = result;
                            }
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
                    grid.setProtections(whites, blacks);
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
            }
        }

        final int numberOfBlackPieces = blacks.size();

        for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
            final Piece black = blacks.get(pieceIndex);
            final int previousRow = black.getRow();
            final int previousColumn = black.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> attackTiles = black.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                if (black.isPawn() && previousRow == 6) {
                    for (Piece replace : Pawn.getPromoted(black)) {
                        previousTile.removeOccupant();
                        attackTile.setOccupant(replace);
                        int pawnIndex = pieceIndex; //blacks.indexOf(black);
                        blacks.set(pawnIndex, replace);
                        int removeIndex = Pieces.remove(whites, enemy);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                                {
                                    int result = min(board, depth);
                                    if (result > max) {
                                        max = result;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                        }
                        previousTile.setOccupant(black);
                        attackTile.setOccupant(enemy);
                        blacks.set(pawnIndex, black);
                        whites.add(removeIndex, enemy);
                        grid.setProtections(whites, blacks);
                    }
                }
                else {
                    previousTile.removeOccupant();
                    attackTile.setOccupant(black);
                    int removeIndex = Pieces.remove(whites, enemy);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        black.increaseMoveCount();
                        {
                            Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                            {
                                int result = min(board, depth);
                                if (result > max) {
                                    max = result;
                                }
                            }
                            if (pawn != null) {
                                pawn.setJustMadeDoubleJump(true);
                            }
                        }
                        black.decreaseMoveCount();
                    }
                    previousTile.setOccupant(black);
                    attackTile.setOccupant(enemy);
                    whites.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
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
                        int removeIndex = Pieces.remove(whites, whitePawn);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            black.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                                {
                                    int result = min(board, depth);
                                    if (result > max) {
                                        max = result;
                                    }
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
                        whites.add(removeIndex, whitePawn);
                        grid.setProtections(whites, blacks);
                    }
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
                {
                    Tile enPassantTile = black.getRightEnPassantTile(grid);
                    if (enPassantTile != null) {
                        Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                        Piece whitePawn = whitePawnTile.getOccupant();
                        previousTile.removeOccupant();
                        whitePawnTile.removeOccupant();
                        enPassantTile.setOccupant(black);
                        int removeIndex = Pieces.remove(whites, whitePawn);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            black.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                                {
                                    int result = min(board, depth);
                                    if (result > max) {
                                        max = result;
                                    }
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
                        whites.add(removeIndex, whitePawn);
                        grid.setProtections(whites, blacks);
                    }
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
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
                    for (Piece replace : Pawn.getPromoted(black)) {
                        previousTile.removeOccupant();
                        moveTile.setOccupant(replace);
                        int pawnIndex = pieceIndex; //blacks.indexOf(black);
                        blacks.set(pawnIndex, replace);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                                {
                                    int result = min(board, depth);
                                    if (result > max) {
                                        max = result;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                        }
                        previousTile.setOccupant(black);
                        moveTile.removeOccupant();
                        blacks.set(pawnIndex, black);
                        grid.setProtections(whites, blacks);
                    }
                }
                else {
                    previousTile.removeOccupant();
                    moveTile.setOccupant(black);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        boolean madeDoubleJump = false;
                        if (black.isPawn() && !black.hasMoved()) {
                            if (black.getRow() == 3) {
                                black.setJustMadeDoubleJump(madeDoubleJump = true);
                            }
                        }
                        black.increaseMoveCount();
                        {
                            Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                            {
                                int result = min(board, depth);
                                if (result > max) {
                                    max = result;
                                }
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
                    grid.setProtections(whites, blacks);
                }
                if (CHECK_MODE) {
                    Tester.checkGrids(grid, clonedGrid);
                    Tester.checkPieces(whites, clonedWhites);
                    Tester.checkPieces(blacks, clonedBlacks);
                }
            }
        }

        if (CHECK_MODE) {
            Tester.checkGrids(grid, clonedGrid);
            Tester.checkPieces(whites, clonedWhites);
            Tester.checkPieces(blacks, clonedBlacks);
        }

        return (max == NEGATIVE_INFINITY) ? checkBlackEndGame(grid, blackKing, depth + 1) : max;
    }

    /**
     * This function should only be called when the White player cannot make any
     * legal moves. The function then determines whether such a position is a
     * Checkmate against White or a Stalemate.
     *
     * @param grid The current chess board.
     * @param whiteKing The White player's King.
     * @param depth How close this position is from the base node. The bigger
     * this value, the closer this position is from the base node. This value
     * cannot be negative.
     * @return An extremely high value (greater than or equal to 200,000,000) if
     * this position is a Checkmate against White or 0 if this position is a
     * Stalemate.
     */
    private static int checkWhiteEndGame(final Grid grid, final King whiteKing, final int depth) {
        AI.DIALOG.increasePositionsScanned();
        //a bigger "depth", is actually shallower in the tree
        return whiteKing.inCheck(grid) ? (CHECKMATE_VALUE + depth) : 0;
    }

    /**
     * This function should only be called when the Black player cannot make any
     * legal moves. The function then determines whether such a position is a
     * Checkmate against Black or a Stalemate.
     *
     * @param grid The current chess board.
     * @param blackKing The Black player's King.
     * @param depth How close this position is from the base node. The bigger
     * this value, the closer this position is from the base node. This value
     * cannot be negative.
     * @return An extremely low value (less than or equal to -200,000,000) if
     * this position is a Checkmate against Black or 0 if this position is a
     * Stalemate.
     */
    private static int checkBlackEndGame(final Grid grid, final King blackKing, final int depth) {
        AI.DIALOG.increasePositionsScanned();
        //a lower "depth", is actually deeper in the tree
        return blackKing.inCheck(grid) ? (-CHECKMATE_VALUE - depth) : 0;
    }
}