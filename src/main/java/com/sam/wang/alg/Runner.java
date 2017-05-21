package com.sam.wang.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by samwang on 21/05/2017.
 */
public class Runner {
  private String name;
  private boolean isVerbose;
  private List<Long> records;

  public Runner(String name) {
    this(name, false);
  }

  public Runner(String name, boolean isVerbose) {
    this.name = name;
    this.isVerbose = isVerbose;
    records = new ArrayList<Long>();
  }

  public <T> Runner run(Supplier<T> executor, Function<T, Boolean> validator, Function<T, String> stringConvertor) {
    long start = System.currentTimeMillis();
    T result = executor.get();
    long last = System.currentTimeMillis() - start;

    if (isVerbose && stringConvertor != null) {
      System.out.println("Result:");
      System.out.println(stringConvertor.apply(result));
    }

    boolean passed = validator.apply(result);
    if (!passed) {
      throw new RuntimeException("Result failed to pass the validation.");
    }

    if (isVerbose) {
      System.out.println(String.format("It took %.3f", last/1000.0));
    }

    records.add(last);
    return this;
  }

  public long min() {
    return records.stream().reduce(records.get(0),
        (result, element) -> result <= element ? result : element);
  }

  public long max() {
    return records.stream().reduce(records.get(0),
        (result, element) -> result < element ? element : result);
  }

  public double mean() {
    if (records.isEmpty()) return 0;
    return sum() / records.size();
  }

  public long sum() {
    return records.stream().reduce(0L,
        (result, element) -> result + element);
  }

  public double stdev() {
    // TODO
    return 0;
  }

  public static void main(String[] args) {

  }
}
