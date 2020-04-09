package com.samwang.anki.impl.model.token;

public class PlainToken implements Token {

    protected String value;

    public PlainToken(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String value(String delim) {
        return value.trim();
    }

    @Override
    public String toString() {
        return value.trim();
    }
}
