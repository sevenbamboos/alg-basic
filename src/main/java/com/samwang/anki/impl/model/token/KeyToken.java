package com.samwang.anki.impl.model.token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KeyToken extends AbstractTokenGroup {

    @Override
    public KeyToken doneToken(String contents) {
        String[] tokens = (" " + contents + " ").split("_");
        List<Token> tempTokens = new ArrayList<>();
        if (tokens.length == 0) {
            // do nothing
        } else if (tokens.length == 1) {
            tempTokens.add(new PlainToken(contents));

        } else {
            for (int i = 0; i < tokens.length; i++) {
                tempTokens.add(new PlainToken(tokens[i]));
                if (i < tokens.length-1) {
                    tempTokens.add(new Filling());
                }
            }
        }
        children.addAll(tempTokens.stream()
            .filter(x -> !(x instanceof PlainToken) || !((PlainToken)x).isEmpty())
            .collect(Collectors.toList()));

        return this;
    }

    public int filled(List<Token> fillings, int index) throws ParsedException {
        if (index >= fillings.size()) {
            return index;
        }

        fillings = fillings.subList(index, fillings.size());

        List<Filling> toBeFilleds = children.stream()
            .filter(x -> x instanceof Filling)
            .map(x -> (Filling) x)
            .collect(Collectors.toList());

        int i;
        for (i = 0; i < toBeFilleds.size(); i++) {
            if (i >= fillings.size()) {
                throw new ParsedException("toBeFilled is more than fillings:" + (i+1) + ">" + fillings.size());
            }
            toBeFilleds.get(i).setFilled(fillings.get(i));
        }

        return i + index;
    }

    @Override
    public String value(TokenContext ctx) {
        return children.stream().map(x -> x.value(ctx)).collect(Collectors.joining(ctx.delim));
    }

    @Deprecated
    public String clozeValue(String delim) {
        return children.stream()
            .map(x -> x instanceof Filling ? ((Filling)x).getFilled() : x)
            .map(x -> x.value(delim))
            .collect(Collectors.joining(delim));
    }

    @Deprecated
    public String filledValue(String delim) {
        return children.stream()
            .map(x -> x instanceof Filling ? ((Filling)x).getFilled().toOriginalAnswer() : x.value(delim))
            .collect(Collectors.joining(delim));
    }

    @Override
    public String toString() {
        String s = children.stream().map(Object::toString).collect(Collectors.joining(";"));
        return "*" + s + "*";
    }
}
