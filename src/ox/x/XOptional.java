package ox.x;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Like Optional, but with some additional functionality.
 */
public class XOptional<T> {

  /**
   * Common instance for {@code empty()}.
   */
  private static final XOptional<?> EMPTY = new XOptional<>();

  private final T value;

  private XOptional() {
    this.value = null;
}

  private XOptional(T value) {
    this.value = value;
  }

  public T get() {
    checkState(isPresent(), "Tried to get value from an empty Optional!");
    return value;
  }

  public boolean isPresent() {
    return value != null;
  }

  public boolean isEmpty() {
    return !isPresent();
  }

  public XOptional<T> ifPresent(Consumer<T> callback) {
    if (isPresent()) {
      callback.accept(value);
    }
    return this;
  }

  public XOptional<T> ifEmpty(Runnable callback) {
    if (isEmpty()) {
      callback.run();
    }
    return this;
  }

  public T orElse(T alternativeValue) {
    return isPresent() ? this.value : alternativeValue;
  }

  public T orElse(Supplier<T> alternativeValue) {
    return isPresent() ? this.value : alternativeValue.get();
  }

  public T orElseNull() {
    return isPresent() ? this.value : null;
  }

  public <V> V compute(Function<T, V> callback, V defaultValue) {
    return value == null ? defaultValue : callback.apply(value);
  }

  public <U> XOptional<U> map(Function<? super T, ? extends U> function) {
    if (isEmpty()) {
      return empty();
    } else {
      return XOptional.ofNullable(function.apply(value));
    }
  }

  /**
   * Useful to avoid cases of having an XOptional<XOptional<T>>
   */
  public <U> XOptional<U> flatMap(Function<? super T, ? extends XOptional<? extends U>> function) {
    checkNotNull(function);
    if (isEmpty()) {
      return empty();
    } else {
      @SuppressWarnings("unchecked")
      XOptional<U> r = (XOptional<U>) function.apply(value);
      return checkNotNull(r);
    }
  }

  public T orElseThrow(String exceptionString) {
    if (value != null) {
      return value;
    } else {
      throw new RuntimeException(exceptionString);
    }
  }

  public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
      return value;
    } else {
      throw exceptionSupplier.get();
    }
  }

  public XList<T> toList() {
    return value == null ? XList.empty() : XList.of(value);
  }

  @Override
  public String toString() {
    return value == null ? "[Empty]" : value.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof XOptional)) {
      return false;
    }
    XOptional<?> that = (XOptional<?>) obj;
    return Objects.equals(this.value, that.value);
  }

  public static <T> XOptional<T> empty() {
    @SuppressWarnings("unchecked")
    XOptional<T> t = (XOptional<T>) EMPTY;
    return t;
  }

  public static <T> XOptional<T> of(T value) {
    checkNotNull(value);
    return new XOptional<T>(value);
  }

  public static <T> XOptional<T> ofNullable(T value) {
    return value == null ? empty() : of(value);
  }

}
