package com.sam.wang.alg;

import java.util.function.Function;

public enum Sort {

  INSTANCE;

  public static Sort getInstance() {
    return INSTANCE;
  }

  public enum Strategy {
    SELECT, INSERT, SHELL;
  }

  private static boolean isLess(Comparable i1, Comparable i2) {
    return i1.compareTo(i2) < 0;
  }

  private static void exchange(Comparable[] a, int i1, int i2) {
    Comparable tmp = a[i1];
    a[i1] = a[i2];
    a[i2] = tmp;
  }

  private static void print(Comparable[] a) {
    for (int i = 0; i < a.length; i++) {
      System.out.print(a[i]);
    }
  }

  private static boolean isSorted(Comparable[] a) {
    for (int i = 0; i < a.length - 1; i++) {
      if (!isLess(a[i], a[i+1])) {
        return false;
      }
    }
    return true;
  }

  public Comparable[] sort(Comparable[] a, Strategy strategy) {
    Comparable[] t = a;
    switch (strategy) {
      case SELECT:
        return selectSort(clone(t));
      case INSERT:
        return insertSort(clone(t));
      case SHELL:
        return shellSort(clone(t));
      default:
        throw new IllegalArgumentException("Unknown strategy:" + strategy);
    }
  }

  private Comparable[] selectSort(Comparable[] t) {
    for (int i = 0; i < t.length; i++) {

      // find the smallest item from ith
      Comparable min = t[i];
      int jj = i;
      for (int j = i+1; j < t.length; j++) {
        if (isLess(t[j], min)) {
          min = t[j];
          jj = j;
        }
      }

      // exchange the ith with the smallest
      exchange(t, i, jj);
    }
    return t;
  }

  private Comparable[] insertSort(Comparable[] t) {
    for (int i = 1; i < t.length; i++) {
      // insert the ith into the right place of 0..<i
      for (int j = i; j > 0; j--) {
        if (isLess(t[j], t[j-1])) {
          exchange(t, j, j-1);
        }
      }
    }
    return t;
  }

  private Comparable[] shellSort(Comparable[] t) {
    int pace = 1, length = t.length;
    while (3*pace < length) pace++;

    while (pace > 0) {

      for (int i = 0; i <= length/pace + 1; i++) {
        for (int j = i + pace; j < length; j += pace) {
          for (int k = j; k >= pace; k -= pace) {
            if (isLess(t[k], t[k-pace])) {
              exchange(t, k, k-pace);
            }
          }
        }
      }

      //System.out.println("\npace:" + pace);
      //print(t);

      pace--;
    }

    return t;
  }

  private static Comparable[] clone(Comparable[] a) {
    return a.clone();
  }

  private static void test(Comparable[] a, Function<Comparable[], Comparable[]> sorting) {
    long start = System.currentTimeMillis();
    Comparable[] t = sorting.apply(a);
    if (!isSorted(t)) {
      print(t);
      System.out.println("\nFailed");
      return;
    }
    System.out.println("\nIt took " + (System.currentTimeMillis() - start) / 1000.0);
  }

  private static Comparable[] randomArray(int len) {
    Comparable[] t = new Comparable[len];
    for (int i = 0; i < t.length; i++) {
      t[i] = i;
    }
    shuffle(t);
    return t;
  }

  private static void shuffle(Comparable[] a) {
    for (int i = 0; i < a.length; i++) {
      exchange(a, i, random(0, i));
    }
  }

  private static int random(int lo, int hi) {
    return (int) Math.floor(Math.random() * (hi - lo)) + lo;
  }

  public static void main(String[] args) {

    Comparable[] a = randomArray(5000);

    System.out.println("\nSelect sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.SELECT));

    System.out.println("\nInsert sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.INSERT));

    System.out.println("\nShell sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.SHELL));
  }
}

