package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static java.util.Collections.reverse;
import static org.logbuddy.LogBuddyException.check;

import java.util.ArrayList;
import java.util.List;

import org.logbuddy.Decorator;

public class ComposedDecorator implements Decorator {
  private final List<Decorator> decorators;

  private ComposedDecorator(List<Decorator> decorators) {
    this.decorators = decorators;
  }

  public static Decorator compose(Decorator... decorators) {
    List<Decorator> decoratorList = validate(decorators);
    reverse(decoratorList);
    return new ComposedDecorator(decoratorList);
  }

  public <T> T decorate(T decorable) {
    T decorated = decorable;
    for (Decorator decorator : decorators) {
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
}
