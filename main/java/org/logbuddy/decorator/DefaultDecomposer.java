package org.logbuddy.decorator;

import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Classes.hierarchy;
import static org.logbuddy.common.Collections.stream;
import static org.logbuddy.common.Fields.read;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.logbuddy.common.Classes;

public class DefaultDecomposer implements Decomposer {
  public static Decomposer decomposer() {
    return new DefaultDecomposer();
  }

  public List<Object> decompose(Object composite) {
    check(composite != null);
    return composite.getClass().isArray()
        ? decomposeArray(composite)
        : decomposeNonArray(composite);
  }

  private List<Object> decomposeArray(Object composite) {
    int length = Array.getLength(composite);
    List<Object> decomposed = new ArrayList<>(length);
    for (int index = 0; index < length; index++) {
      Object element = Array.get(composite, index);
      if (element != null) {
        decomposed.add(element);
      }
    }
    return decomposed;
  }

  private List<Object> decomposeNonArray(Object composite) {
    return stream(hierarchy(composite.getClass()))
        .flatMap(type -> stream(type.getDeclaredFields()))
        .filter(not(Classes::isStatic))
        .map(field -> read(composite, field))
        .filter(value -> value != null)
        .toList();
  }

  public String toString() {
    return "decomposer()";
  }
}
