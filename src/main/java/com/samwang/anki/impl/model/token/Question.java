package com.samwang.anki.impl.model.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Question extends AbstractTokenGroup {

    private List<KeyToken> keys;
    private List<Comment> comments;

    public Question(String s) throws ParsedException {
        keys = new ArrayList<>();
        comments = new ArrayList<>();
        parseToken(s.toCharArray());
    }

    private void parseToken(char[] chars) throws ParsedException {

        int index;
        StringBuilder sb = new StringBuilder();
        Stack<Token> stack = new Stack<>();

        for (index = 0; index < chars.length; index++) {
            char c = chars[index];

            if (c == '*') {

                String buff = sb.toString().trim();
                sb.delete(0, sb.length());

                if (!stack.isEmpty()) {
                    Token top = stack.pop();
                    if (top instanceof PlainToken) {
                        PlainToken text = (PlainToken) top;
                        if (!buff.isEmpty()) {
                            text.setValue(buff);
                            children.add(text);
                        }
                        stack.push(new KeyToken());

                    } else if (top instanceof KeyToken) {
                        KeyToken key = (KeyToken) top;
                        key.doneToken(buff);
                        children.add(key);
                        keys.add(key);

                    } else {
                        throw new ParsedException("Can't parse:" + new String(chars));
                    }
                } else {
                    stack.push(new KeyToken());
                }

            } else if (c == '(') {

                String buff = sb.toString().trim();
                sb.delete(0, sb.length());

                if (!stack.isEmpty()) {
                    Token top = stack.pop();
                    if (top instanceof PlainToken) {
                        PlainToken text = (PlainToken) top;
                        if (!buff.isEmpty()) {
                            text.setValue(buff);
                            children.add(text);
                        }
                        stack.push(new Comment());

                    } else {
                        throw new ParsedException("Can't parse:" + new String(chars));
                    }
                } else {
                    stack.push(new Comment());
                }

            } else if (c == ')') {

                String buff = sb.toString().trim();
                sb.delete(0, sb.length());

                if (!stack.isEmpty()) {
                    Token top = stack.pop();
                    if (top instanceof Comment) {
                        Comment comment = (Comment) top;
                        comment.setValue(buff);
                        children.add(comment);
                        comments.add(comment);

                    } else {
                        throw new ParsedException("Can't parse:" + new String(chars));
                    }
                } else {
                    throw new ParsedException("Can't parse:" + new String(chars));
                }

            } else {
                sb.append(c);
                if (stack.isEmpty()) {
                    stack.push(new PlainToken(""));
                }
            }
        }

        if (!stack.isEmpty()) {
            Token top = stack.pop();
            if (top instanceof PlainToken) {
                children.add(top);
            } else {
                throw new ParsedException("Can't parse:" + new String(chars));
            }
        }

        if (!stack.isEmpty()) {
            throw new ParsedException("Can't parse:" + new String(chars));
        }
    }

    public void apply(Answer answer) throws ParsedException {
        if (answer == null || answer.children.isEmpty()) return;

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
