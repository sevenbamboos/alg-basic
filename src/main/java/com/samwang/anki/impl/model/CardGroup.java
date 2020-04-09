package com.samwang.anki.impl.model;

import com.samwang.anki.impl.AnkiFileLogger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardGroup {
    public final AnkiFileLogger fileLogger;
    public final Path sourceDir;
    public final String name;
    private final int count;
    private List<Card> cards;

    public CardGroup(Path src, String aName, AnkiFileLogger fileLogger) {
        sourceDir = src;
        name = aName;
        this.cards = new ArrayList<>();
        this.fileLogger = fileLogger;
        count = fileLogger.getLastCount(getFullName());
    }

    public void addCard(Card card) {
        cards.add(card.setTags(getTags()));
    }

    private String getTags() {
        return getParentName() + " " + getFullName();
    }

    public List<Card> getCards() {
        if (count == cards.size()) {
            return Collections.emptyList();
        } else {
            return cards;
        }
    }

    public String getFullName() {
        return getParentName() + "-" + name;
    }

    public String getParentName() {
        String parent = sourceDir.getName(sourceDir.getNameCount()-1) + "";
        if (parent.endsWith(".md")) {
            parent = parent.substring(0, parent.length()-3);
        }
        return parent;
    }

    public CardGroup setCount() {
        if (!cards.isEmpty()) fileLogger.setCount(getFullName(), cards.size());
        return this;
    }

    @Override
    public String toString() {
        return "Group:" + name;
    }
}
