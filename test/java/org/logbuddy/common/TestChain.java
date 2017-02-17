package org.logbuddy.common;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.logbuddy.common.Chain.chain;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class TestChain {
  private Chain<Foo> chain;
  private Foo a, b, c, d;

  @Before
  public void before() {
    givenTest(this);
    given(chain = chain());
  }

  @Test
  public void size_of_empty_chain_is_zero() {
    given(chain = chain());
    when(chain.size());
    thenReturned(0);
  }

  @Test
  public void adding_increases_size() {
    given(chain = chain.add(a).add(b).add(c));
    when(chain.size());
    thenReturned(3);
  }

  @Test
  public void gets_last_added_element() {
    given(chain = chain.add(a).add(b).add(c));
    when(chain.get());
    thenReturned(c);
  }

  @Test
  public void gets_fail_for_empty_chain() {
    given(chain = chain());
    when(() -> chain.get());
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void removing_decreases_size() {
    given(chain = chain.add(a).add(b).add(c).remove());
    when(chain.size());
    thenReturned(2);
  }

  @Test
  public void removes_last_added_element() {
    given(chain = chain());
    given(chain = chain.add(a).add(b).remove());
    when(chain.get());
    thenReturned(a);
  }

  @Test
  public void remove_fails_for_empty_chain() {
    given(chain = chain());
    when(() -> chain.remove());
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void reverses_order() {
    given(chain = chain.add(a).add(b).add(c));
    when(chain.reverse());
    thenReturned(chain().add(c).add(b).add(a));
  }

  @Test
  public void implements_iterable() {
    given(chain = chain.add(a).add(b).add(c));
    when(stream(chain.spliterator(), false).collect(toList()));
    thenReturned(asList(c, b, a));
  }

  @Test
  public void converts_to_list() {
    given(chain = chain.add(a).add(b).add(c));
    when(chain.toList());
    thenReturned(asList(c, b, a));
  }

  @Test
  public void converts_to_stream() {
    given(chain = chain.add(a).add(b).add(c));
    when(chain.stream().collect(toList()));
    thenReturned(asList(c, b, a));
  }

  @Test
  public void implements_equals() {
    when(chain = chain.add(a).add(b).add(c));
    then(chain.equals(chain));
    then(chain.equals(chain().add(a).add(b).add(c)));
    then(!chain.equals(chain().add(a).add(b)));
    then(!chain.equals(chain().add(a).add(b).add(c).add(d)));
    then(!chain.equals(chain().add(c).add(b).add(a)));
    then(!chain.equals(new Object()));
    then(!chain.equals(null));
    thenEqual(chain.hashCode(), chain().add(a).add(b).add(c).hashCode());
  }

  @Test
  public void implements_to_string() {
    given(chain = chain.add(a).add(b).add(c));
    when(chain.toString());
    thenReturned(format("chain().add(%s).add(%s).add(%s)", a, b, c));
  }

  private static class Foo {}
}
