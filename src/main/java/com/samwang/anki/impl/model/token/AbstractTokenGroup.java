package com.samwang.anki.impl.model.token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractTokenGroup implements Token {

    protected final List<Token> children;

    public AbstractTokenGroup doneToken(String contents) {
        return this;
    }

    protected AbstractTokenGroup() {
        children = new ArrayList<>();
    }

    @Override
    public String value(String delim) {
        return children.stream().map(x -> x.value(delim)).collect(Collectors.joining(delim));
    }
}
