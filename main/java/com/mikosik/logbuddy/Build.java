package com.mikosik.logbuddy;

import static java.util.Arrays.asList;

import java.util.List;

import com.mikosik.logbuddy.logger.NoLogger;
import com.mikosik.logbuddy.logger.SynchronizedLogger;
import com.mikosik.logbuddy.logger.ThreadLogger;
import com.mikosik.logbuddy.logger.TimeLogger;
import com.mikosik.logbuddy.logger.WriterLogger;

public class Build {
  public List<Class<?>> loggers = asList(
      WriterLogger.class,
      TimeLogger.class,
      ThreadLogger.class,
      NoLogger.class,
      SynchronizedLogger.class);
  public List<Class<?>> logging = asList(
      Logging.class,
      NoLogging.class);
}
