package com.sam.wang.alg;

import java.util.Optional;

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

  public void delete(Comparable key) {
    // TODO
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
