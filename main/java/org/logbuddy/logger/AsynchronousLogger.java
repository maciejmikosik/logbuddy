package org.logbuddy.logger;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.logbuddy.LogBuddyException.check;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.logbuddy.Logger;

public class AsynchronousLogger implements Logger {
  private final Logger logger;
  private final ExecutorService executor;

  private AsynchronousLogger(Logger logger, ExecutorService executor) {
    this.logger = logger;
    this.executor = executor;
  }

  public static Logger asynchronous(Logger logger) {
    check(logger != null);
    return new AsynchronousLogger(logger, newExecutor());
  }

  public void log(Object model) {
    executor.submit(() -> logger.log(model));
  }

  public String toString() {
    return format("asynchronous(%s)", logger);
  }

  private static ExecutorService newExecutor() {
    int corePoolSize = 0;
    int maximumPoolSize = 1;
    long keepAliveTime = 1L;
    TimeUnit keepAliveUnit = SECONDS;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue(Integer.MAX_VALUE);
    return new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        keepAliveUnit,
        workQueue);
  }
}
