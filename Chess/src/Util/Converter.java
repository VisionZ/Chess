package Util;

public abstract class Converter<A extends Object, B extends Object> {
    
    public Converter() {
        
    }
    
    public abstract B convertForward(A a);
    
    public abstract A convertBackward(B b);
}