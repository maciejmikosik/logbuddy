package com.mikosik.logbuddy.logger;

import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import com.mikosik.logbuddy.Logger;

public class BrowserLogger implements Logger {
  private final OutputStreamWriter writer;

  private BrowserLogger(OutputStreamWriter writer) {
    this.writer = writer;
  }

  public static Logger browserLogger() {
    try {
      Process process = new ProcessBuilder("bcat", "--html")
          .redirectError(INHERIT)
          .start();
      OutputStream output = process.getOutputStream();
      Charset charset = Charset.forName("utf8");
      return new BrowserLogger(new OutputStreamWriter(output, charset));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void log(String message) {
    try {
      writer.write("<code>");
      writer.write(message);
      writer.write("</code>");
      writer.write("<br/>\n");
      writer.flush();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
