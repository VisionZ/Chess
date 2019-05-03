package FastAI;

import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES;
import static Util.ChessConstants.NUMBER_OF_ROOK_MOVE_TILES;
import static Util.ChessConstants.getColumn;
import static Util.ChessConstants.getLocation;
import static Util.ChessConstants.getRow;
import java.util.ArrayList;
import java.util.List;

public final class Rook {

    private Rook() {

    }

    public static final List<Integer> getMoveTiles(final Board board, final int index, final boolean color) {
        final List<Integer> list = new ArrayList<>(NUMBER_OF_ROOK_MOVE_TILES);
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            int tile = getLocation(nextRow, currentColumn);
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            int tile = getLocation(nextRow, currentColumn);
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            int tile = getLocation(currentRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            int tile = getLocation(currentRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        return list;
    }

    public static final List<Integer> getAttackTiles(final Board board, final int index, final boolean color) {
        final BitBoard enemyPieces = color ? board.blackPieces : board.whitePieces;
        final List<Integer> list = new ArrayList<>(NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        for (int nextRow = currentRow - 1; nextRow >= 0; --nextRow) {
            int tile = getLocation(nextRow, currentColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; ++nextRow) {
            int tile = getLocation(nextRow, currentColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; --nextColumn) {
            int tile = getLocation(currentRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; ++nextColumn) {
            int tile = getLocation(currentRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        return list;
    }

    public static final void setProtectedTiles(final Board board, final int index, final boolean color) {

    }
}
