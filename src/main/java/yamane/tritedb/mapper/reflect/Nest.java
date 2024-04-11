/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapperがカラムをクラスにセットする際のガイドとなるエイリアスです.
 * 対象となるクラスはデフォルトコンストラクターを持っている必要があります.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface Nest {

  /**
   * ネスト時のエイリアス.
   * @return エイリアス
   */
  public String value();
}
