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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.logbuddy.model.Completed.ReturnedObject;
import org.logbuddy.model.Completed.ReturnedVoid;
import org.logbuddy.model.Completed.Thrown;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.InvocationDepth;

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
    } else if (model instanceof Message) {
      return renderImpl((Message) model);
    } else if (model instanceof Invocation) {
      return renderImpl((Invocation) model);
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
    } else if (model instanceof List) {
      return renderImpl("List", (List<?>) model);
    } else if (model.getClass().isArray()) {
      return renderImpl("", arrayToList(model));
    } else {
      return String.valueOf(model);
    }
  }

  private String renderImpl(Message message) {
    StringBuilder builder = new StringBuilder();
    for (Object attribute : message.attributes()) {
      builder.append(render(attribute)).append("\t");
    }
    builder.append(render(message.content()));
    builder.append("\n");
    return builder.toString();
  }

  private String renderImpl(Invocation invocation) {
    String renderedArguments = invocation.arguments.stream()
        .map(argument -> render(argument))
        .collect(joining(", "));
    return format("%s.%s(%s)",
        render(invocation.instance),
        invocation.method.getName(),
        renderedArguments);
  }

  private String renderImpl(ReturnedObject returned) {
    return format("returned %s", render(returned.object));
  }

  private String renderImpl(ReturnedVoid returned) {
    return "returned";
  }

  private String renderImpl(Thrown thrown) {
    return format("thrown %s", stackTrace(thrown.throwable));
  }

  private String renderImpl(InvocationDepth depth) {
    return times(depth.value, "\t");
  }

  private String renderImpl(String prefix, List<?> list) {
    return list.stream()
        .map(element -> render(element))
        .collect(joining(", ", prefix + "[", "]"));
  }

  private String renderImpl(ZonedDateTime zonedDateTime) {
    return dateTimeFormatter.format(zonedDateTime);
  }

  private String renderImpl(Thread thread) {
    return format("Thread(%s)", thread.getName());
  }
}
