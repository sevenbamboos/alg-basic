package com.samwang.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Memorize computational result for Function or Supplier 
public class Memoizer<T, U> {

    // major for test purpose to verify actual count of execution
    public static interface MemoizerCounter {
    	int executed();
    	int performed();
    }    
    
    private final Map<T, U> cache = new ConcurrentHashMap<>();

	private int executedCount = 0;
    private int performedCount = 0;    
    
    private Memoizer() {}

    public static <T, U> Function<T, U> memoize(Function<T, U> function) {
        return new Memoizer<T, U>().doMemoize(function);
    }

    public Function<T, U> doMemoize(Function<T, U> function) {
        return input -> cache.computeIfAbsent(input, function::apply);
    }
    
	public Supplier<U> doMemoize(T key, Supplier<U> supplier) {
	  	return toSupplier(key, doMemoize(toFunc(supplier)));
	}
	    
	private Function<T, U> toFunc(Supplier<U> supplier) {
	   	return _ignored -> supplier.get();
	}
	    
    private Supplier<U> toSupplier(T t, Function<T, U> func) {
	    return () -> func.apply(t);
	}    
    
    public MemoizerCounter counter() {
	    return new MemoizerCounter() {
			@Override public int executed() { return executedCount; }
			@Override public int performed() { return performedCount; }
	    };
    }    
}
