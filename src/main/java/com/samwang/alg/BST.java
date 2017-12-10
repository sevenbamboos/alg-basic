package com.samwang.alg;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Binary Search Tree
 */
public class BST {

  private static enum ChildName {
    LEFT, RIGHT;
  }

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

    @Override
    protected void finalize() throws Throwable {
      System.out.println("Finalize:" + key);
    }

    private void updateSize() {
      size = 1 + _size(left) + _size(right);
    }

    private void updateChild(Optional<Node> child, ChildName name) {
      switch (name) {
        case LEFT:
          this.left = child;
          return;
        case RIGHT:
          this.right = child;
          return;
        default:
          throw new IllegalArgumentException("ChildName:" + name);
      }
    }

    public String formattedString(String prefix, int prefixCount) {
      String self = toKeyValueSizeString(Optional.of(this));
      String left = toKeyValueSizeString(this.left);
      String right = toKeyValueSizeString(this.right);

      // Don't print leaf node
      if (left == "" && right == "") {
        return "";
      }

      String pre = "";
      int count = prefixCount;
      while (count-- > 0) {
        pre += prefix;
      }

      prefixCount++;
      return String.format("%s %s<-%s->%s\n", pre, left, self, right) + BST.toString(this.left, prefix, prefixCount) + BST.toString(this.right, prefix, prefixCount);
    }

    private String toKeyValueSizeString(Optional<Node> node) {
      if (node.isPresent()) {
        Node nodeUnwrapped = node.get();
        return String.format("[%s:%s]%s", nodeUnwrapped.key, nodeUnwrapped.value == null ? "" : nodeUnwrapped.value, nodeUnwrapped.size > 1 ? "(" + nodeUnwrapped.size + ")" : "");
      } else {
        return "";
      }
    }
  }

  private Optional<Node> root;

  public BST() {
    root = Optional.empty();
  }

  public static Comparable[] sort(Comparable[] s) {
    Util.shuffle(s);
    BST tree = new BST();
    for (Comparable item : s) {
      tree.put(item, null);
    }

    List<Comparable> t = new ArrayList<>();
    tree.visit((x) -> t.add(x.key));

    return t.toArray(new Comparable[0]);
  }

  private class SearchCursor {
    int index = 0, lo = 0, hi = 0;
    List<Node> buff;

    SearchCursor(int lo, int hi) {
      this.lo = lo;
      this.hi = hi;
      buff = new ArrayList<>(hi-lo);
    }

    boolean withIn() {
      return index >= lo && index <= hi;
    }

    void append(Node node) {
      buff.add(node);
    }

    void move() {
      index++;
    }
  }

  public Iterable<Node> select(int loIndex, int hiIndex) {

    if (loIndex >= hiIndex) {
      return Collections.emptyList();
    }

    int lo = Math.max(loIndex, 0);
    int hi = Math.min(hiIndex, root.get().size-1);

    SearchCursor cursor = new SearchCursor(lo, hi);
    visit((node) -> {
      if (!cursor.withIn()) {
        return;
      }
      cursor.append(node);
      cursor.move();
    });

    return cursor.buff;
  }

  public void visit(Consumer<Node> visitor) {
    _visitInOrder(root, visitor);
  }

  private void _visitInOrder(Optional<Node> node, Consumer<Node> visitor) {
    if (!node.isPresent()) return;

    Node nodeUnwrapped = node.get();
    _visitInOrder(nodeUnwrapped.left, visitor);
    visitor.accept(nodeUnwrapped);
    _visitInOrder(nodeUnwrapped.right, visitor);
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
      nodeUnwrapped.updateSize();

    } else if (isBigger(key, nodeKey)) {
      nodeUnwrapped.right = _put(nodeUnwrapped.right, key, value);
      nodeUnwrapped.updateSize();

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
    if (!root.isPresent()) {
      return Optional.empty();
    }

    Optional<Node> node = _delete(root, key, Optional.empty(), Optional.empty());
    if (!node.isPresent()) {
      return Optional.empty();
    } else {
      _size(root);
      return Optional.of(node.get().value);
    }
  }

  private Optional<Node> _delete(Optional<Node> node, Comparable key, Optional<Node> parent, Optional<ChildName> childName) {
    if (!node.isPresent()) {
      return Optional.empty();
    }

    Node nodeUnwrapped = node.get();
    Comparable nodeKey = nodeUnwrapped.key;

    // Continue from left
    if (isLess(key, nodeKey)) {
      return _delete(nodeUnwrapped.left, key, node, Optional.of(ChildName.LEFT));

    // Continue from right
    } else if (isBigger(key, nodeKey)) {
      return _delete(nodeUnwrapped.right, key, node, Optional.of(ChildName.RIGHT));

    // Found
    } else {

      Optional<Node> replacement = Optional.empty();
      if (!nodeUnwrapped.right.isPresent()) {
        replacement = nodeUnwrapped.left;

      } else {
        Node next = _deleteMin(nodeUnwrapped.right, nodeUnwrapped).get();
        next.left = nodeUnwrapped.left;
        next.right = nodeUnwrapped.right;
        replacement = Optional.of(next);
      }

      if (parent.isPresent()) {
        Node parentUnwrapped = parent.get();
        parentUnwrapped.updateChild(replacement, childName.get());
      } else {
        root = replacement;
      }

      return node;

    }
  }

  private Optional<Node> _deleteMin(Optional<Node> node, Node parent) {
    if (!node.isPresent()) {
      return Optional.empty();
    }

    Node nodeUnwrapped = node.get();

    // Find the minimum
    if (!nodeUnwrapped.left.isPresent()) {
      parent.left = nodeUnwrapped.right.isPresent() ? nodeUnwrapped.right : Optional.empty();
      return node;

    // Continue from the left
    } else {
      return _deleteMin(nodeUnwrapped.left, nodeUnwrapped);
    }
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

    // put
    for (int i = 0; i < keys.length; i++) {
      tree.put(keys[i], values[i]);
    }
    System.out.println(tree);

    // contains & get
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

    // delete
    delete(tree, 4);
    delete(tree, 5);
    delete(tree, 7);
    delete(tree, 1);

    // sort
    Comparable[] sorted = BST.sort(keys);
    System.out.println("Sorted:");
    for (Comparable comparable : sorted) {
      System.out.print(comparable + ",");
    }
    System.out.println();

    // select
    tree = new BST();
    keys = Util.randomArray(40);
    for (int i = 0; i < keys.length; i++) {
      tree.put(keys[i], null);
    }
    System.out.println("Select:");
    int length = 0;
    for (Node node : tree.select(0, tree.size()-1)) {
      System.out.print(node.key + ",");
      length++;
    }
    System.out.println();
    if (length != tree.size()) {
      System.out.println("Input:");
      for (Comparable key : keys) {
        System.out.print(key + ",");
      }
      System.out.println("");
      System.out.println(tree);
      throw new RuntimeException("BST select failed");
    }
  }

  private static void delete(BST tree, Comparable key) {
    System.out.println("Delete key:" + key);
    tree.delete(key);
    System.gc();
    System.out.println(tree);
  }
}
