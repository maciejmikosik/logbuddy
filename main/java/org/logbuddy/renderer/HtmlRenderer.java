package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Strings.lines;
import static org.logbuddy.common.Strings.times;
import static org.logbuddy.common.Throwables.stackTrace;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.gallery.Gallery.gallery;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.logbuddy.model.Depth;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Property;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class HtmlRenderer implements Renderer<Html> {
  private final Renderer<Text> textRenderer;

  public HtmlRenderer(Renderer<Text> textRenderer) {
    check(textRenderer != null);
    this.textRenderer = textRenderer;
  }

  public Html render(Object model) {
    if (model instanceof Message) {
      return renderImpl((Message) model);
    } else if (model instanceof Invocation) {
      return renderImpl((Invocation) model);
    } else if (model instanceof Returned) {
      return renderImpl((Returned) model);
    } else if (model instanceof Thrown) {
      return renderImpl((Thrown) model);
    } else if (model instanceof Throwable) {
      return renderImpl((Throwable) model);
    } else if (model instanceof Depth) {
      return renderImpl((Depth) model);
    } else if (model instanceof Property) {
      return renderImpl((Property) model);
    } else if (model instanceof List) {
      return renderImpl("List", (List<?>) model);
    } else if (model != null && model.getClass().isArray()) {
      return renderImpl("", arrayToList(model));
    } else if (model instanceof BufferedImage) {
      return gallery()
          .height(100)
          .paint((BufferedImage) model);
    } else {
      return asHtml(textRenderer.render(model));
    }
  }

  private Html renderImpl(Message message) {
    StringBuilder builder = new StringBuilder();
    for (Object attribute : message.attributes()) {
      builder.append(render(attribute).body).append("&nbsp;&nbsp;");
    }
    builder.append(render(message.content()).body);
    return html(builder.toString());
  }

  private Html renderImpl(Invocation invocation) {
    String renderedArguments = invocation.arguments.stream()
        .map(argument -> render(argument).body)
        .collect(joining(", "));
    return html(format("%s.%s(%s)",
        render(invocation.instance).body,
        invocation.method.getName(),
        renderedArguments));
  }

  private Html renderImpl(Returned returned) {
    return html(format("returned %s", render(returned.object).body));
  }

  private Html renderImpl(Thrown thrown) {
    return html(format("thrown %s", render(thrown.throwable).body));
  }

  private Html renderImpl(Throwable throwable) {
    String stackTrace = lines(stackTrace(throwable)).stream()
        .map(HtmlRenderer::escape)
        .map(line -> format("<code>%s</code><br/>", line))
        .collect(joining());
    String openStackTraceInNewTab = format(""
        + "var w = window.open(); "
        + "w.document.write('%s'); "
        + "w.document.close();",
        stackTrace);
    return html(format("<a href=\"#\" onclick=\"%s\">%s</a>", openStackTraceInNewTab, throwable));
  }

  private Html renderImpl(Depth depth) {
    String indentation = times(2 * depth.value, "&nbsp;");
    return html(indentation + render(depth.model).body);
  }

  private Html renderImpl(String prefix, List<?> list) {
    return html(list.stream()
        .map(element -> render(element).body)
        .collect(joining(escape(", "), escape(prefix + "["), escape("]"))));
  }

  private Html renderImpl(Property property) {
    return html(format("%s&nbsp;&nbsp;%s",
        render(property.value).body,
        render(property.model).body));
  }

  private static Html asHtml(Text text) {
    return html(escape(text.string));
  }

  private static String escape(String string) {
    return string
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "&nbsp;")
        .replace("\t", "&nbsp;&nbsp;");
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
