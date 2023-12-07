package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.logbuddy.common.Classes.isWrapper;

import java.util.List;

public class SkipPrimitivesDecomposer implements Decomposer {
  private final Decomposer decomposer;

  private SkipPrimitivesDecomposer(Decomposer decomposer) {
    this.decomposer = decomposer;
  }

  public static Decomposer skipPrimitives(Decomposer decomposer) {
    return new SkipPrimitivesDecomposer(decomposer);
  }

  public List<Object> decompose(Object composite) {
    return isWrapper(composite.getClass())
        ? emptyList()
        : decomposer.decompose(composite);
  }

  public String toString() {
    return format("skipPrimitives(%s)", decomposer);
  }
}
