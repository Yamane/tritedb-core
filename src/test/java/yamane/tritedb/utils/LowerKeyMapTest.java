/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.utils;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Mapのキーが大文字小文字区別なくてもアクセスできるか")
public class LowerKeyMapTest {

  @Test
  public void test() throws Exception {

    Map<String, Object> m1 = new LowerKeyMap<>();
    m1.put("ID", 1);
    m1.put("name1", "hoge1");
    m1.put("NAME2", "moge1");

    Map<String, Object> m2 = new LowerKeyMap<>();
    m2.put("ID", 2);
    m2.put("NAME1", "hoge2");
    m2.put("NAME2", "moge2");

    assertEquals(m1.containsKey("ID"), true);
    assertEquals(m1.remove("id"), 1);
    assertEquals(m1.remove("NAME1"), "hoge1");
    assertEquals(m1.remove("name2"), "moge1");

    m1.putAll(m2);

    assertEquals(
        "id:2 name1:hoge2 name2:moge2",
        String.format("id:%d name1:%s name2:%s", m1.get("id"), m1.get("name1"), m1.get("name2")));

  }
}
