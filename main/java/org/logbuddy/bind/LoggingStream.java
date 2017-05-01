package org.logbuddy.bind;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.Message.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.logbuddy.Logger;

public class LoggingStream extends OutputStream {
  private final String prefix;
  private final Charset charset;
  private final Logger logger;

  private final ThreadLocal<ByteArrayOutputStream> buffers = ThreadLocal
      .withInitial(ByteArrayOutputStream::new);

  private LoggingStream(String prefix, Charset charset, Logger logger) {
    this.prefix = prefix;
    this.charset = charset;
    this.logger = logger;
  }

  public static OutputStream loggingStream(String prefix, Charset charset, Logger logger) {
    check(prefix != null);
    check(charset != null);
    check(logger != null);
    return new LoggingStream(prefix, charset, logger);
  }

  public void write(int b) throws IOException {
    ByteArrayOutputStream buffer = buffers.get();
    if (b == '\n') {
      logger.log(message(prefix + new String(buffer.toByteArray(), charset)));
      buffer.reset();
    } else {
      buffer.write(b);
    }
  }

  public String toString() {
    return format("loggingStream(%s, %s, %s)", prefix, charset, logger);
  }
}
