package com.sam.wang.util;

import java.util.stream.Stream;

public class Tuple2<T1,T2> {
    public final T1 _1;
    public final T2 _2;

    public Tuple2(T1 t1, T2 t2) { _1 = t1; _2 = t2; }

    public static <R1,R2> Stream<Tuple2<R1,R2>> zip(Stream<R1> sm1, Stream<R2> sm2) {
        return sm1.flatMap(s1 -> sm2.map(s2 -> new Tuple2<>(s1, s2)));
    }
}
