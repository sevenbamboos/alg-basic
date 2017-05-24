package com.sam.wang.alg;

import static com.sam.wang.alg.Util.*;
import java.util.*;

public class PriorityQueue {

  private Comparator comparator;
  private Comparable[] items;
  private int lastIndex;

  public PriorityQueue(Comparator comparator) {
    this.comparator = comparator;

    // TODO how to give a reasonable initial site
    items = new Comparable[100];

    lastIndex = 0;
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
    int emptySlot = makeARoom();
    items[emptySlot] = item;
    swim(emptySlot);
  }

  private void swim(int i) {
    int parentIndex = parentIndex(i);
    if (parentIndex == 0) return;
    Comparable parentItem = getItemAt(parentIndex).get();
    if (isLarger(getItemAt(i).get(), parentItem)) {
      exchange(items, i, parentIndex);
      swim(parentIndex);
    }
  }

  // TODO enlarge items' capacity
  private int makeARoom() {
    int emptySlot = 1;
    while (items[emptySlot] != null) emptySlot++;
    if (emptySlot > lastIndex) lastIndex = emptySlot;
    return emptySlot;
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
    clearItemAt(lastIndex);
    sink(1);
    return top;
  }

  private void sink(int i) {
    int left = leftChildIndex(i), right = rightChildIndex(i);
    Optional<Comparable> item = getItemAt(i), leftItem = getItemAt(left), rightItem = getItemAt(right);

    int exch = 0;
    if (!leftItem.isPresent() && !rightItem.isPresent()) {
      return;
    } else if (leftItem.isPresent() && !rightItem.isPresent()) {
      if (isLarger(leftItem.get(), item.get())) {
        exch = left;
      }
    } else if (!leftItem.isPresent() && rightItem.isPresent()) {
      if (isLarger(rightItem.get(), item.get())) {
        exch = right;
      }
    } else {

      int largerIndex = left;
      Comparable larger = leftItem.get();
      if (isLarger(rightItem.get(), leftItem.get())) {
        largerIndex = right;
        larger = right;
      }

      if (isLarger(larger, item.get())) {
        exch = largerIndex;
      }
    }

    if (exch > 0) {
      exchange(items, i, exch);
      sink(exch);
    }
  }

  private boolean isLarger(Comparable c1, Comparable c2) {
    return comparator.compare(c1, c2) > 0;
  }

  private boolean isSmaller(Comparable c1, Comparable c2) {
    return comparator.compare(c1, c2) < 0;
  }

  // TODO shrink items' capacity
  private void clearItemAt(int index) {
    items[index] = null;
    int i = index-1;
    while (i > 0 && items[i] == null) i--;
    lastIndex = i;
  }

  private Optional<Comparable> getItemAt(int index) {
    if (index > lastIndex) {
      return Optional.empty();
    }

    return Optional.ofNullable(items[index]);
  }

  private int leftChildIndex(int index) {
    return index * 2;
  }

  private int rightChildIndex(int index) {
    return leftChildIndex(index) + 1;
  }

  private int parentIndex(int index) {
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
    //testSimpleAddAndPop();
    testFailureFromSimpleTest("1,3,9,7,2,8,4,5,6,0,");
  }

  public static Comparator MAX = (c1, c2)->((Comparable)c1).compareTo(c2);
  public static Comparator MIN = (c1, c2)->((Comparable)c2).compareTo(c1);

  public static Comparable[] heapSort(Comparable[] a) {
    PriorityQueue pq = new PriorityQueue(MIN);
    pq.addAll(a);
    List<Comparable> dumpList = new ArrayList<>();
    while (!pq.isEmpty()) {
      dumpList.add(pq.pop().get());
    }

    return dumpList.toArray(new Comparable[0]);
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

    Iterator<Comparable[]> ite = arrayGenerator(10);
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
