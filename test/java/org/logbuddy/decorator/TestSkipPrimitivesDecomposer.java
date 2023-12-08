package org.logbuddy.decorator;

import static java.util.Collections.EMPTY_LIST;
import static org.logbuddy.decorator.MockDecomposer.mockDecomposer;
import static org.logbuddy.decorator.SkipPrimitivesDecomposer.skipPrimitives;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;

import java.util.List;

import org.junit.runner.RunWith;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestSkipPrimitivesDecomposer {
  @Quackery
  public static Test testSkipPrimitivesDecomposer() {
    return suite("test skip primitives decomposer")
        .add(suite("includes component of type")
            .add(includes(new Object()))
            .add(includes("string"))
            .add(includes(new Object[] { "string" }))
            .add(includes(new String[] { "string" })))
        .add(suite("includes component of type")
            .add(skips((byte) 123))
            .add(skips((short) 123))
            .add(skips(123))
            .add(skips((long) 123))
            .add(skips((float) 123))
            .add(skips((double) 123))
            .add(skips((char) 123))
            .add(skips(true)))
        .add(implementsToString());
  }

  private static Test includes(Object component) {
    return newCase(component.getClass().getSimpleName(), () -> {
      Object composite = new Object();
      Decomposer decomposer = mockDecomposer()
          .mock(composite, component);

      assertEquals(
          skipPrimitives(decomposer).decompose(composite),
          decomposer.decompose(composite));
    });
  }

  private static Test skips(Object composite) {
    return newCase(composite.getClass().getSimpleName(), () -> {
      Decomposer decomposer = new Decomposer() {
        public List<Object> decompose(Object composite) {
          throw new RuntimeException();
        }
      };

      assertEquals(
          skipPrimitives(decomposer).decompose(composite),
          EMPTY_LIST);
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
          skipPrimitives(decomposer).toString(),
          "skipPrimitives(decomposer)");
    });
  }
}
