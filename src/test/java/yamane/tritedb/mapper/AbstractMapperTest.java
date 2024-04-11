/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import yamane.tritedb.AbstractTest;
import yamane.tritedb.Mapper;

public abstract class AbstractMapperTest<T> extends AbstractTest {

  protected void before() throws SQLException {
    init();
    db.update(sql("tritedb", "create"));
    db.updateAndKey(sql("tritedb", "insert"), "h1", "test1");
    db.updateAndKey(sql("tritedb", "insert"), "h2", "test2");
    db.updateAndKey(sql("tritedb", "insert"), "h3", "test3");
  }

  protected T testSingle() throws SQLException {
    T m = db.select(mapper(), sql("tritedb", "select_single"), 2);
    assertEquals(toString(m), String.format("id:%d name1:h%d name2:test%d", 2, 2, 2));
    return m;
  }

  protected List<T> testList() throws SQLException {
    List<T> list = db.selectList(mapper(), sql("tritedb", "select_list"));
    assertEquals(3, list.size());
    for (int i = 1; i <= list.size(); i++) {
      assertEquals(toString(list.get(i - 1)), String.format("id:%d name1:h%d name2:test%d", i, i, i));
    }
    return list;
  }

  protected void after() throws SQLException {
    db.update(sql("tritedb", "drop"));
  }

  protected abstract Mapper<T> mapper() throws SQLException;

  protected abstract String toString(T t);
}
