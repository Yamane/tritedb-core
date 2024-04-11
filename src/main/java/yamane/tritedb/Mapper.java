/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import yamane.tritedb.utils.SqlBiFunction;
import yamane.tritedb.utils.SqlFunction;

/**
 * ResultSetを任意のオブジェクトに変換するクラスのインターフェースです.
 * @param <T> 処理結果を格納する型
 */
public interface Mapper<T> {

  /**
   * ResultSetを他のオブジェクトに変換します.
   * @param rs 処理対象となるResultSet
   * @param meta ResultSetの型などの情報
   * @return 処理結果
   * @throws SQLException DBがエラーを通知した場合
   */
  public T map(ResultSet rs, ResultSetMetaData meta) throws SQLException;

  /**
   * ラムダ式を用いてResultSetの変換を行うMapperクラスを生成します.
   * @param <T>　処理結果を格納する型
   * @param f ResultSet変換処理
   * @return　Mapper インスタンス
   */
  public static <T> Mapper<T> create(SqlFunction<ResultSet, T> f) {
    return new LambdaMapper<T>(f);
  }

  /**
   * ラムダ式を用いてResultSetの変換を行うMapperクラスを生成します.
   * @param <T> 処理結果を格納する型
   * @param bif ResultSet変換処理
   * @return　Mapper インスタンス
   */
  public static <T> Mapper<T> create(SqlBiFunction<ResultSet, ResultSetMetaData, T> bif) {
    return new LambdaMapper<T>(bif);
  }

  /**
   * ラムダ式を用いてResultSetの変換を行うMapperです.
   * @param <T> 処理結果を格納する型
   */
  static class LambdaMapper<T> implements Mapper<T> {

    private SqlFunction<ResultSet, T> f;
    private SqlBiFunction<ResultSet, ResultSetMetaData, T> bif;

    private LambdaMapper(SqlFunction<ResultSet, T> f) {
      this.f = f;
    }

    private LambdaMapper(SqlBiFunction<ResultSet, ResultSetMetaData, T> bif) {
      this.bif = bif;
    }

    /** {@inheritDoc} */
    @Override
    public T map(ResultSet rs, ResultSetMetaData meta) throws SQLException {
      return f != null ? f.apply(rs) : bif.apply(rs, meta);
    }
  }
}
