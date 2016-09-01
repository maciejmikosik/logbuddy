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

import com.mikosik.logbuddy.formatter.DefaultFormatter;

public class TestDecoratorWithDefaultFormatterStack {
  private Logger logger;
  private Formatter formatter;
  private Throwable throwable;
  private Decorator decorator;
  private List<Object> messages;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
    given(formatter = new DefaultFormatter());
  }

  @Test
  public void formats_stack_indentation_if_returned() {
    given(decorator = new Decorator(logger, formatter));
    when(() -> decorator.decorate(new Decorable(
        decorator.decorate(new Decorable(
            decorator.decorate(new Decorable())))))
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
    given(decorator = new Decorator(logger, formatter));
    when(() -> decorator.decorate(new Decorable(
        decorator.decorate(new Decorable(
            decorator.decorate(new Decorable(throwable))))))
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
    given(decorator = new Decorator(logger, formatter));
    when(() -> decorator.decorate(new Decorable(
        decorator.decorate(new Decorable(
            decorator.decorate(new Decorable())))))
        .chainInNewThread());
    then(messages, not(hasItem(containsString("\t"))));
  }

  public static class Decorable {
    private Object field;

    public Decorable() {}

    public Decorable(Object field) {
      this.field = field;
    }

    public void chain() throws Throwable {
      if (field instanceof Decorable) {
        ((Decorable) field).chain();
      } else if (field instanceof Throwable) {
        throw (Throwable) field;
      }
    }

    public void chainInNewThread() throws Throwable {
      if (field instanceof Decorable) {
        Thread thread = new Thread() {
          public void run() {
            try {
              ((Decorable) field).chainInNewThread();
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
