package org.logbuddy.common;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Varargs<E> {
  List<E> toList();

  static <E> Varargs<E> varargs() {
    return () -> emptyList();
  }

  static <E> Varargs<E> varargs(E element) {
    return () -> singletonList(element);
  }

  static <E> Varargs<E> varargs(E... elements) {
    return () -> asList(elements);
  }

  static <E> Varargs<E> varargs(List<E> elements) {
    return () -> elements;
  }

  default Varargs<E> forbidNullElements() {
    return () -> {
      List<E> list = Varargs.this.toList();
      if (list.contains(null)) {
        throw new NullPointerException();
      }
      return list;
    };
  }

  default Varargs<E> defensiveCopy() {
    return () -> new ArrayList<>(Varargs.this.toList());
  }

  default Varargs<E> unmodifiable() {
    return () -> unmodifiableList(Varargs.this.toList());
  }

  default Varargs<E> onErrorThrow(
      Function<? super RuntimeException, ? extends RuntimeException> factory) {
    return () -> {
      try {
        return Varargs.this.toList();
      } catch (RuntimeException e) {
        throw factory.apply(e);
      }
    };
  }
}
