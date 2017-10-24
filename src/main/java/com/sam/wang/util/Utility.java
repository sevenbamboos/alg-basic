package com.sam.wang.util;

import java.util.Optional;

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
}
