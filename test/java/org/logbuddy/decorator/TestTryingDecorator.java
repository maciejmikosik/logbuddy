package org.logbuddy.decorator;

import static org.logbuddy.decorator.MockDecorator.mockDecorator;
import static org.logbuddy.decorator.PrepareDecorator.decorated;
import static org.logbuddy.decorator.PrepareDecorator.decorator;
import static org.logbuddy.decorator.PrepareDecorator.undecorated;
import static org.logbuddy.decorator.TryingDecorator.trying;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.run.Runners.expect;

import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestTryingDecorator {
  @Quackery
  public static Test testTryingDecorator() {
    return suite("test trying decorator")
        .add(delegatesDecoration())
        .add(swallowsException())
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static Test delegatesDecoration() {
    return newCase("delegates decoration", () -> {
      assertEquals(
          trying(decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test swallowsException() {
    return newCase("swallows exception", () -> {
      Decorator decorator = new Decorator() {
        public <T> T decorate(T decorable) {
          throw new RuntimeException();
        }
      };
      assertEquals(
          trying(decorator).decorate(undecorated),
          undecorated);
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      Decorator decorator = mockDecorator()
          .name("decorator");
      assertEquals(
          trying(decorator).toString(),
          "trying(decorator)");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class, suite("validates arguments")
        .add(newCase("decorator cannot be null", () -> {
          trying(null);
        }))
        .add(newCase("decorable cannot be null", () -> {
          trying(decorator).decorate(null);
        })));
  }
}
