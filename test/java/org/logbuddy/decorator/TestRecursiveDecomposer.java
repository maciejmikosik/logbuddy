package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static org.logbuddy.common.Fields.set;
import static org.logbuddy.decorator.MockDecomposer.mockDecomposer;
import static org.logbuddy.decorator.RecursiveDecomposer.recursive;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.report.AssumeException.assume;
import static org.quackery.run.Runners.expect;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.runner.RunWith;
import org.logbuddy.LogBuddyException;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestRecursiveDecomposer {
  @Quackery
  public static Test testRecursiveDecomposer() {
    return suite("decomposes graph with")
        .add(suite("one layer with")
            .add(decomposesOneLayerWithNoComponents())
            .add(decomposesOneLayerWithSingleComponent())
            .add(decomposesOneLayerWithManyComponents())
            .add(decomposesOneLayerWithSameComponents())
            .add(decomposesOneLayerWithEqualComponents()))
        .add(suite("topology of")
            .add(decomposesTreeTopology())
            .add(decomposesDiamondTopology())
            .add(decomposesSelfReferentialTopology())
            .add(decomposesShortCycleTopology())
            .add(decomposesLongCycleTopology()))
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static Test decomposesOneLayerWithNoComponents() {
    return newCase("no components", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a));
    });
  }

  private static Test decomposesOneLayerWithSingleComponent() {
    return newCase("single component", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b));
    });
  }

  private static Test decomposesOneLayerWithManyComponents() {
    return newCase("many components", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b, c, d);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b, c, d));
    });
  }

  private static Test decomposesOneLayerWithSameComponents() {
    return newCase("many components", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b, b);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b));
    });
  }

  private static Test decomposesOneLayerWithEqualComponents() {
    return newCase("many components", () -> {
      String x = new String("x");
      String xx = new String("x");
      assume(x != xx);
      assume(x.equals(xx));
      Decomposer decomposer = mockDecomposer()
          .mock(a, x, xx);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, x, xx));
    });
  }

  private static Test decomposesTreeTopology() {
    return newCase("tree", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b, c)
          .mock(b, d, e)
          .mock(c, f, g);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b, c, d, e, f, g));
    });
  }

  private static Test decomposesDiamondTopology() {
    return newCase("diamond", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b, c)
          .mock(b, d)
          .mock(c, d);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b, c, d));
    });
  }

  private static Test decomposesSelfReferentialTopology() {
    return newCase("self-referential", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, a);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a));
    });
  }

  private static Test decomposesShortCycleTopology() {
    return newCase("short cycle", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b)
          .mock(b, a);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b));
    });
  }

  private static Test decomposesLongCycleTopology() {
    return newCase("long cycle", () -> {
      Decomposer decomposer = mockDecomposer()
          .mock(a, b)
          .mock(b, c)
          .mock(c, d)
          .mock(d, e)
          .mock(e, a);
      List<Object> components = recursive(decomposer).decompose(a);
      assertEquals(components, asList(a, b, c, d, e));
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      Decomposer decomposer = new Decomposer() {
        public List<Object> decompose(Object composite) {
          return null;
        }

        public String toString() {
          return "decomposer";
        }
      };
      assertEquals(
          recursive(decomposer).toString(),
          "recursive(decomposer)");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class,
        suite("validates arguments")
            .add(newCase("decomposer cannot be null", () -> {
              recursive(null);
            }))
            .add(newCase("composite cannot be null", () -> {
              recursive(mockDecomposer()).decompose(null);
            })));
  }

  private static String a, b, c, d, e, f, g;
  static {
    for (char name = 'a'; name <= 'g'; name++) {
      try {
        Field field = TestRecursiveDecomposer.class.getDeclaredField("" + name);
        set(null, field, "" + name);
      } catch (NoSuchFieldException e) {
        throw new LinkageError("", e);
      }
    }
  }
}
