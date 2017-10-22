package com.sam.wang.util;

import org.junit.Test;

import static com.sam.wang.util.DateElement.*;
import static org.junit.Assert.*;

public class SimpleDateCheckerTest {

    @Test public void testDateCheck() {
        SimpleDateChecker checker = new SimpleDateChecker("YYYYMMDD");

        assertFalse(checker.check("2017"));
        assertFalse(checker.check("201710"));
        assertFalse(checker.check("20171022112600"));
        assertFalse(checker.check("20171032"));

        assertTrue(checker.check("20171022"));
    }

    @Test public void testOptioanlDateCheck() {
        SimpleDateChecker checker = new SimpleDateChecker("YYYYMMDD")
            .optional(DAY)
            .optional(MONTH);

        assertFalse(checker.check("17"));

        assertTrue(checker.check("20171022"));
        assertTrue(checker.check("201710"));
        assertTrue(checker.check("2017"));
    }

    @Test public void testTimeCheck() {
        SimpleDateChecker checker = new SimpleDateChecker("hh:mm:ss.SSSSSS")
            .optional(MILLISECOND)
            .optional(SECOND)
            .optional(MINUTE)
            .optional(HOUR);

        assertTrue(checker.check("23:59:59.123456"));
        assertTrue(checker.check("23:59:59."));
        assertTrue(checker.check("23:59:59"));
        assertTrue(checker.check("23:59:"));
        assertTrue(checker.check("23:59"));
        assertTrue(checker.check("23:"));
        assertTrue(checker.check("23"));
        assertTrue(checker.check(""));

        assertFalse(checker.check("2"));
        assertFalse(checker.check("24"));
        assertFalse(checker.check("23:6"));
        assertFalse(checker.check("23:60"));
        assertFalse(checker.check("23:59:60"));
        assertFalse(checker.check("23:59:59.123abc"));
        assertFalse(checker.check("23:59:59.1234567"));
        assertFalse(checker.check("23:59:59:"));
        assertFalse(checker.check("23:59.59"));
    }

    @Test public void testDateTimeCheck() {
        SimpleDateChecker checker = new SimpleDateChecker("YYYYMMDD hh:mm:ss.SSSSSS&ZZZZ")
            .optional(MILLISECOND)
            .optional(SECOND)
            .optional(MINUTE)
            .optional(HOUR)
            .optional(TIMEZONE_PREFIX)
            .optional(TIMEZONE);

        assertTrue(checker.check("20171022 18:33:00.000000+0000"));
        assertTrue(checker.check("20171022 18:33:00.000000+0830"));
        assertTrue(checker.check("20171022 18:33:00.000000"));
        assertTrue(checker.check("20171022 18:33:00"));
        assertTrue(checker.check("20171022 18:33"));
        assertTrue(checker.check("20171022 18"));
        assertTrue(checker.check("20171022 "));

        assertFalse(checker.check("201710"));
        assertFalse(checker.check("20171022 18:33:00.000000+8"));
        assertFalse(checker.check("20171022 18:33:00.000000+08"));
        assertFalse(checker.check("20171022 18:33:00.000000+1359"));
        assertFalse(checker.check("20171022 18:33:00.000000+0860"));
    }
}
