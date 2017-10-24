package com.sam.wang.util;

import org.junit.Test;

import java.util.Optional;

import static com.sam.wang.util.SimpleDateElement.*;
import static org.junit.Assert.*;

public class SimpleDateElementTest {

    @Test public void testOf() {
        exist(YEAR);
        exist(HOUR);
        exist(TIMEZONE);

        notExist('a');
        notExist('-');
    }

    @Test public void testCheck() {

        valid(YEAR, "2001");
        valid(YEAR, "0001");
        invalid("Year can't be zero", YEAR, "0000");
        invalid("Year must to be numeric", YEAR, "abcd");

        valid(MONTH, "01");
        valid(MONTH, "12");
        invalid("Month can't be more than 12", MONTH, "13");
        invalid("Month must to be numeric", MONTH, "ab");

        // not feb
        check(Optional.empty(), true, DAY, "01", Optional.of(2000), Optional.of(12));
        check(Optional.empty(), true, DAY, "31", Optional.of(2000), Optional.of(12));
        check(Optional.empty(), false, DAY, "00", Optional.of(2000), Optional.of(12));
        check(Optional.empty(), false, DAY, "32", Optional.of(2000), Optional.of(12));

        // not leap year
        check(Optional.empty(), true, DAY, "28", Optional.of(2001), Optional.of(2));
        check(Optional.empty(), false, DAY, "29", Optional.of(2001), Optional.of(2));

        // leap year
        check(Optional.empty(), true, DAY, "29", Optional.of(2000), Optional.of(2));
        check(Optional.empty(), false, DAY, "30", Optional.of(2000), Optional.of(2));

        // no year
        check(Optional.empty(), true, DAY, "29", Optional.empty(), Optional.of(2));
        check(Optional.empty(), false, DAY, "30", Optional.empty(), Optional.of(2));
        check(Optional.empty(), true, DAY, "31", Optional.empty(), Optional.of(1));
        check(Optional.empty(), false, DAY, "32", Optional.empty(), Optional.of(1));

        // no year, no month
        check(Optional.empty(), true, DAY, "31", Optional.empty(), Optional.empty());
        check(Optional.empty(), false, DAY, "32", Optional.empty(), Optional.empty());

        valid(HOUR, "00");
        valid(HOUR, "23");
        invalid("Hour can't be more than 23", HOUR, "24");
        invalid("Hour must to be numeric", HOUR, "ab");

        valid(MINUTE, "00");
        valid(MINUTE, "59");
        invalid("Minute can't be more than 59", MINUTE, "60");
        invalid("Minute must to be numeric", MONTH, "ab");

        valid(SECOND, "00");
        valid(SECOND, "59");
        invalid("Second can't be more than 59", SECOND, "60");
        invalid("Second must to be numeric", SECOND, "ab");

        valid(MILLISECOND, "000000");
        valid(MILLISECOND, "999999");
        invalid("Milli-second must to be numeric", MILLISECOND, "abcdef");

        valid(TIMEZONE, "00");
        valid(TIMEZONE, "12");
        invalid("Time zone for hour can't be more than 12", TIMEZONE, "13");

        valid(TIMEZONE, "0000");
        valid(TIMEZONE, "1259");
        invalid("Time zone for hour can't be more than 13", TIMEZONE, "1359");
        invalid("Time zone for minute can't be more than 59", TIMEZONE, "1260");
        invalid("Time zone can't be of 3 length", TIMEZONE, "123");
        invalid("Time zone can't be of 5 length", TIMEZONE, "12345");

        valid(YEAR_PREFIX, "+");
        valid(YEAR_PREFIX, "-");
        invalid("Only + and - are allowed before year", YEAR_PREFIX, "a");

        valid(TIMEZONE_PREFIX, "+");
        valid(TIMEZONE_PREFIX, "-");
        invalid("Only + and - are allowed before time zone", TIMEZONE_PREFIX, "a");
    }

    private void invalid(String msg, SimpleDateElement part, String input) {
        check(Optional.of(msg), false, part, input, Optional.empty(), Optional.empty());
    }

    private void valid(SimpleDateElement part, String input) {
        check(Optional.empty(), true, part, input, Optional.empty(), Optional.empty());
    }

    private void check(Optional<String> msg, boolean expect, SimpleDateElement part, String input, Optional<Integer> year, Optional<Integer> month) {
        if (msg.isPresent()) {
            assertEquals(msg.get(), expect, part.check(input, year, month));
        } else {
            assertEquals(expect, part.check(input, year, month));
        }
    }

    private void exist(SimpleDateElement expect) {
        Optional<SimpleDateElement> actual = SimpleDateElement.of(expect.symbol);
        assertTrue(actual.isPresent());
        assertEquals(expect, actual.get());
    }

    private void notExist(char ch) {
        Optional<SimpleDateElement> actual = SimpleDateElement.of(ch);
        assertFalse(actual.isPresent());
    }
}
