package Engine;

import static Util.ChessConstants.BLACK;
import static Util.ChessConstants.WHITE;
import static Util.Constants.SPACE;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("EqualsAndHashcode")
public abstract class Piece implements Cloneable, Comparable<Piece>, Locatable {

    private int row;
    private int column;
    private int moveCount;
    private final boolean color;
    
    protected Piece(int r, int c, boolean side) {
        row = r;
        column = c;
        color = side;
    }
    
    protected Piece(int r, int c, int numberOfMoves, boolean side) {
        row = r;
        column = c;
        moveCount = numberOfMoves;
        color = side;
    }

    @Override
    public final int getRow() {
        return row;
    }

    @Override
    public final int getColumn() {
        return column;
    }

    @Override
    public final void setRow(int r) {
        row = r;
    }

    @Override
    public final void setColumn(int c) {
        column = c;
    }

    @Override
    public final void setLocation(int r, int c) {
        row = r;
        column = c;
    }
    
    /**
     * Returns true if this piece is white, false otherwise. This
     * is an convince method.
     * @see isBlack()
     * @return {@code true} if this piece is white, false otherwise.
     */
    public final boolean isWhite() {
        return color;
    }
    
    /**
     * Returns true if this piece is black, false otherwise. This
     * is an convince method.
     * @see isWhite()
     * @return {@code true} if this piece is black, false otherwise.
     */
    public final boolean isBlack() {
        return !color;
    }

    /**
     * Determines whether or not the given piece is an ally or
     * foe of this piece.
     * @param other Another piece object.
     * @return {@code true} if the other piece object is an ally, false otherwise.
     */
    public final boolean isAlly(Piece other) {
        return color == other.color;
    }

    /**
     * Returns true if this piece has moved, false otherwise.
     * @return {@code true} if this piece has moved, false otherwise.
     */
    public final boolean hasMoved() {
        return moveCount != 0;
    }
    
    /**
     * Get the number of times this piece has moved.
     * @see setMoveCount(int)
     * @see increaseMoveCount()
     * @see decreaseMoveCount()
     * @return The number of times this piece has moved.
     */
    public final int getMoveCount() {
        return moveCount;
    }
    
    /**
     * Sets the number of times this piece has moved.
     * @param moveCount 
     */
    public final void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }
    
    /**
     * Increases the number of times this piece has moved by 1.
     * @see setMoveCount(int)
     * @see decreaseMoveCount()
     */
    public final void increaseMoveCount() {
        ++moveCount;
    }

    /**
     * Decreases the number of times this piece has moved by 1.
     * @see setMoveCount(int)
     * @see increaseMoveCount()
     */
    public final void decreaseMoveCount() {
        --moveCount;
    }

    //Keep max checking for this method. During the Alpha Beta Search,
    //we just use reference equality via Pieces.remove() method.
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        // No need to check type, bugs should fail when attempting to cast
        if (!(obj instanceof Piece)) {
            throw new ClassCastException();
        }

        Piece other = (Piece) obj;
        return (row == other.row)
                && (column == other.column)
                && (color == other.color)
                && (moveCount == other.moveCount)
                && (getType().equals(other.getType()));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + row;
        hash = 31 * hash + column;
        hash = 31 * hash + moveCount;
        hash = 31 * hash + (color ? 1 : 0);
        return hash;
    }
    
    //no move counts
    public boolean fastEquals(Piece other) {
        return sameLocationAndColor(other) && getType().equals(other.getType());
    }
    
    public final boolean sameLocationAndColor(Piece other) {
        return (row == other.row) && (column == other.column) && (color == other.color);
    }

    /**
     * Returns an exact deep copy of this piece that is
     * independent of this piece. This policy overrides
     * the original policy of {@link java.lang.Object#clone()}.
     * @return An exact deep copy of this piece.
     */
    @Override
    public abstract Piece clone();

    /**
     * Gets the value of this piece (always positive).
     * @return The value of this piece.
     */
    public abstract int getValue();
    
    /**
     * Gets the text symbol of this piece. This symbol
     * depends on the color of this piece.
     * @return The text symbol of this piece.
     */
    public abstract char getSymbol();
    
    /**
     * Gets the type of this piece. 
     * @see Util.ChessConstants.PAWN
     * @see Util.ChessConstants.KNIGHT
     * @see Util.ChessConstants.BISHOP
     * @see Util.ChessConstants.ROOK
     * @see Util.ChessConstants.QUEEN
     * @see Util.ChessConstants.KING
     * @return The type of this piece.
     */
    public abstract String getType();
    
    /**
     * Gets the full name of this piece which is its color (White or Black)
     * followed by a space and its type.
     * 
     * @see getType()
     * @see isWhite()
     * @see isBlack()
     * @return The full name of this piece.
     */
    public final String getName() {
        return (color) ? WHITE + SPACE + getType() : BLACK + SPACE + getType();
    }

    public abstract Iterator<Tile> getMoveTiles(Grid grid);
    
    public int getNumberOfMoveTiles(Grid grid) {
        int count = 0;
        for (Iterator<Tile> it = getMoveTiles(grid); it.hasNext(); ++count) {
            it.next();
        }
        return count;
    }
    
    public abstract List<Tile> getAttackTiles(Grid grid);
    
    public abstract int getNumberOfAttackTiles(Grid grid);

    public abstract List<Tile> getProtectedTiles(Grid grid);

    public abstract void setProtectedTiles(Grid grid);

    public abstract int getNumberOfProtectedTiles(Grid grid);

    //These methods only apply to Pawn, they are defined here to avoid the need for casting.
    public boolean justMadeDoubleJump() {
        return false;
    }

    public void setJustMadeDoubleJump(boolean doubleJumpJustPerformed) {
        throw new UnsupportedOperationException();
    }

    public Tile getLeftEnPassantTile(Grid grid) {
        throw new UnsupportedOperationException();
    }

    public Tile getRightEnPassantTile(Grid grid) {
        throw new UnsupportedOperationException();
    }

    public List<Tile> getEnPassantTiles(Grid grid) {
        throw new UnsupportedOperationException();
    }

    public boolean isPawn() {
        return false;
    }

    public boolean isKnight() {
        return false;
    }

    public boolean isBishop() {
        return false;
    }

    public boolean isRook() {
        return false;
    }

    public boolean isQueen() {
        return false;
    }

    public boolean isKing() {
        return false;
    }
    
    public abstract int getProtectionValue();
    
    /**
     * Encodes the all the properties of this piece except the number of times it
     * has moved. This method is used by {@see AI} in order to avoid threefold 
     * repetition.
     * @return 
     */
    public String encode() {
        return "(" + color + "," + getType() + "," + row + "," + column + ")";
    }
    
    /**
     * Compares this piece vs the given piece by their exact value.
     *
     * @param other Another piece object.
     * @return
     */
    @Override
    public final int compareTo(Piece other) {
        int materialDifference = getValue() - other.getValue();
        if (materialDifference != 0) {
            return materialDifference;
        }
        return other.getIndex() - getIndex();
    }
    
    public int evaluate(Grid grid) {
        return getValue();
    }
}