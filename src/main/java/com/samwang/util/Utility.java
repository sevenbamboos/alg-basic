package com.samwang.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum Utility {
	;
    public static Optional<String> notEmpty(String s) {

	    return s != null && !s.isEmpty() ? Optional.of(s) : Optional.empty();
    }

    public static Optional<String> notBlank(String s) {
        return s != null && !s.trim().isEmpty() ? Optional.of(s) : Optional.empty();
    }

    public static void require(boolean b, Optional<String> msg) {
	    if (!b) {
	        throw new IllegalStateException(msg.orElse("Pre-condition failed due to unknown reason"));
        }
    }

    public static void foo(Provider<String> s) {
        System.out.println(s.provide());
    }

    @FunctionalInterface
    private interface Provider<T> {
        T provide();
    }

    public static void main(String[] args) {
        foo(() -> "abc");
    }
}
