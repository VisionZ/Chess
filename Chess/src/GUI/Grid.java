package GUI;

import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.LINEAR_LENGTH;
import static Util.ChessConstants.NUMBER_OF_PIECES;
import Util.Constants;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

//use loop unrolling, but I doubt that changes anything significantly.
@SuppressWarnings("EqualsAndHashcode")
public final class Grid implements Renderable {

    private final Tile[] tiles = new Tile[LINEAR_LENGTH];
    
    public Grid(int x, int y, final int width, final int height) {
        final int originalX = x;
        for (int index = 0, row = 0, column = 0; index < LINEAR_LENGTH; ++index) {
            if (column == LENGTH) {
                x = originalX;
                y += height;
                ++row;
                column = 0;
            }
            tiles[index] = (row % 2 == 0) ? new Tile(x, y, width, height, row, column, null, column % 2 == 0) : new Tile(x, y, width, height, row, column, null, column % 2 != 0);
            x += width;
            ++column;
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

    public Tile getTile(int row, int column) {
        return tiles[LENGTH * row + column];
    }

    public Tile getTile(int index) {
        return tiles[index];
    }

    @Override
    public void render(Graphics context) {
        tiles[0].render(context);
        tiles[1].render(context);
        tiles[2].render(context);
        tiles[3].render(context);
        tiles[4].render(context);
        tiles[5].render(context);
        tiles[6].render(context);
        tiles[7].render(context);
        tiles[8].render(context);
        tiles[9].render(context);
        tiles[10].render(context);
        tiles[11].render(context);
        tiles[12].render(context);
        tiles[13].render(context);
        tiles[14].render(context);
        tiles[15].render(context);
        tiles[16].render(context);
        tiles[17].render(context);
        tiles[18].render(context);
        tiles[19].render(context);
        tiles[20].render(context);
        tiles[21].render(context);
        tiles[22].render(context);
        tiles[23].render(context);
        tiles[24].render(context);
        tiles[25].render(context);
        tiles[26].render(context);
        tiles[27].render(context);
        tiles[28].render(context);
        tiles[29].render(context);
        tiles[30].render(context);
        tiles[31].render(context);
        tiles[32].render(context);
        tiles[33].render(context);
        tiles[34].render(context);
        tiles[35].render(context);
        tiles[36].render(context);
        tiles[37].render(context);
        tiles[38].render(context);
        tiles[39].render(context);
        tiles[40].render(context);
        tiles[41].render(context);
        tiles[42].render(context);
        tiles[43].render(context);
        tiles[44].render(context);
        tiles[45].render(context);
        tiles[46].render(context);
        tiles[47].render(context);
        tiles[48].render(context);
        tiles[49].render(context);
        tiles[50].render(context);
        tiles[51].render(context);
        tiles[52].render(context);
        tiles[53].render(context);
        tiles[54].render(context);
        tiles[55].render(context);
        tiles[56].render(context);
        tiles[57].render(context);
        tiles[58].render(context);
        tiles[59].render(context);
        tiles[60].render(context);
        tiles[61].render(context);
        tiles[62].render(context);
        tiles[63].render(context);
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
        tiles[0].setTint(null);
        tiles[1].setTint(null);
        tiles[2].setTint(null);
        tiles[3].setTint(null);
        tiles[4].setTint(null);
        tiles[5].setTint(null);
        tiles[6].setTint(null);
        tiles[7].setTint(null);
        tiles[8].setTint(null);
        tiles[9].setTint(null);
        tiles[10].setTint(null);
        tiles[11].setTint(null);
        tiles[12].setTint(null);
        tiles[13].setTint(null);
        tiles[14].setTint(null);
        tiles[15].setTint(null);
        tiles[16].setTint(null);
        tiles[17].setTint(null);
        tiles[18].setTint(null);
        tiles[19].setTint(null);
        tiles[20].setTint(null);
        tiles[21].setTint(null);
        tiles[22].setTint(null);
        tiles[23].setTint(null);
        tiles[24].setTint(null);
        tiles[25].setTint(null);
        tiles[26].setTint(null);
        tiles[27].setTint(null);
        tiles[28].setTint(null);
        tiles[29].setTint(null);
        tiles[30].setTint(null);
        tiles[31].setTint(null);
        tiles[32].setTint(null);
        tiles[33].setTint(null);
        tiles[34].setTint(null);
        tiles[35].setTint(null);
        tiles[36].setTint(null);
        tiles[37].setTint(null);
        tiles[38].setTint(null);
        tiles[39].setTint(null);
        tiles[40].setTint(null);
        tiles[41].setTint(null);
        tiles[42].setTint(null);
        tiles[43].setTint(null);
        tiles[44].setTint(null);
        tiles[45].setTint(null);
        tiles[46].setTint(null);
        tiles[47].setTint(null);
        tiles[48].setTint(null);
        tiles[49].setTint(null);
        tiles[50].setTint(null);
        tiles[51].setTint(null);
        tiles[52].setTint(null);
        tiles[53].setTint(null);
        tiles[54].setTint(null);
        tiles[55].setTint(null);
        tiles[56].setTint(null);
        tiles[57].setTint(null);
        tiles[58].setTint(null);
        tiles[59].setTint(null);
        tiles[60].setTint(null);
        tiles[61].setTint(null);
        tiles[62].setTint(null);
        tiles[63].setTint(null);
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
        for (int pieceIndex = 0, numberOfPieces = pieces.size(); pieceIndex != numberOfPieces; ++pieceIndex) {
            pieces.get(pieceIndex).setProtectedTiles(this);
        }
    }

    public List<Piece> getPieces() {
        List<Piece> pieces = new ArrayList<>(NUMBER_OF_PIECES);
        for (int index = 0; index < LINEAR_LENGTH; ++index) {
            Piece occupant = tiles[index].getOccupant();
            if (occupant != null) {
                pieces.add(occupant);
            }
        }
        return pieces;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Grid)) {
            return false;
        }
        Grid other = (Grid) obj;
        for (int index = 0; index < LINEAR_LENGTH; ++index) {
            if (!Constants.equals(tiles[index], other.tiles[index])) {
                System.out.println("Error:");
                System.out.println(tiles[index]);
                System.out.println(other.tiles[index]);
                throw new Error();
            }
        }
        return true;
    }
}