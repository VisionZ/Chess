package Engine;

import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.LINEAR_LENGTH;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing the playing board in chess.
 * @author Will
 */
@SuppressWarnings("EqualsAndHashcode")
public final class Grid {

    private final Tile[] tiles = new Tile[LINEAR_LENGTH];

    public Grid() {
        for (int index = 0, row = 0, column = 0; index != LINEAR_LENGTH; ++index) {
            if (column == LENGTH) {
                ++row;
                column = 0;
            }
            tiles[index] = new Tile(row, column++);
        }
    }

    public Grid(Grid grid) {
        tiles[0] = new Tile(grid.tiles[0]);
        tiles[1] = new Tile(grid.tiles[1]);
        tiles[2] = new Tile(grid.tiles[2]);
        tiles[3] = new Tile(grid.tiles[3]);
        tiles[4] = new Tile(grid.tiles[4]);
        tiles[5] = new Tile(grid.tiles[5]);
        tiles[6] = new Tile(grid.tiles[6]);
        tiles[7] = new Tile(grid.tiles[7]);
        tiles[8] = new Tile(grid.tiles[8]);
        tiles[9] = new Tile(grid.tiles[9]);
        tiles[10] = new Tile(grid.tiles[10]);
        tiles[11] = new Tile(grid.tiles[11]);
        tiles[12] = new Tile(grid.tiles[12]);
        tiles[13] = new Tile(grid.tiles[13]);
        tiles[14] = new Tile(grid.tiles[14]);
        tiles[15] = new Tile(grid.tiles[15]);
        tiles[16] = new Tile(grid.tiles[16]);
        tiles[17] = new Tile(grid.tiles[17]);
        tiles[18] = new Tile(grid.tiles[18]);
        tiles[19] = new Tile(grid.tiles[19]);
        tiles[20] = new Tile(grid.tiles[20]);
        tiles[21] = new Tile(grid.tiles[21]);
        tiles[22] = new Tile(grid.tiles[22]);
        tiles[23] = new Tile(grid.tiles[23]);
        tiles[24] = new Tile(grid.tiles[24]);
        tiles[25] = new Tile(grid.tiles[25]);
        tiles[26] = new Tile(grid.tiles[26]);
        tiles[27] = new Tile(grid.tiles[27]);
        tiles[28] = new Tile(grid.tiles[28]);
        tiles[29] = new Tile(grid.tiles[29]);
        tiles[30] = new Tile(grid.tiles[30]);
        tiles[31] = new Tile(grid.tiles[31]);
        tiles[32] = new Tile(grid.tiles[32]);
        tiles[33] = new Tile(grid.tiles[33]);
        tiles[34] = new Tile(grid.tiles[34]);
        tiles[35] = new Tile(grid.tiles[35]);
        tiles[36] = new Tile(grid.tiles[36]);
        tiles[37] = new Tile(grid.tiles[37]);
        tiles[38] = new Tile(grid.tiles[38]);
        tiles[39] = new Tile(grid.tiles[39]);
        tiles[40] = new Tile(grid.tiles[40]);
        tiles[41] = new Tile(grid.tiles[41]);
        tiles[42] = new Tile(grid.tiles[42]);
        tiles[43] = new Tile(grid.tiles[43]);
        tiles[44] = new Tile(grid.tiles[44]);
        tiles[45] = new Tile(grid.tiles[45]);
        tiles[46] = new Tile(grid.tiles[46]);
        tiles[47] = new Tile(grid.tiles[47]);
        tiles[48] = new Tile(grid.tiles[48]);
        tiles[49] = new Tile(grid.tiles[49]);
        tiles[50] = new Tile(grid.tiles[50]);
        tiles[51] = new Tile(grid.tiles[51]);
        tiles[52] = new Tile(grid.tiles[52]);
        tiles[53] = new Tile(grid.tiles[53]);
        tiles[54] = new Tile(grid.tiles[54]);
        tiles[55] = new Tile(grid.tiles[55]);
        tiles[56] = new Tile(grid.tiles[56]);
        tiles[57] = new Tile(grid.tiles[57]);
        tiles[58] = new Tile(grid.tiles[58]);
        tiles[59] = new Tile(grid.tiles[59]);
        tiles[60] = new Tile(grid.tiles[60]);
        tiles[61] = new Tile(grid.tiles[61]);
        tiles[62] = new Tile(grid.tiles[62]);
        tiles[63] = new Tile(grid.tiles[63]);
    }
    
