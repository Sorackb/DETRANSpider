package org.lucassouza.detranspider.model;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Lucas Souza [sorackb@gmail.com]
 */
public class SQLServerConnection {

  public static Connection openConnection() throws SQLServerException,
          SQLException {
    SQLServerDataSource dataSource = new SQLServerDataSource();

    dataSource.setServerName("<SERVER>");
    dataSource.setInstanceName("<INSTANCE");
    dataSource.setDatabaseName("<DATABASE");
    dataSource.setUser("<USERNAME");
    dataSource.setPassword("<PASSWORD>");

    return dataSource.getConnection();
  }
}
