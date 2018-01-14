package com.samwang.demo;

import static com.samwang.demo.LegacyData.Type.INT;

public class Demo {

    public static void main(String[] args) {

        LegacyData.Type type = INT;
        String value = "123";

        // straightforward implementation

        LegacyData data = new LegacyData();

        try {
            switch (type) {
                case INT:
                    data.setInt(Integer.parseInt(value));
                    break;
                case FLOAT:
                    data.setFloat(Float.parseFloat(value));
                    break;
                case TEXT:
                    data.setText(value);
                    break;
                default:
                    // do nothing?
            }
        } catch (Exception e) {
            LogRepo.global().log("Can't set value because:" + e.getMessage());
        }

        // DataSetter

        data = new LegacyData();

        DataSetter.of(type, value).checkAndApply(data);

        System.out.println(data);

        // NewDataSetter

        data = new LegacyData();

        NewDataSetter.of(type, value).checkAndApply(data);

        System.out.println(data);
    }
}
