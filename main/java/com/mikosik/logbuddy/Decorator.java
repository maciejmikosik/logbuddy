package com.mikosik.logbuddy;

public interface Decorator {
  <T> T decorate(T original);
}
