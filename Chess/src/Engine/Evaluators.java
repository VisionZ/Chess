package Engine;

public final class Evaluators {
 
    public static final Evaluator STANDARD = new EvaluatorStandard();
    public static final Evaluator EXPLICIT = new EvaluatorExplicit();
    public static final Evaluator POWERFUL = new EvaluatorPowerful();
    
    //can be toggled at will for testing purposes
    public static Evaluator MAIN = POWERFUL;
    
    private Evaluators() {
        
    }
}