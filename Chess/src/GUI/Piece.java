package GUI;

import Util.ChessConstants;
import static Util.Constants.SPACE;
import Util.ImageUtils;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("EqualsAndHashcode")
public abstract class Piece extends Area implements Comparable<Piece>, Cloneable {
   
    private static final Image WHITE_PAWN = ImageUtils.readGIFImage("WhitePawn3D");
    private static final Image WHITE_KNIGHT = ImageUtils.readGIFImage("WhiteKnight3D");
    private static final Image WHITE_BISHOP = ImageUtils.readGIFImage("WhiteBishop3D");
    private static final Image WHITE_ROOK = ImageUtils.readGIFImage("WhiteRook3D");
    private static final Image WHITE_QUEEN = ImageUtils.readGIFImage("WhiteQueen3D");
    private static final Image WHITE_KING = ImageUtils.readGIFImage("WhiteKing3D");
    
    private static final Image BLACK_PAWN = ImageUtils.readGIFImage("BlackPawn3D");
    private static final Image BLACK_KNIGHT = ImageUtils.readGIFImage("BlackKnight3D");
    private static final Image BLACK_BISHOP = ImageUtils.readGIFImage("BlackBishop3D");
    private static final Image BLACK_ROOK = ImageUtils.readGIFImage("BlackRook3D");
    private static final Image BLACK_QUEEN = ImageUtils.readGIFImage("BlackQueen3D");
    private static final Image BLACK_KING = ImageUtils.readGIFImage("BlackKing3D");
    
    private final Image image;
    private boolean selected = false;
    private final boolean color;
    private int moves = 0;
    private final String type;

    protected Piece(int x, int y, int width, int height, int row, int column, boolean color, String type) {
        super(x, y, width, height, row, column);
        if (this.color = color) {
            switch (this.type = type) {
                case ChessConstants.PAWN: {
                    image = WHITE_PAWN;
                    break;
                }
                case ChessConstants.KNIGHT: {
                    image = WHITE_KNIGHT;
                    break;
                }
                case ChessConstants.BISHOP: {
                    image = WHITE_BISHOP;
                    break;
                }
                case ChessConstants.ROOK: {
                    image = WHITE_ROOK;
                    break; 
                }
                case ChessConstants.QUEEN: {
                    image = WHITE_QUEEN;
                    break;
                }
                case ChessConstants.KING: {
                    image = WHITE_KING;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid Name or Type.");
                }
            }
        }
        else {
            switch (this.type = type) {
                case ChessConstants.PAWN: {
                    image = BLACK_PAWN;
                    break;
                }
                case ChessConstants.KNIGHT: {
                    image = BLACK_KNIGHT;
                    break;
                }
                case ChessConstants.BISHOP: {
                    image = BLACK_BISHOP;
                    break;
                }
                case ChessConstants.ROOK: {
                    image = BLACK_ROOK;
                    break; 
                }
                case ChessConstants.QUEEN: {
                    image = BLACK_QUEEN;
                    break;
                }
                case ChessConstants.KING: {
                    image = BLACK_KING;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid Name or Type.");
                }
            }
        }
    }
    
    protected Piece(int row, int column, boolean color, String type) {
        this(0, 0, 0, 0, row, column, color, type);
    }
    
    @Override
    public abstract Piece clone();
    
    public final boolean isSelected() {
        return selected;
    }
    
    public final void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public final boolean isWhite() {
        return color;
    }
    
    public final boolean isBlack() {
        return !color;
    }
    
    public final boolean isAlly(Piece other) {
        return color == other.color;
    }
    
    public final int getMoveCount() {
        return moves;
    }
    
    public final void setMoveCount(int moves) {
        this.moves = moves;
    }
    
    public final void increaseMoveCount() {
        ++moves;
    }
    
    public final void decreaseMoveCount() {
        --moves;
    }
    
    public final boolean hasMoved() {
        return moves != 0;
    }
 
    //polymorphic trick hehe
    public boolean isPawn() {
        return false;
    }
    
    public boolean isRook() {
        return false;
    }
    
    public boolean isKnight() {
        return false;
    }
    
    public boolean isBishop() {
        return false;
    }
    
    public boolean isQueen() {
        return false;
    }
    
