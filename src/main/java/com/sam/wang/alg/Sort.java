package com.sam.wang.alg;

import static com.sam.wang.alg.Util.*;

import com.google.common.collect.Lists;
import javafx.scene.layout.Priority;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Sort {

  public enum Strategy {
    SELECT, INSERT, SHELL, BST, HEAP, MERGE, QUICK, SYSTEM;
  }

  public static Comparable[] sort(Comparable[] a, Strategy strategy) {
    Comparable[] t = a;
    switch (strategy) {
      case SELECT:
        return selectSort(clone(t));
      case INSERT:
        return insertSort(clone(t));
      case SHELL:
        return shellSort(clone(t));
      case BST:
        // TODO slow performance
        //return bstSort(clone(t));
        return t;
      case HEAP:
        return heapSort(clone(t));
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

  private static Comparable[] bstSort(Comparable[] t) {
    return BST.sort(t);
  }

  private static Comparable[] heapSort(Comparable[] t) {
    return PriorityQueue.heapSort(t);
  }

  private static Comparable[] systemSort(Comparable[] t) {
    Arrays.sort(t, new Comparator<Comparable>() {
      @Override
      public int compare(Comparable o1, Comparable o2) {
        return o1.compareTo(o2);
      }
    });
    return t;
  }

  private static Comparable[] selectSort(Comparable[] t) {
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

  private static Comparable[] insertSort(Comparable[] t) {
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

  private static Comparable[] shellSort(Comparable[] t) {
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

  private static Comparable[] mergeSort(Comparable[] t) {
    doMergeSort(t, 0, t.length-1, new Comparable[t.length]);
    return t;
  }
  
  private static void doMergeSort(Comparable[] t, int lo, int hi, Comparable[] buff) {
    if (lo >= hi) {
      return;
    }
    int mid = (lo + hi) / 2;
    doMergeSort(t, lo, mid, buff);
    doMergeSort(t, mid+1, hi, buff);
    merge(t, lo, mid, hi, buff);
  }
  
  private static void merge(Comparable[] t, int lo, int mi, int hi, Comparable[] buff) {

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

  private static Comparable[] quickSort(Comparable[] t) {
    doQuickSort(t, 0, t.length-1);
    return t;
  }

  private static void doQuickSort(Comparable[] t, int lo, int hi) {
    if (lo >= hi) {
      return;
    }

    int anchor = makePartition(t, lo, hi, lo);
    doQuickSort(t, lo, anchor);
    doQuickSort(t, anchor+1, hi);
  }

  private static int makePartition(Comparable[] t, int lo, int hi, int sp) {
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

  private static void testSortPerformance(int testDataLen, int testRounds, Strategy... strategies) {
    List<Comparable[]> testData = generateRandomArray(testDataLen, testRounds);

    Function<Comparable[], Boolean> validator = Util::isSorted;
    Function<Comparable[], String> stringConvertor = (x)->{
      return Arrays.asList(x).stream().map(y->y.toString()).reduce("", (prev, item)->prev + "," + item);
    };

    List<Runner> allRunners = new ArrayList<>();
    for (Strategy strategy : strategies) {
      Runner runner = new Runner(strategy.name(), false);
      allRunners.add(runner);
      for (int i = 0; i < testRounds; i++) {
        runner.run(sort(strategy, testData.iterator()), validator, stringConvertor);
      }
    }

    //System.out.println(String.format("[Test data] len=%d,\trounds=%d", testDataLen, testRounds));
    printRunnerInfo(allRunners);

  }

  private static void printRunnerInfo(List<Runner> allRunners) {
    /*
    for (Runner runner : allRunners) {
      System.out.println(runner.briefInfo());
    }
    */

    /*
    for (Runner runner : allRunners) {
      System.out.print(runner.getName() + "\t");
    }
    System.out.println();
    */

    for (Runner runner : allRunners) {
      System.out.print(String.format("%.0f\t", runner.mean()));
    }
    System.out.println();

  }

  private static Supplier<Comparable[]> sort(Strategy strategy, Iterator<Comparable[]> dataIte) {
    return ()->{
      if (dataIte.hasNext()) {
        return Sort.sort(dataIte.next(), strategy);
      }
      return new Comparable[0];
    };
  }

  private static List<Comparable[]> generateRandomArray(int arrayLen, int arraySize) {
    Iterator<Comparable[]> ite = Util.arrayGenerator(arrayLen);
    List<Comparable[]> result = new ArrayList<>(arraySize);
    for (int i = 0; i < arraySize; i++) {
      result.add(ite.next());
    }
    return result;
  }

  public static void main(String[] args) {

    int i = 5000;
    while (i < 50000) {
      testSortPerformance(i, 10, Strategy.values());
      i += 5000;
    }

    // from here, data becomes too large for n^2 strategies
    /*
    int j = 50000;
    Strategy[] strategyInNlgN = {Strategy.HEAP, Strategy.MERGE, Strategy.QUICK, Strategy.SYSTEM};
    while (j < 5000000) {
      testSortPerformance(j, 10, strategyInNlgN);
      j *= 2;
    }
    */

  }
}

