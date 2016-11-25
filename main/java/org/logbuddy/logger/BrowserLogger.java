package org.logbuddy.logger;

import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.logger.HtmlWritingLogger.writing;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.logbuddy.Logger;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Html;

public class BrowserLogger {
  public static Logger browserLogger(Renderer<Html> renderer) {
    check(renderer != null);
    try {
      Process process = new ProcessBuilder("bcat", "--html")
          .redirectError(INHERIT)
          .start();
      OutputStreamWriter writer = new OutputStreamWriter(
          process.getOutputStream(),
          Charset.forName("utf8"));
      return writing(renderer, writer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
