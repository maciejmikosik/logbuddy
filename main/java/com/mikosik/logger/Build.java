package com.mikosik.logger;

import static java.util.Arrays.asList;

import java.util.List;

import com.mikosik.logger.logger.NoLogger;
import com.mikosik.logger.logger.SynchronizedLogger;
import com.mikosik.logger.logger.ThreadLogger;
import com.mikosik.logger.logger.TimeLogger;
import com.mikosik.logger.logger.WriterLogger;

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
