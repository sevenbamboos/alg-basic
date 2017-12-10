package com.samwang.util;

import com.samwang.util.Try.Expression;
import com.samwang.util.Try.Pipe;

public final class TryBuilder3<R1,R2,R3> {

    private final Expression<R1> b1;
    private final Pipe<R1,R2> b2;
    private final Pipe<R2,R3> b3;

    TryBuilder3(Expression<R1> b1, Pipe<R1,R2> b2, Pipe<R2,R3> b3) {
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
