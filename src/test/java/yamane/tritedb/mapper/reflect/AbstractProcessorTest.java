/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import yamane.tritedb.mapper.reflect.AbstractProcessorTest.TestBeanA1;
import yamane.tritedb.utils.LazyConsumer;

@DisplayName("AbstractProcessorの処理")
public class AbstractProcessorTest extends AbstractProcessor<TestBeanA1> {

  protected AbstractProcessorTest() {
    super(TestBeanA1.class);
  }
  
  @Test
  @DisplayName("インスタンス化")
  public void newInstanceErrorTest()throws Exception {
    failTest(b -> new AnnotationProcessor<>(TestBeanI1.class).newInstance());
    failTest(b -> new AnnotationProcessor<>(TestBeanI2.class).newInstance());
    failTest(b -> new AnnotationProcessor<>(TestBeanI3.class).newInstance());
  }
  
  @Test
  @DisplayName("フィールドの読み書き")
  public void fieldTest() throws Exception {
    TestBeanA1 a1 = new TestBeanA1();
    write(a1, clazz.getDeclaredField("id"), 1);
    assertEquals(read(a1, clazz.getDeclaredField("id")), 1);
    failTest(b -> write(a1, clazz.getDeclaredField("name"), "hoge"));
    failTest(b -> read(a1, clazz.getDeclaredField("name")));
  }
  
  @Test
  @DisplayName("メソッドの読み書き")
  public void methodTest() throws Exception {
    TestBeanA1 a1 = new TestBeanA1();
    write(a1, clazz.getDeclaredMethod("setId", Integer.class), 1);
    assertEquals(read(a1, clazz.getDeclaredMethod("getId")), 1);
    failTest(b -> write(this, clazz.getDeclaredMethod("setName", String.class), "hoge"));
    failTest(b -> read(this, clazz.getDeclaredMethod("getName")));
  }
  
  void failTest(LazyConsumer<Boolean> c) {
    boolean caught = false;
    try {
      c.accept(true);
    } catch (Exception e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }


  public static class TestBeanA1 {
    public Integer id;
    private String name;
    
    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    @SuppressWarnings("unused")
    private String getName() {
      return name;
    }
    
    @SuppressWarnings("unused")
    private void setName(String name) {
      this.name = name;
    }
  }
  
  static class TestBeanI1 {
    private TestBeanI1() {
    }
  }
  
  static class TestBeanI2 {
    public TestBeanI2() {
      throw new RuntimeException();
    }
  }
  
  static class TestBeanI3 {
    public TestBeanI3(Boolean bool) {
    }
  }
}
