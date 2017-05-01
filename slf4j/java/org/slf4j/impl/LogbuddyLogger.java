package org.slf4j.impl;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.Message.message;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.INFO;
import static org.slf4j.event.Level.TRACE;
import static org.slf4j.event.Level.WARN;

import org.logbuddy.Logger;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class LogbuddyLogger extends MarkerIgnoringBase {
  private final Level level;
  private final Logger logger;

  private LogbuddyLogger(Level level, Logger logger) {
    this.logger = logger;
    this.level = level;
  }

  public static org.slf4j.Logger logbuddyLogger(Level level, Logger logger) {
    check(level != null);
    check(logger != null);
    return new LogbuddyLogger(level, logger);
  }

  private void log(Level level, String message, Throwable throwable) {
    if (isEnabled(level)) {
      logger.log(message(format("[slf4j] [%s] %s", level, message)));
      if (throwable != null) {
        logger.log(message(throwable));
      }
    }
  }

  private void formatAndLog(Level level, String format, Object arg1, Object arg2) {
    if (isEnabled(level)) {
      FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
      log(level, tp.getMessage(), tp.getThrowable());
    }
  }

  private void formatAndLog(Level level, String format, Object... arguments) {
    if (isEnabled(level)) {
      FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
      log(level, tp.getMessage(), tp.getThrowable());
    }
  }

  private boolean isEnabled(Level logLevel) {
    return (logLevel.toInt() >= level.toInt());
  }

  public boolean isTraceEnabled() {
    return isEnabled(TRACE);
  }

  public void trace(String msg) {
    log(TRACE, msg, null);
  }

  public void trace(String format, Object param1) {
    formatAndLog(TRACE, format, param1, null);
  }

  public void trace(String format, Object param1, Object param2) {
    formatAndLog(TRACE, format, param1, param2);
  }

  public void trace(String format, Object... argArray) {
    formatAndLog(TRACE, format, argArray);
  }

  public void trace(String msg, Throwable t) {
    log(TRACE, msg, t);
  }

  public boolean isDebugEnabled() {
    return isEnabled(DEBUG);
  }

  public void debug(String msg) {
    log(DEBUG, msg, null);
  }

  public void debug(String format, Object param1) {
    formatAndLog(DEBUG, format, param1, null);
  }

  public void debug(String format, Object param1, Object param2) {
    formatAndLog(DEBUG, format, param1, param2);
  }

  public void debug(String format, Object... argArray) {
    formatAndLog(DEBUG, format, argArray);
  }

  public void debug(String msg, Throwable t) {
    log(DEBUG, msg, t);
  }

  public boolean isInfoEnabled() {
    return isEnabled(INFO);
  }

  public void info(String msg) {
    log(INFO, msg, null);
  }

  public void info(String format, Object arg) {
    formatAndLog(INFO, format, arg, null);
  }

  public void info(String format, Object arg1, Object arg2) {
    formatAndLog(INFO, format, arg1, arg2);
  }

  public void info(String format, Object... argArray) {
    formatAndLog(INFO, format, argArray);
  }

  public void info(String msg, Throwable t) {
    log(INFO, msg, t);
  }

  public boolean isWarnEnabled() {
    return isEnabled(WARN);
  }

  public void warn(String msg) {
    log(WARN, msg, null);
  }

  public void warn(String format, Object arg) {
    formatAndLog(WARN, format, arg, null);
  }

  public void warn(String format, Object arg1, Object arg2) {
    formatAndLog(WARN, format, arg1, arg2);
  }

  public void warn(String format, Object... argArray) {
    formatAndLog(WARN, format, argArray);
  }

  public void warn(String msg, Throwable t) {
    log(WARN, msg, t);
  }

  public boolean isErrorEnabled() {
    return isEnabled(ERROR);
  }

  public void error(String msg) {
    log(ERROR, msg, null);
  }

  public void error(String format, Object arg) {
    formatAndLog(ERROR, format, arg, null);
  }

  public void error(String format, Object arg1, Object arg2) {
    formatAndLog(ERROR, format, arg1, arg2);
  }

  public void error(String format, Object... argArray) {
    formatAndLog(ERROR, format, argArray);
  }

  public void error(String msg, Throwable t) {
    log(ERROR, msg, t);
  }

  public String toString() {
    return format("logbuddyLogger(%s, %s)", level, logger);
  }
}
