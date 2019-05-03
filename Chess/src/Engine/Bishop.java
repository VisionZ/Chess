package Engine;

import static Engine.EvaluationConstants.BISHOP_MOBILITY;
import static Engine.EvaluationConstants.BISHOP_PROTECTION;
import static Engine.EvaluatorPowerful.BISHOP_POSITION_BLACK;
import static Engine.EvaluatorPowerful.BISHOP_POSITION_WHITE;
import static Util.ChessConstants.BISHOP;
import static Util.ChessConstants.BISHOP_VALUE;
import static Util.ChessConstants.BLACK_BISHOP;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.WHITE_BISHOP;
import static Util.ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES;
import Util.IteratorWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing a bishop in chess.
 * @author Will
 */
public final class Bishop extends Piece {

    public Bishop(int row, int column, boolean color) {
        super(row, column, color);
    }

    public Bishop(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }

    @Override
    public int evaluate(Grid grid) {
        return BISHOP_VALUE 
                + (BISHOP_MOBILITY * getNumberOfProtectedTiles(grid)) 
                + (isWhite() 
                ? BISHOP_POSITION_WHITE[getRow() * LENGTH + getColumn()]
                : BISHOP_POSITION_BLACK[getRow() * LENGTH + getColumn()]);
    }

    @Override
    public char getSymbol() {
        return isWhite() ? WHITE_BISHOP : BLACK_BISHOP;
    }
    
    @Override
    public boolean isBishop() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Bishop clone() {
        return new Bishop(getRow(), getColumn(), getMoveCount(), isWhite());
    }

    @Override
    public int getValue() {
        return BISHOP_VALUE;
    }

    @Override
    public String getType() {
        return BISHOP;
    }
    
    @Override
    public int getProtectionValue() {
        return BISHOP_PROTECTION;
    }

    @Override
    public Iterator<Tile> getMoveTiles(Grid grid) {
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        Iterator<Tile> upLeft = new Iterator<Tile>() {
            
            int nextRow = currentRow - 1;
            int nextColumn = currentColumn - 1;
            
            @Override
            public boolean hasNext() {
                return (nextRow >= 0 && nextColumn >= 0) ? !grid.getTile(nextRow, nextColumn).isOccupied() : false;
            }

            @Override
            public Tile next() {
                return grid.getTile(nextRow--, nextColumn--);
            }
        };
        Iterator<Tile> upRight = new Iterator<Tile>() {
            
            int nextRow = currentRow - 1;
            int nextColumn = currentColumn + 1;
            
            @Override
            public boolean hasNext() {
                return (nextRow >= 0 && nextColumn < LENGTH) ? !grid.getTile(nextRow, nextColumn).isOccupied() : false;
            }

            @Override
            public Tile next() {
                return grid.getTile(nextRow--, nextColumn++);
            }
        };
        Iterator<Tile> downLeft = new Iterator<Tile>() {
            
            int nextRow = currentRow + 1;
            int nextColumn = currentColumn - 1;
            
            @Override
            public boolean hasNext() {
                return (nextRow < LENGTH && nextColumn >= 0) ? !grid.getTile(nextRow, nextColumn).isOccupied() : false;
            }

            @Override
            public Tile next() {
                return grid.getTile(nextRow++, nextColumn--);
            }
        };
        Iterator<Tile> downRight = new Iterator<Tile>() {
            
            int nextRow = currentRow + 1;
            int nextColumn = currentColumn + 1;
            
            @Override
            public boolean hasNext() {
                return (nextRow < LENGTH && nextColumn < LENGTH) ? !grid.getTile(nextRow, nextColumn).isOccupied() : false;
            }

            @Override
            public Tile next() {
                return grid.getTile(nextRow++, nextColumn++);
            }
        };
        return new IteratorWrapper<>(upLeft, upRight, downLeft, downRight);
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    ++count;
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0 && --nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0 && ++nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH && --nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH && ++nextColumn < LENGTH) {
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0 && --nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0 && ++nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH && --nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH && ++nextColumn < LENGTH) {
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

    @Override
    public int getNumberOfProtectedTiles(Grid grid) {
        int count = 0;
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0 && --nextColumn >= 0) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (--nextRow >= 0 && ++nextColumn < LENGTH) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH && --nextColumn >= 0) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            Tile tile = grid.getTile(nextRow, nextColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (++nextRow < LENGTH && ++nextColumn < LENGTH) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
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