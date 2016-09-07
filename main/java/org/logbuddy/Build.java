package org.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import org.logbuddy.decorator.LoggingDecorator;

public class Build {
  public List<Class<?>> decorators = asList(
      LoggingDecorator.class);
}
