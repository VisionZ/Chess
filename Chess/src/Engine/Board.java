package Engine;

import java.util.List;

//BE CAREFUL WHEN REMOVING PIECES FROM 1 LIST
//IT WILL STILL BE IN ANOTHER LIST
//FOR EXAMPLE: IF YOU REMOVE A BLACK PAWN FROM 
//THE BLACK PIECES LIST IT WILL STILL
//BE IN THE BLACK PAWN LIST
@SuppressWarnings("EqualsAndHashcode")
public final class Board {

    //pieces must be sorted before using any algorithm, since eval needs sorted pieces
    final Grid grid;

    final List<Piece> whites;
    final King whiteKing;
    
    final List<Piece> blacks;
    final King blackKing;

    //shallow copy
    public Board(Grid grid, List<Piece> whitePieces, List<Piece> blackPieces) {
        this.grid = grid;
        whiteKing = Pieces.getWhiteKing(whites = whitePieces);
        blackKing = Pieces.getBlackKing(blacks = blackPieces);
    }
    
    //shallow copy
    public Board(Grid grid) {
        List<Piece> pieces = (this.grid = grid).getPieces();
        Pieces.sort(pieces);
        whiteKing = Pieces.getWhiteKing(whites = Pieces.getWhite(pieces));
        blackKing = Pieces.getBlackKing(blacks = Pieces.getBlack(pieces));
    }

    //deep copy constructor
    public Board(Board board) {
        this(new Grid(board.grid));
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Board)) {
            return false;
        }
        Board board = (Board) obj;
        return whiteKing.equals(board.whiteKing)
                && blackKing.equals(board.blackKing)
                && whites.equals(board.whites)
                && blacks.equals(board.blacks)
                && grid.equals(board.grid); //save slowest operation for last
    }

    public final boolean deepEquals(Board board) {
        return whiteKing.equals(board.whiteKing)
                && blackKing.equals(board.blackKing)
                && whites.equals(board.whites)
                && blacks.equals(board.blacks)
                && grid.deepEquals(board.grid); //save slowest operation for last
    }
}