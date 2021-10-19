package by.aliakseishysh.pinfo.dao;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.internal.query.DefaultSqlHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes FluentJDBC
 */
public class FluentConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(HcpSource.class);
    private static final FluentJdbc fluentJdbc;

    static {
        try {
            fluentJdbc = new FluentJdbcBuilder()
                    .connectionProvider(HcpSource.getSource())
                    .defaultSqlHandler(DefaultSqlHandler::new)
                    .build();
        } catch (RuntimeException e) {
            LOGGER.error("Can't initialize FluentJDBC", e);
            throw new ExceptionInInitializerError("Can't initialize FluentJDBC");
        }

    }

    public static FluentJdbc getConnector() {
        return fluentJdbc;
    }

}
