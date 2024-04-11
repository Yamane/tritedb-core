/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * TriteDbのデフォルト実装クラスです.
 */
public class TriteDbImpl extends TriteDbBaseImpl implements TriteDb {

  /**
   * デフォルトコンストラクタ.
   * TriteDb インスタンスを生成します.
   */
  public TriteDbImpl() {
    this(null);
  }

  /**
   * コンストラクタ.
   * 使用するDataSourceを指定してTriteDbインスタンスを生成します.
   * @param dataSource　DBアクセスに使用するDataSource
   */
  public TriteDbImpl(DataSource dataSource) {
    setDataSource(dataSource);
  }

  /** {@inheritDoc} */
  @Override
  public <T> T select(Connection conn, Mapper<T> mapper, String sql, Object... params) throws SQLException {
    try {
      if (params == null || params.length == 0) {
        try (Statement stmt = statement(conn)) {
          try (ResultSet rs = stmt.executeQuery(sql)) {
            return single(rs, mapper);
          }
        }
      } else {
        try (PreparedStatement stmt = statement(conn, sql, Statement.NO_GENERATED_KEYS, params)) {
          try (ResultSet rs = stmt.executeQuery()) {
            return single(rs, mapper);
          }
        }
      }
    } catch (SQLException e) {
      throw wrap(e, sql, params);
    }
  }

  /**
   * Mapperを使って単行を処理結果に変換する共通処理です.
   * @param <T> 処理結果を格納する型
   * @param rs ResultSet
   * @param mapper 処理結果変換クラス
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  protected <T> T single(ResultSet rs, Mapper<T> mapper) throws SQLException {
    ResultSetMetaData metaData = rs.getMetaData();
    if (rs.next()) {
      return mapper.map(rs, metaData);
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public <T> List<T> selectList(Connection conn, Mapper<T> mapper, String sql, Object... params) throws SQLException {
    List<T> result = new ArrayList<T>();
    try {
      if (params == null || params.length == 0) {
        try (Statement stmt = statement(conn)) {
          try (ResultSet resultSet = stmt.executeQuery(sql)) {
            return list(result, resultSet, mapper);
          }
        }
      } else {
        try (PreparedStatement stmt = statement(conn, sql, Statement.NO_GENERATED_KEYS, params)) {
          try (ResultSet resultSet = stmt.executeQuery()) {
            return list(result, resultSet, mapper);
          }
        }
      }
    } catch (SQLException e) {
      throw wrap(e, sql, params);
    }
  }
  
  /**
   * Mapperを使って複数行を処理結果に変換する共通処理です.
   * @param <T> 処理結果を格納する型
   * @param rs ResultSet
   * @param mapper 処理結果変換クラス
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  protected <T> List<T> list(List<T> result, ResultSet rs, Mapper<T> mapper) throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    while (rs.next()) {
      result.add(mapper.map(rs, meta));
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public int update(Connection conn, String sql, Object... params) throws SQLException {
    try {
      if (params == null || params.length == 0) {
        try (Statement stmt = statement(conn)) {
          return stmt.executeUpdate(sql);
        }
      } else {
        try (PreparedStatement stmt = statement(conn, sql, Statement.NO_GENERATED_KEYS, params)) {
          return stmt.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw wrap(e, sql, params);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Number updateAndKey(Connection conn, String sql, Object... params) throws SQLException {
    try {
      if (params == null || params.length == 0) {
        try (Statement stmt = statement(conn)) {
          stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
          try (ResultSet keys = stmt.getGeneratedKeys()) {
            return Mapper.create((r) -> r.next() ? r.getLong(1) : null).map(keys, null);
          }
        }
      } else {
        try (PreparedStatement stmt = statement(conn, sql, Statement.RETURN_GENERATED_KEYS, params)) {
          stmt.executeUpdate();
          try (ResultSet keys = stmt.getGeneratedKeys()) {
            return Mapper.create((r) -> r.next() ? r.getLong(1) : null).map(keys, null);
          }
        }
      }
    } catch (SQLException e) {
      throw wrap(e, sql, params);
    }
  }
}
