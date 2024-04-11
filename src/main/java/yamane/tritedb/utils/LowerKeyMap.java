/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * キーが大文字と小文字を区別しないStringがKeyなHashMapです.
 * RDBのカラム名は大文字と小文字を区別しないことが多いため、カラム名の絡む処理などで使用しています.
 * @param <T> 格納するオブジェクトの型
 */
public class LowerKeyMap<T> extends HashMap<String, T> {

  static final long serialVersionUID = 1L;

  /** {@inheritDoc} */
  @Override
  public boolean containsKey(Object key) {
    return super.containsKey(key.toString().toLowerCase());
  }

  /** {@inheritDoc} */
  @Override
  public T get(Object key) {
    return super.get(key.toString().toLowerCase());
  }

  /** {@inheritDoc} */
  @Override
  public T put(String key, T value) {
    return super.put(key.toLowerCase(), value);
  }

  /** {@inheritDoc} */
  @Override
  public void putAll(Map<? extends String, ? extends T> map) {
    map.entrySet().forEach((s) -> this.put(s.getKey().toLowerCase(), s.getValue()));
  }

  /** {@inheritDoc} */
  @Override
  public T remove(Object key) {
    return super.remove(key.toString().toLowerCase());
  }
}
