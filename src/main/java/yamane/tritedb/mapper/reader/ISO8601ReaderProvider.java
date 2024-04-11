/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.mapper.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * LocalDateTime系のデータ型変換をサポートしているJDBCドライバ用のReaderProviderです.
 */
public class ISO8601ReaderProvider extends ReaderProvider {

  /**
   * このReaderFactoryが利用されるように登録します.
   */
  public static void register() {
    ReaderProvider.register(new ISO8601ReaderProvider());
  }
  
  private ISO8601ReaderProvider() {
    super();
  }
  
  /** {@inheritDoc} */
  @Override
  protected void registerZonedDateTime(List<Reader<?>> list) {
    list.add(new ObjectReader<LocalTime>(LocalTime.class));
    list.add(new ObjectReader<LocalDate>(LocalDate.class));
    list.add(new ObjectReader<LocalDateTime>(LocalDateTime.class));
  }
}
