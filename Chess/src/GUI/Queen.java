package GUI;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

public final class Queen extends Piece {

    public Queen(int x, int y, int width, int height, int row, int column, boolean color) {
        super(x, y, width, height, row, column, color, ChessConstants.QUEEN);
    }
    
    public Queen(int row, int column, boolean color) {
        super(row, column, color, ChessConstants.QUEEN);
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Queen clone() {
        Queen copy = new Queen(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
        copy.setSelected(isSelected());
        copy.setMoveCount(getMoveCount());
        return copy;
    }

    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_QUEEN : ChessConstants.BLACK_QUEEN;
    }
    
    @Override
    public boolean isQueen() {
        return true;
    }
    
    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>();
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
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
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_QUEEN_ATTACK_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
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
        for (int nextRow = currentRow - 1; nextRow >= 0;) {
            Tile tile = grid.getTile(nextRow--, currentColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, currentColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0;) {
            Tile tile = grid.getTile(currentRow, nextColumn--);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH;) {
            Tile tile = grid.getTile(currentRow, nextColumn++);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
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
        for (int nextRow = currentRow - 1; nextRow >= 0;) {
            Tile tile = grid.getTile(nextRow--, currentColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, currentColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0;) {
            Tile tile = grid.getTile(currentRow, nextColumn--);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH;) {
            Tile tile = grid.getTile(currentRow, nextColumn++);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
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