package GUI;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

//Status: Done
public final class Bishop extends Piece {

    public Bishop(int x, int y, int width, int height, int row, int column, boolean color) {
        super(x, y, width, height, row, column, color, ChessConstants.BISHOP);
    }
    
    public Bishop(int row, int column, boolean color) {
        super(row, column, color, ChessConstants.BISHOP);
    }
    
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Bishop clone() {
        Bishop copy = new Bishop(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
        copy.setSelected(isSelected());
        copy.setMoveCount(getMoveCount());
        return copy;
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_BISHOP : ChessConstants.BLACK_BISHOP;
    }
     
    @Override
    public boolean isBishop() {
        return true;
    }

    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>();
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        return list;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
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
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>();
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
    }
}