package com.samwang.anki.impl.model.token;

public class Filled implements Token {

    private final Token filledItem;

    public Filled(Token filledItem) {
        this.filledItem = filledItem;
    }

    @Override
    public String value(TokenContext ctx) {
        switch (ctx.type) {
            case QuestionAndAnswer:
                return filledItem.value(ctx);
            case Cloze:
                return String.format("{{c1::%s}}", filledItem.value(""));
            case BasicAnswer:
                return filledItem.value("");
            default:
                throw new IllegalArgumentException("Unknown card type:" + ctx.type);
        }
    }

    public String toOriginalAnswer() {
        return filledItem.value("");
    }

    @Override
    public String value(String delim) {
        return String.format("{{c1::%s}}", filledItem.value(""));
    }

    @Override
    public String toString() {
        return value("");
    }
}
