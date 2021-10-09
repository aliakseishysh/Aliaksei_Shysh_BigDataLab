package by.aliakseishysh.pinfo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {

    static {
        HikariConfig config = new HikariConfig("resources/psql.properties");
        HikariDataSource dataSource = new HikariDataSource(config);
        System.out.println(dataSource.isRunning());
    }

}
