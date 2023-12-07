package org.logbuddy.decorator;

import static org.logbuddy.decorator.ConnectingLoggerDecorator.connecting;
import static org.logbuddy.decorator.MockDecorator.mockDecorator;
import static org.logbuddy.decorator.PrepareDecorator.decorated;
import static org.logbuddy.decorator.PrepareDecorator.decorator;
import static org.logbuddy.decorator.PrepareDecorator.undecorated;
import static org.logbuddy.logger.NoLogger.noLogger;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.run.Runners.expect;

import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestConnectingLoggerDecorator {
  @Quackery
  public static Test testConnectingLoggerDecorator() {
    return suite("connecting logger decorator")
        .add(suite("connects")
            .add(connectsNoLogger()))
        .add(suite("ignores")
            .add(ignoresConnectedLogger()))
        .add(suite("delegates")
            .add(delegatesObject())
            .add(delegatesPrimitive())
            .add(delegatesPrimitiveWrapper())
            .add(delegatesObjectArray())
            .add(delegatesPrimitiveArray()))
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static final Logger logger = message -> {};

  private static Test connectsNoLogger() {
    return newCase("NoLogger", () -> {
      assertEquals(
          connecting(logger, decorator).decorate(noLogger()),
          logger);
    });
  }

  private static Test ignoresConnectedLogger() {
    return newCase("connected logger", () -> {
      Logger connectedLogger = message -> {};
      assertEquals(
          connecting(logger, decorator).decorate(connectedLogger),
          connectedLogger);
    });
  }

  private static Test delegatesObject() {
    return newCase("Object", () -> {
      assertEquals(
          connecting(logger, decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test delegatesPrimitive() {
    return newCase("int", () -> {
      int undecorated = 123;
      int decorated = 124;
      Decorator decorator = mockDecorator()
          .stub(undecorated, decorated);

      assertEquals(
          connecting(logger, decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test delegatesPrimitiveWrapper() {
    return newCase("Integer", () -> {
      Integer undecorated = 123;
      Integer decorated = 124;
      Decorator decorator = mockDecorator()
          .stub(undecorated, decorated);

      assertEquals(
          connecting(logger, decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test delegatesObjectArray() {
    return newCase("Object[]", () -> {
      Object[] undecorated = new Object[] { new Object() };
      Object[] decorated = new Object[] { new Object() };
      Decorator decorator = mockDecorator()
          .stub(undecorated, decorated);

      assertEquals(
          connecting(logger, decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test delegatesPrimitiveArray() {
    return newCase("int[]", () -> {
      int[] undecorated = new int[] { 1 };
      int[] decorated = new int[] { 2 };
      Decorator decorator = mockDecorator()
          .stub(undecorated, decorated);

      assertEquals(
          connecting(logger, decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      Logger logger = new Logger() {
        public void log(Message message) {}

        public String toString() {
          return "logger";
        }
      };
      Decorator decorator = mockDecorator()
          .name("decorator");

      assertEquals(
          connecting(logger, decorator).toString(),
          "connecting(logger, decorator)");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class, suite("validates arguments")
        .add(newCase("logger cannot be null", () -> {
          connecting(null, decorator);
        }))
        .add(newCase("decorator cannot be null", () -> {
          connecting(logger, null);
        }))
        .add(newCase("decorable cannot be null", () -> {
          connecting(logger, decorator).decorate(null);
        })));
  }
}
