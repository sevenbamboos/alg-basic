package com.sam.wang.alg;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Binary Search Tree
 */
public class BST {

  public class Node {
    private Comparable key;
    private Object value;
    private int size;
    private Optional<Node> left;
    private Optional<Node> right;

    Node(Comparable key, Object value) {
      this.key = key;
      this.value = value;
      this.size = 1;
      left = Optional.empty();
      right = Optional.empty();
    }

    @Override
    public String toString() {
      return formattedString("", 0);
    }

    public String formattedString(String prefix, int prefixCount) {
      String self = toKeyValueString(Optional.of(this));
      String left = toKeyValueString(this.left);
      String right = toKeyValueString(this.right);

      String pre = "";
      int count = prefixCount;
      while (count-- > 0) {
        pre += prefix;
      }

      prefixCount++;
      return String.format("%s %s<-%s->%s\n", pre, left, self, right) + BST.toString(this.left, prefix, prefixCount) + BST.toString(this.right, prefix, prefixCount);
    }

    private String toKeyValueString(Optional<Node> node) {
      return String.format("[%s:%s]", node.isPresent() ? node.get().key : "", node.isPresent() ? node.get().value : "");
    }
  }

  private Optional<Node> root;

  public BST() {
    root = Optional.empty();
  }

  public void put(Comparable key, Object value) {
    root = _put(root, key, value);
  }

  private Optional<Node> _put(Optional<Node> node, Comparable key, Object value) {
    if (!node.isPresent()) {
      return Optional.of(new Node(key, value));
    }

    Node nodeUnwrapped = node.get();
    Comparable nodeKey = nodeUnwrapped.key;
    if (isLess(key, nodeKey)) {
      nodeUnwrapped.left = _put(nodeUnwrapped.left, key, value);
      nodeUnwrapped.size = 1 + _size(nodeUnwrapped.left) + _size(nodeUnwrapped.right);

    } else if (isBigger(key, nodeKey)) {
      nodeUnwrapped.right = _put(nodeUnwrapped.right, key, value);
      nodeUnwrapped.size = 1 + _size(nodeUnwrapped.right) + _size(nodeUnwrapped.left);

    } else {
      nodeUnwrapped.value = value;
    }

    return node;
  }

  public Optional get(Comparable key) {
    return _get(root, key);
  }

  private Optional _get(Optional<Node> node, Comparable key) {
    if (!node.isPresent()) {
      return Optional.empty();
    }

    Node nodeUnwrapped = node.get();
    Comparable nodeKey = nodeUnwrapped.key;
    if (isLess(key, nodeKey)) {
      return _get(nodeUnwrapped.left, key);

    } else if (isBigger(key, nodeKey)) {
      return _get(nodeUnwrapped.right, key);

    } else {
      return Optional.of(node.get().value);
    }
  }

  public boolean contains(Comparable key) {
    return _get(root, key).isPresent();
  }

  public Optional delete(Comparable key) {
    Optional<Node> node = _delete(root, key);
    return node.isPresent() ? Optional.of(node.get().value) : Optional.empty();
  }

  private Optional<Node> _delete(Optional<Node> node, Comparable key) {
    if (!node.isPresent()) {
      return Optional.empty();
    }

    Node nodeUnwrapped = node.get();
    Comparable nodeKey = nodeUnwrapped.key;
    if (isLess(key, nodeKey)) {
      nodeUnwrapped.left = _delete(nodeUnwrapped.left, key);
      nodeUnwrapped.size = 1 + _size(nodeUnwrapped.left) + _size(nodeUnwrapped.right);
      return node;

    } else if (isBigger(key, nodeKey)) {
      nodeUnwrapped.right = _delete(nodeUnwrapped.right, key);
      nodeUnwrapped.size = 1 + _size(nodeUnwrapped.right) + _size(nodeUnwrapped.left);
      return node;

    } else {

      if (!nodeUnwrapped.right.isPresent()) {
        return nodeUnwrapped.left;

      } else {
        Node next = _deleteMin(nodeUnwrapped);
        next.left = nodeUnwrapped.left;
        next.right = nodeUnwrapped.right;
        next.size = 1 + _size(next.right) + _size(next.left);
        return Optional.of(next);
      }

    }
  }

