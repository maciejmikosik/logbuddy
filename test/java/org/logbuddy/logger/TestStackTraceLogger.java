package org.logbuddy.logger;

import static java.lang.String.format;
import static java.util.Collections.synchronizedList;
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
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class TestStackTraceLogger {
  private Logger logger, stackTraceLogger;
  private Object model;
  private Invocation invocation;
  private Returned returned;
  private Thrown thrown;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void initial_depth_is_zero() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> stackTraceLogger.log(model));
    thenCalled(logger).log(depth(0, model));
  }

  @Test
  public void increases_depth_during_returning_invocation() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> {
      stackTraceLogger.log(invocation);
      stackTraceLogger.log(model);
      stackTraceLogger.log(returned);
    });
    thenCalledInOrder(logger).log(depth(0, invocation));
    thenCalledInOrder(logger).log(depth(1, model));
    thenCalledInOrder(logger).log(depth(0, returned));
  }

  @Test
  public void increases_depth_during_throwing_invocation() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> {
      stackTraceLogger.log(invocation);
      stackTraceLogger.log(model);
      stackTraceLogger.log(thrown);
    });
    thenCalledInOrder(logger).log(depth(0, invocation));
    thenCalledInOrder(logger).log(depth(1, model));
    thenCalledInOrder(logger).log(depth(0, thrown));
  }

  @Test
  public void increases_depth_recursively() {
    given(stackTraceLogger = stackTrace(logger));
    when(() -> {
      stackTraceLogger.log(invocation);
      stackTraceLogger.log(invocation);
      stackTraceLogger.log(invocation);
      stackTraceLogger.log(invocation);
      stackTraceLogger.log(model);
      stackTraceLogger.log(returned);
      stackTraceLogger.log(thrown);
      stackTraceLogger.log(returned);
      stackTraceLogger.log(thrown);
    });
    thenCalledInOrder(logger).log(depth(0, invocation));
    thenCalledInOrder(logger).log(depth(1, invocation));
    thenCalledInOrder(logger).log(depth(2, invocation));
    thenCalledInOrder(logger).log(depth(3, invocation));
    thenCalledInOrder(logger).log(depth(4, model));
    thenCalledInOrder(logger).log(depth(3, returned));
    thenCalledInOrder(logger).log(depth(2, thrown));
    thenCalledInOrder(logger).log(depth(1, returned));
    thenCalledInOrder(logger).log(depth(0, thrown));
  }

  @Test
  public void threads_keep_separate_depths() {
    List<Object> models = synchronizedList(new ArrayList<>());
    given(stackTraceLogger = stackTrace(model -> models.add(0, model)));
    given(new Thread() {
      public void run() {
        stackTraceLogger.log(invocation);
      }
    }).start();
    when(() -> stackTraceLogger.log(invocation));
    thenEqual(models.get(0), depth(0, invocation));
  }

  @Test
  public void implements_to_string() {
    given(stackTraceLogger = stackTrace(logger));
    when(stackTraceLogger.toString());
    thenReturned(format("stackTrace(%s)", logger));
  }
}
