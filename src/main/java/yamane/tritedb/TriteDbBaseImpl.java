/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import static yamane.tritedb.TriteDbBase.*;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

/**
 * TriteDbで扱うJDBCの基礎部分の処理の実装クラスです.
 */
public abstract class TriteDbBaseImpl implements TriteDbBase {

  /** データソース */
  protected DataSource dataSource;

  protected boolean metaDataFaild = false;
  
  /** {@inheritDoc} */
  @Override
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /** {@inheritDoc} */
  @Override
  public DataSource getDataSource() {
    return this.dataSource;
  }

  /** {@inheritDoc} */
  @Override
  public PreparedStatement statement(Connection conn, String sql, int key, Object... prams) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(sql, key);
    setParameters(stmt, sql, prams);
    return stmt;
  }

  /**
   * PreparedStatement のパラメータを埋めます.
   * @param stmt 処理対象となる PreparedStatement
   * @param sql 実行するSQL
   * @param params 埋め込む値
   * @throws SQLException DBがエラーを通知した場合
   */
  protected void setParameters(PreparedStatement stmt, String sql, Object... params) throws SQLException {
    int paramsCount = params == null ? 0 : params.length;
    ParameterMetaData meta = null;
    if(!metaDataFaild) {
      try {
        meta = stmt.getParameterMetaData();
      } catch (SQLException e) {
        metaDataFaild = true;
      }
    }
    if (meta != null) {
      int sqlCount = meta.getParameterCount();
      if (meta.getParameterCount() != params.length) {
        SQLException cause = new SQLException(format("setParameters", sqlCount, paramsCount));
        throw wrap(cause, sql, params);
      }
      for (int i = 0; i < paramsCount; i++) {
        if (params[i] != null) {
          stmt.setObject(i + 1, params[i]);
        } else {
          if(!metaDataFaild) {
            try {
              stmt.setNull(i + 1, meta.getParameterType(i + 1));
            } catch (SQLException e) {
              metaDataFaild = true;
              setNullUnknownType(stmt, i + 1);
            }
          } else {
            setNullUnknownType(stmt, i + 1);
          }
        }
      }
    } else {
      for (int i = 0; i < paramsCount; i++) {
        if (params[i] != null) {
          stmt.setObject(i + 1, params[i]);
        } else {
          setNullUnknownType(stmt, i + 1);
        }
      }
    }
  }

  /**
   * パラメータの型が取得できなかった際のNULL挿入処理を行います.
   * DBによって挙動が違うので、対応が必要な場合はこのメソッドをオーバーライドしてください.
   * @param stmt 処理対象となる PreparedStatement
   * @param index　埋め込み位置
   * @throws SQLException DBがエラーを通知した場合
   */
  protected void setNullUnknownType(PreparedStatement stmt, int index) throws SQLException {
    stmt.setNull(index, Types.NULL);
  }
}
