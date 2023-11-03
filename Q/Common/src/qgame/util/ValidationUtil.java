package qgame.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.List;


/**
 * Utilities class which abstracts methods used across several classes.
 */
public class ValidationUtil {
  /**
   * Checks that an object is valid based on a given predicate.
   * @param pred the test used on the object
   * @param value the object you are testing the predicate on
   * @param message The error message given when the object fails the predicate test
   * @param <T> any object
   * @throws IllegalArgumentException if the predicate test fails
   */
  public static <T> void validateArg(Predicate<T> pred, T value, String message)
    throws IllegalArgumentException{
    if (!pred.test(value)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that an object is valid based on a given predicate.
   * @param pred the test used on the object
   * @param value the object you are testing the predicate on
   * @param message The error message given when the object fails the predicate test
   * @param <T> any object
   * @throws IllegalArgumentException if the predicate test fails
   */
  public static <T> void validateState(Predicate<T> pred, T value, String message)
    throws IllegalArgumentException{
    if (!pred.test(value)) {
      throw new IllegalStateException(message);
    }
  }

  /**
   * Checks that an object is not null.
   * @param value the object
   * @param message The error message given when the object fails the predicate test
   * @return the object
   * @param <T> any object
   * @throws IllegalArgumentException if the object is null
   */
  public static <T> T nonNullObj(T value, String message) throws IllegalArgumentException {
    if (Objects.isNull(value)) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  /**
   * Checks that a list is not null and has no null elements.
   * @param list list
   * @param name The name of the list
   * @return the list
   * @param <T> any type
   * @throws IllegalArgumentException if the list is null or has null elements.
   */
  public static <T> List<T> nonNull(List<T> list, String name) throws IllegalArgumentException {
    nonNullObj(list, String.format("%s cannot be null", name));
    list.forEach(val -> nonNullObj(val, String.format("Elements of %s cannot be null", name)));
    return list;
  }
}
