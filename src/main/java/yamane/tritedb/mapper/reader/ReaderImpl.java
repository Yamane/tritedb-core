/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */
/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import yamane.tritedb.utils.SqlBiFunction;

/**
 * Readerインターフェイスの実装クラスです.
 * @param <T> ResultSetから取得するデータ型
 */
public class ReaderImpl<T> implements Reader<T> {

  private Class<T> clazz;
  private Function<Class<?>, Boolean> match;
  protected SqlBiFunction<ResultSet, Integer, T> read;
  
  /**
   * ラムダ式を用いてResultSetからデータ取得処理を行うReaderを生成します.
   * @param clazz ResultSetから取得するデータ型
   * @param read ResultSetからデータを取得する処理
   */
  public ReaderImpl(Class<T> clazz, SqlBiFunction<ResultSet, Integer, T> read) {
    this(clazz, null, read);
  }
  
  /**
   * ラムダ式を用いてResultSetからデータ取得処理と、適用判定を行うReaderを生成します.
   * @param match このReaderが適用できるかどうかの判定
   * @param read ResultSetからデータを取得する処理
   */
  public ReaderImpl(Function<Class<?>, Boolean> match, SqlBiFunction<ResultSet, Integer, T> read) {
    this(null, match, read);
  }

  /**
   * ラムダ式を用いてResultSetからデータ取得処理と、適用判定を行うReaderを生成します.
   * @param clazz clazzResultSetから取得するデータ型
   * @param match このReaderが適用できるかどうかの判定
   * @param read ResultSetからデータを取得する処理
   */
  public ReaderImpl(Class<T> clazz, Function<Class<?>, Boolean> match, SqlBiFunction<ResultSet, Integer, T> read) {
    this.clazz = clazz;
    this.read = read;
    this.match = match;
  }
  
  /** {@inheritDoc} */
  @Override
  public Boolean match(Class<?> clazz) {
    if(match == null) {
      return this.clazz.equals(clazz) || this.clazz.isAssignableFrom(clazz);
    } else {
      return match.apply(clazz);
    }
  }

  /** {@inheritDoc} */
  @Override
  public T read(ResultSet rs, Integer index) throws SQLException {
    return read.apply(rs, index);
  }


}
