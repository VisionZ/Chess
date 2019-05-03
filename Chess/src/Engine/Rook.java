package Engine;

import static Engine.EvaluationConstants.ROOK_PROTECTION;
import static Util.ChessConstants.BLACK_ROOK;
import static Util.ChessConstants.ROOK;
import static Util.ChessConstants.ROOK_VALUE;
import static Util.ChessConstants.WHITE_ROOK;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES;
import Util.IteratorWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Rook extends Piece {

    public Rook(int row, int column, boolean color) {
        super(row, column, color);
    }
    
    public Rook(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? WHITE_ROOK : BLACK_ROOK;
    }
    
    @Override
    public boolean isRook() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Rook clone() {
        return new Rook(getRow(), getColumn(), getMoveCount(), isWhite());
    }

    @Override
    public int getValue() {
        return ROOK_VALUE;
    }

    @Override
    public String getType() {
        return ROOK;
    }
    
    @Override
    public int getProtectionValue() {
        return ROOK_PROTECTION;
    }
    
    @Override
    public Iterator<Tile> getMoveTiles(Grid grid) {
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        Iterator<Tile> up = new Iterator<Tile>() {
            
            int nextRow = currentRow - 1;
            
            @Override
            public boolean hasNext() {
                if (nextRow >= 0) {
                    return !grid.getTile(nextRow, currentColumn).isOccupied();
                }
                return false;
            }

            @Override
            public Tile next() {
                return grid.getTile(nextRow--, currentColumn);
            }
        };
        Iterator<Tile> down = new Iterator<Tile>() {
            
            int nextRow = currentRow + 1;
            
            @Override
            public boolean hasNext() {
                if (nextRow < LENGTH) {
                    return !grid.getTile(nextRow, currentColumn).isOccupied();
                }
                return false;
            }

            @Override
            public Tile next() {
                return grid.getTile(nextRow++, currentColumn);
            }
        };
        Iterator<Tile> left = new Iterator<Tile>() {
            
            int nextColumn = currentColumn - 1;
            
            @Override
            public boolean hasNext() {
                if (nextColumn >= 0) {
                    return !grid.getTile(currentRow, nextColumn).isOccupied();
                }
                return false;
            }

            @Override
            public Tile next() {
                return grid.getTile(currentRow, nextColumn--);
            }
        };
        Iterator<Tile> right = new Iterator<Tile>() {
            
            int nextColumn = currentColumn + 1;
            
            @Override
            public boolean hasNext() {
                if (nextColumn < LENGTH) {
                    return !grid.getTile(currentRow, nextColumn).isOccupied();
                }
                return false;
            }

            @Override
            public Tile next() {
                return grid.getTile(currentRow, nextColumn++);
            }
        };
        
        return new IteratorWrapper<>(up, down, left, right);
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        return list;
    }
    
    @Override
    public int getNumberOfAttackTiles(Grid grid) {
        int count = 0;
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        return count;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>();
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        return list;
    }

    @Override
    public void setProtectedTiles(Grid grid) {
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public int getNumberOfProtectedTiles(Grid grid) {
        int count = 0;
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0) {
                        if (!grid.getTile(nextRow, currentColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH) {
                        if (!grid.getTile(nextRow, currentColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextColumn >= 0) {
                        if (!grid.getTile(currentRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextColumn < LENGTH) {
                        if (!grid.getTile(currentRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        return count;
    }
}