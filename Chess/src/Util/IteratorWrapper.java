package Util;

import java.util.Arrays;
import java.util.Iterator;

public class IteratorWrapper<E> implements Iterator<E> {

    private Iterator<E>[] list;
    private int current;

    public IteratorWrapper(Iterator<E>... iterators) {
        list = iterators;
    }

    @Override 
    public boolean hasNext() {
        Iterator<E>[] iterators = list;
        int length = iterators.length;
        
        while (current < length && !iterators[current].hasNext()) {
            ++current;     
        }

        return current < length;
    }

    @Override
    public E next() {
        Iterator<E>[] iterators = list;
        int length = iterators.length;
        
        while (current < length && !iterators[current].hasNext()) {
            current++;
        }

        return iterators[current].next();
    }

    @Override
    public void remove() {
        Iterator<E>[] iterators = list;
        for (int index = iterators.length - 1; index >= 0; --index) {
            iterators[index] = null;
        }
        list = null;
    }

    public static void main(String... args) {
        Iterator<Integer> a = Arrays.asList(1, 2, 3, 4).iterator();
        Iterator<Integer> b = Arrays.asList(10, 11, 12).iterator();
        Iterator<Integer> c = Arrays.asList(99, 98, 97).iterator();

        Iterator<Integer> ii = new IteratorWrapper<>(a, b, c);

        while (ii.hasNext()) {
            System.out.println(ii.next());
        }
    }
}