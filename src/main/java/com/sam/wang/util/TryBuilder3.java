package com.sam.wang.util;

import com.sam.wang.util.Try.Block;
import com.sam.wang.util.Try.Pipe;

public final class TryBuilder3<R1,R2,R3> {

    private Block<R1> b1;
    private Pipe<R1,R2> b2;
    private Pipe<R2,R3> b3;

    TryBuilder3(Block<R1> b1, Block<R2> b2, Block<R3> b3) {
        this(b1, Pipe.of(null, b2), Pipe.of(null, b3));
    }

    TryBuilder3(Block<R1> b1, Pipe<R1,R2> b2, Pipe<R2,R3> b3) {
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
    }

    public <T> Try<T> yield(Try.TriFunction<R1, R2, R3, T> f) {
        return
            Try.tryWith(b1).flatMap(v1 ->
                Try.tryWith(v1, b2).flatMap(v2 ->
                    Try.tryWith(v2, b3).map(v3 ->
                        f.apply(v1, v2, v3)
                    )
                )
            );
    }
}
