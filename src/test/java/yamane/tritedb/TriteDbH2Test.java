/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("H2を使って一通りのアクセス")
public class TriteDbH2Test extends AbstractTest {

  Mapper<TestDto> mapper;

  class TestDto {
    Integer id;
    String name1;
    String name2;

    @Override
    public String toString() {
      return String.format("id:%d name1:%s name2:%s", id, name1, name2);
    }
  }

  @BeforeEach
  public void setUp() throws ClassNotFoundException {
    init();
    mapper = Mapper.create((r) -> {
      TestDto dto = new TestDto();
      dto.id = r.getInt("id");
      dto.name1 = r.getString("name1");
      dto.name2 = r.getString("name2");
      return dto;
    });
  }

  @Test
  public void test() throws SQLException {
    // テーブルつくって
    assertEquals(0, db.update(sql("tritedb", "create")));
    // 3行追加して
    assertEquals(1L, db.updateAndKey(sql("tritedb", "insert"), "h1", "test1"));
    assertEquals(2L, db.updateAndKey(sql("tritedb", "insert"), "h2", "test2"));
    assertEquals(3L, db.updateAndKey(sql("tritedb", "insert"), "h3", "test3"));
    // 3行検索して
    List<TestDto> list = db.selectList(mapper, sql("tritedb", "select_list"));
    assertEquals(3, list.size());
    for (int i = 1; i <= list.size(); i++) {
      TestDto d = list.get(i - 1);
      assertEquals(d.toString(), String.format("id:%d name1:h%d name2:test%d", i, i, i));
    }
    // 1行更新して
    assertEquals(1, db.update(sql("tritedb", "update"), "h4", "test4", 1));
    // 1行取得する
    TestDto d = db.select(mapper, sql("tritedb", "select_single"), 1);
    assertEquals(d.toString(), String.format("id:%d name1:h%d name2:test%d", 1, 4, 4));

    // テーブル削除
    assertEquals(0, db.update(sql("tritedb", "drop")));
  }
}
