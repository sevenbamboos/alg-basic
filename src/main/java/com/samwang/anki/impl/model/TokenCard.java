package com.samwang.anki.impl.model;

import com.samwang.anki.impl.model.token.Answer;
import com.samwang.anki.impl.model.token.Question;

public class TokenCard implements Card {

    public final String source;
    private Exception parsedException;
    private Question question;
    private Answer answer;
    private String tags;

    public TokenCard(String source) {
        this.source = source;
    }

    @Override
    public boolean hasError() {
        return parsedException != null;
    }

    @Override
    public Card setTags(String tags) {
        this.tags = tags;
        return this;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public String value() {
        return source;
    }

    public String toClozeValue() {
        return String.format("%s | %s", question.toClozeValue(), tags);
    }

    public String toBasicValue() {
        return String.format("%s | %s", question.toBasicValue("|"), tags);
    }

    public static TokenCard parse(String line, String questionSource, String answerSource) {
        TokenCard card = new TokenCard(line);

        try {
            card.question = new Question(questionSource);
            card.answer = new Answer(answerSource);
            card.question.apply(card.answer);

        } catch (Exception e) {
            e.printStackTrace();
            card.parsedException = e;
        }

        return card;
    }

    public static void main(String[] args) {
        String question = "token1 *key1 _ key2 _* (com1) token2 *keya _*";
        String answer = "ans1, ans2, ansa";
        String line = question + " | " + answer;
        try {
            TokenCard card = parse(line, question, answer);
            card.setTags("tag1 tag2");
            System.out.println(card.toClozeValue());
            System.out.println(card.toBasicValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
