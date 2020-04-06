package com.samwang.anki.impl;

import java.util.*;
import java.util.stream.Collectors;

public class Card {
    private final CardType type;
    private List<String> fields = new ArrayList<>();

    private Card(CardType aType, String... aFields) {
        type = aType;
        fields.addAll(Arrays.asList(aFields));
    }

    public static Card newQuestionAndAnswer(String question, String answer) {
        return new Card(CardType.QuestionAndAnswer, question, answer);
    }

    public static Card newCloze(String singleField) {
        return new Card(CardType.Cloze, singleField);
    }

    public CardType getType() {
        return type;
    }

    public Card addField(String field) {
        fields.add(field);
        return this;
    }

    public String getContents(String delim) {
        return fields.stream().collect(Collectors.joining(delim));
    }

    public static Card parseCard(String question, String answer) {
        List<String> questions = parseQuestion(question);

        // question and answer
        if (questions.size() == 1) {
            return Card.newQuestionAndAnswer(question, answer);

        } else { // cloze
            String singleField = parseForCloze(questions, answer);
            return Card.newCloze(singleField);
        }
    }

    private static List<String> parseQuestion(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();

        String[] ss = s.split("_");

        return Arrays.asList(ss).stream()
            .map(item -> item.trim())
            .filter(item -> !item.isEmpty())
            .collect(Collectors.toList());
    }

    private static String parseForCloze(List<String> questions, String answer) {
        List<String> answers = parseAnswerForCloze(answer);
        List<String> result = zip(questions.iterator(), answers.iterator());
        return result.stream().collect(Collectors.joining(" "));
    }

    private static List<String> parseAnswerForCloze(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();

        String[] ss = s.split(",");

        return Arrays.asList(ss).stream()
            .map(item -> item.trim())
            .filter(item -> !item.isEmpty())
            .map(item -> String.format("{{c1::%s}}", item))
            .collect(Collectors.toList());
    }

    private static <T> List<T> zip(Iterator<T> list1, Iterator<T> list2) {
        return zip(list1, list2, new ArrayList<>());
    }

    private static <T> List<T> zip(Iterator<T> list1, Iterator<T> list2, List<T> result) {
        if (!list1.hasNext() && !list2.hasNext()) return result;

        if (list1.hasNext()) {
            result.add(list1.next());
        }

        if (list2.hasNext()) {
            result.add(list2.next());
        }

        return zip(list1, list2, result);
    }
}
