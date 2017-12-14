package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Objects.hash;

import java.util.Objects;

public class DecoratedNode extends Node {
  private final Node decorable;

  private DecoratedNode(Node decorable) {
    super(null, null);
    this.decorable = decorable;
  }

  public static Node decorated(Node decorable) {
    return new DecoratedNode(decorable);
  }

  public Node child() {
    return decorable.child();
  }

  public Node secondChild() {
    return decorable.secondChild();
  }

  public boolean equals(Object object) {
    return object instanceof DecoratedNode && equals((DecoratedNode) object);
  }

  private boolean equals(DecoratedNode node) {
    return Objects.equals(decorable, node.decorable);
  }

  public int hashCode() {
    return hash(decorable.hashCode());
  }

  public String toString() {
    return format("decorated(%s)", decorable);
  }
}
