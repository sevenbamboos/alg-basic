package com.samwang.anki.impl;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.Properties;

public class AnkiFileLogger implements Closeable {

    private File configFile;
    private Properties config;

    public AnkiFileLogger(String root) {
        Path path = FileSystems.getDefault().getPath(root);
        config = new Properties();

        configFile = new File(".", ".anki-imp-config.properties");
        try {
            if (!configFile.exists()) configFile.createNewFile();

            try (Reader reader = new BufferedReader(new FileReader(configFile))) {
                config.load(reader);
            }

        } catch (IOException e) {
            e.printStackTrace();
            configFile = null;
            System.err.println("Can't load config:" + e.getMessage());
        }
    }

    public int getLastCount(String name) {
        try {

            return Integer.parseInt(config.getProperty(name), 10);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setCount(String name, int count) {
        config.setProperty(name, count + "");
    }

    @Override
    public void close() {
        if (configFile == null) return;

        try(Writer writer = new BufferedWriter(new FileWriter(configFile))) {
            config.store(writer, "For Anki Importer App");

        } catch (IOException e) {
            System.err.println("Can't save config:" + e.getMessage());
        }
    }
}
