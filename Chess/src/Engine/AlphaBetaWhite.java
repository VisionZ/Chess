package Engine;

public final class AlphaBetaWhite {
    
    private AlphaBetaWhite() {
        
    }
    
    public static final int min(Board board, int depth) {
        return -AlphaBetaBlack.max(board, depth);
    }
    
    public static final int max(Board board, int depth) {
        return -AlphaBetaBlack.min(board, depth);
    }
}