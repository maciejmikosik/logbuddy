package org.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import org.logbuddy.decorator.ComposedDecorator;
import org.logbuddy.decorator.LoggingDecorator;
import org.logbuddy.decorator.NoDecorator;
import org.logbuddy.logger.AsynchronousLogger;
import org.logbuddy.logger.CatchingLogger;
import org.logbuddy.logger.ComposedLogger;
import org.logbuddy.logger.Fuse;
import org.logbuddy.logger.HtmlWritingLogger;
import org.logbuddy.logger.NoLogger;
import org.logbuddy.logger.StackTraceLogger;
import org.logbuddy.logger.SynchronizedLogger;
import org.logbuddy.logger.TextWritingLogger;
import org.logbuddy.logger.ThreadLogger;
import org.logbuddy.logger.TimeLogger;
import org.logbuddy.logger.wire.BrowserLogger;
import org.logbuddy.logger.wire.ConsoleLogger;
import org.logbuddy.logger.wire.FileLogger;
import org.logbuddy.renderer.HtmlRenderer;
import org.logbuddy.renderer.TextRenderer;
import org.logbuddy.renderer.chart.LineChartRenderer;
import org.logbuddy.renderer.chart.SaturationChartRenderer;
import org.logbuddy.renderer.gallery.Gallery;

public class Build {
  public List<Class<?>> loggers = asList(
      StackTraceLogger.class,
      ThreadLogger.class,
      TimeLogger.class,
      SynchronizedLogger.class,
      AsynchronousLogger.class,
      Fuse.class,
      CatchingLogger.class,
      ComposedLogger.class,
      NoLogger.class,
      TextWritingLogger.class,
      HtmlWritingLogger.class);
  public List<Class<?>> wiredLoggers = asList(
      ConsoleLogger.class,
      BrowserLogger.class,
      FileLogger.class);
  public List<Class<?>> renderers = asList(
      TextRenderer.class,
      HtmlRenderer.class,
      LineChartRenderer.class,
      SaturationChartRenderer.class,
      Gallery.class);
  public List<Class<?>> decorators = asList(
      LoggingDecorator.class,
      ComposedDecorator.class,
      NoDecorator.class);
}
