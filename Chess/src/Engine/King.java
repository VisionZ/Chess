package Engine;

import static Engine.EvaluationConstants.KING_PROTECTION;
import static Util.ChessConstants.BLACK_KING;
import static Util.ChessConstants.KING;
import static Util.ChessConstants.KING_VALUE;
import static Util.ChessConstants.WHITE_KING;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.WHITE_PIECE_ROW;
import static Util.ChessConstants.BLACK_PIECE_ROW;
import static Util.ChessConstants.LEFT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.LEFT_ROOK_START_COLUMN;
import static Util.ChessConstants.RIGHT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.NUMBER_OF_CASTLE_TILES;
import static Util.ChessConstants.NUMBER_OF_KING_PROTECTED_TILES;
import static Util.ChessConstants.RIGHT_ROOK_START_COLUMN;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a king in chess.
 * @author Will
 */
public final class King extends Piece {
    
    public King(int row, int column, boolean color) {
        super(row, column, color);
    }
    
    public King(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? WHITE_KING : BLACK_KING;
    }
    
    @Override
    public boolean isKing() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public King clone() {
        return new King(getRow(), getColumn(), getMoveCount(), isWhite());
    }

    @Override
    public int getValue() {
        return KING_VALUE;
    }

    @Override
    public String getType() {
        return KING;
    }
    
    @Override
    public int getProtectionValue() {
        return KING_PROTECTION;
    }
    
    //could change this method to inTrouble and then
    //refractor and make it general purpose for all pieces.
    public boolean inCheck(Grid grid) {
        return grid.getTile(getRow(), getColumn()).protectedByEnemy(this);
    }

    public boolean canCastle(Grid grid) {
        //configured to call the least number of methods as possible
        if (hasMoved()) {
            return false;
        }
        final int currentRow = getRow();
        return grid.getTile(currentRow, getColumn()).protectedByEnemy(this) ? false
                : isWhite() ? currentRow == WHITE_PIECE_ROW : currentRow == BLACK_PIECE_ROW;
    }

    //assumes that canCastle(grid) is true
    public Tile getLeftCastleTile(Grid grid) {
        final int currentRow = getRow();
        Piece leftRook = grid.getTile(currentRow, LEFT_ROOK_START_COLUMN).getOccupant();
        if (leftRook != null && !leftRook.hasMoved() && leftRook.isRook() && isAlly(leftRook)) {
            for (int column = 1; column < 4; ++column) {
                Tile path = grid.getTile(currentRow, column);
                if (path.protectedByEnemy(this) || path.isOccupied()) {
                    return null;
                }
            }
            return grid.getTile(currentRow, LEFT_KING_CASTLE_COLUMN);
        }
        return null;
    }
    
    //assumes that canCastle(grid) is true
    public Tile getRightCastleTile(Grid grid) {
        final int currentRow = getRow();
        Piece rightRook = grid.getTile(currentRow, RIGHT_ROOK_START_COLUMN).getOccupant();
        if (rightRook != null && !rightRook.hasMoved() && rightRook.isRook() && isAlly(rightRook)) {
            for (int column = 5; column < 7; ++column) {
                Tile path = grid.getTile(currentRow, column);
                if (path.protectedByEnemy(this) || path.isOccupied()) {
                    return null;
                }
            }
            return grid.getTile(currentRow, RIGHT_KING_CASTLE_COLUMN);
        }
        return null;
    }

    public List<Tile> getCastleTiles(Grid grid) {
        if (!canCastle(grid)) {
            return Tile.EMPTY_LIST;
        }
        final List<Tile> list = new ArrayList<>(NUMBER_OF_CASTLE_TILES);
        /**
         * Add the Right Castle Tile THEN the Left Castle Tile, because
         * we read the list of castle tiles in reverse order. So we
         * read Left then Right.
         *
         * This complies with the structure of AlphaBetaBlack where it
         * explicitly checks the Left Castle Tile then the Right Castle Tile,
         * respectively. Likewise, we read Left then Right.
         * 
         * This correction here makes sure that the perft counters
         * match between all AlphaBeta search algorithms, having the same
         * cutoffs
         */
        Tile castleTile = getRightCastleTile(grid);
        if (castleTile != null) {
            list.add(castleTile);
        }
        if ((castleTile = getLeftCastleTile(grid)) != null) {
            list.add(castleTile);
        }
        return list;
    }

    @Override
    public Iterator<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new LinkedList<>();
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
        return list.iterator();
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(NUMBER_OF_KING_PROTECTED_TILES);
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
    public int getNumberOfAttackTiles(Grid grid) {
        int count = 0;
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
                    ++count;
                }
            }
            Tile tile = grid.getTile(up, currentColumn);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                ++count;
            }
            if (right < LENGTH) {
                tile = grid.getTile(up, right);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    ++count;
                }
            }
        }
        if (left >= 0) {
            Tile tile = grid.getTile(currentRow, left);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                ++count;
            }
        }
        if (right < LENGTH) {
            Tile tile = grid.getTile(currentRow, right);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                ++count;
            }
        }
        if (down < LENGTH) {
            if (left >= 0) {
                Tile tile = grid.getTile(down, left);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    ++count;
                }
            }
            Tile tile = grid.getTile(down, currentColumn);
            if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                ++count;
            }
            if (right < LENGTH) {
                tile = grid.getTile(down, right);
                if (!tile.protectedByEnemy(this) && tile.isOccupied() && !isAlly(tile.getOccupant())) {
                    ++count;
                }
            }
        }
        return count;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(NUMBER_OF_KING_PROTECTED_TILES);
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
    public int getNumberOfProtectedTiles(Grid grid) {
        int count = 0;
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;
        if (up >= 0) {
            if (left >= 0) {
                ++count;
            }
            ++count;
            if (right < LENGTH) {
                ++count;
            }
        }
        if (left >= 0) {
            ++count;
        }
        if (right < LENGTH) {
            ++count;
        }
        if (down < LENGTH) {
            if (left >= 0) {
                ++count;
            }
            ++count;
            if (right < LENGTH) {
                ++count;
            }
        }
        return count;
    }
}