/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import yamane.utils.EnumEx;

/**
 * EnumExインターフェイスを持つenumのフィールドに対するデータを読み込むためのReaderです.
 * @param <T> EnumExインターフェイスを持つenum
 */
public class EnumExReader<T extends EnumEx> implements Reader<T> {

  private Class<T> clazz;

  /**
   * コンストラクタ
   * @param clazz EnumExインターフェイスを持つenum
   */
  public EnumExReader(Class<T> clazz) {
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
    Number value = rs.getObject(index, Integer.class);
    if(value != null) {
      T[] values = clazz.getEnumConstants();
      for (T ex : values) {
        if(ex.value().equals(value)) {
          return ex;
        }
      }
    }
    return null;
  }
}
