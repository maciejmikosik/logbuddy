package org.logbuddy.decorator;

import static java.util.Arrays.stream;
import static org.logbuddy.decorator.MockDecorator.mockDecorator;
import static org.logbuddy.decorator.PrepareDecorator.decorated;
import static org.logbuddy.decorator.PrepareDecorator.decorator;
import static org.logbuddy.decorator.PrepareDecorator.undecorated;
import static org.logbuddy.decorator.TraversingDecorator.traversing;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.run.Runners.expect;

import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;
import org.quackery.report.AssumeException;

@SuppressWarnings("deprecation")
@RunWith(QuackeryRunner.class)
public class TestTraversingDecorator {
  @Quackery
  public static Test testTraversingDecorator() {
    return suite("traversing decorator")
        .add(suite("decorates")
            .add(decoratesRootNode())
            .add(decoratesFieldsOfRootNode())
            .add(decoratesFieldsOfDeepNode()))
        .add(suite("includes field")
            .add(suite("of type")
                .add(includesObjectField())
                .add(includesPrimitiveWrapperField())
                .add(includesObjectArrayField())
                .add(includesPrimitiveArrayField()))
            .add(suite("with modifier")
                .add(includesPublicField())
                .add(includesPrivateField())
                .add(includesFinalField()))
            .add(includesFieldFromSuperclass()))
        .add(suite("excludes field")
            .add(excludesFieldAssignToNull())
            .add(excludesStaticField())
            .add(excludesReferenceToOuterClass())
            .add(suite("of type")
                .add(excludesPrimitiveField())
                .add(exlucdesLoggerField())
                .add(exlucdesDecoratorField())
                .add(exlucdesRendererField())))
        .add(suite("handles different topologies")
            .add(handlesDiamond())
            .add(handlesLongCycle())
            .add(handlesShortCycle()))
        .add(suite("implements field filtering")
            .add(filterFields())
            .add(filtersAreCombined()))
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static Test decoratesRootNode() {
    return newCase("root node", () -> {
      Object traversed = traversing(decorator).decorate(undecorated);
      assertEquals(traversed, decorated);
    });
  }

  private static Test decoratesFieldsOfRootNode() {
    return newCase("fields of root node", () -> {
      class RootNode {
        Object field;
      }
      RootNode rootNode = new RootNode();
      rootNode.field = undecorated;

      RootNode traversed = traversing(decorator).decorate(rootNode);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test decoratesFieldsOfDeepNode() {
    return newCase("fields of deep node", () -> {
      class DeepNode {
        Object field;
      }
      class RootNode {
        DeepNode deepNode;
      }
      DeepNode deepNode = new DeepNode();
      RootNode rootNode = new RootNode();
      rootNode.deepNode = deepNode;
      deepNode.field = undecorated;

      RootNode traversed = traversing(decorator).decorate(rootNode);
      assertEquals(traversed.deepNode.field, decorated);
    });
  }

  private static Test includesObjectField() {
    return newCase("Object", () -> {
      class Node {
        Object field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesPrimitiveWrapperField() {
    return newCase("Integer", () -> {
      Integer undecorated = 123;
      Integer decorated = 124;
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        Integer field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesObjectArrayField() {
    return newCase("Object[]", () -> {
      Object[] undecorated = new Object[] { new Object() };
      Object[] decorated = new Object[] { new Object() };
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        Object[] field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesPrimitiveArrayField() {
    return newCase("int[]", () -> {
      int[] undecorated = new int[] { 1 };
      int[] decorated = new int[] { 2 };
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        int[] field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesPublicField() {
    return newCase("public", () -> {
      class Node {
        public Object field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesPrivateField() {
    return newCase("private", () -> {
      class Node {
        private Object field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesFinalField() {
    return newCase("final", () -> {
      class Node {
        final Object field = undecorated;
      }
      Node node = new Node();

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test includesFieldFromSuperclass() {
    return newCase("from superclass", () -> {
      class SuperNode {
        Object field;
      }
      class Node extends SuperNode {}
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, decorated);
    });
  }

  private static Test excludesFieldAssignToNull() {
    return newCase("assigned to null", () -> {
      Decorator decorator = mockDecorator()
          .nice()
          .stub(null, decorated);

      class Node {
        Object field;
      }
      Node node = new Node();
      node.field = null;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, null);
    });
  }

  static class StaticNode {
    static Object field;
  }

  private static Test excludesStaticField() {
    return newCase("static", () -> {
      StaticNode node = new StaticNode();
      StaticNode.field = undecorated;

      traversing(decorator).decorate(node);
      assertEquals(StaticNode.field, undecorated);
    });
  }

  private static Test excludesReferenceToOuterClass() {
    return newCase("reference to outer class", () -> {
      class Outer {
        Object field;

        Inner newInner() {
          return new Inner();
        }

        class Inner {}
      }
      stream(Outer.Inner.class.getDeclaredFields())
          .filter(field -> field.getType() == Outer.class)
          .findFirst().orElseThrow(AssumeException::new);

      Outer outer = new Outer();
      Outer.Inner inner = outer.newInner();
      outer.field = undecorated;

      traversing(decorator).decorate(inner);
      assertEquals(outer.field, undecorated);
    });
  }

  private static Test excludesPrimitiveField() {
    return newCase("primitive", () -> {
      int undecorated = 123;
      int decorated = 124;
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        int field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, undecorated);
    });
  }

  private static Test exlucdesLoggerField() {
    return newCase("Logger", () -> {
      class DummyLogger implements Logger {
        public void log(Message message) {}
      }
      Logger undecorated = new DummyLogger();
      Logger decorated = new DummyLogger();
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        Logger field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, undecorated);
    });
  }

  private static Test exlucdesDecoratorField() {
    return newCase("Decorator", () -> {
      Decorator undecorated = mockDecorator();
      Decorator decorated = mockDecorator();
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        Decorator field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, undecorated);
    });
  }

  private static Test exlucdesRendererField() {
    return newCase("Renderer", () -> {
      class DummyRenderer implements Renderer<String> {
        public String render(Object model) {
          return "";
        }
      }
      DummyRenderer undecorated = new DummyRenderer();
      DummyRenderer decorated = new DummyRenderer();
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Node {
        Renderer field;
      }
      Node node = new Node();
      node.field = undecorated;

      Node traversed = traversing(decorator).decorate(node);
      assertEquals(traversed.field, undecorated);
    });
  }

  private static Test handlesDiamond() {
    return newCase("diamond", () -> {
      class LeafNode {
        Object field;
      }
      class Node {
        LeafNode leafNode;
      }
      class RootNode {
        Node nodeA;
        Node nodeB;
      }
      LeafNode leafNode = new LeafNode();
      Node nodeA = new Node();
      Node nodeB = new Node();
      RootNode rootNode = new RootNode();
      rootNode.nodeA = nodeA;
      rootNode.nodeB = nodeB;
      nodeA.leafNode = leafNode;
      nodeB.leafNode = leafNode;
      leafNode.field = undecorated;

      RootNode traversed = traversing(decorator).decorate(rootNode);
      assertEquals(traversed.nodeA.leafNode.field, decorated);
      assertEquals(traversed.nodeB.leafNode.field, decorated);
    });
  }

  private static Test handlesLongCycle() {
    return newCase("long cycle", () -> {
      class Node {
        Node field;
      }
      Node rootNode = new Node();
      Node node = new Node();
      rootNode.field = node;
      node.field = rootNode;

      Node traversed = traversing(decorator).decorate(rootNode);
      assertEquals(traversed, rootNode);
      assertEquals(traversed.field, node);
      assertEquals(traversed.field.field, rootNode);
    });
  }

  private static Test handlesShortCycle() {
    return newCase("short cycle", () -> {
      class Node {
        Node field;
      }
      Node rootNode = new Node();
      rootNode.field = rootNode;

      Node traversed = traversing(decorator).decorate(rootNode);
      assertEquals(traversed, rootNode);
      assertEquals(traversed.field, rootNode);
    });
  }

  private static Test filterFields() {
    return newCase("filter fields", () -> {
      class Node {
        Object fieldA;
        Object fieldB;
        Object fieldC;
      }
      Node node = new Node();
      node.fieldA = undecorated;
      node.fieldB = undecorated;
      node.fieldC = undecorated;

      Node traversed = traversing(decorator)
          .filter(field -> field.getName().equals("fieldB"))
          .decorate(node);
      assertEquals(traversed.fieldA, undecorated);
      assertEquals(traversed.fieldB, decorated);
      assertEquals(traversed.fieldC, undecorated);
    });
  }

  private static Test filtersAreCombined() {
    return newCase("filters are combined", () -> {
      class Node {
        Object a;
        Object b;
        Object ab;
      }
      Node node = new Node();
      node.a = undecorated;
      node.b = undecorated;
      node.ab = undecorated;

      Node traversed = traversing(decorator)
          .filter(field -> field.getName().startsWith("a"))
          .filter(field -> field.getName().endsWith("b"))
          .decorate(node);
      assertEquals(traversed.a, undecorated);
      assertEquals(traversed.b, undecorated);
      assertEquals(traversed.ab, decorated);
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      Decorator decorator = mockDecorator()
          .name("decorator");
      assertEquals(
          traversing(decorator).toString(),
          "traversing(decorator)");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class, suite("validates arguments")
        .add(newCase("decorator cannot be null", () -> {
          traversing(null);
        }))
        .add(newCase("filter cannot be null", () -> {
          traversing(decorator).filter(null);
        }))
        .add(newCase("decorable cannot be null", () -> {
          traversing(decorator).decorate(null);
        })));
  }
}
