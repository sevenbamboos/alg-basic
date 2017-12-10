package com.samwang.util;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.samwang.util.Try.*;

public enum SimpleDateElement {
    YEAR_PREFIX('~', false),
    YEAR('Y'),
    MONTH('M'),
    DAY('D'),
    HOUR('h'),
    MINUTE('m'),
    SECOND('s'),
    MILLISECOND('S'),
    TIMEZONE_PREFIX('&', false),
    TIMEZONE('Z');

    final char symbol;
    final boolean numerical;

    SimpleDateElement(char ch) { this(ch, true); }
    SimpleDateElement(char ch, boolean numerical) { symbol = ch; this.numerical = numerical; }

    static Optional<SimpleDateElement> of(char ch) {
        for (SimpleDateElement ele : values()) {
            if (ele.symbol == ch) return Optional.of(ele);
        }
        return Optional.empty();
    }

    boolean check(String s, Optional<Integer> year, Optional<Integer> month) {

        Try<Integer> parseInt = parseInt(s);

        switch (this) {
            case MILLISECOND:
                return parseInt.isSuccessful();
            case YEAR:
                return parseInt.map(SimpleDateElement::isValidYear).orElse(false);
            case MONTH:
                return parseInt.map(SimpleDateElement::isValidMonth).orElse(false);
            case DAY:
                return parseInt.map(i -> isValidDay(year, month, i)).orElse(false);
            case HOUR:
                return parseInt.map(SimpleDateElement::isValidHour).orElse(false);
            case MINUTE:
                return parseInt.map(SimpleDateElement::isValidMinute).orElse(false);
            case SECOND:
                return parseInt.map(SimpleDateElement::isValidSecond).orElse(false);
            case YEAR_PREFIX:
            case TIMEZONE_PREFIX:
                return "+".equals(s) || "-".equals(s);
            case TIMEZONE:
                return isValidTimeZone(s);
            default:
                throw new RuntimeException("Can't check " + this);
        }
    }

    private static boolean isValidTimeZone(String s) {
        if (s.length() == 4) {

            Expression<Integer> parseHour = () -> Integer.parseInt(s.substring(0, 2));
            Expression<Integer> parseMin = () -> Integer.parseInt(s.substring(2));

            return try2(parseHour, parseMin).yield(Tuple2::new)
                .map(tp2 -> {
                    boolean validHour = tp2._1 >= 0 && tp2._1 < 15;
                    boolean lessThan1400 = tp2._1 == 14 && tp2._2 == 0;
                    boolean validMin = lessThan1400 || (tp2._1 != 14 && tp2._2 >= 0 && tp2._2 <= 60);
                    return validHour && validMin;
                })
                .orElse(false);

        } else if (s.length() <= 2) {
            return parseInt(s).map(i -> i >= 0 && i < 15).orElse(false);
        } else {
            return false;
        }
    }

    private static Try<Integer> parseInt(String s) { return tryWith(() -> Integer.parseInt(s)); }

    private static boolean isValidDay(Optional<Integer> year, Optional<Integer> month, int d) {
        return month.map(m -> d > 0 && d <= daysOfMonth.apply(year, m))
            .orElseGet(() -> d > 0 && d <= 31);
    }

    private final static BiFunction<Optional<Integer>,Integer,Integer> daysOfMonth = (year, month) -> {
        final int[] days = new int[] {31,28,31,30,31,30,31,31,30,31,30,31};

        Predicate<Integer> isFeb = m -> m == 2;
        BiFunction<Integer,Integer,Boolean> isLeapYearAndFeb = (y,m) -> isFeb.test(m) && isLeapYear(y);

        return year.map(y ->
            isLeapYearAndFeb.apply(y, month) ?
                29 // leap year + feb
                : days[month-1]) // non-feb
            .orElseGet(() -> isFeb.test(month) ?
                29 // unknown year + feb
                : days[month-1]); // non-feb
    };

    private static boolean isLeapYear(int y) {
        return y % 4 == 0  && (y % 100 != 0 || y % 400 == 0);
    }

    private static boolean isValidYear(int x) { return x != 0; }
    private static boolean isValidMonth(int x) { return x > 0 && x < 13; }
    private static boolean isValidHour(int x) { return x >= 0 && x < 24; }
    private static boolean isValidMinute(int x) { return x >= 0 && x < 60; }
    private static boolean isValidSecond(int x) { return x >= 0 && x <= 60; }

}
