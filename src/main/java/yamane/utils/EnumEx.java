/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.utils;

/**
 * 1:男性、2:女性　のような固定値とラベルの組み合わせをENUMで表現するためのインターフェイスです.
 */
public interface EnumEx {

  /**
   * 値に関連するラベルを取得します.
   * @return このEnumExのラベル
   */
  public String label();

  /**
   * 値を取得します.
   * @return このEnumExの値
   */
  public Integer value();

  /**
   * このEnumExがチェック対象となるEnumEx一覧の中に含まれているかを判定します.
   * @param checks チェック対象となるEnumEx一覧
   * @return 判定結果
   */
  default Boolean contains(EnumEx... checks) {
    for (EnumEx check : checks) {
      if (check.equals(this)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 指定されたオブジェクトが EnumExインターフェイスを持つEnumであるかどうかを調査します.
   * @param obj 調査対象オブジェクト
   * @return EnumExインターフェイスを持つEnumであるかどうか
   */
  static Boolean isEnumEx(Object obj) {
    if(obj != null) {
      return isEnumEx(obj.getClass());
    }
    return false;
  }
  
  /**
   * 指定されたクラスが EnumExインターフェイスを持つEnumであるかどうかを調査します.
   * @param clazz 調査対象クラス
   * @return EnumExインターフェイスを持つEnumであるかどうか
   */
  static Boolean isEnumEx(Class<?> clazz) {
    return clazz.isEnum() && EnumEx.class.isAssignableFrom(clazz);
  }
  
  /**
   * 対象オブジェクトがEnumEx派生クラスだった場合、labelを取得します.
   * @param obj 調査対象オブジェクト
   * @return labelの値
   */
  static String label(Object obj) {
    if(isEnumEx(obj)) {
      return ((EnumEx) obj).label();
    }
    return null;
  }
  
  /**
   * 対象オブジェクトがEnumEx派生クラスだった場合、valueを取得します.
   * @param obj 調査対象オブジェクト
   * @return valueの値
   */
  static Integer value(Object obj) {
    if(isEnumEx(obj)) {
      return ((EnumEx) obj).value();
    }
    return null;
  }
}