    public boolean isKing() {
        return false;
    }
    
    public abstract char getSymbol();
    
    @Override
    public final void render(Graphics context) {
        context.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
    }

    public List<Tile> getLegalMoves(Grid grid) {
        List<Tile> legalTiles = getMoveTiles(grid);
      
        for (int index = 0, size = legalTiles.size(); index != size; ++index) {
            legalTiles.get(index).setTint(Board.MOVE);
        }

        List<Tile> attackTiles = getAttackTiles(grid);
        
        for (int index = 0, size = attackTiles.size(); index != size; ++index) {
            Tile attackTile = attackTiles.get(index);
            attackTile.setTint(Board.ATTACK);
            legalTiles.add(attackTile);
        }
        
        return legalTiles;
    }

    public abstract List<Tile> getMoveTiles(Grid grid);
    
    public abstract List<Tile> getAttackTiles(Grid grid);
    
    /**
     * Only method involved with King Piercing and never should be
     * used for active display, this is passive function.
     * Gets ALL the areas that this piece can move to and attack at
     * and if one of the attacked piece is King, if the Piece is piercable 
     * (Queen, Rook, Bishop) then add another box to its protected areas, else 
     * stop.
     * 
     * @param grid
     * @return
     */
    public abstract List<Tile> getProtectedTiles(Grid grid);

    public abstract void setProtectedTiles(Grid grid);
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public boolean justMadeDoubleJump() {
        throw new UnsupportedOperationException();
    }

    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public void setJustMadeDoubleJump(boolean doubleJumpJustPerformed) {
        throw new UnsupportedOperationException();
    }
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public Tile getLeftEnPassantTile(Grid grid) {
        throw new UnsupportedOperationException();
    }
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public Tile getRightEnPassantTile(Grid grid) {
        throw new UnsupportedOperationException();
    }
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public List<Tile> getEnPassantTiles(Grid grid) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Compares this piece vs the given piece by their 
     * exact value.
     * @param other Another piece object.
     * @return 
     */
    @Override
    public final int compareTo(Piece other) {
        return getValue() - other.getValue();
    }
    
