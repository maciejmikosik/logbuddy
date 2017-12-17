package org.logbuddy.decorator;

import static java.lang.String.format;

public class Node {
  public Node child;
  public Node secondChild;

  protected Node(Node child, Node secondChild) {
    this.child = child;
    this.secondChild = secondChild;
  }

  public Node child() {
    return child;
  }

  public Node secondChild() {
    return secondChild;
  }

  public static Node node() {
    return new Node(null, null);
  }

  public static Node node(Node child) {
    return new Node(child, null);
  }

  public static Node node(Node child, Node secondChild) {
    return new Node(child, secondChild);
  }

  private boolean isBeingPrinted;

  public String toString() {
    if (isBeingPrinted) {
      return "CYCLE";
    } else {
      isBeingPrinted = true;
      try {
        return toStringRecursively();
      } finally {
        isBeingPrinted = false;
      }
    }
  }

  private String toStringRecursively() {
    return secondChild != null
        ? format("node(%s, %s)", child, secondChild)
        : child != null
            ? format("node(%s)", child)
            : "node()";
  }
}
