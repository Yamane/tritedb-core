/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;

import java.util.Objects;

@FunctionalInterface
public interface LazyConsumer<T> {

  void accept(T t) throws Exception;

  default LazyConsumer<T> andThen(LazyConsumer<? super T> after) {
    Objects.requireNonNull(after);
    return (T t) -> {
      accept(t);
      after.accept(t);
    };
  }
}
