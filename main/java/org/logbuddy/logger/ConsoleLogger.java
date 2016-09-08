package org.logbuddy.logger;

import static org.logbuddy.LogBuddyException.check;

import java.io.IOException;
import java.io.Writer;

import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Text;

public class ConsoleLogger implements Logger {
  private final Renderer<Text> renderer;
  private final Writer writer;

  private ConsoleLogger(Renderer<Text> renderer, Writer writer) {
    this.renderer = renderer;
    this.writer = writer;
  }

  public static Logger console(Renderer<Text> renderer, Writer writer) {
    check(renderer != null);
    check(writer != null);
    return new ConsoleLogger(renderer, writer);
  }

  public void log(Object model) {
    try {
      writer.write(renderer.render(model).string);
      writer.write("\n");
      writer.flush();
    } catch (IOException e) {
      throw new LogBuddyException(e);
    }
  }
}
