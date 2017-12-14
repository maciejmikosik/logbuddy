package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.decorator.DecoratedNode.decorated;
import static org.logbuddy.decorator.Node.node;
import static org.logbuddy.decorator.TraversingDecorator.traversing;
import static org.testory.Testory.anyInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;

public class TestTraversingDecorator {
  private Decorator decorator, traversing;
  private Node node, otherNode, nodeA, nodeB, nodeC, nexus, traversed;

  @Before
  public void before() {
    givenTest(this);
    given(invocation -> decorated((Node) invocation.arguments.get(0)),
        decorator).decorate(anyInstanceOf(Node.class));
  }

  @Test
  public void decorates_starting_decorable() {
    given(traversing = traversing(decorator));
    when(traversing.decorate(node));
    thenReturned(decorator.decorate(node));
  }

  @Test
  public void decorates_field_of_starting_decorable() {
    given(traversing = traversing(decorator));
    given(node = node(otherNode));
    when(traversed = traversing.decorate(node));
    thenEqual(traversed.child(), decorator.decorate(otherNode));
  }

  @Test
  public void decorates_fields_recursively() {
    given(traversing = traversing(decorator));
    given(node = node(node(otherNode)));
    when(traversed = traversing.decorate(node));
    thenEqual(traversed.child().child(), decorator.decorate(otherNode));
  }

  @Test
  public void decorates_once_even_if_node_has_two_parents() {
    given(traversing = traversing(decorator));
    given(nexus = node(nodeA));
    given(nodeB = node(nexus));
    given(nodeC = node(nexus));
    given(node = node(nodeB, nodeC));
    when(traversed = traversing.decorate(node));
    thenEqual(traversed.child().child().child(), decorator.decorate(nodeA));
    thenEqual(traversed.secondChild().child().child(), decorator.decorate(nodeA));
  }

  @Test
  public void handles_circular_referencing() {
    given(traversing = traversing(decorator));
    given(node = node());
    given(otherNode = node(node));
    given(node.child = otherNode);
    when(traversed = traversing.decorate(node));
    thenReturned(decorator.decorate(node));
    thenEqual(node.child(), decorator.decorate(otherNode));
    thenEqual(node.child().child(), decorator.decorate(node));
  }

  @Test
  public void includes_fields_from_superclass() {
    class SuperDecorable {
      @SuppressWarnings("unused")
      private final Node superField = node;
    }
    class Decorable extends SuperDecorable {}
    given(traversing = traversing(decorator));
    when(traversing.decorate(new Decorable()));
    thenCalled(decorator).decorate(node);
  }

  @Test
  public void ignores_logbuddy_fields() {
    @SuppressWarnings("unused")
    class Decorable {
      Logger loggerField = mock(Logger.class);
      Decorator decoratorField = mock(Decorator.class);
      Renderer rendererField = mock(Renderer.class);
    }
    given(traversing = traversing(decorator));
    when(traversing.decorate(new Decorable()));
    thenCalledNever(decorator).decorate(anyInstanceOf(Logger.class));
    thenCalledNever(decorator).decorate(anyInstanceOf(Decorator.class));
    thenCalledNever(decorator).decorate(anyInstanceOf(Renderer.class));
  }

  @Test
  public void ignores_null_fields() {
    class Decorable {
      @SuppressWarnings("unused")
      Object field = null;
    }
    given(traversing = traversing(decorator));
    when(traversing.decorate(new Decorable()));
    thenCalledNever(decorator).decorate(null);
  }

  @Test
  public void filters_fields() {
    class Decorable {
      Object a = nodeA;
      Object b = nodeB;
      Object c = nodeC;
    }
    Decorable decorable = new Decorable();

    given(traversing = traversing(decorator)
        .filter(field -> field.getName().contains("b")));
    when(traversing.decorate(decorable));
    thenEqual(decorable.a, nodeA);
    thenEqual(decorable.b, decorator.decorate(nodeB));
    thenEqual(decorable.c, nodeC);
  }

  @Test
  public void filters_are_joined() {
    class Decorable {
      Object a = nodeA;
      Object b = nodeB;
      Object ab = nodeC;
    }
    Decorable decorable = new Decorable();

    given(traversing = traversing(decorator)
        .filter(field -> field.getName().startsWith("a"))
        .filter(field -> field.getName().endsWith("b")));
    when(traversing.decorate(decorable));
    thenEqual(decorable.a, nodeA);
    thenEqual(decorable.b, nodeB);
    thenEqual(decorable.ab, decorator.decorate(nodeC));
  }

  @Test
  public void implements_to_string() {
    given(traversing = traversing(decorator));
    when(traversing.toString());
    thenReturned(format("traversing(%s)", decorator));
  }

  @Test
  public void checks_null_decorator() {
    given(decorator = null);
    when(() -> traversing(decorator));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void checks_null_filter() {
    when(() -> traversing(decorator).filter(null));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void checks_null_decorable() {
    given(decorator = traversing(decorator));
    when(() -> decorator.decorate(null));
    thenThrown(LogBuddyException.class);
  }
}
