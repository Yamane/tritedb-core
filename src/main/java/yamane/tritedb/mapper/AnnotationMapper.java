/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import yamane.tritedb.Mapper;
import yamane.tritedb.mapper.reflect.AnnotationProcessor;

/**
 * ResultSetのデータをアノテーションを持つフィールドに設定するMapperです.
 * <code>@Column</code>アノテーションを持つフィールドを走査し、まずSetterがあればSetterを用いて値を設定し、なければパブリックフィールドへの書き込みを実施します.
 * <code>@Nest</code>アノテーションを持つフィールドがある場合、そのフィールドのクラス内も走査の対象となります.
 * @param <T> 処理結果を格納するクラス.デフォルトコンストラクターを持つことが必須となります.
 */
public class AnnotationMapper<T> implements Mapper<T> {

  /** アノテーションを処理するためのクラス */
  private AnnotationProcessor<T> info;

  /**
   * インスタンスを生成します.
   * @param clazz 処理結果を格納するクラス.
   * @throws SQLException 対象クラスに同一クラスの<code>@Nest</code>が設定されていた場合
   */
  private AnnotationMapper(Class<T> clazz) throws SQLException {
    this.info = new AnnotationProcessor<>(clazz);
  }

  /** {@inheritDoc} */
  @Override
  public T map(ResultSet rs, ResultSetMetaData meta) throws SQLException {
    T result = info.newInstance();
    int cols = meta.getColumnCount();
    for (int col = 1; col <= cols; col++) {
      String name = meta.getColumnLabel(col);
      if (info.containsField(name)) {
        info.getField(name).write(result, rs, col);
      }
    }
    return result;
  }
  
  private static Map<Class<?>, AnnotationMapper<?>> mappers = new HashMap<>();

  /**
   * インスタンスを取得します.
   * @param <T> 処理結果を格納するクラス
   * @param clazz 処理結果を格納するクラス
   * @return 指定されたクラスに対応するMapper
   * @throws SQLException 対象クラスに同一クラスの<code>@Nest</code>が設定されていた場合
   */
  @SuppressWarnings("unchecked")
  public static <T> Mapper<T> instance(Class<T> clazz) throws SQLException {
    if(!AnnotationMapper.mappers.containsKey(clazz)) {
      AnnotationMapper.mappers.put(clazz, new AnnotationMapper<T>(clazz));
    }
    return (Mapper<T>) AnnotationMapper.mappers.get(clazz);
  }
}
