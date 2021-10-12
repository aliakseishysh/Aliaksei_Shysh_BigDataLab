package by.aliakseishysh.pinfo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.*;

public class HcpSource {

    private static HikariDataSource hikariDataSource;

    static {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("properties/psql");
            Map<String, String> settings = new HashMap<>();
            Collections.list(bundle.getKeys()).forEach(key -> settings.put(key, bundle.getString(key)));
            Properties properties = new Properties();
            properties.putAll(settings);

            HikariConfig config = new HikariConfig(properties);
            hikariDataSource = new HikariDataSource(config);
        } catch (MissingResourceException e) {
            throw new ExceptionInInitializerError(e.getMessage()); //TODO handle exception
        }
    }

    private HcpSource(){}

    public static HikariDataSource getSource() {
        return  hikariDataSource;
    }

}
