package Engine;

import static Util.ChessConstants.BISHOP;
import static Util.ChessConstants.KING;
import static Util.ChessConstants.KNIGHT;
import static Util.ChessConstants.PAWN;
import static Util.ChessConstants.QUEEN;
import static Util.ChessConstants.ROOK;
import static Util.Constants.SPACE;
import Util.Converter;

/**
 * Utility class to convert a GUI.Piece format text to 
 * a Engine.Piece object and vise versa.
 */
public final class PieceConverter extends Converter<String, Piece> {
    
    public static final PieceConverter PIECE_CONVERTER = new PieceConverter();
    
    private PieceConverter() {
        
    }

    @Override
    public final Piece convertForward(String str) {
        String[] read = str.split(SPACE);
        switch (read[4]) {
            //arrange by most common
            case PAWN: {
                Pawn pawn = new Pawn(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                pawn.setJustMadeDoubleJump(Boolean.parseBoolean(read[5]));
                return pawn;
            }
            case QUEEN: {
                return new Queen(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
            }
            case ROOK: {
                return new Rook(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
            }
            case BISHOP: {
                return new Bishop(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
            }
            case KNIGHT: {
                return new Knight(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
            }
            case KING: {
                return new King(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
            }
        }
        return null;
    }

    @Override
    public final String convertBackward(Piece piece) {
        String result = piece.getRow() 
                + SPACE
                + piece.getColumn()
                + SPACE
                + piece.getMoveCount()
                + SPACE
                + piece.isWhite()
                + SPACE
                + piece.getType();
        return (piece.isPawn()) ? (result + SPACE + piece.justMadeDoubleJump()) : (result);
    }
}