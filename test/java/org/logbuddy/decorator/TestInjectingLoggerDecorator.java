package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.decorator.InjectingLoggerDecorator.injecting;
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
import org.logbuddy.Logger;

public class TestInjectingLoggerDecorator {
  private Decorable decorable;
  private Decorator injecting;
  private Logger logger;

  @Before
  public void before() {
    givenTest(this);
    given(decorable = new Decorable());
  }

  @Test
  public void injects_logger() {
    given(injecting = injecting(logger));
    when(injecting.decorate(decorable));
    thenEqual(decorable.logger, logger);
  }

  @Test
  public void ignores_other_fields() {
    given(injecting = injecting(logger));
    when(injecting.decorate(decorable));
    thenEqual(decorable.object, null);
  }

  @Test
  public void returns_original_instance() {
    given(injecting = injecting(logger));
    when(injecting.decorate(decorable));
    thenReturned(decorable);
  }

  @Test
  public void implements_to_string() {
    given(injecting = injecting(logger));
    when(injecting.toString());
    thenReturned(format("injecting(%s)", logger));
  }

  @Test
  public void checks_null_logger() {
    given(logger = null);
    when(() -> injecting(null));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void checks_null_decorable() {
    given(injecting = injecting(logger));
    when(() -> injecting.decorate(null));
    thenThrown(LogBuddyException.class);
  }

  private static class Decorable {
    Logger logger;
    Object object;
  }
}