    public void fill(List<Piece> pieces) {
        for (int index = 0, numberOfPieces = pieces.size(); index != numberOfPieces; ++index) {
            Piece piece = pieces.get(index);
            tiles[LENGTH * piece.getRow() + piece.getColumn()].setOccupant(piece);
        }
    }

    public Tile getTile(int row, int column) {
        //return tiles[LENGTH * row + column];
        //return tiles[TABLE[row][column]];
        return tiles[LENGTH * row + column];
        /*
        row <<= 3;
        while (column != 0) {
            int carry = (row & column) << 1;
            row ^= column;
            column = carry;
        }
        return tiles[row];
         */
    }

    public Tile getTile(int index) {
        return tiles[index];
    }
    
    /*
    public void storeProtections() {
        for (int index = 0; index < ChessConstants.LINEAR_LENGTH; ++index) {
            tiles[index].storeProtections();
        }
    }
    
    public void restoreProtections() {
        for (int index = 0; index < ChessConstants.LINEAR_LENGTH; ++index) {
            tiles[index].restoreProtections();
        }
    }
    
    public void setProtections(boolean[] whiteProtections, boolean[] blackProtections) {
        for (int index = 0; index < ChessConstants.LINEAR_LENGTH; ++index) {
            tiles[index].setProtectedByWhite(whiteProtections[index]);
            tiles[index].setProtectedByBlack(blackProtections[index]);
        }
    }
    
    public boolean[] getWhiteProtections() {
        boolean[] whiteProtections = new boolean[ChessConstants.LINEAR_LENGTH];
        for (int index = 0; index < ChessConstants.LINEAR_LENGTH; ++index) {
            whiteProtections[index] = tiles[index].protectedByWhite();
        }
        return whiteProtections;
    }
    
    public boolean[] getBlackProtections() {
        boolean[] blackProtections = new boolean[ChessConstants.LINEAR_LENGTH];
        for (int index = 0; index < ChessConstants.LINEAR_LENGTH; ++index) {
            blackProtections[index] = tiles[index].protectedByBlack();
        }
        return blackProtections;
    }
    */

    /*
    private int add(int a, int b) {
        while (b != 0) {
            int carry = (a & b);
            a ^= b;
            b = carry << 1;
        }
        return a;
    }
     */
    
