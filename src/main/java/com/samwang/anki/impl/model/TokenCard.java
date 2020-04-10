package com.samwang.anki.impl.model;

import com.samwang.anki.impl.model.token.Answer;
import com.samwang.anki.impl.model.token.ParsedException;
import com.samwang.anki.impl.model.token.Question;
import com.samwang.anki.impl.model.token.TokenContext;

public class TokenCard implements Card {

    public final String source;
    public Exception parsedException;
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

    public TokenCard setQuestion(Question question) throws ParsedException {
        this.question = question;
        apply();
        return this;
    }

    public TokenCard setAnswer(Answer answer) throws ParsedException {
        this.answer = answer;
        apply();
        return this;
    }

    private void apply() throws ParsedException {
        if (answer != null && question != null) question.apply(answer);
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
        return String.format("%s | %s", question.value(new TokenContext(CardType.Cloze)), tags);
    }

    public String toBasicValue() {
        return String.format("%s | %s", question.value(new TokenContext("|", CardType.QuestionAndAnswer, false)), tags);
    }

    public static TokenCard parse(String line, String questionSource, String answerSource) {
        TokenCard card = new TokenCard(line);

        try {
            card.setQuestion(new Question(questionSource))
                .setAnswer(new Answer(answerSource));

        } catch (Exception e) {
            card.parsedException = e;
        }

        return card;
    }

    public static void main(String[] args) {
        String question = "John *_* an amazing tie *on* (try on) *this _* (time).";
        String answer = "has, morning";
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
