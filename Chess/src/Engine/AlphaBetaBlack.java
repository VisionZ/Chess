package Engine;

import static Engine.EvaluationConstants.CHECKMATE_VALUE;
import static Util.ChessConstants.BLACK_PIECE_ROW;
import static Util.ChessConstants.LEFT_ROOK_CASTLE_COLUMN;
import static Util.ChessConstants.LEFT_ROOK_START_COLUMN;
import static Util.ChessConstants.RIGHT_ROOK_CASTLE_COLUMN;
import static Util.ChessConstants.RIGHT_ROOK_START_COLUMN;
import static Util.ChessConstants.WHITE_PIECE_ROW;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import java.util.List;
import static Engine.Evaluators.MAIN;
import static Util.ChessConstants.BLACK_PAWN_DOUBLE_JUMP_ROW;
import static Util.ChessConstants.BLACK_PAWN_START_ROW;
import static Util.ChessConstants.WHITE_PAWN_DOUBLE_JUMP_ROW;
import static Util.ChessConstants.WHITE_PAWN_START_ROW;
import java.util.Iterator;

/**
 * Currently the fastest search algorithm that I implemented here, with a focus
 * on maximum efficiency. Instead of needlessly re-calculating tile protections,
 * this search algorithm saves them and simply reads them back when necessary.
 *
 * SLIGHT BUG!!! When a Pawn is being promoted, the list is not completely
 * sorted because the Pawn is replaced by a new promoted Piece. So it is likely
 * that a Queen may be surrounded by Pawns in the Piece list after such a
 * promotion.
 *
 * Our algorithm actually does not require that the piece list be sorted, but
 * rather this such sorting promotes more efficient node cutoffs.
 *
 * However, the Evaluation function must be reconfigured in order to process a
 * potentially unsorted Piece list. The King will always be the first piece in
 * the Piece list.
 *
 * @author zwill
 */
//Changes: "pawnIndex is always equal to pieceIndex"
public final class AlphaBetaBlack extends AdvancedSearchAlgorithm {

    public static final AlphaBetaBlack INSTANCE = new AlphaBetaBlack();

    private static final int QUIESCENCE_SEARCH_DEPTH = 1;

    //when using CHECK_MODE, allow for infinite time
    //also all search algorithms must use the same evaluator
    static final boolean CHECK_MODE = !true;
    private static final boolean STRICT_CHECK_MODE = false;

    //position counter
    private static long PERFT_COUNTER;

    static {
        System.out.println("---------------------------------------------------");
        System.out.println("Loading: " + AlphaBetaBlack.class.getName());
        System.out.println("Check Mode: " + (CHECK_MODE ? "Active" : "Inactive"));
        System.out.println("---------------------------------------------------");
    }

    private AlphaBetaBlack() {

    }

    public static void setPerftCounter(long l) {
        PERFT_COUNTER = l;
    }

    public static long getPerftCounter() {
        return PERFT_COUNTER;
    }

