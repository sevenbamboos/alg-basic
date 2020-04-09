package com.samwang.anki.impl.model.token;

public class Answer extends AbstractTokenGroup {
    public Answer(String s) {
        for (String ss : s.split(",")) {
            children.add(new PlainToken(ss.trim()));
        }
    }
}
