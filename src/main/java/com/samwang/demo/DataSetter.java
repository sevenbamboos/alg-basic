package com.samwang.demo;

import com.samwang.demo.LegacyData.Type;

import static com.samwang.demo.LegacyData.Type.*;

abstract class DataSetter {

    private static final LogRepo logger = LogRepo.global();

    public static DataSetter of(Type type, String value) {
        switch (type) {
            case INT:
                return new IntSetter(value);
            case FLOAT:
                return new FloatSetter(value);
            case TEXT:
                return new TextSetter(value);
            default:
                throw new IllegalArgumentException("Unknown type:" + type);
        }
    }

    protected final Type type;
    protected final String value;

    protected DataSetter(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public void checkAndApply(LegacyData data) {
        if (check()) {
            doSet(data);
        } else {
            logger.log("Invalid value " + value);
            doClear(data);
        }
    }

    boolean check() {
        return true;
    }

    protected void doSet(LegacyData data) {
        data.setText(value);
    }

    protected void doClear(LegacyData data) {
        data.clear(type);
    }
}

class TextSetter extends DataSetter {

    protected TextSetter(String value) {
        super(TEXT, value);
    }

    boolean check() {
        return value.length() > 0;
    }
}

abstract class ParseAndSetter<T> extends DataSetter {

    protected T parsedValue;

    protected ParseAndSetter(Type type, String value) {
        super(type, value);
    }

    boolean check() {
        try {
            parsedValue = parseMethod();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract T parseMethod();
}

class IntSetter extends ParseAndSetter<Integer> {

    protected IntSetter(String value) {
        super(INT, value);
    }

    @Override
    protected Integer parseMethod() {
        return Integer.parseInt(value);
    }

    @Override
    protected void doSet(LegacyData data) {
        data.setInt(parsedValue);
    }
}

class FloatSetter extends ParseAndSetter<Float> {

    protected FloatSetter(String value) {
        super(INT, value);
    }

    @Override
    protected Float parseMethod() {
        return Float.parseFloat(value);
    }

    @Override
    protected void doSet(LegacyData data) {
        data.setFloat(parsedValue);
    }
}
