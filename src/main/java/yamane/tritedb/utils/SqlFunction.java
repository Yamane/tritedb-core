/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;

import java.sql.SQLException;

/**
 * SQLExceptionを許容するFunction です.
 * @param <T> 第1引数の型
 * @param <R> 処理結果の型
 */
@FunctionalInterface
public interface SqlFunction<T, R> {

  /**
   * 処理を実施します.
   * @param t 第1引数
   * @return 処理結果
   * @throws SQLException DBがエラーを通知した場合
   */
  R apply(T t) throws SQLException;
}
