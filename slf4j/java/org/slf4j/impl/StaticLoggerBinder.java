package org.slf4j.impl;

import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.logger.NoLogger.noLogger;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.impl.LogbuddyLogger.logbuddyLogger;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {
  public static String REQUESTED_API_VERSION = "1.7.21";
  private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();
  private volatile ILoggerFactory factory = name -> logbuddyLogger(ERROR, noLogger());

  private StaticLoggerBinder() {}

  public static final StaticLoggerBinder getSingleton() {
    return INSTANCE;
  }

  public ILoggerFactory getLoggerFactory() {
    return factory;
  }

  public void setLoggerFactory(ILoggerFactory factory) {
    check(factory != null);
    this.factory = factory;
  }

  public String getLoggerFactoryClassStr() {
    return factory.toString();
  }
}
