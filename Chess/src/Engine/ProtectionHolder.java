package Engine;

import static Util.ChessConstants.LINEAR_LENGTH;

//by storing the tile's protections instead of recalculating them
//we save a lot of time and increase our speed by a lot!
final class ProtectionHolder {

    private static final int LENGTH = LINEAR_LENGTH;
    private BooleanHolder[] protectionGrid = new BooleanHolder[LENGTH];
    
    public ProtectionHolder(Grid grid) {
        for (int index = 0; index != LENGTH; ++index) {
            Tile tile = grid.getTile(index);
            protectionGrid[index] = new BooleanHolder(tile.protectedByWhite(), tile.protectedByBlack());
        }
    }

    /**
     * Resets tile protections on a particular Grid.
     * @param grid The given grid.
     */
    void reset(Grid grid) {
        for (int index = 0; index != LENGTH; ++index) {
            BooleanHolder current = protectionGrid[index];
            grid.getTile(index).setProtections(current.a, current.b);
        }
    }

    /**
     * Clears this ProtectionHolder, rendering it 
     * un-useful after this operations returns.
     */
    void clear() {
        for (int index = 0; index != LENGTH; ++index) {
            protectionGrid[index] = null;
        }
        protectionGrid = null;
    }

    private static final class BooleanHolder {

        private final boolean a;
        private final boolean b;
        
        private BooleanHolder(boolean first, boolean second) {
            a = first;
            b = second;
        }
    }
}