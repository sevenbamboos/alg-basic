package com.samwang.anki.impl;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

public class AnkiFileWriter {

    private final File outputFolder;
    private final List<CardGroup> groups;
    private static final boolean outputByType = true;

    public AnkiFileWriter(Path root, List<CardGroup> groups) {
        this.outputFolder = new File(root.toFile(), "anki-out");
        this.groups = groups;
    }

    public void doWrite() throws IOException {
        if (outputFolder.exists()) {
            Files.walk(outputFolder.toPath()).map(Path::toFile).forEach(File::delete);
        }

        outputFolder.mkdir();
        Map<CardType, List<Card>> cardsByType = new HashMap<>();
        for (CardGroup group : groups) {
            doWriteSync(group, cardsByType);
        }

        for(Map.Entry<CardType, List<Card>> entry : cardsByType.entrySet()) {
            writeToFileByTypeSync(entry.getKey(), entry.getValue());
        }
    }

    private void doWriteSync(CardGroup group, Map<CardType, List<Card>> cardsByType) throws IOException {

        List<Card> cards = group.getCards();

        if (!cards.isEmpty()) {
            if (outputByType) {
                Map<CardType, List<Card>> cardTypeCards = cards.stream().collect(Collectors.groupingBy(Card::getType));

                for(Map.Entry<CardType, List<Card>> entry : cardTypeCards.entrySet()) {
                    cardsByType.putIfAbsent(entry.getKey(), new ArrayList<>());
                    cardsByType.computeIfPresent(entry.getKey(), (a, b) -> {
                        b.addAll(entry.getValue());
                        return b;
                    });
                }

            } else {
                writeToGroupFile(group);
            }

        } else {
            System.out.println("No change, ignore group:" + group.name);
        }
    }

    private void writeToFileByTypeSync(CardType type, List<Card> cards) throws IOException {
        File output = new File(outputFolder, type.name() + ".txt");
        writeFileSync(output, cards);
    }

    private void writeToGroupFile(CardGroup group) throws IOException {
        File output = new File(outputFolder, group.name + ".txt");
        writeFileSync(output, group.getCards());
    }

    private void writeFileSync(File file, List<Card> cards) throws IOException {
        if (cards.isEmpty()) return;

        int count = 0;
        try(FileChannel channel = FileChannel.open(file.toPath(), CREATE, WRITE)) {

            for (Card card : cards) {
                writeLineSync(channel, card.getContents("|"));
                count++;
            }

            System.out.println(String.format("%s created with %d cards", file.getName(), count));
        }
    }

    private void writeLineSync(FileChannel channel, String content) {
        ByteBuffer buffer = ByteBuffer.wrap((content + "\n").getBytes());
        while (buffer.hasRemaining()) {
            try {
                channel.write(buffer);
            } catch (IOException e) { throw new RuntimeException(e); }
        }
    }
}
