package com.mikosik.logbuddy.logger;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import com.mikosik.logbuddy.Logger;

public class WriterLogger implements Logger {
  private final Writer writer;

  private WriterLogger(Writer writer) {
    this.writer = writer;
  }

  public static Logger logger(Writer writer) {
    requireNonNull(writer);
    return new WriterLogger(writer);
  }

  public void log(String message) {
    try {
      writer.write(message);
      writer.write('\n');
      writer.flush();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
