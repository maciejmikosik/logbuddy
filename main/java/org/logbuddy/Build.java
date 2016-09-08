package org.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import org.logbuddy.decorator.LoggingDecorator;
import org.logbuddy.renderer.TextRenderer;

public class Build {
  public List<Class<?>> renderers = asList(
      TextRenderer.class);
  public List<Class<?>> decorators = asList(
      LoggingDecorator.class);
}
