package com.samwang.anki.impl;

import com.samwang.anki.impl.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

public class AnkiFileWriter {

    private final File outputFolder;
    private final List<CardGroup> groups;
    private static final boolean outputByType = true;
    private static final DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD-HH-mm-ss");

    public AnkiFileWriter(Path root, List<CardGroup> groups) {
        this.outputFolder = new File(root.toFile(), "anki-out");
        this.groups = groups;
    }

    public void doWrite() throws IOException {
        // clear output folder
//        if (outputFolder.exists()) {
//            Files.walk(outputFolder.toPath()).map(Path::toFile).forEach(File::delete);
//        }

        outputFolder.mkdir();

        //doWriteSyncForStringListCards();
        doWriteSyncForTokenCards();
    }

    private void doWriteSyncForTokenCards() throws IOException {

        List<Card> cards = groups.stream()
            .flatMap(g -> {
                List<Card> cs = g.getCards();
                if (cs.isEmpty()) {
                    System.out.println("No change, ignore group:" + g.name);
                    return Stream.empty();
                } else {
                    return cs.stream();
                }

            })
            .collect(Collectors.toList());

        writeClozeSync(cards);
        writeBasicSync(cards);
    }

    private void doWriteSyncForStringListCards() throws IOException {
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

    private void writeClozeSync(List<Card> cards) throws IOException {
        CardType type = CardType.Cloze;
        File output = new File(outputFolder, generateFileName(type.name()));
        Function<Card, String> fromCardToValue = (card) -> ((TokenCard) card).toClozeValue();
        writeFileSync(output, cards, fromCardToValue);
    }

    private void writeBasicSync(List<Card> cards) throws IOException {
        CardType type = CardType.QuestionAndAnswer;
        File output = new File(outputFolder, generateFileName(type.name()));
        Function<Card, String> fromCardToValue = (card) -> ((TokenCard) card).toBasicValue();
        writeFileSync(output, cards, fromCardToValue);
    }

    private void writeToFileByTypeSync(CardType type, List<Card> cards) throws IOException {
        File output = new File(outputFolder, generateFileName(type.name()));
        writeFileSync(output, cards, Card::value);
    }

    private void writeToGroupFile(CardGroup group) throws IOException {
        File output = new File(outputFolder, generateFileName(group.name));
        writeFileSync(output, group.getCards(), Card::value);
    }

    private String generateFileName(String name) {
        return String.format("%s-%s.%s", name, dateFormat.format(new Date()), "txt");
    }

    private void writeFileSync(File file, List<Card> cards, Function<Card, String> fromCardToValue) throws IOException {
        if (cards.isEmpty()) return;

        int count = 0;
        try(FileChannel channel = FileChannel.open(file.toPath(), CREATE, WRITE)) {

            for (Card card : cards) {
                writeLineSync(channel, fromCardToValue.apply(card));
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
