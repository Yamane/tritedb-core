package yamane.tritedb.mapper;
/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import yamane.tritedb.Mapper;
import yamane.tritedb.mapper.reflect.BeanProcessor;

/**
 * ResultSetのデータをJavaBeanのプロパティに設定するMapperです.
 * カラムとプロパティの紐づけは、
 * <ul>
 * <li>toLowerCase()された結果の一致</li>
 * <li>スネークケース：キャメルケース変換後、toLowerCase()された結果の一致</li>
 * </ul>
 * で行われます.
 * @param <T> 処理結果を格納するクラス.デフォルトコンストラクターを持つことが必須となります.
 */
public class BeanMapper<T> implements Mapper<T> {

  /** Beanを処理するためのクラス */
  private BeanProcessor<T> info;

  /**
   * インスタンスを生成します.
   * @param clazz 処理結果を格納するクラス.
   * @throws SQLException 対象クラスに同一クラスの<code>@Nest</code>が設定されていた場合
   */
  private BeanMapper(Class<T> clazz) throws SQLException {
    this.info = new BeanProcessor<>(clazz);
  }

  /** {@inheritDoc} */
  @Override
  public T map(ResultSet rs, ResultSetMetaData meta) throws SQLException {
    T result = info.newInstance();
    int cols = meta.getColumnCount();
    for (int col = 1; col <= cols; col++) {
      info.write(result, meta.getColumnLabel(col), rs, col);
    }
    return result;
  }
  
  private static Map<Class<?>, BeanMapper<?>> mappers = new HashMap<>();

  /**
   * インスタンスを取得します.
   * @param <T> 処理結果を格納するクラス
   * @param clazz 処理結果を格納するクラス
   * @return 指定されたクラスに対応するMapper
   * @throws SQLException 対象クラスに同一クラスの<code>@Nest</code>が設定されていた場合
   */
  @SuppressWarnings("unchecked")
  public static <T> Mapper<T> instance(Class<T> clazz) throws SQLException {
    if(!BeanMapper.mappers.containsKey(clazz)) {
      BeanMapper.mappers.put(clazz, new BeanMapper<T>(clazz));
    }
    return (Mapper<T>) BeanMapper.mappers.get(clazz);
  }
}
