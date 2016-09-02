package com.objectivetruth.uoitlibrarybooking.statelessutilities;

/**
 * Simple Class similar to Scala's LeftOrRight class. Right means success, left means failure
 */
public class LeftOrRight<T, M> {
    T left;
    M right;

    public LeftOrRight(T left, M right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public M getRight() {
        return right;
    }

    public void setRight(M right) {
        this.right = right;
    }

    public boolean hasRight() {
        return right != null;
    }

    public boolean hasLeft() {
        return left != null;
    }
}
