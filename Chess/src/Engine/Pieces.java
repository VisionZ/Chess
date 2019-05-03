package Engine;

import Util.ChessConstants;
import static Util.Constants.SPACE;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for manipulating chess pieces.
 *
 * @author Will
 */
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
     * the list and are then are ordered from greatest to least. Then the Black
     * pieces are moved after the white pieces and are then ordered from least
     * to greatest. Note that this method is NOT thread-safe and cannot be
     * safely accessed by multiple threads.
     *
     * @param pieces The given list of pieces.
     */
    synchronized static final void sort(List<Piece> pieces) {
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
    
    static final List<Piece> getBlackSortedCopy(List<Piece> blacks) {
        List<Piece> copy = new ArrayList<>(blacks);
        copy.sort(BEST_PIECES_LAST);
        return copy;
    }
    
    static final List<Piece> getWhiteSortedCopy(List<Piece> whites) {
        List<Piece> copy = new ArrayList<>(whites);
        copy.sort(BEST_PIECES_FIRST);
        return copy;
    }
    
    static final int indexOf(List<Piece> pieces, Piece piece) {
        for (int index = 0, size = pieces.size(); index < size; ++index) {
            if (piece == pieces.get(index)) { //light comparison is faster
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns the index of a piece object in a list of pieces and removes that
     * piece from the list, if the list contains that piece.
     *
     * @param pieces The list of pieces.
     * @param piece The piece to remove.
     * @return The index of the given piece in the given list of pieces or
     * {@code -1} if the list does not contain the given piece.
     */
    static final int remove(List<Piece> pieces, Piece piece) {
        for (int index = (pieces.size() - 1); index >= 0; --index) {
            if (piece == pieces.get(index)) { //light comparison is faster
                pieces.remove(index);
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns a deep copy of a given list of pieces.
     *
     * @param pieces The given list of pieces.
     * @return A deep copy of a given list of pieces, the returned list and its
     * elements may be freely modified without affecting the original list and
     * its elements.
     */
    static final List<Piece> getDeepCopy(final List<Piece> pieces) {
        final int size = pieces.size();
        final List<Piece> copy = new LinkedList<>();
        for (int index = 0; index != size; ++index) {
            copy.add(pieces.get(index).clone());
        }
        return copy;
    }

    /**
     * Finds and returns the white king object from the given list of white
     * pieces.
     *
     * @param whites The given list of white pieces, which must be sorted.
     * @return The white king object from the given list of white pieces.
     */
    static final King getWhiteKing(List<Piece> whites) {
        return (King) whites.get(0);
    }

    /**
     * Finds and returns the black king object from the given list of white
     * pieces.
     *
     * @param whites The given list of black pieces, which must be sorted.
     * @return The black king object from the given list of black pieces.
     */
    static final King getBlackKing(List<Piece> blacks) {
        return (King) blacks.get(0);
    }

    /**
     * Returns a list of white pieces from the given list of pieces. The given
     * list of pieces must be sorted as dictated by the {
     *
     * @see sort(java.util.List)} method.
     * @param pieces The given list of pieces.
     * @return A list of white pieces from the given list of pieces.
     */
    static final List<Piece> getWhite(List<Piece> pieces) {
        List<Piece> whites = new ArrayList<>(AI.NUMBER_OF_WHITE_PIECES);
        for (int index = 0;; ++index) { //no loop condition necessary, list must be sorted
            Piece piece = pieces.get(index);
            if (piece.isWhite()) {
                whites.add(piece);
            }
            else {
                //expect cutoff, the big pieces list must be sorted
                return whites;
            }
        }
    }

    /**
     * Returns a list of black pieces from the given list of pieces. The given
     * list of pieces must be sorted as dictated by the {
     *
     * @see sort(java.util.List)} method.
     * @param pieces The given list of pieces.
     * @return A list of black pieces from the given list of pieces.
     */
    static final List<Piece> getBlack(List<Piece> pieces) {
        List<Piece> blacks = new ArrayList<>(AI.NUMBER_OF_BLACK_PIECES);
        for (int index = (pieces.size() - 1);; --index) { //no loop condition necessary, list must be sorted
            Piece piece = pieces.get(index);
            if (piece.isBlack()) {
                blacks.add(piece);
            }
            else {
                //expect cutoff, the big pieces list must be sorted
                return blacks;
            }
        }
    }

    /**
     * Method that should be called immediately after White successfully
     * finishes his/her turn. If a Black Pawn made a double jump just before
     * White's last turn, and has not been captured En Passant, it is now
     * permanently immune from being targeted by En Passant.
     *
     * @param blackPieces The enemy black pieces.
     * @return The Black Pawn that has just performed a double jump which has not been
     * captured during White's turn. This Black Pawn is now immune to being targeted En Passant.
     * @see checkBlackEnPassantRights()
     */
    public static final Piece checkWhiteEnPassantRights(List<Piece> blackPieces) {
        for (int index = (blackPieces.size() - 1); index >= 1; --index) { //0 index will always be king
            Piece blackPiece = blackPieces.get(index);
            if (blackPiece.justMadeDoubleJump()) {
                blackPiece.setJustMadeDoubleJump(false);
                return blackPiece;
            }
        }
        return null;
    }

    /**
     * Method that should be called immediately after Black successfully
     * finishes his/her turn. If a White Pawn made a double jump just before
     * Black's last turn, and has not been captured En Passant, it is now
     * permanently immune from being targeted by En Passant.
     *
     * @param whitePieces The enemy White pieces.
     * @return The White Pawn that has just performed a double jump which has
     * not been captured during Black's turn. This White Pawn is now immune to
     * being targeted En Passant.
     * @see checkWhiteEnPassantRights()
     */
    public static final Piece checkBlackEnPassantRights(List<Piece> whitePieces) {
        for (int index = (whitePieces.size() - 1); index >= 1; --index) { //0 index will always be king
            Piece whitePiece = whitePieces.get(index);
            if (whitePiece.justMadeDoubleJump()) {
                whitePiece.setJustMadeDoubleJump(false);
                return whitePiece;
            }
        }
        return null;
    }

    //slower & secure versions since they must work with all types of pieces
    public static final Piece checkWhiteEnPassantRightsSlow(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn() && piece.isBlack() && piece.justMadeDoubleJump()) {
                piece.setJustMadeDoubleJump(false);
                return piece;
            }
        }
        return null;
    }

    public static final Piece checkBlackEnPassantRightsSlow(List<Piece> pieces) {
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn() && piece.isWhite() && piece.justMadeDoubleJump()) {
                piece.setJustMadeDoubleJump(false);
                return piece;
            }
        }
        return null;
    }

    private static final StringBuilder ENCODER = new StringBuilder(100);

    //encode format for SEEN_POSITIONS is pieces with space between them and no trailing space at end
    static final String encode(List<Piece> pieces) {
        ENCODER.setLength(0);
        for (Iterator<Piece> it = pieces.iterator(); it.hasNext();) {
            ENCODER.append(it.next().encode());
            if (it.hasNext()) {
                ENCODER.append(SPACE);
            }
            else {
                break;
            }
        }
        return ENCODER.toString();
    }

    static final String encode(List<Piece> whites, List<Piece> blacks) {
        ENCODER.setLength(0);
        for (int index = 0, size = whites.size(); index != size; ++index) {
            ENCODER.append(whites.get(index).encode()).append(SPACE);
        }
        for (Iterator<Piece> it = blacks.iterator(); it.hasNext();) {
            ENCODER.append(it.next().encode());
            if (it.hasNext()) {
                ENCODER.append(SPACE);
            }
            else {
                break;
            }
        }
        return ENCODER.toString();
    }

    @Deprecated
    static final int getWhiteState(Grid grid, List<Piece> pieces, List<Piece> whites, King whiteKing) {
        return (whiteTrapped(grid, pieces, whites, whiteKing)) ? ((whiteKing.inCheck(grid)) ? ChessConstants.CHECKMATED : ChessConstants.STALEMATED) : ChessConstants.SAFE;
    }

    @Deprecated
    static final int getBlackState(Grid grid, List<Piece> pieces, List<Piece> blacks, King blackKing) {
        return (blackTrapped(grid, pieces, blacks, blackKing)) ? ((blackKing.inCheck(grid)) ? ChessConstants.CHECKMATED : ChessConstants.STALEMATED) : ChessConstants.SAFE;
    }

    @Deprecated
    static final boolean whiteTrapped(Grid grid, List<Piece> pieces, List<Piece> whites, King whiteKing) {
        //Grid clone = new Grid(grid);
        for (int pieceIndex = 0, size = whites.size(); pieceIndex != size; ++pieceIndex) {
            Piece white = whites.get(pieceIndex);
            int previousRow = white.getRow();
            int previousColumn = white.getColumn();
            Tile previousTile = grid.getTile(previousRow, previousColumn);
            Iterator<Tile> moveTiles = white.getMoveTiles(grid);
            while (moveTiles.hasNext()) {
                Tile moveTile = moveTiles.next();
                previousTile.removeOccupant();
                moveTile.setOccupant(white);
                grid.setProtections(pieces);
                if (!whiteKing.inCheck(grid)) {
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    grid.setProtections(pieces);
                    //grid.equals(clone);
                    return false;
                }
                previousTile.setOccupant(white);
                moveTile.removeOccupant();
                grid.setProtections(pieces);
                //grid.equals(clone);
            }
            List<Tile> attackTiles = white.getAttackTiles(grid);
            for (int index = 0; index != attackTiles.size(); ++index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                previousTile.removeOccupant();
                attackTile.setOccupant(white);
                int removeIndex = pieces.indexOf(enemy);
                pieces.remove(removeIndex);
                grid.setProtections(pieces);
                if (!whiteKing.inCheck(grid)) {
                    previousTile.setOccupant(white);
                    attackTile.setOccupant(enemy);
                    pieces.add(removeIndex, enemy);
                    grid.setProtections(pieces);
                    //grid.equals(clone);
                    return false;
                }
                previousTile.setOccupant(white);
                attackTile.setOccupant(enemy);
                pieces.add(removeIndex, enemy);
                grid.setProtections(pieces);
                //grid.equals(clone);
            }
            if (white.isKing()) {
                Tile leftKingCastleTile = grid.getTile(7, 2);
                Tile rightKingCastleTile = grid.getTile(7, 6);
                List<Tile> castleTiles = ((King) white).getCastleTiles(grid);
                for (int index = 0; index != castleTiles.size(); ++index) {
                    if (castleTiles.get(index).sameLocation(leftKingCastleTile)) {
                        Tile leftRookTile = grid.getTile(7, 0);
                        Piece leftRook = leftRookTile.getOccupant();
                        Tile leftRookCastleTile = grid.getTile(7, 3);
                        previousTile.removeOccupant();
                        leftRookTile.removeOccupant();
                        leftKingCastleTile.setOccupant(white);
                        leftRookCastleTile.setOccupant(leftRook);
                        grid.setProtections(pieces);
                        if (!whiteKing.inCheck(grid)) {
                            previousTile.setOccupant(white);
                            leftRookTile.setOccupant(leftRook);
                            leftKingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(white);
                        leftRookTile.setOccupant(leftRook);
                        leftKingCastleTile.removeOccupant();
                        leftRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                    else {
                        Tile rightRookTile = grid.getTile(7, 7);
                        Piece rightRook = rightRookTile.getOccupant();
                        Tile rightRookCastleTile = grid.getTile(7, 5);
                        previousTile.removeOccupant();
                        rightRookTile.removeOccupant();
                        rightKingCastleTile.setOccupant(white);
                        rightRookCastleTile.setOccupant(rightRook);
                        grid.setProtections(pieces);
                        if (!whiteKing.inCheck(grid)) {
                            previousTile.setOccupant(white);
                            rightRookTile.setOccupant(rightRook);
                            rightKingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(white);
                        rightRookTile.setOccupant(rightRook);
                        rightKingCastleTile.removeOccupant();
                        rightRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                }
            }
            else if (white.isPawn()) {
                List<Tile> enPassantTiles = ((Pawn) white).getEnPassantTiles(grid);
                for (int index = 0; index != enPassantTiles.size(); ++index) {
                    Tile enPassantTile = enPassantTiles.get(index);
                    if (enPassantTile.getColumn() < previousColumn) {
                        Tile blackPawnTile = grid.getTile(previousRow, previousColumn - 1);
                        Piece blackPawn = blackPawnTile.getOccupant();
                        previousTile.removeOccupant();
                        blackPawnTile.removeOccupant();
                        enPassantTile.setOccupant(white);
                        int removeIndex = pieces.indexOf(blackPawn);
                        pieces.remove(removeIndex);
                        grid.setProtections(pieces);
                        if (!whiteKing.inCheck(grid)) {
                            previousTile.setOccupant(white);
                            blackPawnTile.setOccupant(blackPawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, blackPawn);
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(white);
                        blackPawnTile.setOccupant(blackPawn);
                        enPassantTile.removeOccupant();
                        pieces.add(removeIndex, blackPawn);
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                    else {
                        Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                        Piece blackPawn = blackPawnTile.getOccupant();
                        previousTile.removeOccupant();
                        blackPawnTile.removeOccupant();
                        enPassantTile.setOccupant(white);
                        int removeIndex = pieces.indexOf(blackPawn);
                        pieces.remove(removeIndex);
                        grid.setProtections(pieces);
                        if (!whiteKing.inCheck(grid)) {
                            previousTile.setOccupant(white);
                            blackPawnTile.setOccupant(blackPawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, blackPawn);
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(white);
                        blackPawnTile.setOccupant(blackPawn);
                        enPassantTile.removeOccupant();
                        pieces.add(removeIndex, blackPawn);
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                }
            }
        }
        //grid.equals(clone);
        return true;
    }

    @Deprecated
    static final boolean blackTrapped(Grid grid, List<Piece> pieces, List<Piece> blacks, King blackKing) {
        //Grid clone = new Grid(grid);
        for (int pieceIndex = 0, size = blacks.size(); pieceIndex != size; ++pieceIndex) {
            Piece black = blacks.get(pieceIndex);
            int previousRow = black.getRow();
            int previousColumn = black.getColumn();
            Tile previousTile = grid.getTile(previousRow, previousColumn);
            Iterator<Tile> moveTiles = black.getMoveTiles(grid);
            while (moveTiles.hasNext()) {
                Tile moveTile = moveTiles.next();
                previousTile.removeOccupant();
                moveTile.setOccupant(black);
                grid.setProtections(pieces);
                if (!blackKing.inCheck(grid)) {
                    previousTile.setOccupant(black);
                    moveTile.removeOccupant();
                    grid.setProtections(pieces);
                    //grid.equals(clone);
                    return false;
                }
                previousTile.setOccupant(black);
                moveTile.removeOccupant();
                grid.setProtections(pieces);
                //grid.equals(clone);
            }
            List<Tile> attackTiles = black.getAttackTiles(grid);
            for (int index = 0; index != attackTiles.size(); ++index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                previousTile.removeOccupant();
                attackTile.setOccupant(black);
                int removeIndex = pieces.indexOf(enemy);
                pieces.remove(removeIndex);
                grid.setProtections(pieces);
                if (!blackKing.inCheck(grid)) {
                    previousTile.setOccupant(black);
                    attackTile.setOccupant(enemy);
                    pieces.add(removeIndex, enemy);
                    grid.setProtections(pieces);
                    //grid.equals(clone);
                    return false;
                }
                previousTile.setOccupant(black);
                attackTile.setOccupant(enemy);
                pieces.add(removeIndex, enemy);
                grid.setProtections(pieces);
                //grid.equals(clone);
            }
            if (black.isKing()) {
                Tile leftKingCastleTile = grid.getTile(0, 2);
                Tile rightKingCastleTile = grid.getTile(0, 6);
                List<Tile> castleTiles = ((King) black).getCastleTiles(grid);
                for (int index = 0; index != castleTiles.size(); ++index) {
                    if (castleTiles.get(index).sameLocation(leftKingCastleTile)) {
                        Tile leftRookTile = grid.getTile(0, 0);
                        Piece leftRook = leftRookTile.getOccupant();
                        Tile leftRookCastleTile = grid.getTile(0, 3);
                        previousTile.removeOccupant();
                        leftRookTile.removeOccupant();
                        leftKingCastleTile.setOccupant(black);
                        leftRookCastleTile.setOccupant(leftRook);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            previousTile.setOccupant(black);
                            leftRookTile.setOccupant(leftRook);
                            leftKingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(black);
                        leftRookTile.setOccupant(leftRook);
                        leftKingCastleTile.removeOccupant();
                        leftRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                    else {
                        Tile rightRookTile = grid.getTile(0, 7);
                        Piece rightRook = rightRookTile.getOccupant();
                        Tile rightRookCastleTile = grid.getTile(0, 5);
                        previousTile.removeOccupant();
                        rightRookTile.removeOccupant();
                        rightKingCastleTile.setOccupant(black);
                        rightRookCastleTile.setOccupant(rightRook);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            previousTile.setOccupant(black);
                            rightRookTile.setOccupant(rightRook);
                            rightKingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(black);
                        rightRookTile.setOccupant(rightRook);
                        rightKingCastleTile.removeOccupant();
                        rightRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                }
            }
            else if (black.isPawn()) {
                List<Tile> enPassantTiles = ((Pawn) black).getEnPassantTiles(grid);
                for (int index = 0; index != enPassantTiles.size(); ++index) {
                    Tile enPassantTile = enPassantTiles.get(index);
                    if (enPassantTile.getColumn() < previousColumn) {
                        Tile whitePawnTile = grid.getTile(previousRow, previousColumn - 1);
                        Piece whitePawn = whitePawnTile.getOccupant();
                        previousTile.removeOccupant();
                        whitePawnTile.removeOccupant();
                        enPassantTile.setOccupant(black);
                        int removeIndex = pieces.indexOf(whitePawn);
                        pieces.remove(removeIndex);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            previousTile.setOccupant(black);
                            whitePawnTile.setOccupant(whitePawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, whitePawn);
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(black);
                        whitePawnTile.setOccupant(whitePawn);
                        enPassantTile.removeOccupant();
                        pieces.add(removeIndex, whitePawn);
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                    else {
                        Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                        Piece whitePawn = whitePawnTile.getOccupant();
                        previousTile.removeOccupant();
                        whitePawnTile.removeOccupant();
                        enPassantTile.setOccupant(black);
                        int removeIndex = pieces.indexOf(whitePawn);
                        pieces.remove(removeIndex);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            previousTile.setOccupant(black);
                            whitePawnTile.setOccupant(whitePawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, whitePawn);
                            grid.setProtections(pieces);
                            //grid.equals(clone);
                            return false;
                        }
                        previousTile.setOccupant(black);
                        whitePawnTile.setOccupant(whitePawn);
                        enPassantTile.removeOccupant();
                        pieces.add(removeIndex, whitePawn);
                        grid.setProtections(pieces);
                        //grid.equals(clone);
                    }
                }
            }
        }
        //grid.equals(clone);
        return true;
    }
}