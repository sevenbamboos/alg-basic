package com.sam.wang.util;

import static org.junit.Assert.*;
import static com.sam.wang.util.Try.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TryTest {

    private static DateFormat dateFormat;
    private static Block<Integer> fatalBlock;

    private Block<Integer> parseInt;
    private Block<Date> parseDate;
    private Block<String> splitString;

    private Pipe<Integer,Double> sqrtIt;

    @BeforeClass public static void init() {
        dateFormat = new SimpleDateFormat("yyyyMMdd");

        fatalBlock = () -> {
            if (true) throw new InterruptedException("abc");
            return Integer.parseInt("123");
        };
    }

    private void setup(String strNum, String strDate, String strSplit) {
        parseInt(strNum);
        parseDate(strDate);
        splitString(strSplit);
        sqrtIt = (i) -> {
            System.out.println("\tsqrtIt");
            return Math.sqrt(i);
        };
    }

    private void parseInt(String str) {
        parseInt = () -> {
            System.out.println("\tparseInt");
            return Integer.parseInt(str);
        };
    }

    private void parseDate(String str) {
        parseDate = () -> {
            System.out.println("\tparseDate");
            return dateFormat.parse(str);
        };
    }

    private void splitString(String str) {
        splitString = () -> {
            System.out.println("\tsplitString");
            return str.split(":")[0];
        };
    }

    @Test public void testTry2() {

        setup("123", "20070401", "part1:part2");

        System.out.println("[case] happy path:");
        try2(parseInt, parseDate).yield((n, d) -> String.format("Int:%d, Date:%s", n, d)).andThen(
            r -> assertNotNull(r),
            e -> assertNull(e)
        );

        System.out.println("[case] first logic failed:");
        parseInt("abc");
        try2(parseInt, parseDate).yield((n, d) -> String.format("Int:%d, Date:%s", n, d)).andThen(
            r -> assertNull(r),
            e -> assertNotNull(e)
        );

        System.out.println("[case] second logic failed:");
        parseInt("123");
        parseDate("abcd0401");
        try2(parseInt, parseDate).yield((n, d) -> String.format("Int:%d, Date:%s", n, d)).andThen(
            r -> assertNull(r),
            e -> assertNotNull(e)
        );

        try {
            System.out.println("[case] first logic has fatal error:");
            try2(fatalBlock, parseInt).yield().andThen(
                r -> fail(),
                e -> fail()
            );
            fail();
        } catch (Throwable thr) {}

        System.out.println("[case] block and pipe:");
        try2(parseInt, sqrtIt).yield((i, ii) -> i + ii).andThen(
            r -> assertNull(r),
            e -> assertNull(e)
        );
    }

    //TODO more cases
}
