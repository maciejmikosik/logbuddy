package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import java.io.IOException;
import java.io.Writer;

import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.Renderer;

public class WritingLogger implements Logger {
  private final Writer writer;
  private final Renderer<String> renderer;

  private WritingLogger(Writer writer, Renderer<String> renderer) {
    this.writer = writer;
    this.renderer = renderer;
  }

  public static Logger logger(Writer writer, Renderer<String> renderer) {
    check(renderer != null);
    check(writer != null);
    return new WritingLogger(writer, renderer);
  }

  public void log(Message message) {
    try {
      writer.write(renderer.render(message));
      writer.flush();
    } catch (IOException e) {
      throw new LogBuddyException(e);
    }
  }

  public String toString() {
    return format("logger(%s, %s)", writer, renderer);
  }
}
