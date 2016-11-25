package org.logbuddy.decorator;

import org.logbuddy.Decorator;

public class NoDecorator implements Decorator {
  private NoDecorator() {}

  public static Decorator noDecorator() {
    return new NoDecorator();
  }

  public <T> T decorate(T decorable) {
    return decorable;
  }
}
