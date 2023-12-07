package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.logbuddy.Message.message;
import static org.logbuddy.decorator.InvocationDecorator.invocationDecorator;
import static org.logbuddy.message.Completed.returned;
import static org.logbuddy.message.Completed.thrown;
import static org.logbuddy.message.Invoked.invoked;
import static org.logbuddy.testing.Matchers.withContent;
import static org.logbuddy.testing.TestingAnonymous.anonymousAbstractList;
import static org.logbuddy.testing.TestingAnonymous.anonymousArrayList;
import static org.logbuddy.testing.TestingAnonymous.anonymousList;
import static org.logbuddy.testing.TestingAnonymous.anonymousObject;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.expect;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.message.Invoked;
import org.quackery.Quackery;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestInvocationDecorator {
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
    given(decorated = invocationDecorator(logger)
        .decorate(new Decorable(result)));
    when(decorated.methodReturningField());
    thenReturned(result);
  }

  @Test
  public void returns_from_typed_method() {
    given(decorated = invocationDecorator(logger)
        .decorate(new Decorable()));
    when(decorated.methodReturningString(string));
    thenReturned(string);
  }

  @Test
  public void throws_from_method() throws Throwable {
    given(decorated = invocationDecorator(logger)
        .decorate(new Decorable(throwable)));
    when(() -> decorated.methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void logs_invocation() throws NoSuchMethodException {
    given(decorated = invocationDecorator(logger)
        .decorate(decorable));
    when(() -> decorated.methodWithArguments(argumentA, argumentB));
    thenCalled(logger).log(message(invoked(
        decorable,
        Decorable.class.getMethod("methodWithArguments", Object.class, Object.class),
        asList(argumentA, argumentB))));
  }

  @Test
  public void logs_nested_invocation() throws NoSuchMethodException {
    given(decorator = invocationDecorator(logger));
    given(decorated = decorator.decorate(new Decorable(decorator.decorate(new Decorable()))));
    when(() -> decorated.methodDelegating());
    thenCalledTimes(2, logger).log(any(Message.class, withContent(instanceOf(Invoked.class))));
  }

  @Test
  public void logs_null_arguments() throws NoSuchMethodException {
    given(decorated = invocationDecorator(logger)
        .decorate(decorable));
    when(() -> decorated.methodWithArguments(null, null));
    thenCalled(logger).log(message(invoked(
        decorable,
        Decorable.class.getMethod("methodWithArguments", Object.class, Object.class),
        asList(null, null))));
  }

  @Test
  public void logs_returned_object() {
    given(decorated = invocationDecorator(logger)
        .decorate(new Decorable(result)));
    when(() -> decorated.methodReturningField());
    thenCalled(logger).log(message(returned(result)));
  }

  @Test
  public void logs_returned_void() {
    given(decorated = invocationDecorator(logger)
        .decorate(new Decorable(result)));
    when(() -> decorated.methodReturningVoid());
    thenCalled(logger).log(message(returned()));
  }

  @Test
  public void logs_thrown() {
    given(decorated = invocationDecorator(logger)
        .decorate(new Decorable(throwable)));
    when(() -> decorated.methodThrowingField());
    thenCalled(logger).log(message(thrown(throwable)));
  }

  @Test
  public void decorates_object() {
    given(decorator = invocationDecorator(logger));
    when(() -> decorator.decorate(new Object()));
    thenReturned(instanceOf(Object.class));
  }

  @Test
  public void decorates_decorated_object() {
    given(decorator = invocationDecorator(logger));
    when(() -> decorator.decorate(decorator.decorate(new Decorable())));
    thenReturned(instanceOf(Decorable.class));
  }

  @Test
  public void decorates_unaccessible_anonymous_interface() {
    given(decorator = invocationDecorator(logger));
    when(() -> decorator.decorate(anonymousList()).toString());
    thenReturned();
  }

  @Test
  public void decorates_unaccessible_anonymous_abstract_class() {
    given(decorator = invocationDecorator(logger));
    when(() -> decorator.decorate(anonymousAbstractList()).toString());
    thenReturned();
  }

  @Test
  public void decorates_unaccessible_anonymous_concrete_class() {
    given(decorator = invocationDecorator(logger));
    when(() -> decorator.decorate(anonymousArrayList()).toString());
    thenReturned();
  }

  @Test
  public void decorates_unaccessible_anonymous_object() {
    given(decorator = invocationDecorator(logger));
    when(() -> decorator.decorate(anonymousObject()).toString());
    thenReturned();
  }

  /**
   * TODO test access from other package
   */
  @Test
  public void decorates_package_private_method() throws NoSuchMethodException {
    given(decorated = invocationDecorator(logger)
        .decorate(decorable));
    when(() -> decorated.packagePrivateMethod());
    thenCalled(logger).log(message(invoked(
        decorable,
        Decorable.class.getDeclaredMethod("packagePrivateMethod"),
        asList())));
  }

  /**
   * TODO test access from other package
   */
  @Test
  public void decorates_protected_method() throws NoSuchMethodException {
    given(decorated = invocationDecorator(logger)
        .decorate(decorable));
    when(() -> decorated.protectedMethod());
    thenCalled(logger).log(message(invoked(
        decorable,
        Decorable.class.getDeclaredMethod("protectedMethod"),
        asList())));
  }

  @Test
  public void implements_to_string() {
    given(decorator = invocationDecorator(logger));
    when(decorator.toString());
    thenReturned(format("invocationDecorator(%s)", logger));
  }

  @Test
  public void checks_nulls() {
    when(() -> invocationDecorator(null));
    thenThrown(LogBuddyException.class);

    when(() -> invocationDecorator(logger).decorate(null));
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

    public void methodReturningVoid() {}

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

    public void methodDelegating() {
      if (field instanceof Decorable) {
        ((Decorable) field).methodDelegating();
      }
    }

    void packagePrivateMethod() {}

    protected void protectedMethod() {}
  }

  @Quackery
  public static org.quackery.Test testInvocationDecorator() {
    final class FinalClass {}
    return expect(IllegalArgumentException.class, suite("cannot decorate")
        .add(cannotDecorate(new FinalClass()))
        .add(cannotDecorate(new Object[0]))
        .add(cannotDecorate(new int[0])));
  }

  private static org.quackery.Test cannotDecorate(Object object) {
    return newCase(object.toString(), () -> {
      Logger logger = message -> {};
      invocationDecorator(logger).decorate(object);
    });
  }
}
