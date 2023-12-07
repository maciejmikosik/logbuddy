package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class MockDecomposer implements Decomposer {
  private final Map<Object, List<Object>> map = new IdentityHashMap<>();

  public static MockDecomposer mockDecomposer() {
    return new MockDecomposer();
  }

  public MockDecomposer mock(Object composite, List<Object> components) {
    map.put(composite, components);
    return this;
  }

  public MockDecomposer mock(Object composite, Object... components) {
    map.put(composite, asList(components));
    return this;
  }

  public List<Object> decompose(Object composite) {
    return map.getOrDefault(composite, EMPTY_LIST);
  }
}
