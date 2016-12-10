package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.decorator.TestTraversingDecorator.DecoratedNode.decorated;
import static org.logbuddy.decorator.TestTraversingDecorator.Node.node;
import static org.logbuddy.decorator.TraversingDecorator.traversing;
import static org.logbuddy.testing.Matchers.anyInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;

public class TestTraversingDecorator {
  private Decorator decorator, traversing;
  private Node node, otherNode, nodeA, nodeB, nodeC, nexus, traversed;
  private Predicate<Field> allFields, filter;

  @Before
  public void before() {
    givenTest(this);
    given(allFields = field -> true);
  }

  @Test
  public void decorates_starting_decorable() {
    given(traversing = traversing(allFields, decorator));
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
    when(traversing.decorate(node));
    thenReturned(decorated(node));
  }

  @Test
  public void decorates_field_of_starting_decorable() {
    given(traversing = traversing(allFields, decorator));
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
    given(node = node(otherNode));
    when(traversed = traversing.decorate(node));
    thenEqual(traversed.child(), decorated(otherNode));
  }

  @Test
  public void decorates_fields_recursively() {
    given(traversing = traversing(allFields, decorator));
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
    given(node = node(node(otherNode)));
    when(traversed = traversing.decorate(node));
    thenEqual(traversed.child().child(), decorated(otherNode));
  }

  @Test
  public void decorates_once_even_if_node_has_two_parents() {
    given(traversing = traversing(allFields, decorator));
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
    given(nexus = node(nodeA));
    given(nodeB = node(nexus));
    given(nodeC = node(nexus));
    given(node = node(nodeB, nodeC));
    when(traversed = traversing.decorate(node));
    thenEqual(traversed.child().child().child(), decorated(nodeA));
    thenEqual(traversed.secondChild().child().child(), decorated(nodeA));
  }

  @Test
  public void handles_circular_referencing() {
    given(traversing = traversing(allFields, decorator));
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
    given(node = node());
    given(otherNode = node(node));
    given(node.child = otherNode);
    when(traversed = traversing.decorate(node));
    thenReturned(decorated(node));
    thenEqual(node.child(), decorated(otherNode));
    thenEqual(node.child().child(), decorated(node));
  }

  @Test
  public void filters_fields() {
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
    given(node = node(nodeA, nodeB));
    given(traversing = traversing(field -> field.getName().equals("child"), decorator));
    when(traversing.decorate(node));
    thenEqual(node.child(), decorated(nodeA));
    thenEqual(node.secondChild(), nodeB);
  }

  @Test
  public void ignores_logbuddy_fields() {
    @SuppressWarnings("unused")
    class Decorable {
      Logger loggerField = mock(Logger.class);
      Decorator decoratorField = mock(Decorator.class);
      Renderer rendererField = mock(Renderer.class);
    }
    given(traversing = traversing(allFields, decorator));
    when(traversing.decorate(new Decorable()));
    thenCalledNever(decorator).decorate(anyInstanceOf(Logger.class));
    thenCalledNever(decorator).decorate(anyInstanceOf(Decorator.class));
    thenCalledNever(decorator).decorate(anyInstanceOf(Renderer.class));
  }

  @Test
  public void implements_to_string() {
    given(traversing = traversing(filter, decorator));
    when(traversing.toString());
    thenReturned(format("traversing(%s, %s)", filter, decorator));
  }

  @Test
  public void checks_null_decorator() {
    given(decorator = null);
    when(() -> traversing(allFields, decorator));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void checks_null_filter() {
    when(() -> traversing(null, decorator));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void checks_null_decorable() {
    given(decorator = traversing(allFields, decorator));
    when(() -> decorator.decorate(null));
    thenThrown(LogBuddyException.class);
  }

  public static class Node {
    private Node child;
    private final Node secondChild;

    private Node(Node child, Node secondChild) {
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

    ThreadLocal<Boolean> antiLoop;

    public String toString() {
      if (antiLoop == null) {
        antiLoop = ThreadLocal.withInitial(() -> false);
      }
      if (!antiLoop.get()) {
        antiLoop.set(true);
        try {
          return secondChild != null
              ? format("node(%s, %s)", child, secondChild)
              : child != null
                  ? format("node(%s)", child)
                  : "node()";
        } finally {
          antiLoop.set(false);
        }
      }
      return "LOOP";
    }
  }

  public static class DecoratedNode extends Node {
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
      return decorable.secondChild;
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
}
