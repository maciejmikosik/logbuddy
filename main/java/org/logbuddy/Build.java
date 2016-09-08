package org.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import org.logbuddy.decorator.LoggingDecorator;
import org.logbuddy.logger.BrowserLogger;
import org.logbuddy.logger.ConsoleLogger;
import org.logbuddy.renderer.HtmlRenderer;
import org.logbuddy.renderer.TextRenderer;

public class Build {
  public List<Class<?>> loggers = asList(
      ConsoleLogger.class,
      BrowserLogger.class);
  public List<Class<?>> renderers = asList(
      TextRenderer.class,
      HtmlRenderer.class);
  public List<Class<?>> decorators = asList(
      LoggingDecorator.class);
}