    public final int getValue() {
        if (isPawn()) {
            return ChessConstants.PAWN_VALUE;
        }
        if (isKnight()) {
            return ChessConstants.KNIGHT_VALUE;
        }
        if (isBishop()) {
            return ChessConstants.BISHOP_VALUE;
        }
        if (isRook()){
            return ChessConstants.ROOK_VALUE;
        }
        if (isQueen()) {
            return ChessConstants.QUEEN_VALUE;
        }
        return ChessConstants.KING_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Piece)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        Piece cast = (Piece) obj;
        return selected == cast.selected
                && color == cast.color
                && moves == cast.moves
                && type.equals(cast.type);
    }

    public final String getType() {
        return type;
    }
    
    public final String getName() {
        return (color) ? "White " + type : "Black " + type;
    }
    
    public final String getColor() {
        return color ? ChessConstants.WHITE : ChessConstants.BLACK;
    }

    public String encode() {
        return "(" + color + "," + getType() + "," + getRow() + "," + getColumn() + ")";
    }
    
    public String toEngineString() {
        return getRow() + SPACE + getColumn() + SPACE + getMoveCount() + SPACE + isWhite() + SPACE + getType();
    }
    
    public static final Piece readEngineString(String str) {
        String[] split = str.split(SPACE);
        switch (split[4]) {
            case ChessConstants.PAWN: {
                Pawn pawn = new Pawn(Integer.parseInt(split[0]), Integer.parseInt(split[1]), "true".equals(split[3]));
                pawn.setMoveCount(Integer.parseInt(split[2]));
                pawn.setJustMadeDoubleJump(Boolean.parseBoolean(split[5]));
                return pawn;
            }
            case ChessConstants.KNIGHT: {
                Knight knight = new Knight(Integer.parseInt(split[0]), Integer.parseInt(split[1]), "true".equals(split[3]));
                knight.setMoveCount(Integer.parseInt(split[2]));
                return knight;
            }
            case ChessConstants.BISHOP: {
                Bishop bishop = new Bishop(Integer.parseInt(split[0]), Integer.parseInt(split[1]), "true".equals(split[3]));
                bishop.setMoveCount(Integer.parseInt(split[2]));
                return bishop;
            }
            case ChessConstants.ROOK: {
                Rook rook = new Rook(Integer.parseInt(split[0]), Integer.parseInt(split[1]), "true".equals(split[3]));
                rook.setMoveCount(Integer.parseInt(split[2]));
                return rook;
            }
            case ChessConstants.QUEEN: {
                Queen queen = new Queen(Integer.parseInt(split[0]), Integer.parseInt(split[1]), "true".equals(split[3]));
                queen.setMoveCount(Integer.parseInt(split[2]));
                return queen;
            }
            case ChessConstants.KING: {
                King king = new King(Integer.parseInt(split[0]), Integer.parseInt(split[1]), "true".equals(split[3]));
                king.setMoveCount(Integer.parseInt(split[2]));
                return king;
            }
        }
        return null;
    }
    
    /**
     * Saves the state of this piece object as a string
     * meant to be written to a file or sent over a network.
     * @return A string containing the properties of this piece object,
     * each property nested within '[ ]'. 
     */
    public String toOutputString() {
        return "[" + type + "][" + color + "][" + selected + "][" + moves + "][" + getRow() + "][" + getColumn() + "]";
    }
    
    /**
     * Calls Piece.toOutputString()
     * @return Piece.toOutputString()
     */
    @Override
    public final String toString() {
        return toOutputString();
    }

    /**
     * Reads a piece object from an saved string, which should
     * originate from a file or a network.
     * @param str The input string.
     * @return A copy of a piece object read from a string.
     */
    public static final Piece readPiece(String str) {
        List<String> data = parseData(str);
        //note that x, y, width, height are ignored.
        switch (data.get(0)) {
            case ChessConstants.PAWN: {
                Pawn pawn = new Pawn(Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Boolean.parseBoolean(data.get(1)));
                pawn.setSelected(Boolean.parseBoolean(data.get(2)));
                pawn.setMoveCount(Integer.parseInt(data.get(3)));
                pawn.setJustMadeDoubleJump(Boolean.parseBoolean(data.get(6)));
                return pawn;
            }
            case ChessConstants.KNIGHT: {
                Knight knight = new Knight(Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Boolean.parseBoolean(data.get(1)));
                knight.setSelected(Boolean.parseBoolean(data.get(2)));
                knight.setMoveCount(Integer.parseInt(data.get(3)));
                return knight;
            }
            case ChessConstants.BISHOP: {
                Bishop bishop = new Bishop(Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Boolean.parseBoolean(data.get(1)));
                bishop.setSelected(Boolean.parseBoolean(data.get(2)));
                bishop.setMoveCount(Integer.parseInt(data.get(3)));
                return bishop;
            }
            case ChessConstants.ROOK: {
                Rook rook = new Rook(Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Boolean.parseBoolean(data.get(1)));
                rook.setSelected(Boolean.parseBoolean(data.get(2)));
                rook.setMoveCount(Integer.parseInt(data.get(3)));
                return rook;
            }
            case ChessConstants.QUEEN: {
                Queen queen = new Queen(Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Boolean.parseBoolean(data.get(1)));
                queen.setSelected(Boolean.parseBoolean(data.get(2)));
                queen.setMoveCount(Integer.parseInt(data.get(3)));
                return queen;
            }
            case ChessConstants.KING: {
                King king = new King(Integer.parseInt(data.get(4)), Integer.parseInt(data.get(5)), Boolean.parseBoolean(data.get(1)));
                king.setSelected(Boolean.parseBoolean(data.get(2)));
                king.setMoveCount(Integer.parseInt(data.get(3)));
                return king;
            }
        }
        return null;
    }

    private static List<String> parseData(String str) {
        final int length = str.length();
        List<String> list = new ArrayList<>();
        StringBuilder data = new StringBuilder();
        for (int outer = 0; outer < length; ++outer) {
            if (str.charAt(outer) == '[') {
                for (int inner = outer + 1; inner < length;) {
                    if (str.charAt(inner) == ']') {
                        break;
                    }
                    data.append(str.substring(inner, ++inner));
                }
                list.add(data.toString());
                outer += data.length() + 1;
                data.setLength(0);
            }
        }
        return list;
    }
}