create table if not exists readertest(
  id bigint not null,
  name varchar(100),
  gender tinyint,
  birthday date,
  married boolean,
  income int,
  tax_rate decimal(2,1),
  wackup_time time,
  remarks text,
  create_date datetime,
  update_date timestamp,
  primary key(id)
)