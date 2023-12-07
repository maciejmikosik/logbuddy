package org.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import org.logbuddy.bind.LoggingStream;
import org.logbuddy.bind.StdioBinder;
import org.logbuddy.decorator.CachingDecorator;
import org.logbuddy.decorator.ComponentsDecorator;
import org.logbuddy.decorator.ComposedDecorator;
import org.logbuddy.decorator.DefaultDecomposer;
import org.logbuddy.decorator.InjectingDecorator;
import org.logbuddy.decorator.InvocationDecorator;
import org.logbuddy.decorator.JdkDecorator;
import org.logbuddy.decorator.NoDecorator;
import org.logbuddy.decorator.RecursiveDecomposer;
import org.logbuddy.decorator.Rich;
import org.logbuddy.decorator.TraversingDecorator;
import org.logbuddy.decorator.TryingDecorator;
import org.logbuddy.logger.AsynchronousLogger;
import org.logbuddy.logger.CatchingLogger;
import org.logbuddy.logger.ComposedLogger;
import org.logbuddy.logger.Fuse;
import org.logbuddy.logger.InvocationDepthLogger;
import org.logbuddy.logger.NoLogger;
import org.logbuddy.logger.SynchronizedLogger;
import org.logbuddy.logger.ThreadLogger;
import org.logbuddy.logger.TimeLogger;
import org.logbuddy.logger.WritingLogger;
import org.logbuddy.logger.wire.BrowserLogger;
import org.logbuddy.logger.wire.ConsoleLogger;
import org.logbuddy.logger.wire.FileLogger;
import org.logbuddy.message.Attribute;
import org.logbuddy.renderer.HtmlRenderer;
import org.logbuddy.renderer.TextRenderer;
import org.logbuddy.renderer.chart.LineChartRenderer;
import org.logbuddy.renderer.chart.SaturationChartRenderer;
import org.logbuddy.renderer.gallery.Gallery;

@SuppressWarnings("deprecation")
public class Build {
  public List<Class<?>> message = asList(
      Message.class,
      Attribute.class);

  public List<Class<?>> loggers = asList(
      InvocationDepthLogger.class,
      ThreadLogger.class,
      TimeLogger.class,
      SynchronizedLogger.class,
      AsynchronousLogger.class,
      Fuse.class,
      CatchingLogger.class,
      ComposedLogger.class,
      NoLogger.class,
      WritingLogger.class);
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
      InvocationDecorator.class,
      ComponentsDecorator.class,
      JdkDecorator.class,
      TryingDecorator.class,
      ComposedDecorator.class,
      NoDecorator.class,
      InjectingDecorator.class,
      TraversingDecorator.class,
      CachingDecorator.class);
  public List<Class<?>> decomposers = asList(
      DefaultDecomposer.class,
      RecursiveDecomposer.class);
  public List<Class<?>> rich = asList(
      Rich.class);
  public List<Class<?>> bind = asList(
      LoggingStream.class,
      StdioBinder.class);
}
