package by.aliakseishysh.pinfo.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * Initializes HikariCP
 */
public class HcpSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HcpSource.class);
    private static final HikariDataSource hikariDataSource;

    static {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("properties/psql");
            Map<String, String> settings = new HashMap<>();
            Collections.list(bundle.getKeys()).forEach(key -> settings.put(key, bundle.getString(key)));
            Properties properties = new Properties();
            properties.putAll(settings);

            HikariConfig config = new HikariConfig(properties);
            hikariDataSource = new HikariDataSource(config);
        } catch (RuntimeException e) {
            LOGGER.error("Can't initialize HikariCP", e);
            throw new ExceptionInInitializerError("Can't initialize HikariCP");
        }
    }

    private HcpSource() {
    }

    public static HikariDataSource getSource() {
        return hikariDataSource;
    }

}
