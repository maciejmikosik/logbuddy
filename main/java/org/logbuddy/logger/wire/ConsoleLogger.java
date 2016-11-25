package org.logbuddy.logger.wire;

import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.logger.TextWritingLogger.writing;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.logbuddy.Logger;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Text;

public class ConsoleLogger {
  public static Logger consoleLogger(Renderer<Text> renderer) {
    check(renderer != null);
    Writer writer = new OutputStreamWriter(System.out, Charset.forName("utf8"));
    return writing(renderer, writer);
  }
}
