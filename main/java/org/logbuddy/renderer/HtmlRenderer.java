package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Strings.lines;
import static org.logbuddy.common.Strings.times;
import static org.logbuddy.common.Throwables.stackTrace;
import static org.logbuddy.renderer.gallery.Gallery.gallery;

import java.awt.image.RenderedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.logbuddy.model.Completed.ReturnedObject;
import org.logbuddy.model.Completed.ReturnedVoid;
import org.logbuddy.model.Completed.Thrown;
import org.logbuddy.model.InvocationDepth;
import org.logbuddy.model.Invoked;

public class HtmlRenderer implements Renderer<String> {
  private final Renderer<String> textRenderer;

  public HtmlRenderer(Renderer<String> textRenderer) {
    check(textRenderer != null);
    this.textRenderer = textRenderer;
  }

  public String render(Object model) {
    if (model instanceof Message) {
      return renderImpl((Message) model);
    } else if (model instanceof Invoked) {
      return renderImpl((Invoked) model);
    } else if (model instanceof ReturnedObject) {
      return renderImpl((ReturnedObject) model);
    } else if (model instanceof ReturnedVoid) {
      return renderImpl((ReturnedVoid) model);
    } else if (model instanceof Thrown) {
      return renderImpl((Thrown) model);
    } else if (model instanceof Throwable) {
      return renderImpl((Throwable) model);
    } else if (model instanceof InvocationDepth) {
      return renderImpl((InvocationDepth) model);
    } else if (model instanceof List) {
      return renderImpl("List", (List<?>) model);
    } else if (model != null && model.getClass().isArray()) {
      return renderImpl("", arrayToList(model));
    } else if (model instanceof RenderedImage) {
      return gallery()
          .height(100)
          .paint((RenderedImage) model);
    } else {
      return escape(textRenderer.render(model));
    }
  }

  private String renderImpl(Message message) {
    StringBuilder builder = new StringBuilder();
    builder.append("<span style=\"display: block; white-space: nowrap; font-family: monospace;\">");
    for (Object attribute : message.attributes()) {
      builder.append(render(attribute)).append("&nbsp;&nbsp;");
    }
    builder.append(render(message.content()));
    builder.append("</span>\n");
    return builder.toString();
  }

  private String renderImpl(Invoked invoked) {
    String renderedArguments = invoked.arguments.stream()
        .map(argument -> render(argument))
        .collect(joining(", "));
    return format("%s.%s(%s)",
        render(invoked.instance),
        invoked.method.getName(),
        renderedArguments);
  }

  private String renderImpl(ReturnedObject returned) {
    return format("returned %s", render(returned.object));
  }

  private String renderImpl(ReturnedVoid returned) {
    return "returned";
  }

  private String renderImpl(Thrown thrown) {
    return format("thrown %s", render(thrown.throwable));
  }

  private String renderImpl(Throwable throwable) {
    String stackTrace = lines(stackTrace(throwable)).stream()
        .map(HtmlRenderer::escape)
        .map(line -> format("<code>%s</code><br/>", line))
        .collect(joining());
    String openStackTraceInNewTab = format(""
        + "var w = window.open(); "
        + "w.document.write('%s'); "
        + "w.document.close();",
        stackTrace);
    return format("<a href=\"#\" onclick=\"%s\">%s</a>", openStackTraceInNewTab, throwable);
  }

  private String renderImpl(InvocationDepth depth) {
    return times(2 * depth.value, "&nbsp;");
  }

  private String renderImpl(String prefix, List<?> list) {
    return list.stream()
        .map(element -> render(element))
        .collect(joining(escape(", "), escape(prefix + "["), escape("]")));
  }

  private static String escape(String string) {
    return string
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "&nbsp;")
        .replace("\t", "&nbsp;&nbsp;")
        .replace("\"", "&quot;");
  }

  private static List<Object> arrayToList(Object array) {
    int length = Array.getLength(array);
    List<Object> list = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      list.add(Array.get(array, i));
    }
    return list;
  }
}
