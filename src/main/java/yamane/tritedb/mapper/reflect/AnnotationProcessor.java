/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;

import static yamane.tritedb.TriteDbBase.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import yamane.tritedb.mapper.reader.Reader;
import yamane.tritedb.mapper.reader.ReaderProvider;
import yamane.tritedb.utils.LowerKeyMap;

/**
 * <code>@Column</code>、<code>@Nest</code>のアノテーションを使って読み書きを行うための機能を持つクラスです.
 * @param <T> 処理の対象となるクラス.デフォルトコンストラクターを持つことが必須となります.
 */
public class AnnotationProcessor<T> extends AbstractProcessor<T> {

  private static String DELIMITTER = "\\$";
  private static ReaderProvider provider = ReaderProvider.get();
  private Annotation type;
  private String name;
  private Field field;
  private Method readMethod;
  private Method writeMethod;
  private Reader<?> reader;
  private Map<String, AnnotationProcessor<?>> fields;
  private List<AnnotationProcessor<?>> tree;

  /**
   * インスタンスを生成します.
   * @param clazz 処理の対象となるクラス
   * @throws SQLException 対象クラスに同一クラスの<code>@Nest</code>が設定されていた場合
   */
  public AnnotationProcessor(Class<T> clazz) throws SQLException {
    super(clazz);
    tree = Collections.emptyList();
    scan();
  }

  private AnnotationProcessor(Class<T> clazz, Field field, AnnotationProcessor<?> parent) throws SQLException {
    super(clazz);
    this.field = field;
    this.reader = AnnotationProcessor.provider.getReader(clazz);
    createTree(parent);
    try {
      PropertyDescriptor pd = new PropertyDescriptor(this.field.getName(), this.field.getDeclaringClass());
      this.readMethod = pd.getReadMethod();
      this.writeMethod = pd.getWriteMethod();
    } catch (IntrospectionException e) {
    }
    Column column = this.field.getAnnotation(Column.class);
    if (column != null) {
      this.type = column;
      this.name = column.value();
    }
    Nest nest = this.field.getAnnotation(Nest.class);
    if (nest != null) {
      for (AnnotationProcessor<?> treeInfo : parent.tree) {
        if (treeInfo.clazz.equals(clazz)) {
          throw new SQLException(getStr("reflect.nest.error"));
        }
      }
      this.type = nest;
      this.name = nest.value();
      scan();
    }
  }

  /**
   * 指定された名前のアノテーションを持つフィールド情報が存在するかどうかを返します.
   * @param name アノテーションに指定された名称
   * @return 存在の有無
   */
  public boolean containsField(String name) {
    AnnotationProcessor<?> info = this;
    for (String key : name.split(DELIMITTER)) {
      if (!info.fields.containsKey(key)) {
        return false;
      }
      info = info.fields.get(key);
    }
    return true;
  }

  /**
   * 指定された名前のアノテーションを持つフィールド情報を取得します.
   * @param name アノテーションに指定された名称
   * @return フィールド情報
   */
  public AnnotationProcessor<?> getField(String name) {
    AnnotationProcessor<?> info = this;
    for (String key : name.split(DELIMITTER)) {
      if (!info.fields.containsKey(key)) {
        return null;
      }
      info = info.fields.get(key);
    }
    return info;
  }

  /**
   * フィールドのデータ読み出しを実施します.
   * @param target 対象クラスインスタンス
   * @return フィールドの値
   * @throws SQLException フィールドの読み込み処理がエラーを通知した場合
   */
  public Object read(Object target) throws SQLException {
    for (AnnotationProcessor<?> info : tree) {
      if (info.type instanceof Nest) {
        Object next = info.readSelf(target);
        if (next == null) {
          break;
        }
        target = next;
      } else {
        return info.readSelf(target);
      }
    }
    return null;
  }

  private Object readSelf(Object target) throws SQLException {
    if (readMethod != null) {
      return super.read(target, readMethod);
    } else {
      return super.read(target, field);
    }
  }

  /**
   * ResultSetから取得した値をフィールドへ書き込みます.
   * @param target 対象クラスインスタンス
   * @param rs 処理対象となるResultSet
   * @param index インデックス
   * @throws SQLException フィールドの書き込み処理がエラーを通知した場合、DBがエラーを通知した場合
   */
  public void write(Object target, ResultSet rs, Integer index) throws SQLException {
    write(target, reader.read(rs, index));
  }

  /**
   * フィールドへのデータ書き込みを実施します.
   * @param target 対象クラスインスタンス
   * @param value 書き込む内容
   * @throws SQLException フィールドの書き込み処理がエラーを通知した場合
   */
  public void write(Object target, Object value) throws SQLException {
    for (AnnotationProcessor<?> info : tree) {
      if (info.type instanceof Nest) {
        Object next = info.readSelf(target);
        if (next == null) {
          next = info.newInstance();
          info.writeSelf(target, next);
        }
        target = next;
      } else {
        info.writeSelf(target, value);
      }
    }
  }

  private void writeSelf(Object target, Object value) throws SQLException {
    if (writeMethod != null) {
      super.write(target, writeMethod, value);
    } else {
      super.write(target, field, value);
    }
  }

  private void createTree(AnnotationProcessor<?> parent) {
    tree = new ArrayList<AnnotationProcessor<?>>();
    tree.addAll(parent.tree);
    tree.add(this);
  }

  private void scan() throws SQLException {
    fields = new LowerKeyMap<>();
    for (Field field : getFields(Column.class, Nest.class)) {
      AnnotationProcessor<?> info = new AnnotationProcessor<>(field.getType(), field, this);
      fields.put(info.name, info);
    }
  }

  @SafeVarargs
  private final List<Field> getFields(Class<? extends Annotation>... annotations) {
    List<Field> fields = new ArrayList<Field>();
    Class<?> target = clazz;
    while (target != null && target != Object.class) {
      for (Field field : target.getDeclaredFields()) {
        for (Class<? extends Annotation> annotation : annotations) {
          if (field.getAnnotation(annotation) != null) {
            fields.add(field);
          }
        }
      }
      target = target.getSuperclass();
    }
    return fields;
  }
}
