package com.mikosik.logbuddy;

import com.mikosik.logbuddy.logger.NoLogger;

public class NoDecorator extends Decorator {
  public NoDecorator() {
    super(new NoLogger(), object -> "");
  }

  public <T> T decorate(T original) {
    return original;
  }
}
