package com.samwang.demo;

import com.samwang.common.Tuple;

import java.util.*;
import java.util.stream.Collectors;

import static com.samwang.common.Tuple.Tuple;
import static com.samwang.util.Utility.zip;
import static com.samwang.util.Utility.zipWithIndex;
import static com.samwang.util.Utility.tail;
import static com.samwang.util.Utility.toOption;
import static com.samwang.demo.Gender.*;

public class InterviewA {

    static String nameList(List<Person> persons) {
        return Objects.requireNonNull(persons)
            .stream()
            .map(p ->
                Tuple(
                    // format gender
                    p.Gender() == Male
                        ? "MR. "
                        : p.Gender() == Female
                            ? "MS. "
                            : "",
                    // format name
                    toOption(p.Name(), String::isEmpty)
                        .orElse("<No name>")
                )
            )
            .map(geAndNa -> geAndNa._1 + geAndNa._2)
            .reduce((acc, ele) -> acc + ",\n" + ele)
            .orElseThrow(() ->
                new IllegalArgumentException("persons"));
    }
    // MR. Wang,
    // MS. Zhu,
    // <No name>
}

enum Gender {
    Unknown, Male, Female;
    static Gender of(String s) {
        switch (s) {
            case "M":
                return Male;
            case "F":
                return Female;
            case "U":
            default:
                return Unknown;
        }
    }
}

class Person {
    private final String name;
    private final Gender gender;
    private Person(String name, Gender gender) { this.name = name; this.gender = gender; }
    public String Name() { return name; }
    public Gender Gender() { return gender; }

    public static List<Person> apply(String... nameGenderPair) {
        return zipWithIndex(zip(nameGenderPair, tail(nameGenderPair))).stream()
            .filter(itemIndex -> itemIndex._2 % 2 == 0)
            .map(itemIndex -> itemIndex._1)
            .map(nameGender -> new Person(nameGender._1, Gender.of(nameGender._2)))
            .collect(Collectors.toList());
    }
}

class MyCache<K,V> implements Map<K,V> {

    private Tuple[] elements;

    private MyCache(int length) {
        elements = new Tuple[length];
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public boolean isEmpty() {
        return elements.length == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
