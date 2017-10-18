package com.sam.wang.util;

import java.util.function.BiFunction;

import com.sam.wang.util.Try.Block;
import com.sam.wang.util.Try.Pipe;

public final class TryBuilder2<R1,R2> {

    private Block<R1> b1;
    private Pipe<R1,R2> b2;

    TryBuilder2(Block<R1> b1, Block<R2> b2) {
        this(b1, Pipe.of(null, b2));
    }

    TryBuilder2(Block<R1> b1, Pipe<R1,R2> b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    //TODO What about f can throw exception
    public <T> Try<T> yield(BiFunction<R1, R2, T> f) {
        return
            Try.tryWith(b1).flatMap(v1 ->
                Try.tryWith(v1, b2).map(v2 ->
                    f.apply(v1, v2)
                )
            );
    }
}
