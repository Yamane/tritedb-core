/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;

@FunctionalInterface
public interface LazyFunction<T, R> {

  R apply(T t) throws Exception;
}
