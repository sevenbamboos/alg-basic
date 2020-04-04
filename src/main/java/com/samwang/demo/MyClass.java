package com.samwang.demo;

import java.util.Locale;

public class MyClass {
    public static void main(String[] args) {
//        System.out.println("abc");
//        System.out.println(String.format("123 = %d", 123));

        int result = toRomon("IV", "V");
        System.out.println(result); // 9

        result = toRomon("LX", "X");
        System.out.println(result); // 70

        result = toRomon("XL", "X");
        System.out.println(result); // 50

//        result = toRomon("XL", "X");
//        System.out.println(result); // 50
    }



    private static int toRomon(String s1, String s2) {
        return parse(s1) + parse(s2);
    }

    private static int parse(String s) {
        char[] cs = s.toUpperCase(Locale.US).toCharArray();
        return _parse(cs, 0, 0, 0);
    }

    // TODO less than 20
    private static int _parse(char[] cs, int result, int index, int previosValue) {

        if (index > cs.length - 1) return result;

        char c = cs[index];
        int currentValue = 0;

        if (c == 'L') {
            currentValue = 50;

        }else if (c == 'X') {
            currentValue = 10;
        }else if (c == 'V')  {
            currentValue = 5;
        } else /* for I */ {
            currentValue = 1;
        }

        result += currentValue;
        if (currentValue > previosValue) {
            result -= 2 * previosValue;
        }

        return _parse(cs, result, index+1, currentValue);
    }
}
