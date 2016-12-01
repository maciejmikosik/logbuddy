package org.logbuddy.logger;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.logbuddy.logger.AsynchronousLogger.asynchronous;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;

public class TestAsynchronousLogger {
  private Logger logger, asynchronous;
  private List<Object> logged;
  private Object model;
  private Thread thread;
  private final AtomicInteger counter = new AtomicInteger(0);

  @Before
  public void before() {
    givenTest(this);
    given(logged = new CopyOnWriteArrayList<>());
  }

  @Test
  public void delegates_logging() {
    given(logger = model -> logged.add(model));
    given(asynchronous = asynchronous(logger));
    when(() -> asynchronous.log(model));
    thenReturned();
    sleep();
    thenEqual(logged, asList(model));
  }

  @Test
  public void returns_before_delegation() {
    given(logger = model -> {
      sleep();
      counter.incrementAndGet();
    });
    given(asynchronous = asynchronous(logger));
    when(() -> asynchronous.log(model));
    thenReturned();
    thenEqual(counter.get(), 0);
    sleep();
    sleep();
    thenEqual(counter.get(), 1);
  }

  @Test
  public void logs_using_other_thread() {
    given(logger = model -> thread = Thread.currentThread());
    given(asynchronous = asynchronous(logger));
    when(() -> asynchronous.log(model));
    thenReturned();
    sleep();
    then(thread, not(sameInstance(Thread.currentThread())));
  }

  @Test
  public void implements_to_string() {
    given(asynchronous = asynchronous(logger));
    when(asynchronous.toString());
    thenReturned(format("asynchronous(%s)", logger));
  }

  @Test
  public void checks_null() {
    when(() -> asynchronous(null));
    thenThrown(LogBuddyException.class);
  }

  private static void sleep() {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
