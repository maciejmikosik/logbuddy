package org.logbuddy;

import static java.util.Objects.hash;
import static org.logbuddy.common.Chain.chain;

import java.util.List;
import java.util.Objects;

import org.logbuddy.common.Chain;

public class Message {
  private final Object content;
  private final Chain<Object> attributes;

  private Message(Object content, Chain<Object> attributes) {
    this.content = content;
    this.attributes = attributes;
  }

  public static Message message(Object content) {
    return new Message(content, chain());
  }

  public Object content() {
    return content;
  }

  public List<Object> attributes() {
    return attributes.reverse().toList();
  }

  public Message attribute(Object attribute) {
    return new Message(content, attributes.add(attribute));
  }

  public boolean equals(Object object) {
    return object instanceof Message && equals((Message) object);
  }

  private boolean equals(Message message) {
    return Objects.equals(content, message.content)
        && Objects.equals(attributes, message.attributes);
  }

  public int hashCode() {
    return hash(content, attributes);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("message(").append(content).append(")");
    for (Object attribute : attributes.reverse()) {
      builder.append(".attribute(").append(attribute).append(")");
    }
    return builder.toString();
  }
}
