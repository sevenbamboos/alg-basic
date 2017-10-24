package com.sam.wang.util;

import static java.lang.Math.min;
import static com.sam.wang.util.Try.*;
import static com.sam.wang.util.Utility.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleDateChecker {

    // a template like "YYYYMMDD hhmmss.SSSSSS&ZZZZ"
    private final char[] template;

    // multiple parts of each date element (e.g. year, month, hour, etc.)
    private final List<DatePart> parts;

    public SimpleDateChecker(String pattern) {

        int patternLength = pattern.length();
        template = new char[patternLength];
        parts = new ArrayList<>();
        DatePart currentPart = null;

        // process date template
        for (int i = 0; i < patternLength; i++) {
            char ch = pattern.charAt(i);

            // save to template
            template[i] = ch;

            // save to parts
            Optional<SimpleDateElement> element = SimpleDateElement.of(ch);
            if (element.isPresent()) {
                SimpleDateElement ele = element.get();

                if (currentPart == null) { // first part
                    parts.add(currentPart = new DatePart(ele));

                } else if (currentPart.element != ele) { // a new part comes
                    DatePart nextPart = new DatePart(ele);
                    currentPart.finishAdd(nextPart, Optional.empty());
                    parts.add(currentPart = nextPart);

                } else { // the same part
                    currentPart.addLength();
                }

            } else {
                if (currentPart != null) { // separator after a part
                    currentPart.finishAdd(null, Optional.of(ch));
                }
            }
        }

        // for the last part
        if (currentPart != null) {
            currentPart.finishAdd(null, Optional.empty());
        }
    }

    public SimpleDateChecker optional(SimpleDateElement element) {
        getDatePart(element).optional();
        return this;
    }

    public SimpleDateChecker noFixedLength(SimpleDateElement element) {
        getDatePart(element).notFixed();
        return this;
    }

    private DatePart getDatePart(SimpleDateElement element) {
        return parts.stream()
            .filter(p -> p.element == element)
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("Not found date element:" + element + " in template:" + Arrays.toString(template)));
    }

    public boolean check(String s) {

        parts.forEach(DatePart::resetRuntimeProperty);

        // fill in input to date parts
        char[] input = s.toCharArray();
        int offset = parts.stream().reduce(0, (i, p) -> p.read(input, i), (i1, i2) -> i1);

        // input is longer than expected
        if (offset < s.length()) return false;

        // check each part
        Predicate<DatePart> checkDatePart = p -> {
            Function<SimpleDateElement,Optional<DatePart>> queryDatePart = element ->
                parts.stream().filter(part -> part.element == element).findFirst();

            Optional<Integer> year = queryDatePart.apply(SimpleDateElement.YEAR)
                .flatMap(pa -> tryWith(() -> Integer.parseInt(pa.value())).toOption());

            Optional<Integer> month = queryDatePart.apply(SimpleDateElement.MONTH)
                .flatMap(pa -> tryWith(() -> Integer.parseInt(pa.value())).toOption());

            Try<Void> doCheck = tryWith(() -> p.check(year, month));
            doCheck.orElse(e -> System.err.println(e.getMessage()));
            return doCheck.isSuccessful();

        };
        return parts.stream().allMatch(checkDatePart);
    }

    private static class DatePart {

        // definition property

        private final SimpleDateElement element;
        private final StringBuilder nextSeparatorInTemplate;
        private int length; // for fixed length
        private boolean fixed;
        private boolean optional;
        private DatePart prevPartInTemplate;
        private DatePart nextPartInTemplate;

        // runtime property

        private StringBuilder contents;
        private String actualSeparator;
        private boolean lastOne;

        private DatePart(SimpleDateElement element) {

            // definition property init
            this.element = element;
            length = 1;
            fixed = true;
            optional = false;
            nextSeparatorInTemplate = new StringBuilder();

            // runtime property init
            resetRuntimeProperty();
        }

        private void resetRuntimeProperty() {
            if (contents == null) {
                contents = new StringBuilder();
            } else {
                contents.delete(0, contents.length());
            }
            actualSeparator = null;
            lastOne = false;
        }

        private void addLength() {
            require(contents.length() == 0, Optional.of("Contents should be empty during addLength"));
            require(nextSeparatorInTemplate.length() == 0, Optional.of("Separator next to DatePart should be empty during addLength"));
            ++length;
        }

        private void finishAdd(DatePart nextPart, Optional<Character> nextSeparatorChar) {

            if (nextPart != null) {
                this.nextPartInTemplate = nextPart;
                nextPart.prevPartInTemplate = this;
            }

            nextSeparatorChar.ifPresent(nextSeparatorInTemplate::append);

            // check for unfixed length
            if (!fixed) {
                require(nextSeparatorInTemplate.length() > 0 || (nextPart == null || nextPart.element.numerical),
                    Optional.of("Unfixed length of DatePart must end with a separator (or at the end)"));
            }
        }

        private int read(char[] input, int offset) {

            int i = offset;

            // fill in contents

            if (fixed) {
                while (i < min(input.length, offset + length)) {
                    contents.append(input[i++]);
                }

            } else {
                while (i < input.length) {
                    if (isEnd(input[i])) break;
                    else {
                        contents.append(input[i++]);
                    }
                }
            }

            // fill in next separator
            if (nextSeparatorInTemplate.length() > 0) {
                int offsetIncludingSeparator = i + nextSeparatorInTemplate.length();

                StringBuilder separator = new StringBuilder();
                while (i < min(input.length, offsetIncludingSeparator)) {
                    separator.append(input[i++]);
                }
                actualSeparator = separator.toString();
            }

            // remember the last part (according to input)
            if (i == input.length) {
                lastOne = true;
            }

            return i;
        }

        private boolean isEnd(char ch) {
            require(!fixed, Optional.of("Check end with character only works for unfixed-length"));

            // in case of separator
            if (nextSeparatorInTemplate.length() > 0) {
                // compare with the first separator next to DatePart
                return ch == nextSeparatorInTemplate.charAt(0);
            }

            // in case of non-separator
            if (nextPartInTemplate != null) {
                SimpleDateElement element = nextPartInTemplate.element;
                require(!element.numerical, Optional.of("Numerical element can't be used as separator"));

                return element.check(ch + "", Optional.empty(), Optional.empty());
            }

            return false;
        }

        private void optional() { optional = true; }
        private void notFixed() { fixed = false; }

        private String value() { return contents.toString(); }

        private void check(Optional<Integer> year, Optional<Integer> month) throws DateContentException {

            String value = value();

            // check for optional
            if (value.isEmpty()) {
                if (!optional) throw new DateContentException("Element " + element + " should not be empty");
                else return;
            }

            // check previous separator
            if (prevPartInTemplate != null) prevPartInTemplate.checkSeparator();

            // check fixed length
            if (fixed && value.length() != length) {
                throw new DateContentException("Fixed length element " + element + " length not match");
            }

            // check unfixed length
            if (!fixed && value.length() > length) {
                throw new DateContentException("Element " + element + " length longer than expect " + length);
            }

            // check date contents
            if (!element.check(value, year, month)) {
                throw new DateContentException("Element " + element + " content check failed, content:" + value);
            }

            // check separator for the last part
            if (lastOne) checkSeparator();
        }

        private void checkSeparator() throws DateContentException {

            Optional<String> expected = notEmpty(nextSeparatorInTemplate.toString());
            Optional<String> actual = notEmpty(actualSeparator);

            if (expected.isPresent() && actual.isPresent()) {
                if (lastOne) throw new DateContentException("Element " + element + " is the last one so it should not have separators after");
                if (!expected.get().equals(actual.get())) throw new DateContentException("Element " + element + " separators not match");

            } else if (expected.isPresent()) {
                if (!lastOne) throw new DateContentException("Element " + element + " missing separators");

            } else {
                if (actual.isPresent()) throw new DateContentException("Element " + element + " has unnecessary separators");
            }

        }
    }
}

class DateContentException extends Exception {
    DateContentException(String cause) { super(cause); }
}
