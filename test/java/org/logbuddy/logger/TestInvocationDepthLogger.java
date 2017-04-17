package org.logbuddy.logger;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static org.logbuddy.Message.message;
import static org.logbuddy.logger.InvocationDepthLogger.invocationDepth;
import static org.logbuddy.model.InvocationDepth.invocationDepth;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledInOrder;
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

public class TestInvocationDepthLogger {
  private Logger logger, invocationDepthLogger;
  private Message message;
  private Invocation invocation;
  private Returned returned;
  private Thrown thrown;
  private List<Object> messages;

  @Before
  public void before() {
    givenTest(this);
    given(message = message("content"));
  }

  @Test
  public void initial_depth_is_zero() {
    given(invocationDepthLogger = invocationDepth(logger));
    when(() -> invocationDepthLogger.log(message));
    thenReturned();
    thenCalled(logger).log(message.attribute(invocationDepth(0)));
  }

  @Test
  public void increases_depth_during_returning_invocation() {
    given(invocationDepthLogger = invocationDepth(logger));
    when(() -> {
      invocationDepthLogger.log(message(invocation));
      invocationDepthLogger.log(message);
      invocationDepthLogger.log(message(returned));
    });
    thenCalledInOrder(logger).log(message(invocation).attribute(invocationDepth(0)));
    thenCalledInOrder(logger).log(message.attribute(invocationDepth(1)));
    thenCalledInOrder(logger).log(message(returned).attribute(invocationDepth(0)));
  }

  @Test
  public void increases_depth_during_throwing_invocation() {
    given(invocationDepthLogger = invocationDepth(logger));
    when(() -> {
      invocationDepthLogger.log(message(invocation));
      invocationDepthLogger.log(message);
      invocationDepthLogger.log(message(thrown));
    });
    thenCalledInOrder(logger).log(message(invocation).attribute(invocationDepth(0)));
    thenCalledInOrder(logger).log(message.attribute(invocationDepth(1)));
    thenCalledInOrder(logger).log(message(thrown).attribute(invocationDepth(0)));
  }

  @Test
  public void increases_depth_recursively() {
    given(invocationDepthLogger = invocationDepth(logger));
    when(() -> {
      invocationDepthLogger.log(message(invocation));
      invocationDepthLogger.log(message(invocation));
      invocationDepthLogger.log(message(invocation));
      invocationDepthLogger.log(message(invocation));
      invocationDepthLogger.log(message);
      invocationDepthLogger.log(message(returned));
      invocationDepthLogger.log(message(thrown));
      invocationDepthLogger.log(message(returned));
      invocationDepthLogger.log(message(thrown));
    });
    thenCalledInOrder(logger).log(message(invocation).attribute(invocationDepth(0)));
    thenCalledInOrder(logger).log(message(invocation).attribute(invocationDepth(1)));
    thenCalledInOrder(logger).log(message(invocation).attribute(invocationDepth(2)));
    thenCalledInOrder(logger).log(message(invocation).attribute(invocationDepth(3)));
    thenCalledInOrder(logger).log(message.attribute(invocationDepth(4)));
    thenCalledInOrder(logger).log(message(returned).attribute(invocationDepth(3)));
    thenCalledInOrder(logger).log(message(thrown).attribute(invocationDepth(2)));
    thenCalledInOrder(logger).log(message(returned).attribute(invocationDepth(1)));
    thenCalledInOrder(logger).log(message(thrown).attribute(invocationDepth(0)));
  }

  @Test
  public void threads_keep_separate_depths() {
    given(messages = synchronizedList(new ArrayList<>()));
    given(invocationDepthLogger = invocationDepth(message -> messages.add(message)));
    given(startAndJoin(new Thread(() -> invocationDepthLogger.log(message(invocation)))));
    given(startAndJoin(new Thread(() -> invocationDepthLogger.log(message(invocation)))));
    when(messages);
    thenReturned(asList(
        message(invocation).attribute(invocationDepth(0)),
        message(invocation).attribute(invocationDepth(0))));
  }

  @Test
  public void implements_to_string() {
    given(invocationDepthLogger = invocationDepth(logger));
    when(invocationDepthLogger.toString());
    thenReturned(format("invocationDepth(%s)", logger));
  }

  private static Void startAndJoin(Thread thread) {
    try {
      thread.start();
      thread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
}
