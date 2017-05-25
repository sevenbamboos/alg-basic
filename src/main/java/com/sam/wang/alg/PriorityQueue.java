package com.sam.wang.alg;

import static com.sam.wang.alg.Util.*;
import java.util.*;

public class PriorityQueue {

  private Comparator comparator;
  private Comparable[] items;
  private int lastIndex = 0;

  public PriorityQueue(Comparator comparator, int size) {
    this.comparator = comparator;
    items = new Comparable[size+1];
  }

  public boolean isEmpty() {
    return lastIndex == 0;
  }

  public void addAll(Comparable[] a) {
    for (Comparable i: a) {
      add(i);
    }
  }

  public void add(Comparable item) {
    if (lastIndex == items.length-1) {
      throw new RuntimeException("Not support to enlarge queue capacity yet");
    }

    items[++lastIndex] = item;
    swim(lastIndex);
  }

  private void swim(int i) {
    int parentIndex = parent(i);
    if (parentIndex == 0) return;
    if (isBetter(items[i], items[parentIndex])) {
      exchange(items, i, parentIndex);
      swim(parentIndex);
    }
  }

  public Optional<Comparable> top() {
    if (isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(items[1]);
  }

  public Optional<Comparable> pop() {
    Optional<Comparable> top = top();
    if (!top.isPresent()) {
      return top;
    }

    exchange(items, 1, lastIndex);
    items[lastIndex--] = null;
    // TODO shrink items' capacity

    sink(1);
    return top;
  }

  private void sink(int i) {
    int left = left(i), right = right(i);

    if (left > lastIndex) {
      return;
    }

    int exch = left;
    if (left < lastIndex && isBetter(items[left+1], items[left])) {
      exch = left + 1;
    }

    if (isBetter(items[exch], items[i])) {
      exchange(items, i, exch);
      sink(exch);
    }

  }

  private boolean isBetter(Comparable c1, Comparable c2) {
    return comparator.compare(c1, c2) > 0;
  }

  private int left(int index) {
    return index * 2;
  }

  private int right(int index) {
    return left(index) + 1;
  }

  private int parent(int index) {
    return index / 2;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i <= lastIndex; i++) {
      boolean isLastIndex = lastIndex == i;
      if (isLastIndex) {
        sb.append("[");
      }

      if (i == 0) {
        sb.append("S");
      } else {
        sb.append(items[i] == null ? "_" : items[i]);
      }

      if (isLastIndex) {
        sb.append("]");
      }

      sb.append("\t");
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    testSimpleAddAndPop();
    //testFailureFromSimpleTest("1,3,9,7,2,8,4,5,6,0,");
  }

  public static Comparator MAX = (c1, c2)->((Comparable)c1).compareTo(c2);
  public static Comparator MIN = (c1, c2)->((Comparable)c2).compareTo(c1);

  public static Comparable[] heapSort(Comparable[] a) {
    PriorityQueue pq = new PriorityQueue(MIN, a.length);
    pq.addAll(a);

    Comparable[] result = new Comparable[a.length];
    int i = 0;
    while (!pq.isEmpty()) {
      result[i++] = pq.pop().get();
    }

    return result;
  }

  private static void testFailureFromSimpleTest(String s) {
    System.out.println(s);
    Comparable[] r = heapSort(convertArray(s));
    if (!isSorted(r)) {
      print(r);
      throw new RuntimeException();
    }
  }

  private static void testSimpleAddAndPop() {

    Iterator<Comparable[]> ite = arrayGenerator(100);
    int round = 10, passed = 0;

    for (int i = 0; i < round; i++) {
      Comparable[] source = ite.next();
      Comparable[] result = heapSort(source);

      if (!isSorted(result)) {
        print(source);
        System.out.println("----");
        print(result);
        throw new RuntimeException(passed + " round(s) passed before failure");
      } else {
        passed++;
      }
    }
  }
}
