package GUI;

import Util.ChessConstants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static Util.ChessConstants.NUMBER_OF_PIECES_PER_SIDE;

public final class Pieces {

    
    @SuppressWarnings("Convert2Lambda")
    static final Comparator<Piece> BEST_PIECES_FIRST = new Comparator<Piece>() {
        @Override
        public int compare(Piece first, Piece next) {
            return next.compareTo(first);
        }
    };

    @SuppressWarnings("Convert2Lambda")
    static final Comparator<Piece> BEST_PIECES_LAST = new Comparator<Piece>() {
        @Override
        public int compare(Piece first, Piece next) {
            return first.compareTo(next);
        }
    };
    
    static final List<Piece> WHITES = new ArrayList<>(ChessConstants.NUMBER_OF_PIECES_PER_SIDE);
    static final List<Piece> BLACKS = new ArrayList<>(ChessConstants.NUMBER_OF_PIECES_PER_SIDE);

    private Pieces() {

    }

    /**
     * Sorts the given list of pieces. White pieces are moved to the front of
     * the list and are then ordered from greatest to least value. Then Black
     * pieces are moved to the remaining index positions after the white pieces
     * and are then ordered from least to greatest value. Note that this method
     * is NOT thread-safe and cannot be safely accessed by multiple threads.
     *
     * @param pieces The given list of pieces.
     */
    static void sort(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isWhite()) {
                WHITES.add(piece);
            }
            else {
                BLACKS.add(piece);
            }
        }
        WHITES.sort(BEST_PIECES_FIRST);
        BLACKS.sort(BEST_PIECES_LAST);
        pieces.clear();
        pieces.addAll(WHITES);
        pieces.addAll(BLACKS);
        WHITES.clear();
        BLACKS.clear();
    } 

    /**
     * Returns the index of a piece object in a list of pieces and removes the
     * piece from the list.
     *
     * @param pieces The given list of pieces.
     * @param piece The given piece to remove.
     * @return The index of the given piece in the given list of pieces.
     */
    static int remove(List<Piece> pieces, Piece piece) {
        for (int index = (pieces.size() - 1); index >= 0; --index) {
            if (piece.equals(pieces.get(index))) {
                pieces.remove(index);
                return index; 
            }
        }
        return -1;
    }

    /**
     * Returns a list of white pieces from the given list of pieces. The given
     * list of pieces must be sorted as dictated by the {
     *
     * @see sort(java.util.List)} method.
     * @param pieces The given list of pieces.
     * @return A list of white pieces from the given list of pieces.
     */
    static List<Piece> getWhite(List<Piece> pieces) {
        List<Piece> whites = new ArrayList<>(NUMBER_OF_PIECES_PER_SIDE);
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isWhite()) {
                whites.add(piece);
            }
            else {
                //expect cutoff, the list is sorted
                return whites;
            }
        }
        return null;
    }

    /**
     * Returns a list of black pieces from the given list of pieces. The given
     * list of pieces must be sorted as dictated by the {
     *
     * @see sort(java.util.List)} method.
     * @param pieces The given list of pieces.
     * @return A list of black pieces from the given list of pieces.
     */
    static List<Piece> getBlack(List<Piece> pieces) {
        List<Piece> blacks = new ArrayList<>(NUMBER_OF_PIECES_PER_SIDE);
        for (int index = (pieces.size() - 1); index != -1; --index) {
            Piece piece = pieces.get(index);
            if (piece.isBlack()) {
                blacks.add(piece);
            }
            else {
                return blacks;
            }
        }
        return null;
    }

    public static List<Piece> getDeepCopy(List<Piece> pieces) {
        int size = pieces.size();
        List<Piece> copy = new ArrayList<>(size);
        for (int index = 0; index != size; ++index) {
            copy.add(pieces.get(index).clone());
        }
        return copy;
    }

    public static King getWhiteKing(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isKing() && piece.isWhite()) {
                return (King) piece;
            }
        }
        return null;
    }

    public static King getBlackKing(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isKing() && piece.isBlack()) {
                return (King) piece;
            }
        }
        return null;
    }

    public static Piece getSelected(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isSelected()) {
                return piece;
            }
        }
        return null;
    }

    public static int getWhiteState(Grid grid, List<Piece> pieces, King whiteKing) {
        return (whiteKing == null) ? ChessConstants.SAFE : (whiteTrapped(grid, pieces, whiteKing)) ? ((whiteKing.inCheck(grid)) ? ChessConstants.CHECKMATED : ChessConstants.STALEMATED) : ((whiteKing.inCheck(grid)) ? ChessConstants.CHECKED : ChessConstants.SAFE);
    }

    public static int getBlackState(Grid grid, List<Piece> pieces, King blackKing) {
        return (blackKing == null) ? ChessConstants.SAFE : (blackTrapped(grid, pieces, blackKing)) ? ((blackKing.inCheck(grid)) ? ChessConstants.CHECKMATED : ChessConstants.STALEMATED) : ((blackKing.inCheck(grid)) ? ChessConstants.CHECKED : ChessConstants.SAFE);
    }

    private static boolean whiteTrapped(Grid grid, List<Piece> pieces, King whiteKing) {
        for (int pieceIndex = 0, numberOfPieces = pieces.size(); pieceIndex != numberOfPieces; ++pieceIndex) {
            Piece piece = pieces.get(pieceIndex);
            if (piece.isWhite()) {
                int previousRow = piece.getRow();
                int previousColumn = piece.getColumn();
                Tile previousTile = grid.getTile(previousRow, previousColumn);
                if (piece.isKing()) {
                    Tile leftKingCastleTile = grid.getTile(7, 2);
                    Tile rightKingCastleTile = grid.getTile(7, 6);
                    List<Tile> castleTiles = ((King) piece).getCastleTiles(grid);
                    for (int index = 0; index != castleTiles.size(); ++index) {
                        if (castleTiles.get(index).sameLocation(leftKingCastleTile)) {
                            Tile leftRookTile = grid.getTile(7, 0);
                            Piece leftRook = leftRookTile.getOccupant();
                            Tile leftRookCastleTile = grid.getTile(7, 3);
                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            leftKingCastleTile.setOccupant(piece);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                leftRookTile.setOccupant(leftRook);
                                leftKingCastleTile.removeOccupant();
                                leftRookCastleTile.removeOccupant();
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            leftRookTile.setOccupant(leftRook);
                            leftKingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile rightRookTile = grid.getTile(7, 7);
                            Piece rightRook = rightRookTile.getOccupant();
                            Tile rightRookCastleTile = grid.getTile(7, 5);
                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            rightKingCastleTile.setOccupant(piece);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                rightRookTile.setOccupant(rightRook);
                                rightKingCastleTile.removeOccupant();
                                rightRookCastleTile.removeOccupant();
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            rightRookTile.setOccupant(rightRook);
                            rightKingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                    }
                }
                /*
                else if (piece.isPawn()) {
                    List<Tile> enPassantTiles = ((Pawn) piece).getEnPassantTiles(grid);
                    for (int index = 0; index != enPassantTiles.size(); ++index) {
                        Tile enPassantTile = enPassantTiles.get(index);
                        if (enPassantTile.getColumn() < previousColumn) {
                            Tile blackPawnTile = grid.getTile(previousRow, previousColumn - 1);
                            Piece blackPawn = blackPawnTile.getOccupant();
                            previousTile.removeOccupant();
                            blackPawnTile.removeOccupant();
                            enPassantTile.setOccupant(piece);
                            int removeIndex = pieces.indexOf(blackPawn);
                            pieces.remove(removeIndex);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, blackPawn);
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            blackPawnTile.setOccupant(blackPawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, blackPawn);
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                            Piece blackPawn = blackPawnTile.getOccupant();
                            previousTile.removeOccupant();
                            blackPawnTile.removeOccupant();
                            enPassantTile.setOccupant(piece);
                            int removeIndex = pieces.indexOf(blackPawn);
                            pieces.remove(removeIndex);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, blackPawn);
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            blackPawnTile.setOccupant(blackPawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, blackPawn);
                            grid.setProtections(pieces);
                        }
                    }
                }
                 */
                List<Tile> moveTiles = piece.getMoveTiles(grid);
                for (int index = 0; index != moveTiles.size(); ++index) {
                    Tile moveTile = moveTiles.get(index);
                    previousTile.removeOccupant();
                    moveTile.setOccupant(piece);
                    grid.setProtections(pieces);
                    if (!whiteKing.inCheck(grid)) {
                        previousTile.setOccupant(piece);
                        moveTile.removeOccupant();
                        grid.setProtections(pieces);
                        return false;
                    }
                    previousTile.setOccupant(piece);
                    moveTile.removeOccupant();
                    grid.setProtections(pieces);
                }
                List<Tile> attackTiles = piece.getAttackTiles(grid);
                for (int index = 0; index != attackTiles.size(); ++index) {
                    Tile attackTile = attackTiles.get(index);
                    Piece enemy = attackTile.getOccupant();
                    if (enemy.isKing()) {
                        continue;
                    }
                    previousTile.removeOccupant();
                    attackTile.setOccupant(piece);
                    int removeIndex = pieces.indexOf(enemy);
                    pieces.remove(removeIndex);
                    grid.setProtections(pieces);
                    if (!whiteKing.inCheck(grid)) {
                        previousTile.setOccupant(piece);
                        attackTile.setOccupant(enemy);
                        pieces.add(removeIndex, enemy);
                        grid.setProtections(pieces);
                        return false;
                    }
                    previousTile.setOccupant(piece);
                    attackTile.setOccupant(enemy);
                    pieces.add(removeIndex, enemy);
                    grid.setProtections(pieces);
                }
            }
        }
        return true;
    }

    private static boolean blackTrapped(Grid grid, List<Piece> pieces, King blackKing) {
        for (int pieceIndex = 0, numberOfPieces = pieces.size(); pieceIndex != numberOfPieces; ++pieceIndex) {
            Piece piece = pieces.get(pieceIndex);
            if (piece.isBlack()) {
                int previousRow = piece.getRow();
                int previousColumn = piece.getColumn();
                Tile previousTile = grid.getTile(previousRow, previousColumn);
                if (piece.isKing()) {
                    Tile leftKingCastleTile = grid.getTile(0, 2);
                    Tile rightKingCastleTile = grid.getTile(0, 6);
                    List<Tile> castleTiles = ((King) piece).getCastleTiles(grid);
                    for (int index = 0; index != castleTiles.size(); ++index) {
                        if (castleTiles.get(index).sameLocation(leftKingCastleTile)) {
                            Tile leftRookTile = grid.getTile(0, 0);
                            Piece leftRook = leftRookTile.getOccupant();
                            Tile leftRookCastleTile = grid.getTile(0, 3);
                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            leftKingCastleTile.setOccupant(piece);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                leftRookTile.setOccupant(leftRook);
                                leftKingCastleTile.removeOccupant();
                                leftRookCastleTile.removeOccupant();
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            leftRookTile.setOccupant(leftRook);
                            leftKingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile rightRookTile = grid.getTile(0, 7);
                            Piece rightRook = rightRookTile.getOccupant();
                            Tile rightRookCastleTile = grid.getTile(0, 5);
                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            rightKingCastleTile.setOccupant(piece);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                rightRookTile.setOccupant(rightRook);
                                rightKingCastleTile.removeOccupant();
                                rightRookCastleTile.removeOccupant();
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            rightRookTile.setOccupant(rightRook);
                            rightKingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                    }
                }
                /*
                else if (piece.isPawn()) {
                    List<Tile> enPassantTiles = ((Pawn) piece).getEnPassantTiles(grid);
                    for (int index = 0; index != enPassantTiles.size(); ++index) {
                        Tile enPassantTile = enPassantTiles.get(index);
                        if (enPassantTile.getColumn() < previousColumn) {
                            Tile whitePawnTile = grid.getTile(previousRow, previousColumn - 1);
                            Piece whitePawn = whitePawnTile.getOccupant();
                            previousTile.removeOccupant();
                            whitePawnTile.removeOccupant();
                            enPassantTile.setOccupant(piece);
                            int removeIndex = pieces.indexOf(whitePawn);
                            pieces.remove(removeIndex);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            whitePawnTile.setOccupant(whitePawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, whitePawn);
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                            Piece whitePawn = whitePawnTile.getOccupant();
                            previousTile.removeOccupant();
                            whitePawnTile.removeOccupant();
                            enPassantTile.setOccupant(piece);
                            int removeIndex = pieces.indexOf(whitePawn);
                            pieces.remove(removeIndex);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                previousTile.setOccupant(piece);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                                return false;
                            }
                            previousTile.setOccupant(piece);
                            whitePawnTile.setOccupant(whitePawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, whitePawn);
                            grid.setProtections(pieces);
                        }
                    }
                }
                 */
                List<Tile> moveTiles = piece.getMoveTiles(grid);
                for (int index = 0; index != moveTiles.size(); ++index) {
                    Tile moveTile = moveTiles.get(index);
                    previousTile.removeOccupant();
                    moveTile.setOccupant(piece);
                    grid.setProtections(pieces);
                    if (!blackKing.inCheck(grid)) {
                        previousTile.setOccupant(piece);
                        moveTile.removeOccupant();
                        grid.setProtections(pieces);
                        return false;
                    }
                    previousTile.setOccupant(piece);
                    moveTile.removeOccupant();
                    grid.setProtections(pieces);
                }
                List<Tile> attackTiles = piece.getAttackTiles(grid);
                for (int index = 0; index != attackTiles.size(); ++index) {
                    Tile attackTile = attackTiles.get(index);
                    Piece enemy = attackTile.getOccupant();
                    if (enemy.isKing()) {
                        continue;
                    }
                    previousTile.removeOccupant();
                    attackTile.setOccupant(piece);
                    int removeIndex = pieces.indexOf(enemy);
                    pieces.remove(removeIndex);
                    grid.setProtections(pieces);
                    if (!blackKing.inCheck(grid)) {
                        previousTile.setOccupant(piece);
                        attackTile.setOccupant(enemy);
                        pieces.add(removeIndex, enemy);
                        grid.setProtections(pieces);
                        return false;
                    }
                    previousTile.setOccupant(piece);
                    attackTile.setOccupant(enemy);
                    pieces.add(removeIndex, enemy);
                    grid.setProtections(pieces);
                }
            }
        }
        return true;
    }
}