    public static final int min(Board board, int depth) {
        SecureAlphaBetaBlack.setPerftCounter(PERFT_COUNTER = 0L);
        final Board clonedBoard = new Board(board);
        final int result = INSTANCE.min(board, depth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
        if (CHECK_MODE) {
            //will fail when different evaluators are used, different scores
            if (result != SecureAlphaBetaBlack.min(board, depth)) {
                throw new Error();
            }
            //will fail when different evaluators are used, different cutoffs will happen
            //or when moves are ordered differently, which again
            //leads to different cutoffs
            if (STRICT_CHECK_MODE) {
                if (PERFT_COUNTER != SecureAlphaBetaBlack.getPerftCounter()) {
                    throw new Error();
                }
            }
        }
        if (!board.deepEquals(clonedBoard)) {
            throw new Error();
        }
        return result;
    }

    public static final int max(Board board, int depth) {
        SecureAlphaBetaBlack.setPerftCounter(PERFT_COUNTER = 0L);
        final Board clonedBoard = new Board(board);
        final int result = INSTANCE.max(board, depth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
        if (CHECK_MODE) {
            //will fail when different evaluators are used, different scores
            if (result != SecureAlphaBetaBlack.max(board, depth)) {
                throw new Error();
            }
            //will fail when different evaluators are used, different cutoffs will happen
            //or when moves are ordered differently, which again
            //leads to different cutoffs
            if (STRICT_CHECK_MODE) {
                if (PERFT_COUNTER != SecureAlphaBetaBlack.getPerftCounter()) {
                    throw new Error();
                }
            }
        }
        if (!board.deepEquals(clonedBoard)) {
            throw new Error();
        }
        return result;
    }

    /**
     * Minimizing component of the AlphaBeta search function seeking to reduce
     * Black's score by as much as possible. This method implements White's
     * moves, here we pick a move which leads to smallest possible max (the
     * worst Black can do).
     *
     * @param board The chess board.
     * @param depth Number of ply to search ahead.
     * @param alpha Current highest score for Black, Black seeks to maximize
     * this value by as much as possible.
     * @param beta Current highest score for White (which is also the current
     * lowest score for Black), White seeks to minimize this value by as much as
     * possible.
     * @return The least possible score to reduce Black's score by as much as
     * possible. This score may be extremely HIGH, indicating that Black is
     * wining or that White is about to be checkmated by Black.
     */
    @Override
    public final int min(final Board board, int depth, final int alpha, int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            ++PERFT_COUNTER;
            return MAIN.evaluateInBlackPerspective(board);
        }

        --depth;
        int value = POSITIVE_INFINITY;
        final Grid grid = board.grid;
        final List<Piece> whites = board.whites;
        final List<Piece> blacks = board.blacks;
        final King whiteKing = board.whiteKing;
        final ProtectionHolder holder = new ProtectionHolder(grid);

        if (whiteKing.canCastle(grid)) {
            final Tile previousTile = grid.getTile(whiteKing.getRow(), whiteKing.getColumn());
            {
                Tile kingCastleTile = whiteKing.getLeftCastleTile(grid);
                if (kingCastleTile != null) {
                    Tile leftRookTile = grid.getTile(WHITE_PIECE_ROW, LEFT_ROOK_START_COLUMN);
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
                            int result = max(board, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
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
                    holder.reset(grid);

                    if (beta <= alpha) {
                        return beta;
                    }
                }
            }
            {
                Tile kingCastleTile = whiteKing.getRightCastleTile(grid);
                if (kingCastleTile != null) {
                    Tile rightRookTile = grid.getTile(WHITE_PIECE_ROW, RIGHT_ROOK_START_COLUMN);
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
                            int result = max(board, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
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
                    holder.reset(grid);

                    if (beta <= alpha) {
                        return beta;
                    }
                }
            }
        }

        int maxIndex = whites.size() - 1;

        for (int pieceIndex = maxIndex; pieceIndex >= 0; --pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int currentRow = white.getRow();
            final int currentColumn = white.getColumn();
            final Tile previousTile = grid.getTile(currentRow, currentColumn);
            final List<Tile> attackTiles = white.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (!enemy.isKing()) {
                    if (white.isPawn() && currentRow == BLACK_PAWN_START_ROW) {
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
                                        int result = max(board, depth, alpha, beta);
                                        if (result < value) {
                                            value = result;
                                        }
                                        if (value < beta) {
                                            beta = value;
                                        }
                                    }
                                    if (pawn != null) {
                                        pawn.setJustMadeDoubleJump(true);
                                    }
                                }
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    whites.set(pawnIndex, white);
                                    blacks.add(removeIndex, enemy);
                                    holder.reset(grid);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            whites.set(pawnIndex, white);
                            blacks.add(removeIndex, enemy);
                            holder.reset(grid);
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
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                holder.reset(grid);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        blacks.add(removeIndex, enemy);
                        holder.reset(grid);
                    }
                }
            }
            if (white.isPawn()) {
                {
                    Tile enPassantTile = white.getLeftEnPassantTile(grid);
                    if (enPassantTile != null) {
                        Tile blackPawnTile = grid.getTile(currentRow, currentColumn - 1);
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
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                blacks.add(removeIndex, blackPawn);
                                holder.reset(grid);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        blackPawnTile.setOccupant(blackPawn);
                        enPassantTile.removeOccupant();
                        blacks.add(removeIndex, blackPawn);
                        holder.reset(grid);
                    }
                }
                {
                    Tile enPassantTile = white.getRightEnPassantTile(grid);
                    if (enPassantTile != null) {
                        Tile blackPawnTile = grid.getTile(currentRow, currentColumn + 1);
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
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                blacks.add(removeIndex, blackPawn);
                                holder.reset(grid);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        blackPawnTile.setOccupant(blackPawn);
                        enPassantTile.removeOccupant();
                        blacks.add(removeIndex, blackPawn);
                        holder.reset(grid);
                    }
                }
            }
        }

        for (; maxIndex >= 0; --maxIndex) {
            final Piece white = whites.get(maxIndex);
            final int currentRow = white.getRow();
            final int currentColumn = white.getColumn();
            final Tile previousTile = grid.getTile(currentRow, currentColumn);
            final Iterator<Tile> moveTiles = white.getMoveTiles(grid);
            while (moveTiles.hasNext()) {
                Tile moveTile = moveTiles.next();
                if (white.isPawn() && currentRow == BLACK_PAWN_START_ROW) {
                    for (Piece replace : Pawn.getPromoted(white)) {
                        previousTile.removeOccupant();
                        moveTile.setOccupant(replace);
                        int pawnIndex = maxIndex; //whites.indexOf(white);
                        whites.set(pawnIndex, replace);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                                {
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                moveTile.removeOccupant();
                                whites.set(pawnIndex, white);
                                holder.reset(grid);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        moveTile.removeOccupant();
                        whites.set(pawnIndex, white);
                        holder.reset(grid);
                    }
                }
                else {
                    previousTile.removeOccupant();
                    moveTile.setOccupant(white);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        boolean madeDoubleJump = false;
                        if (white.isPawn() && !white.hasMoved()) {
                            if (white.getRow() == WHITE_PAWN_DOUBLE_JUMP_ROW) {
                                white.setJustMadeDoubleJump(madeDoubleJump = true);
                            }
                        }
                        white.increaseMoveCount();
                        {
                            Piece pawn = Pieces.checkWhiteEnPassantRights(blacks);
                            {
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
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
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            holder.reset(grid);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    holder.reset(grid);
                }
            }
        }

        return (value == POSITIVE_INFINITY) ? checkWhiteEndGame(grid, whiteKing, depth + 1) : value;
    }

    /**
     * Maximizing component of the AlphaBeta search function seeking to increase
     * Black's score by as much as possible. This method implements Black's
     * moves, here we pick a move which leads to greatest possible max (the best
     * Black can do).
     *
     * @param board The chess board.
     * @param depth Number of ply to search ahead.
     * @param alpha Current highest score for Black, Black seeks to maximize
     * this value by as much as possible.
     * @param beta Current highest score for White (which is also the current
     * lowest score for Black), White seeks to minimize this value by as much as
     * possible.
     * @return The greatest possible score to increase Black's score by as much
     * as possible. This score may be extremely LOW, indicating that Black is
     * losing or that Black is about to be checkmated by White.
     */
    @Override
    public final int max(final Board board, int depth, int alpha, final int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            ++PERFT_COUNTER;
            return MAIN.evaluateInBlackPerspective(board);
        }

        --depth;
        int value = NEGATIVE_INFINITY;
        final Grid grid = board.grid;
        final List<Piece> whites = board.whites;
        final List<Piece> blacks = board.blacks;
        final King blackKing = board.blackKing;
        final ProtectionHolder holder = new ProtectionHolder(grid);

        if (blackKing.canCastle(grid)) {
            final Tile previousTile = grid.getTile(blackKing.getRow(), blackKing.getColumn());
            {
                Tile kingCastleTile = blackKing.getLeftCastleTile(grid);
                if (kingCastleTile != null) {
                    Tile leftRookTile = grid.getTile(BLACK_PIECE_ROW, LEFT_ROOK_START_COLUMN);
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
                            int result = min(board, depth, alpha, beta);
                            if (result > value) {
                                value = result;
                            }
                            if (value > alpha) {
                                alpha = value;
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
                    holder.reset(grid);

                    if (beta <= alpha) {
                        return alpha;
                    }
                }
            }
            {
                Tile kingCastleTile = blackKing.getRightCastleTile(grid);
                if (kingCastleTile != null) {
                    Tile rightRookTile = grid.getTile(BLACK_PIECE_ROW, RIGHT_ROOK_START_COLUMN);
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
                            int result = min(board, depth, alpha, beta);
                            if (result > value) {
                                value = result;
                            }
                            if (value > alpha) {
                                alpha = value;
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
                    holder.reset(grid);

                    if (beta <= alpha) {
                        return alpha;
                    }
                }
            }
        }

        int maxIndex = blacks.size() - 1;

        for (int pieceIndex = maxIndex; pieceIndex >= 0; --pieceIndex) {
            final Piece black = blacks.get(pieceIndex);
            final int currentRow = black.getRow();
            final int currentColumn = black.getColumn();
            final Tile previousTile = grid.getTile(currentRow, currentColumn);
            final List<Tile> attackTiles = black.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (!enemy.isKing()) {
                    if (black.isPawn() && currentRow == WHITE_PAWN_START_ROW) {
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
                                        int result = min(board, depth, alpha, beta);
                                        if (result > value) {
                                            value = result;
                                        }
                                        if (value > alpha) {
                                            alpha = value;
                                        }
                                    }
                                    if (pawn != null) {
                                        pawn.setJustMadeDoubleJump(true);
                                    }
                                }
                                if (beta <= alpha) {
                                    previousTile.setOccupant(black);
                                    attackTile.setOccupant(enemy);
                                    blacks.set(pawnIndex, black);
                                    whites.add(removeIndex, enemy);
                                    holder.reset(grid);
                                    return alpha;
                                }
                            }
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            blacks.set(pawnIndex, black);
                            whites.add(removeIndex, enemy);
                            holder.reset(grid);
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
                                    int result = min(board, depth, alpha, beta);
                                    if (result > value) {
                                        value = result;
                                    }
                                    if (value > alpha) {
                                        alpha = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            black.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(black);
                                attackTile.setOccupant(enemy);
                                whites.add(removeIndex, enemy);
                                holder.reset(grid);
                                return alpha;
                            }
                        }
                        previousTile.setOccupant(black);
                        attackTile.setOccupant(enemy);
                        whites.add(removeIndex, enemy);
                        holder.reset(grid);
                    }
                }
            }
            if (black.isPawn()) {
                {
                    Tile enPassantTile = black.getLeftEnPassantTile(grid);
                    if (enPassantTile != null) {
                        Tile whitePawnTile = grid.getTile(currentRow, currentColumn - 1);
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
                                    int result = min(board, depth, alpha, beta);
                                    if (result > value) {
                                        value = result;
                                    }
                                    if (value > alpha) {
                                        alpha = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            black.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                whites.add(removeIndex, whitePawn);
                                holder.reset(grid);
                                return alpha;
                            }
                        }
                        previousTile.setOccupant(black);
                        whitePawnTile.setOccupant(whitePawn);
                        enPassantTile.removeOccupant();
                        whites.add(removeIndex, whitePawn);
                        holder.reset(grid);
                    }
                }
                {
                    Tile enPassantTile = black.getRightEnPassantTile(grid);
                    if (enPassantTile != null) {
                        Tile whitePawnTile = grid.getTile(currentRow, currentColumn + 1);
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
                                    int result = min(board, depth, alpha, beta);
                                    if (result > value) {
                                        value = result;
                                    }
                                    if (value > alpha) {
                                        alpha = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            black.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                whites.add(removeIndex, whitePawn);
                                holder.reset(grid);
                                return alpha;
                            }
                        }
                        previousTile.setOccupant(black);
                        whitePawnTile.setOccupant(whitePawn);
                        enPassantTile.removeOccupant();
                        whites.add(removeIndex, whitePawn);
                        holder.reset(grid);
                    }
                }
            }
        }

        for (; maxIndex >= 0; --maxIndex) {
            final Piece black = blacks.get(maxIndex);
            final int currentRow = black.getRow();
            final int currentColumn = black.getColumn();
            final Tile previousTile = grid.getTile(currentRow, currentColumn);
            final Iterator<Tile> moveTiles = black.getMoveTiles(grid);
            while (moveTiles.hasNext()) {
                Tile moveTile = moveTiles.next();
                if (black.isPawn() && currentRow == WHITE_PAWN_START_ROW) {
                    for (Piece replace : Pawn.getPromoted(black)) {
                        previousTile.removeOccupant();
                        moveTile.setOccupant(replace);
                        int pawnIndex = maxIndex; //blacks.indexOf(black);
                        blacks.set(pawnIndex, replace);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            {
                                Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                                {
                                    int result = min(board, depth, alpha, beta);
                                    if (result > value) {
                                        value = result;
                                    }
                                    if (value > alpha) {
                                        alpha = value;
                                    }
                                }
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
                                }
                            }
                            if (beta <= alpha) {
                                previousTile.setOccupant(black);
                                moveTile.removeOccupant();
                                blacks.set(pawnIndex, black);
                                holder.reset(grid);
                                return alpha;
                            }
                        }
                        previousTile.setOccupant(black);
                        moveTile.removeOccupant();
                        blacks.set(pawnIndex, black);
                        holder.reset(grid);
                    }
                }
                else {
                    previousTile.removeOccupant();
                    moveTile.setOccupant(black);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        boolean madeDoubleJump = false;
                        if (black.isPawn() && !black.hasMoved()) {
                            if (black.getRow() == BLACK_PAWN_DOUBLE_JUMP_ROW) {
                                black.setJustMadeDoubleJump(madeDoubleJump = true);
                            }
                        }
                        black.increaseMoveCount();
                        {
                            Piece pawn = Pieces.checkBlackEnPassantRights(whites);
                            {
                                int result = min(board, depth, alpha, beta);
                                if (result > value) {
                                    value = result;
                                }
                                if (value > alpha) {
                                    alpha = value;
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
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            holder.reset(grid);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(black);
                    moveTile.removeOccupant();
                    holder.reset(grid);
                }
            }
        }

        return (value == NEGATIVE_INFINITY) ? checkBlackEndGame(grid, blackKing, depth + 1) : value;
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