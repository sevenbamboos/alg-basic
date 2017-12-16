package com.samwang.common;

import static com.samwang.common.Case.*;
import static com.samwang.common.Result.*;

import java.util.regex.Pattern;

public class EmailValidation {

    static Pattern emailPattern =
        Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

    static Function<String, IO<Nothing>> success = s ->
        () -> { System.out.println("Mail sent to " + s); return Nothing.instance; };

    static Function<String, IO<Nothing>> failure = s ->
        () -> { System.err.println("Error message logged: " + s); return Nothing.instance; };

    public static void main(String... args) {
        emailChecker.apply("this.is@my.email").tryIO(success, failure);
        emailChecker.apply(null).tryIO(success, failure);
        emailChecker.apply("").tryIO(success, failure);
        emailChecker.apply("john.doe@acme.com").tryIO(success, failure);
    }

    static Function<String, Result<String>> emailChecker = s -> match(
        mcase(() -> success(s)),
        mcase(() -> s == null, () -> failure("email must not be null.")),
        mcase(() -> s.length() == 0, () -> failure("email must not be empty.")),
        mcase(() -> !emailPattern.matcher(s).matches(), () -> failure("email " + s + " is invalid."))
    );
}