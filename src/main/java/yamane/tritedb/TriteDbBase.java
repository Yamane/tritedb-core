/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.sql.DataSource;

/**
 * TriteDbで扱うJDBCの基礎部分の処理を定義したインターフェイスです.
 */
public interface TriteDbBase {

  /** エラーメッセージ等を扱うためのリソースバンドル */
  final static ResourceBundle bundle = ResourceBundle.getBundle("TriteDb");
  
  /**
   * DataSourceを設定します.
   * @param dataSource　DBアクセスに使用するDataSource
   */
  void setDataSource(DataSource dataSource);

  /**
   * 格納している DataSource を返します.
   * @return 格納している DataSource
   */
  DataSource getDataSource();

  /**
   * DataSourceからConnectionを取得します.
   * @return コネクション
   * @throws SQLException DBがエラーを通知した場合
   */
  default Connection getConnection() throws SQLException {
    return getDataSource().getConnection();
  }
  
  /**
   * Connectionをクローズします.
   * @param conn コネクション
   * @throws SQLException DBがエラーを通知した場合
   */
  default void close(Connection conn) throws SQLException {
    conn.close();
  }

  /**
   * Statement を生成します.
   * @param conn コネクション
   * @return PreparedStatement
   * @throws SQLException DBがエラーを通知した場合
   */
  default Statement statement(Connection conn) throws SQLException {
    return conn.createStatement();
  }

  /**
   * PreparedStatement を生成してパラメータをセットします.
   * @param conn コネクション
   * @param sql 実行するSQL
   * @param key 自動採番値を取得するかどうか
   * @param prams 埋め込む値
   * @return PreparedStatement
   * @throws SQLException DBがエラーを通知した場合
   */
  PreparedStatement statement(Connection conn, String sql, int key, Object... prams) throws SQLException;
  
  /**
   * SQLExceptionにSQL実行時情報を付加します.
   * @param cause 原因となった例外
   * @param sql 例外発生時のSQL
   * @param params 例外発生時のパラメータ
   * @return SQLException DBがエラーを通知した場合
   */
  default SQLException wrap(SQLException cause, String sql, Object... params) {
    String causeMessage = cause.getMessage() == null ? getStr("blankMessage") : cause.getMessage();
    String parameters = params == null ? "[]" : Arrays.deepToString(params);
    String msg = format("sqlException", causeMessage == null ? "" : causeMessage, sql, parameters);
    SQLException e = new SQLException(msg, cause.getSQLState(), cause.getErrorCode());
    e.setNextException(cause);
    return e;
  }
  
  /**
   * 実行するSQLとパラメータのセットです.
   */
  static class SqlSet {
    private String sql;
    private Object[] params;

    /**
    * @param sql 実行するSQL
    * @param params SQL実行時の可変パラメータ
    */
    public SqlSet(String sql, Object... params) {
      this.sql = sql;
      this.params = params;
    }

    /**
     * 実行するSQLを取得します.
     * @return 実行するSQL
     */
    public String getSql() {
      return sql;
    }

    /**
     * SQL実行時の可変パラメータを取得します.
     * @return SQL実行時の可変パラメータ
     */
    public Object[] getParams() {
      return params;
    }
  }
  
  /**
   * ResourceBundle(TriteDb.properties)からメッセージを取得します.
   * @param key 取得するメッセージのキー
   * @return メッセージ
   */
  static String getStr(String key) {
    return bundle.getString(key);
  }

  /**
   * ResourceBundle(TriteDb.properties)からメッセージを取得し、フォーマットします.
   * @param key 取得するメッセージのキー
   * @param params メッセージに埋め込むパラメータ
   * @return メッセージ
   */
  static String format(String key, Object... params) {
    return String.format(getStr(key), params);
  }
}
