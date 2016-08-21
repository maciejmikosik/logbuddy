package com.mikosik.logger;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalledInOrder;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class TestLoggingStack {
  private Logger logger;
  private Throwable throwable;
  private Logging logging;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
  }

  @Test
  public void formats_stack_indentation_if_returned() {
    given(logging = new Logging(logger));
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
    given(logging = new Logging(logger));
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
  }
}
