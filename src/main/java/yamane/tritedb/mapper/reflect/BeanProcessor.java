/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reflect;

import static yamane.tritedb.TriteDbBase.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import yamane.tritedb.mapper.reader.Reader;
import yamane.tritedb.mapper.reader.ReaderProvider;

/**
 * JavaBeanのプロパティに書き込む機能を持つクラスです.
 * @param <T> 処理の対象となるクラス.デフォルトコンストラクターを持つことが必須となります.
 */
public class BeanProcessor<T> extends AbstractProcessor<T> {

  private static ReaderProvider provider = ReaderProvider.get();
  private List<PrpoertyInfo> props;

  /**
   * インスタンスを生成します.
   * @param clazz 処理の対象となるクラス
   * @throws SQLException 対象クラスの解析に失敗した場合
   */
  public BeanProcessor(Class<T> clazz) throws SQLException {
    super(clazz);
    try {
      BeanInfo info = Introspector.getBeanInfo(clazz);
      this.props = new ArrayList<>();
      for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
        this.props.add(new PrpoertyInfo(pd));
      }
    } catch (IntrospectionException e) {
      throw new SQLException(format("reflect.bean.error", clazz.getSimpleName()), e);
    }
  }
  
  /**
   * getterを用いてデータの読み出しを行います.
   * @param target 処理の対象となるクラス
   * @param name　プロパティ名
   * @return　読み出した値
   * @throws SQLException メソッドアクセスに失敗した場合
   */
  public Object read(Object target, String name) throws SQLException {
    PrpoertyInfo i = getInfo(name);
    if(i != null) {
      return read(target, i.readMethod);
    }
    return null;
  }
    
  /**
   * setterを用いてデータの書き込みを行います.
   * @param target 処理の対象となるクラス
   * @param name　プロパティ名
   * @param rs　書き込むデータを持つResultSet
   * @param index 書き込むデータのインデックス
   * @throws SQLException メソッドアクセスに失敗した場合
   */
  public void write(Object target, String name, ResultSet rs, Integer index) throws SQLException {
    PrpoertyInfo i = getInfo(name);
    if(i != null) {
      write(target, i.writeMethod, i.reader.read(rs, index));
    }
  }
  
  /**
   * setterを用いてデータの書き込みを行います.
   * @param target 処理の対象となるクラス
   * @param name　プロパティ名
   * @param value　書き込むデータ
   * @throws SQLException メソッドアクセスに失敗した場合
   */
  public void write(Object target, String name, Object value) throws SQLException {
    PrpoertyInfo i = getInfo(name);
    if(i != null) {
      write(target, i.writeMethod, value);
    }
  }
  
  private PrpoertyInfo getInfo(String name) {
    return props.stream().filter(p -> p.match(name)).findFirst().orElse(null);
  }
  
  private class PrpoertyInfo {
    String name;
    String decName;
    Method readMethod;
    Method writeMethod;
    Reader<?> reader;
    
    PrpoertyInfo(PropertyDescriptor pd) {
      this.name = pd.getName();
      this.decName = decamelize(pd.getName());
      this.readMethod = pd.getReadMethod();
      this.writeMethod = pd.getWriteMethod();
      this.reader = BeanProcessor.provider.getReader(pd.getPropertyType());
    }
    
    boolean match(String name) {
      return this.name.equalsIgnoreCase(name) || this.decName.equalsIgnoreCase(name);
    }
    
    String decamelize(final String str) {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < str.length(); ++i) {
          char c = str.charAt(i);
          if (i != 0 && Character.isUpperCase(c)) {
            b.append('_');
          }
          b.append(c);
      }
      return b.toString().toLowerCase();
  }
  }
}
