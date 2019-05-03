package Engine;

public final class SecureMinMaxWhite {
    
    private SecureMinMaxWhite() {
        
    }
    
    static final int min(Board board, int depth) {
        return -SecureMinMaxBlack.max(board, depth);
    }
    
    static final int max(Board board, int depth) {
        return -SecureMinMaxBlack.min(board, depth);
    }
}