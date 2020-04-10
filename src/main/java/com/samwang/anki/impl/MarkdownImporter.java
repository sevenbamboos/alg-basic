package com.samwang.anki.impl;

import com.samwang.anki.impl.model.Card;
import com.samwang.anki.impl.model.CardGroup;
import com.samwang.anki.impl.model.TokenCard;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//import static com.samwang.anki.impl.model.StringListCard.parseCard;
import static com.samwang.anki.impl.model.TokenCard.parse;

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
                Card card = processLine(line);
                if (card != null) {
                    if (card.hasError()) {
                        System.out.println("Failed to parse:" + card.source());
                    } else {
                        group.addCard(card);
                    }
                }
            }
        }

        groups.forEach(CardGroup::setCount);

        return groups;
    }

    private Card processLine(String s) {
        if (s == null || s.trim().isEmpty()) return null;

        String[] tokens = s.split("\\|");

        if (tokens.length < 2) return null;

        String question = tokens[0];
        String answer = tokens[1];
        if (shouldIgnore(question, answer)) return null;

        //return parseCard(question, answer);

        TokenCard card = parse(s, question, answer);
        if (card.hasError()) {
            card.parsedException.printStackTrace();
        }
        return card;
    }

    private boolean shouldIgnore(String question, String answer) {
        question = question.trim();
        answer = answer.trim();
        if (question.equalsIgnoreCase("question") && answer.equalsIgnoreCase("answer")) return true;
        if (question.equals("---") && answer.equals("---")) return true;
        return false;
    }
}
