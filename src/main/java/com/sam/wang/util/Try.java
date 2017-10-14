package com.sam.wang.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Try<R> {

    <T> Try<T> map(Function<R,T> f);
    <T> Try<T> flatMap(Function<R, Try<T>> f);
    void forEach(Consumer<R> callback);
    R get();
    Try<R> ifSuccess(Consumer<R> callback);
    boolean isSuccessful();
    void orElse(Consumer<Throwable> errorHandling);
    Throwable exception();

    @FunctionalInterface
    interface Block<U> {
        U execute() throws Throwable;
    }

    public static <T> Try<T> tryWith(Block<T> s) {
        try {
            return new Success(s.execute());

        } catch (Throwable e) {
            if (isFatal(e))
                throw new RuntimeException(e);
            else
                return new Failure(e);
        }
    }

    static boolean isFatal(Throwable e) {

        Class[] fatalExceptions = new Class[] {
            VirtualMachineError.class,
            ThreadDeath.class,
            InterruptedException.class,
            LinkageError.class,
        };

        return Arrays.stream(fatalExceptions).anyMatch(klass -> e.getClass() == klass);
    }

    public static void main(String[] args) {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        Try<Integer> parseInt = tryWith(() -> {if (true) throw new InterruptedException("abc"); return Integer.parseInt("123");});
        Try<Date> parseDate = tryWith(() -> dateFormat.parse("20010210"));

        Try<String> result = parseInt.flatMap(i ->
            parseDate.map(d -> i + "," + d));

        // 1
        result.forEach(r -> System.out.println("1.Result:" + r));

        // 2
        result.ifSuccess(r -> System.out.println("2.Result:" + r))
            .orElse(e -> System.err.println("Exception:" + e.getMessage()));
    }
}

class Success<R> implements Try<R> {

    private R result;

    Success(R result) { this.result = result; }

    @Override public <T> Try<T> map(Function<R, T> f) {
        return Try.tryWith(() -> f.apply(result));
    }

    @Override
    public <T> Try<T> flatMap(Function<R, Try<T>> f) {
        Try<Try<T>> mapped = Try.tryWith(() -> f.apply(result));
        if (mapped.isSuccessful()) {
            return mapped.get();
        } else {
            return new Failure(mapped.exception());
        }
    }

    @Override
    public void forEach(Consumer<R> callback) {
        Try.tryWith(() -> { callback.accept(result); return null; });
    }

    @Override public R get() { return result; }

    @Override
    public Try<R> ifSuccess(Consumer<R> callback) {
        return Try.tryWith(() -> { callback.accept(result); return result; });
    }

    @Override public boolean isSuccessful() { return true; }

    @Override public void orElse(Consumer<Throwable> errorHandling) { /* do nothing */ }

    @Override public Throwable exception() {
        throw new RuntimeException("Success doesn't have exception");
    }
}

class Failure implements Try {

    private Throwable exception;

    Failure(Throwable exception) { this.exception = exception; }

    @Override public Try map(Function f) { return this; }

    @Override public Try flatMap(Function f) { return this; }

    @Override public void forEach(Consumer callback) { return; }

    @Override public Object get() {
        throw new RuntimeException("Failure has no result");
    }

    @Override public Try ifSuccess(Consumer callback) { return this; }

    @Override public boolean isSuccessful() { return false; }

    @Override public void orElse(Consumer errorHandling) {
        errorHandling.accept(exception);
    }

    @Override public Throwable exception() { return exception; }
}
