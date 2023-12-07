package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import org.logbuddy.Decorator;

public class TryingDecorator implements Decorator {
  private final Decorator decorator;

  private TryingDecorator(Decorator decorator) {
    this.decorator = decorator;
  }

  public static TryingDecorator trying(Decorator decorator) {
    check(decorator != null);
    return new TryingDecorator(decorator);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    try {
      return decorator.decorate(decorable);
    } catch (RuntimeException e) {
      return decorable;
    }
  }

  public String toString() {
    return format("trying(%s)", decorator);
  }
}
