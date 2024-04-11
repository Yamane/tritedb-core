/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * このライブラリのメインとなるインターフェイスです.
 * JDBCを用いてデータベースにアクセスする手続きを簡略化する機能を提供します.
 */
public interface TriteDb extends TriteDbBase {
  
  /**
   * 検索系SQLを実行し、処理結果を１行取得します.
   * @param <T> 処理結果を格納する型
   * @param mapper 処理結果変換クラス
   * @param set SQLセット
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  default <T> T select(Mapper<T> mapper, SqlSet set) throws SQLException {
    return select(mapper, set.getSql(), set.getParams());
  }

  /**
   * 検索系SQLを実行し、処理結果を１行取得します.
   * @param <T> 処理結果を格納する型
   * @param mapper 処理結果変換クラス
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  default <T> T select(Mapper<T> mapper, String sql, Object... params) throws SQLException {
    Connection conn = getConnection();
    try {
      return select(conn, mapper, sql, params);
    } finally {
      close(conn);
    }
  }

  /**
   * 検索系SQLを実行し、処理結果を１行取得します.
   * @param <T> 処理結果を格納する型
   * @param conn コネクション
   * @param mapper 処理結果変換クラス
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  <T> T select(Connection conn, Mapper<T> mapper, String sql, Object... params) throws SQLException;

  /**
   * 検索系SQLを実行し、複数行の処理結果を取得します.
   * @param <T> 処理結果を格納する型
   * @param mapper 処理結果変換クラス
   * @param set SQLセット
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  default <T> List<T> selectList(Mapper<T> mapper, SqlSet set) throws SQLException {
    return selectList(mapper, set.getSql(), set.getParams());
  }
  
  /**
   * 検索系SQLを実行し、複数行の処理結果を取得します.
   * @param <T> 処理結果を格納する型
   * @param mapper 処理結果変換クラス
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  default <T> List<T> selectList(Mapper<T> mapper, String sql, Object... params) throws SQLException {
    Connection conn = getConnection();
    try {
      return selectList(conn, mapper, sql, params);
    } finally {
      close(conn);
    }
  }

  /**
   * 検索系SQLを実行し、複数行の処理結果を取得します.
   * @param <T> 処理結果を格納する型
   * @param conn コネクション
   * @param mapper 処理結果変換クラス
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 検索結果
   * @throws SQLException DBがエラーを通知した場合
   */
  <T> List<T> selectList(Connection conn, Mapper<T> mapper, String sql, Object... params) throws SQLException;

  /**
   * 更新系SQLを実行します.
   * @param set SQLセット
   * @return 処理行数
   * @throws SQLException DBがエラーを通知した場合
   */
  default int update(SqlSet set) throws SQLException {
    return update(set.getSql(), set.getParams()); 
  }
  
  /**
   * 更新系SQLを実行します.
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 処理行数
   * @throws SQLException DBがエラーを通知した場合
   */
  default int update(String sql, Object... params) throws SQLException {
    Connection conn = getConnection();
    try {
      return update(conn, sql, params);
    } finally {
      close(conn);
    }
  }

  /**
   * 更新系SQLを実行します.
   * @param conn コネクション
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 処理行数
   * @throws SQLException DBがエラーを通知した場合
   */
  int update(Connection conn, String sql, Object... params) throws SQLException;

  /**
   * 更新系SQLを実行し、自動採番値を取得します.
   * @param set SQLセット
   * @return 自動採番値
   * @throws SQLException DBがエラーを通知した場合
   */
  default Number updateAndKey(SqlSet set) throws SQLException {
    return updateAndKey(set.getSql(), set.getParams());
  }
  
  /**
   * 更新系SQLを実行し、自動採番値を取得します.
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 自動採番値
   * @throws SQLException DBがエラーを通知した場合
   */
  default Number updateAndKey(String sql, Object... params) throws SQLException {
    Connection conn = getConnection();
    try {
      return updateAndKey(conn, sql, params);
    } finally {
      close(conn);
    }
  }

  /**
   * 更新系SQLを実行し、自動採番値を取得します.
   * @param conn コネクション
   * @param sql 実行するSQL
   * @param params SQL実行時の可変パラメータ
   * @return 自動採番値
   * @throws SQLException DBがエラーを通知した場合
   */
  Number updateAndKey(Connection conn, String sql, Object... params) throws SQLException;

}