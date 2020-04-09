package com.samwang.anki.impl.model.token;

public class Filled implements Token {

    private final Token filledItem;

    public Filled(Token filledItem) {
        this.filledItem = filledItem;
    }

    public String toOriginalAnswer() {
        return filledItem.value(null);
    }

    @Override
    public String value(String delim) {
        return String.format("{{c1::%s}}", filledItem.value(null));
    }

    @Override
    public void addContent(char c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return value(null);
    }
}
