package org.logbuddy.logger;

import static java.lang.String.format;
import static java.util.Collections.synchronizedList;
import static org.logbuddy.Message.message;
import static org.logbuddy.logger.StackTraceLogger.stackTrace;
import static org.logbuddy.model.Depth.depth;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledInOrder;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class TestStackTraceLogger {
  private Logger logger, stackTraceLogger;
  private Message message;
  private Invocation invocation;
  private Returned returned;
  private Thrown thrown;

  @Before
  public void before() {
    givenTest(this);
    given(message = message("content"));
  }

  @Test
  public void initial_depth_is_zero() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> stackTraceLogger.log(message));
    thenReturned();
    thenCalled(logger).log(message.attribute(depth(0)));
  }

  @Test
  public void increases_depth_during_returning_invocation() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> {
      stackTraceLogger.log(message(invocation));
      stackTraceLogger.log(message);
      stackTraceLogger.log(message(returned));
    });
    thenCalledInOrder(logger).log(message(invocation).attribute(depth(0)));
    thenCalledInOrder(logger).log(message.attribute(depth(1)));
    thenCalledInOrder(logger).log(message(returned).attribute(depth(0)));
  }

  @Test
  public void increases_depth_during_throwing_invocation() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> {
      stackTraceLogger.log(message(invocation));
      stackTraceLogger.log(message);
      stackTraceLogger.log(message(thrown));
    });
    thenCalledInOrder(logger).log(message(invocation).attribute(depth(0)));
    thenCalledInOrder(logger).log(message.attribute(depth(1)));
    thenCalledInOrder(logger).log(message(thrown).attribute(depth(0)));
  }

  @Test
  public void increases_depth_recursively() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> {
      stackTraceLogger.log(message(invocation));
      stackTraceLogger.log(message(invocation));
      stackTraceLogger.log(message(invocation));
      stackTraceLogger.log(message(invocation));
      stackTraceLogger.log(message);
      stackTraceLogger.log(message(returned));
      stackTraceLogger.log(message(thrown));
      stackTraceLogger.log(message(returned));
      stackTraceLogger.log(message(thrown));
    });
    thenCalledInOrder(logger).log(message(invocation).attribute(depth(0)));
    thenCalledInOrder(logger).log(message(invocation).attribute(depth(1)));
    thenCalledInOrder(logger).log(message(invocation).attribute(depth(2)));
    thenCalledInOrder(logger).log(message(invocation).attribute(depth(3)));
    thenCalledInOrder(logger).log(message.attribute(depth(4)));
    thenCalledInOrder(logger).log(message(returned).attribute(depth(3)));
    thenCalledInOrder(logger).log(message(thrown).attribute(depth(2)));
    thenCalledInOrder(logger).log(message(returned).attribute(depth(1)));
    thenCalledInOrder(logger).log(message(thrown).attribute(depth(0)));
  }

  @Test
  public void threads_keep_separate_depths() {
    List<Message> messages = synchronizedList(new ArrayList<>());
    given(stackTraceLogger = stackTrace(message -> messages.add(message)));
    given(new Thread(() -> stackTraceLogger.log(message(invocation)))).start();
    when(() -> stackTraceLogger.log(message(invocation)));
    thenEqual(messages.get(0), message(invocation).attribute(depth(0)));
    thenEqual(messages.get(1), message(invocation).attribute(depth(0)));
  }

  @Test
  public void implements_to_string() {
    given(stackTraceLogger = stackTrace(logger));
    when(stackTraceLogger.toString());
    thenReturned(format("stackTrace(%s)", logger));
  }
}
