/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb.testdata;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;
import yamane.tritedb.mapper.reflect.Column;

@Data
public class TestBean {
  @Column("id")
  private Long id;
  @Column("name")
  private String name;  
  @Column("gender")
  private Gender gender;
  @Column("birthday")
  private LocalDate birthday;
  @Column("married")
  private boolean married;
  @Column("income")
  private Integer income;
  @Column("tax_rate")
  private Float taxRate;
  @Column("wackup_time")
  private LocalTime wackupTime;
  @Column("remarks")
  private String remarks;
  @Column("create_date")
  private LocalDateTime createDate;
  @Column("update_date")
  private Timestamp updateDate;
}
