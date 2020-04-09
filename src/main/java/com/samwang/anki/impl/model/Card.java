package com.samwang.anki.impl.model;

public interface Card {

    @Deprecated
    default CardType getType() {
        throw new UnsupportedOperationException();
    }

    default boolean hasError() {
        return false;
    }

    Card setTags(String tags);
    String source();
    String value();
}
