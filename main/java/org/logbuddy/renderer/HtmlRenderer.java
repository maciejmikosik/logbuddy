package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.gallery.Gallery.gallery;

import java.awt.image.BufferedImage;
import java.util.List;

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
    if (model instanceof Invocation) {
      return renderImpl((Invocation) model);
    } else if (model instanceof Returned) {
      return renderImpl((Returned) model);
    } else if (model instanceof Thrown) {
      return renderImpl((Thrown) model);
    } else if (model instanceof Depth) {
      return renderImpl((Depth) model);
    } else if (model instanceof Property) {
      return renderImpl((Property) model);
    } else if (model instanceof List) {
      return html(((List<?>) model).stream()
          .map(element -> render(element).body)
          .collect(joining(escape(", "), escape("List["), escape("]"))));
    } else if (model instanceof BufferedImage) {
      return gallery()
          .height(100)
          .paint((BufferedImage) model);
    } else {
      return asHtml(textRenderer.render(model));
    }
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

  private Html renderImpl(Depth depth) {
    String indentation = new String(new char[depth.value]).replace("\0", "&nbsp;&nbsp;");
    return html(indentation + render(depth.model).body);
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
}
