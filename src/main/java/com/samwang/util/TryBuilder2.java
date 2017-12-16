package com.samwang.util;

import java.util.function.BiFunction;

import com.samwang.util.Try.Expression;
import com.samwang.util.Try.Pipe;

public final class TryBuilder2<R1,R2> {

    private final Expression<R1> b1;
    private final Pipe<R1,R2> b2;

    TryBuilder2(Expression<R1> b1, Expression<R2> b2) {
        this(b1, Pipe.of(null, b2));
    }

    TryBuilder2(Expression<R1> b1, Pipe<R1,R2> b2) {
        this.b1 = b1;
        this.b2 = b2;
    }

    // TODO What about f can throw exception ?
    // But map has already taken care of try ?
    public <T> Try<T> lift(BiFunction<R1, R2, T> f) {
        return
            Try.tryWith(b1).flatMap(v1 ->
                Try.tryWith(v1, b2).map(v2 ->
                    f.apply(v1, v2)
                )
            );
    }

    public Try<R2> lift() {
        return lift((b1, b2) -> b2);
    }
}
