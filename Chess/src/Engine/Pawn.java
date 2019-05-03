package Engine;

import static Engine.EvaluationConstants.PAWN_PROTECTION;
import static Util.ChessConstants.BLACK_PAWN;
import static Util.ChessConstants.PAWN;
import static Util.ChessConstants.PAWN_VALUE;
import static Util.ChessConstants.WHITE_PAWN;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.WHITE_ENPASSANT_ROW;
import static Util.ChessConstants.BLACK_ENPASSANT_ROW;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("EqualsAndHashcode")
public final class Pawn extends Piece {
    
    //need to know which pawn has just peformed a double jump on the last move
    private boolean justMadeDoubleJump = false;

    public Pawn(int row, int column, boolean color) {
        super(row, column, color);
    }

    public Pawn(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }

    @Override
    public char getSymbol() {
        return isWhite() ? WHITE_PAWN : BLACK_PAWN;
    }

    @Override
    public boolean isPawn() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Pawn clone() {
        Pawn copy = new Pawn(getRow(), getColumn(), getMoveCount(), isWhite());
        copy.justMadeDoubleJump = justMadeDoubleJump;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (!(obj instanceof Pawn)) ? false : (super.equals(obj) && (justMadeDoubleJump == ((Pawn) obj).justMadeDoubleJump));
    }
    
    //no need to check getType() after we know that the other piece is a pawn
    @Override
    public boolean fastEquals(Piece piece) {
        return super.fastEquals(piece) && (justMadeDoubleJump == ((Pawn) piece).justMadeDoubleJump);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + (justMadeDoubleJump ? 1 : 0);
    }

    @Override
    public int getValue() {
        return PAWN_VALUE;
    }

    @Override
    public String getType() {
        return PAWN;
    }
    
    @Override
    public int getProtectionValue() {
        return PAWN_PROTECTION;
    }

    @Override
    public boolean justMadeDoubleJump() {
        //return justMadeDoubleJump && (getMoveCount() == 1) && (isWhite() ? (getRow() == 4) : (getRow() == 3));
        return justMadeDoubleJump;
    }

    @Override
    public void setJustMadeDoubleJump(boolean doubleJumpJustPerformed) {
        justMadeDoubleJump = doubleJumpJustPerformed;
    }

