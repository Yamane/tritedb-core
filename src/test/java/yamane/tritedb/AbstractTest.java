/*
 * Copyright (c) Yamamoto Yamane
 * Released under the MIT license
 * https://opensource.org/license/mit
 */

package yamane.tritedb;

import java.nio.file.FileSystems;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.h2.jdbcx.JdbcDataSource;

import yamane.tritedb.utils.SqlFileReader;

public class AbstractTest {

  private ResourceBundle h2 = ResourceBundle.getBundle("h2");

  protected JdbcDataSource ds;
  protected TriteDb db;
  protected SqlFileReader reader;
  
  protected void init() {
    ds = new JdbcDataSource();
    ds.setURL(h2.getString("url"));
    ds.setUser(h2.getString("user"));
    ds.setPassword(h2.getString("password"));
    
    db = new TriteDbImpl(ds);
    SqlFileReader.init(FileSystems.getDefault().getPath("src/test/resources/sql"));
    reader = SqlFileReader.instance();
  }
  
  protected String sql(String table, String key) throws SQLException {
    return reader.read(table + "/" + key + ".sql");
  }
}
