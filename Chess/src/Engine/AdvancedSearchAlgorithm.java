package Engine;

public abstract class AdvancedSearchAlgorithm {
    
    protected AdvancedSearchAlgorithm() {
        
    }
    
    public abstract int min(Board board, int depth, int alpha, int beta);
   
    public abstract int max(Board board, int depth, int alpha, int beta);
}