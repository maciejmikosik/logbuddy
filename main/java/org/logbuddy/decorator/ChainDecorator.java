package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Varargs.varargs;

import java.util.List;

import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;

public class ChainDecorator implements Decorator {
  private final List<Decorator> decorators;

  private ChainDecorator(List<Decorator> decorators) {
    this.decorators = decorators;
  }

  public static ChainDecorator chain(Decorator... decorators) {
    check(decorators != null);
    return new ChainDecorator(varargs(decorators)
        .forbidNullElements()
        .onErrorThrow(LogBuddyException::new)
        .toList());
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    for (Decorator decorator : decorators) {
      T decorated = decorator.decorate(decorable);
      if (decorated != decorable) {
        return decorated;
      }
    }
    return decorable;
  }

  public String toString() {
    return format("chain(%s)", decorators);
  }
}