    @Override
    public Iterator<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(2); //Maximum possible length is 2
        if (isBlack()) {
            final int nextRow = getRow() + 1;
            if (!hasMoved()) {
                final int currentColumn = getColumn();
                Tile nextTileDown = grid.getTile(nextRow, currentColumn);
                if (!nextTileDown.isOccupied()) {
                    list.add(nextTileDown);
                    Tile nextNextTileDown = grid.getTile(nextRow + 1, currentColumn);
                    if (!nextNextTileDown.isOccupied()) {
                        list.add(nextNextTileDown);
                    }
                }
                return list.iterator();
            }
            if (nextRow < LENGTH) {
                Tile nextTileDown = grid.getTile(nextRow, getColumn());
                if (!nextTileDown.isOccupied()) {
                    list.add(nextTileDown);
                }
            }
            return list.iterator();
        }
        final int nextRow = getRow() - 1;
        if (!hasMoved()) {
            final int currentColumn = getColumn();
            Tile nextTileUp = grid.getTile(nextRow, currentColumn);
            if (!nextTileUp.isOccupied()) {
                list.add(nextTileUp);
                Tile nextNextTileUp = grid.getTile(nextRow - 1, currentColumn);
                if (!nextNextTileUp.isOccupied()) {
                    list.add(nextNextTileUp);
                }
            }
            return list.iterator();
        }
        if (nextRow >= 0) {
            Tile nextTileUp = grid.getTile(nextRow, getColumn());
            if (!nextTileUp.isOccupied()) {
                list.add(nextTileUp);
            }
        }
        return list.iterator();
    }
    
    @Override
    public int getNumberOfMoveTiles(Grid grid) {
        if (isBlack()) {
            final int nextRow = getRow() + 1;
            if (!hasMoved()) {
                final int currentColumn = getColumn();
                return !grid.getTile(nextRow, currentColumn).isOccupied()
                        ? (!grid.getTile(nextRow + 1, currentColumn).isOccupied() ? 2 : 1)
                        : 0;
            }
            return (nextRow < LENGTH && !grid.getTile(nextRow, getColumn()).isOccupied()) ? 1 : 0;
        }
        final int nextRow = getRow() - 1;
        if (!hasMoved()) {
            final int currentColumn = getColumn();
            return !grid.getTile(nextRow, currentColumn).isOccupied()
                    ? (!grid.getTile(nextRow - 1, currentColumn).isOccupied() ? 2 : 1)
                    : 0;
        }
        return (nextRow >= 0 && !grid.getTile(nextRow, getColumn()).isOccupied()) ? 1 : 0;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(2); //Maximum possible length is 2
        if (isBlack()) {
            int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    Tile tile = grid.getTile(nextRow, nextColumn);
                    if (tile.isOccupiedByWhite()) {
                        list.add(tile);
                    }
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    Tile tile = grid.getTile(nextRow, nextColumn);
                    if (tile.isOccupiedByWhite()) {
                        list.add(tile);
                    }
                }
            }
            return list;
        }
        int nextRow = getRow() - 1;
        if (nextRow >= 0) {
            final int currentColumn = getColumn();
            int nextColumn = currentColumn - 1;
            if (nextColumn >= 0) {
                Tile tile = grid.getTile(nextRow, nextColumn);
                if (tile.isOccupiedByBlack()) {
                    list.add(tile);
                }
            }
            if ((nextColumn = currentColumn + 1) < LENGTH) {
                Tile tile = grid.getTile(nextRow, nextColumn);
                if (tile.isOccupiedByBlack()) {
                    list.add(tile);
                }
            }
        }
        return list;
    }
    
    @Override
    public int getNumberOfAttackTiles(Grid grid) {
        int count = 0;
        if (isBlack()) {
            int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    if (grid.getTile(nextRow, nextColumn).isOccupiedByWhite()) {
                        ++count;
                    }
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    if (grid.getTile(nextRow, nextColumn).isOccupiedByWhite()) {
                        return ++count;
                    }
                }
            }
            return count;
        }
        int nextRow = getRow() - 1;
        if (nextRow >= 0) {
            final int currentColumn = getColumn();
            int nextColumn = currentColumn - 1;
            if (nextColumn >= 0) {
                if (grid.getTile(nextRow, nextColumn).isOccupiedByBlack()) {
                    ++count;
                }
            }
            if ((nextColumn = currentColumn + 1) < LENGTH) {
                if (grid.getTile(nextRow, nextColumn).isOccupiedByBlack()) {
                    return ++count;
                }
            }
        }
        return count;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(2); //Maximum possible length is 2
        if (isBlack()) {
            final int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
            }
        }
        else {
            final int nextRow = getRow() - 1;
            if (nextRow >= 0) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
            }
        }
        return list;
    }

    @Override
    public void setProtectedTiles(Grid grid) {
        if (isBlack()) {
            final int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
            }
        }
        else {
            final int nextRow = getRow() - 1;
            if (nextRow >= 0) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
            }
        }
    }

    @Override
    public int getNumberOfProtectedTiles(Grid grid) {
        int count = 0;
        if (isBlack()) {
            if ((getRow() + 1) < LENGTH) {
                final int currentColumn = getColumn();
                if ((currentColumn - 1) >= 0) {
                    ++count;
                }
                if ((currentColumn + 1) < LENGTH) {
                    return ++count;
                }
            }
            return count;
        }
        else {
            if ((getRow() - 1) >= 0) {
                final int currentColumn = getColumn();
                if ((currentColumn - 1) >= 0) {
                    ++count;
                }
                if ((currentColumn + 1) < LENGTH) {
                    return ++count;
                }
            }
            return count;
        }
    }

    @Override
    public List<Tile> getEnPassantTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(2); //Maximum possible length is 2
        Tile enPassantTile = getLeftEnPassantTile(grid);
        if (enPassantTile != null) {
            list.add(enPassantTile);
        }
        if ((enPassantTile = getRightEnPassantTile(grid)) != null) {
            list.add(enPassantTile);
        }
        return list;
    }

    @Override
    public Tile getLeftEnPassantTile(Grid grid) {
        final int currentRow = getRow();
        if (isWhite()) {
            if (currentRow != WHITE_ENPASSANT_ROW) {
                return null;
            }
            final int leftColumn = getColumn() - 1;
            if (leftColumn >= 0) {
                Piece blackPawn = grid.getTile(currentRow, leftColumn).getOccupant();
                if (blackPawn != null) {
                    if (blackPawn.justMadeDoubleJump() && blackPawn.isBlack()) {
                        Tile leftEnPassantTile = grid.getTile(currentRow - 1, leftColumn);
                        if (!leftEnPassantTile.isOccupied()) {
                            return leftEnPassantTile;
                        }
                    }
                }
            }
        }
        else {
            if (currentRow != BLACK_ENPASSANT_ROW) {
                return null;
            }
            final int leftColumn = getColumn() - 1;
            if (leftColumn >= 0) {
                Piece whitePawn = grid.getTile(currentRow, leftColumn).getOccupant();
                if (whitePawn != null) {
                    if (whitePawn.justMadeDoubleJump() && whitePawn.isWhite()) {
                        Tile leftEnPassantTile = grid.getTile(currentRow + 1, leftColumn);
                        if (!leftEnPassantTile.isOccupied()) {
                            return leftEnPassantTile;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Tile getRightEnPassantTile(Grid grid) {
        final int currentRow = getRow();
        if (isWhite()) {
            if (currentRow != WHITE_ENPASSANT_ROW) {
                return null;
            }
            final int rightColumn = getColumn() + 1;
            if (rightColumn < LENGTH) {
                Piece blackPawn = grid.getTile(currentRow, rightColumn).getOccupant();
                if (blackPawn != null) {
                    if (blackPawn.justMadeDoubleJump() && blackPawn.isBlack()) {
                        Tile rightEnPassantTile = grid.getTile(currentRow - 1, rightColumn);
                        if (!rightEnPassantTile.isOccupied()) {
                            return rightEnPassantTile;
                        }
                    }
                }
            }
        }
        else {
            if (currentRow != BLACK_ENPASSANT_ROW) {
                return null;
            }
            final int rightColumn = getColumn() + 1;
            if (rightColumn < LENGTH) {
                Piece whitePawn = grid.getTile(currentRow, rightColumn).getOccupant();
                if (whitePawn != null) {
                    if (whitePawn.justMadeDoubleJump() && whitePawn.isWhite()) {
                        Tile rightEnPassantTile = grid.getTile(currentRow + 1, rightColumn);
                        if (!rightEnPassantTile.isOccupied()) {
                            return rightEnPassantTile;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean canEnPassant(Grid grid) {
        return getLeftEnPassantTile(grid) != null || getRightEnPassantTile(grid) != null;
    }

    public static Piece[] getPromoted(Piece pawn) {
        final int row = pawn.getRow();
        final int column = pawn.getColumn();
        final int moveCount = pawn.getMoveCount();
        final boolean color = pawn.isWhite();
        return new Piece[]{
            new Queen(row, column, moveCount, color), 
            new Rook(row, column, moveCount, color), 
            new Bishop(row, column, moveCount, color), 
            new Knight(row, column, moveCount, color)
        };
    }

    @Override
    public String encode() {
        return "(" + isWhite() + "," + getType() + "," + getRow() + "," + getColumn() + "," + justMadeDoubleJump + ")";
    }
}