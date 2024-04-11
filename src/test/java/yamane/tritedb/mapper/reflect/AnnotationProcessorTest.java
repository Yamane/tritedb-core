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

@DisplayName("AnnotationProcessorの読み書き等")
public class AnnotationProcessorTest {
  
  AnnotationProcessor<TestBean1> info1;
  AnnotationProcessor<TestBean2> info2;

  @BeforeEach
  public void before() throws SQLException {
    info1 = new AnnotationProcessor<>(TestBean1.class);
    info2 = new AnnotationProcessor<>(TestBean2.class);
  }


  
  @Test
  @DisplayName("Field、Methodの読み書き")
  public void readWriteTest() throws Exception {
    TestBean1 bean1 = info1.newInstance();
    info1.getField("ID").write(bean1, 10);
    info1.getField("name").write(bean1, "test_name");
    assertEquals(bean1.id, info1.getField("id").read(bean1));
    assertEquals(bean1.name, info1.getField("NAME").read(bean1));

    TestBean2 bean2 = info2.newInstance();
    info2.getField("user_id").write(bean2, 100);
    info2.getField("user_name").write(bean2, "hoge moge");
    info2.getField("gender").write(bean2, (short) 2);
    assertEquals(bean2.getId(), info2.getField("user_id").read(bean2));
    assertEquals(bean2.getName(), info2.getField("user_name").read(bean2));
    assertEquals(bean2.getGender(), info2.getField("gender").read(bean2));

    // @Nestの先がNullの場合にもエラーにはならない
    assertEquals(info2.getField("address$zip_code").read(bean2), null);

    info2.getField("address$zip_code").write(bean2, "100-0001");
    info2.getField("address$prefecture_code").write(bean2, 13);
    info2.getField("address$address").write(bean2, "千代田区千代田1番1号");
    assertEquals(bean2.getBean().getZip(), info2.getField("address$zip_code").read(bean2));
    assertEquals(bean2.getBean().getCode(), info2.getField("address$prefecture_code").read(bean2));
    assertEquals(bean2.getBean().getAddress(), info2.getField("address$address").read(bean2));
    
    // @Nestは直接アクセスできない
    assertEquals(info2.getField("address").read(bean2), null);

  }
  
  @Test
  @DisplayName("Field、Methodの取得失敗系")
  public void findErrorTest() {
    assertEquals(info1.containsField("hoge"), false);
    assertEquals(info1.getField("hoge"), null);
  }

  @Test
  @DisplayName("@Nestのループを検出")
  public void nestErrorTest() {
    boolean caught = false;
    try {
      new AnnotationProcessor<>(TestBean4.class);
    } catch (Exception e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }

  static class TestBean1 {
    @Column("id")
    int id;
    @Column("NAME")
    String name;
  }

  @Data
  static class TestBean2 {
    @Column("user_id")
    private Integer id;
    @Column("user_name")
    private String name;
    @Column("gender")
    private Short gender;
    @Nest("address")
    TestBean3 bean;
  }

  @Data
  static class TestBean3 {
    @Column("zip_code")
    private String zip;
    @Column("prefecture_code")
    private int code;
    @Column("address")
    private String address;

  }
  
  @Data
  static class TestBean4 {
    @Nest("nest")
    private TestBean4 nest;
  }
}
