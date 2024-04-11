/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBCドライバの型変換処理を用いてデータを取得するReaderです.
 * @param <T> ResultSetから取得するデータ型
 */
public class ObjectReader<T> implements Reader<T> {

  private Class<T> clazz;

  /**
   * コンストラクタ.
   * @param clazz ResultSetから取得するデータ型
   */
  public ObjectReader(Class<T> clazz) {
    this.clazz = clazz;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean match(Class<?> clazz) {
    return this.clazz.equals(clazz);
  }

  /** {@inheritDoc} */
  @Override
  public T read(ResultSet rs, Integer index) throws SQLException {
    return rs.getObject(index, clazz);
  }
}
