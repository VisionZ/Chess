package Engine;

public final class SecureAlphaBetaWhite {
    
    private SecureAlphaBetaWhite() {
        
    }

    //these methods will use checking if SecureAlphaBetaBlack does
    static final int min(Board board, int depth) {
        return -SecureAlphaBetaBlack.max(board, depth);
    }
    
    static final int max(Board board, int depth) {
        return -SecureAlphaBetaBlack.min(board, depth);
    }
}