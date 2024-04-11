/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@DisplayName("JDBC API の呼び出し、クローズなど")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TriteDbTest {

  TriteDb db;
  
  @Mock
  DataSource ds;
  @Mock
  Connection conn;
  @Mock
  Statement stmt;
  @Mock
  PreparedStatement prepStmt;
  @Mock
  ParameterMetaData meta;
  @Mock
  ResultSet results;
  @Mock
  ResultSetMetaData resultsMeta;
  
  @BeforeEach
  public void setUp() throws Exception {
    when(ds.getConnection()).thenReturn(conn);
    when(conn.createStatement()).thenReturn(stmt);
    when(conn.prepareStatement(any(String.class), anyInt())).thenReturn(prepStmt);
    when(stmt.getGeneratedKeys()).thenReturn(results);
    when(stmt.executeQuery(any(String.class))).thenReturn(results);
    when(prepStmt.getParameterMetaData()).thenReturn(meta);
    when(prepStmt.getGeneratedKeys()).thenReturn(results);
    when(prepStmt.executeQuery()).thenReturn(results);
    when(prepStmt.executeUpdate()).thenReturn(1);
    when(results.next()).thenReturn(false);
    when(meta.getParameterCount()).thenReturn(2);
    db = new TriteDbImpl(ds);
  }

  @Test
  @DisplayName("INSERT(Connectionなし)")
  public void inset1() throws Exception {
    when(results.next()).thenReturn(true);
    when(results.getLong(anyInt())).thenReturn(11L);
    Number id = db.updateAndKey(sql("insert"), "hoge", "moge");
    assertEquals(11, id.intValue());
    verify(results, times(1)).close();
    verify(prepStmt, times(1)).close();
    verify(conn, times(1)).close();
  }

  @Test
  @DisplayName("INSERT(Connectionあり)")
  public void insert2() throws Exception {
    when(results.next()).thenReturn(true);
    when(results.getLong(anyInt())).thenReturn(11L);
    Number id = new TriteDbImpl().updateAndKey(conn, sql("insert"), "hoge", "moge");
    assertEquals(11, id.intValue());
    verify(results, times(1)).close();
    verify(prepStmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("INSERT(パラメータなし)")
  public void insert3() throws Exception {
    when(results.next()).thenReturn(true);
    when(results.getLong(anyInt())).thenReturn(11L);
    Number id = new TriteDbImpl().updateAndKey(conn, sql("insert"));
    assertEquals(11, id.intValue());
    verify(results, times(1)).close();
    verify(stmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("UPDATE(Connectionなし)")
  public void update1() throws Exception {
    int result = db.update(sql("update"), "hoge", "moge");
    assertEquals(1, result);
    verify(prepStmt, times(1)).executeUpdate();
    verify(prepStmt, times(1)).close();
    verify(conn, times(1)).close();
  }

  @Test
  @DisplayName("UPDATE(Connectionあり)")
  public void update2() throws Exception {
    int result = new TriteDbImpl().update(conn, sql("update"), "hoge", "moge");
    assertEquals(1, result);
    verify(prepStmt, times(1)).executeUpdate();
    verify(prepStmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("一件検索(Connectionなし)")
  public void single1() throws Exception {
    when(results.next()).thenReturn(true);
    when(meta.getParameterCount()).thenReturn(1);
    int i = db.select(Mapper.create((r) -> 1), sql("select_single"), 1);
    assertEquals(i, 1);
    verify(prepStmt, times(1)).executeQuery();
    verify(results, times(1)).next();
    verify(results, times(1)).close();
    verify(prepStmt, times(1)).close();
    verify(conn, times(1)).close();
  }

  @Test
  @DisplayName("一件検索(Connectionあり)")
  public void single2() throws Exception {
    when(results.next()).thenReturn(true);
    when(meta.getParameterCount()).thenReturn(1);
    int i = new TriteDbImpl().select(conn, Mapper.create((r, m) -> 1), sql("select_single"), 1);
    assertEquals(i, 1);
    verify(prepStmt, times(1)).executeQuery();
    verify(results, times(1)).next();
    verify(results, times(1)).close();
    verify(prepStmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("一件検索(パラメータなし)")
  public void single3() throws Exception {
    when(results.next()).thenReturn(false);
    new TriteDbImpl().select(conn, Mapper.create((r) -> 1), sql("select_single"));
    verify(stmt, times(1)).executeQuery(any(String.class));
    verify(results, times(1)).next();
    verify(results, times(1)).close();
    verify(stmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("複数件検索(Connectionなし)")
  public void list1() throws Exception {
    when(results.next()).thenReturn(true, true, false);
    List<Integer> i = db.selectList(Mapper.create((r, m) -> 1), sql("select_list"));
    assertEquals(i.size(), 2);
    verify(stmt, times(1)).executeQuery(any(String.class));
    verify(results, times(3)).next();
    verify(stmt, times(1)).close();
    verify(conn, times(1)).close();
  }

  @Test
  @DisplayName("複数件検索(Connectionあり)")
  public void list2() throws Exception {
    when(meta.getParameterCount()).thenReturn(2);
    when(results.next()).thenReturn(true, true, false);
    List<Integer> i = new TriteDbImpl().selectList(conn, Mapper.create((r) -> 1), sql("select_list"), 1, null);
    assertEquals(i.size(), 2);
    verify(prepStmt, times(1)).executeQuery();
    verify(results, times(3)).next();
    verify(results, times(1)).close();
    verify(prepStmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("ParameterMetaDataが取得できない場合")
  public void list3() throws Exception {
    when(prepStmt.getParameterMetaData()).thenReturn(null);
    when(results.next()).thenReturn(true, true, false);
    List<Integer> i = new TriteDbImpl().selectList(conn, Mapper.create((r) -> 1), sql("select_list"), 1, null);
    assertEquals(i.size(), 2);
    verify(prepStmt, times(1)).executeQuery();
    verify(results, times(3)).next();
    verify(results, times(1)).close();
    verify(prepStmt, times(1)).close();
    verify(conn, never()).close();
  }

  @Test
  @DisplayName("検索系SQLが失敗した場合")
  public void sqlFail1() throws Exception {
    when(stmt.executeQuery(anyString())).thenThrow(new SQLException("hoge"));
    boolean caught = false;
    try {
      db.selectList(Mapper.create((r) -> 1),sql("select"));
      verify(results, times(1)).close();
      verify(stmt, times(1)).close();
      verify(conn, times(1)).close();
    } catch (SQLException e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }

  @Test
  @DisplayName("更新系SQLが失敗した場合")
  public void sqlFail2() throws Exception {
    when(stmt.executeUpdate(any(String.class))).thenThrow(new SQLException("hoge"));
    boolean caught = false;
    try {
      db.update(sql("update"));
      verify(stmt, times(1)).close();
      verify(conn, times(1)).close();
    } catch (SQLException e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }
  

  @Test
  @DisplayName("自動採番値が取得できない場合")
  public void sqlFail3() throws Exception {
    when(results.next()).thenReturn(true);
    when(results.getLong(anyInt())).thenThrow(new SQLException("hoge"));
    boolean caught = false;
    try {
      db.updateAndKey(sql("insert"));
      verify(results, times(1)).close();
      verify(stmt, times(1)).close();
      verify(conn, times(1)).close();
    } catch (SQLException e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }

  @Test
  @DisplayName("パラメータ数が一致しない場合")
  public void parameterFail() throws Exception {
    boolean caught = false;
    try {
      db.select(Mapper.create((r) -> null), sql("select_single"), "hoge", "moge", "boge");
      verify(prepStmt, times(1)).executeQuery();
      verify(results, times(3)).next();
      verify(results, times(1)).close();
      verify(prepStmt, times(1)).close();
      verify(conn, times(1)).close();
    } catch (SQLException e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }
  
  String sql(String key) throws SQLException {
    return "sql";
  }
  
}