    //keep it like this because not all nodes will 
    //be evaluated, espically at depth 0, where
    //we just return the evaluation function's answer.
    //we dont need to get the pieces to look deeper.
    public List<Piece> getPieces() {
        List<Piece> pieces = new ArrayList<>(AI.NUMBER_OF_PIECES);
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            Piece piece = tiles[index].getOccupant();
            if (piece != null) {
                pieces.add(piece);
            }
        }
        return pieces;
    }
    
    public void setProtections(List<Piece> whites, List<Piece> blacks) {
        tiles[0].removeProtections();
        tiles[1].removeProtections();
        tiles[2].removeProtections();
        tiles[3].removeProtections();
        tiles[4].removeProtections();
        tiles[5].removeProtections();
        tiles[6].removeProtections();
        tiles[7].removeProtections();
        tiles[8].removeProtections();
        tiles[9].removeProtections();
        tiles[10].removeProtections();
        tiles[11].removeProtections();
        tiles[12].removeProtections();
        tiles[13].removeProtections();
        tiles[14].removeProtections();
        tiles[15].removeProtections();
        tiles[16].removeProtections();
        tiles[17].removeProtections();
        tiles[18].removeProtections();
        tiles[19].removeProtections();
        tiles[20].removeProtections();
        tiles[21].removeProtections();
        tiles[22].removeProtections();
        tiles[23].removeProtections();
        tiles[24].removeProtections();
        tiles[25].removeProtections();
        tiles[26].removeProtections();
        tiles[27].removeProtections();
        tiles[28].removeProtections();
        tiles[29].removeProtections();
        tiles[30].removeProtections();
        tiles[31].removeProtections();
        tiles[32].removeProtections();
        tiles[33].removeProtections();
        tiles[34].removeProtections();
        tiles[35].removeProtections();
        tiles[36].removeProtections();
        tiles[37].removeProtections();
        tiles[38].removeProtections();
        tiles[39].removeProtections();
        tiles[40].removeProtections();
        tiles[41].removeProtections();
        tiles[42].removeProtections();
        tiles[43].removeProtections();
        tiles[44].removeProtections();
        tiles[45].removeProtections();
        tiles[46].removeProtections();
        tiles[47].removeProtections();
        tiles[48].removeProtections();
        tiles[49].removeProtections();
        tiles[50].removeProtections();
        tiles[51].removeProtections();
        tiles[52].removeProtections();
        tiles[53].removeProtections();
        tiles[54].removeProtections();
        tiles[55].removeProtections();
        tiles[56].removeProtections();
        tiles[57].removeProtections();
        tiles[58].removeProtections();
        tiles[59].removeProtections();
        tiles[60].removeProtections();
        tiles[61].removeProtections();
        tiles[62].removeProtections();
        tiles[63].removeProtections();
        for (int index = (whites.size() - 1); index >= 0; --index) {
            whites.get(index).setProtectedTiles(this);
        }
        for (int index = (blacks.size() - 1); index >= 0; --index) {
            blacks.get(index).setProtectedTiles(this);
        }
    }

    public void setProtections(List<Piece> pieces) {
        tiles[0].removeProtections();
        tiles[1].removeProtections();
        tiles[2].removeProtections();
        tiles[3].removeProtections();
        tiles[4].removeProtections();
        tiles[5].removeProtections();
        tiles[6].removeProtections();
        tiles[7].removeProtections();
        tiles[8].removeProtections();
        tiles[9].removeProtections();
        tiles[10].removeProtections();
        tiles[11].removeProtections();
        tiles[12].removeProtections();
        tiles[13].removeProtections();
        tiles[14].removeProtections();
        tiles[15].removeProtections();
        tiles[16].removeProtections();
        tiles[17].removeProtections();
        tiles[18].removeProtections();
        tiles[19].removeProtections();
        tiles[20].removeProtections();
        tiles[21].removeProtections();
        tiles[22].removeProtections();
        tiles[23].removeProtections();
        tiles[24].removeProtections();
        tiles[25].removeProtections();
        tiles[26].removeProtections();
        tiles[27].removeProtections();
        tiles[28].removeProtections();
        tiles[29].removeProtections();
        tiles[30].removeProtections();
        tiles[31].removeProtections();
        tiles[32].removeProtections();
        tiles[33].removeProtections();
        tiles[34].removeProtections();
        tiles[35].removeProtections();
        tiles[36].removeProtections();
        tiles[37].removeProtections();
        tiles[38].removeProtections();
        tiles[39].removeProtections();
        tiles[40].removeProtections();
        tiles[41].removeProtections();
        tiles[42].removeProtections();
        tiles[43].removeProtections();
        tiles[44].removeProtections();
        tiles[45].removeProtections();
        tiles[46].removeProtections();
        tiles[47].removeProtections();
        tiles[48].removeProtections();
        tiles[49].removeProtections();
        tiles[50].removeProtections();
        tiles[51].removeProtections();
        tiles[52].removeProtections();
        tiles[53].removeProtections();
        tiles[54].removeProtections();
        tiles[55].removeProtections();
        tiles[56].removeProtections();
        tiles[57].removeProtections();
        tiles[58].removeProtections();
        tiles[59].removeProtections();
        tiles[60].removeProtections();
        tiles[61].removeProtections();
        tiles[62].removeProtections();
        tiles[63].removeProtections();
        for (int index = (pieces.size() - 1); index >= 0; --index) {
            pieces.get(index).setProtectedTiles(this);
        }
    }

    public void clear() {
        tiles[0].removeProtections();
        tiles[1].removeProtections();
        tiles[2].removeProtections();
        tiles[3].removeProtections();
        tiles[4].removeProtections();
        tiles[5].removeProtections();
        tiles[6].removeProtections();
        tiles[7].removeProtections();
        tiles[8].removeProtections();
        tiles[9].removeProtections();
        tiles[10].removeProtections();
        tiles[11].removeProtections();
        tiles[12].removeProtections();
        tiles[13].removeProtections();
        tiles[14].removeProtections();
        tiles[15].removeProtections();
        tiles[16].removeProtections();
        tiles[17].removeProtections();
        tiles[18].removeProtections();
        tiles[19].removeProtections();
        tiles[20].removeProtections();
        tiles[21].removeProtections();
        tiles[22].removeProtections();
        tiles[23].removeProtections();
        tiles[24].removeProtections();
        tiles[25].removeProtections();
        tiles[26].removeProtections();
        tiles[27].removeProtections();
        tiles[28].removeProtections();
        tiles[29].removeProtections();
        tiles[30].removeProtections();
        tiles[31].removeProtections();
        tiles[32].removeProtections();
        tiles[33].removeProtections();
        tiles[34].removeProtections();
        tiles[35].removeProtections();
        tiles[36].removeProtections();
        tiles[37].removeProtections();
        tiles[38].removeProtections();
        tiles[39].removeProtections();
        tiles[40].removeProtections();
        tiles[41].removeProtections();
        tiles[42].removeProtections();
        tiles[43].removeProtections();
        tiles[44].removeProtections();
        tiles[45].removeProtections();
        tiles[46].removeProtections();
        tiles[47].removeProtections();
        tiles[48].removeProtections();
        tiles[49].removeProtections();
        tiles[50].removeProtections();
        tiles[51].removeProtections();
        tiles[52].removeProtections();
        tiles[53].removeProtections();
        tiles[54].removeProtections();
        tiles[55].removeProtections();
        tiles[56].removeProtections();
        tiles[57].removeProtections();
        tiles[58].removeProtections();
        tiles[59].removeProtections();
        tiles[60].removeProtections();
        tiles[61].removeProtections();
        tiles[62].removeProtections();
        tiles[63].removeProtections();
        tiles[0].removeOccupant();
        tiles[1].removeOccupant();
        tiles[2].removeOccupant();
        tiles[3].removeOccupant();
        tiles[4].removeOccupant();
        tiles[5].removeOccupant();
        tiles[6].removeOccupant();
        tiles[7].removeOccupant();
        tiles[8].removeOccupant();
        tiles[9].removeOccupant();
        tiles[10].removeOccupant();
        tiles[11].removeOccupant();
        tiles[12].removeOccupant();
        tiles[13].removeOccupant();
        tiles[14].removeOccupant();
        tiles[15].removeOccupant();
        tiles[16].removeOccupant();
        tiles[17].removeOccupant();
        tiles[18].removeOccupant();
        tiles[19].removeOccupant();
        tiles[20].removeOccupant();
        tiles[21].removeOccupant();
        tiles[22].removeOccupant();
        tiles[23].removeOccupant();
        tiles[24].removeOccupant();
        tiles[25].removeOccupant();
        tiles[26].removeOccupant();
        tiles[27].removeOccupant();
        tiles[28].removeOccupant();
        tiles[29].removeOccupant();
        tiles[30].removeOccupant();
        tiles[31].removeOccupant();
        tiles[32].removeOccupant();
        tiles[33].removeOccupant();
        tiles[34].removeOccupant();
        tiles[35].removeOccupant();
        tiles[36].removeOccupant();
        tiles[37].removeOccupant();
        tiles[38].removeOccupant();
        tiles[39].removeOccupant();
        tiles[40].removeOccupant();
        tiles[41].removeOccupant();
        tiles[42].removeOccupant();
        tiles[43].removeOccupant();
        tiles[44].removeOccupant();
        tiles[45].removeOccupant();
        tiles[46].removeOccupant();
        tiles[47].removeOccupant();
        tiles[48].removeOccupant();
        tiles[49].removeOccupant();
        tiles[50].removeOccupant();
        tiles[51].removeOccupant();
        tiles[52].removeOccupant();
        tiles[53].removeOccupant();
        tiles[54].removeOccupant();
        tiles[55].removeOccupant();
        tiles[56].removeOccupant();
        tiles[57].removeOccupant();
        tiles[58].removeOccupant();
        tiles[59].removeOccupant();
        tiles[60].removeOccupant();
        tiles[61].removeOccupant();
        tiles[62].removeOccupant();
        tiles[63].removeOccupant();
    }

    //we used fast equals for Database (HashMap) purposes, speeds things up
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        //System.out.println("EQ");
        return fastEquals((Grid) obj);
    }

    //simply compare the pieces on both grids without their movecounts
    public final boolean fastEquals(Grid other) {
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            if (!pieceEquals(tiles[index].getOccupant(), other.tiles[index].getOccupant())) {
                //System.out.println(tiles[index]);
                //System.out.println(other.tiles[index]);
                return false;
            }
        }
        return true;
    }

    //maximum security checking 
    public final boolean deepEquals(Grid other) {
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            if (!tiles[index].equals(other.tiles[index])) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("null")
    private static boolean pieceEquals(Piece first, Piece second) {
        boolean firstIsNull = (first == null);
        boolean secondIsNull = (second == null);
        return (!firstIsNull && !secondIsNull) ? first.fastEquals(second) : firstIsNull == secondIsNull;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(LINEAR_LENGTH);
        for (int index = 0; index != LINEAR_LENGTH; ++index) {
            Piece piece = tiles[index].getOccupant();
            if (piece != null) {
                builder.append(piece.getSymbol());
                if (piece.isPawn()) {
                    builder.append(piece.justMadeDoubleJump() ? "T" : "F");
                }
                else if (piece.isKing()) {
                    builder.append(piece.hasMoved() ? "T" : "F");
                }
            }
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tiles);
    }
}