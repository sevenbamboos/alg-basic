package com.sam.wang.alg;

import java.util.function.Function;

public enum Sort {

  INSTANCE;

  public static Sort getInstance() {
    return INSTANCE;
  }

  public enum Strategy {
    SELECT, INSERT, SHELL, MERGE;
  }

  private static final boolean SHOW_ARRAY_BEFORE_SORT = false;
  private static final boolean SHOW_ARRAY_AFTER_SORT = false;

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
      System.out.print(',');
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
      case MERGE:
        return mergeSort(clone(t));
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

    while (pace < length/3) pace = 3*pace + 1;

    while (pace > 0) {

      for (int j = 0; j < length; j += pace) {
        for (int k = j; k >= pace; k -= pace) {
          if (isLess(t[k], t[k-pace])) {
            exchange(t, k, k-pace);
          }
        }
      }

      //System.out.println("\npace:" + pace);
      //print(t);

      pace = pace / 3;
    }

    return t;
  }
  
  private Comparable[] mergeSort(Comparable[] t) {
    doMergeSort(t, 0, t.length-1, new Comparable[t.length]);
    return t;
  }
  
  private void doMergeSort(Comparable[] t, int lo, int hi, Comparable[] buff) {
    if (lo >= hi) {
      return;
    }
    int mid = (lo + hi) / 2;
    doMergeSort(t, lo, mid, buff);
    doMergeSort(t, mid+1, hi, buff);
    merge(t, lo, mid, hi, buff);
  }
  
  private void merge(Comparable[] t, int lo, int mi, int hi, Comparable[] buff) {

    if (lo >= hi) {
      return;
    }

    //System.out.println(String.format("merge lo:%s, mi:%s, hi:%s", lo, mi, hi));

    int index1 = lo, index2 = mi+1, len = hi - lo + 1;
    for (int i = 0; i < len; i++) {

      if (index1 > mi && index2 <= hi) {
        buff[lo + i] = t[index2++];
      } else if (index1 <= mi && index2 > hi) {
        buff[lo + i] = t[index1++];
      } else if (index1 <= mi && index2 <= hi) {
        if (isLess(t[index1], t[index2])) {
          buff[lo + i] = t[index1++];
        } else {
          buff[lo + i] = t[index2++];
        }
      } else {
        // not possible
        throw new RuntimeException();
      }
    }

    for (int i = 0; i < len; i++) {
      t[i+lo] = buff[lo + i];
    }

  }

  private static Comparable[] clone(Comparable[] a) {
    Comparable[] t = new Comparable[a.length];
    System.arraycopy(a, 0, t, 0, a.length);
    return t;
  }

  private static void test(Comparable[] a, Function<Comparable[], Comparable[]> sorting) {
    long start = System.currentTimeMillis();
    Comparable[] t = sorting.apply(a);
    if (!isSorted(t)) {
      print(t);
      System.out.println("\nFailed");
      return;
    }

    if (SHOW_ARRAY_AFTER_SORT) {
      System.out.println("After sort");
      print(t);
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

    Comparable[] a = randomArray(40000);

    if (SHOW_ARRAY_BEFORE_SORT) {
      System.out.println("Before sort");
      print(a);
    }

    System.out.println("\nSelect sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.SELECT));

    System.out.println("\nInsert sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.INSERT));

    System.out.println("\nShell sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.SHELL));

    System.out.println("\nMerge sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.MERGE));
  }
}

