package org.logbuddy.decorator;

import static org.logbuddy.decorator.MockDecorator.mockDecorator;

import org.logbuddy.Decorator;

public class PrepareDecorator {
  public static final Object undecorated = new Object(),
      decorated = new Object(),
      overdecorated = new Object();

  public static final Decorator decorator = mockDecorator()
      .nice()
      .stub(undecorated, decorated)
      .stub(decorated, overdecorated);
}
