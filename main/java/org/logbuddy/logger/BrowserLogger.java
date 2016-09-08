package org.logbuddy.logger;

import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static org.logbuddy.LogBuddyException.check;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.logbuddy.Logger;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Html;

public class BrowserLogger implements Logger {
  private final Renderer<Html> renderer;
  private final OutputStreamWriter writer;

  private BrowserLogger(Renderer<Html> renderer, OutputStreamWriter writer) {
    this.renderer = renderer;
    this.writer = writer;
  }

  public static Logger browserLogger(Renderer<Html> renderer) {
    check(renderer != null);
    try {
      Process process = new ProcessBuilder("bcat", "--html")
          .redirectError(INHERIT)
          .start();
      OutputStream output = process.getOutputStream();
      Charset charset = Charset.forName("utf8");
      return new BrowserLogger(renderer, new OutputStreamWriter(output, charset));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void log(Object model) {
    try {
      writer.write("<code>");
      writer.write(renderer.render(model).body);
      writer.write("</code>");
      writer.write("<br/>\n");
      writer.flush();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
