/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.Data;
import yamane.tritedb.Mapper;
import yamane.tritedb.mapper.AbstractMapperTest;
import yamane.tritedb.mapper.AnnotationMapper;
import yamane.tritedb.mapper.reflect.AnnotationMapperTest.Fields;

@DisplayName("AnnotationMapperを使ったDBアクセス")
public class AnnotationMapperTest extends AbstractMapperTest<Fields> {

  @BeforeEach
  public void before() throws SQLException {
    super.before();
  }

  @AfterEach
  public void after() throws SQLException {
    super.after();
  }

  @Test
  public void test() throws Exception {
    super.testSingle();
    super.testList();
  }

  @Override
  protected Mapper<Fields> mapper() throws SQLException {
    return AnnotationMapper.instance(Fields.class);
  }

  @Override
  protected String toString(Fields f) {
    return String.format("id:%d name1:%s name2:%s", f.id, f.str1, f.str2);
  }

  @Data
  static class Fields {
    @Column("id")
    private Integer id;

    @Column("name1")
    private String str1;

    @Column("name2")
    private String str2;

    @Column("nest")
    private NesetField nest;
  }

  @Data
  static class NesetField {
    @Column("name1")
    private String str1;
  }
}
