package org.logbuddy.bind;

import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.bind.LoggingStream.loggingStream;

import java.io.PrintStream;
import java.nio.charset.Charset;

import org.logbuddy.Logger;

public class StdioBinder {
  private final Charset charset;
  private final Logger logger;

  private PrintStream stdout, stderr;

  private StdioBinder(Charset charset, Logger logger) {
    this.charset = charset;
    this.logger = logger;
  }

  public static StdioBinder stdioBinder(Charset charset, Logger logger) {
    check(charset != null);
    check(logger != null);
    return new StdioBinder(charset, logger);
  }

  public void bind() {
    stdout = System.out;
    stderr = System.err;
    System.setOut(new PrintStream(loggingStream("[stdout] ", charset, logger)));
    System.setErr(new PrintStream(loggingStream("[stderr] ", charset, logger)));
  }

  public void unbind() {
    System.setOut(stdout);
    System.setErr(stderr);
  }
}
