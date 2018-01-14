package com.samwang.demo;

import java.util.Objects;

import static com.samwang.demo.LegacyData.Type.*;

public class LegacyData {

    private int intField;
    private float floatField;
    private String textField;

    enum Type {
        INT,FLOAT,TEXT;
    }

    public LegacyData setInt(int value) {
        return set(INT, value);
    }

    public LegacyData setFloat(float value) {
        return set(FLOAT, value);
    }

    public LegacyData setText(String value) {
        return set(TEXT, value);
    }

    private <T> LegacyData set(Type type, T value) {
        if (Objects.isNull(value)) return this;

        switch (type) {
            case INT:
                intField = (Integer) value;
                break;
            case FLOAT:
                floatField = (Float) value;
                break;
            case TEXT:
                String s = (String) value;
                if (s.length() < 5) throw new IllegalArgumentException("Too short for a text");
                textField = s;
                break;
            default:
                throw new IllegalArgumentException("Unknown type:" + type);
        }
        return this;
    }

    public LegacyData clear(Type type) {
        switch (type) {
            case INT:
                intField = 0;
                break;
            case FLOAT:
                floatField = 0f;
                break;
            case TEXT:
                textField = null;
                break;
            default:
                throw new IllegalArgumentException("Unknown type:" + type);
        }
        return this;
    }

    @Override
    public String toString() {
        return "DemoData{" +
            "intField=" + intField +
            ", floatField=" + floatField +
            ", textField='" + textField +
            '}';
    }
}
