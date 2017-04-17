package org.logbuddy.model;

import static java.lang.String.format;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.logbuddy.model.Completed.returned;
import static org.logbuddy.model.Completed.thrown;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.model.Completed.ReturnedObject;
import org.logbuddy.model.Completed.ReturnedVoid;
import org.logbuddy.model.Completed.Thrown;

public class TestCompleted {
  private Object object, otherObject;
  private Throwable throwable, otherThrowable;
  private ReturnedObject returned;
  private ReturnedVoid returnedVoid;
  private Thrown thrown;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void creates_returned_object() {
    given(returned = returned(object));
    when(returned.object);
    thenReturned(sameInstance(object));
  }

  @Test
  public void creates_returned_null() {
    given(returned = returned(null));
    when(returned.object);
    thenReturned(null);
  }

  @Test
  public void creates_returned_void() {
    when(returned());
    thenReturned(instanceOf(ReturnedVoid.class));
  }

  @Test
  public void creates_thrown_throwable() {
    given(thrown = thrown(throwable));
    when(thrown.throwable);
    thenReturned(throwable);
  }

  @Test
  public void implements_equals() {
    when(() -> {
      returned = returned(object);
      thrown = thrown(throwable);
      returnedVoid = returned();
    });
    then(returned.equals(returned));
    then(returned.equals(returned(object)));
    then(!returned.equals(returned(otherObject)));
    then(!returned.equals(new Object()));
    then(!returned.equals(null));
    then(returnedVoid.equals(returnedVoid));
    then(returnedVoid.equals(returned()));
    then(!returnedVoid.equals(new Object()));
    then(!returnedVoid.equals(null));
    then(thrown.equals(thrown));
    then(thrown.equals(thrown(throwable)));
    then(!thrown.equals(thrown(otherThrowable)));
    then(!thrown.equals(new Object()));
    then(!thrown.equals(null));
    then(!returned(object).equals(returnedVoid));
    then(!returned(null).equals(returned()));
    then(!thrown(throwable).equals(returned(throwable)));
    then(!thrown(throwable).equals(returned()));
  }

  @Test
  public void returned_object_implements_to_string() {
    given(returned = returned(object));
    when(returned.toString());
    thenReturned(format("returned(%s)", object));
  }

  @Test
  public void returned_null_implements_to_string() {
    given(returned = returned(null));
    when(returned.toString());
    thenReturned("returned(null)");
  }

  @Test
  public void returned_void_implements_to_string() {
    given(returnedVoid = returned());
    when(returnedVoid.toString());
    thenReturned("returned()");
  }

  @Test
  public void thrown_throwable_implements_to_string() {
    given(thrown = thrown(throwable));
    when(thrown.toString());
    thenReturned(format("thrown(%s)", throwable));
  }

  @Test
  public void cannot_throw_null() {
    when(() -> thrown(null));
    thenThrown(LogBuddyException.class);
  }
}
