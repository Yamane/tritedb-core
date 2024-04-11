/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.testdata;

import yamane.utils.EnumEx;

public enum Gender implements EnumEx {
  
  MALE("男性", 1),
  FEMALE("女性", 2);

  private String label;
  private Integer value;

  private Gender(String label, Integer value) {
      this.label = label;
      this.value = value;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  public Integer value() {
    return value;
  }
}
