package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.common.Collections.arrayToList;
import static org.logbuddy.common.Strings.times;
import static org.logbuddy.common.Throwables.stackTrace;
import static org.logbuddy.message.Attribute.attribute;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.logbuddy.message.Attribute;
import org.logbuddy.message.Completed.ReturnedObject;
import org.logbuddy.message.Completed.ReturnedVoid;
import org.logbuddy.message.Completed.Thrown;
import org.logbuddy.message.InvocationDepth;
import org.logbuddy.message.Invoked;

public class TextRenderer implements Renderer<String> {
  private final DateTimeFormatter dateTimeFormatter;

  public TextRenderer() {
    dateTimeFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2)
        .appendFraction(NANO_OF_SECOND, 3, 3, true)
        .appendOffsetId()
        .toFormatter();
  }

  public String render(Object model) {
    if (model == null) {
      return "null";
    } else if (model instanceof String) {
      return renderImpl((String) model);
    } else if (model instanceof Message) {
      return renderImpl((Message) model);
    } else if (model instanceof Attribute) {
      return renderImpl((Attribute) model);
    } else if (model instanceof Invoked) {
      return renderImpl((Invoked) model);
    } else if (model instanceof ReturnedObject) {
      return renderImpl((ReturnedObject) model);
    } else if (model instanceof ReturnedVoid) {
      return renderImpl((ReturnedVoid) model);
    } else if (model instanceof Thrown) {
      return renderImpl((Thrown) model);
    } else if (model instanceof InvocationDepth) {
      return renderImpl((InvocationDepth) model);
    } else if (model instanceof ZonedDateTime) {
      return renderImpl((ZonedDateTime) model);
    } else if (model instanceof Thread) {
      return renderImpl((Thread) model);
    } else if (model instanceof Class) {
      return renderImpl((Class) model);
    } else if (model instanceof List) {
      return renderImpl("List", (List<?>) model);
    } else if (model.getClass().isArray()) {
      return renderImpl("", arrayToList(model));
    } else {
      return String.valueOf(model);
    }
  }

  protected String escape(String string) {
    return string;
  }

  private String renderImpl(String string) {
    return escape(string);
  }

  private String renderImpl(Message message) {
    StringBuilder builder = new StringBuilder();
    for (Object attribute : message.attributes()) {
      builder.append(render(attribute(attribute))).append(escape("  "));
    }
    builder.append(render(message.content()));
    return builder.toString();
  }

  private String renderImpl(Attribute attribute) {
    if (attribute.model instanceof Thread) {
      return render(((Thread) attribute.model).getName());
    } else {
      return render(attribute.model);
    }
  }

  private String renderImpl(Invoked invoked) {
    return ""
        + render(invoked.instance)
        + escape(".")
        + escape(invoked.method.getName())
        + escape("(")
        + invoked.arguments.stream()
            .map(this::render)
            .collect(joining(escape(", ")))
        + escape(")");
  }

  private String renderImpl(ReturnedObject returned) {
    return escape("returned ") + render(returned.object);
  }

  private String renderImpl(ReturnedVoid returned) {
    return escape("returned");
  }

  private String renderImpl(Thrown thrown) {
    return escape("thrown ") + escape(stackTrace(thrown.throwable));
  }

  private String renderImpl(InvocationDepth depth) {
    return escape(times(2 * depth.value, " "));
  }

  private String renderImpl(String prefix, List<?> list) {
    return list.stream()
        .map(this::render)
        .collect(joining(escape(", "), escape(prefix + "["), escape("]")));
  }

  private String renderImpl(ZonedDateTime zonedDateTime) {
    return escape(dateTimeFormatter.format(zonedDateTime));
  }

  private String renderImpl(Thread thread) {
    return escape(format("Thread(%s)", thread.getName()));
  }

  private String renderImpl(Class type) {
    return escape(type.getName());
  }
}
