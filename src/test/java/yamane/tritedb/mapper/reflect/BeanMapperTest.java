/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.Data;
import yamane.tritedb.Mapper;
import yamane.tritedb.mapper.AbstractMapperTest;
import yamane.tritedb.mapper.BeanMapper;
import yamane.tritedb.mapper.reflect.BeanMapperTest.Fields;
import yamane.tritedb.testdata.TestBean;

@DisplayName("BeanMapperを使ったDBアクセス")
public class BeanMapperTest extends AbstractMapperTest<Fields> {

  @BeforeEach
  public void before() throws SQLException {
    super.before();
    db.update(sql("reader", "create"));
    db.update(sql("reader", "insert"));
  }

  @AfterEach
  public void after() throws SQLException {
    super.after();
    db.update(sql("reader", "drop"));
  }

  @Test
  public void test() throws Exception {
    super.testSingle();
    super.testList();
    
    TestBean bean = db.select(BeanMapper.instance(TestBean.class), sql("reader", "select"));
    assertEquals(bean.getId().longValue(), 1L);
    assertEquals(bean.getName(), "hoge moge");
    assertEquals(bean.getGender().label(), "男性");
    assertEquals(bean.getBirthday().toString(), "2000-01-01");
    assertEquals(bean.isMarried(), true);
    assertEquals(bean.getIncome().intValue(), 4000000);
    assertEquals(bean.getTaxRate().toString(), "0.1");
    assertEquals(bean.getWackupTime().toString(), "07:00");
    assertEquals(bean.getRemarks(), "あいうえおかきくけこ");
    assertNotNull(bean.getCreateDate());
    assertNotNull(bean.getUpdateDate());
  }

  @Override
  protected Mapper<Fields> mapper() throws SQLException {
    return BeanMapper.instance(Fields.class);
  }

  @Override
  protected String toString(Fields f) {
    return String.format("id:%d name1:%s name2:%s", f.id, f.getName1(), f.getName2());
  }

  @Data
  static class Fields {
    private Integer id;
    private String name1;
    private String name2;
  }
}
