package Engine;

public interface Evaluator {
    
    int evaluateInWhitePerspective(final Board board);

    int evaluateInBlackPerspective(final Board board);
    
}