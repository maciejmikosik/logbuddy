package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Html;

public class HtmlWritingLogger implements Logger {
  private final Renderer<Html> renderer;
  private final Writer writer;

  private HtmlWritingLogger(Renderer<Html> renderer, Writer writer) {
    this.renderer = renderer;
    this.writer = writer;
  }

  public static Logger writing(Renderer<Html> renderer, Writer writer) {
    check(renderer != null);
    check(writer != null);
    return new HtmlWritingLogger(renderer, writer);
  }

  public void log(Message message) {
    try {
      writer.write(renderer.render(message).body);
      writer.flush();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String toString() {
    return format("writing(%s, %s)", renderer, writer);
  }
}
