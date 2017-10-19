package com.sam.wang.util;

import java.util.List;
import java.util.function.Function;

public class PerformanceReport {
    public final long min;
    public final long max;
    public final long middle;
    public final double mean;
    public final double sd;

    public PerformanceReport(List<Long> times) {
        long mi = 0, ma = 0, mid = 0, sum = 0;
        double me, s;
        int size = times.size();

        for (int i = 0; i < size; i++) {
            long time = times.get(i);
            if (time < mi) mi = time;
            if (time > ma) ma = time;
            if (i == size / 2) mid = time;
            sum += time;
        }

        me = sum * 1.0 / size;
        double de = times.stream().map(x -> Math.pow(x-me, 2)).reduce(0.0, (a,b) -> a+b);
        s = Math.sqrt(de/(size-1));

        this.min = mi;
        this.max = ma;
        this.middle = mid;
        this.mean = me;
        this.sd = s;
    }

    @Override
    public String toString() {
        return summary(d -> d);
    }

    public String summary(Function<Double,Double> unit) {
        Function<Long,Long> unit2 = lo ->
            unit.apply(lo.doubleValue()).longValue();
        return summary(unit, unit2);
    }

    private String summary(Function<Double,Double> unit, Function<Long,Long> unit2) {
        return String.format("min=%d, max=%d, middle=%d, mean=%.02f, sd=%.03f", unit2.apply(min), unit2.apply(max), unit2.apply(middle), unit.apply(mean), unit.apply(sd));
    }
}
