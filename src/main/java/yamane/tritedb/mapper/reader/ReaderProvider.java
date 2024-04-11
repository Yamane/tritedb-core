/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yamane.utils.EnumEx;

/**
 * ResultSetから指定された型でデータを取得する処理を行うためのクラスです.
 */
public class ReaderProvider {

  private static ReaderProvider CURRENT = new ReaderProvider(); 

  private List<Reader<?>> list;
  private Reader<Object> last;

  private List<EnumExReader<?>> exReaders = new ArrayList<>();

  /**
   * このReaderFactoryが利用されるように登録します.
   */
  public static void register() {
    ReaderProvider.register(new ReaderProvider());
  }

  /**
   * 指定されたReaderFactoryが利用されるように登録します.
   */
  public static void register(ReaderProvider provider) {
    CURRENT = provider;
  }
  
  /**
   * 現在登録されているReaderProviderを取得します.
   * @return　現在登録されているReaderProvider
   */
  public static ReaderProvider get() {
    return CURRENT;
  }
  
  /**
   * コンストラクタ.
   */
  protected ReaderProvider() {
    super();
    list = new ArrayList<>();
    last = new ReaderImpl<Object>(m -> true, (r, i) -> r.getObject(i));
    registerDefault(list);
    registerZonedDateTime(list);
    register(list);
  }

  /**
   * ResultSetReaderに登録されているReaderから適切なものを判断し、取得します.
   * @param clazz　格納先となる型
   * @return 格納先となる型に適応するReader
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final Reader<?> getReader(Class<?> clazz) {
    if(EnumEx.isEnumEx(clazz)) {
      return exReaders.stream().filter(r -> r.match(clazz)).findFirst().orElseGet(() -> {
        EnumExReader newReader = new EnumExReader(clazz);
        exReaders.add(newReader);
        return newReader;
      });
    }
    return list.stream().filter(r -> r.match(clazz)).findFirst().orElse(last);
  }
  
  /**
   * 新たなReaderを登録する場合にはこのメソッドをオーバーライドしてください.
   * @param list Readerの登録先となるList.
   */
  protected void register(List<Reader<?>> list) {
  }

  /**
   * 基本的なデータ型を処理するReaderを登録します.
   * @param list Readerの登録先となるList.
   */
  protected void registerDefault(List<Reader<?>> list) {
    // string
    list.add(new ReaderImpl<CharSequence>(CharSequence.class, (r, i) -> r.getString(i)));
    // primitive(object)
    list.add(new PrimitiveReader<Boolean>(Boolean.class, (r, i) -> r.getBoolean(i)));
    list.add(new PrimitiveReader<Short>(Short.class, (r, i) -> r.getShort(i)));
    list.add(new PrimitiveReader<Integer>(Integer.class, (r, i) -> r.getInt(i)));
    list.add(new PrimitiveReader<Long>(Long.class, (r, i) -> r.getLong(i)));
    list.add(new PrimitiveReader<Float>(Float.class, (r, i) -> r.getFloat(i)));
    list.add(new PrimitiveReader<Double>(Double.class, (r, i) -> r.getDouble(i)));
    list.add(new PrimitiveReader<Byte>(Byte.class, (r, i) -> r.getByte(i)));
    // primitive
    list.add(new ReaderImpl<Boolean>(t -> t.equals(Boolean.TYPE), (r, i) -> r.getBoolean(i)));
    list.add(new ReaderImpl<Short>(t -> t.equals(Short.TYPE), (r, i) -> r.getShort(i)));
    list.add(new ReaderImpl<Integer>(t -> t.equals(Integer.TYPE), (r, i) -> r.getInt(i)));
    list.add(new ReaderImpl<Long>(t -> t.equals(Long.TYPE), (r, i) -> r.getLong(i)));
    list.add(new ReaderImpl<Float>(t -> t.equals(Float.TYPE), (r, i) -> r.getFloat(i)));
    list.add(new ReaderImpl<Double>(t -> t.equals(Double.TYPE), (r, i) -> r.getDouble(i)));
    list.add(new ReaderImpl<Byte>(t -> t.equals(Byte.TYPE), (r, i) -> r.getByte(i)));
    // date time
    list.add(new ReaderImpl<Time>(Time.class, (r, i) -> r.getTime(i)));
    list.add(new ReaderImpl<Timestamp>(Timestamp.class, (r, i) -> r.getTimestamp(i)));
    list.add(new ReaderImpl<Date>(Date.class, (r, i) -> r.getDate(i)));
  }
  
  /**
   * LocalDateTime系のデータ型を処理するReaderを登録します.
   * @param list Readerの登録先となるList.
   */
  protected void registerZonedDateTime(List<Reader<?>> list) {
    list.add(new ReaderImpl<LocalDate>(LocalDate.class, (r, i) -> {
      ZonedDateTime date = zoned(r.getDate(i));
      return date != null ? date.toLocalDate() : null;
    }));
    list.add(new ReaderImpl<LocalTime>(LocalTime.class, (r, i) -> {
      ZonedDateTime date = zoned(r.getTime(i));
      return date != null ? date.toLocalTime() : null;
    }));
    list.add(new ReaderImpl<LocalDateTime>(LocalDateTime.class, (r, i) -> {
      ZonedDateTime date = zoned(r.getDate(i));
      ZonedDateTime time = zoned(r.getTime(i));
      if(date != null) {
        if(time != null) {
          return LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
        }
        return date.toLocalDateTime();
      }
      return null;
    }));
  }
  
  /**
   * java.sql.DateからZonedDateTimeを生成します.
   * java.sql.Date、java.sql.TimeがtoInstant()の実装を持たないため使用.
   * @param date 対象となるDate
   * @return タイムゾーン付きの日付
   */
  protected ZonedDateTime zoned(Date date) {
    if (date != null) {
      return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault());
    }
    return null;
  }
}
