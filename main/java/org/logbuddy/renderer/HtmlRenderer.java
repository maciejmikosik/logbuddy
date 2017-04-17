package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.common.Strings.lines;
import static org.logbuddy.common.Throwables.stackTrace;
import static org.logbuddy.renderer.gallery.Gallery.gallery;

import java.awt.image.RenderedImage;

import org.logbuddy.Message;
import org.logbuddy.model.Completed.Thrown;

public class HtmlRenderer extends TextRenderer {
  public HtmlRenderer() {}

  public String render(Object model) {
    if (model instanceof Message) {
      return renderImpl((Message) model);
    } else if (model instanceof Thrown) {
      return renderImpl((Thrown) model);
    } else if (model instanceof Throwable) {
      return renderImpl((Throwable) model);
    } else if (model instanceof RenderedImage) {
      return gallery()
          .height(100)
          .paint((RenderedImage) model);
    } else {
      return super.render(model);
    }
  }

  protected String escape(String string) {
    return string
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "&nbsp;")
        .replace("\t", "&nbsp;&nbsp;")
        .replace("\"", "&quot;");
  }

  private String renderImpl(Message message) {
    return format(""
        + "<span style=\"display: block; white-space: nowrap; font-family: monospace;\">"
        + "%s"
        + "</span>",
        super.render(message));
  }

  private String renderImpl(Thrown thrown) {
    return escape("thrown ") + render(thrown.throwable);
  }

  private String renderImpl(Throwable throwable) {
    String stackTrace = lines(stackTrace(throwable)).stream()
        .map(this::escape)
        .map(line -> format("<code>%s</code><br/>", line))
        .collect(joining());
    String openStackTraceInNewTab = format(""
        + "var w = window.open(); "
        + "w.document.write('%s'); "
        + "w.document.close();",
        stackTrace);
    return format("<a href=\"#\" onclick=\"%s\">%s</a>", openStackTraceInNewTab, throwable);
  }
}
