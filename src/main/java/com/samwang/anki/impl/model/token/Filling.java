package com.samwang.anki.impl.model.token;

public class Filling implements Token {

    private Filled filled;

    public Filling setFilled(Token fillingItem) {
        filled = new Filled(fillingItem);
        return this;
    }

    public Filled getFilled() {
        return filled;
    }

    @Override
    public String value(String delim) {
        return "_";
    }

    @Override
    public String toString() {
        String value = value(null);
        if (filled != null) value += " [filled:" + filled.value(null) + "]";
        return value;
    }
}
