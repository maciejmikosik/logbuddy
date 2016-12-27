package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.decorator.InjectingDecoratorDecorator.injecting;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;

public class TestInjectingDecoratorDecorator {
  private Decorable decorable;
  private Decorator injecting, injected;

  @Before
  public void before() {
    givenTest(this);
    given(decorable = new Decorable());
  }

  @Test
  public void injects_decorator() {
    given(injecting = injecting(injected));
    when(injecting.decorate(decorable));
    thenEqual(decorable.decorator, injected);
  }

  @Test
  public void ignores_other_fields() {
    given(injecting = injecting(injected));
    when(injecting.decorate(decorable));
    thenEqual(decorable.object, null);
  }

  @Test
  public void returns_original_instance() {
    given(injecting = injecting(injected));
    when(injecting.decorate(decorable));
    thenReturned(decorable);
  }

  @Test
  public void includes_fields_from_superclass() {
    class SubDecorable extends Decorable {}
    given(decorable = new SubDecorable());
    given(injecting = injecting(injected));
    when(injecting.decorate(decorable));
    thenEqual(decorable.decorator, injected);
  }

  @Test
  public void implements_to_string() {
    given(injecting = injecting(injected));
    when(injecting.toString());
    thenReturned(format("injecting(%s)", injected));
  }

  @Test
  public void checks_null_logger() {
    given(injected = null);
    when(() -> injecting(null));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void checks_null_decorable() {
    given(injecting = injecting(injected));
    when(() -> injecting.decorate(null));
    thenThrown(LogBuddyException.class);
  }

  private static class Decorable {
    Decorator decorator;
    Object object;
  }
}
