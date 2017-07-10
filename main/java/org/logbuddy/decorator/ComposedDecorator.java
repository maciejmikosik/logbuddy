package org.logbuddy.decorator;

import static java.util.stream.Collectors.joining;
import static org.logbuddy.common.Varargs.varargs;

import java.util.List;
import java.util.ListIterator;

import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;

public class ComposedDecorator implements Decorator {
  private final List<? extends Decorator> decorators;

  private ComposedDecorator(List<? extends Decorator> decorators) {
    this.decorators = decorators;
  }

  public static Decorator compose(Decorator... decorators) {
    return new ComposedDecorator(varargs(decorators)
        .defensiveCopy()
        .forbidNullElements()
        .onErrorThrow(LogBuddyException::new)
        .toList());
  }

  public static Decorator compose(List<? extends Decorator> decorators) {
    return new ComposedDecorator(varargs(decorators)
        .defensiveCopy()
        .forbidNullElements()
        .onErrorThrow(LogBuddyException::new)
        .toList());
  }

  public <T> T decorate(T decorable) {
    T decorated = decorable;
    ListIterator<? extends Decorator> iterator = decorators.listIterator(decorators.size());
    while (iterator.hasPrevious()) {
      Decorator decorator = iterator.previous();
      decorated = decorator.decorate(decorated);
    }
    return decorated;
  }

  public String toString() {
    return decorators.stream()
        .map(Decorator::toString)
        .collect(joining(", ", "compose(", ")"));
  }
}
