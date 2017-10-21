package com.sam.wang.util;

import static com.sam.wang.util.Try.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class SimpleDateChecker {

    final char[] template;

    static class DatePart {
        final DateElement element;
        final int startIndex;
        int length;
        boolean optional;
        StringBuilder contents;

        private DatePart(DateElement element, int startIndex) {
            this.element = element;
            this.startIndex = startIndex;
            length = 0;
            optional = false;
        }

        private void addLength() { ++length; }
        private void finishAdd() { contents = new StringBuilder(length); }
        private void optional() { optional = !optional; }
        private void addContent(char ch) { contents.append(ch); }
        private String value() { return contents.toString(); }

        private boolean check(Optional<Integer> year, Optional<Integer> month) {
            String value = value();
            if (!optional && value.isEmpty()) return false;
            return element.check(value, year, month);
        }
    }

    final List<DatePart> parts;

    public SimpleDateChecker(String pattern) {

        int patternLength = pattern.length();
        template = new char[patternLength];
        parts = new ArrayList<>();
        DatePart currentPart = null;

        for (int i = 0; i < patternLength; i++) {
            char ch = pattern.charAt(i);

            // save to template
            template[i] = ch;

            // save to parts
            Optional<DateElement> element = DateElement.of(ch);
            if (element.isPresent()) {
                DateElement ele = element.get();

                if (currentPart == null) { // first part
                    parts.add(currentPart = new DatePart(ele, i));

                } else if (currentPart.element != ele) { // a new part comes
                    currentPart.finishAdd();
                    parts.add(currentPart = new DatePart(ele, i));

                } else { // the same part
                    currentPart.addLength();
                }
            }
        }
    }

    public SimpleDateChecker optional(DateElement element) {
        Optional<DatePart> part = parts.stream().filter(p -> p.element == element).findFirst();

        if (!part.isPresent())
            throw new IllegalArgumentException(
                "Not found date element:" + element + " in template:" + Arrays.toString(template));

        part.get().optional();
        return this;
    }

    public boolean check(String s) {

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            char chFromTemp = template[i];

            Optional<DateElement> element = DateElement.of(chFromTemp);
            if (!element.isPresent()) continue;

            Optional<DatePart> part = currentPart(i);
            if (!part.isPresent()) return false;

            DateElement ele = element.get();
            DatePart currentPart = part.get();
            if (currentPart.element != ele) return false;

            currentPart.addContent(ch);
        }

        Optional<Integer> year = part(DateElement.YEAR)
            .map(p -> tryWith(() -> Integer.parseInt(p.value())).toOption())
            .orElse(Optional.empty());

        Optional<Integer> month = part(DateElement.MONTH)
            .map(p -> tryWith(() -> Integer.parseInt(p.value())).toOption())
            .orElse(Optional.empty());

        return parts.stream().allMatch(p -> p.check(year, month));
    }

    private Optional<DatePart> part(DateElement element) {
        return parts.stream()
            .filter(p -> p.element == element)
            .findFirst();
    }

    private Optional<DatePart> currentPart(int index) {
        return parts.stream()
            .filter(p -> index >= p.startIndex && index < p.startIndex + p.length)
            .findFirst();
    }
}

enum DateElement {
    YEAR_PREFIX('~'),
    YEAR('Y'),
    MONTH('M'),
    DAY('D'),
    HOUR('h'),
    MINUTE('m'),
    SECOND('s'),
    MILLISECOND('S'),
    TIMEZONE_PREFIX('&'),
    TIMEZONE('Z');

    final char simbol;
    private DateElement(char ch) { simbol = ch; }

    static Optional<DateElement> of(char ch) {
        for (DateElement ele : values()) {
            if (ele.simbol == ch) return Optional.of(ele);
        }
        return Optional.empty();
    }

    boolean check(String s, Optional<Integer> year, Optional<Integer> month) {

        Try<Integer> parseInt = parseInt(s);

        switch (this) {
            case MILLISECOND:
                return parseInt.isSuccessful();
            case YEAR:
                return parseInt.map(i -> isValidYear(i)).orElse(false);
            case MONTH:
                return parseInt.map(i -> isValidMonth(i)).orElse(false);
            case DAY:
                return parseInt.map(i -> isValidDay(year, month, i)).orElse(false);
            case HOUR:
                return parseInt.map(i -> isValidHour(i)).orElse(false);
            case MINUTE:
                return parseInt.map(i -> isValidMinute(i)).orElse(false);
            case SECOND:
                return parseInt.map(i -> isValidSecond(i)).orElse(false);
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

            Block<Integer> parseHour = () -> Integer.parseInt(s.substring(0, 2));
            Block<Integer> parseMin = () -> Integer.parseInt(s.substring(2));

            return try2(parseHour, parseMin).yield((i1, i2) -> new Tuple2<Integer, Integer>(i1, i2))
                .map(tp2 -> {
                    boolean validHour = tp2._1 >= 0 && tp2._1 < 13;
                    boolean validMin = tp2._2 >= 0 && tp2._2 < 60;
                    return validHour && validMin;
                })
                .orElse(false);

        } else if (s.length() <= 2) {
            return parseInt(s).map(i -> i >= 0 && i < 13).orElse(false);
        } else {
            return false;
        }
    }

    private static Try<Integer> parseInt(String s) { return tryWith(() -> Integer.parseInt(s)); }

    private static boolean isValidDay(Optional<Integer> year, Optional<Integer> month, int d) {
        return month.map(m -> d > 0 && d <= daysOfMonth.apply(year, m))
            .orElseGet(() -> d > 0 && d <= 31);
    }

    private static BiFunction<Optional<Integer>,Integer,Integer> daysOfMonth = (year, month) -> {
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
        if (y % 4 != 0) return false;
        else if (y % 100 != 0) return true;
        else return y % 400 == 0;
    }

    private static boolean isValidYear(int x) { return x != 0; }
    private static boolean isValidMonth(int x) { return x > 0 && x < 13; }
    private static boolean isValidHour(int x) { return x >= 0 && x < 24; }
    private static boolean isValidMinute(int x) { return x >= 0 && x < 60; }
    private static boolean isValidSecond(int x) { return x >= 0 && x < 60; }

}
