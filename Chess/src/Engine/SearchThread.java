package Engine;

import Engine.AI.PositionHolder;
import java.util.ArrayList;
import java.util.List;

final class SearchThread extends Thread {

    private final boolean side;
    private final int maxDepth;
    private SearchTimer timer;
    private PositionHolder position;
    private List<Integer> results = new ArrayList<>(6);

    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    SearchThread(PositionHolder holder, boolean color, int depth, SearchTimer searchTimer) {
        position = holder;
        side = color; 
        maxDepth = depth;
        timer = searchTimer;
        start();
    }

    int getMaxDepth() {
        return results.size();
    }

    int getScore(int index) {
        return results.get(index);
    }

    @Override
    public final String toString() {
        return "SearchThread Depth: " + results.size() + " Results: " + results.toString();
    }

    @Override
    public final void run() {
        Board board = new Board(position.grid, position.whites, position.blacks);
        if (side) {
            results.add(AlphaBetaWhite.min(board, 1));
            for (int searchDepth = 2; searchDepth <= maxDepth; ++searchDepth) {
                int result = AlphaBetaWhite.min(board, searchDepth);
                if (timer.timeOver()) {
                    return;
                }
                results.add(result);
            }
        }
        else {
            results.add(AlphaBetaBlack.min(board, 1));
            for (int searchDepth = 2; searchDepth <= maxDepth; ++searchDepth) {
                int result = AlphaBetaBlack.min(board, searchDepth);
                if (timer.timeOver()) {
                    return;
                }
                results.add(result);
            }
        }
        timer = null;
        position = null;
    }
    
    /**
     * Clears all internal references,
     * freeing up memory. After this method returns, all methods will
     * throw a NullPointerException
     */
    void clear() {
        timer = null;
        position = null;
        results.clear();
        results = null;
    }
}