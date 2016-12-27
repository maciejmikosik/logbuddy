package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.LogBuddyException.check;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.logbuddy.Decorator;

public class ComposedDecorator implements Decorator {
  private final List<Decorator> decorators;

  private ComposedDecorator(List<Decorator> decorators) {
    this.decorators = decorators;
  }

  public static Decorator compose(Decorator... decorators) {
    return new ComposedDecorator(validate(decorators));
  }

  public <T> T decorate(T decorable) {
    T decorated = decorable;
    ListIterator<Decorator> iterator = decorators.listIterator(decorators.size());
    while (iterator.hasPrevious()) {
      Decorator decorator = iterator.previous();
      decorated = decorator.decorate(decorated);
    }
    return decorated;
  }

  private static List<Decorator> validate(Decorator... decorators) {
    check(decorators != null);
    List<Decorator> decoratorList = new ArrayList<>(asList(decorators));
    check(!decoratorList.contains(null));
    return decoratorList;
  }

  public String toString() {
    return decorators.stream()
        .map(Decorator::toString)
        .collect(joining(", ", "compose(", ")"));
  }
}
