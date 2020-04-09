package com.samwang.anki.impl.model;

import java.util.*;
import java.util.stream.Collectors;

public class StringListCard implements Card {
    private final CardType type;
    private List<String> fields = new ArrayList<>();

    private StringListCard(CardType aType, String... aFields) {
        type = aType;
        fields.addAll(Arrays.asList(aFields));
    }

    public static StringListCard newQuestionAndAnswer(String question, String answer) {
        return new StringListCard(CardType.QuestionAndAnswer, question, answer);
    }

    public static StringListCard newCloze(String singleField) {
        return new StringListCard(CardType.Cloze, singleField);
    }

    @Override
    public CardType getType() {
        return type;
    }

    @Override
    public Card setTags(String tags) {
        fields.add(tags);
        return this;
    }

    public String getContents(String delim) {
        return fields.stream().collect(Collectors.joining(delim));
    }

    public static StringListCard parseCard(String question, String answer) {
        List<String> questions = parseQuestion(question);

        // question and answer
        if (questions.size() == 1) {
            return StringListCard.newQuestionAndAnswer(question, answer);

        } else { // cloze
            String singleField = parseForCloze(questions, answer);
            return StringListCard.newCloze(singleField);
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

    @Override
    public String source() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String value() {
        return getContents("|");
    }
}
