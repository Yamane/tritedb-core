/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import yamane.tritedb.Mapper;
import yamane.tritedb.utils.LowerKeyMap;

/**
 * ResultSetをMapに変換するMapperです.
 * Mapのキーとなるカラム名はすべて小文字として処理されます.
 */
public class MapMapper implements Mapper<Map<String, Object>> {

  private static MapMapper INSTANCE = new MapMapper();

  /**
   * インスタンスを取得します.
   * @return インスタンス
   */
  public static Mapper<Map<String, Object>> instance() {
    return INSTANCE;
  }

  private MapMapper() {
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, Object> map(ResultSet rs, ResultSetMetaData meta) throws SQLException {
    Map<String, Object> result = new LowerKeyMap<Object>();
    int cols = meta.getColumnCount();
    for (int i = 1; i <= cols; i++) {
      result.put(meta.getColumnLabel(i), rs.getObject(i));
    }
    return result;
  }
}
