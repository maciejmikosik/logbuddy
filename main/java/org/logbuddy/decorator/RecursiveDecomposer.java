package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Collections.newSetFromMap;
import static org.logbuddy.LogBuddyException.check;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RecursiveDecomposer implements Decomposer {
  private final Decomposer decomposer;

  private RecursiveDecomposer(Decomposer decomposer) {
    this.decomposer = decomposer;
  }

  public static Decomposer recursive(Decomposer decomposer) {
    check(decomposer != null);
    return new RecursiveDecomposer(decomposer);
  }

  public List<Object> decompose(Object composite) {
    check(composite != null);
    List<Object> components = new LinkedList<>();
    Set<Object> processed = newSetFromMap(new IdentityHashMap<>());

    List<Object> unprocessed = new LinkedList<>();
    unprocessed.add(composite);

    while (!unprocessed.isEmpty()) {
      Object node = unprocessed.removeFirst();
      if (!processed.contains(node)) {
        components.add(node);
        processed.add(node);
        unprocessed.addAll(decomposer.decompose(node));
      }
    }
    return components;
  }

  public String toString() {
    return format("recursive(%s)", decomposer);
  }
}
