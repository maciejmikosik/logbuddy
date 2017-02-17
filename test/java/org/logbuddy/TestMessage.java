package org.logbuddy;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.logbuddy.Message.message;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class TestMessage {
  private Message message;
  private Object content, otherContent;
  private Object attribute, otherAttribute, attributeA, attributeB, attributeC;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void stores_content() {
    given(message = message(content));
    when(message.content());
    thenReturned(content);
  }

  @Test
  public void stores_null_content() {
    given(content = null);
    given(message = message(content));
    when(message.content());
    thenReturned(content);
  }

  @Test
  public void new_message_has_no_attributes() {
    given(message = message(content));
    when(message.attributes());
    thenReturned(EMPTY_LIST);
  }

  @Test
  public void stores_attributes() {
    given(message = message(content)
        .attribute(attributeA)
        .attribute(attributeB)
        .attribute(attributeC));
    when(message.attributes());
    thenReturned(asList(attributeA, attributeB, attributeC));
  }

  @Test
  public void stores_null_attribute() {
    given(message = message(content)
        .attribute(attributeA)
        .attribute(null)
        .attribute(attributeC));
    when(message.attributes());
    thenReturned(asList(attributeA, null, attributeC));
  }

  @Test
  public void implements_equals() {
    when(message = message(content).attribute(attribute));
    then(message.equals(message));
    then(message.equals(message(content).attribute(attribute)));
    then(!message.equals(message(otherContent).attribute(attribute)));
    then(!message.equals(message(content).attribute(otherAttribute)));
    then(!message.equals(message(content)));
    then(!message.equals(new Object()));
    then(!message.equals(null));
    thenEqual(message.hashCode(), message(content).attribute(attribute).hashCode());
  }

  @Test
  public void implements_to_string() {
    given(message = message(content).attribute(attributeA).attribute(attributeB));
    when(message.toString());
    thenReturned(format("message(%s).attribute(%s).attribute(%s)", content, attributeA, attributeB));
  }
}
