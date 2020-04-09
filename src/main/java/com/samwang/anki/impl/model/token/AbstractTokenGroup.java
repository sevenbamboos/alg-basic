package com.samwang.anki.impl.model.token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractTokenGroup implements Token {

    protected final List<Token> children;
    protected String contents;

    @Override
    public void addContent(char c) {
        contents += c;
    }

    public AbstractTokenGroup doneToken() {
        return this;
    }

    protected AbstractTokenGroup() {
        children = new ArrayList<>();
        contents = "";
    }

    @Override
    public String value(String delim) {
        return children.stream().map(x -> x.value(delim)).collect(Collectors.joining(delim));
    }
}
