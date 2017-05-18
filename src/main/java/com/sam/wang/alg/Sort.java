package com.sam.wang.alg;

public class Sort {

  public static Comparable[] selectSort(Comparable[] a) {
    Comparable[] t = clone(a);

    for (int i = 0; i < t.length; i++) {

      // find the smallest item from ith
      Comparable min = t[i];
      int jj = i;
      for (int j = i+1; j < t.length; j++) {
        if (t[j].compareTo(min) < 0) {
          min = t[j];
          jj = j;
        }
      }

      // exchange the ith with the smallest
      t[jj] = t[i];
      t[i] = min;

    }

    return t;
  }

  public static Comparable[] insertSort(Comparable[] a) {
    Comparable[] t = clone(a);

    for (int i = 1; i < t.length; i++) {

      // insert the ith into the right place of 0..<i
      for (int j = i; j > 0; j--) {
        if (t[j].compareTo(t[j-1]) < 0) {
          Comparable tmp = t[j-1];
          t[j-1] = t[j];
          t[j] = tmp;
        } else {
          break;
        }
      }

    }

    return t;
  }

  private static Comparable[] clone(Comparable[] a) {
    return a.clone();
  }

  public static void main(String[] args) {
    Integer[] a = {6,7,5,4,1,9,8,3,2};
    Comparable[] t = selectSort(a);
    for (Comparable ti : t) {
      System.out.print(ti);
    }
    System.out.println();

    Comparable[] t2 = insertSort(a);
    for (Comparable ti : t2) {
      System.out.print(ti);
    }
  }
}

