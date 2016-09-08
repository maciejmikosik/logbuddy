package org.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import org.logbuddy.decorator.LoggingDecorator;
import org.logbuddy.logger.BrowserLogger;
import org.logbuddy.logger.ConsoleLogger;
import org.logbuddy.logger.StackTraceLogger;
import org.logbuddy.logger.ThreadLogger;
import org.logbuddy.logger.TimeLogger;
import org.logbuddy.renderer.HtmlRenderer;
import org.logbuddy.renderer.TextRenderer;

public class Build {
  public List<Class<?>> loggers = asList(
      StackTraceLogger.class,
      ThreadLogger.class,
      TimeLogger.class,
      ConsoleLogger.class,
      BrowserLogger.class);
  public List<Class<?>> renderers = asList(
      TextRenderer.class,
      HtmlRenderer.class);
  public List<Class<?>> decorators = asList(
      LoggingDecorator.class);
}
