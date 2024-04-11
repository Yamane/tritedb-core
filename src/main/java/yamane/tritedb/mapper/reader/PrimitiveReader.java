/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import yamane.tritedb.utils.SqlBiFunction;

/**
 * プリミティブ型を持つObjectのフィールドに対するデータを読み込むためのReaderです.
 * データがNullのときにちゃんとNullが取得できることを目的としています.
 * @param <T> ResultSetから取得するデータ型
 */
public class PrimitiveReader<T> extends ReaderImpl<T> {

  /**
   * コンストラクタ
   * @param clazz EnumExインターフェイスを持つenum
   */
  public PrimitiveReader(Class<T> clazz, SqlBiFunction<ResultSet, Integer, T> read) {
    super(clazz, read);
  }

  @Override
  public T read(ResultSet rs, Integer index) throws SQLException {
    T result = read.apply(rs, index);
    return rs.wasNull() ? null : result;
  }


}
