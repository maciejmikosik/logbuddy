package org.logbuddy.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Chain<E> implements Iterable<E> {
  private final int size;
  private final E element;
  private final Chain<E> tail;

  private Chain(int size, E element, Chain<E> tail) {
    this.size = size;
    this.element = element;
    this.tail = tail;
  }

  public static <E> Chain<E> chain() {
    return new Chain<>(0, null, null);
  }

  public int size() {
    return size;
  }

  public E get() {
    checkHasElement();
    return element;
  }

  public Chain<E> add(E element) {
    return new Chain(size + 1, element, this);
  }

  public Chain<E> remove() {
    checkHasElement();
    return tail;
  }

  public Chain<E> reverse() {
    Chain<E> reversed = chain();
    Chain<E> chain = this;
    while (chain.hasElement()) {
      reversed = reversed.add(chain.element);
      chain = chain.tail;
    }
    return reversed;
  }

  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private Chain<E> chain = Chain.this;

      public boolean hasNext() {
        return chain.hasElement();
      }

      public E next() {
        E next = chain.element;
        chain = chain.tail;
        return next;
      }
    };
  }

  public List<E> toList() {
    return stream().collect(Collectors.toList());
  }

  public Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  public boolean equals(Object object) {
    return object instanceof Chain<?> && equals((Chain<?>) object);
  }

  private boolean equals(Chain<?> chain) {
    if (size != chain.size) {
      return false;
    }
    Chain<?> first = this;
    Chain<?> second = chain;
    while (first.hasElement()) {
      if (!Objects.equals(first.element, second.element)) {
        return false;
      }
      first = first.tail;
      second = second.tail;
    }
    return true;
  }

  public int hashCode() {
    int hash = 0xFFFF;
    Chain<E> chain = this;
    while (chain.hasElement()) {
      hash += element.hashCode();
      hash *= 0xFFFF;
      chain = chain.tail;
    }
    return hash;
  }

  public String toString() {
    List<E> elements = new ArrayList<>(size);
    Chain<E> chain = this;
    while (chain.hasElement()) {
      elements.add(chain.element);
      chain = chain.tail;
    }
    Collections.reverse(elements);
    StringBuilder builder = new StringBuilder("chain()");
    for (E element : elements) {
      builder.append(".add(").append(element).append(")");
    }
    return builder.toString();
  }

  private void checkHasElement() {
    if (!hasElement()) {
      throw new NoSuchElementException();
    }
  }

  private boolean hasElement() {
    return size != 0;
  }
}
