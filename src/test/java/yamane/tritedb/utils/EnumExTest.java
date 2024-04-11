/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */
  
package yamane.tritedb.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import yamane.tritedb.testdata.Gender;

@DisplayName("EnumExのデフォルトメソッド")
public class EnumExTest {

  @Test
  public void test() {
    assertEquals(Gender.MALE.contains(Gender.values()), true); 
    assertEquals(Gender.MALE.contains(Gender.FEMALE), false); 
  }
}
