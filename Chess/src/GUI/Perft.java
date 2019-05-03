package GUI;

import Util.ChessConstants;
import java.util.List;

public final class Perft {

    private Perft() {

    }

    static final void check(Grid grid1, Grid grid2, List<Piece> list1, List<Piece> list2) {
        grid1.equals(grid2);
        comparePieces(list1, list2);
    }

    private static void comparePieces(List<Piece> list1, List<Piece> list2) {
        if (!list1.equals(list2)) {
            throw new Error();
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
    private static Piece checkWhiteEnPassantRights(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn() && piece.isBlack()) {
                if (piece.justMadeDoubleJump()) {
                    piece.setJustMadeDoubleJump(false);
                    return piece;
                }
            }
        }
        return null;
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
    private static Piece checkBlackEnPassantRights(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn() && piece.isWhite()) {
                if (piece.justMadeDoubleJump()) {
                    piece.setJustMadeDoubleJump(false);
                    return piece;
                }
            }
        }
        return null;
    }
    
    /**
     * Counts the number of all possible positions from a given 
     * start position.
     * @param grid The given chess position.
     * @param depth How deep the calculation should be.
     * @param color Which player to start the calculation from. 
     * If {@code true} then this function will count the number of all 
     * possible positions starting from Black's turn of the given position. 
     * If {@code false} then this function will count the number of all
     * possible positions starting from White's turn of the given position. 
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
                        Piece pawn = checkBlackEnPassantRights(pieces);
                        moves += perft(grid, depth, !color);
                        if (pawn != null) {
                            pawn.setJustMadeDoubleJump(true);
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
                        Piece pawn = checkBlackEnPassantRights(pieces);
                        moves += perft(grid, depth, !color);
                        if (pawn != null) {
                            pawn.setJustMadeDoubleJump(true);
                        }
                        blackKing.decreaseMoveCount();
                        rightRook.decreaseMoveCount();

                        previousTile.setOccupant(blackKing);
                        rightRookTile.setOccupant(rightRook);
                        kingCastleTile.removeOccupant();
                        rightRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                    }
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
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
                        //Piece replace = ((Pawn) black).promote(QUEEN);
                        for (Piece replace : Pawn.getPromoted(black)) {
                            previousTile.removeOccupant();
                            attackTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(black);
                            pieces.set(pawnIndex, replace);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                Piece pawn = checkBlackEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                            Piece pawn = checkBlackEnPassantRights(pieces);
                            moves += perft(grid, depth, !color);
                            if (pawn != null) {
                                pawn.setJustMadeDoubleJump(true);
                            }
                            black.decreaseMoveCount();
                        }
                        previousTile.setOccupant(black);
                        attackTile.setOccupant(enemy);
                        pieces.add(removeIndex, enemy);
                        grid.setProtections(pieces);
                    }
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
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
                                Piece pawn = checkBlackEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                                Piece pawn = checkBlackEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
                }
            }

            for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
                final Piece black = blacks.get(pieceIndex);
                final int previousRow = black.getRow();
                final int previousColumn = black.getColumn();
                final Tile previousTile = grid.getTile(previousRow, previousColumn);
                List<Tile> moveTiles = black.getMoveTiles(grid);
                for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                    Tile moveTile = moveTiles.get(index);
                    if (black.isPawn() && previousRow == 6) {
                        //Piece replace = ((Pawn) black).promote(QUEEN);
                        for (Piece replace : Pawn.getPromoted(black)) {
                            previousTile.removeOccupant();
                            moveTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(black);
                            pieces.set(pawnIndex, replace);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                Piece pawn = checkBlackEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                            Piece pawn = checkBlackEnPassantRights(pieces);
                            moves += perft(grid, depth, !color);
                            if (pawn != null) {
                                pawn.setJustMadeDoubleJump(true);
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
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
                }
            }
            Perft.check(grid, clonedGrid, pieces, clonedPieces);
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
                        Piece pawn = checkWhiteEnPassantRights(pieces);
                        moves += perft(grid, depth, !color);
                        if (pawn != null) {
                            pawn.setJustMadeDoubleJump(true);
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
                        Piece pawn = checkWhiteEnPassantRights(pieces);
                        moves += perft(grid, depth, !color);
                        if (pawn != null) {
                            pawn.setJustMadeDoubleJump(true);
                        }
                        whiteKing.decreaseMoveCount();
                        rightRook.decreaseMoveCount();

                        previousTile.setOccupant(whiteKing);
                        rightRookTile.setOccupant(rightRook);
                        kingCastleTile.removeOccupant();
                        rightRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                    }
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
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
                        //Piece replace = ((Pawn) white).promote(QUEEN);
                        for (Piece replace : Pawn.getPromoted(white)) {
                            previousTile.removeOccupant();
                            attackTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(white);
                            pieces.set(pawnIndex, replace);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                Piece pawn = checkWhiteEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                            Piece pawn = checkWhiteEnPassantRights(pieces);
                            moves += perft(grid, depth, !color);
                            if (pawn != null) {
                                pawn.setJustMadeDoubleJump(true);
                            }
                            white.decreaseMoveCount();
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        pieces.add(removeIndex, enemy);
                        grid.setProtections(pieces);
                    }
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
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
                                Piece pawn = checkWhiteEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                                Piece pawn = checkWhiteEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                }
            }

            for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
                final Piece white = whites.get(pieceIndex);
                final int previousRow = white.getRow();
                final int previousColumn = white.getColumn();
                final Tile previousTile = grid.getTile(previousRow, previousColumn);
                List<Tile> moveTiles = white.getMoveTiles(grid);
                for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                    Tile moveTile = moveTiles.get(index);
                    if (white.isPawn() && previousRow == 1) {
                        //Piece replace = ((Pawn) white).promote(QUEEN);
                        for (Piece replace : Pawn.getPromoted(white)) {
                            previousTile.removeOccupant();
                            moveTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(white);
                            pieces.set(pawnIndex, replace);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                Piece pawn = checkWhiteEnPassantRights(pieces);
                                moves += perft(grid, depth, !color);
                                if (pawn != null) {
                                    pawn.setJustMadeDoubleJump(true);
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
                            Piece pawn = checkWhiteEnPassantRights(pieces);
                            moves += perft(grid, depth, !color);
                            if (pawn != null) {
                                pawn.setJustMadeDoubleJump(true);
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
                    Perft.check(grid, clonedGrid, pieces, clonedPieces);
                }
            }
            Perft.check(grid, clonedGrid, pieces, clonedPieces);
            return moves;
        }
    }
}