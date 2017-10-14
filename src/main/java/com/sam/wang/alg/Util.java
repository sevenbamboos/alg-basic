package com.sam.wang.alg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Util {

  public static <T> boolean isInstanceOf(Class<T> klass, Object target) {
    return klass.isAssignableFrom(target.getClass());
  }

  public static void main(String[] args) {
    System.out.println(isInstanceOf(Number.class, 1));
  }

  public static boolean isLess(Comparable i1, Comparable i2) {
    return i1.compareTo(i2) < 0;
  }

  public static void exchange(Comparable[] a, int i1, int i2) {
    if (i1 == i2) return;

    Comparable tmp = a[i1];
    a[i1] = a[i2];
    a[i2] = tmp;
  }

  public static boolean isSorted(Comparable[] a) {
    for (int i = 0; i < a.length - 1; i++) {
      if (!isLess(a[i], a[i+1])) {
        return false;
      }
    }
    return true;
  }

  public static void print(Comparable[] a) {
    for (int i = 0; i < a.length; i++) {
      System.out.print(a[i]);
      System.out.print(',');
    }
  }

  public static Iterator<Comparable[]> arrayGenerator(int len) {
    return new Iterator<Comparable[]>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Comparable[] next() {
        return randomArray(len);
      }
    };
  }

  public static Comparable[] convertArray(String inputWithComma) {
    return Arrays.asList(inputWithComma.split(",")).stream()
        .map(x->Integer.parseInt(x))
        .collect(Collectors.toList())
        .toArray(new Comparable[0]);
  }

  public static Comparable[] randomArray(int len) {
    Comparable[] t = new Comparable[len];
    for (int i = 0; i < t.length; i++) {
      t[i] = i;
    }
    shuffle(t);
    return t;
  }

  public static void shuffle(Comparable[] a) {
    for (int i = 0; i < a.length; i++) {
      exchange(a, i, random(0, i));
    }
  }

  public static int random(int lo, int hi) {
    return (int) Math.floor(Math.random() * (hi - lo)) + lo;
  }
}

