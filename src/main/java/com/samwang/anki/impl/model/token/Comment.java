package com.samwang.anki.impl.model.token;

public class Comment extends PlainToken {
    public Comment() {
        super("");
    }

    @Override
    public String value(String delim) {
        return "(" + value + ")";
    }

    @Override
    public String toString() {
        return value(null);
    }
}
