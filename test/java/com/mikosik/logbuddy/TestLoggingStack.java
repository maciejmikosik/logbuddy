package com.mikosik.logbuddy;

import static java.util.Arrays.asList;
import static java.util.Collections.synchronizedList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenCalledInOrder;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestLoggingStack {
  private Logger logger;
  private Formatter formatter;
  private Throwable throwable;
  private Logging logging;
  private List<Object> messages;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
    given(formatter = object -> "format(" + object + ")");
  }

  @Test
  public void formats_stack_indentation_if_returned() {
    given(logging = new Logging(logger, formatter));
    when(() -> logging.wrap(new Wrappable(
        logging.wrap(new Wrappable(
            logging.wrap(new Wrappable())))))
        .chain());
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("chain"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\t", "chain"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\t\t", "chain"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\t\treturned"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\treturned"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("returned"))));
  }

  @Test
  public void formats_stack_indentation_if_thrown() {
    given(logging = new Logging(logger, formatter));
    when(() -> logging.wrap(new Wrappable(
        logging.wrap(new Wrappable(
            logging.wrap(new Wrappable(throwable))))))
        .chain());
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("chain"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\t", "chain"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\t\t", "chain"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\t\tthrown"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("\tthrown"))));
    thenCalledInOrder(logger).log(any(String.class, stringContainsInOrder(asList("thrown"))));
  }

  @Test
  public void does_not_join_stack_from_different_threads() {
    given(messages = synchronizedList(new ArrayList<>()));
    given(logger = message -> messages.add(message));
    given(logging = new Logging(logger, formatter));
    when(() -> logging.wrap(new Wrappable(
        logging.wrap(new Wrappable(
            logging.wrap(new Wrappable())))))
        .chainInNewThread());
    then(messages, not(hasItem(containsString("\t"))));
  }

  public static class Wrappable {
    private Object field;

    public Wrappable() {}

    public Wrappable(Object field) {
      this.field = field;
    }

    public void chain() throws Throwable {
      if (field instanceof Wrappable) {
        ((Wrappable) field).chain();
      } else if (field instanceof Throwable) {
        throw (Throwable) field;
      }
    }

    public void chainInNewThread() throws Throwable {
      if (field instanceof Wrappable) {
        Thread thread = new Thread() {
          public void run() {
            try {
              ((Wrappable) field).chainInNewThread();
            } catch (Throwable e) {
              e.printStackTrace();
            }
          }
        };
        thread.start();
        thread.join();
      } else if (field instanceof Throwable) {
        throw (Throwable) field;
      }
    }
  }
}
