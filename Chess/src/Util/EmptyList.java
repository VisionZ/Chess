package Util;

import java.util.AbstractList;
import java.util.List;

/**
 * A class representing an empty list. This class is immutable and 
 * is designed to have the lowest memory footprint as possible. 
 * @author Will
 * @param <E> The type of object this list 'contains'.
 */
public final class EmptyList<E> extends AbstractList<E> implements List<E> {
    
    public EmptyList() {
        
    }
    
    @Override
    public final E get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        return 0;
    }
    
    @Override
    public final boolean isEmpty() {
        return true;
    }
}