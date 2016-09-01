package com.mikosik.logbuddy.decorator;

import com.mikosik.logbuddy.Decorator;

public class NoDecorator implements Decorator {
  private NoDecorator() {}

  public static Decorator noDecorator() {
    return new NoDecorator();
  }

  public <T> T decorate(T original) {
    return original;
  }
}
