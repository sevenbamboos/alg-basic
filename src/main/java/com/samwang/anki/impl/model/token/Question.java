package com.samwang.anki.impl.model.token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Question extends AbstractTokenGroup {

    private List<KeyToken> keys;
    private List<Comment> comments;

    public Question(String s) throws ParsedException {
        keys = new ArrayList<>();
        comments = new ArrayList<>();
        parseToken(s.toCharArray(), 0, null, null, null);
    }

    private void parseToken(char[] chars, int index, KeyToken key, Comment comment, PlainToken text) throws ParsedException {
        if (index >= chars.length) {
            if (key != null || comment != null) {
                throw new ParsedException("Can't parse:" + new String(chars));
            }
            if (text != null && !text.isEmpty()) children.add(text);
            return;
        }

        char c = chars[index];
        if (c == '*') {

            if (text != null) {
                if (!text.isEmpty()) children.add(text);
                text = null;
            }

            if (key == null) {
                parseToken(chars, index+1, new KeyToken(), comment, text);
            } else {
                key.doneToken();
                children.add(key);
                keys.add(key);
                parseToken(chars, index+1, null, comment, text);
            }

        } else if (c == '(') {

            if (text != null) {
                if (!text.isEmpty()) children.add(text);
                text = null;
            }

            if (comment == null) {
                parseToken(chars, index+1, key, new Comment(), text);
            } else {
                throw new ParsedException("Can't parse:" + new String(chars));
            }

        } else if (c == ')') {

            if (comment != null) {
                children.add(comment);
                comments.add(comment);
                parseToken(chars, index+1, key, null, text);
            } else {
                throw new ParsedException("Can't parse:" + new String(chars));
            }

        } else {
            if (key != null) {
                key.addContent(c);
            } else if (comment != null) {
                comment.addContent(c);
            } else if (text != null) {
                text.addContent(c);
            } else {
                text = new PlainToken(c + "");
            }
            parseToken(chars, index+1, key, comment, text);
        }
    }

    public void apply(Answer answer) throws ParsedException {
        if (answer == null || answer.children.isEmpty()) return;

        // TODO what about having more than one key
//        keys.get(0).filled(answer.children);

        int index = 0;
        for (KeyToken key : keys) {
            index = key.filled(answer.children, index);
        }
    }

    public String toClozeValue() {
        return children.stream()
            .map(x -> x instanceof KeyToken ? ((KeyToken)x).clozeValue(" ") : x.value(" "))
            .collect(Collectors.joining(" "));
    }

    public String toBasicValue(String delim) {
        String keyPart = keys.stream()
            .map(x -> x.filledValue(" "))
            .collect(Collectors.joining(", "));
        String commentPart = comments.stream()
            .map(x -> x.value(delim))
            .collect(Collectors.joining(" "));
        String sentencePart = children.stream()
            .filter(x -> !(x instanceof Comment))
            .map(x -> x instanceof KeyToken ? ((KeyToken)x).filledValue(" ") : x.value(" "))
            .collect(Collectors.joining(" "));

        return String.format("%s %s %s %s", keyPart, commentPart, delim, sentencePart);
    }
}
