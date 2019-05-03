package Engine;

import static Util.ChessConstants.LINEAR_LENGTH;
import java.util.ArrayList;
import java.util.List;

public final class ExplicitBoard {
    
    final Grid grid;

    final King whiteKing;
    final List<Pawn> whitePawns = new ArrayList<>(8);
    final List<Knight> whiteKnights = new ArrayList<>(2);
    final List<Bishop> whiteBishops = new ArrayList<>(2);
    final List<Rook> whiteRooks = new ArrayList<>(2);
    final List<Queen> whiteQueens = new ArrayList<>(1);

    final King blackKing;
    final List<Pawn> blackPawns = new ArrayList<>(8);
    final List<Knight> blackKnights = new ArrayList<>(2);
    final List<Bishop> blackBishops = new ArrayList<>(2);
    final List<Rook> blackRooks = new ArrayList<>(2);
    final List<Queen> blackQueens = new ArrayList<>(1);
    
    public ExplicitBoard(Grid board) {
        grid = board;
        King white = null, black = null;
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            Piece piece = board.getTile(index).getOccupant();
            if (piece != null) {
                if (piece.isWhite()) {
                    //pawns and queens will occur most often
                    //so put them first in the if branches
                    //to reduce overhead from falling through all the way
                    if (piece.isPawn()) {
                        whitePawns.add((Pawn) piece);
                    }
                    else if (piece.isQueen()) {
                        whiteQueens.add((Queen) piece);
                    }
                    else if (piece.isRook()) {
                        whiteRooks.add((Rook) piece);
                    }
                    else if (piece.isBishop()) {
                        whiteBishops.add((Bishop) piece);
                    }
                    else if (piece.isKnight()) {
                        whiteKnights.add((Knight) piece);
                    }
                    else {
                        white = (King) piece;
                    }
                }
                else {
                    //pawns and queens will occur most often
                    //so put them first in the if branches
                    //to reduce overhead from falling through all the way
                    if (piece.isPawn()) {
                        blackPawns.add((Pawn) piece);
                    }
                    else if (piece.isQueen()) {
                        blackQueens.add((Queen) piece);
                    }
                    else if (piece.isRook()) {
                        blackRooks.add((Rook) piece);
                    }
                    else if (piece.isBishop()) {
                        blackBishops.add((Bishop) piece);
                    }
                    else if (piece.isKnight()) {
                        blackKnights.add((Knight) piece);
                    }
                    else {
                        black = (King) piece;
                    }
                }
            }
        }
        whiteKing = white;
        blackKing = black;
    }
    
    //not a copy constructor
    public ExplicitBoard(Board board) {
        this(new Grid(board.grid));
    }
}