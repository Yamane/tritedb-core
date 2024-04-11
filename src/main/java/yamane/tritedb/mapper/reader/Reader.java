/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSetから指定の型でデータを取得するためのインターフェイスです.
 * @param <T> ResultSetから取得するデータ型
 */
public interface Reader<T> {

  /**
   * 指定された型にこのReaderが使用できるかどうか判別します.
   * @param clazz 処理結果を格納する型
   * @return 利用可否
   */
  Boolean match(Class<?> clazz);
  
  /**
   * ResulltSetからカラムのデータを取得します.
   * @param rs 処理対象となるResultSet
   * @param index インデックス
   * @return　ResultSetから取得したカラムの値
   * @throws SQLException DBがエラーを通知した場合
   */
  T read(ResultSet rs, Integer index) throws SQLException;


}
