package com.samwang.alg;

import java.util.Optional;
import java.util.stream.IntStream;

public class ImmutableTree {
}

class TreeNode<T extends Comparable> {

    static final TreeNode Nil = new TreeNode(null);

    private enum LeftRight {
        LEFT,RIGHT;
    }

    final T value;
    TreeNode<T> left;
    TreeNode<T> right;
    TreeNode<T> parent;
    TreeNode(T v) {
        value = v;
    }
    TreeNode(T v, TreeNode<T> le, TreeNode<T> ri) {
        this(v);
        addLeft(le);
        addRight(ri);
    }

    void addChild(T v) {
        int comparision = value.compareTo(v);
        if (comparision == 0) return;
        else if (comparision > 0) {
            if (left == null) {
                addLeft(new TreeNode<>(v));
            } else {
                left.addChild(v);
            }
        } else { // comparison < 0
            if (right == null) {
                addRight(new TreeNode<>(v));
            } else {
                right.addChild(v);
            }
        }
    }

    private TreeNode<T> addLeft(TreeNode<T> child) {
        if (child == null) return this;

        if (right != null) throw new IllegalStateException("Left node can't rebind");
        left = child;
        left.parent = this;
        return left;
    }

    private TreeNode<T> addRight(TreeNode<T> child) {
        if (child == null) return this;

        if (right != null) throw new IllegalStateException("Right node can't rebind");
        right = child;
        right.parent = this;
        return right;
    }

    Optional<TreeNode<T>> find(T v) {
        int comparison = value.compareTo(v);
        if (comparison == 0) return Optional.of(this);
        else if (comparison > 0) {
            return left == null
                ? Optional.empty()
                : left.find(v);
        } else {
            return right == null
                ? Optional.empty()
                : right.find(v);
        }
    }

    Optional<TreeNode<T>> update(T oldValue, T newValue) {
        return find(oldValue).map(node -> update(node, null, null, newValue));
    }

    private LeftRight whichChild() {
        if (parent == null) throw new IllegalStateException("No parent, no children");

        if (parent.left == this) return LeftRight.LEFT;
        else if (parent.right == this) return LeftRight.RIGHT;
        else throw new IllegalStateException("Should not happen");
    }

    private TreeNode<T> update(TreeNode<T> node, TreeNode<T> changedChild, LeftRight leftRight, T newValue) {

        TreeNode<T> newNode = new TreeNode<>(newValue);
        if (changedChild == null) {
            newNode.addLeft(node.left);
            newNode.addRight(node.right);

        } else if (leftRight == LeftRight.LEFT) {
            newNode.addLeft(changedChild);
            newNode.addRight(node.right);

        } else if (leftRight == LeftRight.RIGHT) {
            newNode.addLeft(node.left);
            newNode.addRight(changedChild);
        }

        if (node.parent != null) {
            return update(node.parent, newNode, node.whichChild(), node.parent.value);
        } else {
            return newNode;
        }
    }

    void print() {
        print(0, "->", "Nil");
    }

    private void print(int lvl, String delim, String placeHolder) {
        System.out.println(prefix(lvl, delim) + value);

        if (left == null && right == null) {
            return;
        }

        if (left == null) {
            System.out.println(prefix(lvl+1, delim) + placeHolder);
        } else {
            left.print(lvl+1, delim, placeHolder);
        }

        if (right == null) {
            System.out.println(prefix(lvl+1, delim) + placeHolder);
        } else {
            right.print(lvl+1, delim, placeHolder);
        }
    }

    private String prefix(int lvl, String delim) {
        return IntStream.range(0, lvl+1)
            .mapToObj(_ignored -> delim)
            .reduce((acc, ele) -> acc + ele)
            .orElse("");
    }

    public static void main(String[] args) {
        TreeNode<Integer> root = new TreeNode<>(
            5,
            new TreeNode<>(3,
                new TreeNode<>(2),
                new TreeNode<>(4)),
            new TreeNode<>(7,
                new TreeNode<>(6),
                new TreeNode<>(9,
                    null,
                    new TreeNode<>(10)))
        );
        root.print();


        TreeNode<Integer> root2 = new TreeNode<>(5);
        IntStream.of(3,2,4,7,6,9,10).forEach(root2::addChild);
        root2.print();

        System.out.println("Find 9");
        root2.find(9).orElse(TreeNode.Nil).print();
        System.out.println("Find 8");
        root2.find(8).orElse(TreeNode.Nil).print();

        TreeNode<Integer> changed = root2.update(10, 11).orElse(TreeNode.Nil);
        changed.print();
    }
}

class Tuple2<T1,T2> {
    public final T1 _1;
    public final T2 _2;
    private Tuple2(T1 t1, T2 t2) { _1 = t1; _2 = t2; }

    public static <TKey, TValue> Tuple2<TKey, TValue> apply(TKey key, TValue value) {
        return new Tuple2<>(key, value);
    }
}
