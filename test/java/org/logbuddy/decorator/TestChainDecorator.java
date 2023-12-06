package org.logbuddy.decorator;

import static org.logbuddy.decorator.ChainDecorator.chain;
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
public class TestChainDecorator {
  @Quackery
  public static Test testTryingDecorator() {
    return suite("test trying decorator")
        .add(suite("for zero decorators")
            .add(forZeroDecoratorsUsesNone()))
        .add(suite("for single decorator")
            .add(forSingleDecoratorUsesFirst()))
        .add(suite("for two decorators")
            .add(forTwoDecoratorsUsesOnlyFirstIfDecorated())
            .add(forTwoDecoratorsUsesSecondIfFirstNotDecoratedd()))
        .add(suite("implement toString()")
            .add(implementsToStringForZeroDecorators())
            .add(implementsToStringForOneDecorator())
            .add(implementsToStringForManyDecorators()))
        .add(validatesArguments());
  }

  private static final Object undecorated = new Object();
  private static final Object decorated = new Object();
  private static final Decorator decorator = mockDecorator(undecorated, decorated);

  private static Test forZeroDecoratorsUsesNone() {
    return newCase("uses none", () -> {
      assertEquals(
          chain().decorate(undecorated),
          undecorated);
    });
  }

  private static Test forSingleDecoratorUsesFirst() {
    return newCase("uses first", () -> {
      assertEquals(
          chain(decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test forTwoDecoratorsUsesOnlyFirstIfDecorated() {
    return newCase("uses only first if decorated", () -> {
      Decorator secondDecorator = new Decorator() {
        public <T> T decorate(T decorable) {
          throw new RuntimeException();
        }
      };
      ChainDecorator chain = chain(decorator, secondDecorator);
      assertEquals(
          chain.decorate(undecorated),
          decorated);
    });
  }

  private static Test forTwoDecoratorsUsesSecondIfFirstNotDecoratedd() {
    return newCase("uses second if first not decorated", () -> {
      ChainDecorator chain = chain(mockDecorator(), decorator);
      assertEquals(
          chain.decorate(undecorated),
          decorated);
    });
  }

  private static Test implementsToStringForZeroDecorators() {
    return newCase("for zero decorators", () -> {
      assertEquals(
          chain().toString(),
          "chain([])");
    });
  }

  private static Test implementsToStringForOneDecorator() {
    return newCase("for one decorator", () -> {
      Decorator decorator = new Decorator() {
        public <T> T decorate(T decorable) {
          return null;
        }

        public String toString() {
          return "decorator";
        }
      };
      assertEquals(
          chain(decorator).toString(),
          "chain([decorator])");
    });
  }

  private static Test implementsToStringForManyDecorators() {
    return newCase("for many decorators", () -> {
      Decorator decoratorA = new Decorator() {
        public <T> T decorate(T decorable) {
          return null;
        }

        public String toString() {
          return "decoratorA";
        }
      };
      Decorator decoratorB = new Decorator() {
        public <T> T decorate(T decorable) {
          return null;
        }

        public String toString() {
          return "decoratorB";
        }
      };
      Decorator decoratorC = new Decorator() {
        public <T> T decorate(T decorable) {
          return null;
        }

        public String toString() {
          return "decoratorC";
        }
      };
      assertEquals(
          chain(decoratorA, decoratorB, decoratorC).toString(),
          "chain([decoratorA, decoratorB, decoratorC])");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class, suite("validates arguments")
        .add(newCase("decorators cannot be null", () -> {
          chain((Decorator[]) null);
        }))
        .add(newCase("decorators cannot contain null", () -> {
          chain(decorator, null);
        }))
        .add(newCase("decorable cannot be null", () -> {
          chain(decorator).decorate(null);
        })));
  }

  private static Decorator mockDecorator(
      Object undecorated,
      Object decorated) {
    return new Decorator() {
      public <T> T decorate(T decorable) {
        return decorable == undecorated
            ? (T) decorated
            : decorable;
      }
    };
  }

  private static Decorator mockDecorator() {
    return new Decorator() {
      public <T> T decorate(T decorable) {
        return decorable;
      }
    };
  }
}
