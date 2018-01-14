package com.samwang.demo;

import com.samwang.demo.LegacyData.Type;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static com.samwang.demo.NewDataSetter.Checker.*;
import static com.samwang.demo.NewDataSetter.Setter.*;

public class NewDataSetter<TParsed> {

    private static final LogRepo logger = LogRepo.global();

    interface Checker<T> extends Function<String, Optional<T>> {

        default <T2> Checker<T2> and(Checker<T2> chk) {
            return str ->
                apply(str)
                    .map(_ignored -> chk.apply(str))
                    .orElse(Optional.empty());
        }

        static Checker<String> len(int min) {
            return str -> str.length() >= min
                ? Optional.of(str)
                : Optional.empty();
        }

        static Checker<String> invalid(int... chars) {
            return str -> str.chars().allMatch(s -> Arrays.stream(chars).allMatch(c -> s != c))
                ? Optional.of(str)
                : Optional.empty();
        }

        static Checker<Integer> isInt() {
            return str -> {
                try {
                    return Optional.of(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            };
        }

        static Checker<Float> isFloat() {
            return str -> {
                try {
                    return Optional.of(Float.parseFloat(str));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            };
        }
    }

    interface Setter<T> extends Function<LegacyData,Function<T,LegacyData>> {

        static Setter<String> setText() {
            return data -> value ->
                data.setText(value);
        }

        static Setter<Integer> setInt() {
            return data -> value ->
                data.setInt(value);
        }

        static Setter<Float> setFloat() {
            return data -> value ->
                data.setFloat(value);
        }
    }

    public static NewDataSetter of(Type type, String value) {
        switch (type) {
            case INT:
                return new NewDataSetter<>(type, value, isInt(), setInt());
            case FLOAT:
                return new NewDataSetter<>(type, value, isFloat(), setFloat());
            case TEXT:
                return new NewDataSetter<>(type, value, len(5).and(invalid('a', 'e', 'i', 'o', 'u')), setText());
            default:
                throw new IllegalArgumentException("Unknown type:" + type);
        }
    }

    private final Type type;
    private final String value;
    private final Checker<TParsed> checker;
    private final Setter<TParsed> setter;

    private NewDataSetter(Type type, String value, Checker<TParsed> checker, Setter<TParsed> setter) {
        this.type = type;
        this.value = value;
        this.checker = checker;
        this.setter = setter;
    }

    public void checkAndApply(LegacyData data) {
        checker.apply(value)
            .map(parsed -> setter.apply(data).apply(parsed))
            .orElseGet(() -> {
                logger.log("Invalid value " + value);
                return data.clear(type);
            });
    }

    boolean check() {
        return checker.apply(value).map(parsed -> true).orElse(false);
    }
}

