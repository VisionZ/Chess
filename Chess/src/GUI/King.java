package GUI;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

//Status: Done
@SuppressWarnings("EqualsAndHashcode")
public final class King extends Piece {
   
    public King(int x, int y, int width, int height, int row, int column, boolean color) {
        super(x, y, width, height, row, column, color, ChessConstants.KING);
    }
    
    public King(int row, int column, boolean color) {
        super(row, column, color, ChessConstants.KING);
    }
    
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public King clone() {
        King copy = new King(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
        copy.setSelected(isSelected());
        copy.setMoveCount(getMoveCount());
        return copy;
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_KING : ChessConstants.BLACK_KING;
    }
    
    @Override
    public boolean isKing() {
        return true;
    }

    //could change this method to inTrouble and then
    //refractor and make it general purpose for all pieces.
    public boolean inCheck(Grid grid) {
        return grid.getTile(getRow(), getColumn()).protectedByEnemy(this);
    }

    public List<Tile> getCastleTiles(Grid grid) {
        if (hasMoved() || inCheck(grid)) {
            return Tile.EMPTY_LIST;
        }
        final int currentRow = getRow();
        if (isWhite()) {
            if (currentRow != ChessConstants.WHITE_PIECE_ROW || getColumn() != ChessConstants.KING_START_COLUMN) {
                return Tile.EMPTY_LIST;
            }
        }
        else if (currentRow != ChessConstants.BLACK_PIECE_ROW || getColumn() != ChessConstants.KING_START_COLUMN) {
            return Tile.EMPTY_LIST;
        }
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_CASTLE_TILES);
        Piece leftRook = grid.getTile(currentRow, 0).getOccupant();
        if (leftRook != null && !leftRook.hasMoved() && leftRook.isRook() && isAlly(leftRook)) {
            boolean blocked = false;
            for (int column = 1; column < 4; column++) {
                Tile path = grid.getTile(currentRow, column);
                if (path.protectedByEnemy(this) || path.isOccupied()) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                list.add(grid.getTile(currentRow, ChessConstants.LEFT_KING_CASTLE_COLUMN));
            }
        }
        Piece rightRook = grid.getTile(currentRow, 7).getOccupant();
        if (rightRook != null && !rightRook.hasMoved() && rightRook.isRook() && isAlly(rightRook)) {
            boolean blocked = false;
            for (int column = 5; column < 7; column++) {
                Tile path = grid.getTile(currentRow, column);
                if (path.protectedByEnemy(this) || path.isOccupied()) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                list.add(grid.getTile(currentRow, ChessConstants.RIGHT_KING_CASTLE_COLUMN));
            }
        }
        return list;
    }

    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_KING_PROTECTED_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;
        if (up >= 0) {
            if (left >= 0) {
                Tile tile = grid.getTile(up, left);
                if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                    list.add(tile);
                }
            }
            Tile tile = grid.getTile(up, currentColumn);
            if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = grid.getTile(up, right);
                if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            Tile tile = grid.getTile(currentRow, left);
            if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                list.add(tile);
            }
        }
        if (right < LENGTH) {
            Tile tile = grid.getTile(currentRow, right);
            if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                list.add(tile);
            }
        }
        if (down < LENGTH) {
            if (left >= 0) {
                Tile tile = grid.getTile(down, left);
                if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                    list.add(tile);
                }
            }
            Tile tile = grid.getTile(down, currentColumn);
            if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = grid.getTile(down, right);
                if (!tile.protectedByEnemy(this) && !tile.isOccupied()) {
                    list.add(tile);
                }
            }
        }
        return list;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_KING_PROTECTED_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;
        if (up >= 0) {
            if (left >= 0) {
                Tile tile = grid.getTile(up, left);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
            Tile tile = grid.getTile(up, currentColumn);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = grid.getTile(up, right);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            Tile tile = grid.getTile(currentRow, left);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                list.add(tile);
            }
        }
        if (right < LENGTH) {
            Tile tile = grid.getTile(currentRow, right);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                list.add(tile);
            }
        }
        if (down < LENGTH) {
            if (left >= 0) {
                Tile tile = grid.getTile(down, left);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
            Tile tile = grid.getTile(down, currentColumn);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = grid.getTile(down, right);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
            }
        }
        return list;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_KING_PROTECTED_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;
        if (up >= 0) {
            if (left >= 0) {
                list.add(grid.getTile(up, left));
            }
            list.add(grid.getTile(up, currentColumn));
            if (right < LENGTH) {
                list.add(grid.getTile(up, right));
            }
        }
        if (left >= 0) {
            list.add(grid.getTile(currentRow, left));
        }
        if (right < LENGTH) {
            list.add(grid.getTile(currentRow, right));
        }
        if (down < LENGTH) {
            if (left >= 0) {
                list.add(grid.getTile(down, left));
            }
            list.add(grid.getTile(down, currentColumn));
            if (right < LENGTH) {
                list.add(grid.getTile(down, right));
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
        if (up >= 0) {
            if (left >= 0) {
                grid.getTile(up, left).setProtectedBy(this);
            }
            grid.getTile(up, currentColumn).setProtectedBy(this);
            if (right < LENGTH) {
                grid.getTile(up, right).setProtectedBy(this);
            }
        }
        if (left >= 0) {
            grid.getTile(currentRow, left).setProtectedBy(this);
        }
        if (right < LENGTH) {
            grid.getTile(currentRow, right).setProtectedBy(this);
        }
        if (down < LENGTH) {
            if (left >= 0) {
                grid.getTile(down, left).setProtectedBy(this);
            }
            grid.getTile(down, currentColumn).setProtectedBy(this);
            if (right < LENGTH) {
                grid.getTile(down, right).setProtectedBy(this);
            }
        }
    }
    
    @Override
    public List<Tile> getLegalMoves(Grid grid) {
        List<Tile> legalMoves = super.getLegalMoves(grid);
        List<Tile> castleTiles = getCastleTiles(grid);
        for (int index = 0, size = castleTiles.size(); index != size; ++index) {
            Tile castleTile = castleTiles.get(index);
            castleTile.setTint(Board.CASTLE);
            legalMoves.add(castleTile);
        }
        return legalMoves;
    }
}