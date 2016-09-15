package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.logbuddy.decorator.LoggingDecorator.logging;
import static org.logbuddy.model.Invocation.invocation;
import static org.logbuddy.model.Returned.returned;
import static org.logbuddy.model.Thrown.thrown;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;

public class TestLoggingDecorator {
  private Decorator decorator;
  private Logger logger;
  private Decorable decorable, decorated;
  private Object argumentA, argumentB;
  private Object result;
  private Throwable throwable;
  private String string;

  @Before
  public void before() {
    givenTest(this);
    given(decorable = new Decorable());
    given(throwable = new Throwable());
  }

  @Test
  public void returns_from_method() {
    given(decorated = logging(logger)
        .decorate(new Decorable(result)));
    when(decorated.methodReturningField());
    thenReturned(result);
  }

  @Test
  public void returns_from_typed_method() {
    given(decorated = logging(logger)
        .decorate(new Decorable()));
    when(decorated.methodReturningString(string));
    thenReturned(string);
  }

  @Test
  public void throws_from_method() throws Throwable {
    given(decorated = logging(logger)
        .decorate(new Decorable(throwable)));
    when(() -> decorated.methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void logs_invocation() throws NoSuchMethodException {
    given(decorated = logging(logger)
        .decorate(decorable));
    when(() -> decorated.methodWithArguments(argumentA, argumentB));
    thenCalled(logger).log(invocation(
        decorable,
        Decorable.class.getMethod("methodWithArguments", Object.class, Object.class),
        asList(argumentA, argumentB)));
  }

  @Test
  public void logs_null_arguments() throws NoSuchMethodException {
    given(decorated = logging(logger)
        .decorate(decorable));
    when(() -> decorated.methodWithArguments(null, null));
    thenCalled(logger).log(invocation(
        decorable,
        Decorable.class.getMethod("methodWithArguments", Object.class, Object.class),
        asList(null, null)));
  }

  @Test
  public void logs_returned() {
    given(decorated = logging(logger)
        .decorate(new Decorable(result)));
    when(() -> decorated.methodReturningField());
    thenCalled(logger).log(returned(result));
  }

  @Test
  public void logs_thrown() {
    given(decorated = logging(logger)
        .decorate(new Decorable(throwable)));
    when(() -> decorated.methodThrowingField());
    thenCalled(logger).log(thrown(throwable));
  }

  @Test
  public void decorates_object() {
    given(decorator = logging(logger));
    when(() -> decorator.decorate(new Object()));
    thenReturned(instanceOf(Object.class));
  }

  @Test
  public void decorates_decorated_object() {
    given(decorator = logging(logger));
    when(() -> decorator.decorate(decorator.decorate(new Decorable())));
    thenReturned(instanceOf(Decorable.class));
  }

  @Test
  public void checks_nulls() {
    when(() -> logging(null));
    thenThrown(LogBuddyException.class);

    when(() -> logging(logger).decorate(null));
    thenThrown(LogBuddyException.class);
  }

  public static class Decorable {
    private final Object field;

    public Decorable() {
      this(null);
    }

    public Decorable(Object field) {
      this.field = field;
    }

    public Object methodReturningField() {
      return field;
    }

    public String methodReturningString(String string) {
      return string;
    }

    public Object methodThrowingField() throws Throwable {
      throw (Throwable) field;
    }

    public void methodWithArguments(Object argumentA, Object argumentB) {}
  }
}
