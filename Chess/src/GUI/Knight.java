package GUI;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

//Status: Done
public final class Knight extends Piece {
    
    public Knight(int x, int y, int width, int height, int row, int column, boolean color) {
        super(x, y, width, height, row, column, color, ChessConstants.KNIGHT);
    }
    
    public Knight(int row, int column, boolean color) {
        super(row, column, color, ChessConstants.KNIGHT);
    }
    
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Knight clone() {
        Knight copy = new Knight(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
        copy.setSelected(isSelected());
        copy.setMoveCount(getMoveCount());
        return copy;
    }

    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_KNIGHT : ChessConstants.BLACK_KNIGHT;
    }

    @Override
    public boolean isKnight() {
        return true;
    }

    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_KNIGHT_PROTECTED_TILES);

        final int currentRow = getRow();
        final int currentColumn = getColumn();

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                Tile tile = grid.getTile(up, left2);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                Tile tile = grid.getTile(up, right2);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                Tile tile = grid.getTile(down, left2);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                Tile tile = grid.getTile(down, right2);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                Tile tile = grid.getTile(up2, left);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                Tile tile = grid.getTile(down2, left);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                Tile tile = grid.getTile(up2, right);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                Tile tile = grid.getTile(down2, right);
                if (!tile.isOccupied()) {
                    list.add(tile);
                }
            }
        }
        return list;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_KNIGHT_PROTECTED_TILES);

        final int currentRow = getRow();
        final int currentColumn = getColumn();

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                Tile tile = grid.getTile(up, left2);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                Tile tile = grid.getTile(up, right2);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                Tile tile = grid.getTile(down, left2);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                Tile tile = grid.getTile(down, right2);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                Tile tile = grid.getTile(up2, left);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                Tile tile = grid.getTile(down2, left);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                Tile tile = grid.getTile(up2, right);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                Tile tile = grid.getTile(down2, right);
                if (tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
        }
        return list;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_KNIGHT_PROTECTED_TILES);

        final int currentRow = getRow();
        final int currentColumn = getColumn();

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                list.add(grid.getTile(up, left2));
            }
            if (right2 < LENGTH) {
                list.add(grid.getTile(up, right2));
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                list.add(grid.getTile(down, left2));
            }
            if (right2 < LENGTH) {
                list.add(grid.getTile(down, right2));
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                list.add(grid.getTile(up2, left));
            }
            if (down2 < LENGTH) {
                list.add(grid.getTile(down2, left));
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                list.add(grid.getTile(up2, right));
            }
            if (down2 < LENGTH) {
                list.add(grid.getTile(down2, right));
            }
        }
        return list;
    }

    @Override
    public void setProtectedTiles(Grid grid) {
        final int currentRow = getRow();
        final int currentColumn = getColumn();

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                grid.getTile(up, left2).setProtectedBy(this);
            }
            if (right2 < LENGTH) {
                grid.getTile(up, right2).setProtectedBy(this);
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                grid.getTile(down, left2).setProtectedBy(this);
            }
            if (right2 < LENGTH) {
                grid.getTile(down, right2).setProtectedBy(this);
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                grid.getTile(up2, left).setProtectedBy(this);
            }
            if (down2 < LENGTH) {
                grid.getTile(down2, left).setProtectedBy(this);
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                grid.getTile(up2, right).setProtectedBy(this);
            }
            if (down2 < LENGTH) {
                grid.getTile(down2, right).setProtectedBy(this);
            }
        }
    }
}