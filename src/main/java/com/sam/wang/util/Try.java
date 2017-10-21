package com.sam.wang.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.sam.wang.util.Try.Block;

public interface Try<R> {

    <T> Try<T> map(Function<R,T> f);
    <T> Try<T> flatMap(Function<R, Try<T>> f);
    Try<R> filter(Predicate<R> f);
    Optional<R> toOption();

    // resolve with no carry about error
    void forEach(Consumer<R> callback);

    // resolve with a success callback and an error handling
    void andThen(Consumer<R> callback, Consumer<Throwable> errorHandling);

    // resolve separately by two steps
    Try<R> ifSuccess(Consumer<R> callback);
    void orElse(Consumer<Throwable> errorHandling);
    R orElse(R defaultValue);

    // section start: better to use other ways to resolve
    R get();
    boolean isSuccessful();
    Throwable exception();
    // section end

    @FunctionalInterface
    interface Block<U> {
        U execute() throws Throwable;
    }

    @FunctionalInterface
    interface Pipe<U,T> {
        T execute(U u) throws Throwable;
        static <R1,R2> Pipe<R1,R2> of(R1 r1, Block<R2> b) {
            return r -> b.execute();
        }
    }

    @FunctionalInterface
    interface TriFunction<T1,T2,T3,R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    static <T> Try<T> tryWith(Block<T> s) {
        try {
            return new Success(s.execute());

        } catch (Throwable e) {
            if (isFatal(e))
                throw new RuntimeException(e);
            else
                return new Failure(e);
        }
    }

    static <U,T> Try<T> tryWith(U u, Pipe<U,T> s) {
        try {
            return new Success(s.execute(u));

        } catch (Throwable e) {
            if (isFatal(e))
                throw new RuntimeException(e);
            else
                return new Failure(e);
        }
    }

    static <T1,T2> TryBuilder2<T1,T2> try2(Block<T1> b1, Block<T2> b2) {
        return new TryBuilder2<>(b1, b2);
    }

    static <T1,T2> TryBuilder2<T1,T2> try2(Block<T1> b1, Pipe<T1,T2> b2) {
        return new TryBuilder2<>(b1, b2);
    }

    static <T1,T2,T3> TryBuilder3<T1,T2,T3> try3(Block<T1> b1, Block<T2> b2, Block<T3> b3) {
        return new TryBuilder3<>(b1, b2, b3);
    }

    static <T1,T2,T3> TryBuilder3<T1,T2,T3> try3(Block<T1> b1, Pipe<T1,T2> b2, Pipe<T2,T3> b3) {
        return new TryBuilder3<>(b1, b2, b3);
    }

    static boolean isFatal(Throwable e) {

        Class[] fatalExceptions = new Class[] {
            VirtualMachineError.class,
            ThreadDeath.class,
            InterruptedException.class,
            LinkageError.class,
        };

        return Arrays.stream(fatalExceptions).anyMatch(c -> e.getClass() == c);
    }
}

final class Success<R> implements Try<R> {

    private R result;

    Success(R result) { this.result = result; }

    @Override public <T> Try<T> map(Function<R, T> f) {
        return Try.tryWith(() -> f.apply(result));
    }

    @Override public <T> Try<T> flatMap(Function<R, Try<T>> f) {
        Try<Try<T>> mapped = Try.tryWith(() -> f.apply(result));
        if (mapped.isSuccessful()) {
            return mapped.get();
        } else {
            return new Failure(mapped.exception());
        }
    }

    @Override
    public Try<R> filter(Predicate<R> f) {
        return f.test(result) ? this : new Failure(new RuntimeException("filter failed"));
    }

    @Override public void forEach(Consumer<R> callback) {
        Try.tryWith(() -> { callback.accept(result); return null; });
    }

    @Override public R get() { return result; }

    @Override public Try<R> ifSuccess(Consumer<R> callback) {
        return Try.tryWith(() -> { callback.accept(result); return result; });
    }

    @Override public boolean isSuccessful() { return true; }

    @Override public void orElse(Consumer<Throwable> errorHandling) { /* do nothing */ }

    @Override public R orElse(R defaultValue) { return result; }

    @Override public Throwable exception() {
        throw new RuntimeException("Success doesn't have exception");
    }

    @Override public void andThen(Consumer<R> callback, Consumer<Throwable> errorHandling) {
        ifSuccess(callback);
    }

    @Override public Optional<R> toOption() { return Optional.ofNullable(result); }
}

final class Failure<R> implements Try<R> {

    private Throwable exception;

    Failure(Throwable exception) { this.exception = exception; }

    @Override public Try map(Function f) { return this; }

    @Override public Try flatMap(Function f) { return this; }

    @Override public Try filter(Predicate f) { return this; }

    @Override public void forEach(Consumer callback) { return; }

    @Override public R get() {
        throw new RuntimeException("Failure has no result");
    }

    @Override public Try ifSuccess(Consumer callback) { return this; }

    @Override public boolean isSuccessful() { return false; }

    @Override public void orElse(Consumer errorHandling) {
        errorHandling.accept(exception);
    }

    @Override public R orElse(R defaultValue) { return defaultValue; }

    @Override public Throwable exception() { return exception; }

    @Override public void andThen(Consumer callback, Consumer errorHandling) {
        orElse(errorHandling);
    }

    @Override public Optional toOption() { return Optional.empty(); }
}
