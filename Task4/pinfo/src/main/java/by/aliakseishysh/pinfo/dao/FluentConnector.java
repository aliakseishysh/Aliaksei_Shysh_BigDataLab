package by.aliakseishysh.pinfo.dao;

import by.aliakseishysh.pinfo.exception.DaoException;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
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
                    .defaultBatchSize(100)
                    .defaultSqlHandler(() -> (e, sql) -> {
                        throw new DaoException("Can't execute the query: " + sql, e);
                    })
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
