package com.samwang.demo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InterviewATest {

    @Test public void test1_Normal() {
        String actual = InterviewA.nameList(Person.apply(
            "Alice", "F",
            "Bob", "M",
            "Charlie", "Unknown"
        ));
        String expected = String.format("MS. %s,\nMR. %s,\n%s", "Alice", "Bob", "Charlie");
        assertEquals(expected, actual);
    }

    @Test public void test1_EmptyList() {
        try {
            InterviewA.format(Person.apply());
            fail();
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    @Test public void test1_EmptyName() {
        String actual = InterviewA.format(Person.apply(
            "Alice", "F",
            null, "M",
            "Charlie", "U"));
        String expected = String.format("MS. %s,\nMR. %s,\n%s", "Alice", "<No name>", "Charlie");
        assertEquals(expected, actual);
    }

    @Test public void test1_SingleName_WithoutDelim() {
        String actual = InterviewA.format(Person.apply("Alice", "F"));
        String expected = "MS. Alice";
        assertEquals(expected, actual);
    }
}
