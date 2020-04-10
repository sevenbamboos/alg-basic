package com.samwang.anki.impl.model.token;

public interface Token {

    String value(String delim);

    default String value(TokenContext ctx) {
        return value(ctx.delim);
    }
}
