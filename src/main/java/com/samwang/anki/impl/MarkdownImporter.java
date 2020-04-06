package com.samwang.anki.impl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.samwang.anki.impl.Card.parseCard;

public class MarkdownImporter {

    public final Path path;
    public final AnkiFileLogger fileLogger;

    public MarkdownImporter(String aPath, AnkiFileLogger fileLogger) {
        path = FileSystems.getDefault().getPath(aPath);
        this.fileLogger = fileLogger;
    }

    public List<CardGroup> doImport() throws IOException {
        return process(Files.readAllLines(path).iterator());
    }

    private List<CardGroup> process(Iterator<String> lineIter) {
        List<CardGroup> groups = new ArrayList<>();
        CardGroup group = null;

        while (lineIter.hasNext()) {
            String line = lineIter.next().trim();

            if (line.isEmpty()) {
                continue;

            } else if (line.startsWith("#")) {
                group = new CardGroup(path, line.substring(1).trim(), fileLogger);
                groups.add(group);

            } else if (group != null){
                Optional<Card> card = processLine(line);
                if (card.isPresent()) {
                    group.addCard(card.get());
                }
            }
        }

        groups.forEach(x -> x.setCount());

        return groups;
    }

    private Optional<Card> processLine(String s) {
        if (s == null || s.trim().isEmpty()) return Optional.empty();

        String[] tokens = s.split("\\|");

        if (tokens.length < 2) return Optional.empty();

        String question = tokens[0];
        String answer = tokens[1];
        if (shouldIgnore(question, answer)) return Optional.empty();

        return Optional.of(parseCard(question, answer));
    }

    private boolean shouldIgnore(String question, String answer) {
        question = question.trim();
        answer = answer.trim();
        if (question.equalsIgnoreCase("question") && answer.equalsIgnoreCase("answer")) return true;
        if (question.equals("---") && answer.equals("---")) return true;
        return false;
    }
}