  private Node _deleteMin(Node node) {
    /*
    if (node.left.isPresent()) {
      return _deleteMin(node.left.get());
    }
    node.left
    */

    // TODO
    return null;
  }

  public int size() {
    return _size(root);
  }

  private int _size(Optional<Node> node) {
    if (!node.isPresent()) {
      return 0;
    } else {
      int size = 1 + _size(node.get().left) + _size(node.get().right);
      node.get().size = size;
      return size;
    }
  }

  @Override
  public String toString() {
    return toString(root, ">", 0);
  }

  private static String toString(Optional<Node> node, String prefix, int prefixCount) {
    return node.isPresent() ? node.get().formattedString(prefix, prefixCount) : "";
  }

  private static boolean isLess(Comparable c1, Comparable c2) {
    return c1.compareTo(c2) < 0;
  }

  private static boolean equals(Comparable c1, Comparable c2) {
    return c1.compareTo(c2) == 0;
  }

  private static boolean isBigger(Comparable c1, Comparable c2) {
    return c1.compareTo(c2) > 0;
  }

  public static void main(String[] args) {
    testSimpleCase();

    /*
    testPerformance(10);
    testPerformance(100);
    testPerformance(1000);
    testPerformance(2000);
    testPerformance(4000);
    testPerformance(10000);
    */
  }

  private static void testPerformance(int length) {

    Iterator<Comparable[]> ite = Util.arrayGenerator(length);
    Runner runner = new Runner("BST put&get " + length, false);
    Function<BST, String> stringConvertor = (x)->{
      return x.toString();
    };

    for (int i = 0; i < 10; i++) {
      Comparable[] keys = ite.next();
      Function<BST, Boolean> validator = validateWithGet(keys);
      runner.run(buildBST(keys), validator, stringConvertor);
    }
    printRunnerInfo(runner);
  }

  private static Supplier<BST> buildBST(Comparable[] keys) {
    return ()->{
      BST tree = new BST();
      for (Comparable key : keys) {
        tree.put(key, key);
      }
      return tree;
    };
  }

  private static Function<BST, Boolean> validateWithGet(Comparable[] keys) {
    return (BST tree) -> {
      Util.shuffle(keys);

      for (int i = 0; i < keys.length; i++) {
        if (!tree.contains(keys[i])) {
          System.err.println("BST contains failed for " + keys[i]);
          return false;
        }

        Optional value = tree.get(keys[i]);
        if (!value.get().equals(keys[i])) {
          System.err.println("BST get failed for " + keys[i]);
          return false;
        }
      }

      return true;
    };
  }

  private static void printRunnerInfo(Runner runner) {
    System.out.println(runner.briefInfo());

    /*
    for (Runner runner : allRunners) {
      System.out.print(String.format("%.0f\t", runner.mean()));
    }
    System.out.println();
    */

  }

  private static void testSimpleCase() {
    BST tree = new BST();
    Comparable[] keys = new Integer[] {4, 2, 7, 1, 3, 9, 6, 8, 5};
    Object[] values = new Integer[] {4, 2, 7, 1, 3, 9, 6, 8, 5};
    for (int i = 0; i < keys.length; i++) {
      tree.put(keys[i], values[i]);
    }

    System.out.println(tree);

    Util.shuffle(keys);
    for (int i = 0; i < keys.length; i++) {
      if (!tree.contains(keys[i])) {
        throw new RuntimeException("BST contains failed for " + keys[i]);
      }

      Optional value = tree.get(keys[i]);
      if (!value.get().equals(keys[i])) {
        throw new RuntimeException("BST get failed for " + keys[i]);
      }
    }

  }
}
