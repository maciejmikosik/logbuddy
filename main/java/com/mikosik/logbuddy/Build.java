package com.mikosik.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import com.mikosik.logbuddy.formatter.Charts;
import com.mikosik.logbuddy.formatter.DefaultFormatter;
import com.mikosik.logbuddy.formatter.HtmlFormatter;
import com.mikosik.logbuddy.logger.BrowserLogger;
import com.mikosik.logbuddy.logger.NoLogger;
import com.mikosik.logbuddy.logger.SynchronizedLogger;
import com.mikosik.logbuddy.logger.ThreadLogger;
import com.mikosik.logbuddy.logger.TimeLogger;
import com.mikosik.logbuddy.logger.WriterLogger;

public class Build {
  public List<Class<?>> loggers = asList(
      WriterLogger.class,
      BrowserLogger.class,
      TimeLogger.class,
      ThreadLogger.class,
      NoLogger.class,
      SynchronizedLogger.class);
  public List<Class<?>> decorator = asList(
      Decorator.class,
      NoDecorator.class);
  public List<Class<?>> formatters = asList(
      DefaultFormatter.class,
      HtmlFormatter.class);
  public List<Class<?>> helpers = asList(
      Charts.class);
}
