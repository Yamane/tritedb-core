/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;


import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.FileSystems;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("テキストファイルの読み出し")
public class SqlFileReaderTest {
  
  SqlFileReader reader;
  
  @BeforeEach
  public void setUp() throws Exception {
    SqlFileReader.init();
    SqlFileReader.init(System.getProperty("user.dir"));
    SqlFileReader.init(FileSystems.getDefault().getPath("src/test/resources/sql"));
    reader = SqlFileReader.instance();
  }
  
  @Test
  public void test()throws Exception {
    assertEquals(reader.read("test.txt"), "testtext");
    
    boolean caught = false;
    try {
      reader.read("hoge.txt");
    } catch (SQLException e) {
      caught = true;
    }
    if (!caught) {
      fail("例外が出るのが正しい");
    }
  }
}
