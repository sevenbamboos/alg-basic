package com.samwang.anki;

import com.samwang.anki.impl.AnkiFileLogger;
import com.samwang.anki.impl.AnkiFileWriter;
import com.samwang.anki.impl.model.CardGroup;
import com.samwang.anki.impl.MarkdownImporter;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class AnkiImporterApp implements Closeable {

    private AnkiFileLogger fileLogger;
    private MarkdownImporter importer;

    private AnkiImporterApp(String input) {
        fileLogger = new AnkiFileLogger(input);
        importer = new MarkdownImporter(input, fileLogger);
    }

    private void doImport() {
        List<CardGroup> groups;
        try {
            groups = importer.doImport();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AnkiFileWriter writer = new AnkiFileWriter(importer.path.toAbsolutePath().getParent(), groups);
        try {
            writer.doWrite();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Please use: java -cp alg-basic.jar com.samwang.anki.AnkiImporterApp input");
        }

        String input = args[0];
        System.out.println("Welcome to AnkiImporter to import:" + input);

        try (AnkiImporterApp app = new AnkiImporterApp(input)) {
            app.doImport();
        }

        System.out.println("Bye");
        System.exit(0);
    }


    @Override
    public void close() {
        if (fileLogger != null) fileLogger.close();
    }
}
