/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper;

import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import yamane.tritedb.Mapper;

@DisplayName("MapMapperを使ったDBアクセス")
public class MapMapperTest extends AbstractMapperTest<Map<String, Object>> {

  @BeforeEach
  public void before() throws SQLException {
    super.before();
  }

  @AfterEach
  public void after() throws SQLException {
    super.after();
  }

  @Test
  public void test() throws SQLException {
    super.testSingle();
    super.testList();
  }

  @Override
  protected Mapper<Map<String, Object>> mapper() throws SQLException {
    return MapMapper.instance();
  }

  @Override
  protected String toString(Map<String, Object> m) {
    return String.format("id:%d name1:%s name2:%s", m.get("ID"), m.get("NAME1"), m.get("NAME2"));
  }

}
