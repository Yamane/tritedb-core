/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;

import static yamane.tritedb.TriteDbBase.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * リフレクションを用いてデータの読み書きを行う共通処理を提供します.
 * @param <T> 処理対象となるクラス
 */
public abstract class AbstractProcessor<T> {

  /** 処理対象となるクラス */
  protected Class<T> clazz;

  /**
   * コンストラクタ.
   * @param clazz 処理対象となるクラス
   */
  protected AbstractProcessor(Class<T> clazz) {
    this.clazz = clazz;
  }
  
  /**
   * デフォルトコンストラクタを用いてインスタンスの生成を行います.
   * @return　新規インスタンス
   * @throws SQLException インスタンス化に失敗した場合
   */
  public T newInstance() throws SQLException {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (IllegalAccessException e) {
      throw new SQLException(format("reflect.const.access", clazz.getSimpleName()), e);
    } catch (InstantiationException | NoSuchMethodException | IllegalArgumentException e) {
      throw new SQLException(format("reflect.const.notfound", clazz.getSimpleName()), e);
    } catch (InvocationTargetException e) {
      throw new SQLException(format("reflect.const.error", clazz.getSimpleName()), e);
    }
  }
  
  /**
   * フィールドからデータを読み出します.
   * @param target 処理対象のオブジェクト
   * @param field 処理対象のフィールド
   * @return 取得した値
   * @throws SQLException フィールドへのアクセスに失敗した場合
   */
  protected Object read(Object target, Field field) throws SQLException {
    try {
      return field.get(target);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      Class<?> parent = field.getDeclaringClass();
      throw new SQLException(format("reflect.read.error", parent.getSimpleName(), field.getName()), e);
    }
  }
  
  /**
   * メソッドを用いてデータの読み出しを行います.
   * @param target 処理対象のオブジェクト
   * @param method 処理対象のメソッド
   * @return 取得した値
   * @throws SQLException メソッドへのアクセスに失敗した場合
   */
  protected Object read(Object target, Method method) throws SQLException {
    try {
      return method.invoke(target);
    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
      Class<?> parent = method.getDeclaringClass();
      throw new SQLException(format("reflect.readMethod.error", parent.getSimpleName(), method.getName()), e);
    }
  }
  
  /**
   * フィールドに対してデータの書き込みを行います.
   * @param target 処理対象のオブジェクト
   * @param field 処理対象のフィールド
   * @param value 書き込む値
   * @throws SQLException フィールドへのアクセスに失敗した場合
   */
  protected void write(Object target, Field field, Object value) throws SQLException {
    try {
      field.set(target, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      Class<?> parent = field.getDeclaringClass();
      throw new SQLException(format("reflect.write.error", parent.getSimpleName(), field.getName()), e);
    }
  }
  
  /**
   * メソッドを用いてデータの書き込みを行います.
   * @param target 処理対象のオブジェクト
   * @param method 処理対象のメソッド
   * @param value 書き込む値
   * @throws SQLException メソッドへのアクセスに失敗した場合
   */
  protected void write(Object target, Method method, Object value) throws SQLException {
    try {
      method.invoke(target, value);
    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
      Class<?> parent = method.getDeclaringClass();
      throw new SQLException(format("reflect.writeMethod.error", parent.getSimpleName(), method.getName()), e);
    }
  }
  
}
