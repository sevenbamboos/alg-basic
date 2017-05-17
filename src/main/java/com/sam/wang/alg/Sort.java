package com.sam.wang.alg;

public class Sort {

  public static Comparable[] selectSort(Comparable[] a) {
    Comparable[] t = clone(a);;

    for (int i = 0; i < t.length; i++) {

      Comparable min = t[i];
      int jj = i;
      for (int j = i+1; j < t.length; j++) {
        if (t[j].compareTo(min) < 0) {
          min = t[j];
          jj = j;
        }
      }

      t[jj] = t[i];
      t[i] = min;

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
  }
}

