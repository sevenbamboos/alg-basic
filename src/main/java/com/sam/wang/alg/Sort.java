package com.sam.wang.alg;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Sort {

  INSTANCE;

  private static final boolean SHOW_ARRAY_BEFORE_SORT = false;
  private static final boolean SHOW_ARRAY_AFTER_SORT = false;

  public static Sort getInstance() {
    return INSTANCE;
  }

  public enum Strategy {
    SELECT, INSERT, SHELL, MERGE, QUICK, SYSTEM;
  }

  private static boolean isLess(Comparable i1, Comparable i2) {
    return i1.compareTo(i2) < 0;
  }

  private static void exchange(Comparable[] a, int i1, int i2) {
    if (i1 == i2) return;

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
      case QUICK:
        return quickSort(clone(t));
      case SYSTEM:
        return systemSort(clone(t));
      default:
        throw new IllegalArgumentException("Unknown strategy:" + strategy);
    }
  }

  private Comparable[] systemSort(Comparable[] t) {
    Arrays.sort(t);
    return t;
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

  private Comparable[] quickSort(Comparable[] t) {
    doQuickSort(t, 0, t.length-1);
    return t;
  }

  private void doQuickSort(Comparable[] t, int lo, int hi) {
    if (lo >= hi) {
      return;
    }

    int anchor = makePartition(t, lo, hi, lo);
    doQuickSort(t, lo, anchor);
    doQuickSort(t, anchor+1, hi);
  }

  private int makePartition(Comparable[] t, int lo, int hi, int sp) {
    exchange(t, lo, sp);
    Comparable anchor = t[lo];

    //print(t);
    //System.out.println(String.format("part lo:%s, hi:%s", lo, hi));

    int j = lo+1, k = hi;
    while (j < k) {
      while (j <= hi && isLess(t[j], anchor)) j++;
      while (k >= lo && isLess(anchor, t[k])) k--;

      if (j < k) {
        exchange(t, j, k);
        j++;
        k--;
        continue;
      }
    }

    if (j > k) {
      exchange(t, lo, k);
      return k;
    } else {
      int anchorIndex = isLess(t[k], t[lo]) ? k : k-1;
      exchange(t, lo, anchorIndex);
      return anchorIndex;
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
      throw new RuntimeException("Sort Failed");
    }

    if (SHOW_ARRAY_AFTER_SORT) {
      System.out.println("After sort");
      print(t);
    }

    System.out.println("\nIt took " + (System.currentTimeMillis() - start) / 1000.0);
  }

  private static Comparable[] convertArray(String inputWithComma) {
    return Arrays.asList(inputWithComma.split(",")).stream()
        .map(x->Integer.parseInt(x))
        .collect(Collectors.toList())
        .toArray(new Comparable[0]);
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

    Comparable[] a =
        //convertArray("8,9,4,6,5,7,2,0,1,3");
        //convertArray("4,3,8,0,9,6,2,1,7,5");
      randomArray(80000);

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

    System.out.println("\nQuick sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.QUICK));

    System.out.println("\nSystem sort:");
    test(a, src->Sort.getInstance().sort(src, Strategy.SYSTEM));
  }
}

