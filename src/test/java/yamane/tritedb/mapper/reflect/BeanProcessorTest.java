/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.Data;

@DisplayName("BeanProcessorの読み書き等")
public class BeanProcessorTest {
  
  BeanProcessor<TestBean1> info1;

  @BeforeEach
  public void before() throws SQLException {
    info1 = new BeanProcessor<>(TestBean1.class);
  }

  @Test
  @DisplayName("Field、Methodの読み書き")
  public void readWriteTest() throws Exception {
    TestBean1 bean1 = info1.newInstance();
    info1.write(bean1, "userId", 10);
    info1.write(bean1, "user_name", "test_name");
    assertEquals(bean1.userId, info1.read(bean1, "USER_ID"));
    assertEquals(bean1.userName, info1.read(bean1, "username"));

    // プロパティがなくてもエラーにはならない
    info1.write(bean1, "hoge", 10);
    info1.read(bean1, "hoge");
  }

  @Data
  static class TestBean1 {
    private Integer userId;
    private String userName;
    private Short gender;
  }
}
