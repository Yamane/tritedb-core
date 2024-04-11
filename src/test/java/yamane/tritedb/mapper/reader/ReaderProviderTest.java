/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import yamane.tritedb.AbstractTest;
import yamane.tritedb.Mapper;
import yamane.tritedb.mapper.AnnotationMapper;
import yamane.tritedb.testdata.TestBean;

public class ReaderProviderTest extends AbstractTest{

  @BeforeEach
  public void before() throws SQLException {
    init();
    db.update(sql("reader", "create"));
    db.update(sql("reader", "insert"));
    ReaderProvider.register();
  }
  
  @Test
  public void test() throws SQLException {
    Mapper<TestBean> mapper = AnnotationMapper.instance(TestBean.class);
    TestBean bean = db.select(mapper, sql("reader", "select"));
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
    
    assertEquals(db.update("update readertest set married=null,income=null"), 1);
    bean = db.select(mapper, sql("reader", "select"));
    assertNull(bean.getIncome());
    assertEquals(bean.isMarried(), false);
  }
  
  @AfterEach
  public void after() throws Exception {
    db.update(sql("reader", "drop"));
  }

}
