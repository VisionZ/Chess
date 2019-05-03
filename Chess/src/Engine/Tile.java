package Engine;

import Util.Constants;
import Util.EmptyList;

@SuppressWarnings("EqualsAndHashcode")
public final class Tile implements Locatable {
    
    static final EmptyList<Tile> EMPTY_LIST = new EmptyList<>();
    
    public final int row;
    public final int column;
 
    private boolean protectedByWhite;
    private boolean protectedByBlack;
    
    private Piece occupant;
    
    public Tile(int r, int c) {
        row = r;
        column = c;
    }
    
    public Tile(Tile tile) {
        row = tile.row;
        column = tile.column;
        protectedByWhite = tile.protectedByWhite;
        protectedByBlack = tile.protectedByBlack;
        Piece originalOccupant = tile.occupant;
        if (originalOccupant != null) {
            occupant = originalOccupant.clone();
        }
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
    @Deprecated
    public final void setRow(int row) {
        throw new UnsupportedOperationException("Cannot change row in Tile.");
    }

    @Override
    @Deprecated
    public final void setColumn(int column) {
        throw new UnsupportedOperationException("Cannot change column in Tile.");
    }

    @Override
    @Deprecated
    public final void setLocation(int row, int column) {
        throw new UnsupportedOperationException("Cannot change location in Tile.");
    }
    
    public boolean protectedByWhite() {
        return protectedByWhite;
    }

    public boolean protectedByBlack() {
        return protectedByBlack;
    }
    
    public boolean protectedByEnemy(Piece piece) {
        return piece.isWhite() ? protectedByBlack : protectedByWhite;
    }
    
    public boolean protectedByAlly(Piece piece) {
        return piece.isWhite() ? protectedByWhite : protectedByBlack;
    }
    
    public void setProtectedBy(Piece piece) { 
        if (piece.isWhite()) {
            protectedByWhite = true;
        }
        else {
            protectedByBlack = true;
        }
    }
    
    public void removeProtections() {
        protectedByWhite = protectedByBlack = false;
    }
    
    public void setProtections(boolean white, boolean black) {
        protectedByWhite = white;
        protectedByBlack = black;
    }

    public boolean isOccupied() {
        return occupant != null;
    }
    
    public boolean isOccupiedByWhite() {
        return occupant != null && occupant.isWhite();
    }
    
    public boolean isOccupiedByBlack() {
        return occupant != null && occupant.isBlack();
    }
    
    public Piece getOccupant() {
        return occupant;
    }
    
    public Piece popOccupant() {
        Piece piece = occupant;
        occupant = null;
        return piece;
    }
    
    public void setOccupant(Piece nextOccupant) {
        occupant = null;
        (occupant = nextOccupant).setLocation(row, column);
    }

    public void removeOccupant() {
        occupant = null;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        Tile other = (Tile) obj;
        return (row == other.row) 
                && (column == other.column)
                && (protectedByWhite == other.protectedByWhite)
                && (protectedByBlack == other.protectedByBlack)
                && Constants.equals(occupant, other.occupant);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 5 * hash + (protectedByWhite ? 1 : 0);
        hash = 5 * hash + (protectedByBlack ? 1 : 0);
        hash = 5 * hash + (occupant != null ? occupant.hashCode() : 0);
        return hash;
    }
    
    @Override
    @Deprecated
    public String toString() {
        String result = "";
        if (occupant != null) {
            result += "Occupied by: " + occupant.getName() + "\n";
        }
        result += "Location: [" + row + ", " + column + "]\n";
        result += protectedByWhite ? "Protected By White\n" : "Not Protected By White\n";
        result += protectedByBlack ? "Protected By Black" : "Not Protected By Black";
        return result;
    }
    
    /**
     * If this tile is occupied, returns the protection value of the piece on this tile. Otherwise
     * this method returns {@code 1}.
     * @return The value of protecting this tile.
     */
    public int getProtectionFactor() {
        return occupant != null ? occupant.getProtectionValue() : 1;
    }
}